package matteo.Strategies.Debug;

public sealed interface DebugStrategy {
    void println(String msg);

	public static record DebugDisabilitato() implements DebugStrategy {
		@Override
		public void println(String msg) {
			// no-op
		}
	}
	public static record DebugAbilitato() implements DebugStrategy {
		@Override
		public void println(String msg) {
			System.out.println(msg);
		}
	}

}
