package ro.pirvulescusurdeanu.septica.controllers;

import java.util.Set;

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
public class BluetoothController {
	private static BluetoothController instance;
	private final BluetoothAdapter adapter;
	private BluetoothService service;
	private BluetoothDevice device;
	
	private BluetoothController() {
		// "adapter" va fi NULL daca dispozitivul utilizat nu are Bluetooth
		adapter = BluetoothAdapter.getDefaultAdapter();
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
	}
	
	public void startClient() {
		service.connect(device);
		service.write("Test");
	}
	
	/**
	 * Care este statusul dispozitivului de tip Bluetooth asociat telefonului
	 * mobil de pe care ruleaza aplicatia?
	 * 
	 * @return
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
	 * 
	 * @return
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
}
