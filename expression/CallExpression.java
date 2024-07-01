package expression;

import java.util.ArrayList;

public class CallExpression extends Expression{
    private String funcName;
    private ArrayList<Expression> paramList;

    public CallExpression(String name, ArrayList<Expression> params) {
        this.funcName = name;
        paramList = new ArrayList<>();
        for (Expression param : params) {
            paramList.add(param);
        }
    }

    public String getFuncName() {
        return funcName;
    }

    public ArrayList<Expression> getParams() {
        return paramList;
    }

    @Override
    public String toString() {
        String res = funcName;
        res += "(";
        for (Expression param : paramList) {
            res += param.toString();
            res += ",";
        }
        res += ")";
        return res;
    }
}
