package ro.pirvulescusurdeanu.septica.activities;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.cards.CardBase;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.threads.GameThread;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
	private LinearLayout userHand, table, informatii,butoane;
	private GridLayout grid;
	private Button endTurn;
	private boolean canClick;
	private Button scor;
	private Button turn;
	private Button exit;
	private Button replay;

	
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
		informatii = new LinearLayout(context);
		butoane = new LinearLayout(context);
		
		grid = new GridLayout(context);
		grid.setRowCount(4);
		grid.setColumnCount(1);
		
		//endTurn button
		endTurn = new Button(context);
		endTurn.setText("End Hand");
		endTurn.setEnabled(false);
		endTurn.setOnClickListener(this);
		
		
		//exit button
		exit = new Button(context);
		exit.setText("Exit");
		exit.setEnabled(false);
		exit.setOnClickListener(this);
		exit.setVisibility(View.INVISIBLE);
		
		//replay button
		replay = new Button(context);
		replay.setText("Replay");
		replay.setEnabled(false);
		replay.setOnClickListener(this);
		replay.setVisibility(View.INVISIBLE);
		
		
		//butoane pentru scor si afisat cine este la rand
		turn = new Button(context);
		scor = new Button(context);
		turn.setText("Al doilea");
		turn.setEnabled(false);
		scor.setText("0");
		scor.setEnabled(false);
		
		informatii.addView(endTurn);
		informatii.addView(turn);
		informatii.addView(scor);
		
		butoane.addView(exit);
		butoane.addView(replay);

		grid.addView(table, 600, 200);
		grid.addView(userHand);
		grid.addView(informatii);
		grid.addView(butoane);
		
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
			table.addView(view, 40, 80);
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
	
	
	//updatam scorul
	//rand = 1 - sunt primul
	//rand = 2 - sunt al doilea 
	public void updateInfomations(int scor, int rand){
		this.scor.setText(""+scor);
		
		if(rand == 1){
			this.turn.setText("Primul");
		}else{
			this.turn.setText("Al doilea");
		}
		
	}
	
	//cand se termina jocul
	
	@Override
	public void finish(){
		
		endTurn.setVisibility(View.INVISIBLE);
		scor.setVisibility(View.INVISIBLE);
		turn.setVisibility(View.INVISIBLE);
		
		
		String aaa =""+ this.scor.getText();
		int rezultat = Integer.parseInt(aaa);
		//daca am castigat
		if(rezultat>4){
		
			Toast toast = Toast.makeText(getApplicationContext(),
					"Ai castigat.",
					Toast.LENGTH_SHORT);
			toast.show();

		}else if(rezultat==4){//daca este egalitate
			Toast toast = Toast.makeText(getApplicationContext(),
					"Egalitate",
					Toast.LENGTH_SHORT);
			toast.show();

			
		}else{//daca am pierdut
			
			Toast toast = Toast.makeText(getApplicationContext(),
					"Ai pierdut",
					Toast.LENGTH_SHORT);
			toast.show();

		}
		
	}
	
	
	
	/**
	 * Daca s-a terminat jocul, jucatorul poate iesi din joc
	 */
	public void canExit(){
		
		exit.setVisibility(View.VISIBLE);
		exit.setEnabled(true);
	}
	
	
	/**
	 * Daca s-a terminat jocul, jucatorul poate restarta jocul
	 */
	public void canReplay(){
		
		replay.setVisibility(View.VISIBLE);
		replay.setEnabled(true);
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
			
			// Am selectat ceva, nu mai permitem click-ul pana cand nu vom mai primi
			// alte mesaje.
			canClick = false;
		
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
			
			
			Button bt = (Button) view;
			if(bt.getText().equals("Replay")){
				//daca s-a apasat butonul de replay
				
				Context context = this.getBaseContext();
				Intent mStartActivity = new Intent(context, SplashActivity.class);
				int mPendingIntentId = 123456;
				PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
				System.exit(0);
				
				
				
				
				
			}else if(bt.getText().equals("Exit")){
				
				//daca se apasa butonul de exit
				System.exit(0);
				
			}
			//daca apasam butonul de End Turn
			else{
			
				endTurn.setEnabled(false);
				
				// Am selectat ceva, nu mai permitem click-ul pana cand nu vom mai primi
				// alte mesaje.
				canClick = false;
				
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
}
