package ca.aco.FServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PlayerSocket {
	public static Logger logger = Logger.getLogger(PlayerSocket.class.getName());

	private Socket socket;

	public PlayerSocket(Socket s) {
		socket = s;
	}

	public boolean sendAck() {
		return sendMsg("{\"a\":\"ack\"}");
	}

	public boolean sendError() {
		return sendMsg("{\"a\":\"error\"}");
	}

	public boolean sendMsg(String message) {
		byte[] msg = message.getBytes();
		try {
			socket.getOutputStream().write(msg);
			socket.getOutputStream().flush();
			return true;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't write error", e);
		}
		return false;
	}

	public void shutdown() {
		if(socket != null && !socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket = null;
		}
	}

	public JSONObject readMsg() {
		JSONObject rsp = null;
		StringBuilder sb = null;
		try {
			InputStream in = socket.getInputStream();

			int c = in.read();
			if(c != '{') {
				sendError();
				return null;
			}

			int bracketCount = 1;
			sb = new StringBuilder();
			sb.append((char)c);

			while(bracketCount > 0 && c > 0) {
				c = in.read();
				if(c == '{') {
					bracketCount ++;
				} else if(c == '}') {
					bracketCount--;
				}
				sb.append((char)c);
			}

			rsp =(JSONObject) JSONValue.parse(sb.toString());
		} catch(SocketException se) {
			logger.log(Level.SEVERE, "Socket close", se);
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Trouble with reading Json", e);
		}
		return rsp;
	}

	public boolean isShutdown() {
		return socket.isClosed();
	}

	public boolean sendMatchAndShutdown(List<Player> match) {
		JSONObject obj = new JSONObject();
		obj.put("a", "match");
		
		JSONArray players = new JSONArray();
		for(Player p : match) {
			players.add(p.toJson());
		}
		obj.put("match", players);
		
		boolean result = sendMsg(String.valueOf(obj));
		shutdown();
		return result;
	}
}
