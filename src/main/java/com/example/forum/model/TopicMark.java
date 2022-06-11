package com.example.forum.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "topic_mark")
@AssociationOverrides({
        @AssociationOverride(name = "pk.user",
                joinColumns = @JoinColumn(name = "user_id")),
        @AssociationOverride(name = "pk.topic",
                joinColumns = @JoinColumn(name = "topic_id"))})
public class TopicMark {

    @EmbeddedId
    TopicUserId pk;

    Boolean mark;
}
