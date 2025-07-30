package sperimentazione;

import francesco.IGriglia;
import francesco.IHave2DCoordinate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import matteo.CompitoTreImplementation;
import matteo.ConfigurationMode;
import matteo.ICammino;
import matteo.ICompitoTre;
import matteo.ILandmark;
import matteo.IStatisticheEsecuzione;
import nicolas.CompitoDueImpl;
import nicolas.ICellaConDistanze;
import nicolas.ICompitoDue;
import nicolas.IGrigliaConOrigine;
import processing.core.PApplet;
import processing.data.JSONArray;
import utils.Utils;

public class MainSperimentazione {

    // Per il controllo su cammini uguali
    private static final double MAX_DIFF = 1e-10;
    // Numero di volte che una Griglia viene usata per il calcolo del Cammino Minimo
    private static final int GRIGLIA_TRY = 10;
    private static final String LINEA_SPEZZATA_PATH = "src/sperimentazione/lineaspezzata";
    private static final String VARIAZIONE_OSTACOLI_PATH = "src/sperimentazione/varostacoli";
    private static final String VARIAZIONE_DIMENSIONI_PATH = "src/sperimentazione/vardimensioni";

    private static final List<String> CARTELLE = List.of(LINEA_SPEZZATA_PATH,
            VARIAZIONE_OSTACOLI_PATH,
            VARIAZIONE_DIMENSIONI_PATH);

    private static final List<ConfigurationMode> TRES = List.of(ConfigurationMode.DEFAULT,
            ConfigurationMode.PERFORMANCE_NO_CACHE,
            ConfigurationMode.PERFORMANCE_NO_SORTED_FRONTIERA,
            ConfigurationMode.PERFORMANCE_NO_CONDIZIONE_RAFFORZATA,
            ConfigurationMode.PERFORMANCE_CACHE,
            ConfigurationMode.PERFORMANCE_SORTED_FRONTIERA,
            ConfigurationMode.PERFORMANCE_CONDIZIONE_RAFFORZATA,
            ConfigurationMode.PERFORMANCE
    );

    private static final List<ICompitoDue> DUES = List.of(CompitoDueImpl.V0);

    private static String pathTxt;
    private static String compitiUsati;

