package controllers;

import java.net.UnknownHostException;

import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	private static Repository repo = new Repository();
	
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result hello(String id) {
    	return ok("Hello " + id + "!");
    }
    
    public static Result testConnection() throws UnknownHostException {
    	return ok("MongoDB result: " + repo.testConnection());
    }
   
}
