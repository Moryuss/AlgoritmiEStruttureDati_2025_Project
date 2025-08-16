package matteo.Riassunto;

public class Riassunto {
    private final TipoRiassunto tipo;
    private final String contenuto;
    
    public Riassunto(TipoRiassunto tipo, String contenuto) {
        this.tipo = tipo;
        this.contenuto = contenuto;
    }
    
    public TipoRiassunto getTipo() {
        return tipo;
    }
    
    public String getContenuto() {
        return contenuto;
    }
    
    @Override
    public String toString() {
        return contenuto;
    }
    
    public void stampa() {
        System.out.println(contenuto);
    }
    
    /**
	 * Salva il riassunto in un file con estensione appropriata in base al tipo.
	 * Se il nome del file contiene già un'estensione, la mantiene.
	 * Nota che il metodo non gestisce la creazione di directory.
	 * @param nomeFile Il nome del file da salvare (senza estensione).
	 */
    public void salvaFile(String nomeFile) {
        try {
            String nomeCompleto = aggiungiEstensione(nomeFile);
            java.nio.file.Files.write(
                java.nio.file.Paths.get(nomeCompleto), 
                contenuto.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
//            System.out.println("Riassunto salvato in: " + nomeCompleto);
        } catch (java.io.IOException e) {
            System.err.println("Errore durante il salvataggio del file: " + e.getMessage());
            throw new RuntimeException("Impossibile salvare il file: " + nomeFile, e);
        }
    }
    
    private String aggiungiEstensione(String nomeFile) {
        // Se il file ha già un'estensione, la mantiene
        if (nomeFile.contains(".")) {
            return nomeFile;
        }
        
        // Altrimenti aggiunge l'estensione appropriata in base al tipo
        switch (tipo) {
            case JSON:
                return nomeFile + ".json";
            case CSV:
                return nomeFile + ".csv";
            case MARKDOWN:
                return nomeFile + ".md";
            case VERBOSE:
            case TABELLA:
            case COMPATTO:
            default:
                return nomeFile + ".txt";
        }
    }
    
    public void salvaFile(String nomeFile, String directory) {
        try {
            java.nio.file.Path dirPath = java.nio.file.Paths.get(directory);
            if (!java.nio.file.Files.exists(dirPath)) {
                java.nio.file.Files.createDirectories(dirPath);
            }
            
            String nomeCompleto = aggiungiEstensione(nomeFile);
            java.nio.file.Path filePath = dirPath.resolve(nomeCompleto);
            
            java.nio.file.Files.write(
                filePath,
                contenuto.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            //System.out.println("Riassunto salvato in: " + filePath.toAbsolutePath());
        } catch (java.io.IOException e) {
            //System.err.println("Errore durante il salvataggio del file: " + e.getMessage());
            throw new RuntimeException("Impossibile salvare il file: " + nomeFile + " in " + directory, e);
        }
    }
}
