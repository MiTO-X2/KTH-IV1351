------------------------------------------------------------------------------------
--- Task 3 Queries ----
------------------------------------------------------------------------------------

-- List 1: Total lessons per month and their type
SELECT 
    TO_CHAR(lesson.lesson_start, 'Month') AS Month, 
    COUNT(*) AS Total,
    COUNT(CASE WHEN lesson_price.lesson_type = 'individual' THEN 1 END) AS Individual,
    COUNT(CASE WHEN lesson_price.lesson_type = 'group' THEN 1 END) AS Group,
    COUNT(CASE WHEN lesson_price.lesson_type = 'ensemble' THEN 1 END) AS Ensemble
FROM 
    lesson 
LEFT JOIN
    lesson_price
ON
    lesson.lesson_price_id = lesson_price.lesson_price_id
WHERE 
    EXTRACT(YEAR FROM lesson.lesson_start) = 2024 -- changeable year filter
GROUP BY 
    EXTRACT(MONTH FROM lesson.lesson_start), Month
ORDER BY 
    EXTRACT(MONTH FROM lesson.lesson_start);



-- List 2: The amount of siblings students have
CREATE VIEW siblingAmount AS
SELECT 
    no_of_siblings,
    COUNT(*) AS student_count
FROM 
    (
        SELECT 
            s.student_id,
            COALESCE(sibling_counts.no_of_siblings, 0) AS no_of_siblings
        FROM 
            student s
        LEFT JOIN 
            (
                SELECT 
                    student_id,
                    COUNT(sibling_id) AS no_of_siblings
                FROM 
                    sibling
                GROUP BY 
                    student_id
            ) sibling_counts
        ON 
            s.student_id = sibling_counts.student_id
    ) student_sibling_counts
WHERE no_of_siblings <= 2 -- changeable to other boundary 
GROUP BY 
    no_of_siblings
ORDER BY 
    no_of_siblings;



-- List 3: Checks how many lessons the teachers have assigned for a month
CREATE VIEW lessonsPerTeacher AS
SELECT 
    instructor.instructor_id,
    instructor.name,
    COUNT(lesson.lesson_id) AS total_lessons
FROM 
    instructor
JOIN 
    lesson
ON 
    instructor.instructor_id = lesson.instructor_id
WHERE 
    EXTRACT(YEAR FROM lesson.lesson_start) = EXTRACT(YEAR FROM CURRENT_DATE) --changable
    AND EXTRACT(MONTH FROM lesson.lesson_start) = EXTRACT(MONTH FROM CURRENT_DATE) --changable
GROUP BY 
    instructor.instructor_id, instructor.name
HAVING 
    COUNT(lesson.lesson_id) > 0 --changable
ORDER BY 
    total_lessons DESC;



-- List 4: Checks how many seats are available for the upcoming weeks ensemble lessons
CREATE VIEW ensembleSeatsAvailable AS
SELECT 
	TO_CHAR(lesson.lesson_start, 'Day') AS day, 
	genre, 
    CASE
        WHEN SUM(lesson.students_maximum - lesson.num_of_students) = 0 THEN 'No Seats'
        WHEN SUM(lesson.students_maximum - lesson.num_of_students) BETWEEN 1 AND 2 THEN '1 or 2 Seats'
        WHEN SUM(lesson.students_maximum - lesson.num_of_students) > 2 THEN 'Many Seats'
    END AS free_seats
FROM 
    lesson
LEFT JOIN
    lesson_price
ON 
	lesson.lesson_price_id = lesson_price.lesson_price_id
WHERE
	lesson_price.lesson_type = 'ensemble' AND
	lesson.lesson_start BETWEEN CURRENT_DATE + INTERVAL '1  day' AND CURRENT_DATE + INTERVAL '8 days' --must change example for this to give a result
GROUP BY 
	lesson.lesson_start, lesson.genre
ORDER BY 
	EXTRACT(DAY FROM lesson.lesson_start), DAY, genre;
