package matteo.Riassunto;

public enum TipiRiassunto {
    VERBOSE,     // Modo verbose, quello che era in StatisticheEsecuzione originariamente
    TABELLA,     // Formato tabella con i dati principali
    COMPATTO,    // Versione ridotta con solo le informazioni essenziali
    JSON,        // Formato JSON per export
    CSV,         // Formato CSV per analisi dati
    MARKDOWN     // Formato Markdown per documentazione
}