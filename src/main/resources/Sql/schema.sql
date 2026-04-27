CREATE TABLE collectivities (
    id              VARCHAR(50)  PRIMARY KEY,
    number          INTEGER      UNIQUE,
    name            VARCHAR(255) UNIQUE,
    locality        VARCHAR(255) NOT NULL,
    specialization  VARCHAR(255) NOT NULL,
    created_at      DATE         NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE members (
    id              VARCHAR(50)  PRIMARY KEY,
    last_name       VARCHAR(255) NOT NULL,
    first_name      VARCHAR(255) NOT NULL,
    birth_date      DATE         NOT NULL,
    gender          VARCHAR(10)  NOT NULL CHECK (gender IN ('M', 'F')),
    address         VARCHAR(500) NOT NULL,
    profession      VARCHAR(255) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE collectivity_members (
    id                  VARCHAR(50)  PRIMARY KEY,
    collectivity_id     VARCHAR(50)  NOT NULL REFERENCES collectivities(id),
    member_id           VARCHAR(50)  NOT NULL REFERENCES members(id),
    role                VARCHAR(50)  NOT NULL
                            CHECK (role IN (
                                'PRESIDENT',
                                'VICE_PRESIDENT',
                                'TREASURER',
                                'SECRETARY',
                                'CONFIRMED_MEMBER',
                                'JUNIOR_MEMBER'
                            )),
    adhesion_date       DATE         NOT NULL DEFAULT CURRENT_DATE,
    resignation_date    DATE,
    active              BOOLEAN      NOT NULL DEFAULT TRUE,
    UNIQUE (collectivity_id, member_id)
);

CREATE TABLE member_sponsors (
    id                      VARCHAR(50)  PRIMARY KEY,
    collectivity_member_id  VARCHAR(50)  NOT NULL REFERENCES collectivity_members(id),
    sponsor_member_id       VARCHAR(50)  NOT NULL REFERENCES members(id),
    relationship_nature     VARCHAR(100),  
    UNIQUE (collectivity_member_id, sponsor_member_id)
);

CREATE TABLE membership_fees (
    id              VARCHAR(50)   PRIMARY KEY,
    collectivity_id VARCHAR(50)   NOT NULL REFERENCES collectivities(id),
    label           VARCHAR(255)  NOT NULL,
    status          VARCHAR(20)   NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    frequency       VARCHAR(20)   NOT NULL CHECK (frequency IN ('MONTHLY', 'ANNUALLY', 'ONE_TIME')),
    eligible_from   DATE          NOT NULL,
    amount          BIGINT        NOT NULL, 
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE financial_accounts (
    id                  VARCHAR(50)  PRIMARY KEY,
    collectivity_id     VARCHAR(50)  NOT NULL REFERENCES collectivities(id),
    account_type        VARCHAR(20)  NOT NULL CHECK (account_type IN ('CASH', 'BANK', 'ORANGE_MONEY', 'MVOLA', 'AIRTEL_MONEY')),
    holder_name         VARCHAR(255),
    phone_number        VARCHAR(20),
    bank_name           VARCHAR(50)
                            CHECK (bank_name IN ('BRED','MCB','BMOI','BOA','BGFI','AFG','ACCES_BANQUE','BAOBAB','SIPEM')),
    account_number      VARCHAR(23),
    initial_balance     BIGINT       NOT NULL DEFAULT 0,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uniq_cash_per_collectivity
    ON financial_accounts(collectivity_id)
    WHERE account_type = 'CASH';

CREATE TABLE payments (
    id                      VARCHAR(50)  PRIMARY KEY,
    collectivity_id         VARCHAR(50)  NOT NULL REFERENCES collectivities(id),
    member_id               VARCHAR(50)  NOT NULL REFERENCES members(id),
    amount                  BIGINT       NOT NULL,
    financial_account_id    VARCHAR(50)  NOT NULL REFERENCES financial_accounts(id),
    payment_method          VARCHAR(20)  NOT NULL
                                CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'MOBILE_MONEY')),
    payment_date            DATE         NOT NULL,
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id                      VARCHAR(50)  PRIMARY KEY,
    collectivity_id         VARCHAR(50)  NOT NULL REFERENCES collectivities(id),
    debited_member_id       VARCHAR(50)  REFERENCES members(id),
    amount                  BIGINT       NOT NULL,
    financial_account_id    VARCHAR(50)  NOT NULL REFERENCES financial_accounts(id),
    payment_method          VARCHAR(20)  NOT NULL
                                CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'MOBILE_MONEY')),
    transaction_date        DATE         NOT NULL,
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE activities (
    id                  VARCHAR(50)   PRIMARY KEY,
    collectivity_id     VARCHAR(50)   REFERENCES collectivities(id), 
    title               VARCHAR(255)  NOT NULL,
    activity_type       VARCHAR(30)   NOT NULL
                            CHECK (activity_type IN (
                                'MONTHLY_GENERAL_ASSEMBLY',
                                'MANDATORY_JUNIOR_TRAINING',
                                'EXCEPTIONAL',
                                'FEDERATION'
                            )),
    mandatory           BOOLEAN       NOT NULL DEFAULT FALSE,
    scheduled_date      DATE          NOT NULL,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE member_activity_attendance (
    id              VARCHAR(50)  PRIMARY KEY,
    activity_id     VARCHAR(50)  NOT NULL REFERENCES activities(id),
    member_id       VARCHAR(50)  NOT NULL REFERENCES members(id),
    present         BOOLEAN      NOT NULL DEFAULT FALSE,
    excused         BOOLEAN      NOT NULL DEFAULT FALSE,
    excuse_reason   VARCHAR(500),
    recorded_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (activity_id, member_id)
);