package francesco.generatoriDisposizioni;

import francesco.IGriglia;

public interface GeneratoreDisposizione {

	/**
	 * Genera una griglia specifica in base alla disposizione richiesta.
	 * La generazione avviene in-loco sulla base della griglia passata come parametro.
	 * Si raccomanda l'uso di una griglia vuota come parametro per evitare comportamenti indesiderati.
	 * @param width  la larghezza della griglia
	 * @param height l'altezza della griglia
	 * @param result la griglia da riempire con i dati
	 * @return la griglia riempita secondo la disposizione specifica
	 */
	IGriglia<?> generaGrigliaSpecifica(int width, int height, IGriglia<?> result);
}
