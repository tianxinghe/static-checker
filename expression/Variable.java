package expression;
import java.util.ArrayList;

public class Variable extends Expression{ 
    private String varName;
    private ArrayList<Expression> sizeList;
    private boolean isConst = false;
    private boolean isRet = false;
    private int ID;
    private static int globalID = 0;

    public String getName() {
        return varName;
    }

    public ArrayList<Expression> getSizeList() {
        return sizeList;
    }

    public Variable(String varname, ArrayList<Expression> sizes, boolean isConst) {
        this.varName = varname;
        this.isConst = isConst;
        this.sizeList = new ArrayList<>();
        for (Expression size : sizes) {
            sizeList.add(size);
        }
    }

    public Variable() {
        this.isRet = true;
        this.ID = globalID;
        globalID ++;
    }

    public boolean isConst() {
        return isConst;
    }

    @Override
    public String toString() {
        if (isRet) {
            return "ret" + ID;
        }

        String res = varName;
        for (Expression exp: sizeList) {
            if (exp instanceof Num) {
                if (((Num)exp).getNum() == -1) {
                    res += "[]";
                    continue;
                }
            } 
            res += "["+ exp.toString() + "]";
        }
        return res;
    }
}
