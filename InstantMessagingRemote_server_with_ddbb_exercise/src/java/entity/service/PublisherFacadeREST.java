/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import entity.Publisher;
import entity.Topic;
import util.Subscription_close;
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
import webSocketService.WebSocketServer;

/**
 *
 * @author juanluis
 */
@Stateless
@Path("entity.publisher")
public class PublisherFacadeREST extends AbstractFacade<Publisher> {
  
  @PersistenceContext(unitName = "PubSubWebServerPU")
  private EntityManager em;

  public PublisherFacadeREST() {
    super(Publisher.class);
  }
  
  @POST
  @Path("create")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public void create(Publisher entity) {
    
    // first, create the topic if necessary, otherwise
    // set the topic retrieved from the query into the argument
    // of the method create, entity, to be saved as a new publisher:
    
    // ...
    
    
    // then, create the new publisher for that topic, or
    // modify it, if he/she was publisher of a previous topic:
    
    // ...
    throw new RuntimeException("To be completed by the student");
    
  }

  @POST
  @Path("delete")
  @Consumes({"application/xml", "application/json"})
  public void delete(Publisher entity) {
    
    // check out if the user is really a publisher:
    
    // ...
    
    // if that is the case, use the fresh new instance of Publisher
    // obtained from the query to the data base to delete that publisher:
    
    // ...
    
    // check if it was the last publisher of that topic, if so, delete the
    // topic from the Topic table, and notify all the currently connected
    // clients, using the WebSocketServer, about the topic been closed:
    
    // ...
    throw new RuntimeException("To be completed by the student");
    
    
  }
  
  @POST
  @Path("publisherOf")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public Publisher publisherOf(User entity) {  
    
    // retrieve if the user is publisher of any topic:
    
    // ...
    throw new RuntimeException("To be completed by the student");
    
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
  
}
