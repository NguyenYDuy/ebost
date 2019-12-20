create table users(
    user_id numeric(10) primary key,
    password varchar(255),
    username varchar(40) not null,
    email varchar(255),
    phone varchar (255),
    fullname varchar(255),
    birthday date,
    isactive int not null default 1,
    created timestamp default now(),
    updated timestamp default now()
);

create sequence users_sq;

create table books(
    book_id numeric(10) primary key ,
    name varchar(255),
    description text,
    isactive int not null default 0,
    created timestamp default now(),
    updated timestamp default now(),
    createdby numeric(10) references users(user_id)
)
create sequence books_sq;

create table follows(
    user_id numeric(10),
    follow_id numeric(10),
    isactive int not null default 1,
    primary key (user_id,follow_id)
);
alter table follows add column  updated timestamp default now();
alter table follows add column  created timestamp default now();  
alter table books add column views numeric(10);
create table chapters(
    chapter_id numeric(10),
    title varchar(255),
    content text,
    isactive int not null default 0,
    created timestamp default now(),
    updated timestamp default now(),
    book_id numeric(10) references books(book_id)
);
alter table chapters add column rating numeric(5);
alter table chapters add primary key (chapter_id)
create table libraries(
    user_id numeric(10),
    book_id numeric(10),
    isactive int not null default 1,
    last_seen timestamp default now(),
    primary key (user_id,book_id)

);
create sequence chapters_sq;
alter table chapters add column orders numeric(10);

