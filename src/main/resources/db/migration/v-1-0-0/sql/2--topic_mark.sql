create table topic_mark(
    mark boolean,
    user_id int not null references users,
    topic_id int not null references topics,
    primary key (user_id, topic_id)
)