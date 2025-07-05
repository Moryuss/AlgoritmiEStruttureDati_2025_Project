package francesco.ostacolibuilder;

import francesco.ICella;
import francesco.IGriglia;
import francesco.TipoOstacolo;
import francesco.implementazioni.Ostacolo;

public class CentroCostruttore {

	public static Ostacolo costruttoreCentrico(TipoOstacolo tipo, int width, int height, IGriglia<ICella> griglia, int randomSeed) {
		switch (tipo) {
			case TipoOstacolo.SEMPLICE: {
				return new OSemplice().costruisciOstacolo(width, height, griglia, randomSeed);
			}
			case TipoOstacolo.AGGLOMERATO: {
				return new OAgglomerato().costruisciOstacolo(width, height, griglia, randomSeed);
			}
			case TipoOstacolo.BARRA_VERTICALE: {
				return new OBarraVerticale().costruisciOstacolo(width, height, griglia, randomSeed);
			}
			case TipoOstacolo.BARRA_ORIZZONTALE: {
				return new OBarraOrizzontale().costruisciOstacolo(width, height, griglia, randomSeed);
			}
			case TipoOstacolo.BARRA_DIAGONALE: {
				return new OBarraDiagonale().costruisciOstacolo(width, height, griglia, randomSeed);
			}
			case TipoOstacolo.ZONA_CHIUSA: {
				return new OZonaChiusa().costruisciOstacolo(width, height, griglia, randomSeed);
			}
			case TipoOstacolo.DELIMITATORE_VERTICALE: {
				return new ODelimitatoreVerticale().costruisciOstacolo(width, height, griglia, randomSeed);
			}
			case TipoOstacolo.DELIMITATORE_ORIZZONTALE: {
				return new ODelimitatoreOrizzontale().costruisciOstacolo(width, height, griglia, randomSeed);
			}
			default:
				throw new IllegalArgumentException("Valore ostacolo non riconosciuto: " + tipo.toString());
			}
	}
}
