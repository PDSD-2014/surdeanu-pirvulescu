package ro.pirvulescusurdeanu.septica.activities;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.notifiers.SplashListener;
import ro.pirvulescusurdeanu.septica.threads.SplashThread;
import ro.pirvulescusurdeanu.septica.utils.Status;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AbstractActivity implements SplashListener {

	public SplashActivity() {
		super(R.layout.activity_splash);
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// Verificarile necesare in timpul incarcarii aplicatiei se vor face
		// intr-un nou fir de executie. Imediat dupa pornirea firului de
		// executie, firul principal va trebui sa astepte pana la terminarea
		// celuilalt.
		SplashThread thread = new SplashThread();
		// Ma inregistrez ca si ascultator pentru a primi notificari de la
		// celalalt fir de executie pe care il voi lansa.
		thread.addSplashListener(this);
		// Realizez pornirea firului de executie
		thread.start();
	}

	@Override
	public void finished(Status status) {
		// Ce se intampla daca verificarile au trecut cu succes?
		if (status == Status.OK) {
			// Dupa executia tuturor verificarilor se va lansa o noua activitate,
			// iar cea curenta va fi terminata.
			Intent intent = new Intent(this, MainActivity.class);
			this.startActivity(intent);
			this.finish();
		}
		// Ce se intampla in cazul in care apare o eroare?
		else {
			// TODO
		}
	}

}
