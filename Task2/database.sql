-- Table: student
CREATE TABLE student (
    student_id SERIAL PRIMARY KEY,
    personal_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(320),
    street VARCHAR(250),
    zip VARCHAR(50),
    city VARCHAR(250)
);

-- Table: sibling
CREATE TABLE sibling (
    student_id INT NOT NULL,
    sibling_id INT NOT NULL,
    PRIMARY KEY (student_id, sibling_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (sibling_id) REFERENCES student(student_id) ON DELETE CASCADE
);

-- Table: student_phone
CREATE TABLE student_phone (
    phone_number VARCHAR(50),
    student_id INT NOT NULL,
    PRIMARY KEY (phone_number, student_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
);

-- Table: contact_person
CREATE TABLE contact_person (
    student_id INT,
    contact_person_id INT,
    name VARCHAR(250) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    email VARCHAR(320) NOT NULL,
    PRIMARY KEY (student_id, contact_person_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
);

-- Table: instrument
CREATE TABLE instrument (
    instrument_id SERIAL PRIMARY KEY,
    type VARCHAR(250) NOT NULL,
    brand VARCHAR(250),
    location VARCHAR(250) NOT NULL,
    availability INT NOT NULL,
    price INT NOT NULL
);

-- Table: rental
CREATE TABLE rental (
    rental_id SERIAL PRIMARY KEY,
    rental_start_date TIMESTAMP NOT NULL,
    rental_end_date TIMESTAMP NOT NULL,
    duration INT NOT NULL,
    student_id INT NOT NULL,
    instrument_id INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE SET NULL,
    FOREIGN KEY (instrument_id) REFERENCES instrument(instrument_id) ON DELETE SET NULL,
    CONSTRAINT rental_end_date_limit CHECK (rental_end_date <= rental_start_date + INTERVAL '12 months')
);

-- Table: instructor
CREATE TABLE instructor (
    instructor_id SERIAL PRIMARY KEY,
    personal_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(320),
    street VARCHAR(250),
    zip VARCHAR(50),
    city VARCHAR(250)
);

-- Table: instructor_phone
CREATE TABLE instructor_phone (
    phone_number VARCHAR(50),
    instructor_id INT NOT NULL,
    PRIMARY KEY (phone_number, instructor_id),
    FOREIGN KEY (instructor_id) REFERENCES instructor(instructor_id) ON DELETE CASCADE
);

-- Table: availability
CREATE TABLE availability (
    instructor_id INT,
    time_start TIMESTAMP NOT NULL,
    time_end TIMESTAMP NOT NULL,
    PRIMARY KEY (instructor_id, time_start),
    FOREIGN KEY (instructor_id) REFERENCES instructor(instructor_id) ON DELETE CASCADE
);

-- Table: instrument_expertise
CREATE TABLE instrument_expertise (
    instructor_id INT NOT NULL,
    instrument_type VARCHAR(250) NOT NULL,
    PRIMARY KEY (instructor_id, instrument_type),
    FOREIGN KEY (instructor_id) REFERENCES instructor(instructor_id) ON DELETE CASCADE
);

-- Table: lesson_price
CREATE TABLE lesson_price (
    lesson_price_id SERIAL PRIMARY KEY,
    skill_level VARCHAR(50) NOT NULL CHECK (skill_level IN ('beginner', 'intermediate', 'advanced')),
    lesson_type VARCHAR(250) NOT NULL CHECK (lesson_type IN ('individual', 'group', 'ensemble')),
    price INT NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP NOT NULL
);

-- Table: lesson
CREATE TABLE lesson (
    lesson_id SERIAL PRIMARY KEY,
    lesson_start TIMESTAMP NOT NULL,
    lesson_end TIMESTAMP NOT NULL,
    num_of_students INT,
    students_maximum INT,
    students_minimum INT,
    genre VARCHAR(250),
    instrument_type VARCHAR(250),
    lesson_price_id INT NOT NULL,
    instructor_id INT NOT NULL,
    FOREIGN KEY (lesson_price_id) REFERENCES lesson_price(lesson_price_id) ON DELETE SET NULL,
    FOREIGN KEY (instructor_id) REFERENCES instructor(instructor_id) ON DELETE SET NULL
);

-- Table: student_lesson
CREATE TABLE student_lesson (
    student_id INT NOT NULL,
    lesson_id INT NOT NULL,
    PRIMARY KEY (student_id, lesson_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_id) REFERENCES lesson(lesson_id) ON DELETE CASCADE
);

-- Trigger Function
CREATE OR REPLACE FUNCTION check_rental_limit()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM rental WHERE student_id = NEW.student_id) >= 2 THEN
        RAISE EXCEPTION 'A student can only rent up to 2 instruments at a time.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger
CREATE TRIGGER rental_limit_trigger
BEFORE INSERT OR UPDATE ON rental
FOR EACH ROW
EXECUTE FUNCTION check_rental_limit();

-- Trigger Function
CREATE OR REPLACE FUNCTION enforce_genre_constraint()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT lesson_type FROM lesson_price WHERE lesson_price_id = NEW.lesson_price_id) != 'ensemble' THEN
        NEW.genre = NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger on the lesson table
CREATE TRIGGER check_genre
BEFORE INSERT OR UPDATE ON lesson
FOR EACH ROW
EXECUTE FUNCTION enforce_genre_constraint();

-- Trigger Function
CREATE OR REPLACE FUNCTION enforce_instrument_constraint()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT lesson_type FROM lesson_price WHERE lesson_price_id = NEW.lesson_price_id) = 'ensemble' THEN
        NEW.instrument_type = NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger on the lesson table
CREATE TRIGGER check_instrument_type
BEFORE INSERT OR UPDATE ON lesson
FOR EACH ROW
EXECUTE FUNCTION enforce_instrument_constraint();
