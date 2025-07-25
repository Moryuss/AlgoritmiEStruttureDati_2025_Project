package matteo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import francesco.ICella2D;
import francesco.IGriglia;
import nicolas.StatoCella;

public class CamminoCache {
	private final Map<String, ICammino> cache = new HashMap<>(); //cache per i cammini già calcolati
	private boolean enabled = false;	//cache abilitata o meno
	private boolean debugMode = false;	//modalità debug per loggare le operazioni della cache

	public CamminoCache() {
		this(false, false);
	}

	public CamminoCache(boolean enabled, boolean debugMode) {
		this.enabled = enabled;
		this.debugMode = debugMode;
	}
	// Metodo principale per ottenere un cammino dalla cache
	public ICammino getCammino(IGriglia<?> griglia, ICella2D origine, ICella2D destinazione) {
		if (!enabled) {
			return null; // Cache disabilitata
		}

		String key = generateCacheKey(griglia, origine, destinazione);
		ICammino cached = cache.get(key);

		if (cached != null && debugMode) {
			System.out.println("Cache HIT per chiave: " + key);
		} else if (debugMode) {
			System.out.println("Cache MISS per chiave: " + key);
		}

		return cached;
	}

	// Metodo per salvare un cammino in cache
	public void putCammino(IGriglia<?> griglia, ICella2D origine, ICella2D destinazione, ICammino cammino) {
		if (!enabled) {
			return; // Cache disabilitata
		}

		String key = generateCacheKey(griglia, origine, destinazione);
		cache.put(key, cammino);

		if (debugMode) {
			System.out.println("Salvato in cache con chiave: " + key);
		}
	}

	// Metodi di utilità
	public void clear() {
		cache.clear();
	}

	public int size() {
		return cache.size();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	// Debug: stampa contenuto cache
	public void printCacheContents() {
		if (!debugMode) return;

		System.out.println("Cache corrente:");
		for (Map.Entry<String, ICammino> entry : cache.entrySet()) {
			System.out.println("Chiave: " + entry.getKey() + ", Cammino: ");
			entry.getValue().landmarks().forEach(lm -> {
				System.out.println("  - " + lm.x() + "," + lm.y() + " Stato: " + lm.stato());
			});
		}
	}

	// Genera una chiave unica per la cache basata su origine, destinazione e ostacoli
	private String generateCacheKey(IGriglia<?> griglia, ICella2D origine, ICella2D destinazione) {
		return origine.x() + "," + origine.y() + "->" + 
				destinazione.x() + "," + destinazione.y() + "|" + 
				calcolaHashOstacoli(griglia);
	}

	private int calcolaHashOstacoli(IGriglia<?> griglia) {
		List<Point> ostacoli = new ArrayList<>();
		for (int x = 0; x < griglia.width(); x++) {
			for (int y = 0; y < griglia.height(); y++) {
				if (StatoCella.OSTACOLO.is(griglia.getCellaAt(x, y).stato())) {
					ostacoli.add(new Point(x, y));
				}
			}
		}
		return Objects.hash(ostacoli);
	}
}

