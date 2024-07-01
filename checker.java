import type.*;
import expression.*;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.microsoft.z3.BoolExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.HashMap;

public class checker extends SysYParserBaseVisitor<Void>{
    public static Scope scope = new Scope();    // 全局作用域
    public static Scope curScope = scope;     //当前所处的作用域

    private Type tmpTy;
    private String tmpName;
    private ArrayList<Type> tmpTyArr = new ArrayList<>();
    private ArrayList<String> tmpNameArr = new ArrayList<>();
    private int tmpInt;
    private Value tmpValue;
    private BasicBlock tmpBlock;
    private Expression tmpExp;
    private Stack<BasicBlock> ifEndOrWhileCondStack = new Stack<>();
    private boolean getIntElementFlag = false;
    private boolean inInstFlag = false;
    private boolean inExpFlag = false;
    private boolean inFuncFlag = false;
    private boolean checkStopFlag = false;
    private BasicBlock curBlock;
    private Instruction curInst;
    private int tmpFlag = 0;

    /*
     * a = func(b);
     * a = b + c;
     * and so on
     */

    private HashMap<String, SysYParser.FuncDefContext> funcCtxMap = new HashMap<>();
    private HashMap<String, Function> funcMap = new HashMap<>();
    private ArrayList<Instruction> globalInsts = new ArrayList<>();

    private int tmpLineNo;
    private ExplodedNode curNode;
    private ExplodedNode rootNode;

    public Value Var2Value(Variable var) {
        // to do;
        return null;
    }

    public Value Arith2Value(ArithExpression var) {
        // to do
        return null;
    }

    public Value Num2Value(Num num){
        Scope curScope = curNode.getProgramState().getScope();
        return new Value(IntType.getI32(), null, curScope, num.getNum());
    }

    public boolean Call2Value(CallExpression ce) { 
        // to do
        return true;
    }

    public Value Exp2Value(Expression exp) {
        // to do
        return null;
    }

    public boolean Inst2Scope(Instruction inst) {
        // to do
        return true;
    }

    public void rollBack() {
        while (curNode.getParent() != null) {
            // to do
        }

        if (curNode == rootNode) {
            // to do 
        } else {
            // to do
        }
    }

    public boolean tasteReturn(Instruction retInst) {
        if (curBlock.getParent().getName().equals("main")) {
            return false;
        } else {
            // to do
        }
        return true;
    }

    public void tasteSucc(int i) {
        // to do
    }

    public boolean tasteCondition(Instruction condInst) {
        // to do
        return false;
    }

    public void StaticCheckerEngine() {
        ExplodedNode node = new ExplodedNode();
        Scope globalScope = new Scope();
        ProgramState ps = new ProgramState(globalScope);
        node.setProgramState(ps);
        node.setReached();
        curNode = node;
        rootNode = node;
        curScope = globalScope;

        for (Instruction inst : globalInsts) {
            Inst2Scope(inst);
        }

        Function mainFunc = funcMap.get("main");
        Scope mainScope = new Scope(globalScope, mainFunc.getName());
        ps.setScope(mainScope);

        ArrayList<Value> paramList = new ArrayList<>();
        for (int i = 0; i < mainFunc.getParams().size(); i ++) {
            Variable param = mainFunc.getParams().get(i);
            assert(param.getSizeList().size() == 0);
            Type intTy = IntType.getI32();
            Value pValue = new Value(intTy, param.getName(), ps.getScope(), param.getName());
            ps.addSymbolValue(param.getName(), curNode.getContext());
        }

        BasicBlock entryBlock = mainFunc.getEntryBlock();
        Scope newScope = new Scope(mainScope, "Block" + entryBlock.getID());
        ps.setScope(newScope);

        curBlock = entryBlock;
        ps.changeInlineCount(curBlock.getID(), 1);
        curInst = curBlock.getInstList().get(0);
        while(curInst != null) {
            curScope = curNode.getProgramState().getScope();
            boolean checkFlag = true;
            while (curBlock.getInstList().indexOf(curInst) == curBlock.getInstList().size() - 1) {
                if (curInst.isCond()) {
                    // to do
                }

                if (curInst.isRet()) {
                    // to do
                }

                if (curInst.isJump()) {
                    // to do 
                }
            }

            if (checkFlag) {
                ArrayList<Expression> callList = new ArrayList<>();
                curInst.addCaller(callList);
                if (callList.size() == 0) {
                    curScope = curNode.getProgramState().getScope();
                    checkFlag = Inst2Scope(curInst);
                    if (checkFlag) {
                        curInst = curBlock.getInstList().get(curBlock.getInstList().indexOf(curInst) + 1);
                    }
                } else {
                    checkFlag = Call2Value((CallExpression)curInst.getRExp());
                }               
            }

            if (checkFlag == false) {
                // to do
            }
        }
    }

