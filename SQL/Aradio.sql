CREATE DATABASE radio;

CREATE TABLE users (
    userId SERIAL PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    bithday DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    createdOn TIMESTAMP NOT NULL,
    lastLogIn TIMESTAMP NOT NULL
);

CREATE TABLE radio (
    radioId SERIAL PRIMARY KEY,
    station VARCHAR(50) NOT NULL,
    stnid VARCHAR(50) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    location VARCHAR(50) NOT NULL,
    url VARCHAR(255) NOT NULL,
    likes INT
);

CREATE TABLE shows (
    showsId SERIAL PRIMARY KEY,
    shows VARCHAR(255) NOT NULL,
    showTime TIMESTAMP NOT NULL,
    likes INT,
    showDesc TEXT NOT NULL,
    radioId INT NOT NULL,
    CONSTRAINT fk_radio
    FOREIGN KEY (radioId)
    REFERENCES radio (radioId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE favourites (
    favId SERIAL PRIMARY KEY,
    shows  TEXT,
    stations TEXT,
    myuserId INT NOT NULL,
    CONSTRAINT fk_user
    FOREIGN KEY (myuserId)
    REFERENCES myusers (myuserId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);