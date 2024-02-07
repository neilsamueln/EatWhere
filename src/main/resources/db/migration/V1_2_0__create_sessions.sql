CREATE TABLE "eatwhere"."sessions" (
    "id" bigserial PRIMARY KEY,
    "owner_user_id" bigserial NOT NULL REFERENCES "eatwhere"."users"("id"),
    "state" varchar(30) NOT NULL,
    "start_date" timestamp with time zone NOT NULL,
    "end_date" timestamp with time zone,
    "restaurant" varchar(100)
    );

CREATE INDEX "sessions_owner_user_id" ON "eatwhere"."sessions" ("owner_user_id");
CREATE INDEX "sessions_state" ON "eatwhere"."sessions" ("state");
