package matteo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IHave2DCoordinate;
import matteo.Strategies.StrategyBundle;
import matteo.Strategies.StrategyFactory;
import nicolas.*;
import utils.Utils;


public class CompitoTreImplementation implements ICompitoTre, IHasReport, IHasProgressoMonitor {

	private final CamminoCache pathCache = new CamminoCache(); // cache per i cammini già calcolati
	private final GestioneInterruzioni gestoreInterruzioni = new GestioneInterruzioni();
	private CamminoConfiguration config = ConfigurationMode.DEFAULT.toCamminoConfiguration(); // Configurazione del cammino

	private StrategyBundle strategies;

	IStatisticheEsecuzione stats;
	IProgressoMonitor monitor = new ProgressoMonitor(); // per monitorare l'evoluzione del cammino
	IProgressoMonitor monitorMin = new ProgressoMonitor(); // per monitorare l'evoluzione del cammino minimo

	private String report;
	private int livelloRicorsione = 0;
	private int maxProfonditaRicorsione = 0;

	private Deque<ILandmark> stackCammino = new ArrayDeque<ILandmark>();

	/**
	 * Constructor con configurazione di default
	 */
	public CompitoTreImplementation() {
		initializeStrategies();
	}

	/**
	 * Constructor che accetta una configurazione personalizzata. <br>
	 * Usa CamminoConfiguration.createDefault() per una configurazione di base.<br>
	 * Usa CamminoConfiguration.createDebugMode() per una configurazione che stampa
	 * a video i log.<br>
	 * Usa CamminoConfiguration.createPerformanceMode() per una configurazione che
	 * utilizza tutti i metodi a disposizione per diminuire il tempo di esecuzione.
	 * 
	 * @param config La configurazione da utilizzare
	 */
	public CompitoTreImplementation(CamminoConfiguration config) {
		this.config = config;
		initializeStrategies();
	}

	/**
	 * Constructor che accetta un ConfigurationMode
	 */
	public CompitoTreImplementation(ConfigurationMode mode) {
		this.config = mode.toCamminoConfiguration();
		initializeStrategies();
	}

	public void setConfiguration(CamminoConfiguration config) {
		this.config = config;
		initializeStrategies();
	}

	public CamminoConfiguration getConfiguration() {
		return config;
	}

	public void setConfiguration(ConfigurationMode mode) {
		this.config = mode.toCamminoConfiguration();
		initializeStrategies();
	}

	public CompitoTreImplementation with(ConfigurationFlag flag) {
		config = config.withFlag(flag);
		initializeStrategies();
		return this;
	}

	public CompitoTreImplementation without(ConfigurationFlag flag) {
		config = config.withoutFlag(flag);
		initializeStrategies();
		return this;
	}

