package ca.aco.FServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FServer {
	public static Logger logger = Logger.getLogger(FServer.class.getName());
	private static boolean shutdown;
	private static ServerSocket serverSocket;
	private static ExecutorService executor;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		executor = Executors.newFixedThreadPool(5);

		//Start up sequence
		// Connect to monthership
		// DL member list
		// Start control port listener system
		// Start client listening system
		if(!bootstrapClientSocket()) {
			shutdown();
			return;
		}

		//Run loop
		while(!shutdown) {
			; //Run threads until shutdown;'
			try {
				try {
					Socket socket = serverSocket.accept();
					Runnable worker = new RecieveThread(socket);
					executor.execute(worker);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Can't accept socket", e);
					e.printStackTrace();
				}
				Thread.sleep(500);
			} catch (InterruptedException e) {

			}
		}


		//Shutdown

	}
	/**
	 * 
	 * @return success - Process was started successfully
	 */
	private static boolean bootstrapClientSocket() {
		// TODO Auto-generated method stub
		try {
			serverSocket = new ServerSocket(6116);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Can't bind socket", e);
			return false;
		}

		return true;
	}



	private static void shutdown() {
		executor.shutdown();
	}
}
