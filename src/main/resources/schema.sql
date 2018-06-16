SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS counters;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS comments;

create table counters
(
  id int auto_increment
    primary key,
  title varchar(45) not null,
  value int default '0' not null
)
;

create table users
(
  id int auto_increment
    primary key,
  password varchar(255) null,
  role varchar(255) null,
  username varchar(255) null
)
;

create table authors
(
  id int auto_increment
    primary key,
  birth_date datetime null,
  email varchar(255) null,
  first_name varchar(255) null,
  gender tinyint null,
  homepage varchar(255) null,
  last_name varchar(255) null,
  middle_name varchar(255) null,
  nickname varchar(255) null,
  user_id int null,
  constraint FK6g6ireq6qd4nxohq9ldidxfin
  foreign key (user_id) references users (id)
)
;

create index FK6g6ireq6qd4nxohq9ldidxfin
  on authors (user_id)
;

create table books
(
  id int auto_increment
    primary key,
  count_pages int null,
  description varchar(255) null,
  file_extension varchar(255) null,
  file_size int null,
  language varchar(255) null,
  modified datetime null,
  title varchar(255) null,
  year varchar(255) null,
  author_id int null,
  user_id int null,
  constraint FKfjixh2vym2cvfj3ufxj91jem7
  foreign key (author_id) references authors (id),
  constraint FKcykkh3hxh89ammmwch0gw5o1s
  foreign key (user_id) references users (id)
)
;

create index FKcykkh3hxh89ammmwch0gw5o1s
  on books (user_id)
;

create index FKfjixh2vym2cvfj3ufxj91jem7
  on books (author_id)
;

create table comments
(
  id int auto_increment
    primary key,
  text varchar(255) null,
  book_id int null,
  user_id int null,
  constraint FK1ey8gegnanvybix5a025vepf4
  foreign key (book_id) references books (id),
  constraint FK8omq0tc18jd43bu5tjh6jvraq
  foreign key (user_id) references users (id)
)
;

create index FK1ey8gegnanvybix5a025vepf4
  on comments (book_id)
;

create index FK8omq0tc18jd43bu5tjh6jvraq
  on comments (user_id)
;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

