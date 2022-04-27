create table users
(
    id         serial not null primary key,
    name       text   not null,
    email      text   not null,
    password   text   not null,
    role       text   not null,
    status     text   not null,
    picture_id integer
);

create table pictures
(
    id   serial not null primary key,
    url  text
);

create table topics
(
    id            serial                      not null primary key,
    header        text                        not null,
    is_anonymous  boolean                     not null,
    description   text                        not null,
    creation_date timestamp without time zone not null,
    category_id   int                         not null,
    author_id     int                         not null
);

create table categories
(
    id    serial not null primary key,
    title text
);

alter table topics
    add constraint fk_topics_users
        foreign key (author_id)
            references users (id);

alter table topics
    add constraint fk_topics_categories
        foreign key (category_id)
            references categories (id);

alter table users
    add constraint fk_users_pictures
        foreign key (picture_id)
            references pictures (id);