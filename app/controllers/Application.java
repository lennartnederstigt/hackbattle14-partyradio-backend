package controllers;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import play.Play;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;
import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller {
	
	private static Repository repo = new Repository();
	private static String apiKey = Play.application().configuration().getString("echonest.apikey");
	private static EchoNestAPI echoNest = new EchoNestAPI(apiKey);
   
    private static int currentScore = 0;
    private static int nextMemberIndex = 0;
    private static Map<Integer, WebSocket.Out<JsonNode>> members = new HashMap<Integer, WebSocket.Out<JsonNode>>();

    private static Map<String, Integer> trackVotes = new HashMap<>();
    private static int requestsSinceLastUpdate = 0;
    
    /**
     * Returns a response appropriate headers for allowing cross-origin resource sharing (CORS).
     * Needed for running frontend and backend on separate locations or ports.
     * 
     * @param wholepath	the path of the request
     * @return	response with appropriate headers for allowing cross-origin resource sharing (CORS)
     */
    public static Result preflight(String wholepath) {
    	response().setHeader("Access-Control-Allow-Origin", "*");       // Need to add the correct domain in here!!
        response().setHeader("Access-Control-Allow-Methods", "GET");   // Only allow GET
        response().setHeader("Access-Control-Max-Age", "300");          // Cache response for 5 minutes
        response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");         // Ensure this header is also allowed!
    	return ok();
    }
    
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
//						String trackID = event.get("track").asText();
						
						if(event.get("event").asText().equals("upvote")) {
//							if (trackVotes.containsKey(trackID)) {
//								Integer count = trackVotes.get(trackID);
//								trackVotes.put(trackID, count++);
//								requestsSinceLastUpdate++;
//							}
							currentScore++;
							
							System.out.println("Upvote!");
//							System.out.println("Upvoted " + trackID + "!"); 
							writeMessage(null, "Yeahh!!");
						}
						if(event.get("event").asText().equals("downvote")) {
//							if (trackVotes.containsKey(trackID)) {
//								Integer count = trackVotes.get(trackID);
//								trackVotes.put(trackID, count++);
//								requestsSinceLastUpdate++;
//							}
							currentScore--;
							
							System.out.println("Downvote!");
//							System.out.println("Downvoted " + trackID + "!"); 
							writeMessage(null, "Boohoooo!!");
						}

						if(event.get("event").asText().equals("hi")) {
							writeMessage(null, "hey man!");
						}
						
						if (requestsSinceLastUpdate >= 3) {
							writePlaylist();
							requestsSinceLastUpdate = 0;
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
	
	public static Result getPlaylist() {
		return ok(Json.toJson(trackVotes.keySet()));
	}
	
	public static Result searchSong(String id) throws Exception {
		if (trackVotes.containsKey(id)) {
			Integer count = trackVotes.get(id);
			trackVotes.put(id, ++count);
		} else {
			trackVotes.put(id, 1);
		}
		requestsSinceLastUpdate++;
		
		System.out.println("requests since last update: " + requestsSinceLastUpdate);
		
		if (requestsSinceLastUpdate >= 3) {
			writePlaylist();
//			writeMessage(null, "sending playlist");
			requestsSinceLastUpdate = 0;
		}
		
		System.out.println("Received request for track ID " + id);
		System.out.println(trackVotes);
		
		return ok("Request processed");
	}
	
	public static Result searchSongOld(String searchString) throws Exception {
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
        	System.out.println("Sending to 1 socket out of " + members.values().size());
    		socket.write(Json.toJson(message));
    	} else {
        	System.out.println("Sending to " + members.values().size() + " sockets out of " + members.values().size());
    		for (WebSocket.Out<JsonNode> out : members.values()) {
    			out.write(Json.toJson(message));    		
    		}    		
    	}
    }
    
    private static void writePlaylist() {
    	System.out.println("Sending to " + members.values().size() + " sockets out of " + members.values().size());
    	for (WebSocket.Out<JsonNode> out : members.values()) {
    		out.write(Json.toJson(trackVotes.keySet()));    		
    	}    		
    }
   
}
