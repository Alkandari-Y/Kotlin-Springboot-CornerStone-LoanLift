DROP TABLE IF EXISTS "account_ownerships";
DROP TABLE IF EXISTS "transactions";
DROP TABLE IF EXISTS "accounts";
DROP TABLE IF EXISTS "profiles";
DROP TABLE IF EXISTS "categories";

CREATE TABLE "categories"
(
    "id"   SERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL
);

CREATE TABLE "kycs"
(
    "id"            SERIAL PRIMARY KEY,
    "user_id"       INT           NOT NULL UNIQUE,
    "first_name"    VARCHAR(255)  NOT NULL,
    "last_name"     VARCHAR(255)  NOT NULL,
    "date_of_birth" DATE          NOT NULL,
    "nationality"   VARCHAR(255)  NOT NULL,
    "salary"        DECIMAL(8, 2) NOT NULL
);

CREATE TABLE "accounts"
(
    "id"             SERIAL PRIMARY KEY,
    "name"           VARCHAR(255)  NOT NULL,
    "balance"        DECIMAL(8, 2) NOT NULL,
    "is_active"      BOOLEAN       NOT NULL,
    "created_at"     TIMESTAMP     NOT NULL,
    "is_deleted"     BOOLEAN       NOT NULL,
    "account_number" VARCHAR(255)  NOT NULL UNIQUE,
    "category_id"    INT           NOT NULL,
    CONSTRAINT "fk_account_category"
        FOREIGN KEY ("category_id")
            REFERENCES "categories" ("id")
);

CREATE TABLE "transactions"
(
    "id"                  SERIAL PRIMARY KEY,
    "source_account"      INT           NOT NULL,
    "destination_account" INT           NOT NULL,
    "amount"              DECIMAL(8, 2) NOT NULL,
    "owner_reference_id"  INT           NOT NULL,
    "reference_type"      VARCHAR(50)   NOT NULL,
    "created_at"          TIMESTAMP     NOT NULL,
    "category_id"         INT           NOT NULL,
    CONSTRAINT "fk_transaction_source"
        FOREIGN KEY ("source_account")
            REFERENCES "accounts" ("id") ON DELETE CASCADE,
    CONSTRAINT "fk_transaction_destination"
        FOREIGN KEY ("destination_account")
            REFERENCES "accounts" ("id") ON DELETE CASCADE,
    CONSTRAINT "fk_transaction_category"
        FOREIGN KEY ("category_id")
            REFERENCES "categories" ("id")
);

CREATE TABLE "account_ownerships"
(
    "id"         SERIAL PRIMARY KEY,
    "account_id" INT         NOT NULL UNIQUE,
    "owner_id"   INT         NOT NULL,
    "owner_type" VARCHAR(50) NOT NULL,
    "is_primary" BOOLEAN     NOT NULL,
    CONSTRAINT "fk_account_ownership"
        FOREIGN KEY ("account_id")
            REFERENCES "accounts" ("id") ON DELETE CASCADE
);
