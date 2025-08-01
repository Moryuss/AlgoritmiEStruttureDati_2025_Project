package matteo;


//Enum che definisce i singoli flag come potenze di 2
public enum ConfigurationFlag {
	DEBUG(1),                    // 00000001
	MONITOR_ENABLED(2),          // 00000010
	STOP_MESSAGE(4),             // 00000100
	STATE_CHECK(8),              // 00001000
	SORTED_FRONTIERA(16),        // 00010000
	CONDIZIONE_RAFFORZATA(32),   // 00100000
	CACHE_ENABLED(64),           // 01000000
	SVUOTA_FRONTIERA(128);         		// 10000000

	private final int value;

	ConfigurationFlag(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}