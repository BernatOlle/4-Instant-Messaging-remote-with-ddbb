package topicmanager;

import apiREST.apiREST_Message;
import apiREST.apiREST_Publisher;
import apiREST.apiREST_Subscriber;
import apiREST.apiREST_Topic;
import entity.Message;
import util.Subscription_check;
import entity.Topic;
import util.Topic_check;
import entity.User;
import java.util.ArrayList;
import java.util.List;
import publisher.Publisher;
import publisher.PublisherStub;
import subscriber.Subscriber;
import webSocketService.WebSocketClient;
import java.util.List;


public class TopicManagerStub implements TopicManager {

  public User user;

  public TopicManagerStub(User user) {
    WebSocketClient.newInstance();
    this.user = user;
  }

  public void close() {
    WebSocketClient.close();
  }

  public Publisher addPublisherToTopic(Topic topic) {
    entity.Publisher publisher = new entity.Publisher();
    publisher.setTopic(topic);
    publisher.setUser(user);
    apiREST_Publisher.createPublisher(publisher);
    return new PublisherStub(topic);
  }

  public boolean removePublisherFromTopic(Topic topic) {
    entity.Publisher publisher = new entity.Publisher();
    publisher.setTopic(topic);
    publisher.setUser(user);
    return apiREST_Publisher.deletePublisher(publisher);
  }

  public Topic_check isTopic(Topic topic) {
    return apiREST_Topic.isTopic(topic);
  }

  public List<Topic> topics() {
    return apiREST_Topic.allTopics();
  }

  public Subscription_check subscribe(Topic topic, Subscriber real_subscriber) {
      entity.Subscriber subscriber = new entity.Subscriber();
      subscriber.setTopic(topic);
      subscriber.setUser(user);
      Subscription_check s_c = apiREST_Subscriber.createSubscriber(subscriber);
      System.out.print(s_c);
      if(s_c.result==Subscription_check.Result.OKAY){
        WebSocketClient.addSubscriber(topic, real_subscriber);
      }
      return s_c;
  }

  public Subscription_check unsubscribe(Topic topic, Subscriber real_subscriber) {
      entity.Subscriber subscriber = new entity.Subscriber();
      subscriber.setTopic(topic);
      subscriber.setUser(user);
      
      Subscription_check s_c = apiREST_Subscriber.deleteSubscriber(subscriber);
      if(s_c.result==Subscription_check.Result.OKAY){
        System.out.print("unsubscribe2");
        WebSocketClient.removeSubscriber(topic);
      }
      return s_c;
  }

  public List<Publisher> publisherOf() {
    List<entity.Publisher> publishers = apiREST_Publisher.PublishersOf(user); // Get multiple publishers
    if (publishers != null && !publishers.isEmpty()) {
      // Optionally, return a list of publishers:
      // Converting entity Publishers to stub Publishers and returning
      List<Publisher> stubPublishers = new ArrayList<Publisher>();
      for (entity.Publisher p : publishers) {
        stubPublishers.add(new PublisherStub(p.getTopic()));
      }
      return stubPublishers;
    } else {
      return null; // No publishers found for the user
    }
  }

  public List<entity.Subscriber> mySubscriptions() {
    return apiREST_Subscriber.mySubscriptions(user);
  }

  public List<Message> messagesFrom(Topic topic) {
    return apiREST_Message.messagesFromTopic(topic);
  }

}
