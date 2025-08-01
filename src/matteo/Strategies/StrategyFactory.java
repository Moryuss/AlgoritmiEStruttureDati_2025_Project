package matteo.Strategies;


import matteo.CamminoCache;
import matteo.CamminoConfiguration;
import matteo.ConfigurationMode;
import matteo.Strategies.Cache.CacheAttiva;
import matteo.Strategies.Cache.CacheNull;
import matteo.Strategies.Condizione.CondizioneNormale;
import matteo.Strategies.Condizione.CondizioneRafforzata;
import matteo.Strategies.Debug.DebugAbilitato;
import matteo.Strategies.Debug.DebugDisabilitato;
import matteo.Strategies.Frontiera.FrontieraNormale;
import matteo.Strategies.Frontiera.FrontieraOrdinata;
import matteo.Strategies.SvuotaFrontiera.SvuotaFrontieraAbilitato;
import matteo.Strategies.SvuotaFrontiera.SvuotaFrontieraDisabilitato;

/**
 * Factory semplificato che sfrutta direttamente i ConfigurationMode predefiniti.
 * Molto più pulito e diretto - usa le combinazioni che hai già definito!
 */
public class StrategyFactory {

	private final CamminoCache cache;

	public StrategyFactory() {
		this.cache = new CamminoCache();
	}

	public StrategyFactory(CamminoCache cache) {
		this.cache = cache;
	}

	/**
	 * Crea tutte le strategy da un ConfigurationMode
	 */
	public StrategyBundle createStrategies(ConfigurationMode mode) {
		return new StrategyBundle(
				mode.isDebugEnabled() ? new DebugAbilitato() : new DebugDisabilitato(),
				mode.isCacheEnabled() ? new CacheAttiva(cache) : new CacheNull(),
				mode.isCondizioneRafforzataEnabled() ? new CondizioneRafforzata() : new CondizioneNormale(),
				mode.isSortedFrontieraEnabled() ? new FrontieraOrdinata() : new FrontieraNormale(),
				mode.isSvuotaFrontieraEnabled() ? new SvuotaFrontieraAbilitato() : new SvuotaFrontieraDisabilitato());
	}

	/**
	 * Overload per CamminoConfiguration (che contiene il mode)
	 */
	public StrategyBundle createStrategies(CamminoConfiguration configuration) {
		return createStrategies(configuration.getMode());
	}



}