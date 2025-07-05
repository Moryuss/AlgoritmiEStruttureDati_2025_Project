package francesco;

public enum TipoOstacolo {
	SEMPLICE, // cella singolo
	AGGLOMERATO, // rettangolo di celle
	BARRA_VERTICALE,
	BARRA_ORIZZONTALE,
	BARRA_DIAGONALE, // solo sottile (spessore = 1)
	ZONA_CHIUSA, // forme rettangolari cave all'interno
	DELIMITATORE_VERTICALE,
	DELIMITATORE_ORIZZONTALE
}
