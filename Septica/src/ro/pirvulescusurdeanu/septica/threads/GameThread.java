package ro.pirvulescusurdeanu.septica.threads;

import java.util.Collections;
import java.util.LinkedList;

import ro.pirvulescusurdeanu.septica.activities.GameActivity;
import ro.pirvulescusurdeanu.septica.cards.Card;
import ro.pirvulescusurdeanu.septica.cards.CardList;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;

public class GameThread extends Thread {
	private final boolean isServer;
	public final GameActivity ga;
	public LinkedList<Card> listajos = new LinkedList<Card>();
	public LinkedList<Card> listaserver = new LinkedList<Card>();
	public LinkedList<Card> listaclient = new LinkedList<Card>();
	public boolean begingame;
	Card cs1, cs2, cs3, cs4, cc1, cc2, cc3, cc4;
	CardList cl;

	public GameThread(boolean isServer, GameActivity ga) {
		this.isServer = isServer;
		// Obtinem referinta de la Gama Activity
		this.ga = ga;

		begingame = true;

		// Instantiem obiectul ce va contine lista de carti
		cl = new CardList(ga.getBaseContext());
	}

	public void serverShuffleCards() {

		// facem shuffle la lista de carti
		Collections.shuffle(cl.allCards);
	}

	public void serverInitializeClientHand() {

		// Initializare mana de joc a clientului
		cc1 = cl.allCards.get(0);
		cl.allCards.remove(0);
		listaserver.add(cc1);

		cc2 = cl.allCards.get(0);
		cl.allCards.remove(0);
		listaserver.add(cs2);

		cc3 = cl.allCards.get(0);
		cl.allCards.remove(0);
		listaserver.add(cc3);

		cc4 = cl.allCards.get(0);
		cl.allCards.remove(0);
		listaserver.add(cc4);

	}

	public void serverInitializeServerHand() {
		// Initializare mana de joc a serverului
		cs1 = cl.allCards.get(0);
		cl.allCards.remove(0);
		listaserver.add(cs1);

		cs2 = cl.allCards.get(0);
		cl.allCards.remove(0);
		listaserver.add(cs2);

		cs3 = cl.allCards.get(0);
		cl.allCards.remove(0);
		listaserver.add(cs3);

		cs4 = cl.allCards.get(0);
		cl.allCards.remove(0);
		listaserver.add(cs4);

	}

	public void serverAnnounceGameStart() {

		StringBuilder temp = new StringBuilder("");
		for (int i = 0; i < listaclient.size(); i++) {
			temp.append(listaclient.get(i).basecard.getName() + ",");
		}
		temp.deleteCharAt(temp.length() - 1);

		BluetoothController.getInstance().sendMessage(temp.toString());

	}

	@Override
	public void run() {
		while (true) {
			// TODO: Implementare joc
			// Serverul va executa urmatorul joc
			if (isServer) {

				if (begingame == true) {
					// punem begingame pe false, jocul deja a inceput
					begingame = false;

					// facem shuffle la lista de carti
					serverShuffleCards();

					// intializam cele 4 carti de inceput ale serverului
					serverInitializeServerHand();

					// intializam cele 4 carti de inceput ale clientului
					serverInitializeClientHand();

					// notificam GameActivity de cele 4 carti din lista de
					// server
					ga.initializeHand(listaserver);

					serverAnnounceGameStart();

				}

			} else {// if isClient

				// se asteapta pana clientul primeste mesaj
				while (BluetoothController.getInstance().hasMessage() == false) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// luam mesajul de la server
				String temp = BluetoothController.getInstance().removeMessage();

				// verificam daca este mesajul initial
				String[] cards = temp.split(",");
				if (cards.length != 4) {
					throw new RuntimeException(
							"Initial  nu s-au trimis 4 carti");
				}
				
				//Adaugam in lista clientului cele 4 carti primite ca mesaj
				listaclient.add(new Card(cards[0],ga.getBaseContext()));
				listaclient.add(new Card(cards[1],ga.getBaseContext()));
				listaclient.add(new Card(cards[2],ga.getBaseContext()));
				listaclient.add(new Card(cards[3],ga.getBaseContext()));
				
				// notificam GameActivity de cele 4 carti din lista de
				// client
				ga.initializeHand(listaclient);
				
				
				
			}
		}
	}
}
