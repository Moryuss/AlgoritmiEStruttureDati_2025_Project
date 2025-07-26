package matteo.Strategies;

/**
 * Container che raggruppa tutte le strategy per facilitarne l'uso.
 * Evita di dover gestire singolarmente ogni strategy.
 */
public class StrategyBundle {
    
    private final DebugStrategy debugStrategy;
    private final CacheStrategy cacheStrategy;
    private final CondizioneStrategy condizioneStrategy;
    private final FrontieraStrategy frontieraStrategy;
    
    public StrategyBundle(DebugStrategy debugStrategy, 
                         CacheStrategy cacheStrategy,
                         CondizioneStrategy condizioneStrategy, 
                         FrontieraStrategy frontieraStrategy) {
        this.debugStrategy = debugStrategy;
        this.cacheStrategy = cacheStrategy;
        this.condizioneStrategy = condizioneStrategy;
        this.frontieraStrategy = frontieraStrategy;
    }
    
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
    
    @Override
    public String toString() {
        return "StrategyBundle{" +
                "debugStrategy=" + debugStrategy.getClass().getSimpleName() +
                ", cacheStrategy=" + cacheStrategy.getClass().getSimpleName() +
                ", condizioneStrategy=" + condizioneStrategy.getClass().getSimpleName() +
                ", frontieraStrategy=" + frontieraStrategy.getClass().getSimpleName() +
                '}';
    }
}