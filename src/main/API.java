package main;
/*
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;


interface IObstacle {
	List<ICella> getList();
}
interface ICella {
	int x();
	int y();
	int stato();
}

// Cella in contesto e in frontiera = 0b001 | 0b100 = 0b101
enum StatoCella {
	CONTESTO	(0b000001),
	COMPLEMENTO	(0b000010),
	CHIUSURA	(0b000011),
	FRONTIERA	(0b000100),
	OSTACOLO	(0b001000),
	ORIGINE		(0b010000),
	DESTINAZIONE(0b100000);
	
	private StatoCella(int mask) {
		
	}
}

enum TipoOstacolo {
	SEMPLICE, // cella
	AGGLOMERATI,
	BARRA_VERTICALE,
	BARRA_ORIZZONTALE,
	BARRA_DIAGONALE, // solo sottile
	ZONA_CHIUSA
}


interface IGriglia<C extends ICella> {
	// costruttore: crea
	
	// true se in (x,y) c'è un'ostacolo 
	boolean isNavigabile(int x, int y);
	
	C getCellaAt(int x, int y);
	
	// larghezza
	int width();
	// altezza
	int height();
	
	// lista di ostacoli
	List<ICella> obstacles();
	
	// itera ogni coordinata non navigabile e invoca action
	void forEachNonNavigabile(BiConsumer<Integer,Integer> action);
	
	IGriglia<C> addObstacle(IObstacle obstacle);
	
}

interface CompitoUno {
	
	IGriglia<?> crea(Path file);
	
}


// -----------------------------------------------------------------------------------------------------------




interface ICella2 extends ICella {
	int distanzaDaOrigine();
}


interface IGrigliaConOrigine extends IGriglia<ICella2> {
	
	ICella2 getOrigine();
	boolean isInContesto(int x, int y);
	boolean isInComplemento(int x, int y);
	
	default boolean isInChiusura(int x, int y) {
		return isInContesto(x,y) || isInComplemento(x,y);
	}
	
	
	boolean isInFrontiera(int x, int y);
	
	// se (xd,yd) è nella chiusura restitiusco la distanza
	// altrimenti Double.infinity;
	double distanzaLiberaDa(int xd, int yd);
	
	Stream<ICella2> getFrontiera();
	
	IObstacle convertiChiusuraInOstacolo();
}



interface CompitoDue {
	
	IGrigliaConOrigine calcola(IGriglia<?> griglia, ICella O);
	
}

class CompitoDueImpl implements CompitoDue {

	@Override
	public IGrigliaConOrigine calcola(IGriglia<?> griglia, ICella O) {
		return null;
	}
	
}





//-----------------------------------------------------------------------------------------------------------

interface ILandMark extends ICella {
	int index();
}

interface ICammino {
	double lunghezza();
	List<ILandMark> landMarks();
}


interface CompitoTre {
	
	default ICammino camminoMin(IGriglia<?> griglia,
	ICella O, ICella D) {
		
		IGrigliaConOrigine g = new CompitoDueImpl()
		.calcola(griglia, O);
		
		if (false) {return null;} // contesto
		if (false) {return null;} // complemento
		if (false) {return null;} // frontiera
		
		// contesto di O diventa ostacolo
		IGriglia<?> g2 = griglia.addObstacle(g.convertiChiusuraInOstacolo()); 
		
		for (var F : g.getFrontiera().toList()) {
			ICammino c2 = camminoMin(g2, F, D);
		}
		
		return null;
	}
}
*/