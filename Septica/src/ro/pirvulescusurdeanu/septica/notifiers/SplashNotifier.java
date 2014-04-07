package ro.pirvulescusurdeanu.septica.notifiers;

import java.util.ArrayList;

public interface SplashNotifier {
	ArrayList<SplashListener> listeners = new ArrayList<SplashListener>();
	
	public void addSplashListener(SplashListener listener);
	public void notifyUpdate(String method, Object... arguments);
}
