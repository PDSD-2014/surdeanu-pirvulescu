package ro.pirvulescusurdeanu.septica.cards;


import ro.pirvulescusurdeanu.septica.utils.CardType;

public class CardBase {

	//proprietati carte
	private final int points; //daca o carte are punctaj sau nu
	private final int cardNumber; //ce numar are cartea
	private final String name; //numele cartii
	private CardType type; //tipul cartii

	public CardBase(String name) {

		this.name = "" + name;
		String aux_point = "";
		aux_point = "" + this.name.charAt(1);

		//verificam daca cartea va avea punct sau nu, verificand dupa numele cartii(daca este As sau un 10)
		if (aux_point.equals("1")) {
			points = 1;
		} else {
			points = 0;
		}

		//obtinem ce numar are cartea in functie de numele ei
		this.cardNumber = this.calculateNumber(this.name);
		
		//vedem ce tip de carte este in functie de prima litera din nume
		String tipCarte = "" + this.name.charAt(0);
		
		if (tipCarte.equals("d")) {
			this.setType(CardType.Diamond);
		} else if(tipCarte.equals("c")){
			
			this.setType(CardType.Club);
		}else if(tipCarte.equals("s")){
			
			this.setType(CardType.Spades);
		}else if(tipCarte.equals("h")){
			
			this.setType(CardType.Hearts);
		}
	}

	//aceasta functie obtine numarul cartii din nume
	private int calculateNumber(String name) {

		//verificam daca lungimea cartii este 3, numai cele cu numarul 10 au lungimea 3, de exemplu d10, h10, etc
		if (name.length() == 3)
			return 10;

		String aux = "";
		aux = "" + this.name.charAt(1);

		//verificam daca este juvete, regina sau rege
		if (aux.equals("j")) {
			return 12;
		} else if (aux.equals("q")) {
			return 13;
		} else if (aux.equals("k")) {

			return 14;
		}

		//daca nu obtinem numarul cartii
		int number = Integer.parseInt(aux);

		return number;

	}

	
	//intoarce numarul de puncte din carte
	public int getPoints() {

		return points;
	}

	//intoarce ce numar are cartea
	public int getNumber() {

		return this.cardNumber;
	}

	//intoarce numele cartii
	public String getName() {

		return this.name;
	}

	//intoarce tipul cartii
	public CardType getType() {
		return type;
	}

	//seteaza tipul cartii
	public void setType(CardType type) {
		this.type = type;
	}



}
