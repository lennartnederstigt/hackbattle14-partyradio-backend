package controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import akka.japi.Function;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongCatalog;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.*;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import play.mvc.*;
import scala.Function0;
import views.html.*;

public class Application extends Controller {
	
	private static Repository repo = new Repository();
	private static String apiKey = Play.application().configuration().getString("echonest.apikey");
	private static EchoNestAPI echoNest = new EchoNestAPI(apiKey);
   
    private static int currentScore = 0;
    private static int nextMemberIndex = 0;
    private static Map<Integer, WebSocket.Out<JsonNode>> members = new HashMap<Integer, WebSocket.Out<JsonNode>>();
	private static Iterator<JsonNode> iterator;
	
	// Create a WebSocket on startup
	public static WebSocket<JsonNode> index(){
		return new WebSocket<JsonNode>(){
			public void onReady(WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out){
				// Send a single 'Hello!' message
				members.put(nextMemberIndex++, out);
				writeMessage(out, "Hello!");

				// For each event received on the socket,
				in.onMessage(new Callback<JsonNode>() {
					public void invoke(JsonNode event) {				       
						
						if(event.get("event").asText().equals("upvote")) {
							currentScore++;
							
							System.out.println("Upvote!"); 
							writeMessage(null, "Yeahh!!");
						}
						if(event.get("event").asText().equals("downvote")) {
							currentScore--;
							System.out.println("Downvote!"); 
							writeMessage(null, "Boohoooo!!");
						}

						if(event.get("event").asText().equals("hi")) {
							writeMessage(null, "hey man!");
						}

						writeMessage(null, "Huidige score: " + currentScore);

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
	
	public static Result searchSong(String searchString) throws Exception {
		Params p = new Params();
        p.add("title", searchString);
        p.add("results", 1);
        p.add("bucket", "id:deezer");
        p.add("bucket", "tracks");
        List<Song> songs = echoNest.searchSongs(p);
//        
//        Song song = songs.get(0);
//		Map<String, Object> result = new HashMap<>();
//        result.put("name", song.getArtistName());
//        result.put("id", song.getID());
//        result.put("image", song.getCoverArt());
        
//        String url = "http://developer.echonest.com/api/v4/song/search?api_key="+ apiKey +"&format=json&results=1&title=" + URLEncoder.encode(searchString) + "&bucket=id:deezer&bucket=tracks";
//        URL obj = new URL(url);
//		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//		BufferedReader in = new BufferedReader(
//		        new InputStreamReader(con.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
// 
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		in.close();
//		
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode rootNode = mapper.readTree(response.toString());
//		
//		Map<String, Object> result = new HashMap<>();
//        result.put("name", rootNode.findValue("artist_name").asText());
//        result.put("id", rootNode.findValue("id").asText());
		        
        Song song = songs.get(0);
        Map<String, Object> result = new HashMap<>();
//        System.out.println(song.toString());
        result.put("name", song.getArtistName());
        result.put("id", song.getID());
        result.put("image", song.getCoverArt());
		return ok(Json.toJson(result));
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
    	if (socket != null) {
    		socket.write(Json.toJson(message));
    	} else {
    		for (WebSocket.Out<JsonNode> out : members.values()) {
    			out.write(Json.toJson(message));    		
    		}    		
    	}
    }
   
}
