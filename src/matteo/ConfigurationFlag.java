package matteo;

import java.util.ArrayList;
import java.util.List;

//Enum che definisce i singoli flag come potenze di 2
public enum ConfigurationFlag {
	DEBUG					(0b00001101),
	MONITOR_ENABLED			(0b00000010),
	@Deprecated
	STOP_MESSAGE			(0b00000000),
	@Deprecated
	STATE_CHECK				(0b00000000),
	SORTED_FRONTIERA		(0b00010000),
	CONDIZIONE_RAFFORZATA	(0b00100000),
	CACHE_ENABLED			(0b01000000),
	SVUOTA_FRONTIERA		(0b10000000);
	
	private final int value;
	public static final int LENGTH = values().length;
	
	ConfigurationFlag(int i) {
		this.value = i;
	}

	public int getValue() {
		return value;
	}
	
	public static ConfigurationFlag fromIndex(int index) {
		if (index<0 || index>=LENGTH) throw new IllegalArgumentException(index+"");
		return values()[index];
	}
	
	public static List<ConfigurationFlag> allFlags(){
		return List.of(DEBUG, 
				SORTED_FRONTIERA, 
				CONDIZIONE_RAFFORZATA,
				CACHE_ENABLED, 
				SVUOTA_FRONTIERA);
	}
	
}