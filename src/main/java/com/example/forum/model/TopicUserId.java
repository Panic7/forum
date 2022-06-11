package com.example.forum.model;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TopicUserId implements Serializable {

    @ManyToOne(cascade = CascadeType.MERGE)
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Topic topic;
}
