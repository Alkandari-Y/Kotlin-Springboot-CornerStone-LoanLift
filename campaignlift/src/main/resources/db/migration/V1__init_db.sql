DROP TABLE IF EXISTS "replies";
DROP TABLE IF EXISTS "comments";
DROP TABLE IF EXISTS "files";
DROP TABLE IF EXISTS "pledge_transactions";
DROP TABLE IF EXISTS "pledges";
DROP TABLE IF EXISTS "campaings";

CREATE TABLE "campaigns"
(
    "id"                SERIAL PRIMARY KEY,
    "created_by"        INT             NOT NULL,
    "category_id"       INT             NOT NULL,
    "title"             VARCHAR(255)    NOT NULL,
    "description"       VARCHAR(255)    NOT NULL,
    "goal_amount"       DECIMAL(9, 3)   NOT NULL,
    "interest_rate"     DECIMAL(5, 4)   NOT NULL,
    "repayment_months"  INTEGER         NOT NULL,
    "status"            INT             NOT NULL,
    "submitted_at"      DATE            NOT NULL,
    "approved_by"       INT,
    "deadline"          DATE            NOT NULL,
    "account_id"        INT             NOT NULL,
    "image_url"         VARCHAR(255)    NOT NULL UNIQUE
);

CREATE TABLE "files"(
    "id"                SERIAL PRIMARY KEY,
    "campaign_id"       INT             NOT NULL,
    "media_type"        VARCHAR(255)    NOT NULL,
    "url"               VARCHAR(255)    NOT NULL,
    "bucket"            VARCHAR(255)    NOT NULL,
    "created_at"        DATE            NOT NULL,
    "is_public"         BOOLEAN         NOT NULL,
    "verified_by"       INT,
    CONSTRAINT "file_campaign_id_foreign"
        FOREIGN KEY ("campaign_id")
            REFERENCES "campaigns" ("id") ON DELETE CASCADE
);


CREATE TABLE "pledges"
(
     "id"               SERIAL PRIMARY KEY,
     "user_id"          INT             NOT NULL,
     "account_id"       INT             NOT NULL,
     "campaign_id"      INT             NOT NULL,
     "amount"           DECIMAL(9, 3)   NOT NULL,
     "created_at"       DATE            NOT NULL,
     "updated_at"       DATE            NOT NULL,
     "status"           INT             NOT NULL,
     "withdrawn_at"     DATE            NULL,
     "commited_at"      DATE            NOT NULL,
     CONSTRAINT "pledge_campaign_id_foreign"
         FOREIGN KEY ("campaign_id")
            REFERENCES "campaigns" ("id") ON DELETE CASCADE
);


CREATE TABLE "pledge_transactions"
(
    "id"                SERIAL PRIMARY KEY,
    "transaction_id"    INT          NOT NULL,
    "pledge_id"         INT          NOT NULL,
    "type"              INT          NOT NULL,
    CONSTRAINT "pledge_transactions_pledge_id_foreign"
        FOREIGN KEY ("pledge_id")
            REFERENCES "pledges" ("id") ON DELETE CASCADE
);


CREATE TABLE "comments"(
    "id"                SERIAL PRIMARY KEY,
    "created_by"        INT             NOT NULL,
    "campaign_id"       INT             NOT NULL,
    "created_at"        DATE            NOT NULL,
    "message"           VARCHAR(255)    NOT NULL,
    CONSTRAINT "comment_campaign_id_foreign"
        FOREIGN KEY ("campaign_id")
            REFERENCES "campaigns" ("id") ON DELETE CASCADE
);


CREATE TABLE "replies"(
    "id"                SERIAL PRIMARY KEY,
    "comment_id"        INT             NOT NULL,
    "created_at"        DATE            NOT NULL,
    "message"           VARCHAR(255)    NOT NULL,
    CONSTRAINT "reply_comment_id_foreign"
        FOREIGN KEY("comment_id")
            REFERENCES "comments"("id")
);
