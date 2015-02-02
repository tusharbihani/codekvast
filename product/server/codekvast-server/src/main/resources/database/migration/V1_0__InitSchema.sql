-- Roles ----------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
  name VARCHAR(20) NOT NULL UNIQUE,
);
COMMENT ON TABLE roles IS 'Spring Security roles, without the ROLE_ prefix';

-- Users ----------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id            BIGINT                              NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username           VARCHAR(100)                        NOT NULL UNIQUE,
  encoded_password   VARCHAR(80),
  plaintext_password VARCHAR(255),
  enabled            BOOLEAN DEFAULT TRUE                NOT NULL,
  email_address VARCHAR(64) UNIQUE,
  full_name          VARCHAR(255),
  created_at    TIMESTAMP DEFAULT current_timestamp NOT NULL,
  modified_at   TIMESTAMP AS NOW()
);
COMMENT ON COLUMN users.plaintext_password IS 'Will be replaced by an encoded password at application startup';

DROP TABLE IF EXISTS user_roles;
CREATE TABLE user_roles (
  user_id    BIGINT                              NOT NULL REFERENCES users (id),
  role        VARCHAR(20)                         NOT NULL REFERENCES roles (name),
  created_at TIMESTAMP DEFAULT current_timestamp NOT NULL,
  modified_at TIMESTAMP AS NOW()
);

DROP INDEX IF EXISTS ix_user_roles;
CREATE UNIQUE INDEX ix_user_roles ON user_roles (user_id, role);

-- Organisations --------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS organisations;
CREATE TABLE organisations (
  id         BIGINT                              NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100)                        NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT current_timestamp NOT NULL,
  modified_at TIMESTAMP AS NOW()
);

DROP TABLE IF EXISTS organisation_members;
CREATE TABLE organisation_members (
  organisation_id BIGINT NOT NULL REFERENCES organisations (id),
  user_id         BIGINT NOT NULL REFERENCES users (id),
  primary_contact BOOLEAN DEFAULT FALSE               NOT NULL,
  created_at      TIMESTAMP DEFAULT current_timestamp NOT NULL,
  modified_at     TIMESTAMP AS NOW()
);

DROP INDEX IF EXISTS ix_organisation_members;
CREATE UNIQUE INDEX ix_organisation_members ON organisation_members (organisation_id, user_id);

-- Applications ---------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS applications;
CREATE TABLE applications (
  id              BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  organisation_id BIGINT NOT NULL REFERENCES organisations (id),
  name            VARCHAR(100)                        NOT NULL,
  created_at      TIMESTAMP DEFAULT current_timestamp NOT NULL,
  modified_at     TIMESTAMP AS NOW()
);

DROP INDEX IF EXISTS ix_applications;
CREATE UNIQUE INDEX ix_applications ON applications (organisation_id, name);

-- JVM info -------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS jvm_info;
CREATE TABLE jvm_info (
  id                    BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  organisation_id       BIGINT       NOT NULL REFERENCES organisations (id),
  application_id        BIGINT       NOT NULL REFERENCES applications (id),
  application_version   VARCHAR(100) NOT NULL,
  jvm_uuid VARCHAR(50) NOT NULL,
  collector_computer_id VARCHAR(50)  NOT NULL,
  collector_host_name   VARCHAR(255) NOT NULL,
  agent_computer_id     VARCHAR(50)  NOT NULL,
  agent_host_name       VARCHAR(255) NOT NULL,
  codekvast_version     VARCHAR(20)  NOT NULL,
  codekvast_vcs_id      VARCHAR(50)  NOT NULL,
  started_at            BIGINT       NOT NULL,
  dumped_at             BIGINT       NOT NULL
);
COMMENT ON TABLE jvm_info IS 'Data about one JVM that is instrumented by the Codekvast Collector';
COMMENT ON COLUMN jvm_info.jvm_uuid IS 'The UUID generated by each Codekvast Collector instance';
COMMENT ON COLUMN jvm_info.collector_computer_id IS 'The se.crisp.codekvast.agent.util.ComputerID value generated by the Codekvast Collector';
COMMENT ON COLUMN jvm_info.collector_host_name IS 'The hostname of the machine in which Codekvast Collector executes';
COMMENT ON COLUMN jvm_info.agent_computer_id IS 'The se.crisp.codekvast.agent.util.ComputerID value generated by the Codekvast Agent';
COMMENT ON COLUMN jvm_info.agent_host_name IS 'The hostname of the machine in which Codekvast Agent executes';
COMMENT ON COLUMN jvm_info.codekvast_version IS 'The version of Codekvast used for collecting the data';
COMMENT ON COLUMN jvm_info.codekvast_vcs_id IS 'The Git hash of Codekvast used for collecting the data';
COMMENT ON COLUMN jvm_info.started_at IS 'The value of System.currentTimeMillis() when Codekvast Collector instance was started';
COMMENT ON COLUMN jvm_info.dumped_at IS 'The value of System.currentTimeMillis() when Codekvast Collector made an output of the collected
data';

