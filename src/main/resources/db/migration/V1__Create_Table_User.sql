CREATE TABLE IF NOT EXISTS users (
    id serial primary key,
    username varchar(100) not null unique ,
    password varchar(255) not null,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);