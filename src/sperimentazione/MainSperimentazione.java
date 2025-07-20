package sperimentazione;

import francesco.IGriglia;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import matteo.CompitoTreImplementation;
import matteo.ICammino;
import matteo.ICompitoTre;
import matteo.ILandmark;
import nicolas.GrigliaConOrigineFactory;
import nicolas.ICella2;
import nicolas.ICompitoDue;
import nicolas.IGrigliaConOrigine;
import nicolas.Utils;
import processing.core.PApplet;
import processing.data.JSONArray;

public class MainSperimentazione {

	// Per il controllo su cammini uguali
	private static final double MAX_DIFF = 1e-10;
	private static final String LINEA_SPEZZATA_PATH = "src/sperimentazione/lineaspezzata";
	private static final String VARIAZIONE_OSTACOLI_PATH = "src/sperimentazione/varostacoli";
	
	private static final List<String> CARTELLE = List.of(LINEA_SPEZZATA_PATH
															, VARIAZIONE_OSTACOLI_PATH
																						);
	
	private static final List<ICompitoTre> TRES = List.of(new CompitoTreImplementation());
	private static final List<ICompitoDue> DUES = List.of();
	
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
    	
		
		for(ICompitoTre tre : TRES) {
			for(ICompitoDue due : DUES) {
				compitiUsati = tre.getClass().getSimpleName() + "&" + due.getClass().getSimpleName();
				for(String cartella : CARTELLE) {
					System.out.println("Cartella: " + cartella + "\n");
					
					List<String> nomi = new ArrayList<>();
					List<CoordinateCella> origini = new ArrayList<>();
					List<CoordinateCella> destinazioni = new ArrayList<>();
					// nomi, origini e destinazioni vengono riempiti in loco
					List<JSONArray> files = getGriglie(cartella, nomi, origini, destinazioni);
					if (files == null || files.isEmpty()) {
						System.err.println("Cartella non trovata o vuota: " + cartella);
						continue;
					}
					ScritturaFile.pulisciFile(pathTxt);
					for(int i = 0; i < files.size(); i++) {
						try {
							scriviEStampa("Nuova Griglia: " + nomi.get(i));
							
							// Carica la griglia dal JSONArray
							IGriglia<?> griglia = Utils.loadSimple(files.get(i));
							
							CoordinateCella origine = origini.get(i);
							//TODO CAMBIARE CON "due" QUANDO CI SARA'
							IGrigliaConOrigine gO = GrigliaConOrigineFactory.creaV0(griglia, origine.x(), origine.y()); 
							ICella2 start = gO.getCellaAt(origine.x(), origine.y());
							
							CoordinateCella destinazione = destinazioni.get(i);
							ICella2 end = gO.getCellaAt(destinazione.x(), destinazione.y()); 
//							ICompitoTre solver = new CompitoTreImplementation();
							ICammino cammino1 = tre.camminoMin(griglia, start, end);
							
							String report = tre.getReport();
							
							// Report ottenuto dal cammino
							scriviEStampa(report);
							scriviEStampa("==============================");
							System.out.println("Inizio veririfica correttezza...");
							// Utilizzo  di un secondo solver ed un secondo cammino per verificare la correttezza
							// Questa verifica non deve inficiare sul tempo d'esecuzione del primo cammino
//							ICompitoTre solverCorrettezza = new CompitoTreImplementation();
							ICammino cammino2 = tre.camminoMin(griglia, end, start);
							boolean corretto = isLunghezzaUguale(cammino1, cammino2);
							scriviEStampa("e' corretto?: " + corretto);
							
							System.out.println("Inizio verifica opposizione...");
							boolean opposto = isOpposto(cammino1.landmarks(), cammino2.landmarks());
							scriviEStampa("e' opposto?: " + opposto);
							
							// Questi sono generati per poi scrivere sul file txt in caso servano osservazioni
							scriviEStampa("# COMMENTI: niente");
							scriviEStampa("==============================");
							scriviEStampa("\n\n");
						} catch (Exception e) {
			            	System.err.println("Errore nella sperimentazione sulla griglia numero " + i + ": " + e.getMessage());
			            	e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/**
	 * Metodo per verificare che un array contenga l'esatto opposto dell'altro (controlla classe Landmark)
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
	 * Metodo per verificare la correttezza del percorso (origine-destinazione = destinazione-origine)
	 * @param primoCammino
	 * @param secondoCammino
	 * @return
	 */
	private static boolean isLunghezzaUguale(ICammino primoCammino, ICammino secondoCammino) {
    	// Uguaglianze tra float non consigliate
    	return Math.abs(primoCammino.lunghezza() - secondoCammino.lunghezza()) < MAX_DIFF;
	}
	
	/**
	 * Dato un path iniziale, carica le griglie scritte. La convenzione è l'uso di un file
	 * paths.json contenente i seguenti campi:
	 * - path: un array di stringhe che rappresentano i percorsi relativi ai file JSON delle griglie (nella stessa cartella)
	 * - name: un array di stringhe che rappresentano i nomi delle griglie (ossia il campo da cui attingere, ma usato anche per differenziare le griglie)
	 * - ox: un array di interi che rappresentano le origini X delle griglie
	 * - oy: un array di interi che rappresentano le origini Y delle griglie
	 * - dx: un array di interi che rappresentano le destinazioni X delle griglie
	 * - dy: un array di interi che rappresentano le destinazioni Y delle griglie
	 * - txt: path al txt dove scrivere report e controlli
	 * @param pathIniziale path che porta alla cartella desiderata
	 * @param nomi array inizialmente vuoto che verrà riempito con i nomi delle griglie
	 * @param origini array inizialmente vuoto che verrà riempito con le coordinate delle celle di origine
	 * @param destinazioni array inizialmente vuoto che verrà riempito con le coordinate delle celle di destinazione
	 * @return una lista di JSONArray per poter usare {@link Utils} e caricare con loadSimple
	 * @precondizioni nomi != null && origini != null && destinazioni != null
	 * @postcondizioni nomi viene riempito con tutti i nomi delle griglie trovate, gli array di origini e destinazioni
	 * vengono riempiti con le coordinate delle celle di origine e destinazione rispettivamente per ogni griglia trovata.
	 */
	private static List<JSONArray> getGriglie(String pathIniziale, List<String> nomi, List<CoordinateCella> origini, List<CoordinateCella> destinazioni){
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
		
		for(int i = 0; i < paths.length; i++) {
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
}


/**
 * Helper per tenere in memoria le coordinate delle celle
 */
record CoordinateCella(int x, int y) {}
