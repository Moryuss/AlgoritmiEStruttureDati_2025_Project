package matteo;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import francesco.IGriglia;
import nicolas.*;

public class CompitoTreImplementation implements ICompitoTre, IHasReport, IHasProgressoMonitor, IInterrompibile{

	private final CamminoCache pathCache = new CamminoCache(); //cache per i cammini già calcolati

	IStatisticheEsecuzione stats;	
	IProgressoMonitor monitor = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino
	IProgressoMonitor monitorMin = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino minimo

	private String report;
	private int livelloRicorsione = 0;

	private CamminoConfiguration config = new CamminoConfiguration(); // Configurazione del cammino
//	public boolean debug = false;	//per debug, stampa informazioni utili
//	public boolean monitorON = true; 	//NON mettere a false, è legato al funzionamento degli interrupt.
//	public boolean stopMessage = false;
//	public boolean stateCheck = false;	//stampa lo stato di celle considerate

	private boolean interrompiSuRichiesta = false;	// questo non va modificato da qui, ma da setTimeout(tempo)
	private boolean interrompiSuTempo = false;	

//	public boolean sortedFrontiera = false;	//applica sort alla frontiera, considera prima quelle più vicine a destinazione
//	public boolean condizioneRafforzata = false; // Imposta a false per usare la condizione originale
//	public boolean cacheEnabled = false; // Flag per abilitare/disabilitare la cache

	private long tempoInizio;
	private long timeoutMillis; 

	private Deque<ILandmark> stackCammino = new ArrayDeque<ILandmark>();

