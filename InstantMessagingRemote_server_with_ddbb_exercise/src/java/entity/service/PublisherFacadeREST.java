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
        // Check if the topic exists in the database
        Query topicQuery = em.createQuery("SELECT t FROM Topic t WHERE t.name = :topicName");
        topicQuery.setParameter("topicName", entity.getTopic().getName());
        List<Topic> topics = topicQuery.getResultList();

        Topic topic;
        if (topics.isEmpty()) {
            // Create a new topic if it doesn't exist
            topic = new Topic();
            topic.setName(entity.getTopic().getName());
            em.persist(topic);
            em.flush(); // Synchronize to get the generated ID
        } else {
            // Use the existing topic
            topic = topics.get(0);
        }

        // Assign the topic to the Publisher entity
        entity.setTopic(topic);

        // Check if the user is already a publisher
        Query publisherQuery = em.createQuery(
                "SELECT p FROM Publisher p WHERE p.user.id = :userId"
        );
        publisherQuery.setParameter("userId", entity.getUser().getId());
        List<Publisher> publishers = publisherQuery.getResultList();

        if (!publishers.isEmpty()) {
            // Update the existing publisher's topic
            Publisher existingPublisher = publishers.get(0);
            existingPublisher.setTopic(topic);
            em.merge(existingPublisher);
        } else {
            // Create a new publisher
            em.persist(entity);
        }

        em.flush();

    }

    @POST
    @Path("delete")
    @Consumes({"application/xml", "application/json"})
    public void delete(Publisher entity) {
        // Check if the user is a publisher
        if (entity.getTopic() != null) {
            Query publisherQuery = em.createQuery(
                    "SELECT p FROM Publisher p WHERE p.user.id = :userId AND p.topic.id = :topicId"
            );
            publisherQuery.setParameter("userId", entity.getUser().getId());
            publisherQuery.setParameter("topicId", entity.getTopic().getId());
            List<Publisher> publishers = publisherQuery.getResultList();

            if (publishers.isEmpty()) {
                throw new RuntimeException("El usuario no es un publicador de este tema.");
            }

            // Delete the publisher
            Publisher publisher = publishers.get(0);
            em.remove(em.merge(publisher));
            em.flush();

            // Check if the topic has any publishers left
            Query remainingPublishersQuery = em.createQuery(
                    "SELECT p FROM Publisher p WHERE p.topic.id = :topicId"
            );
            remainingPublishersQuery.setParameter("topicId", entity.getTopic().getId());
            List<Publisher> remainingPublishers = remainingPublishersQuery.getResultList();

            if (remainingPublishers.isEmpty()) {
                // Delete the topic if no publishers are left
                Topic topic = em.find(Topic.class, entity.getTopic().getId());
                em.remove(topic);
                em.flush();

                // Notify clients about the topic being closed
                Subscription_close closeNotification = new Subscription_close(topic, Subscription_close.Cause.PUBLISHER);
                WebSocketServer.notifyTopicClose(closeNotification);
            }
        }

    }

    @POST
    @Path("publisherOf")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public Publisher publisherOf(User entity) {
        // Retrieve the publisher for the user
        Query publisherQuery = em.createQuery(
                "SELECT p FROM Publisher p WHERE p.user.id = :userId"
        );
        publisherQuery.setParameter("userId", entity.getId());
        List<Publisher> publishers = publisherQuery.getResultList();

        if (publishers.isEmpty()) {
            return null;
        }

        return publishers.get(0);

    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