	/**
	 * Inizializza le strategie in base alla configurazione corrente. Le strategie
	 * sono utilizzate per gestire il calcolo del cammino, la cache, il debug e
	 * altre funzionalità.
	 */
	private void initializeStrategies() {
		StrategyFactory factory = new StrategyFactory(pathCache);
		this.strategies = factory.createStrategies(config);
	}

	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D, ICompitoDue compitoDue) {

		stampaStatiOrigineDestinazione(O, D);
		inizializzaCalcolo(griglia, O, D, compitoDue);
		ICammino risultato = null;
		try {
			pushOrigineInStack(O);

			risultato = calcoloCamminoMin(griglia, O, D, stats, compitoDue);
			stampaStatoDestinazioneFinale(risultato);
			return risultato;
		} catch (InterruptedException e) {
			return gestisciInterruzione_GeneraCammino(e);
		} finally {
			finalizzaStatisticheEsecuzione(); // Salva il tempo di esecuzione
			// Genera sempre il report, anche in caso di interruzione
			generaReportAlways(risultato);
		}
	}

	private void pushOrigineInStack(ICella2D O) {
		stackCammino.push(new Landmark(StatoCella.LANDMARK.addTo(O.stato()), O.x(), O.y()));
	}

	private void generaReportAlways(ICammino risultato) {
		if (risultato != null)
			generaReportFinale(risultato);
		else if (this.getProgressMin() != null & this.getProgressMin().getCammino() != null
				& this.getProgressMin().getCammino().lunghezza() < Double.POSITIVE_INFINITY) {
			generaReportFinale(this.getProgressMin().getCammino());
		} else {
			generaReportFinale(new Cammino(Integer.MAX_VALUE, Integer.MAX_VALUE, new ArrayList<>()));
		}
	}

	private void finalizzaStatisticheEsecuzione() {
		stats.saveTime();
		stats.setMaxDepth(maxProfonditaRicorsione);
	}

	private void generaReportFinale(ICammino risultato) {
		report = stats.generaRiassunto(risultato);
	}

	private void stampaStatoDestinazioneFinale(ICammino risultato) {
		if (risultato != null && risultato.landmarks() != null && !risultato.landmarks().isEmpty()) {
			strategies.getDebugStrategy()
			.println("stato ultimo landmark: " + bitPrint(risultato.landmarks().getLast().stato()));
		} else {
			strategies.getDebugStrategy().println("Cammino nullo o senza landmark.");
		}
	}

	private void stampaStatiOrigineDestinazione(ICella2D O, ICella2D D) {
		strategies.getDebugStrategy().println("Origine e destinazione stati" + "\nOrigine: " + bitPrint(O.stato())
		+ "\nDestinazione: " + bitPrint(D.stato()));
	}

	private ICammino gestisciInterruzione_GeneraCammino(InterruptedException e) {
		strategies.getDebugStrategy().println(e.getMessage());

		stats.interrompiCalcolo();
		if (this.getProgressMin().getCammino() != null
				&& this.getProgressMin().getCammino().lunghezza() < Double.POSITIVE_INFINITY) {
			strategies.getDebugStrategy().println("Cammino trovato");
			return this.getProgressMin().getCammino();
		} else if (this.getProgress().getCammino() != null) {
			strategies.getDebugStrategy().println("Cammino non trovato");
			return this.getProgress().getCammino();
		} else {
			strategies.getDebugStrategy().println("Calcolo non andato a buon fine");
			return null;
		}
	}

	/**
	 * Inizializza le statistiche, il compitoDue, la modalità del compitoTre, la cache,
	 * i monitor e la frontiera. Salva le informazioni della griglia e inizializza lo
	 * stack del cammino.
	 * 
	 * @param griglia   La griglia su cui calcolare il cammino
	 * @param O        La cella di origine
	 * @param D        La cella di destinazione
	 * @param compitoDue Il compitoDue da usare per il calcolo del cammino
	 */
	private void inizializzaCalcolo(IGriglia<?> griglia, ICella2D O, ICella2D D, ICompitoDue compitoDue) {
		inizializzaStatistiche();
		inizializzaCompitoDue(compitoDue);
		inizializzaCompitoTreMode();
		inizializzaCache();
		inizializzaMonitors(O, D);
		inizializzazioneFrontiera();
		salvaInformazioniGriglia(griglia, O, D);
		inizializzaStackCammino();
	}

	private void inizializzaCompitoDue(ICompitoDue compitoDue) {
		stats.setNomeCompitoDue(compitoDue.name());
	}

	private void inizializzaStackCammino() {
		stackCammino.clear();
	}

	private void inizializzaCompitoTreMode() {
		stats.setCompitoTreMode(config);
	}

	private void salvaInformazioniGriglia(IGriglia<?> griglia, ICella2D O, ICella2D D) {
		stats.saveDimensioniGriglia(griglia.height(), griglia.width());
		stats.saveTipoGriglia(griglia.getTipo());
		stats.saveOrigine(O);
		stats.saveDestinazione(D);
	}

	private void inizializzaStatistiche() {
		stats = new StatisticheEsecuzione();
	}

	private void inizializzaMonitors(ICella2D O, ICella2D D) {
		monitor.setOrigine(O);
		monitor.setDestinazione(D);
		monitorMin.setOrigine(O);
		monitorMin.setDestinazione(D);
		monitorMin.setCammino(new Cammino(Integer.MAX_VALUE, Integer.MAX_VALUE, new ArrayList<>()));
	}

	private void inizializzaCache() {
		pathCache.setEnabled(config.isCacheEnabled());
		pathCache.setDebugMode(config.isDebugEnabled());
		stats.setCache(pathCache.isEnabled());
	}

	private void inizializzazioneFrontiera() {
		stats.setSvuotaFrontiera(config.isSvuotaFrontieraEnabled());
		stats.setFrontieraSorted(config.isSortedFrontieraEnabled());
	}

	private ICammino calcoloCamminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D, IStatisticheEsecuzione stats,
			ICompitoDue compitoDue) throws InterruptedException {

		ICammino risultatoCache = verificaPresenzaCamminoInCache(griglia, O, D);
		if (risultatoCache != null) {
			strategies.getDebugStrategy().println("Cammino trovato in cache");
			return risultatoCache;
		}

//		Thread.sleep(100);

		IGrigliaConOrigine g = compitoDue.calcola(griglia, O);
		ICella2D origine = g.getCellaAt(O.x(), O.y());
		ICella2D dest = g.getCellaAt(D.x(), D.y());

		preparaRicorsione(origine);

		if (StatoCella.COMPLEMENTO.is(dest) || StatoCella.CONTESTO.is(dest)) {
			strategies.getDebugStrategy().println("TROVATO UN PERCORSO");
		}

		strategies.getDebugStrategy().println("D: " + D.x() + "," + D.y() + " stato: " + bitPrint(D.stato()) + " dest: "
				+ dest.x() + "," + dest.y() + " stato: " + bitPrint(dest.stato()));


		if (condizioneCasoBase(g, dest)) {
			return gestisciCasoBase(origine, g, dest);
		}

		var pair = compitoDue.getGrigliaFrontieraPair(g, origine, dest);
		var g2 = pair.griglia();

		List<ICellaConDistanze> frontieraList = strategies.getFrontieraStrategy().
				getFrontiera(pair.frontiera(), dest)
				.toList();

		frontieraList = strategies.getSvuotaFrontieraStrategy().
				isFrontieraDaSvuotare(origine, frontieraList,
						monitorMin, stackCammino, stats);

		if (frontieraList.isEmpty()) {
			return gestisciCasoFrontieraVuota(g, origine, dest);
		}


		ICammino risultatoFinale = null;
		double lunghezzaMin = Double.POSITIVE_INFINITY;
		int lunghezzaTorreMin = Integer.MAX_VALUE;
		int lunghezzaAlfiereMin = Integer.MAX_VALUE;
		List<ILandmark> seqMin = new ArrayList<>();

		for (ICellaConDistanze F : frontieraList) {

			this.gestoreInterruzioni.checkInterruzione();

			stampaFrontieraStato(F);

			if (F.isNot(StatoCella.OSTACOLO)) {

				stats.incrementaCelleFrontiera();

				strategies.getDebugStrategy().println("Analizzo cella frontiera"
						+ "(" + F.x() + "," + F.y() + ")"+ " stato: " + bitPrint(F.stato()));

				double IF = F.distanzaDaOrigine();
				int IFdistanzaTorre = F.distanzaTorre();
				int IFdistanzaAlfiere = F.distanzaAlfiere();

				boolean condizioneSoddisfatta = strategies.getCondizioneStrategy().isSoddisfatta(F, dest, IF,
						lunghezzaMin);

				if (condizioneSoddisfatta) {

					strategies.getDebugStrategy().println("condizione 16/17 soddisfatta per cella "
							+ F.x() + "," + F.y() + ")"+ " stato: " + bitPrint(F.stato()));

					stackCammino.push(new Landmark(StatoCella.LANDMARK.addTo(F.stato()), F.x(), F.y())); //questo era il problema! >:3 so evil

					ICammino camminoFD = calcoloCamminoMin(g2, F, dest, stats, compitoDue);


					double ITot = IF + camminoFD.lunghezza();
					int ITotTorre = IFdistanzaTorre + camminoFD.lunghezzaTorre();
					int ITotAlfiere = IFdistanzaAlfiere + camminoFD.lunghezzaAlfiere();

					aggiornaMonitorConNuovaFrontiera(ITotTorre, ITotAlfiere);

					if (ITot < lunghezzaMin || (ITotTorre == lunghezzaTorreMin && ITotAlfiere == lunghezzaAlfiereMin
							&& camminoFD.landmarks().size() + 1 < seqMin.size())) {
						lunghezzaMin = ITot;
						lunghezzaTorreMin = ITotTorre;
						lunghezzaAlfiereMin = ITotAlfiere;

						aggiornaSequenzaMinima(O, seqMin, F, camminoFD);

						aggiornaMonitorMinimo(O, lunghezzaTorreMin, lunghezzaAlfiereMin, seqMin);
					}
					stackCammino.pop();

				} else {
					stats.incrementaIterazioniCondizione();
					// requisito funzionale: numero totale di volte in cui
					// la condizione alla riga 16/17 ha assunto il valore «falso»
				}
			}
		}

		livelloRicorsione--;

		strategies.getDebugStrategy().println("end");

		risultatoFinale = new Cammino(lunghezzaTorreMin, lunghezzaAlfiereMin, seqMin);
		// SALVA IL RISULTATO CALCOLATO NELLA CACHE
		strategies.getCacheStrategy().put(griglia, O, D, risultatoFinale);

		return risultatoFinale;
	}


	private static void aggiornaSequenzaMinima(ICella2D O, List<ILandmark> seqMin, ICella2D F, ICammino camminoFD) {
		seqMin.clear();
		seqMin.add(new Landmark(StatoCella.LANDMARK.addTo(O.stato()), O.x(), O.y()));
		seqMin.addAll(camminoFD.landmarks());

		if (camminoFD.landmarks().size() > 1) {
			seqMin.set(1, new Landmark(StatoCella.LANDMARK.addTo(F.stato()), F.x(), F.y()));
		}

	}

	private void aggiornaMonitorMinimo(ICella2D O, int lunghezzaTorreMin, int lunghezzaAlfiereMin,
			List<ILandmark> seqMin) {
		if (config.isMonitorEnabled() && O.sameCoordinateAs(monitor.getOrigine())) {
			monitorMin.setCammino(new Cammino(lunghezzaTorreMin, lunghezzaAlfiereMin, seqMin));
		}
	}


	private void stampaFrontieraStato(ICellaConDistanze F) {
		strategies.getDebugStrategy().println("Frontiera stato: " + bitPrint(F.stato()));
	}

	private ICammino gestisciCasoFrontieraVuota(IGriglia<?> griglia, ICella2D O, ICella2D dest) {
		livelloRicorsione--;

		if (config.isMonitorEnabled()) {
			monitor.setCammino(new Cammino(Integer.MAX_VALUE, Integer.MAX_VALUE, new ArrayList<>(stackCammino)));
		}

		strategies.getDebugStrategy().println("caso base infinity");

		ICammino risultato = new Cammino(Integer.MAX_VALUE, Integer.MAX_VALUE, new ArrayList<>());

		// Salva anche i risultati "infiniti" nella cache
		strategies.getCacheStrategy().put(griglia, O, dest, risultato);

		return risultato;
	}

	private ICammino gestisciCasoBase(ICella2D O, IGrigliaConOrigine g, ICella2D dest) {
		strategies.getDebugStrategy().println("caso base");

		int distanzaTorre = g.getCellaAt(dest.x(), dest.y()).distanzaTorre();
		int distanzaAlfiere = g.getCellaAt(dest.x(), dest.y()).distanzaAlfiere();

		ILandmark landmarkOrigine = new Landmark(StatoCella.LANDMARK.addTo(O.stato()), O.x(), O.y());

		ILandmark landmarkDestinazione = new Landmark(StatoCella.LANDMARK.addTo(dest.stato()), dest.x(), dest.y());

		if (config.isMonitorEnabled()) {
		    stackCammino.push(landmarkDestinazione);

		    List<ILandmark> percorsoCorrente = new ArrayList<>(stackCammino).reversed();
		    double distanza = percorsoCorrente.stream().collect(Utils.collectPairsToDouble(Utils::distanzaLiberaTra)).sum();
		    if (condizioneAggiornaMoniorMin(distanza)) {
		        aggiornaMonitorMinInCasoBase(percorsoCorrente, distanza);
		    }
		    stackCammino.pop();
		}
		livelloRicorsione--;

		ICammino risultato = new Cammino(distanzaTorre, distanzaAlfiere,
				Arrays.asList(landmarkOrigine, landmarkDestinazione));

		// Salva il risultato nella cache se la cache è abilitata
		strategies.getCacheStrategy().put(g, O, dest, risultato);

		return risultato;
	}

	private void aggiornaMonitorMinInCasoBase(List<ILandmark> percorso,double lunghezzaTot) {
		monitorMin.setCammino(new Cammino(0, 0, percorso, lunghezzaTot));
	}

	private boolean condizioneAggiornaMoniorMin(double distanza) {
		return monitorMin.getCammino()==null || 
				monitorMin.getCammino().lunghezza()>distanza;
	}

	private static boolean condizioneCasoBase(IGriglia<?> g, IHave2DCoordinate dest) {
		return StatoCella.CHIUSURA.check(g.getCellaAt(dest).stato());
	}

	private void aggiornaMonitorConNuovaFrontiera(int lTorre, int lAlfiere) {
		if (config.isMonitorEnabled()) {
			List<ILandmark> percorsoCorrente = new ArrayList<>(stackCammino);
			Collections.reverse(percorsoCorrente);

			monitor.setCammino(new Cammino(lTorre, lAlfiere, new ArrayList<>(percorsoCorrente)));
		}
	}

	private void preparaRicorsione(ICella2D O) throws InterruptedException {
		aumentaValoreRicorsione();
		this.gestoreInterruzioni.checkInterruzione();

		strategies.getDebugStrategy().println("Chiamata camminoMinConStatistiche livello " + livelloRicorsione);
	}

	private void aumentaValoreRicorsione() {
		livelloRicorsione++;
		if(livelloRicorsione > maxProfonditaRicorsione) {
			maxProfonditaRicorsione  = livelloRicorsione;
		}
	}

	private ICammino verificaPresenzaCamminoInCache(IGriglia<?> griglia, ICella2D O, ICella2D D) {
		// stampa tutti gli elementi in cache se debug abilitato
		pathCache.printCacheContents();

		// CACHE
		ICammino cached = strategies.getCacheStrategy().get(griglia, O, D);
		if (cached != null) {
			this.stats.incrementaCacheHit();
			return cached;
		}
		return null; // Se non c'è un cammino in cache, procedi con il calcolo
		// END CACHE CHECK
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
		this.gestoreInterruzioni.interrupt();
	}

	@Override
	public void setTimeout(long duration, TimeUnit unit) {
		gestoreInterruzioni.setTimeout(duration, unit);
	}

	private static String bitPrint(int numero) {
		return String.format("%8s", Integer.toBinaryString(numero)).replace(' ', '0');
	}

	/**
	 * Pulisce la cache, utile per test / gestione memoria
	 */
	public void clearCache() {
		pathCache.clear();
	}

	/**
	 * Restituisce statistiche della cache
	 */
	public int getCacheSize() {
		return pathCache.size();
	}

	@Override
	public IStatisticheEsecuzione getStatisticheEsecuzione() {
		return this.stats;
	}

}