    @Override
    public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
        for (int i = 0; i < ctx.constDef().size(); i ++) {
            visit(ctx.constDef(i));
        }

        return null;
    }

    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        String varName = ctx.IDENT().getText();
        inInstFlag = true;
        Instruction inst = null;
        ArrayList<Expression> sizes = new ArrayList<>();

        for (int i = 0; i < ctx.constExp().size(); i++) {
            visit(ctx.constExp(i));
            sizes.add(tmpExp);
        }

        Variable var = new Variable(varName, sizes, true);
        if (ctx.ASSIGN() != null) {
            visitConstInitVal(ctx.constInitVal());
            inst = new Instruction(ctx.IDENT().getSymbol().getLine(), var, tmpExp, tmpBlock, true);
        } else {
            inst = new Instruction(ctx.IDENT().getSymbol().getLine(), var, tmpBlock, true);
        }

        if (inFuncFlag) {
            tmpBlock.insertInstruction(inst);
        } else {
            globalInsts.add(inst);
        }
        inInstFlag = false;
        return null;
    }

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
        for (int i = 0; i < ctx.varDef().size(); i ++) {
            visit(ctx.varDef(i));
        }

        return null;
    }

    @Override
    public Void visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        visit(ctx.constExp().exp());
        return null;
    }

    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        String varName = ctx.IDENT().getText();
        inInstFlag = true;
        Instruction inst = null;
        ArrayList<Expression> sizes = new ArrayList<>();

        for (int i = 0; i < ctx.constExp().size(); i++) {
            visit(ctx.constExp(i));
            sizes.add(tmpExp);
        }

        Variable var = new Variable(varName, sizes, false);
        if (ctx.ASSIGN() != null) {
            visitInitVal(ctx.initVal());
            inst = new Instruction(ctx.IDENT().getSymbol().getLine(), var, tmpExp, tmpBlock, true);
        } else {
            inst = new Instruction(ctx.IDENT().getSymbol().getLine(), var, tmpBlock, true);
        }

        if (inFuncFlag) {
            tmpBlock.insertInstruction(inst);
        } else {
            globalInsts.add(inst);
        }
        
        inInstFlag = false;
        return null;
    }

    @Override
    public Void visitInitVal(SysYParser.InitValContext ctx) {
        visit(ctx.exp());
        return null;
    }

    public void addJumps(Function func) {
        HashMap<Integer, Integer> nameMap = new HashMap<>();
        Stack<BasicBlock> printStack = new Stack<>();
        BasicBlock entryBlock = func.getEntryBlock();

        printStack.push(entryBlock);
        while (!printStack.empty()) {
            BasicBlock workBlock = printStack.pop();

            if (!nameMap.containsKey(workBlock.getID())) {
                nameMap.put(workBlock.getID(), 1);
                if (workBlock.getInstList().isEmpty() 
                    || (!workBlock.getInstList().get(workBlock.getInstList().size() - 1).isCond() && 
                        !workBlock.getInstList().get(workBlock.getInstList().size() - 1).isRet())) {
                            Instruction inst = new Instruction(0, null, workBlock);
                            workBlock.insertInstruction(inst);
                            inst.setJump();
                        }

                if (workBlock.getSucc1() != null) {
                    printStack.push(workBlock.getSucc1());
                }

                if (workBlock.getSucc2() != null) {
                    printStack.push(workBlock.getSucc2());
                }
            }
        }
    }

    /**
     * funcDef : funcType IDENT L_PAREN funcFParams? R_PAREN block
     */
    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
        inFuncFlag = true;

        String funcName = ctx.IDENT().getText();
        Function func = new Function(funcName);
        funcMap.put(funcName, func);
        funcCtxMap.put(funcName, ctx);

        String retStr = ctx.getChild(0).getText();
        if (retStr.equals("int")) {
            func.setRet(true);
        }

        ArrayList<Type> paramsTyList = new ArrayList<>();
        if (ctx.funcFParams() != null) {
            visit(ctx.funcFParams());
            paramsTyList.addAll(tmpTyArr);
        }

        for (int i = 0; i < tmpNameArr.size(); i ++) {
            Type pty = tmpTyArr.get(i);
            String pname = tmpNameArr.get(i);
            ArrayList<Expression> List = new ArrayList<>();
            if (pty.isArrayTy()) {
                while (pty.isArrayTy()) {
                    List.add(new Num(-1));
                }
            }
            Variable pValue = new Variable(pname, List, false);
            func.addParams(pValue);
        }

        tmpNameArr.clear();
        tmpTyArr.clear();

        tmpBlock = func.getEntryBlock();

        visit(ctx.block());
        addJumps(func);
        func.print();

        inFuncFlag = false;

        if (funcName.equals("main")) {
            StaticCheckerEngine();
        }
        return null;
    }

    @Override
    public Void visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        ArrayList<Type> params = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ctx.funcFParam().forEach(param -> {
            visit(param);
            params.add(tmpTy);
            names.add(tmpName);
        });
        tmpTyArr = params;
        tmpNameArr = names;
        return null;
    }

    @Override
    public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {     
        //数组参数
        if (!ctx.L_BRACKT().isEmpty()) {
            Type type = IntType.getI32();
            for (int i = 0; i < ctx.exp().size(); i++) {
                visit(ctx.exp(ctx.exp().size() - (i + 1))); // 从后往前访问
                type = new ArrayType(type, tmpInt);       // 构建嵌套的数组类型
            }
            tmpTy = new PointerType(type);   //如果只是一维数组的话，那type只会是IntType
        } else {
            tmpTy = IntType.getI32();
        }
        tmpName = ctx.IDENT().getText();
        return null;
    }

    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        ctx.blockItem().forEach(this::visit);
        return null;
    }

    @Override
    public Void visitWhileStmt(SysYParser.WhileStmtContext ctx) {
        Expression exp1 = null;
        Expression exp2 = null;
        String op = null;
        ArithExpression cond = null;
        SysYParser.BlockStmtContext whileBlockCtx = null;
        SysYParser.ExpCondContext expCond1 = null;
        SysYParser.ExpCondContext expCond2 = null;
        // while ((a - 1) > (b - 2))
        // (a-1) : expCond1    
        // (b-2) : expCond2
        //   >   : op

        BasicBlock whileCond = new BasicBlock(tmpBlock.getParent());
        tmpBlock.setSucc(whileCond);
        tmpBlock.setScopeDepth1(0); // depth + 1;
        inInstFlag = true;

        for(int i = 0; i < ctx.getChildCount(); ++i) {
            if (ctx.getChild(i).getChildCount() > 1) {                
                for (int j = 0; j < ctx.getChild(i).getChildCount(); j ++) {
                    if (ctx.getChild(i).getChild(j) instanceof SysYParser.ExpCondContext) {
                        if (expCond1 == null) {
                            expCond1 = ((SysYParser.ExpCondContext) ctx.getChild(i).getChild(j));
                        } else {
                            expCond2 = ((SysYParser.ExpCondContext) ctx.getChild(i).getChild(j));
                        }
                    }

                    switch(ctx.getChild(i).getChild(j).getText()) {
                        case("<"):{
                            op = "<";
                            break;
                        }

                        case(">"):{
                            op = ">";
                            break;
                        }

                        case("<="):{
                            op = "<=";
                            break;
                        }

                        case(">="):{
                            op = ">=";
                            break;
                        }

                        case("=="):{
                            op = "==";
                            break;
                        }

                        case("!="):{
                            op = "!=";
                            break;
                        }
                    }
                }
            }

            if (ctx.getChild(i) instanceof SysYParser.BlockStmtContext) {
                whileBlockCtx = ((SysYParser.BlockStmtContext) ctx.getChild(i));
            }
        }

        visit(expCond1);
        exp1 = tmpExp;

        if (expCond2 != null) {
            visit(expCond2);
            exp2 = tmpExp;
            cond = new ArithExpression(exp1, exp2, op); 
        } else {
            cond = new ArithExpression(exp1, op); 
        }
        
        BasicBlock whileBody = new BasicBlock(tmpBlock.getParent());
        BasicBlock whileEnd = new BasicBlock(tmpBlock.getParent());

        whileCond.setSuccs(whileBody, whileEnd);
        Instruction inst = new Instruction(ctx.getStart().getLine(), cond, whileCond);
        whileCond.insertInstruction(inst);
        inst.setCond();

        whileCond.setScopeDepth1(1);
        whileCond.setScopeDepth2(0);

        whileBody.setSucc(whileCond);
        whileBody.setScopeDepth1(-1);
        
        inInstFlag = false;

        if(!ifEndOrWhileCondStack.empty()) {
            whileEnd.setSucc(ifEndOrWhileCondStack.lastElement());
        }
        ifEndOrWhileCondStack.push(whileCond);

        tmpBlock = whileBody;
        visit(whileBlockCtx);
        tmpBlock = whileEnd;

        if(!ifEndOrWhileCondStack.empty()) {
            ifEndOrWhileCondStack.pop();
        }
        
        return null;
    }

    @Override
    public Void visitConditionStmt(SysYParser.ConditionStmtContext ctx) {
        int n = ctx.getChildCount();
        ArithExpression cond = null;
        Expression op1 = null;
        Expression op2 = null;
        SysYParser.BlockStmtContext trueBlockCtx = null;
        SysYParser.BlockStmtContext falseBlockCtx = null;
        String op = null;
        inInstFlag = true;

        for(int i = 0; i < n; ++i) {
            if (ctx.getChild(i).getChildCount() > 1) {                
                for (int j = 0; j < ctx.getChild(i).getChildCount(); j ++) {
                    if (ctx.getChild(i).getChild(j) instanceof SysYParser.ExpCondContext) {
                        visit(ctx.getChild(i).getChild(j));
                        if (op1 == null) {
                            op1 = tmpExp;
                        } else {
                            op2 = tmpExp;
                        }
                    }

                    switch(ctx.getChild(i).getChild(j).getText()) {
                        case("<"):{
                            op = "<";
                            break;
                        }

                        case(">"):{
                            op = ">";
                            break;
                        }

                        case("<="):{
                            op = "<=";
                            break;
                        }

                        case(">="):{
                            op = ">=";
                            break;
                        }

                        case("=="):{
                            op = "==";
                            break;
                        }

                        case("!="):{
                            op = "!=";
                            break;
                        }
                    }
                }
            }
           
            if (ctx.getChild(i) instanceof SysYParser.BlockStmtContext) {
                if (trueBlockCtx == null) {
                    trueBlockCtx = ((SysYParser.BlockStmtContext) ctx.getChild(i));
                } else {
                    falseBlockCtx = ((SysYParser.BlockStmtContext) ctx.getChild(i));
                }
            }
        }

        if (op2 != null) {
            cond = new ArithExpression(op1, op2, op); 
        } else {
            cond = new ArithExpression(op1, op); 
        }
        inInstFlag = false;

        BasicBlock ifBody = new BasicBlock(tmpBlock.getParent());
        BasicBlock ifEnd = new BasicBlock(tmpBlock.getParent());
        if (!ifEndOrWhileCondStack.empty()) {
            ifEnd.setSucc(ifEndOrWhileCondStack.lastElement());
            ifEnd.setScopeDepth1(-1);
        }
        ifEndOrWhileCondStack.push(ifEnd);

        if (falseBlockCtx == null) {
            tmpBlock.setSuccs(ifBody, ifEnd);
            Instruction inst = new Instruction(ctx.getStart().getLine(), cond, tmpBlock);
            tmpBlock.insertInstruction(inst);
            inst.setCond();

            tmpBlock.setScopeDepth1(1);
            tmpBlock.setScopeDepth2(0);
            ifBody.setSucc(ifEnd);
            ifBody.setScopeDepth1(-1);
            tmpBlock = ifBody;
            visit(trueBlockCtx);
            tmpBlock = ifEnd;
        } else {
            BasicBlock elseBody = new BasicBlock(tmpBlock.getParent());
            tmpBlock.setSuccs(ifBody, elseBody);
            Instruction inst = new Instruction(ctx.getStart().getLine(), cond, tmpBlock);
            tmpBlock.insertInstruction(inst);
            inst.setCond();

            tmpBlock.setScopeDepth1(1);
            tmpBlock.setScopeDepth2(1);
            ifBody.setSucc(ifEnd);
            elseBody.setSucc(ifEnd);
            ifBody.setScopeDepth1(-1);
            elseBody.setScopeDepth1(-1);
            tmpBlock = ifBody;
            visit(trueBlockCtx);
            tmpBlock = elseBody;
            visit(falseBlockCtx);
            tmpBlock = ifEnd;
        }

        if (!ifEndOrWhileCondStack.empty()) {
            ifEndOrWhileCondStack.pop();
        }

        return null;
    }

    @Override
    public Void visitExpCond(SysYParser.ExpCondContext ctx) {
        visit(ctx.exp());
        return null;
    }

    @Override
    public Void visitPlusExp(SysYParser.PlusExpContext ctx) {
        checkArithmeticOperation(ctx.exp(0), ctx.exp(1), (TerminalNode) ctx.getChild(1));
        return null;
    }

    @Override
    public Void visitMulExp(SysYParser.MulExpContext ctx) {
        checkArithmeticOperation(ctx.exp(0), ctx.exp(1), (TerminalNode) ctx.getChild(1));
        return null;
    }

    @Override
    public Void visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        ArrayList<Type> params = null;
        Type currentType;
        for (int i = 0; i < ctx.param().size(); i++) {
            visit(ctx.param(i));
            if (tmpTy == null) {
                continue;
            }

            currentType = tmpTy;
            if (params == null) {
                params = new ArrayList<>();
            }
            params.add(currentType);
        }

        tmpTyArr = params;
        return null;
    }
 
    @Override
    public Void visitCallFuncExp(SysYParser.CallFuncExpContext ctx) {
        SysYParser.FuncDefContext fctx = funcCtxMap.get(ctx.IDENT().getText());
        ArrayList<Expression> params = new ArrayList<>();
        
        boolean instFlag = true;
        boolean expFlag = true;
        if (inInstFlag)
            instFlag = false;
        if (inExpFlag) 
            expFlag = false;

        inInstFlag = true;
        inExpFlag = true;

        if (ctx.funcRParams() != null) { //check if rparams are empty
            assert(fctx.funcFParams().funcFParam().size() == ctx.funcRParams().param().size());
            for (int i = 0; i < ctx.funcRParams().param().size(); i ++) {
                String fparamName = fctx.funcFParams().funcFParam(i).IDENT().getText();
                visit(ctx.funcRParams().param(i));
                params.add(tmpExp);
            }
        }

        CallExpression exp = new CallExpression(ctx.IDENT().getText(), params);
        if (instFlag) {
            Instruction inst = new Instruction(ctx.getStart().getLine(), exp, tmpBlock);
            tmpBlock.insertInstruction(inst);
            inInstFlag = false;
        } else {
            if (!expFlag) {
                Variable retVar = new Variable();
                Instruction inst = new Instruction(ctx.getStart().getLine(), retVar, exp, tmpBlock, false);
                tmpBlock.insertInstruction(inst);
                tmpExp = retVar;
            } else {
                tmpExp = exp;
                inExpFlag = false;
            }
        }

        return null;
    }

    @Override
    public Void visitNumberExp(SysYParser.NumberExpContext ctx) {
        visit(ctx.number());
        return null;
    }

    @Override
    public Void visitLVal(SysYParser.LValContext ctx) {
        String lValName = ctx.IDENT().getText();
        ArrayList<expression.Expression> List = new ArrayList<>();
        for (int i = 0; i < ctx.exp().size(); i++) {
            visit(ctx.exp(i));
            List.add(tmpExp);
        }

        Variable lValue = new Variable(lValName, List, false);
        tmpExp = lValue;
        return null;
    }

    @Override
    public Void visitNumber(SysYParser.NumberContext ctx) {
        TerminalNode node = ctx.INTEGER_CONST();
        if (node.getText().startsWith("0x") || node.getText().startsWith("0X")) {    //十六进制
            tmpInt = Integer.parseInt(node.getText().substring(2), 16);
        } else if (node.getText().length() > 1 && node.getText().startsWith("0")) {   // 八进制
            tmpInt = Integer.parseInt(node.getText(), 8);
        } else {    //十进制
            tmpInt = Integer.parseInt(node.getText(), 10);
        }

        tmpExp = new Num(tmpInt);
        return null;
    }

    @Override
    public Void visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
        if (ctx.exp() != null) {     //void
            inInstFlag = true;
            inExpFlag = true;
            visit(ctx.exp());
            inExpFlag = false;
            inInstFlag = false;
            Instruction inst = new Instruction(ctx.getStart().getLine(), tmpExp, tmpBlock);
            tmpBlock.insertInstruction(inst);
            inst.setRet();    
        } else {
            Instruction inst = new Instruction(ctx.getStart().getLine(), null, tmpBlock);
            tmpBlock.insertInstruction(inst);
            inst.setRet();
        }
        return null;
    }

    @Override
    public Void visitConstExp(SysYParser.ConstExpContext ctx) {
        visit(ctx.exp());
        return null;
    }

    private Void checkArithmeticOperation(SysYParser.ExpContext exp1, SysYParser.ExpContext exp2,
            TerminalNode operand) {
        boolean instFlag = true;
        boolean expFlag = true;
        if (inInstFlag)
            instFlag = false;
        
        if (inExpFlag) 
            expFlag = false;

        inInstFlag = true;
        inExpFlag = true;
        visit(exp1);
        Expression op1 = tmpExp;

        visit(exp2);
        Expression op2 = tmpExp;

        Expression res = new ArithExpression(op1, op2, operand.getText());
        if (instFlag) {
            Instruction inst = new Instruction(exp1.getStart().getLine(), res, tmpBlock);
            tmpBlock.insertInstruction(inst);
            inInstFlag = false;
        } else {
            tmpExp = res;
        }

        if (expFlag)
            inExpFlag = false;

        return null;
    }

    @Override
    public Void visitAssignStmt(SysYParser.AssignStmtContext ctx) {
        inInstFlag = true;

        SysYParser.LValContext lValContext = ctx.lVal();
        String lValName = lValContext.IDENT().getText();
        visit(lValContext);
        Expression op1 = tmpExp;

        SysYParser.ExpContext expContext = ctx.exp();
        visit(expContext);
        Expression op2 = tmpExp;

        Instruction inst = new Instruction(lValContext.IDENT().getSymbol().getLine(), op1, op2, tmpBlock, false);
        tmpBlock.insertInstruction(inst);
        inInstFlag = false;

        return null;
    }
}

