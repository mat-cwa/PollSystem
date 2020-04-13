CREATE SCHEMA IF NOT EXISTS POLL;

CREATE TABLE IF NOT EXISTS POLL.USER(
id bigserial PRIMARY KEY,
username VARCHAR(255),
password VARCHAR(255),
email VARCHAR(255),
role text
);

CREATE TABLE IF NOT EXISTS POLL.POLL(
id bigserial PRIMARY KEY,
name VARCHAR(255),
fk_user BIGINT REFERENCES POLL.USER(id)
);

CREATE TABLE IF NOT EXISTS POLL.QUESTION(
id bigserial PRIMARY KEY,
question_description VARCHAR(255),
fk_poll BIGINT REFERENCES POLL.POLL(id)
);

CREATE TABLE IF NOT EXISTS POLL.ANSWER(
id bigserial PRIMARY KEY,
answer_description VARCHAR(255),
fk_question BIGINT REFERENCES POLL.QUESTION(id)
);

CREATE TABLE IF NOT EXISTS POLL.VOTE(
id bigserial PRIMARY KEY,
fk_user BIGINT REFERENCES POLL.USER(id),
fk_answer BIGINT REFERENCES POLL.ANSWER(id),
date TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS POLL.QUESTION_IPSET(
fk_question BIGINT,
ip_set VARCHAR(255)
);