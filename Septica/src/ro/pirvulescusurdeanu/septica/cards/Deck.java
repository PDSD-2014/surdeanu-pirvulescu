package ro.pirvulescusurdeanu.septica.cards;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Clasa ce abstractizeaza un pachet de carti pentru septica. Initial el contine
 * toate cartile de la 7 in sus si pe masura ce jocul se desfasoara, numarul de
 * carti interne se va reduce, pana nu va mai exista nicio carte.
 */
public class Deck {
	private final LinkedList<CardBase> cards;
	private int numberOfCards;
	
	public Deck() {
		cards = new LinkedList<CardBase>();
		numberOfCards = 0;
		init();
	}
	
	/**
	 * Initializeaza un pachet de carti cu cartile shuffle-uite.
	 */
	public void init() {
		// Ne asiguram ca nu exista nicio carte in deck
		cards.clear();
		
		// Se genereaza cartile din cadrul deck-ului
		for (int i = 7; i < 15; i++) {
			cards.add(new CardBase(i, CardType.CLUB));
			cards.add(new CardBase(i, CardType.DIAMOND));
			cards.add(new CardBase(i, CardType.HEART));
			cards.add(new CardBase(i, CardType.SPADE));
			numberOfCards += 4;
		}
		
		// Facem un shuffle la lista de carti interna
		Collections.shuffle(cards);
	}
	
	/**
	 * Metoda utilitara pentru a obtine numarul de carti curent din cadrul
	 * pachetului de carti.
	 */
	public int getNumberOfCards() {
		return numberOfCards;
	}
	
	public CardBase removeFirst() {
		if (numberOfCards > 0) {
			numberOfCards--;
			return cards.removeFirst();
		}
		return null;
	}
	
}
