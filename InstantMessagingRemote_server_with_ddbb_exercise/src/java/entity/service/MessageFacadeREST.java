/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import entity.Message;
import entity.Topic;
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
@Path("entity.message")
public class MessageFacadeREST extends AbstractFacade<Message> {

    @PersistenceContext(unitName = "PubSubWebServerPU")
    private EntityManager em;

    public MessageFacadeREST() {
        super(Message.class);
    }

    @POST
    @Path("create")
    @Consumes({"application/xml", "application/json"})
    public void create(Message entity) {

        // check out if the topic of this message is defined:
        // ...
        // save the new message and use the WebSocketServer to forward that message
        // to the currently connected subscribers of the involved topic.
        // WARNING!!! do not use the same instance of Message to save and forward
        // the message, make a copy of the message and use both of them, one for each
        // action.
        // Verificar si el tema está definido
        // Verificar si el tema está definido
        System.out.print("AAAAAAA"+entity.getContent()+" "+entity.getId()+" "+entity.getTopic());
        Topic topic = entity.getTopic();
        if (topic == null || topic.getId() == null) {
            throw new IllegalArgumentException("El tema asociado al mensaje no está definido o no tiene un ID válido.");
        }
        

        // Verificar si el tema existe en la base de datos
        Query query = em.createQuery("SELECT t FROM Topic t WHERE t.id = :id");
        query.setParameter("id", topic.getId());
        List<Topic> result = query.getResultList();
        if (result.isEmpty()) {
            throw new IllegalArgumentException("El tema asociado al mensaje no existe en la base de datos.");
        }
        
        // Recuperar el tema persistente desde la base de datos
        Topic persistentTopic = result.get(0);

        // Crear una copia del mensaje para guardar en la base de datos
        Message messageToSave = new Message();
        
        messageToSave.setContent(entity.getContent());
        messageToSave.setTopic(persistentTopic);
        
        // Guardar el mensaje en la base de datos
        
        em.persist(messageToSave);
        
        // Crear una copia del mensaje para reenviar a los suscriptores
        Message messageToForward = new Message();
        messageToForward.setContent(entity.getContent());
        messageToForward.setTopic(persistentTopic);
        
        // Notificar a los suscriptores usando WebSocketServer
        WebSocketServer.notifyNewMessage(messageToForward);

    }

    @POST
    @Path("messagesFromTopic")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public List<Message> messagesFrom(Topic entity) {

        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("El tema proporcionado es inválido.");
        }

        // Consulta para obtener todos los mensajes del tema
        Query query = em.createQuery("SELECT m FROM Message m WHERE m.topic.id = :topicId");
        query.setParameter("topicId", entity.getId());

        @SuppressWarnings("unchecked")
        List<Message> messages = query.getResultList();

        return messages;

    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
