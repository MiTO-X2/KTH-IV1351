-- Historical Data (Higher grade)
INSERT INTO historical_lessons (lesson_type, genre, instrument, lesson_price, student_name, student_email)
SELECT 
    lp.lesson_type, -- Lesson type
    CASE 
        WHEN lp.lesson_type = 'ensemble' THEN l.genre
        ELSE NULL 
    END AS genre, -- Genre for ensemble lessons
    CASE 
        WHEN lp.lesson_type != 'ensemble' THEN l.instrument_type
        ELSE NULL 
    END AS instrument, -- Instrument type for non-ensemble lessons
    lp.price AS lesson_price, -- Price of the lesson
    s.name AS student_name, -- Student's name
    s.email AS student_email -- Student's email
FROM 
    lesson l
JOIN 
    lesson_price lp
ON 
    l.lesson_price_id = lp.lesson_price_id
JOIN 
    student_lesson sl
ON 
    l.lesson_id = sl.lesson_id
JOIN 
    student s
ON 
    sl.student_id = s.student_id;