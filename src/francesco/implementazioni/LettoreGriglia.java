package francesco.implementazioni;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import francesco.*;
import francesco.generatoriDisposizioni.GeneratoreDoppiaLineaSpezzata;
import francesco.generatoriDisposizioni.GeneratoreLineaSpezzata;
import francesco.generatoriDisposizioni.GeneratoreScacchiera;
import francesco.generatoriDisposizioni.GeneratoreSpirale;
import francesco.generatoriDisposizioni.GeneratoreVariazioneDimensioni;
import processing.core.PApplet;
import processing.data.JSONObject;

public class LettoreGriglia extends PApplet implements ICompitoUno {
	
	
	@Override
	public IGriglia<?> crea(Path file) {
		File jsonFile = file.toFile();
		if (!jsonFile.exists()) {
			System.err.println("Errore nella lettura del file, ritorno una griglia vuota 50x50...");
			Cella[][] mat = GrigliaMatrix.inizializzaMatrice(50, 50);
			GrigliaMatrix griglia = new GrigliaMatrix(mat, 0);
			return griglia;
		}
		
		// Carica il file JSON
		JSONObject json = loadJSONObject(jsonFile);
		int width = json.getInt("width");
		int height = json.getInt("height");
		
		// Ora crea la matrice vuota
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		
		// Inizializzo una IGriglia senza ostacoli per semplicita'
		IGriglia<?> griglia = new GrigliaMatrix(mat, 0);
		
		// Infine aggiunge gli ostacoli
		int randomSeed = json.getInt("randomSeed");
		griglia = generaOstacoli(width, height, griglia, randomSeed, json.getJSONObject("maxOstacoli"),
				json.getJSONObject("disposizione"));
		
		return griglia;
	}
	
	/**
	 * Metodo per la generazione di griglie come specificato nel file JSON.
	 * @Precondizioni griglia rappresenta una griglia vuota 
	 * @Postcondizioni griglia viene riempita con gli ostacoli specificati nel file JSON.
	 * @param width
	 * @param height
	 * @param griglia
	 * @param randomSeed
	 * @param json
	 * @param disposizioni
	 * @return
	 */
	private static IGriglia<?> generaOstacoli(int width, int height, IGriglia<?> griglia, int randomSeed, JSONObject json, JSONObject disposizioni) {
		IGriglia<?> result = griglia;
		int ostCounter = 0;
		
		if(disposizioni == null) {
			result = generazioneStandard(width, height, randomSeed, json, result, ostCounter);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.STANDARD)) {
			result = generazioneStandard(width, height, randomSeed, json, result, ostCounter);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.VARAZIONE_DIMENSIONI)) {
			result = new GeneratoreVariazioneDimensioni().generaGrigliaSpecifica(width, height, result);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.SPIRALE)) {
			result = new GeneratoreSpirale().generaGrigliaSpecifica(width, height, result);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.LINEA_SPEZZATA)) {
			result = new GeneratoreLineaSpezzata().generaGrigliaSpecifica(width, height, result);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.DOPPIA_LINEA_SPEZZATA)) {
			result = new GeneratoreDoppiaLineaSpezzata().generaGrigliaSpecifica(width, height, result);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.SCACCHIERA)) {
			result = new GeneratoreScacchiera().generaGrigliaSpecifica(width, height, result);
		}
		else {
			result = generazioneStandard(width, height, randomSeed, json, result, ostCounter);
		}
		
		return result;
	}


	/** Metodo ausiliario per controllare quale disposizione viene richiesta
	 * @param disposizioni
	 * @return
	 */
	private static boolean isDisposizioneInJSONAndTrue(JSONObject disposizioni, DisposizioneOstacoli disp) {
		return disposizioni.hasKey(disp.toString()) && disposizioni.getBoolean(disp.toString());
	}


	/**
	 * @param width
	 * @param height
	 * @param randomSeed
	 * @param json
	 * @param result
	 * @param ostCounter
	 * @return
	 */
	public static IGriglia<?> generazioneStandard(int width, int height, int randomSeed, JSONObject json,
			IGriglia<?> result, int ostCounter) {
		for (TipoOstacolo ost : TipoOstacolo.values()) {
			
			if(ost == TipoOstacolo.PERSONALIZZATO) {
				continue; // Il tipo PERSONALIZZATO non è un vero ostacolo
			}
			
			if (json.hasKey(ost.toString())) {
				int num = json.getInt(ost.toString());
				if (num < 0) {
					System.err.println("Numero di ostacoli " + ost.toString() + " non valido: " + num);
					continue;
				}
				for (int i=0; i<num; i++) {
					List<ICella2D> celle = ost.generaCelle(width, height, result, randomSeed*(i+ostCounter));
					Ostacolo ostacolo = new Ostacolo(celle);
					result = result.addObstacle(ostacolo, ost.value());
				}
				randomSeed += num;
			}
			else {
				System.err.println("Ostacolo non trovato: " + ost.toString());
			}
			ostCounter++;
		}
		return result;
	}
	
