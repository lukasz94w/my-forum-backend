-- ROLES
INSERT INTO ROLES (ENUMERATED_ROLE) VALUES ('ROLE_USER');
INSERT INTO ROLES (ENUMERATED_ROLE) VALUES ('ROLE_ADMIN');

-- CATEGORIES
INSERT INTO CATEGORIES (ENUMERATED_CATEGORY) VALUES ('PROGRAMMING');
INSERT INTO CATEGORIES (ENUMERATED_CATEGORY) VALUES ('SPORT');
INSERT INTO CATEGORIES (ENUMERATED_CATEGORY) VALUES ('ELECTRONICS');
INSERT INTO CATEGORIES (ENUMERATED_CATEGORY) VALUES ('CAR');
INSERT INTO CATEGORIES (ENUMERATED_CATEGORY) VALUES ('INTRODUCTION');
INSERT INTO CATEGORIES (ENUMERATED_CATEGORY) VALUES ('ADVERTISEMENT');
INSERT INTO CATEGORIES (ENUMERATED_CATEGORY) VALUES ('PERSONAL');

-- NORMAL USERS (COULD BE FUNCTION USED THERE TO CREATE USERS...), PASSWORDS THE SAME AS USER NAME, GENERATED USING https://www.javainuse.com/onlineBcrypt
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user1@gmail.com', 'user1', '$2a$10$oKb/VYe4RVcT4Mwxqj97w.xiVuEI1ndrX9DOiwUh52qm1lsCTg/xi', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user2@gmail.com', 'user2', '$2a$10$59iKwiUWyfkTXrOYR1OWReTDfa0I1bnXy/CkwuX4OrrM5rK0IxppO', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user3@gmail.com', 'user3', '$2a$10$4o528TbjB2W6YcFe.HjxgeCiLElk0PVpJJKlHyZc1ty3PQS.xsw.W', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user4@gmail.com', 'user4', '$2a$10$Jn/Au5XhkXCzulaeerNPJOrmVGnEJ55hvOajUwSZxVVC9/Od4DFwm', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user5@gmail.com', 'user5', '$2a$10$k1u3KB8CLMBxq4V98YU3IOLEu91oiGmcGY5rCpLINlc44BaMCKcbC', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user6@gmail.com', 'user6', '$2a$10$R4YXp1191OJLqmcTcpvo9.i8bh2Y5fePrvPReBfLz6mE08YG49gbu', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user7@gmail.com', 'user7', '$2a$10$Iu72/t2hh9rZbMZOiyBP1elUcTvGvD81nu9P6yZEvoKLinRqs5j4i', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user8@gmail.com', 'user8', '$2a$10$xmofrS2.2mq0ia0xY4aqquMYV52tBOfwYnpfNqcrRKv6sz9Sy/H0u', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user9@gmail.com', 'user9', '$2a$10$ldGfje08t71c7BMPAEfsJ.UfsDT9TdKcN0.svAT8XpQwuaxbAAB.C', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED) VALUES (TRUE, 'user10@gmail.com', 'user10', '$2a$10$pftTyJgwlgCyAUf6J9cVhOJwutbn3gdCNVQntInPZOIL4ByT3s27W', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval);

-- ADMIN USERS
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED, BAN_ID, PROFILEPIC_ID) VALUES (TRUE, 'admin@gmail.com', 'admin', '$2a$10$aYBV.3UvUA4MyVQBaqJgFui0EuxMqgbph7RQNiWcdP9gt11GG5w8S', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval, null, null);
INSERT INTO USERS (ACTIVATED, EMAIL, NAME, PASSWORD, REGISTERED, BAN_ID, PROFILEPIC_ID) VALUES (TRUE, 'admin2@gmail.com', 'admin2', '$2a$10$hpu4JmtIo9Re1AmexnMDgOlUTJgsbBEUr0OvAPNPKNPzbqrrzcN5u', NOW() - ROUND(RANDOM() * 365) * '1 day'::interval + ROUND(RANDOM() * 86400) * '1 second'::interval, null, null);

