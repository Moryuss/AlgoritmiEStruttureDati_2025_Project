package francesco.generatoriDisposizioni;

import java.util.ArrayList;
import java.util.List;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.TipoOstacolo;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.Ostacolo;
import nicolas.StatoCella;

public class GeneratoreSpirale implements GeneratoreDisposizione {

	
	enum Direzione {
		DESTRA,
		GIU,
		SINISTRA,
		SU
	}
	
	@Override
	public IGriglia<?> generaGrigliaSpecifica(int width, int height, IGriglia<?> result) {
		// Cella di partenza per la scrittura
		ICella2D cellaPartenza = new Cella2D(StatoCella.OSTACOLO.value(), 0, 1);
		// Ordine delle direzioni
		List<Direzione> direzioni = List.of(Direzione.DESTRA, Direzione.GIU, Direzione.SINISTRA, Direzione.SU);
		// Contatore per le direzioni
		int direzione = 0;
		
		while(true) {
			List<ICella2D> celle = new ArrayList<>();
			
			// Si creano le celle ostacolo da aggiungere alla griglia
			celle = assistenteSpirale(result, cellaPartenza, direzioni.get(direzione));
			
			// Si salva l'ultima cella dell'aggiunta
			ICella2D ultimaCella = celle.getLast();
			if(ultimaCella == null) {
				// Errore, quindi break per sicurezza
				break;
			}
			// Si aggiungono le celle alla griglia, così l'iterazione dopo è corretta
			result = result.addObstacle(new Ostacolo(celle), TipoOstacolo.PERSONALIZZATO.value());
			
			// Calcola la distanza tra l'ultima cella e quella di partenza
			int distanza = (int) Math.sqrt(Math.pow(ultimaCella.x() - cellaPartenza.x(), 2) + 
					Math.pow(ultimaCella.y() - cellaPartenza.y(), 2));
			
			// Se la distanza è minore o uguale a 1, si interrompe. Questo perché una distanza pari a 2 significa che
			// tra la linea per la spirale al punto corrente è formata da una sola cella. Ergo, si è arrivati al centro della spirale.
			if(distanza <= 1) {
				break;
			}
			// Se non si è alla fine, si prosegue
			cellaPartenza = ultimaCella;
			direzione = direzione < direzioni.size()-1 ? direzione + 1 : 0;
		}
				
		return result;	
	}
	
	private static List<ICella2D> assistenteSpirale(IGriglia<?> griglia, ICella2D cellaPartenza, Direzione direzione){
		// Si stabilisce se si sta andando in verticale o orizzontale
		boolean isVerticale = direzione == Direzione.SU || direzione == Direzione.GIU;
		// Si determina da dove partire
		int partenza = isVerticale ? cellaPartenza.y() : cellaPartenza.x();
		// E quale coordinata rimane invariata
		int invariata = isVerticale ? cellaPartenza.x() : cellaPartenza.y(); 
		// Si stabilisce il confine
		int confine = isVerticale ? griglia.height() : griglia.width();
		// Si stabilisce se si va avanti o indietro
		boolean isAvanti = direzione == Direzione.GIU || direzione == Direzione.DESTRA;
		int offset = isAvanti ? 1 : -1;
		
		
		List<ICella2D> celle = new ArrayList<>();
		ICella2D nuovaCella = null;
		
		for(int i = partenza; ( isAvanti ? i < confine-1 : i > 0); i+=offset) {
			// Condizione di terminazione
			// Bisogna prima verificare di essere arrivati ad una possibile fine senza sforare i margini
			int coordinataVariabileProblematica = i + offset;
			
			if(isAvanti ? coordinataVariabileProblematica < confine : coordinataVariabileProblematica >=0) {
				
				// Se la cella a 1 di distanza (spazio vuoto in mezzo) è un ostacolo, bisogna fermarsi
				ICella cellaProblematica = isVerticale ? griglia.getCellaAt(invariata, coordinataVariabileProblematica)
										: griglia.getCellaAt(coordinataVariabileProblematica, invariata);
				if(cellaProblematica.stato() == StatoCella.OSTACOLO.value()) {
					break;
				}
			}
			
			nuovaCella = isVerticale ? new Cella2D(StatoCella.OSTACOLO.value(), invariata, i)
							: new Cella2D(StatoCella.OSTACOLO.value(), i, invariata);
			
			celle.add(nuovaCella);
		}
				
		// Ritorna l'ultima cella aggiunta
		return celle;
	}

}
