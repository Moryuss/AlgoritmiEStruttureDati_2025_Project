package matteo.Strategies;

import matteo.Strategies.Cache.CacheStrategy;
import matteo.Strategies.Condizione.CondizioneStrategy;
import matteo.Strategies.Debug.DebugStrategy;
import matteo.Strategies.Frontiera.FrontieraStrategy;
import matteo.Strategies.SvuotaFrontiera.SvuotaFrontieraStrategy;

/**
 * Container che raggruppa tutte le strategy per facilitarne l'uso.
 * Evita di dover gestire singolarmente ogni strategy.
 */
public class StrategyBundle {
    
    private final DebugStrategy debugStrategy;
    private final CacheStrategy cacheStrategy;
    private final CondizioneStrategy condizioneStrategy;
    private final FrontieraStrategy frontieraStrategy;
    private final SvuotaFrontieraStrategy svuotaFrontieraStrategy;
    
    public StrategyBundle(DebugStrategy debugStrategy, 
                         CacheStrategy cacheStrategy,
                         CondizioneStrategy condizioneStrategy, 
                         FrontieraStrategy frontieraStrategy,
                         SvuotaFrontieraStrategy svuotaFrontieraStrategy) {
        this.debugStrategy = debugStrategy;
        this.cacheStrategy = cacheStrategy;
        this.condizioneStrategy = condizioneStrategy;
        this.frontieraStrategy = frontieraStrategy;
    	this.svuotaFrontieraStrategy = svuotaFrontieraStrategy;}
    
    // Getters
    public DebugStrategy getDebugStrategy() {
        return debugStrategy;
    }
    
    public CacheStrategy getCacheStrategy() {
        return cacheStrategy;
    }
    
    public CondizioneStrategy getCondizioneStrategy() {
        return condizioneStrategy;
    }
    
    public FrontieraStrategy getFrontieraStrategy() {
        return frontieraStrategy;
    }
    
    public SvuotaFrontieraStrategy getSvuotaFrontieraStrategy() {
		return svuotaFrontieraStrategy;
	}
    
    @Override
    public String toString() {
        return "StrategyBundle{" +
                "debugStrategy=" + debugStrategy.getClass().getSimpleName() +
                ", cacheStrategy=" + cacheStrategy.getClass().getSimpleName() +
                ", condizioneStrategy=" + condizioneStrategy.getClass().getSimpleName() +
                ", frontieraStrategy=" + frontieraStrategy.getClass().getSimpleName() +
                ", svuotaFrontieraStrategy=" + svuotaFrontieraStrategy.getClass().getSimpleName() +
                '}';
    }
}