-- SETTING USERS ROLES
DO'
DECLARE
ROLE_USER_ID INT; ROLE_ADMIN_ID INT;
USER_1_ID INT; USER_2_ID INT; USER_3_ID INT; USER_4_ID INT; USER_5_ID INT;
USER_6_ID INT; USER_7_ID INT; USER_8_ID INT; USER_9_ID INT; USER_10_ID INT;
ADMIN_1_ID INT; ADMIN_2_ID INT;
BEGIN
SELECT ID INTO ROLE_USER_ID FROM ROLES WHERE ENUMERATED_ROLE=''ROLE_USER'';
SELECT ID INTO ROLE_ADMIN_ID FROM ROLES WHERE ENUMERATED_ROLE=''ROLE_ADMIN'';
SELECT ID INTO USER_1_ID FROM USERS WHERE NAME=''user1'';
SELECT ID INTO USER_2_ID FROM USERS WHERE NAME=''user2'';
SELECT ID INTO USER_3_ID FROM USERS WHERE NAME=''user3'';
SELECT ID INTO USER_4_ID FROM USERS WHERE NAME=''user4'';
SELECT ID INTO USER_5_ID FROM USERS WHERE NAME=''user5'';
SELECT ID INTO USER_6_ID FROM USERS WHERE NAME=''user6'';
SELECT ID INTO USER_7_ID FROM USERS WHERE NAME=''user7'';
SELECT ID INTO USER_8_ID FROM USERS WHERE NAME=''user8'';
SELECT ID INTO USER_9_ID FROM USERS WHERE NAME=''user9'';
SELECT ID INTO USER_10_ID FROM USERS WHERE NAME=''user10'';
SELECT ID INTO ADMIN_1_ID FROM USERS WHERE NAME=''admin'';
SELECT ID INTO ADMIN_2_ID FROM USERS WHERE NAME=''admin2'';
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_1_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_2_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_3_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_4_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_5_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_6_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_7_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_8_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_9_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (USER_10_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (ADMIN_1_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (ADMIN_1_ID, ROLE_ADMIN_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (ADMIN_2_ID, ROLE_USER_ID);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (ADMIN_2_ID, ROLE_ADMIN_ID);
END;' LANGUAGE PLPGSQL;

-- -- TOPICS WITH POSTS (EACH TOPIC MUST HAVE AT LEAST ONE POST), UNCOMMENT IN CASE OF NEED SOME DATA, COMMENT IN CASE OF TEST
 DO'
 DECLARE
 COUNTER_FOR_OUTER_TOPIC_LOOP INTEGER := 1;
 COUNTER_FOR_INNER_POST_LOOP INTEGER := 1;
 RANDOM_DATE_TIME_OF_NEXT_TOPIC_IN_LOOP TIMESTAMP;
 ID_OF_TOPIC_AUTHOR INTEGER;
 RANDOM_DATE_TIME_OF_LAST_POST_UNDER_TOPIC TIMESTAMP;
 ID_OF_POST_AUTHOR INTEGER;
 BEGIN
     WHILE COUNTER_FOR_OUTER_TOPIC_LOOP < ROUND(RANDOM() * 5000 + 2500) LOOP
     RANDOM_DATE_TIME_OF_NEXT_TOPIC_IN_LOOP = NOW() - ROUND(RANDOM() * 1460) * ''1 day''::interval + ROUND(RANDOM() * 86400) * ''1 second''::interval;
     ID_OF_TOPIC_AUTHOR = (SELECT ID FROM USERS ORDER BY RANDOM() LIMIT 1);
     EXIT WHEN RANDOM_DATE_TIME_OF_NEXT_TOPIC_IN_LOOP > NOW();
     INSERT INTO TOPICS (CLOSED, DATE_TIME, TIME_OF_ACTUALIZATION, TITLE, CATEGORY_ID, USER_ID) VALUES
     (FALSE, RANDOM_DATE_TIME_OF_NEXT_TOPIC_IN_LOOP, RANDOM_DATE_TIME_OF_NEXT_TOPIC_IN_LOOP,
     (ARRAY [''Random topic name'', ''Test topic'', ''Another test topic'', ''Sample very very long topic with so much words in it I cant even imagine'',
     ''Another very random topic about something and topic id for example '' || COUNTER_FOR_OUTER_TOPIC_LOOP, ''Example topic in random category''])[ROUND(RANDOM() * 5) + 1],
     (SELECT ID FROM CATEGORIES ORDER BY RANDOM() LIMIT 1), ID_OF_TOPIC_AUTHOR);
         WHILE COUNTER_FOR_INNER_POST_LOOP < ROUND(RANDOM() * 250 + 25) LOOP
         IF COUNTER_FOR_INNER_POST_LOOP = 1 THEN
         RANDOM_DATE_TIME_OF_LAST_POST_UNDER_TOPIC = RANDOM_DATE_TIME_OF_NEXT_TOPIC_IN_LOOP;
         ID_OF_POST_AUTHOR = ID_OF_TOPIC_AUTHOR;
         ELSE RANDOM_DATE_TIME_OF_LAST_POST_UNDER_TOPIC = RANDOM_DATE_TIME_OF_LAST_POST_UNDER_TOPIC + ROUND(RANDOM() * 86400) * ''1 second''::interval;
         ID_OF_POST_AUTHOR = (SELECT ID FROM USERS ORDER BY RANDOM() LIMIT 1);
         END IF;
         EXIT WHEN RANDOM_DATE_TIME_OF_LAST_POST_UNDER_TOPIC > NOW();
         INSERT INTO POSTS (CONTENT, DATE_TIME, MODERATED, NUMBER, TOPIC_ID, USER_ID) VALUES
         ((ARRAY [''Sample comment'', ''Very long example post which does not carry any information in it'', ''Another test comment'',
         ''Sample very very long post with so much words in it I cant even imagine'', ''Another very random post about nothing special'', ''Example post'',
         ''Test post with a little bit words in it'', ''One more random post'', ''Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt''])[ROUND(RANDOM() * 8) + 1],
         RANDOM_DATE_TIME_OF_LAST_POST_UNDER_TOPIC, FALSE, COUNTER_FOR_INNER_POST_LOOP, COUNTER_FOR_OUTER_TOPIC_LOOP, ID_OF_POST_AUTHOR);
         COUNTER_FOR_INNER_POST_LOOP := COUNTER_FOR_INNER_POST_LOOP + 1;
         END LOOP;
         COUNTER_FOR_INNER_POST_LOOP := 1;
     UPDATE TOPICS
     SET TIME_OF_ACTUALIZATION = (SELECT DATE_TIME FROM POSTS WHERE TOPIC_ID = COUNTER_FOR_OUTER_TOPIC_LOOP ORDER BY DATE_TIME DESC LIMIT 1)
     WHERE ID = COUNTER_FOR_OUTER_TOPIC_LOOP;
     COUNTER_FOR_OUTER_TOPIC_LOOP := COUNTER_FOR_OUTER_TOPIC_LOOP + 1;
     END LOOP;
 END;' LANGUAGE PLPGSQL;