	/**
	 * Constructor con configurazione di default
	 */
    public CompitoTreImplementation() {
        this(CamminoConfiguration.createDefault());
    }
    /**
     * Constructor che accetta una configurazione personalizzata. <br>
     * Usa CamminoConfiguration.createDefault() per una configurazione di base.<br>
     * Usa CamminoConfiguration.createDebugMode per una configurazione che stampa a video i log.<br>
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
	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2 O, ICella2 D) {

		if(config.isStateCheckEnabled()) {
			System.out.println("Origine e destinazione stati");
			this.bitPrint(O.stato());
			this.bitPrint(D.stato());
		}

		inizializzazione(griglia, O, D);

		try {
			ICammino risultato = camminoMinConStatistiche(griglia, O, D, stats);

			report = stats.generaRiassunto(risultato);
			
			if(config.isStateCheckEnabled()) {
				System.out.println("stato destinazione");
				bitPrint(risultato.landmarks().getLast().stato());
			}
			return risultato;
		} catch (InterruptedException e) {
			return gestisciInterruzione(e);
		}
	}

	private ICammino gestisciInterruzione(InterruptedException e) {
		if(config.isStopMessageEnabled()) System.out.println(e.getMessage());
		stats.interrompiCalcolo();
		if(this.getProgressMin().getCammino()!= null) {
			if(config.isStopMessageEnabled()) System.out.println("Cammino trovato");
			return this.getProgressMin().getCammino();
		}
		else if(this.getProgress().getCammino()!= null) {
			if(config.isStopMessageEnabled()) System.out.println("Cammino non trovato");
			return this.getProgress().getCammino();
		}
		else {
			if(config.isStopMessageEnabled()) System.out.println("Calcolo non andato a buon fine");
			return null;
		}
	}

	private void inizializzazione(IGriglia<?> griglia, ICella2 O, ICella2 D) {
		stats = new StatisticheEsecuzione();

		// Inizializza la cache
		pathCache.setEnabled(config.isCacheEnabled());
		pathCache.setDebugMode(config.isDebugEnabled());
		stats.setCache(pathCache.isEnabled());

		monitor.setOrigine(O);
		monitor.setDestinazione(D);
		monitorMin.setOrigine(O);
		monitorMin.setDestinazione(D);
		monitorMin.setCammino(new Cammino(Integer.MAX_VALUE, Integer.MAX_VALUE, new ArrayList<>()));

		stats.saveDimensioniGriglia(griglia.height(), griglia.width());
		stats.saveTipoGriglia(griglia.getTipo());
		stats.saveOrigine(O);
		stats.saveDestinazione(D);

		stats.setFrontieraStored(config.isSortedFrontieraEnabled());
	}

	public ICammino camminoMinConStatistiche(IGriglia<?> griglia, ICella2 O, ICella2 D, IStatisticheEsecuzione stats) throws InterruptedException {

		//stampa tutti gli elementi in cache se debug abilitato
		pathCache.printCacheContents();

		//CHACHE
		ICammino cached = pathCache.getCammino(griglia, O, D);
		if (cached != null) {
			stats.incrementaCacheHit();
			return cached;
		}
		//END CACHE CHECK

		livelloRicorsione++;

		this.checkInterruzione();

		ILandmark currentLandmark = new Landmark(
				StatoCella.LANDMARK.addTo(O.stato()),
				O.x(), O.y());

		if(config.isStateCheckEnabled()) {
			System.out.println("Current landmark");
			this.bitPrint(currentLandmark.stato());
		}

		stackCammino.push(currentLandmark);


		if(config.isDebugEnabled()) System.out.println("Chiamata camminoMinConStatistiche livello " + livelloRicorsione);

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y());

		if(config.isStateCheckEnabled()) {
			System.out.println("PRIMA: " );
			bitPrint(g.getCellaAt(D.x(), D.y()).stato());
		}
		StatoCella.DESTINAZIONE.addTo(g.getCellaAt(D.x(), D.y()));


		if(config.isStateCheckEnabled()) {
			System.out.println("DOPO MODIFICA A D: " );
			bitPrint(g.getCellaAt(D.x(), D.y()).stato());
		}

		ICella2 dest = g.getCellaAt(D.x(), D.y());
		if(config.isStateCheckEnabled()) {
			System.out.println("Dest PRIMA: " );
			bitPrint(g.getCellaAt(dest.x(), dest.y()).stato());
		}

		//Non dovrebbe servire
		//StatoCella.DESTINAZIONE.addTo(dest);

		if(config.isStateCheckEnabled()) {
			System.out.println("Dest dopo: " );
			bitPrint(g.getCellaAt(dest.x(), dest.y()).stato());
			System.out.println("end");
		}

		if(config.isStateCheckEnabled()) {
			System.out.println("Stato destinazione presa da D");
			this.bitPrint(dest.stato());
			System.out.println("Stato destinazione presa da griglia (dest)");
			this.bitPrint(dest.stato());
		}
		if(config.isMonitorEnabled()) {
			int distanzaTorre = g.getCellaAt(dest.x(), dest.y()).distanzaTorre();
			int distanzaAlfiere = g.getCellaAt(dest.x(), dest.y()).distanzaAlfiere();
			updateMonitor(distanzaTorre,distanzaAlfiere);
		}
		if (g.isInContesto(dest.x(), dest.y()) || g.isInComplemento(dest.x(), dest.y())) {

			if(config.isDebugEnabled()) System.out.println("caso base");

			int distanzaTorre = g.getCellaAt(dest.x(), dest.y()).distanzaTorre();
			int distanzaAlfiere = g.getCellaAt(dest.x(), dest.y()).distanzaAlfiere();

			if(config.isMonitorEnabled()) {
				ILandmark landmarkDestinazione = new Landmark(
						StatoCella.LANDMARK.addTo(dest.stato()),
						dest.x(), dest.y());

				stackCammino.push(landmarkDestinazione);
				updateMonitor(distanzaTorre,distanzaAlfiere);
				stackCammino.pop();


				if(livelloRicorsione==1) {
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


		List<ICella2> frontieraList;
		if(config.isSortedFrontieraEnabled()) {
			frontieraList = g.getFrontiera()
					.sorted(Comparator.comparingDouble(
							c -> Utils.distanzaLiberaTra(c, dest)))
					.toList();
		}else {
			frontieraList = g.getFrontiera().toList();
		}

		if (frontieraList.isEmpty()) {

			livelloRicorsione--;

			if(config.isMonitorEnabled()) {
				monitor.setCammino(new Cammino(Integer.MAX_VALUE,
						Integer.MAX_VALUE, 
						new ArrayList<>(stackCammino)));

				stackCammino.pop();
			}

			if(config.isDebugEnabled()) System.out.println("caso base infinity");

			ICammino risultato = new Cammino(Integer.MAX_VALUE,
					Integer.MAX_VALUE,
					new ArrayList<>());

			// Salva anche i risultati "infiniti" nella cache
			pathCache.putCammino(griglia, O, dest, risultato);

			return risultato;
		}

		double lunghezzaMin = Double.POSITIVE_INFINITY;
		int lunghezzaTorreMin = Integer.MAX_VALUE;
		int lunghezzaAlfiereMin = Integer.MAX_VALUE;

		List<ILandmark> seqMin = new ArrayList<>();

		IGriglia<ICella2> g2 = g.addObstacle(g.convertiChiusuraInOstacolo());


		for (ICella2 F : frontieraList) {
			this.checkInterruzione();
			if(config.isStateCheckEnabled()) {
				System.out.println("Frontiera stato");
				this.bitPrint(F.stato());
			}

			if (StatoCella.OSTACOLO.isNot(F.stato())) {

				stats.incrementaCelleFrontiera();

				if(config.isDebugEnabled()) System.out.println("Analizzo cella frontiera (" + F.x() + "," + F.y() + ")");


				int IFdistanzaTorre = g.getCellaAt(F.x(), F.y()).distanzaTorre();
				int IFdistanzaAlfiere = g.getCellaAt(F.x(), F.y()).distanzaAlfiere();
				double IF = F.distanzaDaOrigine();

				boolean condizioneSoddisfatta;
				if (config.isConditioneRafforziataEnabled()) {
					double limiteInferioreDistanza = Utils.distanzaLiberaTra(F, dest);
					condizioneSoddisfatta = (IF + limiteInferioreDistanza < lunghezzaMin);
				} else {
					condizioneSoddisfatta = (IF < lunghezzaMin);
				}

				if (condizioneSoddisfatta) {
					//System.out.println("condizione 16/17 triggerata");
					ICammino camminoFD = camminoMinConStatistiche(g2, F, dest, stats);
					double ITot = IF + camminoFD.lunghezza();
					int ITotTorre = IFdistanzaTorre + camminoFD.lunghezzaTorre();
					int ITotAlfiere = IFdistanzaAlfiere + camminoFD.lunghezzaAlfiere();

					if (config.isMonitorEnabled()) {
						monitor.setCammino(new Cammino(
								lunghezzaTorreMin, 
								lunghezzaAlfiereMin,
								new ArrayList<>(stackCammino)));
					}

					if (ITot < lunghezzaMin) {
						lunghezzaMin = ITot;
						lunghezzaTorreMin = ITotTorre;
						lunghezzaAlfiereMin = ITotAlfiere;

						seqMin.clear();
						//                          StatoCella.LANDMARK.addTo(O);
						seqMin.add(new Landmark(O.stato(), O.x(), O.y()));
						seqMin.addAll(camminoFD.landmarks());



						if (camminoFD.landmarks().size()>1)    
							seqMin.get(1).setStato(F.stato());


						if (config.isMonitorEnabled() 
								&& O.x()==monitor.getOrigine().x() 
								&& O.y()==monitor.getOrigine().y()) {
							monitorMin.setCammino(new Cammino(lunghezzaTorreMin, lunghezzaAlfiereMin, seqMin));
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
		stackCammino.pop();


		if(config.isDebugEnabled()) System.out.println("end");
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

//		private String generaChiaveCache(IGriglia<?> griglia, ICella2 origine, ICella2 destinazione) {
//			return origine.x() + "," + origine.y() + "->" + 
//					destinazione.x() + "," + destinazione.y() + "|" + 
//					calcolaHashOstacoli(griglia);
//		}

	/**
	 * Calcola un hash degli ostacoli presenti nella griglia
	 */
	//	private int calcolaHashOstacoli(IGriglia<?> griglia) {
	//		List<Point> ostacoli = new ArrayList<>();
	//		for (int x = 0; x < griglia.width(); x++) {
	//			for (int y = 0; y < griglia.height(); y++) {
	//				if (StatoCella.OSTACOLO.is(griglia.getCellaAt(x, y).stato())) {
	//					ostacoli.add(new Point(x, y));
	//				}
	//			}
	//		}
	//		int hash = Objects.hash(ostacoli);
	//		return hash;
	//	}
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
