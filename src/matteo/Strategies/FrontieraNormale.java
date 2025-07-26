package matteo.Strategies;

import java.util.List;

import nicolas.ICellaConDistanze;
import nicolas.IGrigliaConOrigine;


public class FrontieraNormale implements FrontieraStrategy {

	@Override
	public List<ICellaConDistanze> getFrontiera(IGrigliaConOrigine g, ICellaConDistanze D) {
		return g.getFrontiera().toList();
	}



}
