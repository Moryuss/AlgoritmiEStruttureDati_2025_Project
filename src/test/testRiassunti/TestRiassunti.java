package test.testRiassunti;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import francesco.ICella2D;
import matteo.ConfigurationMode;
import matteo.ICammino;
import matteo.ILandmark;
import matteo.Riassunto.IStatisticheEsecuzione;
import matteo.Riassunto.Riassunto;
import matteo.Riassunto.StatisticheEsecuzione;
import matteo.Riassunto.TipiRiassunto;
import nicolas.DistanzaLibera;

import java.util.List;
import java.util.ArrayList;

public class TestRiassunti {

	boolean stampaRiassunto = false; // Variabile per decidere se stampare i riassunti
	boolean salvaRiassunto = false; // Imposta a true per salvare su files i riassunti
	private IStatisticheEsecuzione stats;

	@BeforeEach
	void setUp() {
		stats = new StatisticheEsecuzione();

		// Setup dati di esempio
		stats.saveDimensioniGriglia(20, 30);
		stats.saveTipoGriglia(1);
		stats.setOrigine(new CellaEsempio(5, 3));
		stats.setDestinazione(new CellaEsempio(18, 15));

		// Simula statistiche
		for (int i = 0; i < 150; i++) stats.incrementaCelleFrontiera();
		for (int i = 0; i < 500; i++) stats.incrementaIterazioniCondizione();
		for (int i = 0; i < 25; i++) stats.incrementaCacheHit();
		for (int i = 0; i < 3; i++) stats.incrementaSvuotaFrontiera();

		stats.setCache(true);
		stats.setFrontieraSorted(true);
		stats.setSvuotaFrontiera(true);
		stats.setMaxDepth(12);
		stats.setCompitoTreMode(ConfigurationMode.DEFAULT.toCamminoConfiguration());
		stats.setNomeCompitoDue("Compito Due Test");
		stats.setCammino(new CamminoEsempio());
		stats.saveTime();
	}

	@Test
	@DisplayName("Visualizza tutti i tipi di riassunto")
	void visualizzaTuttiIRiassunti() {
		if (stampaRiassunto) {

			System.out.println("\n=== DEMO DI TUTTI I TIPI DI RIASSUNTO ===\n");

			for (TipiRiassunto tipo : TipiRiassunto.values()) {
				System.out.println("🔸".repeat(40));
				System.out.println("   " + tipo.name());
				System.out.println("🔸".repeat(40));

				Riassunto riassunto = stats.generaRiassunto(tipo);
				riassunto.stampa();
				System.out.println("\n" + "─".repeat(80) + "\n");
			}
		}
	}

	@Test
	@DisplayName("Test formato VERBOSE")
	void testVerbose() {
		Riassunto riassunto = stats.generaRiassunto(TipiRiassunto.VERBOSE);
		if(stampaRiassunto) {
			System.out.println("\n--- VERBOSE ---");
			riassunto.stampa();
		}
	}

	@Test
	@DisplayName("Test formato TABELLA")
	void testTabella() {
		Riassunto riassunto = stats.generaRiassunto(TipiRiassunto.TABELLA);
		if(stampaRiassunto) {
			System.out.println("\n--- TABELLA ---");
			riassunto.stampa();
		}
	}

	@Test
	@DisplayName("Test formato COMPATTO")
	void testCompatto() {
		Riassunto riassunto = stats.generaRiassunto(TipiRiassunto.COMPATTO);
		if(stampaRiassunto) {
			System.out.println("\n--- COMPATTO ---");
			riassunto.stampa();
		}
	}

	@Test
	@DisplayName("Test formato JSON")
	void testJson() {
		Riassunto riassunto = stats.generaRiassunto(TipiRiassunto.JSON);
		if(stampaRiassunto) {
			System.out.println("\n--- JSON ---");
			riassunto.stampa();
		}
	}

	@Test
	@DisplayName("Test formato CSV")
	void testCsv() {
		Riassunto riassunto = stats.generaRiassunto(TipiRiassunto.CSV);
		if(stampaRiassunto) {
			System.out.println("\n--- CSV ---");
			riassunto.stampa();
		}
	}

