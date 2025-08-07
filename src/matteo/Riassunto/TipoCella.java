package matteo.Riassunto;

public enum TipoCella {
    TIPO_A(29); // 29 Byte = 5 * 4 int(4) + 1 double(8) + 1 boolean(1)

    private final int dimensioneCella;

    TipoCella(int dim) {
        this.dimensioneCella = dim;
    }

    public int getDimensioneCella() {
        return dimensioneCella;
    }
}

