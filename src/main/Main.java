package main;

import static nicolas.StatoCella.OSTACOLO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import matteo.CamminoConfiguration;
import matteo.CompitoTreImplementation;
import matteo.ConfigurationFlag;
import matteo.ConfigurationMode;
import matteo.ICammino;
import matteo.ICompitoTre;
import matteo.IProgressoMonitor;
import matteo.ProgressoMonitor;
import matteo.Riassunto.IStatisticheEsecuzione;
import matteo.Riassunto.Riassunto;
import matteo.Riassunto.TipoRiassunto;
import nicolas.CompitoDueImpl;
import nicolas.GrigliaConOrigineFactory;
import nicolas.IGrigliaConOrigine;
import nicolas.StatoCella;
import utils.Utils;

public class Main {

	private static IGriglia<?> griglia = null;
	static ICompitoTre c;

	//VARIABILI:
	private static String FILE_DI_CONFIGURAZIONE = "config.json";

	private static final int TIME_STOP = 30;
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	private static final int ORIGINE_X = 0;
	private static final int ORIGINE_Y = 0;

	private static final int DESTINAZIONE_X = 0;
	private static final int DESTINAZIONE_Y = 0;

	private static final CompitoDueImpl COMPITO_DUE_MODALITA = CompitoDueImpl.V0;
	
/**
 * Configurazione per il Compito Tre, utilizzando configurazioni pre-esistenti
 * 
	private static final CamminoConfiguration COMPITO_TRE_MODALITA = ConfigurationMode.DEFAULT.
																		toCamminoConfiguration();
*/	
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
		c.setTimeout(TIME_STOP, TIME_UNIT); 

		//Generazione di Origine e Destinazione
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D origine = g.getCellaAt(ORIGINE_X,ORIGINE_Y);	// Punto di partenza
		ICella2D destinazione = g.getCellaAt(DESTINAZIONE_X,DESTINAZIONE_Y);	 // Punto di arrivo
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
