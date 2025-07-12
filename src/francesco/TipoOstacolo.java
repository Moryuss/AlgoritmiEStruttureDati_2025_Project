package francesco;

public enum TipoOstacolo {
	SEMPLICE					(0b00000001), // cella singolo
	AGGLOMERATO					(0b00000010), // rettangolo di celle
	BARRA_VERTICALE				(0b00000100),
	BARRA_ORIZZONTALE			(0b00001000),
	BARRA_DIAGONALE				(0b00010000), // solo sottile (spessore = 1)
	ZONA_CHIUSA					(0b00100000), // forme rettangolari cave all'interno
	DELIMITATORE_VERTICALE		(0b01000000),
	DELIMITATORE_ORIZZONTALE	(0b10000000);
	
	private final short valore;
	
	private TipoOstacolo(int n) {
		valore = (short)n;
	}
	
	public int value() {
		return valore;
	}
	
	public static int sommaTipi(int first, int second) {
		return first | second;
	}
}
