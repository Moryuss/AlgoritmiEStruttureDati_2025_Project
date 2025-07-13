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
import nicolas.Utils;


public class CompitoTreImplementation implements ICompitoTre, IHasReport{

	StatisticheEsecuzione stats;
	String report;
	private int livelloRicorsione = 0;
	private boolean debug = false;

	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2 O, ICella2 D) {

		stats = new StatisticheEsecuzione();

		//aggiungere le stats della griglia in stats
		stats.saveDimensioniGriglia(griglia.height(), griglia.width());
		stats.saveTipoGriglia(griglia.getTipo());
		stats.saveOrigine(O);
		stats.saveDestinazione(D);
		
		ICammino risultato = camminoMinConStatistiche(griglia, O, D, stats);
		
		report = stats.generaRiassunto(risultato);
		//
		//Metti la distaza espressa come distanza torre + distanza  alfiere che è più facile da controllare la correttezza
		return risultato;
	}

	public ICammino camminoMinConStatistiche(IGriglia<?> griglia, ICella2 O, ICella2 D, StatisticheEsecuzione stats) {
		livelloRicorsione++;

		stats.incrementaIterazioniCondizione();

		if(debug) System.out.println("Chiamata camminoMinConStatistiche livello " + livelloRicorsione);

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y());
		DESTINAZIONE.addTo(g.getCellaAt(D.x(), D.y()));

		if (g.isInContesto(D.x(), D.y())) {
			double distanza = g.distanzaLiberaDa(D.x(), D.y());

			//stats.aggiungiPrestazione("Raggiunta DESTINAZIONE in CONTESTO a livello " + livelloRicorsione);

			return new Cammino(distanza, Arrays.asList(
					new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y()),
					new Landmark(StatoCella.LANDMARK.addTo(StatoCella.CONTESTO.value()), D.x(), D.y())
					));
		}

		if (g.isInComplemento(D.x(), D.y())) {
			double distanza = g.distanzaLiberaDa(D.x(), D.y());

			//stats.aggiungiPrestazione("Raggiunta DESTINAZIONE in COMPLEMENTO a livello " + livelloRicorsione);

			return new Cammino(distanza, Arrays.asList(
					new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y()),
					new Landmark(StatoCella.LANDMARK.addTo(StatoCella.COMPLEMENTO.value()), D.x(), D.y())
					));
		}

		List<ICella2> frontieraList = g.getFrontiera()
				.sorted(Comparator.comparingDouble(
						c -> Utils.distanzaLiberaTra(c, D)))
				.toList();

		if (frontieraList.isEmpty()) {

			//stats.aggiungiPrestazione("Frontiera vuota a livello " + livelloRicorsione);

			return new Cammino(Double.POSITIVE_INFINITY, new ArrayList<>());
		}

		//stats.aggiungiPrestazione("Frontiera di dimensione " + frontieraList.size() + " a livello " + livelloRicorsione);

		double lunghezzaMin = Double.POSITIVE_INFINITY;
		List<ILandmark> seqMin = new ArrayList<>();

		IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo());

		for (ICella2 F : frontieraList) {
			if (StatoCella.OSTACOLO.isNot(F.stato())) {

				stats.incrementaCelleFrontiera();

				if(debug) System.out.println("Analizzo cella frontiera (" + F.x() + "," + F.y() + ")");

				double IF = F.distanzaDaOrigine();

				if (IF < lunghezzaMin) {		//questa condizione può essere fatta diventare più forte
					ICammino camminoFD = camminoMinConStatistiche(g2, F, D, stats);
					double ITot = IF + camminoFD.lunghezza();

					if (ITot < lunghezzaMin) {
						lunghezzaMin = ITot;
						seqMin = new ArrayList<>();
						seqMin.add(new Landmark(StatoCella.ORIGINE.value(), O.x(), O.y()));
						seqMin.add(new Landmark(StatoCella.FRONTIERA.addTo(F.stato()), F.x(), F.y()));

						List<ILandmark> landmarksFromRecursion = camminoFD.landmarks();
						if (!landmarksFromRecursion.isEmpty()) {
							seqMin.addAll(landmarksFromRecursion.subList(1, landmarksFromRecursion.size()));
						}
					}
				}
				else {
					stats.incrementaIterazioniCondizione();
					//requisito funzionale: numero totale di volte in cui 
					//la condizione alla riga 16/17 ha assunto il valore «falso»
				}
			}
		}

		return new Cammino(lunghezzaMin, seqMin);
	}

	@Override
	public String getReport() {
		return this.report; 
	}
}