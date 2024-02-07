CREATE TABLE "eatwhere"."users" (
    "id" bigserial PRIMARY KEY,
    "name" varchar(100) UNIQUE NOT NULL
    );

CREATE INDEX "users_name" ON "eatwhere"."users" ("name");
