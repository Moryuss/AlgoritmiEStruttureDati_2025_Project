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

	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D) {
		  // Calcola contesto di O
        IGrigliaConOrigine g = new CompitoDueImpl().calcola(griglia, O); //impl nicolas
        
        // Verifica se D è nel contesto
        if (g.isInContesto(D.x(), D.y())) {
            double distanza = g.distanzaLiberaDa(D.x(), D.y());
            return new Cammino(distanza, Arrays.asList(
                new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y(), 0),
                new Landmark(StatoCella.LANDMARK.value(), D.x(), D.y(), 1)
            )); 
            // QUI Vanno fatte delle modifiche:
            //1. probabilmente rimuovere completamente index
            //2. Fare che lo stato è l'OR degli stati
            
        }
        
        // Verifica se D è nel complemento
        if (g.isInComplemento(D.x(), D.y())) {
            double distanza = g.distanzaLiberaDa(D.x(), D.y());
            return new Cammino(distanza, Arrays.asList(
                new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y(), 0),
                new Landmark(StatoCella.LANDMARK.value(), D.x(), D.y(), 1)
           )); 
        }
        
        // Verifica se frontiera è vuota (vicolo cieco)
        List<ICella2D> frontieraList = g.getFrontiera().toList();	//controlla il tipo di ritorno,
        if (frontieraList.isEmpty()) {
            return new Cammino(Double.POSITIVE_INFINITY,
            		new ArrayList<>());
        }
        
        // Ricerca ricorsiva attraverso la frontiera
        double lunghezzaMin = Double.POSITIVE_INFINITY;
        List<ILandmark> seqMin = new ArrayList<>();
        
        // Crea griglia con chiusura come ostacolo
        IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo());
        
        for (ICella2 F : frontieraList) {	//ICella2 o ICella2D?
            double IF = g.distanzaLiberaDa(F.x(), F.y());	//Questi metodi funzionano con ICella2D
            
            if (IF < lunghezzaMin) {	//La cella di frontiera é raggiungibile
                // Ricorsione: cerca cammino da F a D
                ICammino camminoFD = camminoMin(g2, F, D);	
                double ITot = IF + camminoFD.lunghezza();
                
                if (ITot < lunghezzaMin) {
                    lunghezzaMin = ITot;
                    seqMin = new ArrayList<>();
                    seqMin.add(new Landmark(StatoCella.LANDMARK.value(),O.x(), O.y(), 0));
                    seqMin.add(new Landmark(StatoCella.LANDMARK.value(),F.x(), F.y(), 1));
                    seqMin.addAll(camminoFD.landmarks().subList(1, camminoFD.landmarks().size()));
                }
            }
        }
        
        return new Cammino(lunghezzaMin, seqMin);
    }
}

