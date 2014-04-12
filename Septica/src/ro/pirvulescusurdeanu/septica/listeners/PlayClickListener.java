package ro.pirvulescusurdeanu.septica.listeners;

import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.intents.BluetoothIntent;
import ro.pirvulescusurdeanu.septica.utils.BluetoothStatus;
import android.bluetooth.BluetoothAdapter;
import android.view.View;
import android.view.View.OnClickListener;

public class PlayClickListener implements OnClickListener {

	@Override
	public void onClick(View v) {
		// Daca modulul Bluetooth nu este activat, vom incerca sa il activam
		if (BluetoothController.getInstance()
							   .getAdapterStatus() == BluetoothStatus.DISABLED) {
			BluetoothIntent intent = new BluetoothIntent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			intent.startActivityForResult();
		}
		
		// Alegem dispozitivul cu care dorim sa ne conectam
		
	}

}
