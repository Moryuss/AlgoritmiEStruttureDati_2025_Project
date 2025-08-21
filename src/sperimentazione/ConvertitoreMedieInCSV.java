package sperimentazione;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Utils;

public class ConvertitoreMedieInCSV {
	    
	private static final String FOLDER_PATH = "src/sperimentazione";
    private static final String OUTPUT_CSV = "results.csv";
    
    // Pattern to match the execution results section
    private static final String PATTERN_START = "=============== MEDIA ESECUZIONI ===============";
    
    // Regex patterns to extract values
    private static final Pattern CACHE_PATTERN = Pattern.compile("Cache usata\\? (true|false)");
    private static final Pattern SORTED_PATTERN = Pattern.compile("Sorted Frontiera\\? (true|false)");
    private static final Pattern CACHE_HIT_PATTERN = Pattern.compile("Media Cache hit: (\\d+)");
    private static final Pattern CELLE_FRONTIERA_PATTERN = Pattern.compile("Media Celle di Frontiera: (\\d+)");
    private static final Pattern ITERAZIONI_PATTERN = Pattern.compile("Media Iterazioni Condizione: (\\d+)");
    // Updated pattern to handle all time formats (just ns, ns with ms, full format)
    private static final Pattern TEMPO_PATTERN = Pattern.compile("Media dei Tempi d'Esecuzione: (\\d+) ns(?:\\s*\\(([\\d,]+) ms(?:\\s*=\\s*([\\d,]+)\\s*s\\s*=\\s*([\\d,]+)\\s*m)?\\))?");
    
    // Updated pattern to handle KB, MB, and B
    private static final Pattern SPAZIO_PATTERN = Pattern.compile("Media Spazio Occupato: ([\\d,]+) (KB|MB|B)");
    private static final Pattern PROFONDITA_PATTERN = Pattern.compile("Media Massima Profondita': ([\\d,]+)");
    private static final Pattern CORRETTE_PATTERN = Pattern.compile("Tutte le esecuzioni sono corrette\\? (true|false)");
    
    // Pattern to detect timeout
    private static final Pattern TIMEOUT_PATTERN = Pattern.compile("La Griglia .+ e' stata interrotta per tempo limite \\(.+\\)");
    
    // Pattern to detect unreachable destination
    private static final Pattern DESTINAZIONE_IRRAGGIUNGIBILE_PATTERN = Pattern.compile("Destinazione Irraggiungibile\\.");
    
