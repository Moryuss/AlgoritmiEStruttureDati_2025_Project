package sperimentazione;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import matteo.CompitoTreImplementation;
import matteo.ConfigurationMode;
import matteo.ICammino;
import matteo.ICompitoTre;
import matteo.Riassunto.CalcoloUsoMemoria;
import matteo.Riassunto.IStatisticheEsecuzione;
import matteo.Riassunto.TipoCella;
import nicolas.CompitoDueImpl;
import nicolas.ICompitoDue;
import nicolas.IGrigliaConOrigine;
import utils.Utils;

public class SperimentazioneGrandiDimensioni {

	private static final long TEMPO_SCADENZA_ESECUZIONE = TimeUnit.MINUTES.toMillis(20);;
	
	private static final Path CARTELLA_GRIGLIE = Paths.get("src/sperimentazione/grandiDimensioni/griglie");
	private static final String CARTELLA_REPORT = "src/sperimentazione/grandiDimensioni/";
	private static final String REPORT_FILE_NAME = "report_";
	
	private static final int OFFSET_ORIGINE_X = 0;
	private static final int OFFSET_ORIGINE_Y = 0;
	
	private static final int OFFSET_DESTINAZIONE_X = 1;
	private static final int OFFSET_DESTINAZIONE_Y = 1;
	
	private static final List<ConfigurationMode> TRES = List.of(
			ConfigurationMode.DEFAULT,
			ConfigurationMode.PERFORMANCE_FULL
			);
	
	private static final List<ICompitoDue> DUES = List.of(
			CompitoDueImpl.V0,
			CompitoDueImpl.V1
			);
	
	public static void main(String[] args) {
		// Lettura dei file e creazione delle Griglie come da CompitoUno
		System.out.println("Lettura di " + CARTELLA_GRIGLIE);
		
		List<String> nomiGriglie = new ArrayList<>();
		// I nomi delle Griglie vengono riempiti in loco
		List<Path> paths = recuperaPathGriglie(CARTELLA_GRIGLIE, nomiGriglie);
		
		LettoreGriglia lettore = new LettoreGriglia();
		// Iterazione su tutte le Griglie...
		
		for(Path path : paths) {
			// Lettura della Griglia con CompitoUno
			IGriglia<?> griglia = lettore.crea(path);
			CoordinateCella origine = new CoordinateCella(OFFSET_ORIGINE_X, OFFSET_ORIGINE_Y);
			CoordinateCella destinazione = new CoordinateCella(griglia.width() - OFFSET_DESTINAZIONE_X,
					griglia.height() - OFFSET_DESTINAZIONE_Y);
			
			List<IStatisticheEsecuzione> statistiche = new ArrayList<>();
			
			System.out.println("Iniziando: " + nomiGriglie.get(0));
			
			// Esecuzione con i metodi in Lista
			for(ConfigurationMode tre : TRES) {
				for(ICompitoDue due : DUES) {
					
					ICompitoTre implementazioneTre = new CompitoTreImplementation(tre);
					
					// Conversione in Griglia con Origine
					IGrigliaConOrigine gO = due.calcola(griglia, origine);
					ICella2D start = gO.getCellaAt(origine.x(), origine.y());
					
					ICella2D end = gO.getCellaAt(destinazione.x(), destinazione.y());
					
					implementazioneTre.setTimeout(TEMPO_SCADENZA_ESECUZIONE, TimeUnit.MILLISECONDS);
					
					ICammino cammino1 = implementazioneTre.camminoMin(griglia, start, end, due);
					
					if(cammino1 == null) {
						System.out.println("Nessun cammino trovato per la griglia: " + path);
						break;
					}
					
					String report = implementazioneTre.getReport();
					
					String combinazione = tre.toString() + "_" + due.name();
					scriviReportSuFile(report, nomiGriglie.get(0), combinazione);
					
					IStatisticheEsecuzione statisticheEsecuzione = implementazioneTre.getStatisticheEsecuzione();
					statistiche.add(statisticheEsecuzione);
					
					if(statisticheEsecuzione.isCalcoloInterrotto()) {
						System.out.println("Tempo Scaduto.");
					}
					
					if(cammino1.landmarks().isEmpty() || Double.isInfinite(cammino1.lunghezza())) {
						System.out.println("Destinazione non raggiungibile");
					}
					
				}
			}
			
			// Salvataggio su file dei risultati
			scriviComparazioniSuFile(statistiche, nomiGriglie.get(0));
			nomiGriglie.remove(0);
		}
	}

