DROP TABLE IF EXISTS "user_roles";
DROP TABLE IF EXISTS "roles";
DROP TABLE IF EXISTS "users";


CREATE TABLE "users" (
     "id" UUID NOT NULL PRIMARY KEY,
     "civil_id" VARCHAR(255) NOT NULL,
     "username" VARCHAR(255) NOT NULL,
     "email" VARCHAR(255) NOT NULL,
     "password" VARCHAR(255) NOT NULL,
     "created_at" DATE NOT NULL,
     "updated_at" DATE NOT NULL,
     "is_active" BOOLEAN NOT NULL
);

CREATE TABLE "roles" (
     "id" BIGSERIAL PRIMARY KEY,
     "name" VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE "user_roles" (
    "id" BIGSERIAL PRIMARY KEY,
    "user_id" UUID NOT NULL,
    "role_id" BIGINT NOT NULL,
    CONSTRAINT "fk_user" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE,
    CONSTRAINT "fk_role" FOREIGN KEY ("role_id") REFERENCES "roles"("id") ON DELETE CASCADE
);