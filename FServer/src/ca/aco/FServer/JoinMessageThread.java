package ca.aco.FServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JoinMessageThread implements Runnable {
	public static Logger logger = Logger.getLogger(JoinMessageThread.class.getName());

	private PlayerSocket socket;

	private boolean isShutdown;

	private ScheduledExecutorService timeout;
	public JoinMessageThread(Socket s){
		this.socket=new PlayerSocket(s);
		isShutdown = false;

		// When your program starts up
		timeout = Executors.newSingleThreadScheduledExecutor();

		// then, when you want to schedule a task
		Runnable task = new Runnable() {
			@Override
			public void run() {
				logger.info("timeout");
				socket.shutdown();
				isShutdown = true;
			}
		};  
		timeout.schedule(task, 60, TimeUnit.SECONDS);
	}
	//timeout -- 
	@Override
	public void run() {
		if(isShutdown)
			return;

		logger.info("Start thread");

		
		JSONObject joinMessage = socket.readMsg();
		if(joinMessage != null) {
			logger.info(String.valueOf(joinMessage));
		} else {
			if(!isShutdown) {
				socket.sendError();
				isShutdown = true;
			}
		}

		if(!isShutdown) {
			if(checkJoin(joinMessage)) {
				socket.sendAck();
				
				String id = (String) joinMessage.get("uid");
				MatchingService.getInstance().playerJoin(id, socket);
			} else {
				socket.sendError();
				
				logger.warning("Bad message detected: " + String.valueOf(joinMessage));
			}
		}

		timeout.shutdown();
		logger.info("End thread");
	}

	private boolean checkJoin(JSONObject joinMessage) {
		if("join".equals(joinMessage.get("a")) && joinMessage.containsKey("uid") && (joinMessage.get("uid") instanceof String))
			return true;
		return false;
	}
}
