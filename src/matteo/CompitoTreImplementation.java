package matteo;

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
	
	IStatisticheEsecuzione stats;	
	IProgressoMonitor monitor = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino
	IProgressoMonitor monitorMin = new ProgressoMonitor();		//per monitorare l'evoluzione del cammino minimo
	
	private String report;
	private int livelloRicorsione = 0;
	
	public boolean debug = false;
	public boolean monitorON = true; 	//NON mettere a false, è legato al funzionamento degli interrupt.
	public boolean stopMessage = false;
	public boolean stateCheck = false;	//stampa lo stato di celle considerate
	
	private boolean interrompiSuRichiesta = false;	// questo non va modificato da qui, ma da setTimeout(tempo)
	private boolean interrompiSuTempo = false;	
	
	boolean sortedFrontiera = true;	//applica sort alla frontiera, considera prima quelle più vicine a destinazione
	boolean condizioneRafforzata = false; // Imposta a false per usare la condizione originale
	
	private long tempoInizio;
	private long timeoutMillis; 
	
	private Deque<ILandmark> stackCammino = new ArrayDeque<ILandmark>();
	
	
	@Override
	public ICammino camminoMin(IGriglia<?> griglia, ICella2 O, ICella2 D) {
		
		if(stateCheck) {
			System.out.println("Origine e destinazione stati");
			this.bitPrint(O.stato());
			this.bitPrint(D.stato());
		}
		
		inizializzazione(griglia, O, D);
		
		try {
			ICammino risultato = camminoMinConStatistiche(griglia, O, D, stats);

			report = stats.generaRiassunto(risultato);

			return risultato;
		} catch (InterruptedException e) {
			return gestisciInterruzione(e);
		}
	}

	private ICammino gestisciInterruzione(InterruptedException e) {
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

	private void inizializzazione(IGriglia<?> griglia, ICella2 O, ICella2 D) {
		stats = new StatisticheEsecuzione();
		monitor.setOrigine(O);
		monitor.setDestinazione(D);
		monitorMin.setOrigine(O);
		monitorMin.setDestinazione(D);
		monitorMin.setCammino(new Cammino(Integer.MAX_VALUE, Integer.MAX_VALUE, new ArrayList<>()));
		
		stats.saveDimensioniGriglia(griglia.height(), griglia.width());
		stats.saveTipoGriglia(griglia.getTipo());
		stats.saveOrigine(O);
		stats.saveDestinazione(D);
	}
	
	public ICammino camminoMinConStatistiche(IGriglia<?> griglia, ICella2 O, ICella2 D, IStatisticheEsecuzione stats) throws InterruptedException {
			livelloRicorsione++;
			
			this.checkInterruzione();
			
			ILandmark currentLandmark = new Landmark(
					StatoCella.LANDMARK.addTo(O.stato()),
					O.x(), O.y());

			if(stateCheck) {
				System.out.println("Current landmark");
				this.bitPrint(currentLandmark.stato());
			}

			stackCammino.push(currentLandmark);
			
			
			if(debug) System.out.println("Chiamata camminoMinConStatistiche livello " + livelloRicorsione);
			
			IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y());
			
			ICella2 dest = g.getCellaAt(D.x(), D.y());
			StatoCella.DESTINAZIONE.addTo(dest);
			
			
			if(stateCheck) {
				System.out.println("Stato destinazione presa da D");
				this.bitPrint(dest.stato());
				System.out.println("Stato destinazione presa da griglia (dest)");
				this.bitPrint(dest.stato());
			}
			if(monitorON) {
				int distanzaTorre = g.getCellaAt(dest.x(), dest.y()).distanzaTorre();
				int distanzaAlfiere = g.getCellaAt(dest.x(), dest.y()).distanzaAlfiere();
				updateMonitor(distanzaTorre,distanzaAlfiere);
			}
			if (g.isInContesto(dest.x(), dest.y()) || g.isInComplemento(dest.x(), dest.y())) {
				
				if(debug) System.out.println("caso base");
				
				int distanzaTorre = g.getCellaAt(dest.x(), dest.y()).distanzaTorre();
				int distanzaAlfiere = g.getCellaAt(dest.x(), dest.y()).distanzaAlfiere();
				
				if(monitorON) {
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

				return new Cammino(distanzaTorre,distanzaAlfiere,
						Arrays.asList(
								new Landmark(StatoCella.LANDMARK.value(), O.x(), O.y()),
								new Landmark(StatoCella.LANDMARK.addTo(StatoCella.CONTESTO.value()), dest.x(), dest.y())
								));
			}
			
			
			List<ICella2> frontieraList;
			if(sortedFrontiera) {
			frontieraList = g.getFrontiera()
					.sorted(Comparator.comparingDouble(
							c -> Utils.distanzaLiberaTra(c, dest)))
					.toList();
			}else {
				frontieraList = g.getFrontiera().toList();
			}
			
			if (frontieraList.isEmpty()) {
				
				livelloRicorsione--;
				
				if(monitorON) {
					monitor.setCammino(new Cammino(Integer.MAX_VALUE,
							Integer.MAX_VALUE, 
							new ArrayList<>(stackCammino)));
					
					stackCammino.pop();
				}
				
				if(debug) System.out.println("caso base infinity");
				
				return new Cammino(Integer.MAX_VALUE,
						Integer.MAX_VALUE,
						new ArrayList<>());
			}
			
			double lunghezzaMin = Double.POSITIVE_INFINITY;
			int lunghezzaTorreMin = Integer.MAX_VALUE;
			int lunghezzaAlfiereMin = Integer.MAX_VALUE;
			
			List<ILandmark> seqMin = new ArrayList<>();
			
			IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo());
			
			for (ICella2 F : frontieraList) {
				this.checkInterruzione();
				if(stateCheck) {
					System.out.println("Frontiera stato");
					this.bitPrint(F.stato());
				}
				
				if (StatoCella.OSTACOLO.isNot(F.stato())) {
					
					stats.incrementaCelleFrontiera();
					
					if(debug) System.out.println("Analizzo cella frontiera (" + F.x() + "," + F.y() + ")");
					
					
					int IFdistanzaTorre = g.getCellaAt(F.x(), F.y()).distanzaTorre();
					int IFdistanzaAlfiere = g.getCellaAt(F.x(), F.y()).distanzaAlfiere();
					double IF = F.distanzaDaOrigine();
					
					boolean condizioneSoddisfatta;
			        if (condizioneRafforzata) {
			        	double limiteInferioreDistanza = Utils.distanzaLiberaTra(F, dest);
			            condizioneSoddisfatta = (IF + limiteInferioreDistanza < lunghezzaMin);
			        } else {
			            condizioneSoddisfatta = (IF < lunghezzaMin);
			        }

					if (condizioneSoddisfatta) {
//						System.out.println("condizione 16/17 triggerata");
						ICammino camminoFD = camminoMinConStatistiche(g2, F, dest, stats);
						double ITot = IF + camminoFD.lunghezza();
						int ITotTorre = IFdistanzaTorre + camminoFD.lunghezzaTorre();
						int ITotAlfiere = IFdistanzaAlfiere + camminoFD.lunghezzaAlfiere();
						
						if (monitorON) {
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
                            
                            
                       //╔═══════════════════════════════════════════╗
                       /*║*/if (camminoFD.landmarks().size()>1)    //║
                       /*║*/    seqMin.get(1).setStato(F.stato()); //║ 
                       //╚═══════════════════════════════════════════╝
                            
                            if (monitorON && O.x()==monitor.getOrigine().x() && O.y()==monitor.getOrigine().y()) {
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
			
			
			if(debug) System.out.println("end");
			
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

}
