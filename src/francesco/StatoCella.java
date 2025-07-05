package francesco;

public enum StatoCella {
	VUOTA		(0b0000000), 
	CONTESTO	(0b0000001),
	COMPLEMENTO	(0b0000010),
	//CHIUSURA	(0b0000011),
	FRONTIERA	(0b0000100),
	OSTACOLO	(0b0001000),
	ORIGINE		(0b0010000),
	DESTINAZIONE(0b0100000),
	LANDMARK	(0b1000000);
	
	
	private final int value;
	
	private StatoCella(int v) {
		value = v;
	}
	
	public int value() {
		return value;
	}
	
	
	public boolean is(int n) {
		return (value&n)>0;
	}
	
	public boolean isNot(int n) {
		return (value&n)==0;
	}
	
	
	public boolean is(ICella cella) {
		return is(cella.stato());
	}
	public boolean isNot(ICella cella) {
		return isNot(cella.stato());
	}
	
	
	
	public int addTo(int n) {
		return n | value;
	}
	
	public int removeTo(int n) {
		return n & ~value;
	}
	
}