	@Test
	@DisplayName("Test formato MARKDOWN")
	void testMarkdown() {
		Riassunto riassunto = stats.generaRiassunto(TipiRiassunto.MARKDOWN);
		if(stampaRiassunto) {
			System.out.println("\n--- MARKDOWN ---");
			riassunto.stampa();
		}
	}
	@Test
	@DisplayName("Test salvataggio file - tutti i formati")
	void testSalvataggioFile() {
		String baseDir = "src/test/testRiassunti/tutti_formati";

		if (stampaRiassunto) {
			System.out.println("\n=== TEST SALVATAGGIO FILE ===");
			System.out.println("Directory: " + baseDir);
		}

		for (TipiRiassunto tipo : TipiRiassunto.values()) {
			Riassunto riassunto = stats.generaRiassunto(tipo);
			String nomeFile = "riassunto_" + tipo.name().toLowerCase();

			try {
				if(salvaRiassunto) {
					// Test salvataggio con estensione automatica
					riassunto.salvaFile(nomeFile, baseDir);
					if (stampaRiassunto) {
						System.out.println("✓ Salvato: " + tipo.name());
					}
				}
			} catch (Exception e) {
				if (stampaRiassunto) {
					System.err.println("✗ Errore salvando " + tipo.name() + ": " + e.getMessage());
				}
			}
		}

		if (stampaRiassunto) {
			System.out.println("=== FINE TEST SALVATAGGIO ===\n");
		}
	}

	@Test
	@DisplayName("Test salvataggio con estensioni personalizzate")
	void testSalvataggioEstensioniPersonalizzate() {

		if (stampaRiassunto) {
			System.out.println("\n=== TEST ESTENSIONI PERSONALIZZATE ===");
		}

		// Test con estensioni forzate
		Riassunto json = stats.generaRiassunto(TipiRiassunto.JSON);
		if(salvaRiassunto) json.salvaFile("custom_report.data", "src/test/testRiassunti/estensioni_personalizzate"); // Forza .data invece di .json

		Riassunto csv = stats.generaRiassunto(TipiRiassunto.CSV);
		if(salvaRiassunto) csv.salvaFile("spreadsheet.txt", "src/test/testRiassunti/estensioni_personalizzate"); // Forza .txt invece di .csv

		Riassunto markdown = stats.generaRiassunto(TipiRiassunto.MARKDOWN);
		if(salvaRiassunto) markdown.salvaFile("src/test/testRiassunti/estensioni_personalizzate/readme"); // Estensione automatica (.md)

		if (stampaRiassunto) {
			System.out.println("✓ Test estensioni completato");
			System.out.println("=== FINE TEST ESTENSIONI ===\n");
		}
	}

	@Test
	@DisplayName("Test salvataggio directory corrente")
	void testSalvataggioDirectoryCorrente() {
		if (stampaRiassunto) {
			System.out.println("\n=== TEST DIRECTORY CORRENTE ===");
		}

		Riassunto compatto = stats.generaRiassunto(TipiRiassunto.COMPATTO);

		try {
			if(salvaRiassunto) {
				compatto.salvaFile("src/test/testRiassunti/dir_corrente/riassunto_compatto");
				if (stampaRiassunto) {
					System.out.println("✓ File salvato nella directory corrente");
				}
			}
		} catch (Exception e) {
			if (stampaRiassunto) {
				System.err.println("✗ Errore: " + e.getMessage());
			}
		}

		if (stampaRiassunto) {
			System.out.println("=== FINE TEST DIRECTORY CORRENTE ===\n");
		}
	}



	// Classi helper per il test
	private static class CellaEsempio implements ICella2D {
		private final int x, y;

		public CellaEsempio(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int x() { return x; }

		@Override
		public int y() { return y; }

		@Override
		public int stato() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	private static class CamminoEsempio implements ICammino {
		@Override
		public double lunghezza() {
			return 25;
		}

		@Override
		public List<ILandmark> landmarks() {
			List<ILandmark> landmarks = new ArrayList<>();
			landmarks.add(new LandmarkEsempio(5, 3));
			landmarks.add(new LandmarkEsempio(10, 8));
			landmarks.add(new LandmarkEsempio(15, 12));
			landmarks.add(new LandmarkEsempio(18, 15));
			return landmarks;
		}

		@Override
		public DistanzaLibera distanzaLibera() {
			return DistanzaLibera.ZERO;
		}
	}

	private static class LandmarkEsempio implements ILandmark {
		private final int x, y;

		public LandmarkEsempio(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int x() { return x; }

		@Override
		public int y() { return y; }

		@Override
		public int stato() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
