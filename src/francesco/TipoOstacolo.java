package francesco;

import java.util.List;
import francesco.ostacolibuilder.*;

public enum TipoOstacolo implements CostruttoreOstacolo {
	SEMPLICE				(new OSemplice()), // cella singolo
	AGGLOMERATO				(new OAgglomerato()), // rettangolo di celle
	BARRA_VERTICALE			(new OBarraVerticale()),
	BARRA_ORIZZONTALE		(new OBarraOrizzontale()),
	BARRA_DIAGONALE			(new OBarraDiagonale()), // solo sottile (spessore = 1)
	ZONA_CHIUSA				(new OZonaChiusa()), // forme rettangolari cave all'interno
	DELIMITATORE_VERTICALE	(new ODelimitatoreVerticale()),
	DELIMITATORE_ORIZZONTALE(new ODelimitatoreOrizzontale()),
	PERSONALIZZATO          (new OPersonalizzato()); // il costruttore è solo un placeholder
	
	
	private final int valore = 1<<ordinal();
	private final CostruttoreOstacolo costruttoreOstacolo;
	
	
	private TipoOstacolo(CostruttoreOstacolo co) {
		costruttoreOstacolo = co;
	}
	
	
	public int value() {
		return valore;
	}
	
	@Override
	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		return costruttoreOstacolo.generaCelle(width, height, griglia, randomSeed);
	}
	
	
	public static int sommaTipi(int first, int second) {
		return first | second;
	}
	
}
