package ca.aco.FServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class RecieveThread implements Runnable {
	public static Logger logger = Logger.getLogger(RecieveThread.class.getName());

	private Socket socket;
	public RecieveThread(Socket s){
		this.socket=s;
	}
	//timeout -- 
	@Override
	public void run() {
		// TODO Auto-generated method stub
		JSONObject r = this.read(socket);
		if(r != null) {
			logger.info(String.valueOf(r));
		
			sendOk();
		} else if(!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private JSONObject read(Socket socket) {
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
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Trouble with reading Json", e);
		}
		return rsp;
	}

	private void sendOk() {
		sendMsg("Ok");
	}
	
	private void sendError() {
		sendMsg("Error");
	}
	
	private void sendMsg(String message) {
		byte[] msg = message.getBytes();
		try {
			socket.getOutputStream().write(msg);
			socket.getOutputStream().flush();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't write error", e);
		}

		try {
			socket.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't close socket", e);
		}
	}

}
