package ro.pirvulescusurdeanu.septica.threads;

import java.util.LinkedList;

import ro.pirvulescusurdeanu.septica.activities.GameActivity;
import ro.pirvulescusurdeanu.septica.cards.CardBase;
import ro.pirvulescusurdeanu.septica.cards.Deck;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.controllers.ErrorController;
import ro.pirvulescusurdeanu.septica.controllers.MainController;
import android.util.Log;

public class GameThread extends Thread {
	private final boolean isServer;
	private Deck deck;
	private final GameActivity game;
	private String command;
	private LinkedList<CardBase> turnList;
	private boolean meFirst;
	private boolean flag;

	public GameThread(boolean isServer) {
		this.isServer = isServer;
		game = (GameActivity)MainController.getInstance().getCurrentActivity();
		if (isServer) {
			turnList = new LinkedList<CardBase>();
			// Serverul va fi intotdeauna primul
			meFirst = true;
		} else {
			// Clientul va fi la inceput al doilea jucator...
			meFirst = false;
		}
	}
	
	public void postMessage(String command) {
		this.command = command;
	}
	
	public void runServer() {
		// Se construieste pachetul de carti.
		deck = new Deck();
		
		// Se iau 4 carti din acest pachet si se trimit catre utilizatorul de
		// pe server. In principiu, se vor trimite catre GameActivity.
		for (int i = 0; i < 4; i++) {
			sendCardToUI(1, deck.removeFirst());
		}
		
		//initializam numarul de carti din mana serverului
		int serverCards = 4;
		
		// Se iau 4 carti din pachet si se trimit catre client. Acestea se vor
		// trimite prin Bluetooth.
		StringBuilder frame = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			frame.append(deck.removeFirst().getName() + ",");
		}
		frame.deleteCharAt(frame.length() - 1);
		sendBluetoothCommand("take-" + frame.toString());
		
		//astept ACK
		waitForBluetoothCommand();
		
		
		int serverPoints = 0;
		int clientPoints = 0;
		boolean serverWinner = true;
		flag = true;
		
