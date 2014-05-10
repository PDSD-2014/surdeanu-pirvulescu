package ro.pirvulescusurdeanu.septica.notifiers;

import java.util.ArrayList;

public interface MainNotifier {
	ArrayList<MainListener> listeners = new ArrayList<MainListener>();
	
	public void addMainListener(MainListener listener);
	public void notifyStartGame();
}
