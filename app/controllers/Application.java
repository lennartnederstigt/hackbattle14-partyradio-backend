package controllers;

import java.net.UnknownHostException;
import java.util.List;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.fasterxml.jackson.databind.JsonNode;

import play.*;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	private static Repository repo = new Repository();
	private static String apiKey = Play.application().configuration().getString("echonest.apikey");
	private static EchoNestAPI echoNest = new EchoNestAPI(apiKey);
   
    private static int currentScore = 0;
	
	// Create a WebSocket on startup
	public static WebSocket<String> index(){
		return new WebSocket<String>(){
			public void onReady(WebSocket.In<String> in, final WebSocket.Out<String> out){
				
				// For each event received on the socket,
				  in.onMessage(new Callback<String>() {
				     public void invoke(String event) {				       
				      
				       if(event.equals("upvote")) {
				    	   currentScore++;
				    	   System.out.println("Upvote!"); 
				    	   out.write("Yeahh!!");
				       }
				       if(event.equals("downvote")) {
				    	   currentScore--;
				    	   System.out.println("Downvote!"); 
				    	   out.write("Boohoooo!!");
				       }
				       
				       if(event.equals("hi")) {
				    	   out.write("hey man!");
				       }
				       
				       out.write("Huidige score: " + currentScore);
				       
				     } 
				  });
				  
				  // When the socket is closed.
				  in.onClose(new Callback0() {
				     public void invoke() {				         
				    	 System.out.println("WebSocket Disconnected");				         
				     }
				  });
				  
				  // Send a single 'Hello!' message
				  out.write("Hello!");
				  
			}
		};
	}
	
    public static Result hello(String id) {
    	return ok("Hello " + id + "!");
    }
	
	public static Result join() {
	  JsonNode json =  request().body().asJson();
	  System.out.println("JSON Object: " + json);
	  if(json == null) {
	    return badRequest("Expecting Json data");
	  } else {
		  return ok("Hello person");	    
	  }
	}
    
    public static Result testEchoNest() throws EchoNestException {
    	List<String> genres = echoNest.listGenres();
    	return ok(Json.toJson(genres));
    }
    
    public static Result testConnection() throws UnknownHostException {
    	return ok("MongoDB result: " + repo.testConnection());
    }
   
}
