package matteo.Strategies;

import java.util.Comparator;
import java.util.List;

import nicolas.ICellaConDistanze;
import nicolas.IGrigliaConOrigine;
import utils.Utils;

public class FrontieraOrdinata implements FrontieraStrategy {

	@Override
	public List<ICellaConDistanze> getFrontiera(IGrigliaConOrigine g, ICellaConDistanze D) {
		return g.getFrontiera()
				.sorted(Comparator.comparingDouble(
						c -> Utils.distanzaLiberaTra(c, D)))
				.toList();
	}

}
