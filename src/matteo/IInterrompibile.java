package matteo;

import java.util.concurrent.TimeUnit;

public interface IInterrompibile {
	
    void interrupt();
    
    void setTimeout(long amount, TimeUnit unit);
    
}