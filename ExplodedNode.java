import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.z3.*;

public class ExplodedNode {
    private boolean isReached = false;
    private ArrayList<ExplodedNode> childList;
    private ExplodedNode parent;
    private ProgramState ps;
    private static HashMap<String, String> cfg = new HashMap<String, String>() {{
        put("model", "true");
    }};
    private static Context ctx = new Context(cfg); // z3 context
    private static Solver solver = ctx.mkSolver(); // z3 solver
    private Instruction curInst;
    private BasicBlock curBlock;
    private boolean isCall = false;
    private boolean isRet = false;

    private int ID;
    private static int globalID = 0;

    public ExplodedNode getParent() {
        return parent;
    }

    public ArrayList<ExplodedNode> getChildList() {
        return childList;
    }

    public int getID() {
        return ID;
    }

    public ExplodedNode(ExplodedNode parent) {
        ID = globalID;
        globalID ++;
        this.parent = parent;
        childList = new ArrayList<>();
        parent.getChildList().add(this);
    }

    public ExplodedNode() {
        ID = globalID;
        globalID ++;
        childList = new ArrayList<>();
    }

    public boolean isReached() {
        return isReached;
    }

    public void setReached() {
        this.isReached = true;
    }

    public void setCurInst(Instruction inst) {
        this.curInst = inst;
    }

    public void setCurBlock(BasicBlock block) {
        this.curBlock = block;
    }

    public BasicBlock getBasicBlock() {
        return curBlock;
    }
    
    public void setProgramState(ProgramState ps) {
        this.ps = ps;
    }

    public ProgramState getProgramState() {
        return ps;
    }

    public Instruction getCurInst() {
        return curInst;
    }

    public static Context getContext() {
        return ctx;
    }

    public static Solver getSolver() {
        return solver;
    }

    public boolean isCall() {
        return isCall;
    }

    public boolean isRet() {
        return isRet;
    }

    public void setCall() {
        isCall = true;
    }

    public void setRet() {
        isRet = true;
    }

    public static void solverPush() {
        solver.push();
    }

    public static void solverPop() {
        solver.pop();
    }

    public static boolean check() {
        try {  
            Status result = solver.check(); 
            if (result == Status.SATISFIABLE){ 
                System.out.println(solver.getModel());  // possible value for a and b 
                return true;
            }  
            else if(result == Status.UNSATISFIABLE)  
                    return false;
            else   
                return true; 
              
        } catch(Exception e) {  
            System.out.println("z3 exception");  
            e.printStackTrace();  
        }
        return false;
    }
}
