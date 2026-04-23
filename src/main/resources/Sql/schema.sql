CREATE TYPE collectivity_post_name AS ENUM (
    'PRESIDENT', 'VICE_PRESIDENT', 'TREASURER',
    'SECRETARY', 'CONFIRMED', 'JUNIOR'
);
CREATE TYPE collectivity_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');
CREATE TYPE membership_end_reason AS ENUM ('MANDATE_END', 'RESIGNATION', 'TRANSFER');
CREATE TYPE payment_mode AS ENUM ('CASH', 'BANK_TRANSFER', 'MOBILE_MONEY');
CREATE TYPE payment_type AS ENUM ('REGISTRATION_FEE', 'PERIODIC_DUES', 'PUNCTUAL');
CREATE TYPE cotisation_frequency AS ENUM ('MONTHLY', 'ANNUAL', 'PUNCTUAL');
CREATE TYPE bank_name_enum AS ENUM ('BRED', 'MCB', 'BMOI', 'BOA', 'BGFI', 'AFG', 'ACCES_BANQUE', 'BAOBAB', 'SIPEM');
CREATE TYPE mobile_money_service AS ENUM ('ORANGE_MONEY', 'MVOLA', 'AIRTEL_MONEY');
CREATE TYPE movement_type AS ENUM ('IN', 'OUT');

CREATE TABLE city (
                      id   SERIAL  NOT NULL,
                      name VARCHAR NOT NULL UNIQUE,
                      PRIMARY KEY (id)
);

CREATE TABLE federation (
                            id                    SERIAL       NOT NULL,
                            cotisation_percentage NUMERIC(5,2) NOT NULL DEFAULT 10.00,
                            PRIMARY KEY (id)
);

CREATE TABLE member (
                        id             SERIAL    NOT NULL,
                        first_name     VARCHAR   NOT NULL,
                        last_name      VARCHAR   NOT NULL,
                        birth_date     DATE      NOT NULL,
                        enrolment_date TIMESTAMP NOT NULL,
                        address        TEXT      NOT NULL,
                        email          VARCHAR   NOT NULL UNIQUE,
                        phone          VARCHAR   NOT NULL UNIQUE,
                        job            VARCHAR   NOT NULL,
                        gender         gender    NOT NULL,
                        PRIMARY KEY (id)
);

CREATE TABLE collectivity (
                              id                 SERIAL              NOT NULL,
                              number             VARCHAR             NOT NULL UNIQUE,
                              name               VARCHAR             NOT NULL UNIQUE,
                              speciality         VARCHAR             NOT NULL,
                              creation_datetime  TIMESTAMP           NOT NULL,
                              status             collectivity_status NOT NULL DEFAULT 'PENDING',
                              authorization_date TIMESTAMP,
                              id_federation      INT                 NOT NULL,
                              id_city            INT                 NOT NULL,
                              PRIMARY KEY (id),
                              CONSTRAINT fk_collectivity_federation FOREIGN KEY (id_federation) REFERENCES federation (id),
                              CONSTRAINT fk_collectivity_city       FOREIGN KEY (id_city)       REFERENCES city (id)
);

CREATE TABLE member_collectivity (
                                     id              SERIAL                 NOT NULL,
                                     id_member       INT                    NOT NULL,
                                     id_collectivity INT                    NOT NULL,
                                     post_name       collectivity_post_name NOT NULL,
                                     start_date      TIMESTAMP              NOT NULL,
                                     end_date        TIMESTAMP,
                                     end_reason      membership_end_reason,
                                     PRIMARY KEY (id),
                                     CONSTRAINT fk_mc_member       FOREIGN KEY (id_member)       REFERENCES member (id),
                                     CONSTRAINT fk_mc_collectivity FOREIGN KEY (id_collectivity) REFERENCES collectivity (id),
                                     CONSTRAINT chk_end_reason_requires_end_date CHECK (end_reason IS NULL OR end_date IS NOT NULL),
                                     CONSTRAINT chk_mandate_duration CHECK (end_date IS NULL OR end_date <= start_date + INTERVAL '1 year')
    );

CREATE UNIQUE INDEX uq_unique_active_post_per_collectivity
    ON member_collectivity (id_collectivity, post_name)
    WHERE end_date IS NULL
      AND post_name IN ('PRESIDENT', 'VICE_PRESIDENT', 'TREASURER', 'SECRETARY');

CREATE OR REPLACE FUNCTION check_mandate_limit()
RETURNS TRIGGER AS $$
DECLARE mandate_count INT;
BEGIN
    IF NEW.post_name NOT IN ('PRESIDENT', 'VICE_PRESIDENT', 'TREASURER', 'SECRETARY')
    THEN RETURN NEW; END IF;
SELECT COUNT(*) INTO mandate_count
FROM member_collectivity
WHERE id_member = NEW.id_member
  AND id_collectivity = NEW.id_collectivity
  AND post_name = NEW.post_name;
IF mandate_count >= 2 THEN
        RAISE EXCEPTION 'Le membre % a déjà occupé le poste % 2 fois.', NEW.id_member, NEW.post_name;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_mandate_limit
    BEFORE INSERT ON member_collectivity
    FOR EACH ROW EXECUTE FUNCTION check_mandate_limit();

