package ro.pirvulescusurdeanu.septica.activities;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.threads.GameThread;
import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		GameThread startGame = new GameThread(BluetoothController.getInstance().isServer());
		startGame.start();
	}
}
