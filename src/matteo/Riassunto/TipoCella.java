package matteo.Riassunto;

public enum TipoCella {
    TIPO_A(4); // 4 byte = 1 intero == stato della cella

    private final int dimensioneCella;

    TipoCella(int dim) {
        this.dimensioneCella = dim;
    }

    public int getDimensioneCella() {
        return dimensioneCella;
    }
}