//	public static IGriglia<?> generazioneVariazioniDimensioni(int width, int height, IGriglia<?> result){
//		List<ICella2D> celle = new ArrayList<>();
//		int posizioneX = (int) Math.ceil(width / 2);
//		for(int i = 0; i < height; i++) {
//			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), posizioneX, i));
//		}
//		result = result.addObstacle(new Ostacolo(celle), TipoOstacolo.PERSONALIZZATO.value());
//		return result;
//	}
	
		
//	public static IGriglia<?> generazioneSpirale(int width, int height, IGriglia<?> result){
//		// Cella di partenza per la scrittura
//		ICella2D cellaPartenza = new Cella2D(StatoCella.OSTACOLO.value(), 0, 1);
//		// Ordine delle direzioni
//		List<Direzione> direzioni = List.of(Direzione.DESTRA, Direzione.GIU, Direzione.SINISTRA, Direzione.SU);
//		// Contatore per le direzioni
//		int direzione = 0;
//		while(true) {
//			ICella2D ultimaCella = assistenteSpirale(result, cellaPartenza, direzioni.get(direzione));
//			// Calcola la distanza tra l'ultima cella e quella di partenza
//			int distanza = (int) Math.sqrt(Math.pow(ultimaCella.x() - cellaPartenza.x(), 2) + 
//					Math.pow(ultimaCella.y() - cellaPartenza.y(), 2));
//			// Se la distanza è minore o uguale a 1, si interrompe. Questo perché una distanza pari a 2 significa che
//			// tra la linea per la spirale al punto corrente è formata da una sola cella. Ergo, si è arrivati al centro della spirale.
//			if(distanza <= 1) {
//				break;
//			}
//			// Se non si è alla fine, si prosegue
//			cellaPartenza = ultimaCella;
//			direzione = direzione < direzioni.size() ? direzione + 1 : 0;
//			if(ultimaCella == null) {
//				// Errore, quindi break per sicurezza
//				break;
//			}
//			break;
//		}
//		
//		return result;	
//	}
	
//	private static ICella2D assistenteSpirale(IGriglia<?> griglia, ICella2D cellaPartenza, Direzione direzione){
//		// Si stabilisce se si sta andando in verticale o orizzontale
//		boolean isVerticale = direzione == Direzione.SU || direzione == Direzione.GIU;
//		// Si determina da dove partire
//		int partenza = isVerticale ? cellaPartenza.y() : cellaPartenza.x();
//		// E quale coordinata rimane invariata
//		int invariata = isVerticale ? cellaPartenza.x() : cellaPartenza.y(); 
//		// Si stabilisce il confine
//		int confine = isVerticale ? griglia.height() : griglia.width();
//		// Si stabilisce se si va avanti o indietro
//		boolean isAvanti = direzione == Direzione.GIU || direzione == Direzione.DESTRA;
//		int offset = isAvanti ? 1 : -1;
//		
//		
//		List<ICella2D> celle = new ArrayList<>();
//		ICella2D nuovaCella = null;
//		
//		for(int i = partenza; ( isAvanti ? i < confine-1 : i >=0); i+=offset) {
//			// Condizione di terminazione
//			// Bisogna prima verificare di essere arrivati ad una possibile fine senza sforare i margini
//			int coordinataVariabileProblematica = i + 2*offset;
//			if(isAvanti ? coordinataVariabileProblematica < confine : coordinataVariabileProblematica >=0) {
//				// Se la cella a 2 di distanza (spazio vuoto in mezzo) è un ostacolo, bisogna fermarsi
//				ICella cellaProblematica = isVerticale ? griglia.getCellaAt(invariata, coordinataVariabileProblematica)
//										: griglia.getCellaAt(coordinataVariabileProblematica, invariata);
//				if(cellaProblematica.stato() == StatoCella.OSTACOLO.value()) {
//					break;
//				}
//			}
//			
//			nuovaCella = isVerticale ? new Cella2D(StatoCella.OSTACOLO.value(), invariata, i)
//							: new Cella2D(StatoCella.OSTACOLO.value(), i, invariata);
//			
//			celle.add(nuovaCella);
//		}
//		
//		griglia = griglia.addObstacle(new Ostacolo(celle), TipoOstacolo.PERSONALIZZATO.value());
//		
//		// Ritorna l'ultima cella aggiunta
//		return nuovaCella;
//	}
		
