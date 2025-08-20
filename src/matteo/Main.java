package matteo;

import static nicolas.StatoCella.OSTACOLO;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import matteo.Riassunto.IStatisticheEsecuzione;
import matteo.Riassunto.Riassunto;
import matteo.Riassunto.TipoRiassunto;
import nicolas.CompitoDueImpl;
import nicolas.GrigliaConOrigineFactory;
import nicolas.IGrigliaConOrigine;
import nicolas.StatoCella;
import processing.core.PApplet;

public class Main {

	private static IGriglia<?> griglia = null;
	static ICompitoTre c;

	//VARIABILI:
	private static String FILE_DI_CONFIGURAZIONE = "src/matteo/config.json";

	private static  int TEMPO_LIMITE = Integer.MAX_VALUE; 
	private static TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	private static int ORIGINE_X = 0;
	private static int ORIGINE_Y = 0;

	private static int DESTINAZIONE_X = 0;
	private static int DESTINAZIONE_Y = 0;

	private static CompitoDueImpl COMPITO_DUE_MODALITA = CompitoDueImpl.V0;

	//Modalità di esecuzione personalizzabile per il Compito Tre
	private static CamminoConfiguration COMPITO_TRE_MODALITA;


	private static TipoRiassunto TIPO_RIASSUNTO = TipoRiassunto.VERBOSE;
	/**
	 * Punto di ingresso principale dell'applicazione.
	 * Carica la griglia da un file JSON e calcola il cammino minimo tra due celle.
	 * Stampa le statistiche di esecuzione al termine.
	 * 
	 */
	public static void main(String[] args) {
		//Generazioen della griglia dal file config,json
		try {

			griglia = new LettoreGriglia().crea(Path.of(FILE_DI_CONFIGURAZIONE));

			//carica le impostazioni
			File file = new File(FILE_DI_CONFIGURAZIONE);
			var config = PApplet.loadJSONObject(file).getJSONObject("impostazioniMain");

			TEMPO_LIMITE = config.getInt("tempoLimite");
			TIME_UNIT = TimeUnit.valueOf(config.getString("timeUnit").toUpperCase());
			ORIGINE_X = config.getInt("origineX");
			ORIGINE_Y = config.getInt("origineY");
			DESTINAZIONE_X = config.getInt("destinazioneX");
			DESTINAZIONE_Y = config.getInt("destinazioneY");
			COMPITO_DUE_MODALITA = CompitoDueImpl.valueOf(config.getString("compitoDueModalita").toUpperCase());
			
			var compitoTreModalitaFlags = config.getJSONObject("compitoTreModalitaFlags");
			COMPITO_TRE_MODALITA = CamminoConfiguration.custom(Stream.of(ConfigurationFlag.values())
			    .filter(k -> compitoTreModalitaFlags.hasKey(k.name()) && compitoTreModalitaFlags.getBoolean(k.name(), false))
			    .toArray(ConfigurationFlag[]::new));
			
			TIPO_RIASSUNTO = TipoRiassunto.valueOf(config.getString("tipoRiassunto").toUpperCase());

		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}

		//Inpostazione della modlaità di esecuzione del CompitoTre
		c = new CompitoTreImplementation(COMPITO_TRE_MODALITA);


		// Imposta un timeout per l'esecuzione del compito
		c.setTimeout(TEMPO_LIMITE, TIME_UNIT); 

		//Generazione di Origine e Destinazione
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		if(g == null) {
			System.err.println("Errore nella creazione della griglia con origine.");
			return; // Esci se la griglia con origine non può essere creata
		}

		ICella2D origine = getCellaSafe(g, ORIGINE_X, ORIGINE_Y);	// Punto di partenza
		ICella2D destinazione = getCellaSafe(g, DESTINAZIONE_X, DESTINAZIONE_Y);	 // Punto di arrivo

		if(origine.is(StatoCella.OSTACOLO) || destinazione.is(StatoCella.OSTACOLO)) {
			System.err.println("Origine o destinazione sono ostacoli, impossibile calcolare il cammino.");
			return; // Esci se origine o destinazione sono ostacoli
		}

		//Stampa su console la griglia
		printGriglia(griglia);

		ICammino cammino = c.camminoMin(griglia, origine,
				destinazione,
				COMPITO_DUE_MODALITA);


		//Stampa il riassunto nel tipo specificato
		stampaRiassunto(c.getStatisticheEsecuzione() ,TIPO_RIASSUNTO);

	}

	private static ICella2D getCellaSafe(IGrigliaConOrigine g, int cellaX, int cellaY) {
		try {
			return g.getCellaAt(cellaX,cellaY);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Errore nella creazione della cella: " + e.getMessage());
			System.exit(1); // Ritorna 1 se la cella non può essere creata
		}
		return null; // Non dovrebbe mai arrivare qui, ma per evitare errori di compilazione
	}

	/**
	 * Stampa un riassunto delle statistiche di esecuzione.
	 * @param stats Le statistiche da stampare.
	 * @param tipo Il tipo di riassunto da generare.
	 */
	public static void stampaRiassunto(IStatisticheEsecuzione stats,TipoRiassunto tipo) {
		Riassunto riassunto = stats.generaRiassunto(tipo);
		riassunto.stampa();
	}

	private static void printGriglia(IGriglia<?> griglia) {
		System.out.println(griglia.collect(c->c.is(OSTACOLO)?"██":".'", Collectors.joining(), Collectors.joining("\n")));
	}
}
