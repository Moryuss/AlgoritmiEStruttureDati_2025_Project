package matteo;

import static nicolas.StatoCella.DESTINAZIONE;
import static nicolas.StatoCella.ORIGINE;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import francesco.IGriglia;
import nicolas.GrigliaConOrigineFactory;
import nicolas.ICella2;
import nicolas.IGrigliaConOrigine;
import nicolas.StatoCella;
import nicolas.Utils;


public class CompitoTreImplementation implements ICompitoTre, IHasReport, IHasProgressoMonitor, IInterrompibile{

	IStatisticheEsecuzione stats;	
	IProgressoMonitor monitor = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino
	IProgressoMonitor monitorMin = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino minimo

	private String report;
	private int livelloRicorsione = 0;

	public boolean debug = false;
	public boolean monitorON = true; 	//NON mettere a false, è legato al funzionamento degli interrupt.
	public boolean stopMessage = false;

	private boolean interrompiSuRichiesta = false;	// questo non va modificato da qui, ma da setTimeout(tempo)
	private boolean interrompiSuTempo = false;	

	private long tempoInizio;
	private long timeoutMillis; 

	private Deque<ILandmark> stackCammino = new ArrayDeque<ILandmark>();


	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2 O, ICella2 D) {

		stats = new StatisticheEsecuzione();
		monitor.setOrigine(O);
		monitor.setDestinazione(D);
		monitorMin.setOrigine(O);
		monitorMin.setDestinazione(D);

		stats.saveDimensioniGriglia(griglia.height(), griglia.width());
		stats.saveTipoGriglia(griglia.getTipo());
		stats.saveOrigine(O);
		stats.saveDestinazione(D);

		DESTINAZIONE.addTo(griglia.getCellaAt(D.x(), D.y()));
		ORIGINE.addTo(griglia.getCellaAt(D.x(), D.y()));


		try {
			ICammino risultato = camminoMinConStatistiche(griglia, O, D, stats);

			report = stats.generaRiassunto(risultato);

			return risultato;
		} catch (InterruptedException e) {

			if(stopMessage) System.out.println(e.getMessage());
			stats.interrompiCalcolo();
			if(this.getProgressMin().getCammino()!= null) {
				if(stopMessage) System.out.println("Cammino trovato");
				return this.getProgressMin().getCammino();
			}
			else if(this.getProgress().getCammino()!= null) {
				if(stopMessage) System.out.println("Cammino non trovato");
				return this.getProgress().getCammino();
			}
			else {
				if(stopMessage) System.out.println("Calcolo non andato a buon fine");
				return null;
			}
		}
	}

	public ICammino camminoMinConStatistiche(IGriglia<?> griglia, ICella2 O, ICella2 D, IStatisticheEsecuzione stats) throws InterruptedException {
		livelloRicorsione++;
		stats.incrementaIterazioniCondizione();

		this.checkInterruzione();

		ILandmark currentLandmark = new Landmark(
				StatoCella.LANDMARK.addTo(O.stato()),
				O.x(), O.y());


		stackCammino.push(currentLandmark);

		if(debug) System.out.println("Chiamata camminoMinConStatistiche livello " + livelloRicorsione);

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y());

		if(monitorON) {
			int distanzaTorre = g.getCellaAt(D.x(), D.y()).distanzaTorre();
			int distanzaAlfiere = g.getCellaAt(D.x(), D.y()).distanzaAlfiere();
			updateMonitor(distanzaTorre,distanzaAlfiere);
		}

		if (g.isInContesto(D.x(), D.y()) || g.isInComplemento(D.x(), D.y())) {
			if(monitorON) {
				int distanzaTorre = g.getCellaAt(D.x(), D.y()).distanzaTorre();
				int distanzaAlfiere = g.getCellaAt(D.x(), D.y()).distanzaAlfiere();
				updateMonitor(distanzaTorre,distanzaAlfiere);
				if(livelloRicorsione==1) {
					stackCammino.push(new Landmark(
							StatoCella.LANDMARK.addTo(O.stato()),
							D.x(), D.y()));
					updateMonitorMin(distanzaTorre,distanzaAlfiere);
				}
			}
			stackCammino.pop();
			livelloRicorsione--;

			return createPassoCammino(O, D, g);
		}


		List<ICella2> frontieraList = g.getFrontiera()
				.sorted(Comparator.comparingDouble(
						c -> Utils.distanzaLiberaTra(c, D)))
				.toList();

		if (frontieraList.isEmpty()) {
			if(monitorON) {
				int distanzaTorre = g.getCellaAt(D.x(), D.y()).distanzaTorre();
				int distanzaAlfiere = g.getCellaAt(D.x(), D.y()).distanzaAlfiere();
				updateMonitor(distanzaTorre,distanzaAlfiere);
				updateMonitorMin(distanzaTorre,distanzaAlfiere);
				
			}
			stackCammino.pop(); // rimuove punto corrente
			livelloRicorsione--;
			
			return passoInfinitoCammino();
		}


		//stats.aggiungiPrestazione("Frontiera di dimensione " + frontieraList.size() + " a livello " + livelloRicorsione);

		double lunghezzaMin = Double.POSITIVE_INFINITY;
		int lunghezzaTorreMin = Integer.MAX_VALUE;
		int lunghezzaAlfiereMin = Integer.MAX_VALUE;

		List<ILandmark> seqMin = new ArrayList<>();

		IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo());

		for (ICella2 F : frontieraList) {
			this.checkInterruzione();

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

						seqMin.clear();
						seqMin.add(new Landmark(StatoCella.ORIGINE.value(), O.x(), O.y()));
						seqMin.addAll(camminoFD.landmarks());			

						List<ILandmark> stackReversed = new ArrayList<>(stackCammino);
						Collections.reverse(stackReversed);

						List<ILandmark> union = new ArrayList<>();
						for(ILandmark landmark : stackReversed) {
							union.add(new Landmark(landmark.stato(), landmark.x(), landmark.y()));
						}
						// Evita duplicazioni del primo elemento
						for(int i = 1; i < seqMin.size(); i++) {
							ILandmark landmark = seqMin.get(i);
							union.add(new Landmark(landmark.stato(), landmark.x(), landmark.y()));
						}

						// Aggiorna monitorMin SOLO quando trova un nuovo minimo
						if(monitorON) {
							monitorMin.setCammino(new Cammino(
									lunghezzaTorreMin,
									lunghezzaAlfiereMin,
									new ArrayList<>(union))); 

							monitorMin.setCammino(new Cammino(
									lunghezzaTorreMin,
									lunghezzaAlfiereMin,
									new ArrayList<>(union))); 
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

		// Pop del punto corrente prima di uscire dalla ricorsione
		stackCammino.pop();
		livelloRicorsione--;


		return new Cammino(lunghezzaTorreMin, lunghezzaAlfiereMin, seqMin);


	}

	private ICammino passoInfinitoCammino() {
		return new Cammino(Integer.MAX_VALUE,
				Integer.MAX_VALUE,
				new ArrayList<>());
	}

	private ICammino createPassoCammino(ICella2 O, ICella2 D, IGrigliaConOrigine g) {
		int distanzaTorre = g.getCellaAt(D.x(), D.y()).distanzaTorre();
		int distanzaAlfiere = g.getCellaAt(D.x(), D.y()).distanzaAlfiere();


		return new Cammino(distanzaTorre,distanzaAlfiere,
				Arrays.asList(
						new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y()),
						new Landmark(StatoCella.LANDMARK.addTo(D.stato()), D.x(), D.y())
						));
	}

	private void updateMonitor(int distanzaTorre, int distanzaAlfiere) {

		List<ILandmark> percorsoCorrente = new ArrayList<>(stackCammino);
		Collections.reverse(percorsoCorrente);

		monitor.setCammino((new Cammino(
				distanzaTorre,
				distanzaAlfiere,
				percorsoCorrente)));
	}
	/**
	 * Utilizzato nei casi base, per gli edge case che non entrano nel ciclo for
	 * @param d 
	 * @param g 
	 */
	private void updateMonitorMin(int distanzaTorre, int distanzaAlfiere) {
		List<ILandmark> percorsoCorrente = new ArrayList<>(stackCammino);
		Collections.reverse(percorsoCorrente);

		monitorMin.setCammino(new Cammino(
				distanzaTorre,
				distanzaAlfiere,
				percorsoCorrente));
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

	@Override
	public void interrupt() {
		this.interrompiSuRichiesta = true;
	}

	@Override
	public void setTimeout(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
		this.tempoInizio = System.currentTimeMillis();
		this.interrompiSuTempo = true;
	}

	private void checkInterruzione() throws InterruptedException {
		if (interrompiSuRichiesta) {
			throw new InterruptedException("Interrotto su richiesta");
		}

		if (interrompiSuTempo && (System.currentTimeMillis() - tempoInizio) > timeoutMillis) {
			throw new InterruptedException("Timeout raggiunto");
		}
	}
}