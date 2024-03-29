package game.helpers;

public class Tuple<X, Y>{
    public final int x;
    public final int y;
    public Tuple(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}