package matteo;

import java.util.ArrayList;
import java.util.List;

// Classe per raccogliere statistiche
public class StatisticheEsecuzione {
    private int totaleCelleFrontiera = 0;
    private int totaleIterazioniCondizione = 0;
    private List<String> prestazioni = new ArrayList<>();
    private long tempoInizio;
    private boolean calcoloInterrotto = false;
    
    public StatisticheEsecuzione() {
        this.tempoInizio = System.currentTimeMillis();
    }
    
    public void incrementaCelleFrontiera() {
        totaleCelleFrontiera++;
    }
    
    public void incrementaIterazioniCondizione() {
        totaleIterazioniCondizione++;
    }
    
    public void aggiungiPrestazione(String prestazione) {
        prestazioni.add(prestazione);
    }
    
    public void interrompiCalcolo() {
        calcoloInterrotto = true;
    }
    
    public String generaRiassunto(ICammino risultato) {
        long tempoTotale = System.currentTimeMillis() - tempoInizio;
        StringBuilder sb = new StringBuilder();
        sb.append("=== RIASSUNTO ESECUZIONE CAMMINOMIN ===\n");
        sb.append("Tempo di esecuzione: ").append(tempoTotale).append(" ms\n");
        sb.append("Totale celle di frontiera considerate: ").append(totaleCelleFrontiera).append("\n");
        sb.append("Totale iterazioni condizione (riga 16/17): ").append(totaleIterazioniCondizione).append("\n");
        sb.append("Calcolo interrotto: ").append(calcoloInterrotto ? "SI" : "NO").append("\n");
        
        if (risultato != null) {
            sb.append("Lunghezza cammino trovato: ").append(risultato.lunghezza()).append("\n");
            sb.append("Numero landmarks: ").append(risultato.landmarks().size()).append("\n");
            sb.append("Sequenza landmarks: ");
            for (ILandmark l : risultato.landmarks()) { 
                sb.append("(").append(l.x()).append(",").append(l.y()).append(",")
                  .append(l.stato()).append(") ");
            }
            sb.append("\n");
        }
        
        sb.append("Prestazioni registrate:\n");
        for (String prestazione : prestazioni) {
            sb.append("  - ").append(prestazione).append("\n");
        }
        
        return sb.toString();
    }
}
