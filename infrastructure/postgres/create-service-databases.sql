-- One database and one role per service, see Data Ownership in docs/rules/governance.md.
-- Names carry the slotgate_ prefix because a local PostgreSQL server is often shared with
-- other projects. PUBLIC may connect to every new database by default, so it is revoked:
-- a service role reaches its own database only. These credentials are for local development.

CREATE ROLE slotgate_auth LOGIN PASSWORD 'slotgate_auth';
CREATE DATABASE slotgate_auth OWNER slotgate_auth;
REVOKE ALL ON DATABASE slotgate_auth FROM PUBLIC;

CREATE ROLE slotgate_user LOGIN PASSWORD 'slotgate_user';
CREATE DATABASE slotgate_user OWNER slotgate_user;
REVOKE ALL ON DATABASE slotgate_user FROM PUBLIC;
