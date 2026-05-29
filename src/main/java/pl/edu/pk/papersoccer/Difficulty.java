package pl.edu.pk.papersoccer;

public enum Difficulty {
    EASY(2),     // patrzy 2 ruchy w przód
    MEDIUM(4),   // patrzy 4 ruchy w przód
    HARD(7);     // patrzy 7 ruchów w przód, najmocniejszy

    private final int searchDepth; // głębokość przeszukiwania w krawędziach (plies)

    Difficulty(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public int getSearchDepth() {
        return searchDepth;
    }
}
