CREATE TABLE auth_users (
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    CONSTRAINT auth_users_email_is_unique UNIQUE (email)
);
