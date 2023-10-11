
INSERT INTO course (year, semester, course_id, title, instructor) VALUES
(2020, 'Fall', 30157, 'BUS 203 - Financial Accounting', 'cchou@csumb.edu'),
(2020, 'Fall', 30163, 'BUS 306 - Fundamentals of Marketing', 'anariswari@csumb.edu'),
(2020, 'Fall', 30291, 'BUS 304 - Business Communication, Pro-seminar & Critical Thinking', 'kposteher@csumb.edu'),
(2020, 'Fall', 31045, 'CST 363 - Introduction to Database Systems', 'dwisneski@csumb.edu'),
(2020, 'Fall', 31249, 'CST 237 - Intro to Computer Architecture', 'sislam@csumb.edu'),
(2020, 'Fall', 31253, 'BUS 307 - Finance', 'hwieland@csumb.edu'),
(2020, 'Fall', 31747, 'CST 238 - Introduction to Data Structures', 'jgross@csumb.edu'),
(2021, 'Fall', 40443, 'CST 438 - engineering', 'dwisneski@csumb.edu');
INSERT INTO assignment (id, due_date, name, course_id) values
(1, '2021-09-01', 'db design', 31045),
(2, '2021-09-02', 'requirements', 31045),
(3, '2021-09-02', 'assignment4', 40443),
(4, '2021-09-02', 'assignment4 part 2', 40443);
INSERT INTO enrollment (id, student_email, student_name, course_id)  values
(2, 'dwisneski@csumb.edu', 'david', 31045),
(3, 'tom@csumb.edu', 'tom', 40443),
(4, 'test@csumb.edu', 'test', 40443)
;
insert into assignment_grade (score, assignment_id, enrollment_id) values
(94, 2, 2),
(95, 2, 3),
(80, 3 ,3),
(95, 3 ,4)
;

