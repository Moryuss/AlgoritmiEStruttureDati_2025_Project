package main;

import static nicolas.StatoCella.OSTACOLO;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import matteo.CamminoConfiguration;
import matteo.CompitoTreImplementation;
import matteo.ConfigurationFlag;
import matteo.ICammino;
import matteo.ICompitoTre;
import matteo.Riassunto.IStatisticheEsecuzione;
import matteo.Riassunto.Riassunto;
import matteo.Riassunto.TipoRiassunto;
import nicolas.CompitoDueImpl;
import nicolas.GrigliaConOrigineFactory;
import nicolas.IGrigliaConOrigine;
import nicolas.StatoCella;

public class Main {

	private static IGriglia<?> griglia = null;
	static ICompitoTre c;

	//VARIABILI:
	private static String FILE_DI_CONFIGURAZIONE = "config.json";

	private static final int TEMPO_LIMITE = 30;
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	private static final int ORIGINE_X = 0;
	private static final int ORIGINE_Y = 0;

	private static final int DESTINAZIONE_X = 39;
	private static final int DESTINAZIONE_Y = 19;

	private static final CompitoDueImpl COMPITO_DUE_MODALITA = CompitoDueImpl.V0;

	/**
	 * Configurazione per il Compito Tre, utilizzando configurazioni pre-esistenti
	 * 
	private static final CamminoConfiguration COMPITO_TRE_MODALITA = ConfigurationMode.DEFAULT.
																		toCamminoConfiguration();
	 */	
	//Modalità di esecuzione personalizzabile per il Compito Tre
	private static final CamminoConfiguration COMPITO_TRE_MODALITA = CamminoConfiguration.custom(
			ConfigurationFlag.CONDIZIONE_RAFFORZATA,
			ConfigurationFlag.SVUOTA_FRONTIERA);

	private static final TipoRiassunto TIPO_RIASSUNTO = TipoRiassunto.VERBOSE;
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
