package ro.pirvulescusurdeanu.septica.intents;

import ro.pirvulescusurdeanu.septica.controllers.MainController;
import android.content.Intent;

public class AbstractIntent {
	protected Intent intent;
	
	public AbstractIntent(String action) {
		intent = new Intent(action);
	}
	
	protected void startActivityForResult(int requestCode) {
		MainController.getInstance()
					  .getCurrentActivity()
					  .startActivityForResult(intent, requestCode);
	}
}
