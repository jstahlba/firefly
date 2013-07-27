package ca.aco.FServer;

public interface PlayerCallback {
	public void onSuccess(Player p);
	public void onError(Exception e);
}
