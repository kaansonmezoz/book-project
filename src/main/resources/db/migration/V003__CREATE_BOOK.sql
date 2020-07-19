CREATE TABLE book
(
    id                    BIGINT       NOT NULL,
    book_recommended_by   VARCHAR(255),
    date_finished_reading DATE,
    date_started_reading  DATE,
    edition               VARCHAR(255),
    genre                 INTEGER,
    number_of_pages       INTEGER,
    rating                INTEGER,
    series_position       INTEGER,
    title                 VARCHAR(255) NOT NULL,
    author_id             BIGINT,
    shelf_id              BIGINT,
    PRIMARY KEY (id)
) ENGINE = InnoDB
