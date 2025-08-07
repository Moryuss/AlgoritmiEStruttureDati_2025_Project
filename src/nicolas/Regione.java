package nicolas;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import francesco.ICella2D;
import francesco.IHave2DCoordinate;

public class Regione {
	protected List<ICella2D> celle = new LinkedList<>();
	protected List<ICella2D> frontiera = new LinkedList<>();
	protected int xmin, ymin, xmax, ymax;
	
	
	public Regione(ICella2D cella) {
		celle.add(cella);
		xmin = xmax = cella.x();
		ymin = ymax = cella.y();
	}
	
	
	@Override
	public String toString() {
		var sb = new StringBuilder("[(%d,%d) (%d,%d)]".formatted(xmin, ymin, xmax, ymax));
		sb.append("celle: (%d)".formatted(celle.size()));
		for (var c : celle) {
			sb.append("(%d,%d)".formatted(c.x(), c.y()));
		}
		sb.append("frontiera: (%d)".formatted(frontiera.size()));
		for (var c : frontiera) {
			sb.append("(%d,%d)".formatted(c.x(), c.y()));
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return celle.hashCode() ^ Objects.hash(xmin, ymin, xmax, ymax);
	}
	
	
	public void addCella(ICella2D cella) {
		celle.add(cella);
		int x = cella.x();
		int y = cella.y();
		if (x<xmin) xmin=x;
		if (y<ymin) ymin=y;
		if (x>xmax) xmax=x;
		if (y>ymax) ymax=y;
	}
	
	
	public boolean inRange(int x, int y) {
		return x>=xmin && x<=xmax && y>=ymin && x<=ymax;
	}
	public boolean inRange(IHave2DCoordinate c) {
		return inRange(c.x(), c.y());
	}
	
	public List<ICella2D> celle() {return celle;}
	public List<ICella2D> frontiera() {return frontiera;}
	public int xmin() {return xmin;}
	public int ymin() {return ymin;}
	public int xmax() {return xmax;}
	public int ymax() {return ymax;}
	public int maxWidth() {return xmax-xmin+1;}
	public int maxHeight() {return ymax-ymin+1;}
	
	public Stream<ICella2D> frontiera(IHave2DCoordinate O) {
		return frontiera.stream()
		.map(c -> ICella2D.of(c.x(), c.y(), c.stato()));
	}
	
}
