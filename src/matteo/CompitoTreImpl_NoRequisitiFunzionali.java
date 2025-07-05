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


public class CompitoTreImpl_NoRequisitiFunzionali implements ICompitoTre {

	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D) {
		  // Calcola contesto di O
        IGrigliaConOrigine g = new CompitoDueImpl().calcola(griglia, O); //impl nicolas
        
        // Verifica se D è nel contesto (Caso base)
        if (g.isInContesto(D.x(), D.y())) {
            double distanza = g.distanzaLiberaDa(D.x(), D.y());
            return new Cammino(distanza, Arrays.asList(
                new Landmark(StatoCella.LANDMARK.value(),
                		O.x(), O.y()),
                new Landmark(StatoCella.LANDMARK.addTo(StatoCella.CONTESTO.value()),
                		D.x(), D.y())
            ));          
        }
        
        // Verifica se D è nel complemento (Caso base)
        if (g.isInComplemento(D.x(), D.y())) {
            double distanza = g.distanzaLiberaDa(D.x(), D.y());
            return new Cammino(distanza, Arrays.asList(
                new Landmark(StatoCella.LANDMARK.value(),
                		O.x(), O.y()),
                new Landmark(StatoCella.LANDMARK.addTo(StatoCella.COMPLEMENTO.value()),
                		D.x(), D.y())
           )); 
        }
        
        // Verifica se frontiera è vuota (vicolo cieco)
        List<ICella2D> frontieraList = g.getFrontiera().toList();	//controlla il tipo di ritorno, per ora è ICella2
        if (frontieraList.isEmpty()) {
            return new Cammino(Double.POSITIVE_INFINITY,
            		new ArrayList<>());
        }
        
        // Ricerca ricorsiva attraverso la frontiera
        double lunghezzaMin = Double.POSITIVE_INFINITY;
        List<ILandmark> seqMin = new ArrayList<>();
        
        // Crea griglia con chiusura come ostacolo
        IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo());
        
        for (ICella2D F : frontieraList) {		//qui potrebbe essere ICella2 invece
            double IF = g.distanzaLiberaDa(F.x(), F.y());
            
            if (IF < lunghezzaMin) {
                //RICORSIONE
                ICammino camminoFD = camminoMin(g2, F, D);
                double ITot = IF + camminoFD.lunghezza();
                
                if (ITot < lunghezzaMin) {
                    lunghezzaMin = ITot;
                    seqMin = new ArrayList<>();
                    seqMin.add(new Landmark(StatoCella.ORIGINE.value(), O.x(), O.y()));
                    seqMin.add(new Landmark(StatoCella.FRONTIERA.addTo(F.stato()), F.x(), F.y()));  //non sicuro si questo stato
                    
                    // Aggiungi i landmark dalla ricorsione (saltando il primo che dovrebbe essere l'ultimo della chaimata prima)
                    List<ILandmark> landmarksFromRecursion = camminoFD.landmarks();
                    if (!landmarksFromRecursion.isEmpty()) {
                        seqMin.addAll(landmarksFromRecursion.subList(1, landmarksFromRecursion.size()));
                    }
                }
            }
        }
        
        return new Cammino(lunghezzaMin, seqMin);
    }
}

