package webSocketService;

import apiREST.Cons;
import com.google.gson.Gson;
import entity.Message;
import entity.Topic;
import util.Subscription_close;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import subscriber.Subscriber;
import util.Subscription_request;

@ClientEndpoint
public class WebSocketClient {

  static Map<Topic, Subscriber> subscriberMap;
  static Session session;

  public static void newInstance() {
    subscriberMap = new HashMap<Topic, Subscriber>();
    try {
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      session = container.connectToServer(WebSocketClient.class,
        URI.create(Cons.SERVER_WEBSOCKET));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void close() {
    try {
      session.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static synchronized void addSubscriber(Topic topic, Subscriber subscriber) {
    try {
      
      // Create a subscription request for adding
      Subscription_request subscriptionRequest = new Subscription_request(topic, Subscription_request.Type.ADD);
      String jsonRequest = new Gson().toJson(subscriptionRequest);
      session.getBasicRemote().sendText(jsonRequest);

      // Add the subscriber to the local map
      subscriberMap.put(topic, subscriber);
      System.out.println("Subscriber added for topic: " + topic.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static synchronized void removeSubscriber(Topic topic) {
    try {
      // Create a subscription request for removing
      Subscription_request subscriptionRequest = new Subscription_request(topic, Subscription_request.Type.REMOVE);
      String jsonRequest = new Gson().toJson(subscriptionRequest);
      session.getBasicRemote().sendText(jsonRequest);

      // Remove the subscriber from the local map
      subscriberMap.remove(topic);
      System.out.println("Subscriber removed for topic: " + topic.getName());      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @OnMessage
  public void onMessage(String json) {
      
      
    System.out.println("Received message: " + json);
    Gson gson = new Gson();  
    Subscription_close subs_close = gson.fromJson(json, Subscription_close.class);

    //ordinary message from topic:
    if (subs_close.cause==null) {
      try {
        Message message = gson.fromJson(json, Message.class);
        Topic topic = message.getTopic();

        // Notify the subscriber if one exists for the topic
        Subscriber subscriber = subscriberMap.get(topic);
        if (subscriber != null) {
          subscriber.onMessage(message);
        } else {
          System.out.println("No subscriber found for topic: " + topic.getName());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    }
    //ending subscription message:
    else {
      
      try {
        Topic topic = subs_close.topic;
        removeSubscriber(topic);
        System.out.println("Topic closed: " + topic.getName() + ", Cause: " + subs_close.cause);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    } 
  }

}
