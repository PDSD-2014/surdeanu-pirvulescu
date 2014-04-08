package ro.pirvulescusurdeanu.septica.activities;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.listeners.PlayClickListener;
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

}
