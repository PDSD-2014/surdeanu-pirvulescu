package ro.pirvulescusurdeanu.septica.activities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import ro.pirvulescusurdeanu.septica.R;
import ro.pirvulescusurdeanu.septica.cards.Card;
import ro.pirvulescusurdeanu.septica.cards.CardList;
import ro.pirvulescusurdeanu.septica.controllers.BluetoothController;
import ro.pirvulescusurdeanu.septica.threads.GameThread;
import ro.pirvulescusurdeanu.septica.utils.PictureFinder;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GameActivity extends Activity implements OnClickListener {

	public LinkedList<Card> listajos = new LinkedList<Card>();
	public LinkedList<Card> listacarti = new LinkedList<Card>();
	Card card, card1, card2, card3;
	LinearLayout linear, linearjos, linearoponent;
	GridLayout grid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		GameThread startGame = new GameThread(BluetoothController.getInstance().isServer());
		startGame.start();
		
		final Context context = getBaseContext();

		FrameLayout frame = (FrameLayout) findViewById(R.id.container);

		//creare layout
		linear = new LinearLayout(context);
		linearjos = new LinearLayout(context);
		linearoponent = new LinearLayout(context);
		grid = new GridLayout(context);
		grid.setRowCount(4);
		grid.setColumnCount(1);
		
		// creare lista carti
		CardList cl = new CardList(context);
		//amestecare lista carti
		Collections.shuffle(cl.allCards);

		
		//extragem cartile din lista random
		Random rand = new Random();
		int nr, nr1, nr2, nr3, nr4, nr5;


			// creating the 4 starting cards
			// deleting them from the initial list
			nr = rand.nextInt(cl.allCards.size());
			card = cl.allCards.get(nr);
			cl.allCards.remove(nr);
			listacarti.add(card);

			nr1 = rand.nextInt(cl.allCards.size());
			Card card1 = cl.allCards.get(nr1);
			cl.allCards.remove(nr1);

			listacarti.add(card1);

			nr2 = rand.nextInt(cl.allCards.size());
			Card card2 = cl.allCards.get(nr2);
			cl.allCards.remove(nr2);

			listacarti.add(card2);

			nr3 = rand.nextInt(cl.allCards.size());
			Card card3 = cl.allCards.get(nr3);
			cl.allCards.remove(nr3);
			listacarti.add(card3);

			
			
			//mai cream 2 carti de test pentru linearlayout de jos
			nr4 = rand.nextInt(cl.allCards.size());
			Card card4 = cl.allCards.get(nr4);
			cl.allCards.remove(nr4);
			listajos.add(card4);

			nr5 = rand.nextInt(cl.allCards.size());
			Card card5 = cl.allCards.get(nr5);
			cl.allCards.remove(nr5);
			listajos.add(card5);

			
			//setam layout parameters la carti
			card.image.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			card1.image.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			card2.image.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			card3.image.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			card4.image.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			card5.image.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			
			//cream 4 imagini pentru lista de carti a oponentului
			ImageView im = new ImageView(context);
			im.setImageResource(PictureFinder.findPictureByName("image1"));
			im.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));

			ImageView im2 = new ImageView(context);
			im.setImageResource(PictureFinder.findPictureByName("image2"));
			im.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));

			ImageView im3 = new ImageView(context);
			im.setImageResource(PictureFinder.findPictureByName("image3"));
			im.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));

			ImageView im4 = new ImageView(context);
			im.setImageResource(PictureFinder.findPictureByName("image4"));
			im.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			
			//adaugam cartile la layout-uri

			linearoponent.addView(im, 60, 100);
			linearoponent.addView(im2, 60, 100);
			linearoponent.addView(im3, 60, 100);
			linearoponent.addView(im4, 60, 100);

			linear.addView(card.image);
			linear.addView(card1.image);
			linear.addView(card2.image);
			linear.addView(card3.image);

			linearjos.addView(card4.image, 60, 100);
			linearjos.addView(card5.image, 60, 100);

			
			//cream un buton ce va reprezenta sfarsitul mainii de joc
			Button bt = new Button(context);
			bt.setText("End Hand");
			

			grid.addView(linearoponent, 600, 200);
			grid.addView(linearjos, 600, 200);
			grid.addView(linear);
			grid.addView(bt);


			//cream listeneri pe cartile din mana
			for (int i = 0; i < listacarti.size(); i++) {
				listacarti.get(i).image.setOnClickListener(this);
			}

			frame.addView(grid);

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//obtinem adresa obiectului din tag-ul imaginii
		Card d = (Card) v.getTag();
		
		//adaugam cartea in lista de carti de jos(cele date deja)
		listajos.add(d);
		//scoatem cartea din lista de carti din mana
		listacarti.remove(d);

		//updatam linear layout
		linear.removeView(d.image);
		linearjos.addView(d.image, 60, 100);

	}

}
