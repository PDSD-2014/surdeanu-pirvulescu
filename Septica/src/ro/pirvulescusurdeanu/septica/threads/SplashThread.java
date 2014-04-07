package ro.pirvulescusurdeanu.septica.threads;

import java.lang.reflect.Method;

import ro.pirvulescusurdeanu.septica.notifiers.SplashListener;
import ro.pirvulescusurdeanu.septica.notifiers.SplashNotifier;
import ro.pirvulescusurdeanu.utils.Status;

public class SplashThread extends Thread implements SplashNotifier {
	
	@Override
	public void run() {
		// TODO: Realizeaza verificarile necesare
		// WORKAROUND: Simuleaza o asteptare de 3 secunde
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Semnalez faptul ca initializarea aplicatiei s-a realizat cu succes
		notifyUpdate("finished", Status.OK);
	}

	/**
	 * Un nou ascultator este inregistrat pentru a putea fi notificat atunci
	 * cand firul de executie de fata doreste sa realizeze o actiune, o posibila
	 * actualizare in view-ul principal.
	 */
	@Override
	public void addSplashListener(SplashListener listener) {
		listeners.add(listener);
	}

	/**
	 * Notifica ascultatorii atunci cand un eveniment se produce.
	 */
	@Override
	public void notifyUpdate(String method, Object... arguments) {
		try {
			// Cauta metoda care se doreste a fi apelata in cadrul ascultatorului
			Method[] methods = SplashListener.class.getMethods();
			Method callback = null;
			for (Method m: methods) {
				if (m.getName().equals(method)) {
					callback = m;
					break;
				}
			}
			
			// Daca metoda nu este gasita se va jurnaliza un warning, ce va
			// aparea in cadrul ferestrei LogCat.
			if (callback == null) {
				// TODO: Jurnalizare warning
				// Se evita exceptiile de pe NullPointerException
				return;
			}
			
			// Daca metoda a fost gasita atunci, folosind reflection o vom invoca
			// cu parametrii primiti ca si argument.			
			for (SplashListener listener: listeners) {
				callback.invoke(listener, arguments);
			}
		} catch (Exception e) {
			// Singurele exceptii ce pot aparea vor fi cele generate de apelul
			// functiei "invoke" al unei metode. Asa ca mare atentie la
			// parametrii trimisi functiei :)
			// TODO: Jurnalizare warning
		}
	}
	
}