DROP INDEX IF EXISTS ix_jvm_info;
CREATE UNIQUE INDEX ix_jvm_info ON jvm_info (organisation_id, application_id, jvm_uuid);

-- Signatures -----------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS signatures;
CREATE TABLE signatures (
  id              BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  organisation_id BIGINT NOT NULL REFERENCES organisations (id),
  application_id  BIGINT NOT NULL REFERENCES applications (id),
  jvm_id BIGINT NOT NULL REFERENCES jvm_info (id),
  signature       VARCHAR(2000) NOT NULL,
  invoked_at      BIGINT NOT NULL,
  confidence      TINYINT
);
COMMENT ON COLUMN signatures.jvm_id IS 'From which JVM does this signature originate?';
COMMENT ON COLUMN signatures.invoked_at IS 'The value of System.currentTimeMillis() the method was invoked (rounded to nearest collection
 interval). 0 means not yet invoked';
COMMENT ON COLUMN signatures.confidence IS 'The ordinal for se.crisp.codekvast.server.agent_api.model.v1.SignatureConfidence. NULL for
not yet invoked.';

DROP INDEX IF EXISTS ix_signatures_id;
CREATE INDEX ix_signatures_organisation_id ON signatures (organisation_id, signature);

DROP INDEX IF EXISTS ix_signatures_invoked_at;
CREATE INDEX ix_signatures_invoked_at ON signatures (invoked_at);

-- System data ----------------------------------------------------------------------------------------------
INSERT INTO roles (name) VALUES ('SUPERUSER');
INSERT INTO roles (name) VALUES ('AGENT');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('MONITOR');

-- System account ---------------------------------------------------------------------------
INSERT INTO organisations (id, name) VALUES (0, 'System');

INSERT INTO users (id, username, plaintext_password, enabled) VALUES (0, 'system', '0000', TRUE);
INSERT INTO organisation_members (organisation_id, user_id) VALUES (0, 0);
INSERT INTO user_roles (user_id, role) VALUES (0, 'SUPERUSER');
INSERT INTO user_roles (user_id, role) VALUES (0, 'AGENT');
INSERT INTO user_roles (user_id, role) VALUES (0, 'ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (0, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (0, 'MONITOR');

INSERT INTO users (id, username, plaintext_password, enabled) VALUES (1, 'monitor', '0000', TRUE);
INSERT INTO organisation_members (organisation_id, user_id) VALUES (0, 1);
INSERT INTO user_roles (user_id, role) VALUES (1, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (1, 'MONITOR');

-- Demo account ---------------------------------------------------------------------------
INSERT INTO organisations (id, name) VALUES (1, 'Demo');

INSERT INTO users (id, username, plaintext_password, enabled) VALUES (2, 'agent', '0000', TRUE);
INSERT INTO organisation_members (organisation_id, user_id) VALUES (1, 2);
INSERT INTO user_roles (user_id, role) VALUES (2, 'AGENT');

INSERT INTO users (id, username, plaintext_password, enabled) VALUES (3, 'admin', '0000', TRUE);
INSERT INTO organisation_members (organisation_id, user_id) VALUES (1, 3);
INSERT INTO user_roles (user_id, role) VALUES (3, 'ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (3, 'USER');

INSERT INTO users (id, username, plaintext_password, enabled, email_address) VALUES (4, 'user', '0000', TRUE, 'user@demo.com');
INSERT INTO organisation_members (organisation_id, user_id) VALUES (1, 4);
INSERT INTO user_roles (user_id, role) VALUES (4, 'USER');
