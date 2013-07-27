package ca.aco.FServer;

import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class PlayerService {
	public static Logger logger = Logger.getLogger(PlayerService.class.getName());
	private static PlayerService instance;	
	
	public static PlayerService getInstance() {
		if(instance == null)
			instance = new PlayerService();
		return instance;
	}

	private MongoClient mongoClient;

	PlayerService() {
		try {
			mongoClient = new MongoClient( "localhost" );
		} catch (UnknownHostException e) {
			logger.severe("Couldn't start db connection");
		}
	}
	
	public void getPlayer(String id, PlayerCallback playerCallback) {
		
		
		
		DB db = mongoClient.getDB("firefly");
		DBCollection coll = db.getCollection("user");
		BasicDBObject query = new BasicDBObject("_id", id);

		DBCursor cursor = coll.find(query);
		if(cursor.hasNext()) {
			playerCallback.onSuccess(new Player(cursor.next()));
		} else {
			logger.warning("Can't find player: " + id);
			playerCallback.onError(null); //TODO add exception;
		}
	}

}
