package expression;

import java.util.ArrayList;

public class ArithExpression extends Expression{
    private Expression exp1;
    private String op;
    private Expression exp2;

    public Expression getExp1() {
        return exp1;
    }

    public Expression getExp2() {
        return exp2;
    }

    public String getOp() {
        return op;
    }

    public ArithExpression(Expression exp1, Expression exp2, String op) {
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.op = op;
    }

    public ArithExpression(Expression exp1, String op) {
        this.exp1 = exp1;
        this.op = op;
    }

    public void addCaller(ArrayList<Expression> callList) {
        if (exp1.isCall()) {
            callList.add(exp1);
            return;
        }

        if (exp1.isArith()) {
            ((ArithExpression) exp1).addCaller(callList);
            return;
        }

        if (exp2 != null) {
            if (exp2.isCall()) {
                callList.add(exp2);
                return;
            }

            if (exp2.isArith()) {
                ((ArithExpression) exp2).addCaller(callList);
                return;
            }
        }

        return;
    }

    @Override
    public String toString() {
        if (exp2 != null) {
            return exp1.toString() + " " + op + " " + exp2.toString() ;
        } else {
            return op + " " + exp1.toString() ;
        }
        
    }
}
 