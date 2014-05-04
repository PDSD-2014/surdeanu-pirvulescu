package ro.pirvulescusurdeanu.septica.activities;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.listeners.PlayClickListener;
import ro.pirvulescusurdeanu.septica.utils.Constants;
import ro.pirvulescusurdeanu.septica.utils.TagName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AbstractActivity {
	private PlayClickListener listener;
	
	public MainActivity() {
		super(R.layout.activity_main);
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		listener = new PlayClickListener();
		
		// Se inregistreaza pe butonul de Play un ascultator
		Button play = (Button) findViewById(R.id.play);
		play.setTag(TagName.SERVER_BUTTON);
		play.setOnClickListener(listener);
		
		// Se inregistreaza un ascultator asupra butonului Client
		Button client = (Button) findViewById(R.id.client);
		client.setTag(TagName.CLIENT_BUTTON);
		client.setOnClickListener(listener);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case Constants.BT_REQUESTED_CODE: 
				synchronized (listener) {
					listener.notify();
				}
				break;
			case Constants.DC_REQUESTED_CODE:
				BluetoothController.getInstance().startClient();
				break;
		}
    }

}
