package matteo.Riassunto;

import java.util.List;
import java.util.stream.Stream;

public enum TipoRiassunto {
    VERBOSE,     // Modo verbose, quello che era in StatisticheEsecuzione originariamente
    TABELLA,     // Formato tabella con i dati principali
    COMPATTO,    // Versione ridotta con solo le informazioni essenziali
    JSON,        // Formato JSON per export
    CSV,         // Formato CSV per analisi dati
    MARKDOWN;    // Formato Markdown per documentazione
	
	public final int mask = 1<<ordinal();
	public static final int LENGTH = TipoRiassunto.values().length;
	
	public boolean isIn(int n) {
		return (n&mask)>0;
	}
	
	public static List<TipoRiassunto> from(int n) {
		return Stream.of(values())
		.filter(t->t.isIn(n))
		.toList();
	}
	
}