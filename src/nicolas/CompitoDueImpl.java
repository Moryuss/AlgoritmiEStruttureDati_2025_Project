package nicolas;

import francesco.IGriglia;
import francesco.IHave2DCoordinate;

public enum CompitoDueImpl implements ICompitoDue {
	// GrigliaMatrix senza regioni
	V0 {
		@Override
		public IGrigliaConOrigine calcola(IGriglia<?> griglia, int x, int y) {
			return GrigliaConOrigineFactory.creaV0(griglia, x, y);
		}
	},
	
	// GrigliaBiHashmap, con regioni
	V1 {
		@Override
		public IGrigliaConOrigine calcola(IGriglia<?> griglia, int x, int y) {
			return GrigliaConOrigineFactory.creaV1(griglia, x, y);
		}
		@Override
		public GrigliaFrontieraPair getGrigliaFrontieraPair(IGrigliaConOrigine griglia, IHave2DCoordinate O, IHave2DCoordinate D) {
			Regione regione = RegioneFactory.regioneContenente2(griglia, D.x(), D.y(), griglia::getCellaAt);
			var frontieraList = regione.frontiera(O);
			IGriglia<?> g = RegioneFactory.creaSottoGriglia2(griglia, regione);
			return new GrigliaFrontieraPair(g, frontieraList);
		}
	};
	
	
	public static final int LENGTH = CompitoDueImpl.values().length;
	
	
	public CompitoDueImpl next() {
		return values()[(ordinal()+1)%LENGTH];
	}
	
}
