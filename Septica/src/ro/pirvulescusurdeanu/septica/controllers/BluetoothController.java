package ro.pirvulescusurdeanu.septica.controllers;

import ro.pirvulescusurdeanu.septica.utils.BluetoothStatus;
import android.bluetooth.BluetoothAdapter;

/**
 * TODO
 * 
 * @author 	Mihu
 * @since	1.0
 */
public class BluetoothController {
	private static BluetoothController instance;
	private final BluetoothAdapter adapter;
	
	private BluetoothController() {
		// "adapter" va fi NULL daca dispozitivul utilizat nu are Bluetooth
		adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public BluetoothController getInstance() {
		if (instance == null) {
			instance = new BluetoothController();
		}
		return instance;
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
}
