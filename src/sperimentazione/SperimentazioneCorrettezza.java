package sperimentazione;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import matteo.CompitoTreImplementation;
import matteo.ConfigurationMode;
import matteo.ICammino;
import matteo.ICompitoTre;
import matteo.Riassunto.IStatisticheEsecuzione;
import nicolas.CompitoDueImpl;
import nicolas.ICompitoDue;
import nicolas.IGrigliaConOrigine;
import processing.data.JSONObject;
import utils.Utils;

public class SperimentazioneCorrettezza {

	private static final long TEMPO_SCADENZA_ESECUZIONE = TimeUnit.MINUTES.toMillis(5);
	
	private static final String PATH = "src/sperimentazione/correttezza/griglie";
	private static final String CARTELLA_REPORT = "src/sperimentazione/correttezza/";
	
	private static final int MAX_CAPIENZA = 100;
	
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
		List<String> nomiGriglie = new ArrayList<>();
		
		List<IGriglia<?>> griglie = leggiGriglieDaPath(PATH, seedRandom, nomiGriglie);
		
		IStatisticheEsecuzione primaEsecuzione = null;
		
		//Per ogni griglia:
		
		for(IGriglia<?> griglia : griglie) {
			System.out.println("Griglia: " + nomiGriglie.get(0));
			// Inizio salvataggio delle coppie di cammini
			List<coppiaCammini> coppie = new ArrayList<>();
			List<Statistiche> stat = new ArrayList<>();
			List<CoordinateCella> origini = new ArrayList<>();
			List<CoordinateCella> destinazioni = new ArrayList<>();
			
			List<String> compitiUsati = new ArrayList<>();
			for(int i = 0; i < RETRIES; i++) {
				// Genera le celle casuali e scegline casualmente una come origine ed una come destinazione
				int currentSeed = seedRandom.get(0) + i*seedRandom.get(0);
				Random random = new Random(currentSeed);
				
				List<CoordinateCella> celle = generaCelleCasuali(griglia.width(), griglia.height(), random, griglia);
				
				random = new Random(currentSeed + 1);
				
				CoordinateCella origine = estraiCoordinataCasualeNonOccupata(celle, griglia, random);
				if(origine == null) {
					// In caso di assenza di origine, allora salta
					continue;
				}
				System.out.println("Origine: " + origine.x() + ", " + origine.y());
				origini.add(origine);
				
				random = new Random(currentSeed + 2);
				
				CoordinateCella destinazione = estraiCoordinataCasualeNonOccupata(celle, griglia, random);
				if(destinazione == null) {
					// In caso di assenza di destinazione, allora salta
					continue;
				}
				System.out.println("Destinazione: " + destinazione.x() + ", " + destinazione.y());
				destinazioni.add(destinazione);
				
				for(ConfigurationMode tre : TRES) {
					for(ICompitoDue due : DUES) {
						
						compitiUsati.add(tre.toString() + "_" + due.name());
						
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
						
						//Gestione timeout e non raggiungibilita', per quando si implementera' la scrittura su file
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
						IStatisticheEsecuzione secondaEsecuzione = implementazioneTre.getStatisticheEsecuzione();
						
						coppie.add(new coppiaCammini(cammino1, cammino2));
						stat.add(new Statistiche(primaEsecuzione, secondaEsecuzione));
					}
				}
			}
			scriviDifferenzaCammini(griglia, coppie, stat, seedRandom.get(0),
					origini, destinazioni, nomiGriglie.get(0), compitiUsati);
			nomiGriglie.remove(0);
			seedRandom.remove(0);
		}
	}

	private static List<IGriglia<?>> leggiGriglieDaPath(String path, List<Integer> seedRandom, List<String> nomiGriglie) {
		List<Path> pathGriglie = new ArrayList<>();
		List<IGriglia<?>> risultato = new ArrayList<>();
		try (Stream<Path> stream = Files.list(Paths.get(path))) {
			pathGriglie = stream.toList();
			pathGriglie.forEach(pathGriglia -> {
				String nomeCompleto = pathGriglia.getFileName().toString();
				nomiGriglie.add(nomeCompleto.substring(0, nomeCompleto.lastIndexOf('.')));
			});
		} catch (IOException e) {
			System.out.println("Errore nella lettura della cartella");
			e.printStackTrace();
		}
		for(Path pathGriglia : pathGriglie) {
			risultato.add(new LettoreGriglia().crea(pathGriglia));
			File jsonFile = pathGriglia.toFile();
			JSONObject json = LettoreGriglia.loadJSONObject(jsonFile);
			int seed = json.getInt("randomSeed");
			seedRandom.add(seed);
		}
		
		return risultato;
	}
	
	private static List<CoordinateCella> generaCelleCasuali(int width, int height, Random random, IGriglia<?> griglia) {
		// Evita duplicati
		Set<CoordinateCella> coordinateSet = new HashSet<>();
		while (coordinateSet.size() < MAX_CAPIENZA) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			if(griglia.isNavigabile(x, y)) {
				coordinateSet.add(new CoordinateCella(x, y));
			}
		}
		
		return new ArrayList<>(coordinateSet);
	}
	
	private static CoordinateCella estraiCoordinataCasualeNonOccupata(List<CoordinateCella> celle,
			IGriglia<?> griglia, Random random) {
		
//		List<CoordinateCella> navigabili = new ArrayList<>();
		
//		for (CoordinateCella c : celle) {
//			if (griglia.isNavigabile(c.x(), c.y())) {
//				navigabili.add(c);
//			}
//		}
////		
//		if (navigabili.isEmpty()) {
//			return null;
//		}
		
		int idx = random.nextInt(celle.size());
		CoordinateCella risultato = celle.get(idx);
		// Rimuove la coordinata estratta così che non venga usata di nuovo
		celle.remove(idx);
		
		return risultato;
	}
	
	private static void scriviDifferenzaCammini(IGriglia<?> griglia, List<coppiaCammini> coppie,
			List<Statistiche> stat, Integer seed, List<CoordinateCella> origini,
			List<CoordinateCella> destinazioni, String nomeGriglia, List<String> compitiUsati) {
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
		
		String basePath = CARTELLA_REPORT + "/" + nomeGriglia;
		String path = basePath + "_CORRETTEZZA.md";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("# Configurazione Griglia\n\n");
		sb.append("- seed: ").append(seed).append("\n");
		sb.append("- width: ").append(griglia.width()).append("\n");
		sb.append("- height: ").append(griglia.height()).append("\n");
		sb.append("- tipoGriglia: ").append(griglia.getTipo()).append("\n\n");
		sb.append("# Test\n\n");
		
		for(int i = 0; i < origini.size(); i++) {
			
			coppiaCammini coppia = coppie.get(i);
			Statistiche statistiche = stat.get(i);
			CoordinateCella origine = origini.get(i);
			CoordinateCella destinazione = destinazioni.get(i);
			
			// Origine e Destinazione
			sb.append("## O=(").append(origine.x()).append(",").append(origine.y())
				.append("), D=(").append(destinazione.x()).append(",").append(destinazione.y()).append(")\n\n");
			sb.append("### Combinazione: " + compitiUsati.get(i) + "\n\n");
			sb.append("|| camminoMin(O,D) | camminoMin(D,O) |\n");
			sb.append("| --- | --- | --- |\n");
			
			// Lunghezze cammini
			sb.append("| Lunghezza | ")
			// Cammino 1
				.append(coppia.cammino1().lunghezzaTorre()).append(" + ")
				.append(coppia.cammino1().lunghezzaAlfiere()).append("√2 = ")
				.append(coppia.cammino1().lunghezza())
				.append(" | ")
			// Cammino 2
				.append(coppia.cammino2().lunghezzaTorre()).append(" + ")
				.append(coppia.cammino2().lunghezzaAlfiere()).append("√2 = ")
				.append(coppia.cammino2().lunghezza())
				.append(" |\n");
			
			// Landmarks
			sb.append("| Landmarks | ").append(coppia.cammino1().landmarks().size())
				.append(" | ").append(coppia.cammino2().landmarks().size()).append(" |\n");
			
			// Tempo
			sb.append("| Tempo | ")
				.append(Utils.formatTempo(statistiche.primaStat().getTempoEsecuzione())).append(" | ")
				.append(Utils.formatTempo(statistiche.secondaStat().getTempoEsecuzione())).append(" |\n\n");
			
			// Controllo correttezza
			boolean corretto = MainSperimentazione.isLunghezzaUguale(coppia.cammino1(), coppia.cammino2());
			
			sb.append("Corretto? " + corretto + "\n\n");
		}
		
		ScritturaFile.pulisciFile(path);
		ScritturaFile.writeToFile(path, sb.toString());
	}
}


record coppiaCammini(ICammino cammino1, ICammino cammino2) {}

record Statistiche(IStatisticheEsecuzione primaStat, IStatisticheEsecuzione secondaStat) {}