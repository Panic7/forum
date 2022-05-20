package com.example.forum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ForumApplicationTests {







/*	@JsonTest
@Autowired
	private JacksonTester<Topic> jsonT;
	@Autowired
	private JacksonTester<Comment> jsonC;
	@Autowired
	private JacksonTester<User> jsonU;
	@Test
	void wwernlsjdf() throws IOException {
		Topic topic = new Topic();
		topic.setId(1);

		User user = new User();
		user.setID(1);

		Comment comment1 = new Comment();
		comment1.setId(1);
		comment1.setUser(user);
		comment1.setTopic(topic);
		Set<Comment> comments = Set.of(comment1);

		user.setComments(comments);
		topic.setComments(comments);
		topic.setUser(user);

		Topic getTopic = comment1.getTopic();

		JsonContent<Topic> resultT = this.jsonT.write(topic);
		JsonContent<Comment> resultC = this.jsonC.write(comment1);
		JsonContent<User> resultU = this.jsonU.write(user);
		Topic desTopic = this.jsonT.parse(resultT.getJson()).getObject();
		User desUser = this.jsonU.parse(resultU.getJson()).getObject();
		Comment desComment = this.jsonC.parse(resultC.getJson()).getObject();
		Topic getDesTopic = desComment.getTopic();
	}*/


}
