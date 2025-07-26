package matteo.Strategies;

import java.util.List;

import francesco.ICella2D;
import nicolas.ICellaConDistanze;
import nicolas.IGrigliaConOrigine;


public class FrontieraNormale implements FrontieraStrategy {

	@Override
	public List<ICellaConDistanze> getFrontiera(IGrigliaConOrigine g, ICella2D D) {
		return g.getFrontiera().toList();
	}



}
