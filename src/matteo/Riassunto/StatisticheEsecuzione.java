package matteo.Riassunto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import francesco.ICella2D;
import matteo.CamminoConfiguration;
import matteo.ConfigurationMode;
import matteo.ICammino;
import utils.Utils;

// Classe per raccogliere statistiche
public class StatisticheEsecuzione implements IStatisticheEsecuzione {
	
	private ICammino cammino;
	
	private int width;
	private int height;
	private int tipoGriglia;
	private ICella2D origine;
	private ICella2D destinazione;
	private int totaleCelleFrontiera = 0;
	private int totaleIterazioniCondizione = 0;
	private int cacheHit = 0;
	private int totaleSvuotaFrontiera = 0;
	private List<String> prestazioni = new ArrayList<>();
	
	private int maxDepth = 0;
	private long tempoInizio = System.nanoTime();
	private Long tempoTotaleNs;
	
	private boolean calcoloInterrotto = false;
	private boolean cacheAttiva = false;
	private boolean frontieraSotred = false;
	private boolean svuotaFrontiera = false;
	
	private String compitoDueName = "Compito Due";
	private CamminoConfiguration compitoTreMode = ConfigurationMode.DEFAULT.toCamminoConfiguration();
	
	
	@Override
	public void saveDimensioniGriglia(int h, int w) {
		this.width = w;
		this.height = h;
	}
		
	@Override
	public void saveTipoGriglia(int tipo) {this.tipoGriglia= tipo;}
	@Override
	public void setOrigine(ICella2D origine) {this.origine = origine;}
	@Override
	public void setDestinazione(ICella2D destinazione) {this.destinazione = destinazione;}
	@Override
	public void incrementaCelleFrontiera() {totaleCelleFrontiera++;}
	@Override
	public void incrementaIterazioniCondizione() {totaleIterazioniCondizione++;}
	@Override
	public void incrementaCacheHit() {this.cacheHit++;}
	@Override
	public void aggiungiPrestazione(String prestazione) {prestazioni.add(prestazione);}
	@Override
	public void interrompiCalcolo() {calcoloInterrotto = true;}
	@Override
	public void setCache(boolean cacheStatus) {this.cacheAttiva = cacheStatus;}
	@Override
	public void setFrontieraSorted(boolean frontieraStored) {this.frontieraSotred = frontieraStored;}
	@Override
	public boolean isFrontieraSorted() {return this.frontieraSotred;}
	@Override
	public void setCompitoTreMode(CamminoConfiguration mode) {this.compitoTreMode = mode;}
	@Override
	public int getAltezzaGriglia() {return this.height;}
	@Override
	public int getLarghezzaGriglia() {return this.width;}
	@Override
	public int getTipoGriglia() {return this.tipoGriglia;}
	@Override
	public ICella2D getOrigine() {return this.origine;}
	@Override
	public ICella2D getDestinazione() {return this.destinazione;}
	@Override
	public int getQuantitaCelleFrontiera() {return this.totaleCelleFrontiera;}
	@Override
	public int getIterazioniCondizione() {return this.totaleIterazioniCondizione;}
	@Override
	public List<String> getPrestazioni() {return new ArrayList<>(this.prestazioni);}
	@Override
	public boolean isCalcoloInterrotto() {return this.calcoloInterrotto;}
	@Override
	public int getCacheHit() {return this.cacheHit;}
	@Override
	public boolean isCacheAttiva() {return this.cacheAttiva;}
	@Override
	public boolean isSvuotaFrontieraAttiva() {return this.svuotaFrontiera;}
	@Override
	public void setSvuotaFrontiera(boolean svuotaFrontiera) {this.svuotaFrontiera = svuotaFrontiera;}
	@Override
	public void incrementaSvuotaFrontiera() {this.totaleSvuotaFrontiera++;}
	@Override
	public CamminoConfiguration getCompitoTreMode() {return this.compitoTreMode;}
	@Override
	public int getQuantitaSvuotaFrontiera() {return this.totaleSvuotaFrontiera;}
	@Override
	public void setMaxDepth(int maxDepth) {this.maxDepth = maxDepth;}
	@Override
	public int getMaxDepth() {return this.maxDepth;}
	@Override
	public String getNomeCompitoDue() {return this.compitoDueName;}
	@Override
	public void setNomeCompitoDue(String nomeCompitoDue) {this.compitoDueName = nomeCompitoDue;}
	@Override
	public void setCammino(ICammino cammino) {this.cammino = cammino;}
	@Override
	public ICammino getCammino() {return this.cammino;}
	@Override
	public void saveTime() {this.tempoTotaleNs = System.nanoTime() - tempoInizio;}
	@Override
	public long getTempoEsecuzione() {
		if(this.tempoTotaleNs==null) saveTime();
		return this.tempoTotaleNs;
	}
	
	
	@Override
	public Riassunto generaRiassunto(TipoRiassunto tipoRiassunto) {
		return RiassuntoFactory.creaRiassunto(tipoRiassunto, this);
	}
	
	@Deprecated
	@Override
	public String generaRiassunto(ICammino risultato) {
		if(this.tempoTotaleNs==null) tempoTotaleNs = System.nanoTime() - tempoInizio;
		return this.generaRiassunto(TipoRiassunto.VERBOSE).toString();
	}
	
	
	@Override
	public SequencedMap<String, String> toSequencedMap() {
		var map = new LinkedHashMap<String,String>();
		map.put("Dimensioni griglia", getLarghezzaGriglia() + "x" + getAltezzaGriglia());
		map.put("Tipo griglia", getOrigine().x() + "," + getOrigine().y() + ")");
		map.put("Origine", "(" + getOrigine().x() + "," + getOrigine().y() + ")");
		map.put("Destinazione", "(" + getDestinazione().x() + "," + getDestinazione().y() + ")");
		map.put("Tempo esecuzione", Utils.formatTempo(getTempoEsecuzione()));
		map.put("Celle frontiera", String.valueOf(getQuantitaCelleFrontiera()));
		map.put("Iterazioni condizione", String.valueOf(getIterazioniCondizione()));
		map.put("Cache hit", String.valueOf(getCacheHit()));
		map.put("Max depth", String.valueOf(getMaxDepth()));
		
		if (getCammino() != null) {
			map.put("Lunghezza torre", String.valueOf(getCammino().lunghezzaTorre()));
			map.put("Lunghezza alfiere", String.valueOf(getCammino().lunghezzaAlfiere()));
			map.put("Lunghezza cammino", String.valueOf(getCammino().lunghezza()));
			map.put("Landmarks", String.valueOf(getCammino().landmarks().size()));
		}
		
		return map;
	}
	
}
