package controllers;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static int nextMemberIndex = 0;
    private static Map<Integer, WebSocket.Out<JsonNode>> members = new HashMap<Integer, WebSocket.Out<JsonNode>>();
	
	// Create a WebSocket on startup
	public static WebSocket<JsonNode> index(){
		return new WebSocket<JsonNode>(){
			public void onReady(WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out){
				// Send a single 'Hello!' message
				writeMessage(out, "Hello!");
				members.put(nextMemberIndex++, out);

				// For each event received on the socket,
				in.onMessage(new Callback<JsonNode>() {
					public void invoke(JsonNode event) {				       

						System.out.println("json node: " + event);
						if(event.equals("upvote")) {
							currentScore++;
							
							System.out.println("Upvote!"); 
							writeMessage(out, "Yeahh!!");
						}
						if(event.equals("downvote")) {
							currentScore--;
							System.out.println("Downvote!"); 
							writeMessage(out, "Boohoooo!!");
						}

						if(event.equals("hi")) {
							writeMessage(out, "hey man!");
						}

						writeMessage(out, "Huidige score: " + currentScore);

					} 
				});

				// When the socket is closed.
				in.onClose(new Callback0() {
					public void invoke() {				         
						System.out.println("WebSocket Disconnected");				         
					}
				});

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
    
    private static void writeMessage(WebSocket.Out<JsonNode> socket, String message) {
    	socket.write(Json.toJson(message));
    }
   
}
