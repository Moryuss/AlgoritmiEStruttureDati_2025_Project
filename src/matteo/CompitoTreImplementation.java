package matteo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import nicolas.ICella2;
import nicolas.IGrigliaConOrigine;
import matteo.Cammino;
import matteo.Landmark;
import francesco.StatoCella;

public class CompitoTreImplementation implements ICompitoTre {
    
   
    // Interfaccia per controllo interruzione
    public interface IControlloInterruzione {
        boolean deveInterrompere();
        void setDurataMaxMs(long durataMaxMs);
    }
    
    // Implementazione semplice del controllo interruzione
    public static class ControlloInterruzioneImpl implements IControlloInterruzione {
        private volatile boolean interrupted = false;
        private long durataMaxMs = Long.MAX_VALUE;
        private long inizioCalcolo = System.currentTimeMillis();
        
        @Override
        public boolean deveInterrompere() {
            if (interrupted) return true;
            if (System.currentTimeMillis() - inizioCalcolo > durataMaxMs) {
                interrupted = true;
                return true;
            }
            return false;
        }
        
        @Override
        public void setDurataMaxMs(long durataMaxMs) {
            this.durataMaxMs = durataMaxMs;
        }
        
        public void interrompi() {
            interrupted = true;
        }
    }
    
    private StatisticheEsecuzione statistiche;
    private IControlloInterruzione controlloInterruzione;
    
