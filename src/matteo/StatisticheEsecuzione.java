package matteo;

import java.util.ArrayList;
import java.util.List;

import francesco.ICella;
import nicolas.ICella2;

// Classe per raccogliere statistiche
public class StatisticheEsecuzione implements IStatisticheEsecuzione{

	private int width;
	private int height;
	private int tipoGriglia;
	
	private ICella2 origine;
	private ICella2 destinazione;
	
    private int totaleCelleFrontiera = 0;
    private int totaleIterazioniCondizione = 0;
    private List<String> prestazioni = new ArrayList<>();
    private long tempoInizio;
    private boolean calcoloInterrotto = false;
    
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
	public void saveOrigine(ICella2 origine) {
    	this.origine = origine;
	}

	@Override
	public void saveDestinazione(ICella2 destinazione) {
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
    public void aggiungiPrestazione(String prestazione) {
        prestazioni.add(prestazione);
    }
    @Override
    public void interrompiCalcolo() {
        calcoloInterrotto = true;
    }
    @Override
    public String generaRiassunto(ICammino risultato) {
    	  long tempoTotaleNs = System.nanoTime() - tempoInizio;
          double tempoTotaleMs = tempoTotaleNs / 1_000_000.0;
        StringBuilder sb = new StringBuilder();
        sb.append("=== RIASSUNTO ESECUZIONE CAMMINOMIN ===\n");
        sb.append("Dimensioni griglia: Width = ").append(this.width).append(", Height = ").append(this.height).append("\n");
        sb.append("Tipo griglia: ").append(this.tipoGriglia).append("\n");
        sb.append("Origine: (").append(origine.x()).append(",").append(origine.y()).append(")").append("\n");
        sb.append("Destinazione: (").append(destinazione.x()).append(",").append(destinazione.y()).append(")").append("\n");
        sb.append("Tempo di esecuzione: ").append(tempoTotaleNs).append(" ns (").append(tempoTotaleMs).append(" ms)\n");
        sb.append("Totale celle di frontiera considerate: ").append(totaleCelleFrontiera).append("\n");
        sb.append("Totale iterazioni condizione (riga 16/17): ").append(totaleIterazioniCondizione).append("\n");
        sb.append("Calcolo interrotto: ").append(calcoloInterrotto ? "SI" : "NO").append("\n");
        
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

	
}
