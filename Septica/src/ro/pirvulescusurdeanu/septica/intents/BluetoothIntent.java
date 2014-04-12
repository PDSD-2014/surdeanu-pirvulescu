package ro.pirvulescusurdeanu.septica.intents;

import ro.pirvulescusurdeanu.septica.utils.Constants;

public class BluetoothIntent extends AbstractIntent {

	public BluetoothIntent(String action) {
		super(action);
	}
	
	public void startActivityForResult() {
		super.startActivityForResult(Constants.BT_REQUESTED_CODE);
	}
}