	private static void scriviReportSuFile(String report, String nomeGriglia, String combinazione) {
		String pathCartella = CARTELLA_REPORT + "/" + nomeGriglia;
		File cartellaSalvataggio = new File(pathCartella);
        if(!cartellaSalvataggio.exists()) {
        	boolean creata = cartellaSalvataggio.mkdirs();
        }
		String filePath = pathCartella + "/" + REPORT_FILE_NAME + nomeGriglia + "_" + combinazione + ".txt";
		ScritturaFile.pulisciFile(filePath);
		System.out.println(report);
		ScritturaFile.writeToFile(filePath, report);
	}
	
	private static void scriviComparazioniSuFile(List<IStatisticheEsecuzione> statistiche, String nomeGriglia) {
		String filePath = CARTELLA_REPORT + REPORT_FILE_NAME + nomeGriglia + "_CONFRONTI.md";
		ScritturaFile.pulisciFile(filePath);
		StringBuilder sb = new StringBuilder();
		sb.append("# Confronto di dati per la griglia " + nomeGriglia + "\n");
		
		sb.append("## Risultati Generali\n\n");
		
		sb.append("| Combinazione | Tempo | Profondita' | Spazio Occupato | Celle di Frontiera Considerate "
				+ "| Lunghezza Cammino | Numero Landmark |\n");
		sb.append("|---|---|---|---|---|---|---|\n");
		
		for(IStatisticheEsecuzione stat : statistiche) {
			// Nome Combinazione
			sb.append("| " + stat.getCompitoTreMode().getConfigurationName() + "_" + stat.getNomeCompitoDue() + " | ");
			// Tempo di Esecuzione
			sb.append(Utils.formatTempo(stat.getTempoEsecuzione() )+ " | ");
			// Massima Profondita'
			sb.append(stat.getMaxDepth() + " | ");
			// Spazio Occupato
			sb.append(CalcoloUsoMemoria.formattaMemoria(CalcoloUsoMemoria.calcolaUsoMemoria(stat, TipoCella.TIPO_A)) + " | ");
			// Celle di Frontiera Considerate
			sb.append(stat.getQuantitaCelleFrontiera() + " | ");
			// Lunghezza Cammino
			sb.append(stat.getCammino().lunghezza() + " | ");
			// Numero Landmark
			sb.append(stat.getCammino().landmarks().size() + " |\n");
		}
		
		
		sb.append("\n## Statistiche Implementazioni Aggiuntive\n\n");
		sb.append("| Combinazione | Iterazioni Condizione | Usi di Cache | Svuota Frontiera Totale |\n");
		sb.append("|---|---|---|---|\n");
		for(IStatisticheEsecuzione stat : statistiche) {
			// Nome Combinazione
			sb.append("| " + stat.getCompitoTreMode().getConfigurationName() + "_" + stat.getNomeCompitoDue() + " | ");
			// Iterazioni Condizione
			sb.append(stat.getIterazioniCondizione() + " | ");
			// Cache Hit
			sb.append(stat.getCacheHit() + " | ");
			// Svuota Frontiera
			sb.append(stat.getQuantitaSvuotaFrontiera() + " |\n");
		}
		
		ScritturaFile.writeToFile(filePath, sb.toString());
		System.out.println(sb.toString());
	}

	private static List<Path> recuperaPathGriglie(Path cartellaGriglie, List<String> nomiGriglie) {
		List<Path> paths = new ArrayList<>();
		try (Stream<Path> stream = Files.list(cartellaGriglie)) {
			paths = stream.toList();
			paths.forEach(path -> {
				String nomeCompleto = path.getFileName().toString();
				nomiGriglie.add(nomeCompleto.substring(0, nomeCompleto.lastIndexOf('.')));
			});
		} catch (IOException e) {
			System.out.println("Impossibile leggere la cartella assegnata");
			e.printStackTrace();
		}
		return paths;
	}
}
