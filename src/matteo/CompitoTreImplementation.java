package matteo;

import static nicolas.StatoCella.DESTINAZIONE;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
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


public class CompitoTreImplementation implements ICompitoTre, IHasReport, IHasProgressoMonitor{

	IStatisticheEsecuzione stats;	
	IProgressoMonitor monitor = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino
	IProgressoMonitor monitorMin = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino minimo

	private String report;
	private int livelloRicorsione = 0;
	private boolean debug = false;
	private boolean monitorON = true;
	private Deque<ILandmark> stackCammino = new ArrayDeque<ILandmark>();


	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2 O, ICella2 D) {

		stats = new StatisticheEsecuzione();
		monitor.setOrigine(O);
		monitor.setDestinazione(D);

		stats.saveDimensioniGriglia(griglia.height(), griglia.width());
		stats.saveTipoGriglia(griglia.getTipo());
		stats.saveOrigine(O);
		stats.saveDestinazione(D);

		ICammino risultato = camminoMinConStatistiche(griglia, O, D, stats);

		report = stats.generaRiassunto(risultato);

		return risultato;
	}

	public ICammino camminoMinConStatistiche(IGriglia<?> griglia, ICella2 O, ICella2 D, IStatisticheEsecuzione stats) {
		livelloRicorsione++;
		stats.incrementaIterazioniCondizione();

		stackCammino.push(new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y()));


		if(debug) System.out.println("Chiamata camminoMinConStatistiche livello " + livelloRicorsione);

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y());
		DESTINAZIONE.addTo(g.getCellaAt(D.x(), D.y()));

		if (g.isInContesto(D.x(), D.y())) {

			//double distanza = g.distanzaLiberaDa(D.x(), D.y());
			int distanzaTorre = g.getCellaAt(D.x(), D.y()).distanzaTorre();
			int distanzaAlfiere = g.getCellaAt(D.x(), D.y()).distanzaAlfiere();

			livelloRicorsione--;
			if(monitorON) {
				monitor.setCammino(new Cammino(
						distanzaTorre,
						distanzaAlfiere,
						new ArrayList<>(stackCammino)));


				stackCammino.pop();
			}

			return new Cammino(distanzaTorre,distanzaAlfiere,
					Arrays.asList(
							new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y()),
							new Landmark(StatoCella.LANDMARK.addTo(StatoCella.CONTESTO.value()), D.x(), D.y())
							));
		}

		if (g.isInComplemento(D.x(), D.y())) {

			//double distanza = g.distanzaLiberaDa(D.x(), D.y());
			int distanzaTorre = g.getCellaAt(D.x(), D.y()).distanzaTorre();
			int distanzaAlfiere = g.getCellaAt(D.x(), D.y()).distanzaAlfiere();

			livelloRicorsione--;

			if(monitorON) {
				monitor.setCammino(new Cammino(
						distanzaTorre,
						distanzaAlfiere,
						new ArrayList<>(stackCammino)));


				stackCammino.pop();
			}

			return new Cammino(distanzaTorre, distanzaAlfiere,
					Arrays.asList(
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

			livelloRicorsione--;

			if(monitorON) {
				monitor.setCammino(new Cammino(Integer.MAX_VALUE,
						Integer.MAX_VALUE, 
						new ArrayList<>(stackCammino)));

				stackCammino.pop();
			}

			return new Cammino(Integer.MAX_VALUE,
					Integer.MAX_VALUE,
					new ArrayList<>());
		}

		//stats.aggiungiPrestazione("Frontiera di dimensione " + frontieraList.size() + " a livello " + livelloRicorsione);

		double lunghezzaMin = Double.POSITIVE_INFINITY;
		int lunghezzaTorreMin = Integer.MAX_VALUE;
		int lunghezzaAlfiereMin = Integer.MAX_VALUE;

		List<ILandmark> seqMin = new ArrayList<>();

		IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo());

		for (ICella2 F : frontieraList) {
			if (StatoCella.OSTACOLO.isNot(F.stato())) {

				stats.incrementaCelleFrontiera();

				if(debug) System.out.println("Analizzo cella frontiera (" + F.x() + "," + F.y() + ")");


				int IFdistanzaTorre = g.getCellaAt(F.x(), F.y()).distanzaTorre();
				int IFdistanzaAlfiere = g.getCellaAt(F.x(), F.y()).distanzaAlfiere();
				double IF = F.distanzaDaOrigine();

				if (IF < lunghezzaMin) {		//questa condizione può essere fatta diventare più forte
					ICammino camminoFD = camminoMinConStatistiche(g2, F, D, stats);
					double ITot = IF + camminoFD.lunghezza();
					int ITotTorre = IFdistanzaTorre + camminoFD.lunghezzaTorre();
					int ITotAlfiere = IFdistanzaAlfiere + camminoFD.lunghezzaAlfiere();

					if (ITot < lunghezzaMin) {
						lunghezzaMin = ITot;
						lunghezzaTorreMin = ITotTorre;
						lunghezzaAlfiereMin = ITotAlfiere;

						seqMin = new ArrayList<>();
						seqMin.add(new Landmark(StatoCella.ORIGINE.value(), O.x(), O.y()));
						seqMin.add(new Landmark(StatoCella.FRONTIERA.addTo(F.stato()), F.x(), F.y()));

						List<ILandmark> landmarksFromRecursion = camminoFD.landmarks();
						if (!landmarksFromRecursion.isEmpty()) {
							seqMin.addAll(landmarksFromRecursion.subList(1, landmarksFromRecursion.size()));
						}
						
						if(monitorON) {
						monitorMin.setCammino(new Cammino(
								lunghezzaTorreMin,
								lunghezzaAlfiereMin,
								new ArrayList<>(stackCammino)));
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

		livelloRicorsione--;
		if(monitorON) {
			monitor.setCammino(new Cammino(
					lunghezzaTorreMin,
					lunghezzaAlfiereMin,
					new ArrayList<>(stackCammino)));
			
			
			stackCammino.pop();
		}

		return new Cammino(lunghezzaTorreMin, lunghezzaAlfiereMin, seqMin);
	}

	@Override
	public String getReport() {
		return this.report; 
	}

	@Override
	public IProgressoMonitor getProgress() {
		return this.monitor;
	}
	
	@Override
	public IProgressoMonitor getProgressMin() {
		return this.monitorMin;
	}
	
}