CREATE DATABASE agricultural_federation;
CREATE USER agricultural_federation_manager WITH PASSWORD '123456';
GRANT CONNECT ON DATABASE agricultural_federation TO agricultural_federation_manager;
GRANT CREATE ON SCHEMA public TO agricultural_federation_manager;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO agricultural_federation_manager;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO agricultural_federation_manager;
GRANT ALL PRIVILEGES ON DATABASE agricultural_federation TO agricultural_federation_manager;