INSERT INTO course_table ( name, description, price, category, duration)
VALUES ('Math 101', 'Basic Mathematics', 100, 'Mathematics', 30);

INSERT INTO course_table ( name, description, price, category, duration)
VALUES ( 'Python1', 'Basic Python', 100, 'Python', 24);



INSERT INTO course_enrolled_user_ids (course_id, user_id)
VALUES (1, 1), (1, 2);

INSERT INTO course_enrolled_user_ids (course_id, user_id)
VALUES (2, 1), (2, 2);