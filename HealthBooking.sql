CREATE DATABASE user_db;
USE user_db;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(150) NOT NULL,
  birth DATE,
  gender ENUM('male', 'female', 'other') DEFAULT 'female',
  email VARCHAR(150) UNIQUE NOT NULL,
  phone VARCHAR(30) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('patient', 'doctor', 'admin') NOT NULL DEFAULT 'patient',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  status TINYINT(1) NOT NULL DEFAULT 1 CHECK (status IN (0, 1, 2)),
  avatar VARCHAR(255),
  address VARCHAR(255) NOT NULL,
  location POINT SRID 4326 NOT NULL,
  SPATIAL INDEX idx_doctors_location (location)
);

-- Tạo 2 Bác sĩ
INSERT INTO users (id, full_name, birth, gender, email, phone, password, role, status, avatar, address, location) VALUES
(1, 'Nguyễn Văn An', '1985-05-20', 'male', 'dr.nguyenvanan@clinic.com', '0901112221', '123', 'doctor', 1, 'avatars/doctor_an.jpg',  '273 An Dương Vương, Phường Chợ Quán, Hồ Chí Minh, Việt Nam', ST_SRID(POINT(106.682414, 10.760130), 4326)),
(2, 'Trần Thị Bích', '1990-11-15', 'female', 'dr.tranthibich@clinic.com', '0901112222', '123', 'doctor', 1, 'avatars/doctor_bich.jpg', '72 Lê Thánh Tôn, Bến Nghé, Quận 1, Hồ Chí Minh, Việt Nam', ST_SRID(POINT(106.702077, 10.777799), 4326));

-- Tạo 2 Bệnh nhân
INSERT INTO users (id, full_name, birth, gender, email, phone, password, role, status, avatar, address, location) VALUES
(3, 'Lê Văn Cường', '1995-02-10', 'male', 'lecuong@email.com', '0903334443', '123', 'patient', 1, 'avatars/patient_cuong.jpg', '171 Đ. Cao Đạt, Phường Chợ Quán, Hồ Chí Minh, Việt Nam', ST_SRID(POINT(106.683662, 10.754311), 4326)),
(4, 'Phạm Thị Dung', '2000-07-30', 'female', 'phamtdung@email.com', '0903334444', '123', 'patient', 1, 'avatars/patient_dung.jpg', '231 Nguyễn Văn Cừ,Phường 4, Quận 5, Hồ Chí Minh, Việt Nam', ST_SRID(POINT(106.682299, 10.763327), 4326));


CREATE DATABASE doctor_db;
USE doctor_db;


CREATE TABLE doctors (
  user_id INT PRIMARY KEY, 
  specialization VARCHAR(255) NOT NULL,
  description TEXT,
  bank_number VARCHAR(50) NOT NULL,
  bank_name VARCHAR(50) NOT NULL,
  bank_id VARCHAR(100) NOT NULL,
  INDEX idx_doctors_specialization (specialization)
);

CREATE TABLE doctor_recurring_schedules (
  id INT AUTO_INCREMENT PRIMARY KEY,
  doctor_id INT NOT NULL, 
  weekday TINYINT NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  status ENUM('active','inactive') DEFAULT 'active',
  INDEX idx_doctor_recurring_schedules_doctor_id (doctor_id), 
  CONSTRAINT fk_doctor_recurring_schedules_doctors 
    FOREIGN KEY (doctor_id) 
    REFERENCES doctors(user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE doctor_booking_policies (
  id INT AUTO_INCREMENT PRIMARY KEY,
  doctor_id INT UNIQUE NOT NULL,
  booking_fee INT DEFAULT 0,
  cancel_policy_minutes INT DEFAULT 0,
  cancel_fee_percent TINYINT DEFAULT 0,
  default_duration_minutes INT DEFAULT 30,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_doctor_booking_policies_doctor_id (doctor_id), 
  CONSTRAINT fk_doctor_booking_policies_doctors 
    FOREIGN KEY (doctor_id) 
    REFERENCES doctors(user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

INSERT INTO doctors (
  user_id,
  specialization,
  description,
  bank_number,
  bank_name,
  bank_id
) VALUES
(
  1,
  'TIM,NHI',
  'Chuyên gia về các bệnh lý tim mạch với hơn 10 năm kinh nghiệm.',
  '123456789',
  'Vietcombank',
  '12345'
),
(
  2,
  'NHI',
  'Chuyên khám và điều trị cho trẻ em và trẻ sơ sinh.',
  '987654321',
  'Techcombank',
  '12345'
);


INSERT INTO doctor_booking_policies (doctor_id, booking_fee, cancel_policy_minutes, cancel_fee_percent, default_duration_minutes) VALUES
    (1, 150000, 120, 50, 30), 
    (2, 120000, 60, 100, 20);  




CREATE DATABASE appointment_db;
USE appointment_db;

CREATE TABLE appointments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL, 
  doctor_id INT NOT NULL,  
  appointment_starttime DATETIME NOT NULL,
  appointment_endtime DATETIME DEFAULT NULL,
  status ENUM('pending', 'paid', 'cancelled', 'finished', 'transferred') DEFAULT 'pending',
  booking_fee INT DEFAULT 0,
  cancel_policy_minutes INT DEFAULT 0,
  cancel_fee_percent TINYINT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_appointments_patient_id (patient_id), 
  INDEX idx_appointments_doctor_id (doctor_id)  
  
);

INSERT INTO appointments (id, patient_id, doctor_id, appointment_starttime, appointment_endtime, status, booking_fee) VALUES
(101, 3, 1, '2025-10-15 09:00:00', '2025-10-15 09:30:00', 'finished', 150000);

INSERT INTO appointments (id, patient_id, doctor_id, appointment_starttime, appointment_endtime, status, booking_fee) VALUES
(102, 4, 2, '2025-10-16 14:00:00', '2025-10-16 14:20:00', 'paid', 120000);

INSERT INTO appointments (id, patient_id, doctor_id, appointment_starttime, status, booking_fee) VALUES
(103, 3, 1, '2025-10-15 10:00:00', 'cancelled', 150000);
