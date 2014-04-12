package ro.pirvulescusurdeanu.septica.activities;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.listeners.PlayClickListener;
import ro.pirvulescusurdeanu.septica.utils.Constants;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AbstractActivity {

	public MainActivity() {
		super(R.layout.activity_main);
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// Se inregistreaza pe butonul de Play un ascultator
		Button play = (Button) findViewById(R.id.play);
		play.setOnClickListener(new PlayClickListener());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		switch (requestCode) {
			case Constants.BT_REQUESTED_CODE: 
				synchronized(this) {
					this.notify();
				}
				break;
		}
    }

}
