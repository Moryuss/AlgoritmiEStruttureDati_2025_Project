package nicolas;

import francesco.IGriglia;
import francesco.IHave2DCoordinate;

public interface ICompitoDue {
	
	IGrigliaConOrigine calcola(IGriglia<?> griglia, int x, int y);
	
	String name();
	
	default GrigliaFrontieraPair getGrigliaFrontieraPair(IGrigliaConOrigine griglia, IHave2DCoordinate O, IHave2DCoordinate D) {
		var newGriglia = griglia.addObstacle(griglia.convertiChiusuraInOstacolo(), 0);
		return new GrigliaFrontieraPair(newGriglia, griglia.getFrontiera());
	}
	
	
	
	default IGrigliaConOrigine calcola(IGriglia<?> griglia, IHave2DCoordinate c) {
		return calcola(griglia, c.x(), c.y());
	}
	
}