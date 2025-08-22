package matteo;

// Enum delle modalità predefinite usando i bitflags
public enum ConfigurationMode {
	DEFAULT(ConfigurationFlag.MONITOR_ENABLED),

	DEBUG(ConfigurationFlag.DEBUG, 
			ConfigurationFlag.MONITOR_ENABLED, 
			ConfigurationFlag.STOP_MESSAGE, 
			ConfigurationFlag.STATE_CHECK),

	PERFORMANCE(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.SORTED_FRONTIERA,
			ConfigurationFlag.CONDIZIONE_RAFFORZATA,
			ConfigurationFlag.CACHE_ENABLED),

	PERFORMANCE_CACHE(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.CACHE_ENABLED),

	PERFORMANCE_NO_CACHE(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.SORTED_FRONTIERA,
			ConfigurationFlag.CONDIZIONE_RAFFORZATA),


	PERFORMANCE_SORTED_FRONTIERA(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.SORTED_FRONTIERA),

	PERFORMANCE_NO_SORTED_FRONTIERA(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.CONDIZIONE_RAFFORZATA,
			ConfigurationFlag.CACHE_ENABLED),

	PERFORMANCE_CONDIZIONE_RAFFORZATA(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.CONDIZIONE_RAFFORZATA),

	PERFORMANCE_NO_CONDIZIONE_RAFFORZATA(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.SORTED_FRONTIERA,
			ConfigurationFlag.CACHE_ENABLED),
	
	PERFORMANCE_SVUOTA_FRONTIERA(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.SVUOTA_FRONTIERA),
	
	
	PERFORMANCE_FULL(ConfigurationFlag.MONITOR_ENABLED,
			ConfigurationFlag.SORTED_FRONTIERA,
			ConfigurationFlag.CONDIZIONE_RAFFORZATA,
			ConfigurationFlag.CACHE_ENABLED,
			ConfigurationFlag.SVUOTA_FRONTIERA);


	private final int flags;


	// Constructor che accetta flag variabili
	ConfigurationMode(ConfigurationFlag... enabledFlags) {
		int tempFlags = 0;
		for (ConfigurationFlag flag : enabledFlags) {
			tempFlags |= flag.getValue();
		}
		this.flags = tempFlags;
	}


	// Verifica se un flag specifico è attivo
	public boolean hasFlag(ConfigurationFlag flag) {
		return (flags & flag.getValue()) != 0;
	}

	// Verifica se tutti i flag specificati sono attivi
	public boolean hasAllFlags(ConfigurationFlag... flagsToCheck) {
		for (ConfigurationFlag flag : flagsToCheck) {
			if (!hasFlag(flag)) {
				return false;
			}
		}
		return true;
	}

	// Verifica se almeno uno dei flag specificati è attivo
	public boolean hasAnyFlag(ConfigurationFlag... flagsToCheck) {
		for (ConfigurationFlag flag : flagsToCheck) {
			if (hasFlag(flag)) {
				return true;
			}
		}
		return false;
	}

	

	// Getters per compatibilità con il codice esistente
	public boolean isDebugEnabled() { 
		return hasFlag(ConfigurationFlag.DEBUG); 
	}

	public boolean isMonitorEnabled() { 
		return hasFlag(ConfigurationFlag.MONITOR_ENABLED); 
	}

	@Deprecated
	public boolean isStopMessageEnabled() { 
		return hasFlag(ConfigurationFlag.STOP_MESSAGE); 
	}

	@Deprecated
	public boolean isStateCheckEnabled() { 
		return hasFlag(ConfigurationFlag.STATE_CHECK); 
	}

	public boolean isSortedFrontieraEnabled() { 
		return hasFlag(ConfigurationFlag.SORTED_FRONTIERA); 
	}

	public boolean isCondizioneRafforzataEnabled() { 
		return hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA); 
	}

	public boolean isCacheEnabled() { 
		return hasFlag(ConfigurationFlag.CACHE_ENABLED); 
	}
	public boolean isSvuotaFrontieraEnabled() { 
		return hasFlag(ConfigurationFlag.SVUOTA_FRONTIERA); 
	}

	// Restituisce una rappresentazione binaria dei flag (per debug)
	public String toBinaryString() {
		return String.format("%7s", Integer.toBinaryString(flags)).replace(' ', '0');
	}

	// Restituisce la lista dei flag attivi
	public java.util.List<ConfigurationFlag> getActiveFlags() {
		java.util.List<ConfigurationFlag> activeFlags = new java.util.ArrayList<>();
		for (ConfigurationFlag flag : ConfigurationFlag.values()) {
			if (hasFlag(flag)) {
				activeFlags.add(flag);
			}
		}
		return activeFlags;
	}

	public String getModeName() {
		return name() + " (flags: " + toBinaryString() + ", active: " + getActiveFlags() + ")";
	}


	public CamminoConfiguration toCamminoConfiguration() {
		return new CamminoConfiguration(this);
	}
	
	public int getFlagsValue() {
	    return flags; 
	}

}