package nicolas;

import java.util.Optional;
import francesco.ICella;
import francesco.IGriglia;

public enum StatoCella {
	CONTESTO		(0b00000001, 0b00000001), // celle raggiungibili con cammini liberi di tipo 1
	REGINA			(0b00000011, 0b00000010), // origine + mosse diagonali + mosse cardinali
	ORIGINE			(0b00000111, 0b00000100), // (Ox, Oy)
	COMPLEMENTO		(0b00001000, 0b00001000), // celle raggiungibili con solo cammini liberi di tipo 2
	FRONTIERA		(0b00010000, 0b00010000), // celle nel contesto adiacenti a celle non nel contesto
	DESTINAZIONE	(0b00100000, 0b00100000),
	LANDMARK		(0b01010000, 0b01000000),
	OSTACOLO		(0b10000000, 0b10000000),
	CHIUSURA		(0b00000000, 0b00001001);
	
	
	private final short value, mask;
	
	private StatoCella(int n, int m) {
		value = (short)n;
		mask = (short)m;
	}
	
	
	public int value() {
		return value;
	}
	public int mask() {
		return mask;
	}
	
	
	public int addTo(int n) {
		return n|value;
	}
	
	public int toggleTo(int n) {
		return n^value;
	}
	
	public int removeTo(int n) {
		return n&~value;
	}
	
	
	public void addTo(IGriglia<?> griglia, int x, int y) {
		griglia.setStato(x, y, addTo(griglia.getCellaAt(x, y).stato()));
		
	}
	public void toggleTo(IGriglia<?> griglia, int x, int y) {
		griglia.setStato(x, y, toggleTo(griglia.getCellaAt(x, y).stato()));
	}
	public void removeTo(IGriglia<?> griglia, int x, int y) {
		griglia.setStato(x, y, removeTo(griglia.getCellaAt(x, y).stato()));
	}
	
	
	public boolean is(int n) {
		return matches(n);
	}
	
	public boolean isNot(int n) {
		return (n&mask) == 0;
	}
	
	public boolean is(ICella cella) {
		return is(cella.stato());
	}
	public boolean isNot(ICella cella) {
		return isNot(cella.stato());
	}
	
	
	public boolean check(int n) {
		return (n&mask) > 0;
	}
	
	public boolean matches(int n) {
		return (n&mask) == mask;
	}
	
	
	public static Optional<StatoCella> from(int n) {
		n = 31-Integer.numberOfLeadingZeros(n);
		var values = values();
		if (n<0 || n>=values.length) return Optional.empty();
		return Optional.of(values[n]);
	}
	
}
