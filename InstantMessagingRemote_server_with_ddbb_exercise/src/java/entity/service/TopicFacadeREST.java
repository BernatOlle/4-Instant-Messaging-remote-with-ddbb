/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import entity.Topic;
import util.Topic_check;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author upcnet
 */
@Stateless
@Path("entity.topic")
public class TopicFacadeREST extends AbstractFacade<Topic> {

  @PersistenceContext(unitName = "PubSubWebServerPU")
  private EntityManager em;

  public TopicFacadeREST() {
    super(Topic.class);
  }

  @POST
  @Path("isTopic")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public Topic_check isTopic(Topic topic) {
    
    // return if the topic is defined at the Topic table:
    // ...
    throw new RuntimeException("To be completed by the student");

  }

  @GET
  @Path("allTopics")
  @Produces({"application/xml", "application/json"})
  public List<Topic> findAll() {
    return super.findAll();
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

}