    // Per qualsiasi cosa, controlla classe Test Compito Tre
    // ma guarda la lettura da file da AppletMain
    public static void main(String[] args) {

        // getReport per il report sul tempo
        // Un ciclo per iterare su molte istanze che usero':
        //1) Confronto del percorso da un estremo all'altro con sole barre verticali disposte per zig zag, varia la dimensione
        //2) Confronto del percorso le cui dimensioni restano fisse ma variano gli ostacoli
        //3) Confronto del percorso con vari numeri di Landmark, confronto sul tempo più che altro
        // Questi tre punti vengono salvati nel loro rispettivo file TXT: al termine dell'esecuzione, si scrive il report che si ottiene
        // con getReport() del CompitoTreImplementation, il "e' corretto?: " con il risultato di isLunghezzaUguale, il "e' opposto?"
        // con il risultato di isOpposto e poi uno spazio commentato per aggiungere commenti:
        // ================================================================
        // COMMENTI: niente
        // ================================================================
    	
        for (String cartella : CARTELLE) {
            System.out.println("Cartella: " + cartella + "\n");
            // HashMap con chiave il nome del tempo e come valore una HashMap con combinazione e tempo
            HashMap<String, TreeMap<Long, String>> tempi = new HashMap<String, TreeMap<Long, String>>();
            List<String> nomi = new ArrayList<>();
            List<CoordinateCella> origini = new ArrayList<>();
            List<CoordinateCella> destinazioni = new ArrayList<>();
            List<JSONArray> files = getGriglie(cartella, nomi, origini, destinazioni);
            if (files == null || files.isEmpty()) {
                System.err.println("Cartella non trovata o vuota: " + cartella);
                continue;
            }

            // Struttura che tiene combinazione, tempo e tipo griglia
            for (ConfigurationMode tre : TRES) {
                ICompitoTre implementazioneTre = new CompitoTreImplementation(tre);
                for (ICompitoDue due : DUES) {
                    String compiti = tre.name() + "_" + due.toString();
                    compitiUsati = compiti;

//					List<String> nomi = new ArrayList<>();
//					List<CoordinateCella> origini = new ArrayList<>();
//					List<CoordinateCella> destinazioni = new ArrayList<>();
//					// nomi, origini e destinazioni vengono riempiti in loco
//					List<JSONArray> files = getGriglie(cartella, nomi, origini, destinazioni);
//					if (files == null || files.isEmpty()) {
//						System.err.println("Cartella non trovata o vuota: " + cartella);
//						continue;
//					}
                    ScritturaFile.pulisciFile(pathTxt + "_" + compitiUsati + ".txt");
                    // Potrei anche farlo eseguire più volte e poi lasciare una media dei tempi...
                    // Mi basta un ciclo for interno
                    for (int i = 0; i < files.size(); i++) {
                    	String nomeGriglia = nomi.get(i);
                    	String path = pathTxt + "_" + nomeGriglia + "_" + compitiUsati;
                    	List<IStatisticheEsecuzione> statistiche = new ArrayList<>();
                        try {
                        	scriviEStampaConPath("Nuova Griglia: " + nomeGriglia, path);
                        	for(int j = 0; j < GRIGLIA_TRY; j++) {
                        		scriviEStampaConPath("Analisi n. " + (j+1), path);
	
	                            // Carica la griglia dal JSONArray
	                            IGriglia<?> griglia = Utils.loadSimple(files.get(i));
	
	                            CoordinateCella origine = origini.get(i);
	                            IGrigliaConOrigine gO = due.calcola(griglia, origine);
	                            ICellaConDistanze start = gO.getCellaAt(origine.x(), origine.y());
	
	                            CoordinateCella destinazione = destinazioni.get(i);
	                            ICellaConDistanze end = gO.getCellaAt(destinazione.x(), destinazione.y());
	
	                            // Trova il cammino minimo con CompitoTre
	                            ICammino cammino1 = implementazioneTre.camminoMin(griglia, start, end, due);
	
	                            String report = implementazioneTre.getReport();
	                            IStatisticheEsecuzione statisticheEsecuzione = implementazioneTre.getStatisticheEsecuzione();
								long tempo = statisticheEsecuzione.getTempoEsecuzione();
	                            aggiungiTempo(tempo, compiti, nomeGriglia, tempi);
	                            statistiche.add(statisticheEsecuzione);
	
	                            // Report ottenuto dal cammino
	                            scriviEStampaConPath(report, path);
	                            scriviEStampaConPath("==============================", path);
	                            System.out.println("Inizio veririfica correttezza...");
	                            // Utilizzo  di un secondo solver ed un secondo cammino per verificare la correttezza
	                            // Questa verifica non deve inficiare sul tempo d'esecuzione del primo cammino
	                            ICammino cammino2 = implementazioneTre.camminoMin(griglia, end, start, due);
	                            boolean corretto = isLunghezzaUguale(cammino1, cammino2);
	                            scriviEStampaConPath("e' corretto?: " + corretto, path);
	
	                            System.out.println("Inizio verifica opposizione...");
	                            boolean opposto = isOpposto(cammino1.landmarks(), cammino2.landmarks());
	                            scriviEStampa("e' opposto?: " + opposto);
	
	                            // Questi sono generati per poi scrivere sul file txt in caso servano osservazioni
	                            scriviEStampaConPath("# COMMENTI: niente", path);
	                            scriviEStampaConPath("==============================", path);
	                            scriviEStampaConPath("\n\n", path);
                        	}
                        } catch (Exception e) {
                            System.err.println("Errore nella sperimentazione sulla griglia numero " + i + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                        // Qui vengono calcolate e scritte le media per griglia
                        scriviMediaEsecuzioni(statistiche, GRIGLIA_TRY, path);
                    }
                }
            }
            // Qui vengono calcolate numerose informazioni legate ai tempi
            scriviInformazioniGenerali(tempi);
        }
    }

    /**
     * Metodo per verificare che un array contenga l'esatto opposto dell'altro
     * (controlla classe Landmark)
     *
     * @param primoCammino
     * @param secondoCammino
     * @return
     */
    private static boolean isOpposto(List<ILandmark> primoCammino, List<ILandmark> secondoCammino) {
        if (primoCammino.size() != secondoCammino.size()) {
            return false;
        }
        int n = primoCammino.size();
        for (int i = 0; i < n; i++) {
            ILandmark a = primoCammino.get(i);
            ILandmark b = secondoCammino.get(n - 1 - i);
            // Per sicurezza, controlliamo anche lo stato, oltre alle coordinate
            if (a.x() != b.x() || a.y() != b.y() || a.stato() != b.stato()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Metodo per verificare la correttezza del percorso (origine-destinazione =
     * destinazione-origine)
     *
     * @param primoCammino
     * @param secondoCammino
     * @return
     */
    private static boolean isLunghezzaUguale(ICammino primoCammino, ICammino secondoCammino) {
        // Uguaglianze tra float non consigliate
        return Math.abs(primoCammino.lunghezza() - secondoCammino.lunghezza()) < MAX_DIFF;
    }

    /**
     * Dato un path iniziale, carica le griglie scritte. La convenzione è l'uso
     * di un file paths.json contenente i seguenti campi: - path: un array di
     * stringhe che rappresentano i percorsi relativi ai file JSON delle griglie
     * (nella stessa cartella) - name: un array di stringhe che rappresentano i
     * nomi delle griglie (ossia il campo da cui attingere, ma usato anche per
     * differenziare le griglie) - ox: un array di interi che rappresentano le
     * origini X delle griglie - oy: un array di interi che rappresentano le
     * origini Y delle griglie - dx: un array di interi che rappresentano le
     * destinazioni X delle griglie - dy: un array di interi che rappresentano
     * le destinazioni Y delle griglie - txt: path al txt dove scrivere report e
     * controlli
     *
     * @param pathIniziale path che porta alla cartella desiderata
     * @param nomi array inizialmente vuoto che verrà riempito con i nomi delle
     * griglie
     * @param origini array inizialmente vuoto che verrà riempito con le
     * coordinate delle celle di origine
     * @param destinazioni array inizialmente vuoto che verrà riempito con le
     * coordinate delle celle di destinazione
     * @return una lista di JSONArray per poter usare {@link Utils} e caricare
     * con loadSimple
     * @precondizioni nomi != null && origini != null && destinazioni != null
     * @postcondizioni nomi viene riempito con tutti i nomi delle griglie
     * trovate, gli array di origini e destinazioni vengono riempiti con le
     * coordinate delle celle di origine e destinazione rispettivamente per ogni
     * griglia trovata.
     */
    private static List<JSONArray> getGriglie(String pathIniziale, List<String> nomi, List<CoordinateCella> origini, List<CoordinateCella> destinazioni) {
        String pathname = pathIniziale + "/paths.json";
        File puntoCentrale = new File(pathname);
        var config = PApplet.loadJSONObject(puntoCentrale);

        // Controlli di sicurezza sul corretto formato
        if (!config.hasKey("path")) {
            System.err.println("config.load non ha l'attributo \"path\"");
            return null;
        }
        if (!config.hasKey("name")) {
            System.err.println("config.load non ha l'attributo \"name\"");
            return null;
        }
        if (!config.hasKey("ox")) {
            System.err.println("config.load non ha l'attributo \"ox\"");
            return null;
        }
        if (!config.hasKey("oy")) {
            System.err.println("config.load non ha l'attributo \"oy\"");
            return null;
        }
        if (!config.hasKey("dx")) {
            System.err.println("config.load non ha l'attributo \"dx\"");
            return null;
        }
        if (!config.hasKey("dy")) {
            System.err.println("config.load non ha l'attributo \"dy\"");
            return null;
        }
        if (!config.hasKey("txt")) {
            System.err.println("config.load non ha l'attributo \"txt\"");
            return null;
        }

        // Ottenimento dei valori dei campi
        String[] paths = config.getJSONArray("path").toStringArray();
        String[] names = config.getJSONArray("name").toStringArray();
        int[] originiX = config.getJSONArray("ox").toIntArray();
        int[] originiY = config.getJSONArray("oy").toIntArray();
        int[] destinazioniX = config.getJSONArray("dx").toIntArray();
        int[] destinazioniY = config.getJSONArray("dy").toIntArray();

        pathTxt = pathIniziale + "/" + config.getString("txt");

        if (paths == null) {
            System.err.println("Cartella non trovata o vuota: " + pathname);
            return List.of();
        }

        List<JSONArray> files = new ArrayList<>();

        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            String name = names[i];
            int origineX = originiX[i];
            int origineY = originiY[i];
            int destinazioneX = destinazioniX[i];
            int destinazioneY = destinazioniY[i];

            nomi.add(name);

            origini.add(new CoordinateCella(origineX, origineY));
            destinazioni.add(new CoordinateCella(destinazioneX, destinazioneY));

            File file = new File(pathIniziale + "/" + path);
            if (!file.exists()) {
                System.err.println("File non trovato: " + file.getPath());
                continue;
            }
            var src = PApplet.loadJSONObject(file);
            JSONArray toAdd = src.getJSONArray(name);
            files.add(toAdd);
        }

        return files;
    }

    private static void scriviEStampa(String msg) {
        System.out.println(msg);
        ScritturaFile.writeToFile(pathTxt + "_" + compitiUsati + ".txt", msg);
    }

    private static void scriviEStampaGenerico(String msg) {
        System.out.println(msg);
        ScritturaFile.writeToFile(pathTxt, msg);
    }
    
    private static void scriviEStampaConPath(String msg, String path) {
    	System.out.println(msg);
        ScritturaFile.writeToFile(path + ".txt", msg);
    }

    /**
     * Aggiunge il tempo con relative informazioni su combinazione compiti e
     * griglia usata usando una doppia HashMap e riempiendola in loco
     *
     * @param tempo
     * @param combinazione
     * @param griglia
     * @param tempi
     */
    private static void aggiungiTempo(long tempo, String combinazione, String griglia, HashMap<String, TreeMap<Long, String>> tempi) {
        if (tempi.containsKey(griglia)) {
            // cerco l'HashMap già esistente
        	TreeMap<Long, String> map = tempi.get(griglia);
            // Se la map ottenuta è vuota, allora aggiungo il valore
            map.put(tempo, combinazione);
        } // la chiave non esisteva, creo una nuova map ed usa la chiave
        else {
        	TreeMap<Long, String> map = new TreeMap<>();
            map.put(tempo, combinazione);
            tempi.put(griglia, map);
        }
    }

    private static void scriviInformazioniGenerali(HashMap<String, TreeMap<Long, String>> tempi) {
        // Viene anzitutto pulito il file
        ScritturaFile.pulisciFile(pathTxt);
        StringBuilder sb = new StringBuilder();
        // Le chiavi sono i nomi delle griglie
        for (String griglia : tempi.keySet()) {
            // Viene ottenuta la HashMap che associa i tempi alle combinazioni di implementazioni dei compiti
            TreeMap<Long, String> map = tempi.get(griglia);
            // Ora si forma una String unica per trovare 
			sb.append("=============== NUOVA GRIGLIA ===============\n");
			sb.append("Statistiche per la griglia: " + griglia + "\n");
			// Min
			long valoreMinimo = map.firstKey();
			sb.append("Valore minimo: " + valoreMinimo + ", combinazione: " + map.get(valoreMinimo) + "\n");
			// Max
			long valoreMassimo = map.lastKey();
			sb.append("Valore massimo: " + valoreMassimo + ", combinazione: " + map.get(valoreMassimo) + "\n");
			// Media
			long valoreMedio = 0;
			for (Long tempo : map.keySet()) {
				valoreMedio += tempo;
			}
			valoreMedio /= map.size();
			sb.append("Valore medio: " + valoreMedio + "\n");
			// Deviazione standard
			long deviazioneStandard = 0;
			for (Long tempo : map.keySet()) {
				deviazioneStandard += Math.pow(tempo - valoreMedio, 2);
			}
			deviazioneStandard = (long) Math.sqrt(deviazioneStandard / map.size());
			sb.append("Deviazione standard: " + deviazioneStandard + "\n");
        }
        scriviEStampaGenerico(sb.toString());
    }
    
    /**
     * Scrive le statistiche medie delle esecuzioni in un file di testo
     * @param statistiche
     * @param tentativi
     * @param path
     */
    private static void scriviMediaEsecuzioni(List<IStatisticheEsecuzione> statistiche, int tentativi, String path) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("=============== MEDIA ESECUZIONI ===============\n");
    	// Si presuppone che tutte le statistiche stiano usando la stessa implementazione
    	sb.append("Cache usata? " + statistiche.get(0).isCacheAttiva() + "\n");
    	sb.append("Sorted Frontiera? " + statistiche.get(0).isFrontieraStored() + "\n");
    	int mediaCache = 0;
    	int mediaCelle = 0;
    	int mediaIterazioni = 0;
    	long mediaTempo = 0;
    	for (IStatisticheEsecuzione s : statistiche) {
			mediaCache += s.getCacheHit();
			mediaCelle += s.getQuantitaCelleFrontiera();
			mediaIterazioni += s.getIterazioniCondizione();
			mediaTempo += s.getTempoEsecuzione();
		}
    	mediaCache /= tentativi;
    	sb.append("Media Cache hit: ");
		sb.append(mediaCache + "\n");

		mediaCelle /= tentativi;
		sb.append("Media Celle di Frontiera: ");
		sb.append(mediaCelle + "\n");
		
		mediaIterazioni /= tentativi;
		sb.append("Media Iterazioni Condizione: ");
		sb.append(mediaIterazioni + "\n");
		
		mediaTempo /= tentativi;
		sb.append("Media Tempo Esecuzione: ");
		sb.append("Media dei Tempi d'Esecuzione: " + Utils.formatTempo(mediaTempo) + "\n");
		
		ScritturaFile.writeToFile(path, sb.toString());
    }
}

/**
 * Helper per tenere in memoria le coordinate delle celle
 */
record CoordinateCella(int x, int y) implements IHave2DCoordinate {

}
