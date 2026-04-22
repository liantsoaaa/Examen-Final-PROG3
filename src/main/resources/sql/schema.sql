CREATE TYPE member_occupation AS ENUM ('JUNIOR', 'SENIOR', 'SECRETARY', 'PRESIDENT', 'TREASURER', 'VICE_PRESIDENT');
CREATE TYPE gender AS ENUM ('MALE', 'FEMALE', 'OTHER');

CREATE TABLE members (
                         id SERIAL PRIMARY KEY,
                         firstName VARCHAR(255) NOT NULL,
                         lastName VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         phone VARCHAR(20) NOT NULL,
                         gender gender NOT NULL,
                         occupation member_occupation NOT NULL
);

CREATE TABLE collectivities (
                                id SERIAL PRIMARY KEY,
                                name VARCHAR(255) NOT NULL UNIQUE,
                                location VARCHAR(255) NOT NULL,

);