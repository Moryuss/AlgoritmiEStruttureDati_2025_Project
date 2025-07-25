package matteo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IHave2DCoordinate;
import nicolas.*;
import utils.Utils;

public class CompitoTreImplementation implements ICompitoTre, IHasReport, IHasProgressoMonitor, IInterrompibile{
	
	private final CamminoCache pathCache = new CamminoCache(); //cache per i cammini già calcolati
	private final GestioneInterruzioni gestoreInterruzioni = new GestioneInterruzioni();	
	private CamminoConfiguration config = ConfigurationMode.DEFAULT.toCamminoConfiguration(); // Configurazione del cammino
	
	IStatisticheEsecuzione stats;	
	IProgressoMonitor monitor = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino
	IProgressoMonitor monitorMin = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino minimo
	
	private String report;
	private int livelloRicorsione = 0;
	
	private Deque<ILandmark> stackCammino = new ArrayDeque<ILandmark>();
	
	
	/**
	 * Constructor con configurazione di default
	 */
	public CompitoTreImplementation() {
		
	}
	/**
	 * Constructor che accetta una configurazione personalizzata. <br>
	 * Usa CamminoConfiguration.createDefault() per una configurazione di base.<br>
	 * Usa CamminoConfiguration.createDebugMode() per una configurazione che stampa a video i log.<br>
	 * Usa CamminoConfiguration.createPerformanceMode() per una configurazione che utilizza tutti 
	 * i metodi a disposizione per diminuire il tempo di esecuzione.
	 * @param config La configurazione da utilizzare
	 */
	public CompitoTreImplementation(CamminoConfiguration config) {
		this.config = config;
	}
	
	
	public void setConfiguration(CamminoConfiguration config) {
		this.config = config;
	}
	public CamminoConfiguration getConfiguration() {
		return config;
	}
	
	
	public void setConfiguration(ConfigurationMode mode) {
		this.config = mode.toCamminoConfiguration();
	}
	
	
	public CompitoTreImplementation with(ConfigurationFlag flag) {
		config = config.withFlag(flag);
		return this;
	}
	public CompitoTreImplementation without(ConfigurationFlag flag) {
		config = config.withoutFlag(flag);
		return this;
	}
	
	
	
	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D, ICompitoDue compitoDue) {
		
		stampaStatiOrigineDestinazione(O, D);
		inizializzaCalcolo(griglia, O, D);
		ICammino risultato = null;
		try {
			risultato = calcoloCamminoMin(griglia, O, D, stats, compitoDue);
//			generaReportFinale(risultato);
			stampaStatoDestinazioneFinale(risultato);
			return risultato;
		} catch (InterruptedException e) {
			return gestisciInterruzione(e);
		}finally {
			// Genera sempre il report, anche in caso di interruzione
			if (risultato!=null) generaReportFinale(risultato);
			else if (this.getProgressMin().getCammino().lunghezza()<Integer.MAX_VALUE) {
				generaReportFinale(this.getProgressMin().getCammino());
			}
			else {
				generaReportFinale(new Cammino(Integer.MAX_VALUE, Integer.MAX_VALUE, new ArrayList<>()));
			}
		}
	}
	private void generaReportFinale(ICammino risultato) {
		report = stats.generaRiassunto(risultato);
	}
	private void stampaStatoDestinazioneFinale(ICammino risultato) {
		if (config.isStateCheckEnabled()) {
			System.out.println("stato destinazione");
			bitPrint(risultato.landmarks().getLast().stato());
		}
	}
	private void stampaStatiOrigineDestinazione(ICella2D O, ICella2D D) {
		if (config.isStateCheckEnabled()) {
			System.out.println("Origine e destinazione stati");
			this.bitPrint(O.stato());
			this.bitPrint(D.stato());
		}
	}

	private ICammino gestisciInterruzione(InterruptedException e) {
		if (config.isStopMessageEnabled()) System.out.println(e.getMessage());

		stats.interrompiCalcolo();
		if (this.getProgressMin().getCammino()!=null && 
				this.getProgressMin().getCammino().lunghezza() < Integer.MAX_VALUE) {
			if (config.isStopMessageEnabled()) System.out.println("Cammino trovato");
			return this.getProgressMin().getCammino();
		}
		else if (this.getProgress().getCammino()!=null) {
			if (config.isStopMessageEnabled()) System.out.println("Cammino non trovato");
			return this.getProgress().getCammino();
		}
		else {
			if (config.isStopMessageEnabled()) System.out.println("Calcolo non andato a buon fine");
			return null;
		}
	}
	
	private void inizializzaCalcolo(IGriglia<?> griglia, ICella2D O, ICella2D D) {
		inizializzaStatistiche();
		inizializzaCache();
		inizializzaMonitors(O, D);
		salvaInformazioniGriglia(griglia, O, D);
	}
	private void salvaInformazioniGriglia(IGriglia<?> griglia, ICella2D O, ICella2D D) {
		stats.saveDimensioniGriglia(griglia.height(), griglia.width());
		stats.saveTipoGriglia(griglia.getTipo());
		stats.saveOrigine(O);
		stats.saveDestinazione(D);
		
		stats.setFrontieraStored(config.isSortedFrontieraEnabled());
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
	
	
	private ICammino calcoloCamminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D, 
			IStatisticheEsecuzione stats,
			ICompitoDue compitoDue) throws InterruptedException {

		ICammino risultatoCache = verificaPresenzaCamminoInCache(griglia, O, D);
		if (risultatoCache != null) {
			if (config.isDebugEnabled()) System.out.println("Cammino trovato in cache");
			return risultatoCache;
		}
		
		preparaRicorsione(O);
		
		IGrigliaConOrigine g = creaGriglia(griglia, O, D, compitoDue);
		
		ICella2D dest = prendiCellaDestinazione(D, g);
		
		aggiornaMonitor(g, dest);
		
		if (condizioneCasoBase(g, dest)) {
			return gestisciCasoBase(O, g, dest);
		}
		
		List<ICellaConDistanze> frontieraList = ottieniFrontiera(g, dest);
		
		if (frontieraList.isEmpty()) {
			return gestisciCasoFrontieraVuota(griglia, O, dest);
		}
		
		double lunghezzaMin = Double.POSITIVE_INFINITY;
		int lunghezzaTorreMin = Integer.MAX_VALUE;
		int lunghezzaAlfiereMin = Integer.MAX_VALUE;
		List<ILandmark> seqMin = new ArrayList<>();
		
		IGriglia<?> g2 = g.addObstacle(g.convertiChiusuraInOstacolo(), 0);
		
		for (ICellaConDistanze F : frontieraList) {
			this.gestoreInterruzioni.checkInterruzione();
			
			stampaFrontieraStato(F);
			
			if (isCellaFrontieraNonOstacolo(F)) {
				
				stats.incrementaCelleFrontiera();
				
				if (config.isDebugEnabled()) System.out.println("Analizzo cella frontiera (" + F.x() + "," + F.y() + ")");
				
				int IFdistanzaTorre = g.getCellaAt(F.x(), F.y()).distanzaTorre();
				int IFdistanzaAlfiere = g.getCellaAt(F.x(), F.y()).distanzaAlfiere();
				double IF = F.distanzaDaOrigine();
				
				boolean condizioneSoddisfatta = condizioneSoddisfattaDaFrontiera(dest, lunghezzaMin, F, IF);
				
				if (condizioneSoddisfatta) {
					//System.out.println("condizione 16/17 triggerata");
					ICammino camminoFD = calcoloCamminoMin(g2, F, dest, stats, compitoDue);
					
					double ITot = IF + camminoFD.lunghezza();
					int ITotTorre = IFdistanzaTorre + camminoFD.lunghezzaTorre();
					int ITotAlfiere = IFdistanzaAlfiere + camminoFD.lunghezzaAlfiere();
					
					aggiornaMonitorConNuovaFrontiera(lunghezzaTorreMin, lunghezzaAlfiereMin);
					
					if (ITot < lunghezzaMin) {
						lunghezzaMin = ITot;
						lunghezzaTorreMin = ITotTorre;
						lunghezzaAlfiereMin = ITotAlfiere;
						
						aggiornaSequenzaMinima(O, seqMin, F, camminoFD);
						
						aggiornaMonitorMinimo(O, lunghezzaTorreMin, lunghezzaAlfiereMin, seqMin);
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
		stackCammino.pop();
		
		
		if (config.isDebugEnabled()) System.out.println("end");
		
		ICammino risultatoFinale = new Cammino(lunghezzaTorreMin, 
				lunghezzaAlfiereMin, 
				seqMin);
		
		// SALVA IL RISULTATO CALCOLATO NELLA CACHE
		//
		//Serve test per controllare che griglia sia corretto (invece di g o g2)
		//
		pathCache.putCammino(griglia, O, D, risultatoFinale);
		
		return risultatoFinale;
	}
	private boolean isCellaFrontieraNonOstacolo(ICellaConDistanze F) {
		return StatoCella.OSTACOLO.isNot(F.stato());
	}
	
	private void aggiornaSequenzaMinima(ICella2D O, List<ILandmark> seqMin, ICellaConDistanze F, ICammino camminoFD) {
		seqMin.clear();
		seqMin.add(new Landmark(O.stato(), O.x(), O.y()));
		seqMin.addAll(camminoFD.landmarks());
		
		
		if (camminoFD.landmarks().size()>1) {
			seqMin.set(1, new Landmark(F));
		}
		
	}
	private void aggiornaMonitorMinimo(ICella2D O, int lunghezzaTorreMin, int lunghezzaAlfiereMin,
			List<ILandmark> seqMin) {
		if (config.isMonitorEnabled() && O.sameCoordinateAs(monitor.getOrigine())) {
			monitorMin.setCammino(new Cammino(lunghezzaTorreMin, lunghezzaAlfiereMin, seqMin));
		}
	}
	private void aggiornaMonitorConNuovaFrontiera(int lunghezzaTorreMin, int lunghezzaAlfiereMin) {
		if (config.isMonitorEnabled()) {
			monitor.setCammino(new Cammino(
					lunghezzaTorreMin, 
					lunghezzaAlfiereMin,
					new ArrayList<>(stackCammino)));
		}
	}
	private boolean condizioneSoddisfattaDaFrontiera(ICella2D dest, double lunghezzaMin, ICellaConDistanze F, double IF) {
		boolean condizioneSoddisfatta;
		if (config.isCondizioneRafforzataEnabled()) {
			double limiteInferioreDistanza = Utils.distanzaLiberaTra(F, dest);
			condizioneSoddisfatta = (IF + limiteInferioreDistanza < lunghezzaMin);
		} else {
			condizioneSoddisfatta = (IF < lunghezzaMin);
		}
		return condizioneSoddisfatta;
	}
	private void stampaFrontieraStato(ICellaConDistanze F) {
		if (config.isStateCheckEnabled()) {
			System.out.println("Frontiera stato");
			this.bitPrint(F.stato());
		}
	}
	private List<ICellaConDistanze> ottieniFrontiera(IGrigliaConOrigine g, ICella2D dest) {
		List<ICellaConDistanze> frontieraList;
		if (config.isSortedFrontieraEnabled()) {
			frontieraList = g.getFrontiera()
					.sorted(Comparator.comparingDouble(
							c -> Utils.distanzaLiberaTra(c, dest)))
					.toList();
		}else {
			frontieraList = g.getFrontiera().toList();
		}
		return frontieraList;
	}
	private ICammino gestisciCasoFrontieraVuota(IGriglia<?> griglia, ICella2D O, ICella2D dest) {
		livelloRicorsione--;
		
		if (config.isMonitorEnabled()) {
			monitor.setCammino(new Cammino(Integer.MAX_VALUE,
					Integer.MAX_VALUE, 
					new ArrayList<>(stackCammino)));
			
			stackCammino.pop();
		}
		
		if (config.isDebugEnabled()) System.out.println("caso base infinity");
		
		ICammino risultato = new Cammino(Integer.MAX_VALUE,
				Integer.MAX_VALUE,
				new ArrayList<>());
		
		// Salva anche i risultati "infiniti" nella cache
		pathCache.putCammino(griglia, O, dest, risultato);
		
		return risultato;
	}
	private ICammino gestisciCasoBase(ICella2D O, IGrigliaConOrigine g, ICella2D dest) {
		if (config.isDebugEnabled()) System.out.println("caso base");
		
		int distanzaTorre = g.getCellaAt(dest.x(), dest.y()).distanzaTorre();
		int distanzaAlfiere = g.getCellaAt(dest.x(), dest.y()).distanzaAlfiere();
		
		if (config.isMonitorEnabled()) {
			ILandmark landmarkDestinazione = new Landmark(
					StatoCella.LANDMARK.addTo(dest.stato()),
					dest.x(), dest.y());
			
			stackCammino.push(landmarkDestinazione);
			updateMonitor(distanzaTorre,distanzaAlfiere);
			stackCammino.pop();
			
			
			if (livelloRicorsione==1) {
				stackCammino.push(landmarkDestinazione);
				updateMonitorMin(distanzaTorre,distanzaAlfiere);
			}
		}
		stackCammino.pop();
		livelloRicorsione--;
		
		ICammino risultato = new Cammino(distanzaTorre,distanzaAlfiere,
				Arrays.asList(
						new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y()),
						new Landmark(StatoCella.LANDMARK.addTo(dest.stato()),
								dest.x(), dest.y())
						));
		
		// Salva il risultato nella cache se la cache è abilitata
		pathCache.putCammino(g, O, dest, risultato);
		
		return risultato;
	}
	private boolean condizioneCasoBase(IGriglia<?> g, IHave2DCoordinate dest) {
		return StatoCella.CHIUSURA.check(g.getCellaAt(dest).stato()); 
	}
	private void aggiornaMonitor(IGrigliaConOrigine g, ICella2D dest) {
		if (config.isMonitorEnabled()) {
			int distanzaTorre = g.getCellaAt(dest.x(), dest.y()).distanzaTorre();
			int distanzaAlfiere = g.getCellaAt(dest.x(), dest.y()).distanzaAlfiere();
			updateMonitor(distanzaTorre,distanzaAlfiere);
		}
	}
	private ICella2D prendiCellaDestinazione(ICella2D D, IGrigliaConOrigine g) {
		ICella2D dest = g.getCellaAt(D.x(), D.y());
		if (config.isStateCheckEnabled()) {
			System.out.println("Dest PRIMA: " );
			bitPrint(g.getCellaAt(dest.x(), dest.y()).stato());
		}
		
		if (config.isStateCheckEnabled()) {
			System.out.println("Dest dopo: " );
			bitPrint(g.getCellaAt(dest.x(), dest.y()).stato());
			System.out.println("end");
			
			System.out.println("Stato destinazione presa da D");
			this.bitPrint(dest.stato());
			System.out.println("Stato destinazione presa da griglia (dest)");
			this.bitPrint(dest.stato());
		}
		
		return dest;
	}
	private IGrigliaConOrigine creaGriglia(IGriglia<?> griglia, ICella2D O, ICella2D D, ICompitoDue compitoDue) {
		IGrigliaConOrigine g = compitoDue.calcola(griglia, O);
		
		if (config.isStateCheckEnabled()) {
			System.out.println("PRIMA: " );
			bitPrint(g.getCellaAt(D.x(), D.y()).stato());
		}
		StatoCella.DESTINAZIONE.addTo(g, D.x(), D.y());
		
		if (config.isStateCheckEnabled()) {
			System.out.println("DOPO MODIFICA A D: " );
			bitPrint(g.getCellaAt(D.x(), D.y()).stato());
		}
		return g;
	}
	private void preparaRicorsione(ICella2D O) throws InterruptedException {
		livelloRicorsione++;
		this.gestoreInterruzioni.checkInterruzione();
		
		ILandmark currentLandmark = new Landmark(
				StatoCella.LANDMARK.addTo(O.stato()),
				O.x(), O.y());
		
		if (config.isStateCheckEnabled()) {
			System.out.println("Current landmark");
			this.bitPrint(currentLandmark.stato());
		}
		
		stackCammino.push(currentLandmark);
		
		if (config.isDebugEnabled()) System.out.println("Chiamata camminoMinConStatistiche livello " + livelloRicorsione);
	}
	
	private ICammino verificaPresenzaCamminoInCache(IGriglia<?> griglia, ICella2D O, ICella2D D) {
		//stampa tutti gli elementi in cache se debug abilitato
		pathCache.printCacheContents();
		
		//CACHE
		ICammino cached = pathCache.getCammino(griglia, O, D);
		if (cached != null) {
			this.stats.incrementaCacheHit();
			return cached;
		}
		return null; // Se non c'è un cammino in cache, procedi con il calcolo
		//END CACHE CHECK
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
	public void setTimeout(long timeoutMillis) {
		gestoreInterruzioni.setTimeout(timeoutMillis);
	}
	
	//	private void checkInterruzione() throws InterruptedException {
	//		if (interrompiSuRichiesta) {
	//			throw new InterruptedException("Interrotto su richiesta");
	//		}
	//
	//		if (interrompiSuTempo && (System.currentTimeMillis() - tempoInizio) > timeoutMillis) {
	//			throw new InterruptedException("Timeout raggiunto");
	//		}
	//	}
	private void bitPrint(int numero) {
		String bit = String.format("%32s", Integer.toBinaryString(numero)).replace(' ', '0');
		System.out.println(bit);
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
	
}
