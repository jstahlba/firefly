package ca.aco.FServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatchingService {
	private static final int MATCH_SIZE = 3;


	public static Logger logger = Logger.getLogger(MatchingService.class.getName());


	private Map<String, PlayerSocket> playerSocket = new ConcurrentHashMap<String, PlayerSocket>();
	private Map<String, Player> player = new ConcurrentHashMap<String, Player>();


	private static MatchingService instance;	
	public static MatchingService getInstance() {
		if(instance == null)
			instance = new MatchingService();
		return instance;
	}

	public void playerJoin(String id, PlayerSocket socket) {
		boolean isError = false;

		PlayerSocket existing = playerSocket.get(id);
		if(existing != null && !existing.isShutdown()) {
			logger.severe("Player Already Connected");
			isError = true;
		}
		playerSocket.put(id, socket);


		if(isError)
			socket.sendError();

		PlayerService.getInstance().getPlayer(id, new PlayerCallback() {
			@Override
			public void onSuccess(Player p) {
				addPlayer(p);
			}

			@Override
			public void onError(Exception e) {
				logger.log(Level.WARNING, "Trouble getting player!", e);
			}
		});
	}

	private void addPlayer(Player p) {
		synchronized(player) {
			List<Player> match =  checkMatch(p);
			if(match != null && match.size() >= 3) {
				logger.info("Dectect Match: " + p.getId());
				logger.info(String.valueOf(player));
				match = match.subList(0, 3);
				match.add(p);
				sendMatch(match);
			} else {
				logger.info("Queue player:" + p.getId());
				player.put(p.getId(), p);
			}
		}
	}

	private boolean sendMatch(List<Player> match) {
		List<PlayerSocket> matchSocket = new ArrayList<PlayerSocket>();
		boolean isError = false;

		for(Player p: match) {
			player.remove(p.getId());

			PlayerSocket socket = playerSocket.get(p.getId());
			if(socket == null || socket.isShutdown()) {
				isError = true;
			}


			playerSocket.remove(p.getId());
			matchSocket.add(socket);

		}

		if(isError) {
			//putback

			//toDo
			return false;
		}

		for(PlayerSocket p : matchSocket) {
			p.sendMatchAndShutdown(match);
		}


		return !isError;
	}

	private List<Player> checkMatch(final Player p) {
		List<Player> tempList = new ArrayList<Player>(player.values());
		tempList.remove(p);

		Collections.sort(tempList, new Comparator<Player>() {
			@Override
			public int compare(Player pp1, Player pp2) {
				double ans1 = computeMatch(p,pp1);
				double ans2 = computeMatch(p,pp2);
				return Double.compare(ans2, ans1);
			}

		});
		return tempList;
	}

	private double computeMatch(Player p1, Player p2) {
		double beta2 = 16;
		double c2 = 2*beta2 + p1.getO()*p1.getO() + p2.getO()*p2.getO();

		double d = (2*beta2)/c2;
		double exp = ((p1.getU()-p2.getU())*(p1.getU()-p2.getU()))/(2*c2);
		return Math.pow(Math.E, -1*exp)* Math.sqrt(d);	
	}
}
