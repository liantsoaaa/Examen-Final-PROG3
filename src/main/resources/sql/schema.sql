CREATE TYPE member_occupation AS ENUM ('JUNIOR', 'SENIOR', 'SECRETARY', 'PRESIDENT', 'TREASURER', 'VICE_PRESIDENT');
CREATE TYPE gender AS ENUM ('MALE', 'FEMALE', 'OTHER');

CREATE TABLE member (
    id SERIAL PRIMARY KEY,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    gender gender NOT NULL,
    adress VARCHAR(255) NOT NULL,
    profession VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    occupation member_occupation NOT NULL,
    collectivity_id INT NOT NULL,
);

CREATE TABLE collectivity (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL,
    structure_collectivity structure NOT NULL;
);

CREATE TABLE collectivity_structure (
    id SERIAL PRIMARY KEY,
    collectivity_id VARCHAR UNIQUE NOT NULL,
    president_id INT,
    vice_president_id INT,
    secretary_id INT,
    treasurer_id INT,
    FOREIGN KEY (collectivity_id) REFERENCES collectivity(id),
    FOREIGN KEY (president_id) REFERENCES member(id),
    FOREIGN KEY (vice_president_id) REFERENCES member(id),
    FOREIGN KEY (secretary_id) REFERENCES member(id),
    FOREIGN KEY (treasurer_id) REFERENCES member(id)
);

CREATE TABLE sponsorship (
    id SERIAL PRIMARY KEY,
    id_candidate INT NOT NULL,
    id_sponsor INT NOT NULL,
    id_collectivity VARCHAR NOT NULL,
    relationship VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (id_candidate) REFERENCES member(id),
    FOREIGN KEY (id_sponsor) REFERENCES member(id),
    FOREIGN KEY (id_collectivity) REFERENCES collectivity(id),
    UNIQUE (id_candidate, id_sponsor)
);

CREATE TABLE member_referee (
    id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    referee_id INT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (referee_id) REFERENCES member(id),
    UNIQUE (member_id, referee_id)
);

CREATE TABLE collectivity_member (
    id SERIAL PRIMARY KEY,
    collectivity_id VARCHAR NOT NULL,
    member_id INT NOT NULL,
    FOREIGN KEY (collectivity_id) REFERENCES collectivity(id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    UNIQUE (collectivity_id, member_id)
);