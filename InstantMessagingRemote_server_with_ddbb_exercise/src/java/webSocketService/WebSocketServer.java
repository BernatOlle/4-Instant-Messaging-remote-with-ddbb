package webSocketService;

import com.google.gson.Gson;
import entity.Message;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import util.Subscription_request;
import entity.Topic;
import util.Subscription_close;
import entity.service.TopicFacadeREST;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ServerEndpoint("/ws")
public class WebSocketServer {

  TopicFacadeREST topicFacadeREST = lookupTopicFacadeRESTBean();

  // to store the open websocket sessions with the clients:
  private static Set<Session> sessions = new HashSet<Session>();
  
  // to store the subscriptions to the different topics of the connected clients:
  private static Map<Session, List<Topic>> subscriptions = new HashMap<Session, List<Topic>>();

  @OnMessage
  public void onMessage(String message, Session session)
    throws IOException, SQLException {
    System.out.println("onMessage: " + message);

    Subscription_request s_req = new Gson().fromJson(message, Subscription_request.class);
    
    // check if the topic exists, otherwise exit:
    // ...
    
    // process the subscription request, from a given client, according
    // to how the websocket client has been programmed at the other end:
    // ...
    List <Topic> topics = topicFacadeREST.findAll();
  
    

    // Verificar si el tema existe
    if (!topics.contains(s_req.topic)) {
      session.getBasicRemote().sendText("Error: El tema no existe.");
      return;
    }

    // Agregar el tema a las suscripciones del cliente
    List<Topic> subscribedTopics = subscriptions.get(session);
    if (!subscribedTopics.contains(s_req.topic)) {
      subscribedTopics.add(s_req.topic);
      subscriptions.put(session, subscribedTopics);
      
    } 

  }

  @OnOpen
  public void onOpen(Session session) {
    sessions.add(session);
    subscriptions.put(session, new ArrayList<Topic>());
    System.out.println("new session: " + session.getId());
  }

  @OnClose
  public void onClose(Session session) {
    System.out.println("closed session: " + session.getId());
    sessions.remove(session);
    subscriptions.remove(session);
  }

  public static void notifyNewMessage(Message message) {
    String json_message = new Gson().toJson(message);
    Topic topic = message.getTopic();
    
    try {
      
      for (Map.Entry<Session, List<Topic>> entry : subscriptions.entrySet()) {
        Session session = entry.getKey();
        List<Topic> subscribedTopics = entry.getValue();

        if (subscribedTopics.contains(topic) && session.isOpen()) {
          System.out.print("message");
          session.getBasicRemote().sendText(json_message);
        }
      }
    
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public static void notifyTopicClose(Subscription_close subs_close) {
    Gson gson = new Gson();
    String json_subs_close = gson.toJson(subs_close);
    Topic topic = subs_close.topic;
    
    try {
      
      for (Map.Entry<Session, List<Topic>> entry : subscriptions.entrySet()) {
        Session session = entry.getKey();
        List<Topic> subscribedTopics = entry.getValue();

        if (subscribedTopics.contains(topic) && session.isOpen()) {
          session.getBasicRemote().sendText(json_subs_close);
        }
      }

      // Eliminar el tema de todas las suscripciones
      for (List<Topic> subscribedTopics : subscriptions.values()) {
        subscribedTopics.remove(topic);
      }
      
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private TopicFacadeREST lookupTopicFacadeRESTBean() {
    try {
      Context c = new InitialContext();
      return (TopicFacadeREST) c.lookup("java:global/InstantMessagingRemote_server_with_ddbb_exercise/TopicFacadeREST!entity.service.TopicFacadeREST");
    } catch (NamingException ne) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
      throw new RuntimeException(ne);
    }
  }

}
