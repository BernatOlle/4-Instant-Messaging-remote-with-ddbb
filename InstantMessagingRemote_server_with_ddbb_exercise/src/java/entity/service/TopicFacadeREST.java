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
    
    // Crear una instancia de respuesta
    Topic_check topicCheck = new Topic_check();

    // Verificar si el tema existe en la base de datos
    if (topic != null && topic.getName() != null) {
      Query query = em.createQuery("SELECT t FROM Topic t WHERE t.name = :name");
      query.setParameter("name", topic.getName());
      List<Topic> topics = query.getResultList();

      if (!topics.isEmpty()) {
        topicCheck.isOpen = true;
        topicCheck.topic = topics.get(0);
      } else {
        topicCheck.isOpen = false;
      }
    } else {
      topicCheck.isOpen = false;
    }

    return topicCheck;

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
