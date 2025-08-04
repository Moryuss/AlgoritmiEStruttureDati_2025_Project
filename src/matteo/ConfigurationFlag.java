package matteo;


//Enum che definisce i singoli flag come potenze di 2
public enum ConfigurationFlag {
	DEBUG,
	MONITOR_ENABLED,
	STOP_MESSAGE,
	STATE_CHECK,
	SORTED_FRONTIERA,
	CONDIZIONE_RAFFORZATA,
	CACHE_ENABLED,
	SVUOTA_FRONTIERA;
	
	private final int value = 1<<ordinal();
	public static final int LENGTH = values().length;
	
	public int getValue() {
		return value;
	}
	
	public static ConfigurationFlag fromIndex(int index) {
		if (index<0 || index>=LENGTH) throw new IllegalArgumentException(index+"");
		return values()[index];
	}
	
}