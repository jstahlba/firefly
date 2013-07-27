package ca.aco.FServer;

import org.json.simple.JSONObject;

import com.mongodb.DBObject;

public class Player {
	private String id = null;
	private Double u;
	private Double o;
	
	
	Player() {
		
	}
	Player(String id) {
		this.id = id;
	}
	
	public Player(DBObject obj) {
		// TODO Auto-generated constructor stub
		id = (String) obj.get("_id");
		u = (Double) obj.get("u");
		o = (Double) obj.get("o");
	}
	public Player(String _id, double _u, double _o) {
		id = _id;
		u = _u;
		o = _o;
	}
	public String getId() {
		return id;
	}
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		return obj;
	}
	
	public double getU() {
		return u;
	}
	public double getO() {
		return o;
	}
	
	public String toString() {
		return this.id + "|" + this.u + "|" + this.o;
	}
}
