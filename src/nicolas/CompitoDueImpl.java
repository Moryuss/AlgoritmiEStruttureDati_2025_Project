package nicolas;

import francesco.IGriglia;

public enum CompitoDueImpl implements ICompitoDue {
	V0 {
		@Override
		public IGrigliaConOrigine calcola(IGriglia<?> griglia, int x, int y) {
			return GrigliaConOrigineFactory.creaV0(griglia, x, y);
		}
	};
	
}
