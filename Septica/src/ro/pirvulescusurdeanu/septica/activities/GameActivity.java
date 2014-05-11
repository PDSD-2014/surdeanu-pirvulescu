package ro.pirvulescusurdeanu.septica.activities;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.cards.CardBase;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.threads.GameThread;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameActivity extends AbstractActivity implements OnClickListener {
	private GameThread startGame;
	private LinearLayout userHand, table;
	private GridLayout grid;
	private Button endTurn;
	private boolean canClick;
	
	public GameActivity() {
		super(R.layout.activity_game);
		canClick = false;
	}
	
	public void canClickOverImage() {
		canClick = true;
	}
	
	/**
	 * O tura poate fi oprita doar prin apasarea butonului "End Turn". Acest
	 * buton nu va fi intotdeauna activat.
	 */
	public void canStopTurn() {
		endTurn.setEnabled(true);
	}
	
	/**
	 * Imediat dupa crearea activitatii vom mai lansa un fir de executie, pe care
	 * se va realiza logica jocului.
	 */
	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// Instanta catre aceasta clasa va fi accesata in GameThread din cadrul
		// clasei singleton MainController.
		startGame = new GameThread(BluetoothController.getInstance().isServer());
		startGame.start();
		
		// Se initializeaza Layout-ul
		final Context context = getBaseContext();
		FrameLayout frame = (FrameLayout)findViewById(R.id.container);
		userHand = new LinearLayout(context);
		table = new LinearLayout(context);
		
		grid = new GridLayout(context);
		grid.setRowCount(4);
		grid.setColumnCount(1);
		
		endTurn = new Button(context);
		endTurn.setText("End Hand");
		endTurn.setEnabled(false);
		endTurn.setOnClickListener(this);

		grid.addView(table, 600, 200);
		grid.addView(userHand);
		grid.addView(endTurn);
		
		frame.addView(grid);
	}
	
	/**
	 * Redeseneaza grid-ul cu cartile de joc.
	 */
	public void redrawScreen() {
		grid.invalidate();
	}
	
	/**
	 * Adauga o carte in cadrul unei zone.
	 * 
	 * @param where - unde va fi plasata cartea?
	 * 		0 = se sterg toate cartile de pe masa
	 * 		1 = se sterg toate cartile din mana utilizatorului
	 * 		2 = se scot toate cartile din mana opnonentului
	 */
	public void addView(int where, ImageView view) {
		if (where == 0) {
			table.addView(view, 60, 100);
		} else if (where == 1) {
			userHand.addView(view);
		}
	}
	
	/**
	 * Sterge una sau toate cartile din cadrul unei zone.
	 * 
	 * @param from - de unde se vor scoate carti?
	 * 		0 = se sterg toate cartile de pe masa
	 * 		1 = se sterg toate cartile din mana utilizatorului
	 * 		2 = se scot toate cartile din mana opnonentului
	 * @param view - ce carte se va scoate?
	 * 		Daca valoarea null este data atunci se vor scoate toate.
	 */
	public void removeView(int from, ImageView view) {
		if (from == 0) {
			if (view != null) {
				table.removeView(view);
			} else {
				table.removeAllViews();
			}
		} else if (from == 1) {
			if (view != null) {
				userHand.removeView(view);
			} else  {
				userHand.removeAllViews();
			}
		}
		// TODO: pentru oponent
	}

	/**
	 * Ce se intampla cand se da click pe o carte de joc?
	 */
	@Override
	public void onClick(View view) {
		if (view instanceof ImageView) {
			// Ne asiguram ca utilizatorul poate alege doar o carte pe tura...
			if (!canClick) {
				return;
			}
			// Am selectat ceva, nu mai permitem click-ul pana cand nu vom mai primi
			// alte mesaje.
			canClick = false;
		
			// Obtinem cartea care a fost selectata.
			CardBase card = (CardBase) view.getTag();
			
			// Verificam daca butonul "End Turn" este activat. Daca este activat
			// atunci inseamna ca s-a produs o taiere si nu vom permite decat
			// anumite carti sa fie puse jos de jucator. Fie va pune 7, fie
			// va pune o carte din categoria primeia din lista.
			if (endTurn.isEnabled() && card.getNumber() != 7) {
				// Se obtine prima carte care a fost pusa pe tabla.
				CardBase firstCard = (CardBase)(((ImageView)table.getChildAt(0)).getTag());
				if (firstCard.getNumber() != card.getNumber()) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Trebuie sa tai neaparat.",
							Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
			}
			
			if (endTurn.isEnabled()) {
				endTurn.setEnabled(false);
			}
		
			// Scoatem cartea din mana utilizatorului
			removeView(1, (ImageView)view);
			// Adaugam cartea pe tabla, dar mai inainte vor detasa ascultatorul de pe
			// imagine.
			card.unregisterClickListener();
			addView(0, (ImageView)view);

			// Trimitem firului de executie un mesaj prin care il notificam ce carte
			// a selectat utilizatorul.
			startGame.postMessage(card.getName());
			// Il notificam sa se deblocheze
			synchronized (startGame) {
				startGame.notify();
			}
		}
		// S-a apasat pe butonul de Stop? Anuntam incheierea turii.
		else {
			endTurn.setEnabled(false);
			
			// Trimitem firului de executie un mesaj prin care il notificam
			// ca utilizatorul nu a selectat nicio carte
			startGame.postMessage("no");
			// Il notificam sa se deblocheze
			synchronized (startGame) {
				startGame.notify();
			}
		}
	}
}
