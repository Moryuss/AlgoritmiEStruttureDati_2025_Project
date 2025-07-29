package matteo;

import francesco.ICella2D;

public interface IStatisticheEsecuzione {

	public void saveDimensioniGriglia(int h, int w);
	public int getAltezzaGriglia();
	public int getLarghezzaGriglia();

	public void saveTipoGriglia(int tipo);
	public int getTipoGriglia();

	public void saveOrigine(ICella2D origine);
	public void saveDestinazione(ICella2D destinazione);
	public ICella2D getOrigine();
	public ICella2D getDestinazione();

	public void incrementaCelleFrontiera();
	public int getQuantitaCelleFrontiera();

	public void incrementaIterazioniCondizione();
	public int getIterazioniCondizione();
	
	public void aggiungiPrestazione(String prestazione);
	public java.util.List<String> getPrestazioni();
	
	public void interrompiCalcolo();
	public boolean isCalcoloInterrotto();
	
	public String generaRiassunto(ICammino risultato);

	public void incrementaCacheHit();
	public int getCacheHit();

	public void setCache(boolean cahceStatus);
	public boolean isCacheAttiva();
	
	public void setFrontieraStored(boolean frontieraStored);
	public boolean isFrontieraStored();
	
	public void setCompitoTreMode(ConfigurationMode mode);
	public ConfigurationMode getCompitoTreMode();

	public void saveTime();

}
