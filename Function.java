import java.beans.Expression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import expression.*;

public class Function {
    private String name;
    private BasicBlock EntryBlock;
    private ArrayList<Variable> params;
    private boolean retFlag = false;

    public void setRet(boolean retFlag) {
        this.retFlag = retFlag;
    }

    public String getName() {
        return name;
    }
    
    public BasicBlock getEntryBlock() {
        return EntryBlock;
    }

    public void addParams(Variable param) {
        params.add(param);
    }

    public ArrayList<Variable> getParams() {
        return params;
    }

    public void print() {
        HashMap<Integer, Integer> nameMap = new HashMap<>();
        Stack<BasicBlock> printStack = new Stack<>();

        System.out.print("function " + name + "(");
        for (Variable param : params) {
            System.out.print(param.toString()+",");
        }
        System.out.println("):");

        printStack.push(EntryBlock);
        while (!printStack.empty()) {
            BasicBlock workBlock = printStack.pop();

            if (!nameMap.containsKey(workBlock.getID())) {
                nameMap.put(workBlock.getID(), 1);
                workBlock.print();
                if (workBlock.getSucc1() != null) {
                    System.out.println("Block" + workBlock.getSucc1().getID() + " " + workBlock.getScopeDepth1());
                }

                if (workBlock.getSucc2() != null) {
                    System.out.println("Block" + workBlock.getSucc2().getID() + " " + workBlock.getScopeDepth2());
                }
                System.out.println();

                if (workBlock.getSucc1() != null) {
                    printStack.push(workBlock.getSucc1());
                }

                if (workBlock.getSucc2() != null) {
                    printStack.push(workBlock.getSucc2());
                }
            }
        }
    }

    public Function(String name) {
        this.name = name;
        params = new ArrayList<>();
        EntryBlock = new BasicBlock(this);
    }
}
