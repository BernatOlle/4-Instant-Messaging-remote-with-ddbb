/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import entity.Subscriber;
import util.Subscription_check;
import entity.Topic;
import entity.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author upcnet
 */
@Stateless
@Path("entity.subscriber")
public class SubscriberFacadeREST extends AbstractFacade<Subscriber> {

  @PersistenceContext(unitName = "PubSubWebServerPU")
  private EntityManager em;

  public SubscriberFacadeREST() {
    super(Subscriber.class);
  }

  @POST
  @Path("create")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public Subscription_check check_to_create(Subscriber entity) {
    // Create a response object
    Subscription_check response = new Subscription_check();

    Query topicQuery = em.createQuery("SELECT t FROM Topic t WHERE t.name = :topicName");
    topicQuery.setParameter("topicName", entity.getTopic().getName());
    List<Topic> topics = topicQuery.getResultList();
    System.out.print(topics.get(0).getId());
    
    if (topics.isEmpty()) {
      response.result = Subscription_check.Result.NO_TOPIC;
      response.topic = (null);
      return response;
    }

    // Check if the user is already subscribed to the topic
    Query subscriberQuery = em.createQuery(
      "SELECT s FROM Subscriber s WHERE s.user.id = :userId AND s.topic.id = :topicId"
    );
    subscriberQuery.setParameter("userId", entity.getUser().getId());
    subscriberQuery.setParameter("topicId", entity.getTopic().getId());
    List<Subscriber> subscribers = subscriberQuery.getResultList();

    if (!subscribers.isEmpty()) {
      response.result = Subscription_check.Result.OKAY;
      response.topic = topics.get(0); // Return the topic as the user is already subscribed
      return response;
    }

    

    // Return a successful response
    response.result = Subscription_check.Result.OKAY;
    response.topic = topics.get(0);
    return response;
    
  }

  @POST
  @Path("delete")
  @Consumes({"application/xml", "application/json"})
  public Subscription_check check_to_delete(Subscriber entity) {
    // Create a response object
    Subscription_check response = new Subscription_check();

    // Check if the topic exists in the database
    Query topicQuery = em.createQuery("SELECT t FROM Topic t WHERE t.name = :topicName");
    topicQuery.setParameter("topicName", entity.getTopic().getName());
    List<Topic> topics = topicQuery.getResultList();

    if (topics.isEmpty()) {
      response.result = Subscription_check.Result.NO_TOPIC;
      response.topic = null;
      return response;
    }

    // Check if the user is subscribed to the topic
    Query subscriberQuery = em.createQuery(
      "SELECT s FROM Subscriber s WHERE s.user.id = :userId AND s.topic.id = :topicId"
    );
    subscriberQuery.setParameter("userId", entity.getUser().getId());
    subscriberQuery.setParameter("topicId", entity.getTopic().getId());
    List<Subscriber> subscribers = subscriberQuery.getResultList();

    if (subscribers.isEmpty()) {
      response.result = Subscription_check.Result.NO_SUBSCRIPTION;
      response.topic = topics.get(0);
      return response;
    }

    // Remove the subscription
    Subscriber subscriber = subscribers.get(0);
    em.remove(em.merge(subscriber));
    em.flush();

    // Return a successful response
    response.result = Subscription_check.Result.OKAY;
    response.topic = topics.get(0);
    return response;
  }

  @POST
  @Path("subscriptions")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public List<Subscriber> subscriptions(User entity) {
    Query query = em.createQuery("SELECT s FROM Subscriber s WHERE s.user.id = :userId");
    query.setParameter("userId", entity.getId());

    @SuppressWarnings("unchecked")
    List<Subscriber> subscriptions = query.getResultList();

    return subscriptions;
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

}
