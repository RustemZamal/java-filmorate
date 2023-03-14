SET MODE PostgreSQL;

CREATE TABLE IF NOT EXISTS users
(
    user_id  bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar(20) NOT NULL UNIQUE,
    login    varchar(20) NOT NULL UNIQUE,
    name     varchar(20),
    birthday Date         NOT NULL
);


CREATE TABLE IF NOT EXISTS mpa_rating
(
    mpa_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name varchar(7) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS director
(
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film
(
    film_id bigint GENERATED BY DEFAULT AS IDENTITY,
    name varchar(50) NOT NULL,
    description varchar(200),
    release_date DATE CHECK (release_date > '1895-12-28'),
    duration integer,
    mpa_rating smallint NOT NULL,
    FOREIGN KEY (mpa_rating) REFERENCES mpa_rating (mpa_id),
    CONSTRAINT films_pkey PRIMARY KEY (film_id)
);

CREATE TABLE IF NOT EXISTS friendship
(
    user_id bigint NOT NULL,
    friend_id bigint NOT NULL,
    status boolean DEFAULT 'false',
    CONSTRAINT friendship_pkey PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT friendship_check CHECK (user_id <> friend_id)
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name varchar (15) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS like_to_film
(
    user_id bigint NOT NULL,
    film_id bigint NOT NULL,
    CONSTRAINT likes_to_films_pkey PRIMARY KEY (user_id, film_id),
    CONSTRAINT likes_to_films_film_id FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    CONSTRAINT likes_to_films_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id bigint NOT NULL,
    genre_id bigint NOT NULL,
    CONSTRAINT film_genre_pkey PRIMARY KEY (genre_id, film_id),
    CONSTRAINT film_genre_film_id_fkey FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    CONSTRAINT film_genre_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES genre (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_director
(
    film_id bigint NOT NULL,
    director_id bigint NOT NULL,
    CONSTRAINT film_director_pkey PRIMARY KEY (film_id, director_id),
    CONSTRAINT film_director_film_id_fkey FOREIGN KEY(film_id) REFERENCES film(film_id) ON DELETE CASCADE,
    CONSTRAINT film_director_director_id_fkey FOREIGN KEY(director_id) REFERENCES director(id) ON DELETE CASCADE
);