    public static void main(String[] args) {
        try {
            convertTxtToCsv();
            System.out.println("Conversione completata " + OUTPUT_CSV);
        } catch (IOException e) {
            System.err.println("Errore nel main: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            System.out.println(
                Files.lines(Path.of("results.csv"))
                .map(row -> row.split(",").length)
                .distinct()
                .toList()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    private static void convertTxtToCsv() throws IOException {
        Path folderPath = Paths.get(FOLDER_PATH);
        
        if (!Files.exists(folderPath)) {
            throw new IOException("Folder not found: " + FOLDER_PATH);
        }
        
        try (BufferedWriter csvWriter = Files.newBufferedWriter(Paths.get(OUTPUT_CSV))) {
            // Header del CSV
            csvWriter.write("Categoria_Griglia,Nome_Griglia,Compito_Due,Compito_Tre,Cache_Usata,Sorted_Frontiera,Media_Cache_Hit,Media_Celle_Frontiera," +
                           "Media_Iterazioni_Condizione,Tempo," +
                           "Spazio_KB,Massima_Profondita,Esecuzioni_Corrette,Timeout,Destinazione_Irraggiungibile");
            csvWriter.newLine();
            
            // Ricorsione nella cartella per trovare i file .txt
            Files.walk(folderPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().toLowerCase().endsWith(".txt"))
                .forEach(txtFile -> {
                    try {
                        processFile(txtFile, csvWriter);
                    } catch (IOException e) {
                        System.err.println("Error col file " + txtFile + ": " + e.getMessage());
                    }
                });
        }
    }
    
    private static void processFile(Path txtFile, BufferedWriter csvWriter) throws IOException {
        String content = Files.readString(txtFile);
        
        // Se il file non va bene, viene saltato
        if (!content.contains(PATTERN_START)) {
            return;
        }
                
        // Estrazione della sola parte che comincia con il pattern di media esecuzioni
        int patternIndex = content.indexOf(PATTERN_START);
        String relevantSection = content.substring(patternIndex);
        
        // Controlla di timeout
        String beforePattern = content.substring(0, patternIndex);
        boolean isTimeout = TIMEOUT_PATTERN.matcher(beforePattern).find();
        
        // Controlla di Destinazione Irraggiungibile
        boolean isDestinationUnreachable = DESTINAZIONE_IRRAGGIUNGIBILE_PATTERN.matcher(relevantSection).find();
        
        // Estrazione dei valori con regex
        ExtractionResult result = extractValues(relevantSection);
        
        if (result != null) {
            
            // Estrazione di tipo, nome griglia e compiti
            String nomeFile = txtFile.getFileName().toString();
            String[] parti = nomeFile.split("_");
            String categoriaGriglia = parti.length > 0 ? parti[0] : "Invalido";
            if(categoriaGriglia.contains("analisi")) {
            	// Rimozione della scrtita "analisi"
            	categoriaGriglia = categoriaGriglia.replace("analisi", "").trim();
            }
            if(categoriaGriglia.equals("DoppioDenteDiSega")) {
            	// Cambio legacy
            	categoriaGriglia = "DoppiaLineaSpezzata";
            }
            result.categoriaGriglia = categoriaGriglia;
            result.nomeGriglia = parti.length > 1 ? parti[1] : "Invalido";
            
            // Visto che il nome è salvato con _, allora si predono tutti gli elementi dall'indice in 2 in poi
            // escluso l'ultimo che è il compito due
            result.compitoTreUsato = parti.length > 2 ? String.join("_", 
                    Arrays.copyOfRange(parti, 2, parti.length - 1)) : "Invalido";
            
            // L'ultimo elemento è il compito due
            result.compitoDueUsato = parti.length > 3 ? parti[parti.length - 1] : "Invalido";
            
            if(result.compitoDueUsato.contains(".txt")) {
            	// Rimozione dell'estensione .txt
				result.compitoDueUsato = result.compitoDueUsato.replace(".txt", "").trim();
            }
            // Write to CSV
            csvWriter.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                result.categoriaGriglia,
                result.nomeGriglia,
                result.compitoDueUsato,
                result.compitoTreUsato,
                result.cacheUsata,
                result.sortedFrontiera,
                result.mediaCacheHit,
                result.mediaCelleFrontiera,
                result.mediaIterazioniCondizione,
                result.tempo,
                result.spazioKB,
                result.massimaProfondita,
                result.esecuzioniCorrette,
                isTimeout,
                isDestinationUnreachable
            ));
            csvWriter.newLine();
        } else {
            System.err.println("Errore nell'estrazione dati: " + txtFile.getFileName());
        }
    }
    
    private static ExtractionResult extractValues(String section) {
        ExtractionResult result = new ExtractionResult();
        
        try {
            // Cache usata
            Matcher matcher = CACHE_PATTERN.matcher(section);
            if (matcher.find()) {
                result.cacheUsata = matcher.group(1);
            } else {
                return null;
            }
            
            // Sorted Frontiera
            matcher = SORTED_PATTERN.matcher(section);
            if (matcher.find()) {
                result.sortedFrontiera = matcher.group(1);
            } else {
                return null;
            }
            
            // Media Cache hit
            matcher = CACHE_HIT_PATTERN.matcher(section);
            if (matcher.find()) {
                result.mediaCacheHit = matcher.group(1);
            } else {
                return null;
            }
            
            // Media Celle di Frontiera
            matcher = CELLE_FRONTIERA_PATTERN.matcher(section);
            if (matcher.find()) {
                result.mediaCelleFrontiera = matcher.group(1);
            } else {
                return null;
            }
            
            // Media Iterazioni Condizione
            matcher = ITERAZIONI_PATTERN.matcher(section);
            if (matcher.find()) {
                result.mediaIterazioniCondizione = matcher.group(1);
            } else {
                return null;
            }
            

            // Tempo d'esecuzione
            matcher = TEMPO_PATTERN.matcher(section);
            if (matcher.find()) {
            	long tempo = Long.parseLong(matcher.group(1));
                result.tempo = Utils.tempoToString(tempo);
            } else {
                return null;
            }

            // Spazio occupato
            matcher = SPAZIO_PATTERN.matcher(section);
            if (matcher.find()) {
                String spazioValue = matcher.group(1);
                String unit = matcher.group(2);
                spazioValue = spazioValue.replace(",", "."); 
                if ("MB".equals(unit)) {
                    // Conversione da MB a KB
                    double mbValue = Double.parseDouble(spazioValue);
                    double kbValue = mbValue * 1024;
                    result.spazioKB = kbValue + "";
                } else if ("B".equals(unit)) {
                    // Conversione da B a KB
                    double bValue = Double.parseDouble(spazioValue);
                    double kbValue = bValue / 1024;
                    result.spazioKB = kbValue + "";
                } else {
                    // Already in KB
                    result.spazioKB = spazioValue;
                }
                
            } else {
                return null;
            }
            
            // Massima Profondita'
            matcher = PROFONDITA_PATTERN.matcher(section);
            if (matcher.find()) {
                result.massimaProfondita = matcher.group(1);
            } else {
                return null;
            }
            
            // Esecuzioni corrette
            matcher = CORRETTE_PATTERN.matcher(section);
            if (matcher.find()) {
                result.esecuzioniCorrette = matcher.group(1);
            } else {
                return null;
            }
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    // Classe per contenere i dati d'interesse
    private static class ExtractionResult {
    	String categoriaGriglia;
    	String nomeGriglia;
    	String compitoTreUsato;
    	String compitoDueUsato;
        String cacheUsata;
        String sortedFrontiera;
        String mediaCacheHit;
        String mediaCelleFrontiera;
        String mediaIterazioniCondizione;
        String tempo;
        String spazioKB;
        String massimaProfondita;
        String esecuzioniCorrette;
    }
}
