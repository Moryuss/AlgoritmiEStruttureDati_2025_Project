package sperimentazione;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import francesco.ICella2D;
import francesco.IGriglia;
import matteo.CompitoTreImplementation;
import matteo.ConfigurationMode;
import matteo.ICammino;
import matteo.ICompitoTre;
import matteo.Riassunto.IStatisticheEsecuzione;
import nicolas.CompitoDueImpl;
import nicolas.ICompitoDue;
import nicolas.IGrigliaConOrigine;

public class SperimentazioneCorrettezza {

	private static final long TEMPO_SCADENZA_ESECUZIONE = TimeUnit.MINUTES.toMillis(5);
	
	private static final String PATH = "src/sperimentazione/correttezza";
	
	private static final int RETRIES = 10;
	
	private static final List<ConfigurationMode> TRES = List.of(
			ConfigurationMode.DEFAULT,
			ConfigurationMode.PERFORMANCE_NO_CACHE,
			ConfigurationMode.PERFORMANCE_NO_SORTED_FRONTIERA,
			ConfigurationMode.PERFORMANCE_NO_CONDIZIONE_RAFFORZATA,
			ConfigurationMode.PERFORMANCE_CACHE,
			ConfigurationMode.PERFORMANCE_SORTED_FRONTIERA,
			ConfigurationMode.PERFORMANCE_CONDIZIONE_RAFFORZATA,
			ConfigurationMode.PERFORMANCE,
			ConfigurationMode.PERFORMANCE_SVUOTA_FRONTIERA,
			ConfigurationMode.PERFORMANCE_FULL
			);
	
	private static final List<ICompitoDue> DUES = List.of(
			CompitoDueImpl.V0,
			CompitoDueImpl.V1
		);
	
	public static void main(String[] args) {
		
		// Leggi tutte le griglie (e salva i seed visto che non sono recuperabili da nessun'altra parte)
		List<Integer> seedRandom = new ArrayList<>();
		
		List<IGriglia<?>> griglie = leggiGriglieDaPath(PATH, seedRandom);
		
		IStatisticheEsecuzione primaEsecuzione = null;
		
		//Per ogni griglia:
		
		for(IGriglia<?> griglia : griglie) {
			for(int i = 0; i < RETRIES; i++) {
				// Inizio salvataggio delle coppie di cammini
				List<coppiaCammini> coppie = new ArrayList<>();
				// Genera le celle casuali e scegline casualmente una come origine ed una come destinazione
				List<CoordinateCella> celle = generaCelleCasuali(griglia.width(), griglia.height());
				
				CoordinateCella origine = estraiCoordinataCasualeNonOccupata(celle, griglia);
				
				CoordinateCella destinazione = estraiCoordinataCasualeNonOccupata(celle, griglia);
				
				for(ConfigurationMode tre : TRES) {
					for(ICompitoDue due : DUES) {
						ICompitoTre implementazioneTre = new CompitoTreImplementation(tre);
						
						IGrigliaConOrigine gO = due.calcola(griglia, origine);
						
						ICella2D origineCella = gO.getCellaAt(origine.x(), origine.y());
						ICella2D destinazioneCella = gO.getCellaAt(destinazione.x(), destinazione.y());
						
						implementazioneTre.setTimeout(TEMPO_SCADENZA_ESECUZIONE, TimeUnit.MILLISECONDS);
						
						ICammino cammino1 = implementazioneTre.camminoMin(griglia, origineCella, destinazioneCella, due);
						
						// Cammino nullo -> ignora e prosegui
						if (cammino1 == null) {
							continue;
						}
						
						primaEsecuzione = implementazioneTre.getStatisticheEsecuzione();
						
						//TODO Gestione timeout e non raggiungibilita', per quando si implementera' la scrittura su file
						if(primaEsecuzione.isCalcoloInterrotto()) {
							System.out.println("Timeout.");
							break;
						}
						
						if(cammino1.landmarks().isEmpty() || Double.isInfinite(cammino1.lunghezza())) {
							System.out.println("Destinazione non Raggiungibile.");
							break;
						}
						
						ICammino cammino2 = implementazioneTre.camminoMin(griglia, destinazioneCella, origineCella, due);
						
						// Caso: cammino inverso non valido (TODO va segnato errore come non correttezza)
						if (cammino2 == null || cammino2.landmarks().isEmpty() || Double.isInfinite(cammino2.lunghezza())) {
							break;
						}
						
						// Uso di MainSperimentazione per verificare la correttezza
						boolean corretto = MainSperimentazione.isLunghezzaUguale(cammino1, cammino2);
						
						coppie.add(new coppiaCammini(cammino1, cammino2));
					}
				}
				scriviDifferenzaCammini(griglia, coppie, origine, destinazione);
			}
		}
	}

	private static List<IGriglia<?>> leggiGriglieDaPath(String path, List<Integer> seedRandom) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static List<CoordinateCella> generaCelleCasuali(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static CoordinateCella estraiCoordinataCasualeNonOccupata(List<CoordinateCella> celle,
			IGriglia<?> griglia) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static void scriviDifferenzaCammini(IGriglia<?> griglia, List<coppiaCammini> coppie,
			CoordinateCella origine, CoordinateCella destinazione) {
		// TODO Auto-generated method stub
//		# Config
//
//		- seed : 🦆
//		- width : 🦆
//		- height : 🦆
//		- tipoGriglia : 🦆
//
//		# Test
//
//		## O=(🦆,🦆), D=(🦆,🦆)
//		|| camminoMin(O,D) | camminoMin(D,O) |
//		| --- | --- | --- |
//		| lunghezza | 🦆+🦆√2=🦆 | 🦆+🦆√2=🦆|
//		| Landmarks | 🦆 |🦆|
//		| tempo | 🦆|🦆|
		
//		FORMATO .md, lunghezza alfiere e torre stanno in cammino
	}
}


record coppiaCammini(ICammino cammino1, ICammino cammino2) {}
