package ro.pirvulescusurdeanu.septica.cards;

public enum CardType {
	DIAMOND("d"),
	SPADE("s"),
	HEART("h"),
	CLUB("c");
	
	private String type;

    private CardType(String type) {
    	this.type = type;
    }

    public String getType() {
    	return type;
    }
}
