import expression.*;
import java.util.ArrayList;

public class BasicBlock {
    private Function parent;
    private ArrayList<Instruction> instList;
    private BasicBlock succ1 = null;
    private BasicBlock succ2 = null;
    private int scopeDepth1;
    private int scopeDepth2;
    private int ID;
    private static int globalID = 0;

    public Function getParent() {
        return parent;
    }

    public void insertInstruction(Instruction inst) {
        instList.add(inst);
    }

    public void setSuccs(BasicBlock succ1, BasicBlock succ2) {
        this.succ1 = succ1;
        this.succ2 = succ2;
    }

    public void setScopeDepth1(int scopeDepth1) {
        this.scopeDepth1 = scopeDepth1;
    }

    public void setScopeDepth2(int scopeDepth2) {
        this.scopeDepth2 = scopeDepth2;
    }

    public int getScopeDepth1() {
        return scopeDepth1;
    }

    public int getScopeDepth2() {
        return scopeDepth2;
    }

    public void setSucc(BasicBlock succ1) {
        this.succ1 = succ1;
    }

    public BasicBlock getSucc1() {
        return succ1;
    }

    public BasicBlock getSucc2() {
        return succ2;
    }

    public int getID() {
        return ID;
    }

    public ArrayList<Instruction> getInstList() {
        return instList;
    }

    BasicBlock(Function parent) {
        ID = globalID;
        globalID ++;
        instList = new ArrayList<>();
        this.parent = parent;
    }

    public void print(){
        System.out.println("Block" + ID + ":");
        for (int i = 0; i < instList.size(); i ++) {
            if (instList.get(i).isCond()) {
                System.out.println("lineNo" + instList.get(i).getLineNo() + " : " + "if(" + instList.get(i).getRExp().toString() + ") goto Block " + succ1.ID);
                System.out.println("else goto Block " + succ2.ID);
                break;
            }

            if (instList.get(i).isRet()) {
                System.out.println("lineNo" + instList.get(i).getLineNo() + " : " + "return " + instList.get(i).getRExp().toString());
                break;
            }

            if (instList.get(i).isJump()) {
                System.out.println("goto Block " + succ1.ID);
                break;
            }

            System.out.println(instList.get(i).toString());
        }

        
        // if (cond != null) {
        //     System.out.println("if(" + cond.toString() + ") goto Block " + succ1.ID);
        //     System.out.println("else goto Block " + succ2.ID);
        // } else {
        //     if (succ1 != null) {
        //         System.out.println("goto Block " + succ1.ID);
        //     } else {
        //         System.out.println("return " + ret.toString());
        //     }
        // }
        System.out.println();
    }
}
