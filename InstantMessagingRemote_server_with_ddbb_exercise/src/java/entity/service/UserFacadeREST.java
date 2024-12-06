/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

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
import util.Login_check;

/**
 *
 * @author upcnet
 */
@Stateless
@Path("entity.user")
public class UserFacadeREST extends AbstractFacade<User> {
  @PersistenceContext(unitName = "PubSubWebServerPU")
  private EntityManager em;

  public UserFacadeREST() {
    super(User.class);
  }
  
  @POST
  @Path("create")
  @Consumes({"application/xml", "application/json"})
  @Produces({"application/xml", "application/json"})
  public User create_and_return(User entity) {
    
    // check out if the user with that login is defined at the User table,
    // if that is the case, return that instance, otherwise save that new 
    // user at the User table:
    
    // ...
    throw new RuntimeException("To be completed by the student");
    
  }
  
  @POST
  @Path("login")
  @Produces({"application/xml", "application/json"})
  @Consumes({"application/xml", "application/json"})
  public User login(Login_check login) {
    System.out.println("login: "+login.login+", password: "+login.password);
    
    // check out if a user with that login and password is defined at the User table,
    // return that user or null, accordingly:
    // ...
    throw new RuntimeException("To be completed by the student");
    
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
  
}
