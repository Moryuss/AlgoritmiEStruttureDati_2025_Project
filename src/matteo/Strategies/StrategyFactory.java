package matteo.Strategies;

import matteo.CamminoCache;
import matteo.CamminoConfiguration;
import matteo.Strategies.Cache.*;
import matteo.Strategies.Condizione.*;
import matteo.Strategies.Debug.*;
import matteo.Strategies.Frontiera.*;
import matteo.Strategies.SvuotaFrontiera.*;


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
	 * Crea tutte le strategy da un CamminoConfiguration
	 */
	public StrategyBundle createStrategies(CamminoConfiguration mode) {
		return new StrategyBundle(
				mode.isDebugEnabled() ? new DebugStrategy.DebugAbilitato() : new DebugStrategy.DebugDisabilitato(),
						mode.isCacheEnabled() ? new CacheStrategy.CacheAttiva(cache) : new CacheStrategy.CacheNull(),
								mode.isCondizioneRafforzataEnabled() ? new CondizioneStrategy.CondizioneRafforzata() : new CondizioneStrategy.CondizioneNormale(),
										mode.isSortedFrontieraEnabled() ? new FrontieraStrategy.FrontieraNormale() : new FrontieraStrategy.FrontieraOrdinata(),
												mode.isSvuotaFrontieraEnabled() ? new SvuotaFrontieraStrategy.SvuotaFrontieraAbilitato() : new SvuotaFrontieraStrategy.SvuotaFrontieraDisabilitato());
	}
}