drop table if exists album;
drop table if exists book;

create table album (
    album_id bigint not null generated by default as identity primary key,
    title varchar(50),
    artist varchar(20),
    stock_quantity int,
    price int
);

create table book (
    book_id bigint not null generated by default as identity  primary key,
    title varchar(50),
    author varchar(20),
    isbn varchar(20),
    publisher varchar(20),
    stock_quantity int,
    price int
);