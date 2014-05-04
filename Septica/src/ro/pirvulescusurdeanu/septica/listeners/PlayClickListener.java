package ro.pirvulescusurdeanu.septica.listeners;

import ro.pirvulescusurdeanu.septica.activities.DeviceActivity;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.controllers.MainController;
import ro.pirvulescusurdeanu.septica.intents.BluetoothIntent;
import ro.pirvulescusurdeanu.septica.utils.BluetoothStatus;
import ro.pirvulescusurdeanu.septica.utils.Constants;
import ro.pirvulescusurdeanu.septica.utils.TagName;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
		
		BluetoothController.getInstance().createService();
		
		TagName tag = (TagName)v.getTag();
		switch (tag) {
			// Un dispozitiv va reprezenta serverul
			case SERVER_BUTTON:
				// Serverul va asculta in permanenta pe un port
				BluetoothController.getInstance().startServer();
				break;
			// Celalalt dispozitiv va reprezenta dispozitivul de tip client
			case CLIENT_BUTTON:
				// Clientul va trebui sa se conecteze la un dispozitiv de tip
				// Bluetooth. Ii afisam lista de dispozitive Blueotooth care au
				// fost gasite. Il lasam sa aleaga ceva...
				Activity activity = MainController.getInstance().getCurrentActivity();
				Intent enableIntent = new Intent(activity, DeviceActivity.class);
		        activity.startActivityForResult(enableIntent, Constants.DC_REQUESTED_CODE);
				break;
			default:
				// Aici ar trebui sa nu ajunga niciodata, dar cum clasa TagName
				// va contine mai multe tag-uri ne vom asigura ca nu sunt generate
				// exceptii in cazul in care unele cazuri nu sunt tratate.
			
		}
		
		// Alegem dispozitivul cu care dorim sa ne conectam
		
	}

}