		//trimitem scorul initial si anuntam serverul ca incepe primul
		update(serverPoints,1);

		
		// Jocul se v-a termina atunci cand nu vor mai fi carti in pachet si cand oponentul nu mai are carti in mana
		while (serverCards > 0 || deck.getNumberOfCards() > 0) {
			CardBase myCard;
			CardBase opponentCard;
			boolean continueTurn = false;
			
			
			// Simuleaza o tura...
			do {
				
				// Sunt eu primul?
				if (meFirst) {
					
					// Daca da astept sa primesc o carte de la utilizator
					waitForUserTurn((continueTurn) ? true : false);
					// Daca oponentul a renuntat si nu mai taie cartea
					if (command.equals("no")) {
						serverWinner = false;
						break;
					}
					//daca se da o carte jos
					serverCards--;
					
					myCard = new CardBase(command);
					// Adaugam cartea in lista de carti
					turnList.add(myCard);
					// Trimit cartea aleasa de mine catre client...
					sendBluetoothCommand("update-" + command);
					
					//astept ACK
					waitForBluetoothCommand();
					// Cer o carte de la client
					sendBluetoothCommand("give");
					// Astept pana primesc raspuns de la client cu o carte
					waitForBluetoothCommand();
					
					// Ce carte a fost aleasa?
					opponentCard = new CardBase(command);
					// Adaugam cartea in lista de carti
					turnList.add(opponentCard);
					// Ce carte am primit de la client?
					sendCardToUI(0, opponentCard);
				} else {
					// Cer o carte de la client. Imi va da ceva sau nu?
					if (continueTurn) {
						sendBluetoothCommand("continue");
					} else {
						sendBluetoothCommand("give");
					}
					
					// Astept pana primesc raspuns de la client cu o carte
					waitForBluetoothCommand();
					// Daca oponentul a renuntat si nu mai taie cartea
					if (command.equals("no")) {
						serverWinner = true;
						break;
					}
					
					//daca se da o carte jos
					serverCards--;
					
					opponentCard = new CardBase(command);
					// Adaugam cartea in lista de carti
					turnList.add(opponentCard);
					// Ce carte am primit de la client?
					sendCardToUI(0, opponentCard);
					// Acum astept si eu o carte de la utilizator
					waitForUserTurn(false);
					myCard = new CardBase(command);
					turnList.add(myCard);
					// Trimit cartea aleasa de mine catre client...
					sendBluetoothCommand("update-" + command);
					
					//astept ACK
					waitForBluetoothCommand();
				}
				// Tura va continua pana cand se produce o taietura si jocul se va continua
				if ((meFirst && myCard.isCut(opponentCard,turnList.get(0))) || (!meFirst && opponentCard.isCut(myCard,turnList.get(0)))) {
					continueTurn = true;
				} else {
					continueTurn = false;
				}
			}
			// Continua executia turei atata timp cat s-a produs o taietura sau
			// utilizatorul a mai dorit asta...
			while (continueTurn);
			// Cine a castigat tura?
			if (serverWinner) {
				meFirst = true;
				// Serverul este cel care a castigat. Actualizam punctajul sau...
				for (CardBase card : turnList) {
					serverPoints += card.getPoints();
				}
			} else {
				meFirst = false;
				// Clientul este cel care a castigat. Sa-i acordam punctele sale...
				for (CardBase card : turnList) {
					clientPoints += card.getPoints();
				}
			}
			
			//updatam scorul si randul serverului
			if(meFirst==true){
				update(serverPoints,1);
				sendBluetoothCommand("Informations-"+","+clientPoints+","+"2");
				
				
			}else{
				update(serverPoints,2);
				sendBluetoothCommand("Informations-"+","+clientPoints+","+"1");
			}
			
			//astept ACK
			waitForBluetoothCommand();
			
			
			
			// In acest moment tura s-a terminat (notificam clientul pentru faptul ca
			// tura s-a terminat).
			sendBluetoothCommand("clear");
			// Si cartile de pe propria tabla, vor trebui sa dispara...
			
			
			
			clearTable();
			
			
			//astept ACK ca am curatat tabla
			waitForBluetoothCommand();
			
			
			// Cate carti se ofera?
			int count = turnList.size() / 2;
			for (int i = 0; i < count; i++) {
				if (deck.getNumberOfCards()==0) {
					break;
				}
				//primim carte din pachet
				serverCards++;
				
				// Serverul este primul?
				if (meFirst) {
					sendCardToUI(1, deck.removeFirst());
					sendBluetoothCommand("take-" + deck.removeFirst().getName());
				} else {
					sendBluetoothCommand("take-" + deck.removeFirst().getName());
					sendCardToUI(1, deck.removeFirst());
				}
				
				//astept ACK
				waitForBluetoothCommand();
				
				
			}
			// Tura curenta se va sterge complet!
			turnList.clear();
		}
		Log.i("SP", serverPoints + "");
		Log.i("CP", clientPoints + "");
		
