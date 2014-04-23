package controllers;

import java.net.UnknownHostException;
import java.util.Set;

import org.slf4j.Logger;

import play.Play;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Repository {

    private static final Logger log = play.Logger.of(Repository.class).underlying();
	
	private static final String mongoHost = Play.application().configuration().getString("mongo.host");
	private static final int mongoPort = Play.application().configuration().getInt("mongo.port");
	private static final String mongoDB = Play.application().configuration().getString("mongo.database");
	private static final String mongoUsername = Play.application().configuration().getString("mongo.username");
	private static final char[] mongoPassword = Play.application().configuration().getString("mongo.password").toCharArray();
	
	private DB db;
	
	public Repository() {
		try {
			MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
			db = mongoClient.getDB(mongoDB);
			boolean auth = db.authenticate(mongoUsername, mongoPassword);			
		} catch (UnknownHostException e) {
			log.error("Couldn't connect to MongoDB: " + e.getMessage());
		}
	}
	
	public DB getConnection() {
		return db;
	}

	public String testConnection() {
		DBCollection coll = db.getCollection("testCollection");
    	BasicDBObject doc = new BasicDBObject("name", "MongoDB").append("type", "database")
                 .append("count", 1)
                 .append("info", new BasicDBObject("x", 203).append("y", 102));
    	coll.insert(doc); 
		DBObject myDoc = coll.findOne();
		return myDoc.toString();
	}

}
