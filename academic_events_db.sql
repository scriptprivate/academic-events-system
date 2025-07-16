-- Categories table
CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Locations table
CREATE TABLE locations (
    location_id SERIAL PRIMARY KEY,
    location_name VARCHAR(200) NOT NULL,
    address TEXT,
    capacity INTEGER CHECK (capacity > 0),
    city VARCHAR(100),
    state VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Events table
CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    event_name VARCHAR(200) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    registration_deadline DATE,
    max_participants INTEGER CHECK (max_participants > 0),
    registration_fee DECIMAL(10,2) DEFAULT 0.00,
    category_id INTEGER REFERENCES categories(category_id),
    location_id INTEGER REFERENCES locations(location_id),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_dates CHECK (end_date >= start_date),
    CONSTRAINT valid_registration_deadline CHECK (registration_deadline <= start_date)
);

-- Participants table
CREATE TABLE participants (
    participant_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    phone VARCHAR(20),
    institution VARCHAR(200),
    participant_type VARCHAR(20) DEFAULT 'STUDENT' CHECK (participant_type IN ('STUDENT', 'PROFESSOR', 'RESEARCHER', 'OTHER')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Registrations table
CREATE TABLE registrations (
    registration_id SERIAL PRIMARY KEY,
    event_id INTEGER REFERENCES events(event_id) ON DELETE CASCADE,
    participant_id INTEGER REFERENCES participants(participant_id) ON DELETE CASCADE,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'CONFIRMED' CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
    payment_status VARCHAR(20) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'REFUNDED')),
    notes TEXT,
    UNIQUE(event_id, participant_id)
);

-- Insert sample data
INSERT INTO categories (category_name, description) VALUES 
('Conference', 'Multi-day academic conferences'),
('Workshop', 'Hands-on learning workshops'),
('Seminar', 'Educational seminars and talks'),
('Symposium', 'Academic symposiums');

INSERT INTO locations (location_name, address, capacity, city, state) VALUES 
('Main Auditorium', '123 University Ave', 500, 'University City', 'State'),
('Computer Lab A', '456 Tech Building', 30, 'University City', 'State'),
('Conference Hall B', '789 Academic Center', 200, 'University City', 'State');

INSERT INTO events (event_name, description, start_date, end_date, registration_deadline, max_participants, registration_fee, category_id, location_id) VALUES 
('AI & Machine Learning Conference 2025', 'Latest trends in AI and ML', '2025-08-15', '2025-08-17', '2025-08-01', 300, 150.00, 1, 1),
('Java Programming Workshop', 'Advanced Java programming techniques', '2025-07-20', '2025-07-20', '2025-07-15', 25, 50.00, 2, 2),
('Data Science Seminar', 'Introduction to data science', '2025-07-25', '2025-07-25', '2025-07-20', 100, 0.00, 3, 3);

INSERT INTO participants (first_name, last_name, email, phone, institution, participant_type) VALUES 
('John', 'Doe', 'john.doe@university.edu', '555-1234', 'State University', 'STUDENT'),
('Jane', 'Smith', 'jane.smith@university.edu', '555-5678', 'State University', 'PROFESSOR'),
('Bob', 'Johnson', 'bob.johnson@research.org', '555-9012', 'Research Institute', 'RESEARCHER');

-- Create indexes for better performance
CREATE INDEX idx_events_date ON events(start_date);
CREATE INDEX idx_events_category ON events(category_id);
CREATE INDEX idx_registrations_event ON registrations(event_id);
CREATE INDEX idx_registrations_participant ON registrations(participant_id);
CREATE INDEX idx_participants_email ON participants(email);