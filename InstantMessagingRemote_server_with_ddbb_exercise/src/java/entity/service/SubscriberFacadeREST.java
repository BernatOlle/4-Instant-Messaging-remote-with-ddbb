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
    
    // first, check out if the topic from which to subscribe is defined at
    // the Topic table, otherwise return the corresponding message:
    
    // ...
    
    // return a Subscription_check after saving the user as subscriber of the
    // topic:
    
    // ...
    throw new RuntimeException("To be completed by the student");
    
  }

  @POST
  @Path("delete")
  @Consumes({"application/xml", "application/json"})
  public Subscription_check check_to_delete(Subscriber entity) {
    
    // first, check out if the topic from which to unsubscribe is defined at
    // the Topic table, otherwise return the corresponding message:
    
    // ...
    
    // check out if the user was subscribed to the intended topic, otherwise
    // return the corresponding message. Return the corresponding message
    // after removing the user from been subscribed to that topic:
    
    // ...
    throw new RuntimeException("To be completed by the student");
    
  }

  @POST
  @Path("subscriptions")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public List<Subscriber> subscriptions(User entity) {
    
    // return the list of subscriptions of the requested user:
    
    // ...
    throw new RuntimeException("To be completed by the student");
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

}
