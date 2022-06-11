create table if not exists users
(
    id          serial not null primary key,
    username    text   not null,
    email       text   not null,
    password    text   not null,
    role        text   not null,
    status      text   not null,
    picture_url text
);

create table if not exists topics
(
    id            serial                      not null primary key,
    header        text                        not null,
    is_anonymous  boolean                     not null,
    description   text                        not null,
    score         double precision            not null,
    creation_date timestamp without time zone not null,
    category_id   int                         not null,
    user_id       int                         not null
);

create table if not exists categories
(
    id    serial not null primary key,
    title text   not null
);

create table if not exists comments
(
    id            serial                      not null primary key,
    content       text                        not null,
    creation_date timestamp without time zone not null,
    is_anonymous  boolean                     not null,
    user_id       int                         not null,
    topic_id      int                         not null
);

ALTER TABLE topics
    DROP CONSTRAINT IF EXISTS fk_topics_users;
ALTER TABLE topics
    DROP CONSTRAINT IF EXISTS fk_topics_categories;
ALTER TABLE users
    DROP CONSTRAINT IF EXISTS fk_users_pictures;
ALTER TABLE comments
    DROP CONSTRAINT IF EXISTS fk_comments_topics;
ALTER TABLE comments
    DROP CONSTRAINT IF EXISTS fk_comments_users;

alter table topics
    add constraint fk_topics_users
        foreign key (user_id)
            references users (id);

alter table topics
    add constraint fk_topics_categories
        foreign key (category_id)
            references categories (id);

alter table comments
    add constraint fk_comments_topics foreign key (topic_id) references topics (id);
alter table comments
    add constraint fk_comments_users foreign key (user_id) references users (id);