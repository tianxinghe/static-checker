package expression;

public class Expression {
    public boolean isVariable() {
        return this instanceof Variable;
    }

    public boolean isNum(){
        return this instanceof Num;
    }

    public boolean isArith(){
        return this instanceof ArithExpression;
    }

    public boolean isCall(){
        return this instanceof CallExpression;
    }

    @Override
    public String toString() {
        return "";
    }
}
