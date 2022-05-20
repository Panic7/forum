create table users
(
    id         serial not null primary key,
    username   text   not null,
    email      text   not null,
    password   text   not null,
    role       text   not null,
    status     text   not null,
    picture_id integer
);

create table pictures
(
    id  serial not null primary key,
    url text
);

create table topics
(
    id            serial                      not null primary key,
    header        text                        not null,
    is_anonymous  boolean                     not null,
    description   text                        not null,
    views         int                         not null,
    creation_date timestamp without time zone not null,
    category_id   int                         not null,
    user_id       int                         not null
);

create table categories
(
    id    serial not null primary key,
    title text   not null
);

create table comments
(
    id            serial                      not null primary key,
    content       text                        not null,
    creation_date timestamp without time zone not null,
    user_id       int                         not null,
    topic_id      int                         not null
);

alter table topics
    add constraint fk_topics_users
        foreign key (user_id)
            references users (id);

alter table topics
    add constraint fk_topics_categories
        foreign key (category_id)
            references categories (id);

alter table users
    add constraint fk_users_pictures
        foreign key (picture_id)
            references pictures (id);

alter table comments
    add constraint fk_comments_topics foreign key (topic_id) references topics (id);
alter table comments
    add constraint fk_comments_users foreign key (user_id) references users (id);