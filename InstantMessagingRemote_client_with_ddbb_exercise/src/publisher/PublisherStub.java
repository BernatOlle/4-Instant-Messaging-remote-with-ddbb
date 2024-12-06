package publisher;

import apiREST.apiREST_Message;
import entity.Message;
import entity.Topic;

public class PublisherStub implements Publisher {

  Topic topic;

  public PublisherStub(Topic topic) {
    this.topic = topic;
  }

  public void publish(Message message) {
    if (topic.equals(message.topic)) {
      apiREST_Message.createMessage(message);
    }
  }

  public Topic topic() {
    return topic;
  }

}
