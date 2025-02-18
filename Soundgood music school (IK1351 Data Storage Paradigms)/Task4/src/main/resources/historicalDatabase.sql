-- Table: historical_lessons (Denormalization)
CREATE TABLE historical_lessons (
    historical_id SERIAL PRIMARY KEY, -- Unique ID for each lesson
    lesson_type VARCHAR(20) NOT NULL, -- Type: individual, group, or ensemble
    genre VARCHAR(50), -- Genre, only for ensemble lessons
    instrument VARCHAR(50), -- Instrument, not for ensemble lessons
    lesson_price NUMERIC(10, 2) NOT NULL, -- Price of the lesson
    student_name VARCHAR(100) NOT NULL, -- Student's name
    student_email VARCHAR(100) NOT NULL -- Student's email
);