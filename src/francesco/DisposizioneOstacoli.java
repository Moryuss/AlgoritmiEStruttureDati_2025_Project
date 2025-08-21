package francesco;

public enum DisposizioneOstacoli {

	VARAZIONE_DIMENSIONI,
	SPIRALE,
	LINEA_SPEZZATA,
	DOPPIA_LINEA_SPEZZATA,
	SCACCHIERA,
	STANDARD;
	
	private final int value = TipoOstacolo.PERSONALIZZATO.value() + ordinal();
	
	public int value() {
		return value;
	}

}