//	public static IGriglia<?> generazioneLineaSpezzata(int width, int height, IGriglia<?> result){
//		int partenza = 1;
//		int offsetFinale = 1;
//		// Se la larghezza è divisible per 4, allora le due linee di spazio vuote vanno lasciate all'inizio
//		if(width % 4 == 0) {
//			partenza = 2;
//		}
//		// Se non è così, allora l'offset finale va a 2
//		else {
//			offsetFinale = 2;
//		}
//		
//		boolean daSopra = true;
//		
//		for(int i = partenza; i < width - offsetFinale; i+=2) {
//			List<ICella2D> celle = new ArrayList<>();
//			// Si crea un'intera linea
//			for(int j = 0; j < height; j++) {
//				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, j));
//			}
//			// Se si va da sopra, si rimuove la cella alla fine
//			if(daSopra) {
//				celle.removeLast();
//			}
//			// Altrimenti si rimuove la cella sopra
//			else {
//				celle.remove(0);
//			}
//			daSopra = !daSopra;
//			result = result.addObstacle(new Ostacolo(celle), TipoOstacolo.PERSONALIZZATO.value());
//		}
//		
//		return result;
//	}
	
//	public static IGriglia<?> generazioneDoppiaLineaSpezzata(int width, int height, IGriglia<?> result){
//		// Anzitutto, viene piazzata la linea che divide a meta' la griglia
//		int divisione = (int) Math.ceil(width/2);
//		
//		List<ICella2D> celle = new ArrayList<>();
//		
//		for(int i = 0; i < height; i++) {
//			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), divisione, i));
//		}
//		
//		// Poi si scorre e si mettono le celle. Prima si scorre a destra e poi a sinistra
//		// Dovendo lasciare uno spazio vuoto, si scorre di 2 in 2
//		for(int i = 0; i < height - 1; i+=2) {
//			// In questo for, si ha la coordinata y con i. Quindi si cerca le x
//			// Prima a destra
//			for(int j = 0; j < divisione - 1; j++) {
//				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), j, i));
//			}
//			// Poi a sinistra
//			for(int j = divisione + 1; j < width; j++) {
//				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), j, i));
//			}
//		}
//		
//		// Per l'ultima riga, si mette una linea piena tranne che per quella centrale
//		for(int j = 0; j < width; j++) {
//			if(j != divisione) {
//				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), j, height - 1));
//			}
//		}
//		
//		return result;
//	}
	
//	public static IGriglia<?> generazioneScacchiera(int width, int height, IGriglia<?> result){
//		
//		List<ICella2D> celle = new ArrayList<>();
//		List<tipoLinea> ordine = List.of(tipoLinea.INIZIO_VUOTO, tipoLinea.INIZIO_PIENO, tipoLinea.PIENA_SINISTRA,
//				tipoLinea.INIZIO_PIENO, tipoLinea.INIZIO_VUOTO, tipoLinea.PIENA_DESTRA);	
//		int indiceProgressoLinea = 0;
//		// Si inizia scorrendo tutte le righe
//		for(int i = 0; i < height; i++) {
//			// Si genera la linea in base al tipo corrente
//			celle.addAll(generazioneLineaScacchiera(width, i, ordine.get(indiceProgressoLinea)));
//			// Si prosegue con il tipo successivo
//			indiceProgressoLinea = indiceProgressoLinea < ordine.size() - 1 ? indiceProgressoLinea + 1 : 0;
//		}
//		
//		return result;
//	}

//	private static List<ICella2D> generazioneLineaScacchiera(int width, int y, tipoLinea tipo) {
//		List<ICella2D> celle = new ArrayList<>();
//		
//		switch (tipo) {
//			case INIZIO_PIENO:
//				for(int i = 0; i < width; i+=2) {
//					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
//				}
//				break;
//			case INIZIO_VUOTO:
//				for(int i = 1; i < width; i+=2) {
//					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
//				}
//				break;
//			case PIENA_SINISTRA:
//				for(int i = 0; i < width - 1; i++) {
//					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
//				}
//				break;
//			case PIENA_DESTRA:
//				for(int i = 1; i < width; i++) {
//					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
//				}
//				break;
//			default:
//				System.err.println("Tipo di linea non riconosciuto: " + tipo);
//		}
//		
//		return celle;
//	}

}
