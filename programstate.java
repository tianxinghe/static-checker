import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.misc.Pair;
import com.microsoft.z3.*;

public class programstate {
    private Scope scope;
    private HashMap<Integer, Integer> inlineMap;  // inline count map (memory leak)
    private static HashMap<String, IntExpr> symbolMap = new HashMap<>(); // input name, symbol expr
    private Value condValue;

    public static ProgramState copy(ProgramState ps, Context ctx, Solver solver) {
        return null;
    }

    public void changeInlineCount(int blockID, int count) {
        inlineMap.put(blockID, count);
    }

    public int getInlineCount(int blockID) {
        if (inlineMap.containsKey(blockID))
            return inlineMap.get(blockID);
        return 0;
    }

    public boolean checkExample() {
        try{  
            HashMap<String, String> cfg = new HashMap<String, String>();  
            cfg.put("model", "true");  
            Context tmpctx = new Context(cfg);  
            Solver s = tmpctx.mkSolver();
            IntExpr input1 = tmpctx.mkIntConst("a"); // input a
            IntExpr input2 = tmpctx.mkIntConst("b"); // input b
            IntExpr intA = tmpctx.mkInt(1); // const int

            s.add(tmpctx.mkGt(input1, input2)); // signed a > b 
            s.add(tmpctx.mkGt(input1, tmpctx.mkInt(10))); // signed a > 10
            s.add(tmpctx.mkGt(tmpctx.mkInt(10), input2)); // signed 9 > b

            Status result = s.check(); 
            if (result == Status.SATISFIABLE){  
                System.out.println(s.getModel());  // possible value for a and b
            }  
            else if(result == Status.UNSATISFIABLE)  
                    System.out.println("unsat");  
            else   
                System.out.println("unknow");  
              
        } catch(Exception e) {  
            System.out.println("z3 exception");  
            e.printStackTrace();  
        }

        return false;
    }

    public IntExpr addOp(Value v, Context ctx, Solver solver) {
        IntExpr op1 = null;
        IntExpr op2 = null;
        // to do
        return null;
    }

    public void addConstraint(Value cond, Context ctx, Solver solver, boolean ifTrue) {
        Value v1 = cond.getOp1();
        Value v2 = cond.getOp2();
        String op = cond.getOp();

        try {
            IntExpr op1 = null;
            if (v1.getOp1() != null) {
                op1 = addOp(v1, ctx, solver);
            } else {
                if (v1.isInputRelated()) {
                    // to do 
                } else {
                    // to do
                }
            }

            IntExpr op2 = null;
            if (v2 != null) {
                // to do
            }

            switch(op){                
                // to do
            }         
        } catch(Exception e) {  
            System.out.println("z3 exception");  
            e.printStackTrace();  
        }
    }

    programstate(Scope scope) {
        this.scope = scope;
        inlineMap = new HashMap<>();
    }

    public void addSymbolValue(String symbol, Context ctx) {
        IntExpr input = ctx.mkIntConst(symbol);
        symbolMap.put(symbol, input);
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setCondValue(Value value) {
        this.condValue = value;
    }

    public Value getCondValue() {
        return condValue;
    }
}
