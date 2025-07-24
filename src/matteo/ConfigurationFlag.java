package matteo;


//Enum che definisce i singoli flag come potenze di 2
public enum ConfigurationFlag {
	DEBUG(1),                    // 0000001
	MONITOR_ENABLED(2),          // 0000010
	STOP_MESSAGE(4),             // 0000100
	STATE_CHECK(8),              // 0001000
	SORTED_FRONTIERA(16),        // 0010000
	CONDIZIONE_RAFFORZATA(32),   // 0100000
	CACHE_ENABLED(64);           // 1000000

	private final int value;

	ConfigurationFlag(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}