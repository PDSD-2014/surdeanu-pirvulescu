package ro.pirvulescusurdeanu.septica.cards;

import java.util.ArrayList;

import android.content.Context;

public class CardList {
	
	ArrayList<Card>  allCards  = new ArrayList<Card>();
	
	public CardList(Context context){
		
		//Creating the diamond cards
		Card d1 = new Card("d1",context);
		Card d7 = new Card("d7",context);
		Card d8 = new Card("d8",context);
		Card d9 = new Card("d9",context);
		Card d10 = new Card("d10",context);
		Card dj = new Card("dj",context);
		Card dq = new Card("dq",context);
		Card dk = new Card("dk",context);
		
		//Creating the heart cards
		Card h1 = new Card("h1",context);
		Card h7 = new Card("h7",context);
		Card h8 = new Card("h8",context);
		Card h9 = new Card("h9",context);
		Card h10 = new Card("h10",context);
		Card hj = new Card("hj",context);
		Card hq = new Card("hq",context);
		Card hk = new Card("hk",context);
		
		//Creating the spades cards
		Card s1 = new Card("s1",context);
		Card s7 = new Card("s7",context);
		Card s8 = new Card("s8",context);
		Card s9 = new Card("s9",context);
		Card s10 = new Card("s10",context);
		Card sj = new Card("sj",context);
		Card sq = new Card("sq",context);
		Card sk = new Card("sk",context);
		
		//Creating the club cards
		Card c1 = new Card("c1",context);
		Card c7 = new Card("c7",context);
		Card c8 = new Card("c8",context);
		Card c9 = new Card("c9",context);
		Card c10 = new Card("c10",context);
		Card cj = new Card("cj",context);
		Card cq = new Card("cq",context);
		Card ck = new Card("ck",context);

		
		//adding the diamond cards to the allCards list
		allCards.add(d1);
		allCards.add(d7);
		allCards.add(d8);
		allCards.add(d9);
		allCards.add(d10);
		allCards.add(dj);
		allCards.add(dq);
		allCards.add(dk);
		
		//adding the heart cards to the allCards list
		allCards.add(h1);
		allCards.add(h7);
		allCards.add(h8);
		allCards.add(h9);
		allCards.add(h10);
		allCards.add(hj);
		allCards.add(hq);
		allCards.add(hk);
		
		//adding the spade cards to the allCards list
		allCards.add(s1);
		allCards.add(s7);
		allCards.add(s8);
		allCards.add(s9);
		allCards.add(s10);
		allCards.add(sj);
		allCards.add(sq);
		allCards.add(sk);
		
		//adding the club cards to the allCards list
		allCards.add(c1);
		allCards.add(c7);
		allCards.add(c8);
		allCards.add(c9);
		allCards.add(c10);
		allCards.add(cj);
		allCards.add(cq);
		allCards.add(ck);
		
	}
	
}
