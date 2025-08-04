package utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class BiHashmap<K,V> {
	
	private final Map<K,Map<K,V>> map = new HashMap<>();
	
	
	public V get(K k1, K k2) {
		var v1 = map.get(k1);
		if (v1==null) throw new IllegalArgumentException("no value found for k1=%s, k2=%s".formatted(k1, k2));
		var v2 = v1.get(k2);
		if (v2==null) throw new IllegalArgumentException("no value found for k2=%s, k1=%s".formatted(k2, k1));
		return v2;
	}
	
	public V getOrDefault(K k1, K k2, V orElse) {
		return map.getOrDefault(k1, Collections.emptyMap())
		.getOrDefault(k2, orElse);
	}
	
	public Optional<V> safeGet(K k1, K k2) {
		return Optional.ofNullable(getOrDefault(k1, k2, null));
	}
	
	
	public void put(K k1, K k2, V value) {
		map.computeIfAbsent(k1, k->new HashMap<>()).put(k2, value);
	}
	
	
	public V compute(K k1, K k2, UnaryOperator<V> func) {
		return map.get(k1).compute(k2, (k,v) -> func.apply(v));
	}
	
	
	public void forEach(BiConsumer<K,Map<K,V>> action) {
		map.forEach(action);
	}
	
}
