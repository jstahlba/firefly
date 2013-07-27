package ca.aco.FClient;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

public class TestClient {
	private String id;
	public static Logger logger = Logger.getLogger(TestClient.class.getName());

	public TestClient(String uid) {
		this.id = uid;
	}

	public void start() {
		// TODO Auto-generated method stub
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1",6116);
		} catch (UnknownHostException e) {
			logger.log(Level.WARNING,"Can't connect", e);
		} catch (IOException e) {
			logger.log(Level.WARNING,"Can't io", e);
			return;
		}
		if(socket == null)
			return;

		final PlayerSocket pSocket = new PlayerSocket(socket);

		JSONObject msg = new JSONObject();
		msg.put("a", "join");
		msg.put("uid", this.id);

		pSocket.sendMsg(String.valueOf(msg));
		
if((Integer.parseInt(id) % 3) == 0) {
	pSocket.shutdown();
return;
	}
		
		//Start reading
		ScheduledExecutorService readThread = Executors.newSingleThreadScheduledExecutor();

		// then, when you want to schedule a task
		Runnable task = new Runnable() {
			@Override
			public void run() {
				while(true) {
					JSONObject obj = pSocket.readMsg();
					if(obj.containsKey("a")) {
						if("ACK".equals(obj.get("a"))) {
							logger.info("ACK");
						}
						if("match".equals(obj.get("a"))) {
							logger.info("match: " + id + "|" + String.valueOf(obj));
							pSocket.shutdown();
							break;
						}
					}
					logger.info(String.valueOf(obj));
					if(obj == null)
						break;
				}
			}
		};  
		readThread.submit(task);
	}
}
