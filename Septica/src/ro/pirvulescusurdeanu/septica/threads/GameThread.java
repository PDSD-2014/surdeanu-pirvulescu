package ro.pirvulescusurdeanu.septica.threads;

public class GameThread extends Thread {
	private final boolean isServer;
	
	public GameThread(boolean isServer) {
		this.isServer = isServer;
	}
	
	@Override
	public void run() {
		while(true) {
			// TODO: Implementare joc
			// Serverul va executa urmatorul joc
			if (isServer) {
			}
		}
	}
}