		if(flag==true){
			flag = false;
			
			sendBluetoothCommand("Finish");
			canReplay();
			canExit();
			//activam butoanele de exit si replay 
			finish();
		}
		
		
	}
	
	public void runClient() {
		while (true) {
			// Astepta sa primeasca cele 4 carti initiale pentru jucator...
			waitForBluetoothCommand();
			// Am primit o comanda de adaugare carti in mana utilizatorului
			if (command.startsWith("take")) {
				// Vom adauga cartile in mana utilizatorului nostru...
				String[] cards = command.substring(command.indexOf('-') + 1).split(",");
				for (String card : cards) {
					sendCardToUI(1, new CardBase(card));
				}
				
				//trimit ACK ca am primit carti
				sendBluetoothCommand("ack");
				
			}
			// Am primit o comanda de adaugare carte a adversarului pe tabla...
			else if (command.startsWith("update")) {
				// Vom adauga cartile pe tabla
				String[] cards = command.substring(command.indexOf('-') + 1).split(",");
				for (String card : cards) {
					sendCardToUI(0, new CardBase(card));
				}
				
				//trimit ACK ca am updatat lista de jos a cartilor
				sendBluetoothCommand("ack");
			}
			// Am primit o comanda prin care suntem obligati sa dam o carte...
			else if (command.startsWith("give")) {
				// Asteptam pana utilizatorul alege o carte.
				waitForUserTurn(false);
				// Dupa ce a ales o carte o vom putea trimite prin Bluetooth.
				sendBluetoothCommand(command);
			}
			// Am primit o comanda prin care suntem intrebati daca dorim sa continuam
			else if (command.startsWith("continue")) {
				// Asteptam pana utilizatorul alege o carte cu care sa poata taia
				// sau apasa pe butonul de "End Turn".
				waitForUserTurn(true);
				// Daca nu alege nicio carte, atunci command va avea valoarea "no"
				sendBluetoothCommand(command);
			}
			// Am primit o comanda prin care suntem notificati de faptul ca tura
			// curenta s-a terminat.
			else if (command.startsWith("clear")) {
				clearTable();
				
				//trimit ACK 
				sendBluetoothCommand("ack");
			} 
			
			//vedem daca se face update la scor sau la cine incepe primul
			else if(command.startsWith("Informations")){
				
				String[] cards = command.substring(command.indexOf('-') + 1).split(",");
				
				int scor3 = Integer.parseInt(cards[1]);
				int rand3 = Integer.parseInt(cards[2]);
				update(scor3, rand3);
				
				//trimit ACK 
				sendBluetoothCommand("ack");
				
				
			}
			
			else if(command.startsWith("Fi")){
				
				//activam butoanele de exit si replay 
				
				canReplay();
				canExit();
				//afisam daca am castigat sau nu
				finish();
			}
		}
	}

	/**
	 * Atat serverul cat si clientul vor rula pana cand se va termina jocul.
	 */
	@Override
	public void run() {
		if (isServer) {
			runServer();
		} else {
			runClient();
		}
	}
	
	public boolean isEndTurn() {
		return false;
	}
	
	/**
	 * S-a terminat o mana. Vom face ca toate cartile de pe tabla sa dispara...
	 */
	private void clearTable() {
		game.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				game.removeView(0, null);
			}
		});
	}
	
	
	
	/**
	 * Cand se termina o mana facem update la scor si informam cine incepe mana
	 */
	private void finish(){


		game.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				game.finish();
				
			}
		});
		
	}
	
	
	/**
	 * Daca jocul s-a terminat se poate apasa butonul de Exit
	 */
	private void canExit(){
	
		game.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				game.canExit();
				
			}
		});
		
	}
	
	
	/**
	 * Daca jocul s-a terminat se poate apasa butonul de Exit
	 */
	private void canReplay(){
	
		game.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				game.canReplay();
				
			}
		});
		
	}
	
	
	
	/**
	 * Cand se termina o mana facem update la scor si informam cine incepe mana
	 */
	private void update(int scor, int rand){
		final int scor2 = scor;
		final int rand2 = rand;
		game.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				game.updateInfomations(scor2, rand2);
				
			}
		});
		
	}
	
	/**
	 * Trimite o carte catre utilizatorul de pe dispozitivul care ruleaza acum.
	 * 
	 * @param card
	 * 		Cartea ce va fi trimisa.
	 */
	private void sendCardToUI(final int where, final CardBase card) {
		game.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (where == 1) {
					card.registerClickListener();
				}
				game.addView(where, card.getImage());
			}
		});
	}
	
	private void sendBluetoothCommand(String command) {
		BluetoothController.getInstance().sendMessage(command);
	}
	
	private void waitForBluetoothCommand() {
		BluetoothController bc = BluetoothController.getInstance();
		// Asteptam pana primim un mesaj
		while (!bc.hasMessage()) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				ErrorController.getInstance().addError(e);
			}
		}
		// Apoi il salvam in variabila "command"
		command = bc.removeMessage();
	}

	private void waitForUserTurn(final boolean canStop) {
		game.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				game.canClickOverImage();
				if (canStop) {
					game.canStopTurn();
				}
			}
		});
		// Asteptam pana cand utilizatorul va selecta o carte...
		try {
			synchronized (this) {
				this.wait();
			}
		} catch (InterruptedException e) {
			ErrorController.getInstance().addError(e);
		}
	}
}
