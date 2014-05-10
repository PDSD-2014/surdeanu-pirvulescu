package ro.pirvulescusurdeanu.septica.controllers;

import java.util.LinkedList;
import java.util.Set;

import ro.pirvulescusurdeanu.septica.notifiers.MainListener;
import ro.pirvulescusurdeanu.septica.notifiers.MainNotifier;
import ro.pirvulescusurdeanu.septica.services.BluetoothService;
import ro.pirvulescusurdeanu.septica.utils.BluetoothStatus;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * TODO
 * 
 * @author 	Mihu
 * @since	1.0
 */
public class BluetoothController implements MainNotifier {
	private static BluetoothController instance;
	private final BluetoothAdapter adapter;
	private BluetoothService service;
	private BluetoothDevice device;
	private boolean isServer = false;
	private final LinkedList<String> queue;
	
	private BluetoothController() {
		// "adapter" va fi NULL daca dispozitivul utilizat nu are Bluetooth
		adapter = BluetoothAdapter.getDefaultAdapter();
		queue = new LinkedList<String>();
	}
	
	public static BluetoothController getInstance() {
		if (instance == null) {
			instance = new BluetoothController();
		}
		return instance;
	}
	
	public void createService() {
		service = new BluetoothService();
	}
	
	public void startServer() {
		service.start();
		service.waitUntilConnected();
		isServer = true;
		notifyStartGame();
	}
	
	public void startClient() {
		service.connect(device);
		service.waitUntilConnected();
		notifyStartGame();
	}
	
	public boolean isServer() {
		return isServer;
	}
	
	/**
	 * Metoda utitilizata de firul de executie asociat comunicarii prin Bluetooth.
	 * Adauga in coada de mesaje un mesaj primit prin Bluetooth.
	 * 
	 * @param message
	 * 		Mesajul care a fost primit prin Bluetooth.
	 */
	public synchronized void addMessage(String message) {
		queue.addLast(message);
	}
	
	/**
	 * Metoda utilizata pentru a returna un mesaj din coada de mesaje primite.
	 * Este vorba despre cel mai vechi mesaj primit.
	 * 
	 * @return
	 * 		Mesajul din coada sau null in cazul in care lista de mesaje este goala.
	 */
	public synchronized String removeMessage() {
		if (!queue.isEmpty()) {
			return queue.removeFirst();
		}
		return null;
	}
	
	/**
	 * Metoda utilizata pentru a verifica daca exista un mesaj receptionat in
	 * coada de mesaje.
	 */
	public synchronized boolean hasMessage() {
		return !queue.isEmpty();
	}
	
	/**
	 * Care este statusul dispozitivului de tip Bluetooth asociat telefonului
	 * mobil de pe care ruleaza aplicatia?
	 */
	public BluetoothStatus getAdapterStatus() {
		if (adapter == null) {
			return BluetoothStatus.NOT_SUPPORTED;
		} else if (!adapter.isEnabled()) {
			return BluetoothStatus.DISABLED;
		} else {
			return BluetoothStatus.ENABLED;
		}
	}
	
	/**
	 * Intoarce lista de dispozitive cu care exista un pairing deja stabilit.
	 */
	public BluetoothDevice[] getPairedDevices() {
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		return devices.toArray(new BluetoothDevice[devices.size()]);
	}
	
	public BluetoothDevice getBluetoothDevice() {
		return device;
	}
	
	public void setBluetoothDevice(BluetoothDevice device) {
		this.device = device;
	}

	@Override
	public void addMainListener(MainListener listener) {
		listeners.add(listener);
		
	}

	@Override
	public void notifyStartGame() {
		for (MainListener listener : listeners) {
			listener.startGame(isServer);
		}
		
	}
}