CREATE TABLE sponsorship (
                             id              SERIAL    NOT NULL,
                             id_candidate    INT       NOT NULL,
                             id_sponsor      INT       NOT NULL,
                             id_collectivity INT       NOT NULL,
                             relationship    VARCHAR   NOT NULL,
                             created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
                             PRIMARY KEY (id),
                             UNIQUE (id_candidate, id_sponsor),
                             CONSTRAINT fk_sp_candidate    FOREIGN KEY (id_candidate)    REFERENCES member (id),
                             CONSTRAINT fk_sp_sponsor      FOREIGN KEY (id_sponsor)      REFERENCES member (id),
                             CONSTRAINT fk_sp_collectivity FOREIGN KEY (id_collectivity) REFERENCES collectivity (id)
);

CREATE TABLE cotisation_plan (
                                 id              SERIAL               NOT NULL,
                                 id_collectivity INT                  NOT NULL,
                                 label           VARCHAR              NOT NULL,
                                 frequency       cotisation_frequency NOT NULL,
                                 amount          NUMERIC(15,2)        NOT NULL,
                                 year            INT,
                                 is_active       BOOLEAN              NOT NULL DEFAULT TRUE,
                                 PRIMARY KEY (id),
                                 CONSTRAINT fk_cp_collectivity FOREIGN KEY (id_collectivity) REFERENCES collectivity (id)
);

CREATE TABLE account (
                         id              SERIAL NOT NULL,
                         id_collectivity INT,
                         id_federation   INT,
                         PRIMARY KEY (id),
                         CONSTRAINT chk_account_owner CHECK (
                             (id_collectivity IS NOT NULL AND id_federation IS NULL) OR
                             (id_collectivity IS NULL     AND id_federation IS NOT NULL)
                             ),
                         CONSTRAINT fk_acc_collectivity FOREIGN KEY (id_collectivity) REFERENCES collectivity (id),
                         CONSTRAINT fk_acc_federation   FOREIGN KEY (id_federation)   REFERENCES federation (id)
);

CREATE TABLE cash_account (
                              id         SERIAL NOT NULL,
                              id_account INT    NOT NULL UNIQUE,
                              PRIMARY KEY (id),
                              CONSTRAINT fk_cash_account FOREIGN KEY (id_account) REFERENCES account (id) ON DELETE CASCADE
);

CREATE TABLE bank_account (
                              id             SERIAL         NOT NULL,
                              id_account     INT            NOT NULL UNIQUE,
                              holder_name    VARCHAR        NOT NULL,
                              bank_name      bank_name_enum NOT NULL,
                              bank_code      CHAR(5)        NOT NULL,
                              branch_code    CHAR(5)        NOT NULL,
                              account_number CHAR(11)       NOT NULL,
                              rib_key        CHAR(2)        NOT NULL,
                              PRIMARY KEY (id),
                              CONSTRAINT fk_bank_account FOREIGN KEY (id_account) REFERENCES account (id) ON DELETE CASCADE
);

CREATE TABLE mobile_money_account (
                                      id           SERIAL               NOT NULL,
                                      id_account   INT                  NOT NULL UNIQUE,
                                      holder_name  VARCHAR              NOT NULL,
                                      service_name mobile_money_service NOT NULL,
                                      phone_number VARCHAR              NOT NULL UNIQUE,
                                      PRIMARY KEY (id),
                                      CONSTRAINT fk_mobile_account FOREIGN KEY (id_account) REFERENCES account (id) ON DELETE CASCADE
);

CREATE TABLE account_movement (
                                  id         SERIAL          NOT NULL,
                                  id_account INT             NOT NULL,
                                  type       movement_type   NOT NULL,
                                  amount     NUMERIC(15,2)   NOT NULL CHECK (amount > 0),
                                  created_at TIMESTAMP       NOT NULL DEFAULT NOW(),
                                  PRIMARY KEY (id),
                                  CONSTRAINT fk_movement_account FOREIGN KEY (id_account) REFERENCES account (id) ON DELETE CASCADE
);

CREATE TABLE payment (
                         id                 SERIAL        NOT NULL,
                         id_member          INT           NOT NULL,
                         id_collectivity    INT           NOT NULL,
                         id_cotisation_plan INT,
                         id_account         INT           NOT NULL,
                         amount             NUMERIC(15,2) NOT NULL,
                         payment_date       TIMESTAMP     NOT NULL,
                         payment_mode       payment_mode  NOT NULL,
                         payment_type       payment_type  NOT NULL,
                         recorded_by        INT           NOT NULL,
                         PRIMARY KEY (id),
                         CONSTRAINT fk_pay_member          FOREIGN KEY (id_member)          REFERENCES member (id),
                         CONSTRAINT fk_pay_collectivity    FOREIGN KEY (id_collectivity)    REFERENCES collectivity (id),
                         CONSTRAINT fk_pay_cotisation_plan FOREIGN KEY (id_cotisation_plan) REFERENCES cotisation_plan (id),
                         CONSTRAINT fk_pay_account         FOREIGN KEY (id_account)         REFERENCES account (id),
                         CONSTRAINT fk_pay_recorded_by     FOREIGN KEY (recorded_by)        REFERENCES member (id),
                         CONSTRAINT chk_registration_fee_no_plan
                             CHECK (payment_type != 'REGISTRATION_FEE' OR id_cotisation_plan IS NULL),
    CONSTRAINT chk_periodic_dues_needs_plan
        CHECK (payment_type != 'PERIODIC_DUES' OR id_cotisation_plan IS NOT NULL)
);