package matteo;

import static nicolas.StatoCella.DESTINAZIONE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import nicolas.Cella2;
import nicolas.GrigliaConOrigineFactory;
import nicolas.ICella2;
import nicolas.IGrigliaConOrigine;
import matteo.Cammino;
import matteo.Landmark;
import nicolas.StatoCella;


public class CompitoTreImpl_NoRequisitiFunzionali implements ICompitoTre {
	private int livelloRicorsione = 0;
	private boolean debug = false;

	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2 O, ICella2 D) {
		livelloRicorsione++;
//		if(livelloRicorsione>100) {
//			if(debug) System.out.println("STOP ricorsione al livello " + livelloRicorsione);
//			return new Cammino(Double.POSITIVE_INFINITY,
//					new ArrayList<>());
//		}
		if(debug) System.out.println("chaiamta di camminoMinimo");

		//Creazione griglia
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y()); //impl nicolas
		DESTINAZIONE.addTo(g.getCellaAt(D.x(), D.y()));


		// Verifica se D è nel contesto (Caso base)
		if (g.isInContesto(D.x(), D.y())) {
			if(debug) System.out.println("Caso base: CONTESTO");
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
			
			if(debug) System.out.println("Caso base: COMPLEMENTO");
			
			double distanza = g.distanzaLiberaDa(D.x(), D.y());
			return new Cammino(distanza, Arrays.asList(
					new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y()),
					new Landmark(StatoCella.LANDMARK.addTo(StatoCella.COMPLEMENTO.value()), D.x(), D.y())
					));
		}

		//ordina la frontiera per distanza da origine
		List<ICella2> frontieraList = g.getFrontiera()
			    .sorted(Comparator.comparingDouble(ICella2::distanzaDaOrigine))
			    .toList();

		// Verifica se frontiera è vuota (vicolo cieco)	
		if (frontieraList.isEmpty()) {
			return new Cammino(Double.POSITIVE_INFINITY,
					new ArrayList<>());
		}

		// Ricerca ricorsiva attraverso la frontiera
		double lunghezzaMin = Double.POSITIVE_INFINITY;
		List<ILandmark> seqMin = new ArrayList<>();

		// Crea griglia con chiusura come ostacolo
		IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo());

		if(debug) {
			System.out.println("griglia");
			griglia.print();
			System.out.println("g2 == griglia + chiusura come ostacolo");
			g2.print();
		}
		for (ICella2 F : frontieraList) {		
			//Provo a fare il controllo a mano
			if(StatoCella.OSTACOLO.isNot(F.stato())) {
				if(debug) System.out.println(F.x() + "--" + F.y());

				double IF = F.distanzaDaOrigine();

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
		}

		return new Cammino(lunghezzaMin, seqMin);
	}
}

