package matteo.Riassunto;

import java.util.SequencedMap;

import francesco.ICella2D;
import matteo.CamminoConfiguration;
import matteo.ICammino;

public interface IStatisticheEsecuzione {

	public void saveDimensioniGriglia(int h, int w);
	public int getAltezzaGriglia();
	public int getLarghezzaGriglia();

	public void saveTipoGriglia(int tipo);
	public int getTipoGriglia();

	public void setOrigine(ICella2D origine);
	public void setDestinazione(ICella2D destinazione);
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
	
	public void incrementaCacheHit();
	public int getCacheHit();

	public void setCache(boolean cacheStatus);
	public boolean isCacheAttiva();
	
	public void setFrontieraSorted(boolean frontieraStored);
	public boolean isFrontieraSorted();
	
	public boolean isSvuotaFrontieraAttiva();
	public void setSvuotaFrontiera(boolean svuotaFrontiera);
	public void incrementaSvuotaFrontiera();
	public int getQuantitaSvuotaFrontiera();
	
	public void setCompitoTreMode(CamminoConfiguration mode);
	public CamminoConfiguration getCompitoTreMode();

	public void saveTime();
	
	public long getTempoEsecuzione();

	public void setMaxDepth(int maxDepth);
	public int getMaxDepth();
	
	public String getNomeCompitoDue();
	public void setNomeCompitoDue(String nomeCompitoDue);
	
	public void setCammino(ICammino cammino);
	public ICammino getCammino();
	
	@Deprecated
	public String generaRiassunto(ICammino risultato);
	
	public Riassunto generaRiassunto(TipoRiassunto tipoRiassunto);
	
	public SequencedMap<String,String> toSequencedMap();
}
