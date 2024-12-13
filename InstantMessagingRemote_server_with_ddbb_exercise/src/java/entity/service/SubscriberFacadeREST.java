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
     // Crear objeto de respuesta
    Subscription_check response = new Subscription_check();

    // Buscar el tema en la base de datos por su nombre
    Query topicQuery = em.createQuery("SELECT t FROM Topic t WHERE t.name = :topicName");
    topicQuery.setParameter("topicName", entity.getTopic().getName());
    List<Topic> topics = topicQuery.getResultList();
    
    // Verificar si el tema no existe
    if (topics.isEmpty()) {
      response.result = Subscription_check.Result.NO_TOPIC;
      response.topic = null;  // No hay tema para devolver
      return response;
    }

    // Verificar si el usuario ya está suscrito al tema
    Query subscriberQuery = em.createQuery(
      "SELECT s FROM Subscriber s WHERE s.user.id = :userId AND s.topic.id = :topicId"
    );
    subscriberQuery.setParameter("userId", entity.getUser().getId());
    subscriberQuery.setParameter("topicId", topics.get(0).getId());
    List<Subscriber> subscribers = subscriberQuery.getResultList();

    if (!subscribers.isEmpty()) {
      // El usuario ya está suscrito, retornar respuesta con el tema
      response.result = Subscription_check.Result.OKAY;
      response.topic = topics.get(0);  // Devolver el tema al que está suscrito
      return response;
    }

    // Si no está suscrito, crear una nueva suscripción
    Subscriber newSubscriber = new Subscriber();
    newSubscriber.setUser(entity.getUser());  // Asociar el usuario
    newSubscriber.setTopic(topics.get(0));   // Asociar el tema
    em.persist(newSubscriber);               // Persistir la nueva suscripción

    // Devolver respuesta de suscripción exitosa
    response.result = Subscription_check.Result.OKAY;
    response.topic = topics.get(0);  // Retornar el tema al cual se suscribió
    return response;
    
  }

  @POST
  @Path("delete")
  @Consumes({"application/xml", "application/json"})
  public Subscription_check check_to_delete(Subscriber entity) {
    // Crear el objeto de respuesta
    Subscription_check response = new Subscription_check();

    // Buscar el tema en la base de datos por nombre
    Query topicQuery = em.createQuery("SELECT t FROM Topic t WHERE t.name = :topicName");
    topicQuery.setParameter("topicName", entity.getTopic().getName());
    List<Topic> topics = topicQuery.getResultList();

    // Si el tema no existe, devolver error
    if (topics.isEmpty()) {
        response.result = Subscription_check.Result.NO_TOPIC;
        response.topic = null;
        return response;
    }

    // Buscar la suscripción del usuario al tema
    Query subscriberQuery = em.createQuery(
        "SELECT s FROM Subscriber s WHERE s.user.id = :userId AND s.topic.id = :topicId"
    );
    subscriberQuery.setParameter("userId", entity.getUser().getId());
    subscriberQuery.setParameter("topicId", topics.get(0).getId());
    List<Subscriber> subscribers = subscriberQuery.getResultList();

    // Si no hay suscripciones, devolver error
    if (subscribers.isEmpty()) {
        response.result = Subscription_check.Result.NO_SUBSCRIPTION;
        response.topic = topics.get(0);
        return response;
    }

    // Eliminar la suscripción
    try {
        Subscriber subscriber = subscribers.get(0);
        em.remove(em.merge(subscriber)); // Asegurarse de que la entidad esté gestionada antes de eliminarla

        // Confirmar la eliminación
        response.result = Subscription_check.Result.OKAY;
        response.topic = topics.get(0);
    } catch (Exception e) {
        // Manejar errores durante la eliminación
       
        response.topic = topics.get(0);
        e.printStackTrace(); // Opcional: registrar el error para depuración
    }

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
