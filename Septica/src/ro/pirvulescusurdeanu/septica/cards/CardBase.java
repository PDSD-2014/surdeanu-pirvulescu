package ro.pirvulescusurdeanu.septica.cards;

import ro.pirvulescusurdeanu.septica.activities.GameActivity;
import ro.pirvulescusurdeanu.septica.controllers.MainController;
import ro.pirvulescusurdeanu.septica.utils.PictureFinder;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * Clasa model ce descrie o carte dintr-un pachet de carti.
 */
public class CardBase {
	/** Tipul cartii */
	private CardType type;
	/** Numarul cartii : 7, 8, 9, 10, 11, 12, 13, 14 */
	private final int number;
	/** Referinta catre view-ul cu imaginea aferenta. */
	private ImageView image;
	
	public CardBase(int number, CardType type) {
		this.number = number;
		this.type = type;
		
		setImageReference();
	}
	
	public CardBase(String name) {
		switch (name.charAt(0)) {
			case 'd':
				type = CardType.DIAMOND;
				break;
			case 'c':
				type = CardType.CLUB;
				break;
			case 'h':
				type = CardType.HEART;
				break;
			default:
				type = CardType.SPADE;
		}
		number = Integer.parseInt(name.substring(1));
		
		setImageReference();
	}
	
	private void setImageReference() {
		image = new ImageView(MainController.getInstance().getCurrentActivity().getBaseContext());
		image.setImageResource(PictureFinder.findPictureByName(getName()));
		// Mai setam cativa parametri pentru imagini
		image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
											   LayoutParams.WRAP_CONTENT));
		// Fiecare imagine va pastra intern o referinta catre aceasta clasa, ce
		// o descrie, in fapt.
		image.setTag(this);
	}
	
	public ImageView getImage() {
		return image;
	}
	
	public int getNumber() {
		return number;
	}
	
	/**
	 * Ataseaza un ascultator pe o carte.
	 */
	public void registerClickListener() {
		image.setOnClickListener((GameActivity)MainController.getInstance().getCurrentActivity());
	}
	
	/**
	 * Detaseaza un ascultator de pe o carte.
	 */
	public void unregisterClickListener() {
		image.setOnClickListener(null);
	}
	
	/**
	 * Verifica daca s-a produs o taietura de catre celalalt jucator.
	 * 
	 * @param card
	 * 		Cartea cu care se compara
	 * @return
	 * 		True daca exista taiatura sau False altfel.
	 */
	public boolean isCut(CardBase card) {
		return (card.getNumber() == 7 || number == card.getNumber()) ? true : false;
	}
	
	/**
	 * Intoarce numarul de puncte pe care o carte il are in calcularea scorului
	 * final.
	 */
	public int getPoints() {
		// Doar 10 si AS-ul sunt puncte.
		return (number == 10 || number == 11) ? 1 : 0;
	}
	
	/**
	 * Determina si intoarce numele cartii pe baza caruia putem selecta imaginea
	 * aferenta cartii.
	 */
	public String getName() {
		return type.getType() + number;
	}

}
