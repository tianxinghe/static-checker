import java.util.ArrayList;

import expression.*;

public class Instruction {
    private Expression lExp = null;
    private Expression rExp = null;
    private int lineNo;
    private boolean declFlag = false;
    private BasicBlock parent;
    private boolean isRet = false;
    private boolean isCond = false;
    private boolean isJump = false;

    public Instruction(int lineNo, Expression lExp, Expression rExp, BasicBlock parent, boolean declFlag) {
        this.lineNo = lineNo;
        this.lExp = lExp;
        this.rExp = rExp;
        this.parent = parent;
        this.declFlag = declFlag;
    }

    public Instruction(int lineNo, Expression lExp, BasicBlock parent, boolean declFlag) {
        this.lineNo = lineNo;
        this.lExp = lExp;
        this.parent = parent;
        this.declFlag = declFlag;
    }

    public Instruction(int lineNo, Expression rExp, BasicBlock parent) {
        this.lineNo = lineNo;
        this.rExp = rExp;
        this.parent = parent;
    }

    public void addCaller(ArrayList<Expression> callList) {
        if (rExp == null) {
            return;
        }

        if (rExp.isVariable() || rExp.isNum()) {
            return;
        }

        if (rExp.isArith()) {
            ((ArithExpression) rExp).addCaller(callList);
            return;
        }

        if (rExp.isCall()) {
            callList.add(rExp);
            return;
        }

        return;
    }

    public String toString() {
        if (declFlag) {
            if (rExp != null) {
                if (((Variable) lExp).isConst()) {
                    return "lineNo" + lineNo + " : decl const " + lExp.toString() + " = " + rExp.toString() + ";";
                } else {
                    return "lineNo" + lineNo + " : decl " + lExp.toString() + " = " + rExp.toString() + ";";
                }
            } else {
                return "lineNo" + lineNo + " : decl " + lExp.toString() + ";";
            } 
        } else {
            if (lExp != null) {
                return "lineNo" + lineNo + " : " + lExp.toString() + " = " + rExp.toString() + ";";
            } else {
                return "lineNo" + lineNo + " : " + rExp.toString() + ";";
            }
        }        
    }

    public void setCond() {
        isCond = true;
    }

    public boolean isCond() {
        return isCond;
    }

    public void setRet() {
        isRet = true;
    }

    public boolean isRet() {
        return isRet;
    }

    public void setJump() {
        isJump = true;
    }

    public boolean isJump() {
        return isJump;
    }

    public Expression getLExp() {
        return lExp;
    }

    public Expression getRExp() {
        return rExp;
    }

    public int getLineNo() {
        return lineNo;
    }

    public boolean isDecl() {
        return declFlag;
    }

    public BasicBlock getParent() {
        return parent;
    }
}
