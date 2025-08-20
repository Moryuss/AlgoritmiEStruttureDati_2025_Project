package sperimentazione;

import java.io.*;
import java.nio.file.*;
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
            System.out.println("Conversione completata in " + OUTPUT_CSV);
        } catch (IOException e) {
            System.err.println("Errore conversione: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void convertTxtToCsv() throws IOException {
        Path folderPath = Paths.get(FOLDER_PATH);
        
        if (!Files.exists(folderPath)) {
            throw new IOException("Folder not found: " + FOLDER_PATH);
        }
        
        try (BufferedWriter csvWriter = Files.newBufferedWriter(Paths.get(OUTPUT_CSV))) {
            // Write CSV header
            csvWriter.write("File,Cache_Usata,Sorted_Frontiera,Media_Cache_Hit,Media_Celle_Frontiera," +
                           "Media_Iterazioni_Condizione,Tempo," +
                           "Spazio_KB,Massima_Profondita,Esecuzioni_Corrette,Timeout,Destinazione_Irraggiungibile");
            csvWriter.newLine();
            
            // Process all txt files recursively
            Files.walk(folderPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().toLowerCase().endsWith(".txt"))
                .forEach(txtFile -> {
                    try {
                        processFile(txtFile, csvWriter);
                    } catch (IOException e) {
                        System.err.println("Errore nella lettura del file " + txtFile + ": " + e.getMessage());
                    }
                });
        }
    }
    
    private static void processFile(Path txtFile, BufferedWriter csvWriter) throws IOException {
        String content = Files.readString(txtFile);
        
        // Check if file contains the pattern we're looking for
        if (!content.contains(PATTERN_START)) {
            return; // Skip files that don't contain our pattern
        }
        
        // Extract the section starting from PATTERN_START
        int patternIndex = content.indexOf(PATTERN_START);
        String relevantSection = content.substring(patternIndex);
        
        // Check for timeout before the pattern (optional - defaults to false if not found)
        String beforePattern = content.substring(0, patternIndex);
        boolean isTimeout = TIMEOUT_PATTERN.matcher(beforePattern).find();
        
        // Check for "Destinazione Irraggiungibile" in the relevant section
        boolean isDestinationUnreachable = DESTINAZIONE_IRRAGGIUNGIBILE_PATTERN.matcher(relevantSection).find();
        
        // Extract values using regex patterns
        ExtractionResult result = extractValues(relevantSection);
        
        if (result != null) {
            // Write to CSV
            csvWriter.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                txtFile.getFileName().toString(),
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
        }
    }
    
    private static ExtractionResult extractValues(String section) {
        ExtractionResult result = new ExtractionResult();
        
        try {
            // Extract Cache usata
            Matcher matcher = CACHE_PATTERN.matcher(section);
            if (matcher.find()) {
                result.cacheUsata = matcher.group(1);
            } else return null;
            
            // Extract Sorted Frontiera
            matcher = SORTED_PATTERN.matcher(section);
            if (matcher.find()) {
                result.sortedFrontiera = matcher.group(1);
            } else return null;
            
            // Extract Media Cache hit
            matcher = CACHE_HIT_PATTERN.matcher(section);
            if (matcher.find()) {
                result.mediaCacheHit = matcher.group(1);
            } else return null;
            
            // Extract Media Celle di Frontiera
            matcher = CELLE_FRONTIERA_PATTERN.matcher(section);
            if (matcher.find()) {
                result.mediaCelleFrontiera = matcher.group(1);
            } else return null;
            
            // Extract Media Iterazioni Condizione
            matcher = ITERAZIONI_PATTERN.matcher(section);
            if (matcher.find()) {
                result.mediaIterazioniCondizione = matcher.group(1);
            } else return null;
            
            // Extract Tempo d'Esecuzione (get ns value and convert using Utils)
            matcher = TEMPO_PATTERN.matcher(section);
            if (matcher.find()) {
                String tempoNs = matcher.group(1);
                long castedTime = Integer.parseInt(tempoNs);
                String tempoConvertito = Utils.tempoToString(castedTime);
                result.tempo = tempoConvertito;
            } else return null;
            
            // Extract Spazio Occupato (handles B, KB, and MB - converts all to KB)
            matcher = SPAZIO_PATTERN.matcher(section);
            if (matcher.find()) {
                String spazioValue = matcher.group(1);
                String unit = matcher.group(2);
                
                if ("MB".equals(unit)) {
                    // Convert MB to KB: multiply by 1024
                    double mbValue = Double.parseDouble(spazioValue.replace(",", "."));
                    double kbValue = mbValue * 1024;
                    result.spazioKB = String.format("%.2f", kbValue).replace(".", ",");
                } else if ("B".equals(unit)) {
                    // Convert B to KB: divide by 1024
                    double bValue = Double.parseDouble(spazioValue.replace(",", "."));
                    double kbValue = bValue / 1024;
                    result.spazioKB = String.format("%.3f", kbValue).replace(".", ",");
                } else {
                    // Already in KB
                    result.spazioKB = spazioValue;
                }
            } else return null;
            
            // Extract Massima Profondita'
            matcher = PROFONDITA_PATTERN.matcher(section);
            if (matcher.find()) {
                result.massimaProfondita = matcher.group(1);
            } else return null;
            
            // Extract Esecuzioni corrette
            matcher = CORRETTE_PATTERN.matcher(section);
            if (matcher.find()) {
                result.esecuzioniCorrette = matcher.group(1);
            } else return null;
            
            return result;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    // Helper class to store extracted values
    private static class ExtractionResult {
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
