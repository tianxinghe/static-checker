package expression;

public class Num extends Expression{
    private int num;

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {
        return Integer.toString(num);
    }

    public Num(int num) {
        this.num = num;
    }
}