    @Override
    public ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D) {
        // Inizializza monitoring
        statistiche = new StatisticheEsecuzione();
        controlloInterruzione = new ControlloInterruzioneImpl();
        
        System.out.println("=== INIZIO CALCOLO CAMMINOMIN ===");
        System.out.println("Origine: (" + O.x() + "," + O.y() + ")");
        System.out.println("Destinazione: (" + D.x() + "," + D.y() + ")");
        System.out.println("Dimensioni griglia: " + "Altezza: " + griglia.height() +
        		"; Larghezza:" + griglia.width());
        
        try {
            ICammino risultato = camminoMinInternal(griglia, O, D, 0);
            
            // Stampa riassunto
            System.out.println(statistiche.generaRiassunto(risultato));
            
            return risultato;
            
        } catch (InterruptedException e) {
            statistiche.interrompiCalcolo();
            System.out.println("CALCOLO INTERROTTO!");
            System.out.println(statistiche.generaRiassunto(null));
            return new Cammino(Double.POSITIVE_INFINITY, new ArrayList<>());
        }
    }
    
    // Metodo pubblico per permettere interruzione esterna
    public void interrompiCalcolo() {
        if (controlloInterruzione instanceof ControlloInterruzioneImpl) {
            ((ControlloInterruzioneImpl) controlloInterruzione).interrompi();
        }
    }
    
    // Metodo pubblico per impostare timeout
    public void setTimeoutMs(long timeoutMs) {
        if (controlloInterruzione != null) {
            controlloInterruzione.setDurataMaxMs(timeoutMs);
        }
    }
    
    private ICammino camminoMinInternal(IGriglia griglia, ICella2D O, ICella2D D, int livelloRicorsione) throws InterruptedException {
        // Controlla se deve interrompere
        if (controlloInterruzione.deveInterrompere()) {
            throw new InterruptedException("Calcolo interrotto");
        }
        
        String indentazione = "  ".repeat(livelloRicorsione);
        System.out.println(indentazione + "Calcolando cammino da (" + O.x() + "," + O.y() + ") a (" + D.x() + "," + D.y() + ")");
        
        // Calcola contesto di O
        IGrigliaConOrigine g = new CompitoDueImpl().calcola(griglia, O);
        statistiche.aggiungiPrestazione("Calcolato contesto per (" + O.x() + "," + O.y() + ")");
        
        // Verifica se D è nel contesto
        if (g.isInContesto(D.x(), D.y())) {
            double distanza = g.distanzaLiberaDa(D.x(), D.y());
            System.out.println(indentazione + "CASO BASE: D nel contesto, distanza=" + distanza);
            
            ICammino risultato = new Cammino(distanza, Arrays.asList(
                    new Landmark(StatoCella.LANDMARK.value(),
                    		O.x(), O.y()),
                    new Landmark(StatoCella.LANDMARK.addTo(StatoCella.CONTESTO.value()),
                    		D.x(), D.y())
                ));          
            
            statistiche.aggiungiPrestazione("Caso base: D nel contesto");
            return risultato;
        }
        
        // Verifica se D è nel complemento
        if (g.isInComplemento(D.x(), D.y())) {
            double distanza = g.distanzaLiberaDa(D.x(), D.y());
            System.out.println(indentazione + "CASO BASE: D nel complemento, distanza=" + distanza);
            
            ICammino risultato = new Cammino(distanza, Arrays.asList(
                    new Landmark(StatoCella.LANDMARK.value(),
                    		O.x(), O.y()),
                    new Landmark(StatoCella.LANDMARK.addTo(StatoCella.COMPLEMENTO.value()),
                    		D.x(), D.y())
               )); 
            
            statistiche.aggiungiPrestazione("Caso base: D nel complemento");
            return risultato;
        }
        
        // Verifica se frontiera è vuota
        List<ICella2D> frontieraList = g.getFrontiera().toList();
        if (frontieraList.isEmpty()) {
            System.out.println(indentazione + "VICOLO CIECO: frontiera vuota");
            statistiche.aggiungiPrestazione("Vicolo cieco rilevato");
            return new Cammino(Double.POSITIVE_INFINITY, new ArrayList<>());
        }
        
        System.out.println(indentazione + "Frontiera trovata con " + frontieraList.size() + " celle");
        statistiche.incrementaCelleFrontiera();		//devo alzarlo di un numero pari alla lista .size 
        
        // Ricerca ricorsiva
        double lunghezzaMin = Double.POSITIVE_INFINITY;
        List<Landmark> seqMin = new ArrayList<>();
        
        IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo());
        
        for (ICella2D F : frontieraList) {
            // Controlla interruzione ad ogni iterazione
            if (controlloInterruzione.deveInterrompere()) {
                throw new InterruptedException("Calcolo interrotto durante iterazione frontiera");
            }
            
            double IF = g.distanzaLiberaDa(F.x(), F.y());
            System.out.println(indentazione + "Valutando cella frontiera (" + F.x() + "," + F.y() + "), distanza=" + IF);
            
            if (IF < lunghezzaMin) {
                statistiche.incrementaIterazioniCondizione();
                System.out.println(indentazione + "Condizione IF < lunghezzaMin soddisfatta, procedo con ricorsione");
                
                // Ricorsione
                ICammino camminoFD = camminoMinInternal(g2, F, D, livelloRicorsione + 1);
                double ITot = IF + camminoFD.lunghezza();
                
                System.out.println(indentazione + "Ritorno da ricorsione: lunghezza=" + camminoFD.lunghezza() + ", totale=" + ITot);
                
                if (ITot < lunghezzaMin) {
                    lunghezzaMin = ITot;
                    seqMin = new ArrayList<>();
                    seqMin.add(new Landmark(StatoCella.ORIGINE.value(), O.x(), O.y()));
                    seqMin.add(new Landmark(StatoCella.FRONTIERA.value(), F.x(), F.y()));
                    
                    List<Landmark> landmarksFromRecursion = camminoFD.landmarks();
                    if (!landmarksFromRecursion.isEmpty()) {
                        seqMin.addAll(landmarksFromRecursion.subList(1, landmarksFromRecursion.size()));
                    }
                    
                    System.out.println(indentazione + "NUOVO MINIMO trovato: " + lunghezzaMin);
                    statistiche.aggiungiPrestazione("Nuovo minimo trovato: " + lunghezzaMin + " via (" + F.x() + "," + F.y() + ")");
                }
            }
        }
        
        System.out.println(indentazione + "Completato livello, lunghezza finale: " + lunghezzaMin);
        return new Cammino(lunghezzaMin, seqMin);
    }
}