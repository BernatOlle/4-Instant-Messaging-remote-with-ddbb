package apiREST;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Publisher;
import entity.User;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.List;

public class apiREST_Publisher {
  public static void createPublisher(Publisher publisher) {
    try {
      URL url = new URL(Cons.SERVER_REST+"/entity.publisher/create");
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

      ucon.setRequestMethod("POST");
      ucon.setDoInput(true);
      ucon.setDoOutput(true);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");

      PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
      String json = new Gson().toJson(publisher);
      System.out.println("Json "+json);
      out.println(json);
      out.flush();
      ucon.connect();

      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        System.out.println(line);
      }
      

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static boolean deletePublisher(Publisher publisher) {
    try {
      URL url = new URL(Cons.SERVER_REST+"/entity.publisher/delete");
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

      ucon.setRequestMethod("POST");
      ucon.setDoInput(true);
      ucon.setDoOutput(true);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");
      
      PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
      String json = new Gson().toJson(publisher);
      System.out.println(json);
      out.println(json);
      out.flush();
      ucon.connect();

      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        System.out.println(line);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
public static List<Publisher> PublishersOf(User user) {
    try {
      URL url = new URL(Cons.SERVER_REST+"/entity.publisher/publisherOf");
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

      ucon.setRequestMethod("POST");
      ucon.setDoInput(true);
      ucon.setDoOutput(true);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");

      PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
      Gson gson = new Gson();
      String json = gson.toJson(user);
      System.out.println(json);
      out.println(json);
      out.flush();
      ucon.connect();

      // Read the response
      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      Type listType = new TypeToken<List<Publisher>>() {}.getType();
      List<Publisher> publishers = gson.fromJson(in, listType);

      return publishers;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
