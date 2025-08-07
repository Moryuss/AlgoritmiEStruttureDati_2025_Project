package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import francesco.ICella2D;
import matteo.ConfigurationMode;
import matteo.ICammino;
import matteo.ILandmark;
import matteo.Riassunto.IStatisticheEsecuzione;
import matteo.Riassunto.Riassunto;
import matteo.Riassunto.StatisticheEsecuzione;
import matteo.Riassunto.TipiRiassunto;

class TestStatisticheEsecuzione {
    
    private IStatisticheEsecuzione stats;
    private ICella2D mockOrigine;
    private ICella2D mockDestinazione;
    private ICammino mockRisultato;
    
    private boolean stampaRiassunto = false;
    
    @BeforeEach
    void setUp() {
        stats = new StatisticheEsecuzione();
        
        // Mock delle celle (assumendo che abbiano metodi x() e y())
        mockOrigine = new ICella2D() {
            @Override
            public int x() { return 0; }
            @Override
            public int y() { return 0; }
			@Override
			public int stato() {
				// TODO Auto-generated method stub
				return 1;
			}
        };
        
        mockDestinazione = new ICella2D() {
            @Override
            public int x() { return 5; }
            @Override
            public int y() { return 5; }
			@Override
			public int stato() {
				// TODO Auto-generated method stub
				return 0;
			}
        };
        
        // Mock del risultato cammino
        mockRisultato = new ICammino() {
            @Override
            public double lunghezza() { return 7.07; }
            @Override
            public List<ILandmark> landmarks() {
                return List.of(
                    new ILandmark() {
                        @Override
                        public int x() { return 2; }
                        @Override
                        public int y() { return 2; }
						@Override
						public int stato() {
							// TODO Auto-generated method stub
							return 1;
						}
                    },
                    new ILandmark() {
                        @Override
                        public int x() { return 4; }
                        @Override
                        public int y() { return 4; }
						@Override
						public int stato() {
							// TODO Auto-generated method stub
							return 2;
						}
                    }
                );
            }
			@Override
			public int lunghezzaTorre() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public int lunghezzaAlfiere() {
				// TODO Auto-generated method stub
				return 0;
			}
        };
    }
    
    @Test
    void testTutteFunzionalitaStatistiche() {
        // Test salvataggio dimensioni griglia
        stats.saveDimensioniGriglia(10, 20);
        assertEquals(10, stats.getAltezzaGriglia());
        assertEquals(20, stats.getLarghezzaGriglia());
        
        // Test tipo griglia
        stats.saveTipoGriglia(1);
        assertEquals(1, stats.getTipoGriglia());
        
        // Test origine e destinazione
        stats.setOrigine(mockOrigine);
        stats.setDestinazione(mockDestinazione);
        assertEquals(mockOrigine, stats.getOrigine());
        assertEquals(mockDestinazione, stats.getDestinazione());
        
        // Test incrementi contatori
        stats.incrementaCelleFrontiera();
        stats.incrementaCelleFrontiera();
        stats.incrementaIterazioniCondizione();
        stats.incrementaCacheHit();
        stats.incrementaSvuotaFrontiera();
        assertEquals(2, stats.getQuantitaCelleFrontiera());
        assertEquals(1, stats.getIterazioniCondizione());
        assertEquals(1, stats.getCacheHit());
		assertEquals(1, stats.getQuantitaSvuotaFrontiera());
		
        // Test prestazioni
        stats.aggiungiPrestazione("Test prestazione 1");
        stats.aggiungiPrestazione("Test prestazione 2");
        List<String> prestazioni = stats.getPrestazioni();
        assertEquals(2, prestazioni.size());
        assertTrue(prestazioni.contains("Test prestazione 1"));
        assertTrue(prestazioni.contains("Test prestazione 2"));
        
        // Test flag booleani
        assertFalse(stats.isCalcoloInterrotto());
        stats.interrompiCalcolo();
        assertTrue(stats.isCalcoloInterrotto());
        
        assertFalse(stats.isCacheAttiva());
        stats.setCache(true);
        assertTrue(stats.isCacheAttiva());
        
        assertFalse(stats.isFrontieraSorted());
        stats.setFrontieraSorted(true);
        assertTrue(stats.isFrontieraSorted());
        
        assertFalse(stats.isSvuotaFrontieraAttiva());
        stats.setSvuotaFrontiera(true);
        assertTrue(stats.isSvuotaFrontieraAttiva());
        
        // Test configurazione mode
        assertEquals(ConfigurationMode.DEFAULT.toCamminoConfiguration(), stats.getCompitoTreMode());
        stats.setCompitoTreMode(ConfigurationMode.PERFORMANCE.toCamminoConfiguration()); 
        assertEquals(ConfigurationMode.PERFORMANCE.toCamminoConfiguration(),
        		stats.getCompitoTreMode());
        
        // Test tempo esecuzione
        //Tempo è inziato quando è stato creato l'oggetto StatisticheEsecuzione
        stats.saveTime();
        long tempoEsecuzione = stats.getTempoEsecuzione();
        assertTrue(tempoEsecuzione > 1, "Il tempo di esecuzione dovrebbe essere positivo");
        
        // Test generazione riassunto
        stats.setCammino(mockRisultato);
        String riassunto = stats.generaRiassunto(mockRisultato);
        
        // Verifica che il riassunto contenga le informazioni principali
        assertTrue(riassunto.contains("=== RIASSUNTO ESECUZIONE CAMMINOMIN ==="));
        assertTrue(riassunto.contains("Width = 20, Height = 10"));
        assertTrue(riassunto.contains("Tipo griglia: 1"));
        assertTrue(riassunto.contains("Origine: (0,0)"));
        assertTrue(riassunto.contains("Destinazione: (5,5)"));
        assertTrue(riassunto.contains("Tempo di esecuzione:"));
        assertTrue(riassunto.contains("Totale celle di frontiera considerate: 2"));
        assertTrue(riassunto.contains("Totale iterazioni condizione (riga 16/17): 1"));
        assertTrue(riassunto.contains("Calcolo interrotto: SI"));
        assertTrue(riassunto.contains("Cache attiva: SI"));
        assertTrue(riassunto.contains("Cache hit: 1"));
        assertTrue(riassunto.contains("Frontiera sorted: SI"));
        assertTrue(riassunto.contains("Lunghezza cammino trovato: 7.07"));
        assertTrue(riassunto.contains("Numero landmarks: 2"));
        assertTrue(riassunto.contains("<(2,2),1>, <(4,4),2>"));
        
        // Test formattazione tempo
        assertTrue(riassunto.contains("ns"), "Il riassunto dovrebbe contenere nanosecondi");
        
        // Test con risultato null
        stats.setCammino(null);
        Riassunto riassuntoSenzaRisultato = stats.generaRiassunto(TipiRiassunto.VERBOSE);
        assertFalse(riassuntoSenzaRisultato.getContenuto().contains("Lunghezza cammino"));
        assertFalse(riassuntoSenzaRisultato.getContenuto().contains("Numero landmarks"));
        
        // Test saveTime
        stats.saveTime();
        long tempoSalvato = stats.getTempoEsecuzione();
        assertTrue(tempoSalvato > 0);
        
      
		if(stampaRiassunto) {
        System.out.println("Test completato con successo!");
        System.out.println("Esempio di riassunto generato:");
        System.out.println(riassunto);
    }
    }
}