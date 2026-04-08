PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS librarians (
                                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          first_name TEXT NOT NULL,
                                          last_name TEXT NOT NULL,
                                          email TEXT NOT NULL UNIQUE,
                                          username TEXT NOT NULL UNIQUE,
                                          password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
                                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          name TEXT NOT NULL UNIQUE,
                                          description TEXT
);

CREATE TABLE IF NOT EXISTS students (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        student_number TEXT NOT NULL UNIQUE,
                                        first_name TEXT NOT NULL,
                                        last_name TEXT NOT NULL,
                                        school_class TEXT NOT NULL,
                                        email TEXT
);

CREATE TABLE IF NOT EXISTS books (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     title TEXT NOT NULL,
                                     author TEXT NOT NULL,
                                     isbn TEXT NOT NULL UNIQUE,
                                     category_id INTEGER NOT NULL,
                                     cover_image TEXT,
                                     total_quantity INTEGER NOT NULL CHECK (total_quantity >= 0),
                                     available_quantity INTEGER NOT NULL CHECK (available_quantity >= 0),
                                     CHECK (available_quantity <= total_quantity),
                                     FOREIGN KEY (category_id) REFERENCES categories(id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS loans (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     student_id INTEGER NOT NULL,
                                     book_id INTEGER NOT NULL,
                                     registered_by INTEGER NOT NULL,
                                     loan_date TEXT NOT NULL,
                                     due_date TEXT NOT NULL,
                                     return_date TEXT,
                                     status TEXT NOT NULL CHECK (status IN ('ONGOING', 'RETURNED', 'OVERDUE')),
                                     FOREIGN KEY (student_id) REFERENCES students(id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT,
                                     FOREIGN KEY (book_id) REFERENCES books(id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT,
                                     FOREIGN KEY (registered_by) REFERENCES librarians(id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT
);
