CREATE TABLE "eatwhere"."session_users" (
    "id" bigserial PRIMARY KEY,
    "session_id" bigserial NOT NULL REFERENCES "eatwhere"."sessions"("id"),
    "user_id" bigserial NOT NULL REFERENCES "eatwhere"."users"("id"),
    "state" varchar(30) NOT NULL,
    "restaurant" varchar(100),
    UNIQUE("session_id", "user_id")
    );

CREATE INDEX "session_users_session_id" ON "eatwhere"."session_users" ("session_id");
CREATE INDEX "session_users_user_id" ON "eatwhere"."session_users" ("user_id");
CREATE INDEX "session_users_state" ON "eatwhere"."session_users" ("state");
