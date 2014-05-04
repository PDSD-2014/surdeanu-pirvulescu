package ro.pirvulescusurdeanu.septica.activities;

import java.util.ArrayList;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DeviceActivity extends Activity implements OnItemClickListener {
	private ArrayList<BluetoothDevice> pairedDevices;
	private ArrayList<String> namedDevices;
	private ArrayAdapter<String> arrayAdapter;
	private int currrentSelection = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		
        pairedDevices = new ArrayList<BluetoothDevice>();
        namedDevices = new ArrayList<String>();
        namedDevices.add("No devices");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, namedDevices);
        ListView view = (ListView)findViewById(R.id.deviceList);
        view.setAdapter(arrayAdapter);
        view.setOnItemClickListener(this);
		
		BluetoothDevice[] devices = BluetoothController.getInstance().getPairedDevices();
		if (devices.length > 0) {
			pairedDevices.clear();
			arrayAdapter.clear();
		}
			
		for(BluetoothDevice device : devices) {
			pairedDevices.add(device);
			arrayAdapter.add(device.getName());
		}
	}

	/**
	 * Ce se intampla la distrugerea activitatii?
	 * Se va salva dispozitivul ales, sau daca nu este niciunul atunci nu se va
	 * face nimic.
	 */
	@Override
    protected void onDestroy() {
		super.onDestroy();
		
    	// Salveaza in controller-ul atasat Bluetooth-ului dispozitivul la care
    	// vom incerca sa ne conectam. Cu siguranta acesta se afla in lista
    	// noastra interna.
    	if (currrentSelection > -1) {
    		BluetoothController.getInstance()
    						   .setBluetoothDevice(pairedDevices.get(currrentSelection));
    	}
    }

	/**
	 * Ascultatorul care este pus pe lista noastra.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO: Verifica daca lista are doar elementul "No devices"
		currrentSelection = position;
		
		// Salveaza in controller-ul atasat Bluetooth-ului dispozitivul la care
    	// vom incerca sa ne conectam. Cu siguranta acesta se afla in lista
    	// noastra interna.
    	if (currrentSelection > -1) {
    		BluetoothController.getInstance()
    						   .setBluetoothDevice(pairedDevices.get(currrentSelection));
    	}
    	
		finish();
	}
}
