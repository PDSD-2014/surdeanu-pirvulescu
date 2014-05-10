package ro.pirvulescusurdeanu.septica.cards;

import ro.pirvulescusurdeanu.septica.utils.PictureFinder;
import android.content.Context;
import android.widget.ImageView;

public class Card{

	public ImageView image;
	public CardBase basecard;
	
	public Card(String name,Context context) {
		
		//instantiem o un obiect de tipul CardBase, pentru a avea acces la proprietatile cartii 
		basecard = new CardBase(name);
		
		//asociem o imagine la aceasta carte
		image = new ImageView(context);
		image.setImageResource(PictureFinder.findPictureByName(name));
		//setam un tag cu adresa acestui obiect
		image.setTag(this);

	}
	
	//intoarce imaginea folosita
	 public ImageView getImage() {

		return this.image;
	}

	

}
