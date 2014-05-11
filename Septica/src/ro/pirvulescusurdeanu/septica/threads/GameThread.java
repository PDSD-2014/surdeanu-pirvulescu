package ro.pirvulescusurdeanu.septica.threads;

import java.util.LinkedList;

import ro.pirvulescusurdeanu.septica.activities.GameActivity;
import ro.pirvulescusurdeanu.septica.cards.CardBase;
import ro.pirvulescusurdeanu.septica.cards.Deck;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.controllers.ErrorController;
import ro.pirvulescusurdeanu.septica.controllers.MainController;

public class GameThread extends Thread {
	private final boolean isServer;
	private Deck deck;
	private final GameActivity game;
	private String command;
	private LinkedList<CardBase> turnList;
	private boolean meFirst; 

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
			sendCardToUser(deck.removeFirst());
		}
		// Se iau 4 carti din pachet si se trimit catre client. Acestea se vor
		// trimite prin Bluetooth.
		StringBuilder frame = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			frame.append(deck.removeFirst().getName() + ",");
		}
		frame.deleteCharAt(frame.length() - 1);
		sendBluetoothCommand("take-" + frame.toString());
		// Incepem o tura. Initializam lista de carti din aceasta tura.
		turnList.clear();
		CardBase myCard;
		CardBase opponentCard;
		do {
			// Sunt eu primul?
			if (meFirst) {
				// Daca da astept sa primesc o carte de la utilizator
				waitForUserTurn(false);
				// Ce carte a fost aleasa?
				myCard = new CardBase(command);
				// Adaugam cartea in lista de carti
				turnList.add(myCard);
				// Cer o carte de la client
				sendBluetoothCommand("give");
				// Astept pana primesc raspuns de la client cu o carte
				waitForBluetoothCommand();
				// Ce carte a fost aleasa?
				opponentCard = new CardBase(command);
				// Adaugam cartea in lista de carti
				turnList.add(opponentCard);
			} else {
				// Cer o carte de la client
				sendBluetoothCommand("give");
				// Astept pana primesc raspuns de la client cu o carte
				waitForBluetoothCommand();
				// Ce carte a fost aleasa?
				opponentCard = new CardBase(command);
				// Adaugam cartea in lista de carti
				turnList.add(opponentCard);
				// Acum astept si eu o carte de la utilizator
				waitForUserTurn(false);
				myCard = new CardBase(command);
				turnList.add(myCard);
			}
		}
		// Tura va continua pana cand se produce o taietura si jocul se va continua
		while ((meFirst && myCard.isCut(opponentCard)) || (!meFirst && opponentCard.isCut(myCard)));
		
	}
	
	public void runClient() {
		while (true) {
			// Astepta sa primeasca cele 4 carti initiale pentru jucator...
			waitForBluetoothCommand();
			// Am primit o comanda de adaugare carti...
			if (command.startsWith("take")) {
				// Vom adauga cartile in mana utilizatorului nostru...
				String[] cards = command.substring(command.indexOf('-') + 1).split(",");
				for (String card : cards) {
					sendCardToUser(new CardBase(card));
				}
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
	 * Trimite o carte catre utilizatorul de pe dispozitivul care ruleaza acum.
	 * 
	 * @param card
	 * 		Cartea ce va fi trimisa.
	 */
	private void sendCardToUser(final CardBase card) {
		game.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Inainte de a afisa cartea pe ecran, ne asiguram ca ii setam
				// si un listener.
				card.registerClickListener();
				game.addView(1, card.getImage());
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
