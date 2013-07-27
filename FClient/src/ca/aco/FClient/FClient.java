package ca.aco.FClient;
public class FClient {
	public static void main(String args[]) {

		
		
		for(int i = 1; i < 15; i++) {
			TestClient c = new TestClient(""+i);
			c.start();
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				break;
			}
		}
		
	}
}
