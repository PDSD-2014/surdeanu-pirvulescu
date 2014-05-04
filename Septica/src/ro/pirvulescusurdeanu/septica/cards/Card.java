package ro.pirvulescusurdeanu.septica.cards;

import ro.pirvulescusurdeanu.septica.utils.CardType;
import ro.pirvulescusurdeanu.septica.utils.PictureFinder;
import android.content.Context;
import android.widget.ImageView;

public class Card {

	private final int points;
	private final int cardNumber;
	private final String name;
	private final ImageView image;
	private CardType type;

	public Card(String name, Context context) {

		this.name = "" + name;
		String aux_point = "";
		aux_point = "" + this.name.charAt(1);

		if (aux_point.equals("1")) {
			points = 1;
		} else {
			points = 0;
		}

		image = new ImageView(context);
		image.setImageResource(PictureFinder.findPictureByName(this.name));
		this.cardNumber = this.calculateNumber(this.name);
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

	private int calculateNumber(String name) {

		if (name.length() == 3)
			return 10;

		String aux = "";
		aux = "" + this.name.charAt(1);

		if (aux.equals("j")) {
			return 12;
		} else if (aux.equals("q")) {
			return 13;
		} else if (aux.equals("k")) {

			return 14;
		}

		int number = Integer.parseInt(aux);

		return number;

	}

	public int getPoints() {

		return points;
	}

	public int getNumber() {

		return this.cardNumber;
	}

	public String getName() {

		return this.name;
	}

	public ImageView getImage() {

		return this.image;
	}

	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
	}

}
