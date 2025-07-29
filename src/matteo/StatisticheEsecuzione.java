package matteo;

import java.util.ArrayList;
import java.util.List;

import francesco.ICella2D;

// Classe per raccogliere statistiche
public class StatisticheEsecuzione implements IStatisticheEsecuzione{

	private int width;
	private int height;
	private int tipoGriglia;

	private ICella2D origine;
	private ICella2D destinazione;

	private int totaleCelleFrontiera = 0;
	private int totaleIterazioniCondizione = 0;
	private int cacheHit = 0;
	private List<String> prestazioni = new ArrayList<>();
	
	private Long tempoInizio;
	private Long tempoTotaleNs;
	
	private boolean calcoloInterrotto = false;
	private boolean cacheAttiva = false;
	private boolean frontieraSotred = false;
	
	private ConfigurationMode compitoTreMode = ConfigurationMode.DEFAULT;
	

	@Override
	public void saveDimensioniGriglia(int h, int w) {
		this.width = w;
		this.height = h;
	}

	@Override
	public void saveTipoGriglia(int tipo) {
		this.tipoGriglia= tipo;
	}

	@Override
	public void saveOrigine(ICella2D origine) {
		this.origine = origine;
	}

	@Override
	public void saveDestinazione(ICella2D destinazione) {
		this.destinazione = destinazione;
	}

	public StatisticheEsecuzione() {
		this.tempoInizio = System.nanoTime();
	}
	@Override
	public void incrementaCelleFrontiera() {
		totaleCelleFrontiera++;
	}
	@Override
	public void incrementaIterazioniCondizione() {
		totaleIterazioniCondizione++;
	}
	@Override
	public void incrementaCacheHit() {
		this.cacheHit++;
	}

	@Override
	public void aggiungiPrestazione(String prestazione) {
		prestazioni.add(prestazione);
	}
	@Override
	public void interrompiCalcolo() {
		calcoloInterrotto = true;
	}
	@Override
	public void setCache(boolean cacheStatus) {
		this.cacheAttiva = cacheStatus;
	}
	@Override
	public void setFrontieraStored(boolean frontieraStored) {
		this.frontieraSotred = frontieraStored;
	}
	@Override
	public void setCompitoTreMode(ConfigurationMode mode) {
		this.compitoTreMode = mode;
	}
	
	@Override
	public String generaRiassunto(ICammino risultato) {
		if(this.tempoTotaleNs==null) tempoTotaleNs = System.nanoTime() - tempoInizio;
		String tempoFormattato = formatTempo(tempoTotaleNs);
		
		StringBuilder sb = new StringBuilder();
		sb.append("=== RIASSUNTO ESECUZIONE CAMMINOMIN ===\n");
		sb.append("Dimensioni griglia: Width = ").append(this.width).append(", Height = ").append(this.height).append("\n");
		sb.append("Tipo griglia: ").append(this.tipoGriglia).append("\n");
		sb.append("Origine: (").append(origine.x()).append(",").append(origine.y()).append(")").append("\n");
		sb.append("Destinazione: (").append(destinazione.x()).append(",").append(destinazione.y()).append(")").append("\n");
		sb.append("Modalità Compito Tre: ").append(compitoTreMode.getModeName()).append("\n");
		sb.append("Tempo di esecuzione: ").append(tempoFormattato).append("\n");
		sb.append("Totale celle di frontiera considerate: ").append(totaleCelleFrontiera).append("\n");
		sb.append("Totale iterazioni condizione (riga 16/17): ").append(totaleIterazioniCondizione).append("\n");
		sb.append("Calcolo interrotto: ").append(calcoloInterrotto ? "SI" : "NO").append("\n");
		sb.append("Cache attiva: ").append(cacheAttiva ? "SI" : "NO").append("\n");
		sb.append("Cache hit: ").append(cacheHit).append("\n");
		sb.append("Frontiera sorted: ").append(frontieraSotred ? "SI" : "NO").append("\n");
		if (risultato != null) {
			sb.append("Lunghezza cammino trovato: ").append(risultato.lunghezza()).append("\n");
			sb.append("Numero landmarks: ").append(risultato.landmarks().size()).append("\n");
			sb.append("Sequenza landmarks: ");
			for (ILandmark l : risultato.landmarks()) { 
				sb.append("(").append(l.x()).append(",").append(l.y()).append(")");
			}
			sb.append("\n");
		}

		//        sb.append("Prestazioni registrate:\n");
		//        for (String prestazione : prestazioni) {
		//            sb.append("  - ").append(prestazione).append("\n");
		//        }

		return sb.toString();
	}

	@Override
	public int getAltezzaGriglia() {
		return this.height;
	}

	@Override
	public int getLarghezzaGriglia() {
		return this.width;
	}

	@Override
	public int getTipoGriglia() {
		return this.tipoGriglia;
	}

	@Override
	public ICella2D getOrigine() {
		return this.origine;
	}

	@Override
	public ICella2D getDestinazione() {
		return this.destinazione;
	}

	@Override
	public int getQuantitaCelleFrontiera() {
		return this.totaleCelleFrontiera;
	}

	@Override
	public int getIterazioniCondizione() {
		return this.totaleIterazioniCondizione;
	}

	@Override
	public List<String> getPrestazioni() {
		return new ArrayList<>(this.prestazioni);
	}

	@Override
	public boolean isCalcoloInterrotto() {
		return this.calcoloInterrotto;
	}

	@Override
	public int getCacheHit() {
		return this.cacheHit;
	}

	@Override
	public boolean isCacheAttiva() {
		return this.cacheAttiva;
	}

	@Override
	public boolean isFrontieraStored() {
		return this.frontieraSotred;
	}

	@Override
	public ConfigurationMode getCompitoTreMode() {
		return this.compitoTreMode;
	}

	@Override
	public void saveTime() {
		this.tempoTotaleNs = System.nanoTime() - tempoInizio;
	}

	private String formatTempo(long tempoNs) {
	    StringBuilder tempoStr = new StringBuilder();
	    
	    // Sempre mostra i nanosecondi
	    tempoStr.append(tempoNs).append(" ns");
	    
	    // Se >= 1 millisecondo, aggiungi anche i millisecondi
	    if (tempoNs >= 1_000_000) {
	        double tempoMs = tempoNs / 1_000_000.0;
	        tempoStr.append(" (").append(String.format("%.3f", tempoMs)).append(" ms");
	        
	        // Se >= 1 secondo, aggiungi anche i secondi
	        if (tempoNs >= 1_000_000_000) {
	            double tempoS = tempoNs / 1_000_000_000.0;
	            tempoStr.append(" = ").append(String.format("%.3f", tempoS)).append(" s");
	        }
	        
	        tempoStr.append(")");
	    }
	    
	    return tempoStr.toString();
	}

	

}
