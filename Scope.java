import type.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scope {
    public HashMap<String, Type> table;    //符号表
    public HashMap<String, Value> valueTable; // program state map;
    public HashMap<String, Boolean> constTable;
    public List<Scope> childScopes;      //子作用域
    private int ID;
    private static int globalID = 0;
    private String contains;

    public Scope parent = null;     //该作用域的父级作用域
    public Scope caller = null; 

    Scope(Scope parent, String contains) {
        ID = globalID;
        globalID ++;
        table = new HashMap<>();
        valueTable = new HashMap<>();
        constTable = new HashMap<>();
        this.parent = parent;
        parent.getChildScope().add(this);
        childScopes = new ArrayList<>();
        this.contains = contains;
    }

    public String getContains() {
        return contains;
    }

    public Type find(String name) {     // 只在当前作用域里找
        if (table.containsKey(name))
            return table.get(name);
        return null;
    }

    public Value findValue(String name) {
        if (valueTable.containsKey(name)) {
            return valueTable.get(name);
        } else if (this.parent != null) {
            return parent.findValue(name);
        }

        return null;
    }

    public void put(String name, Value v, boolean isConst) {
        this.valueTable.put(name, v);
        this.constTable.put(name, isConst);
    }

    public void put(String name, Value v) {
        this.valueTable.put(name, v);
    }

    public HashMap<String, Value> getValueTable() {
        return valueTable;
    }

    public HashMap<String, Boolean> getConstTable() {
        return constTable;
    }

    public Type findWholeScope(String name){
        if (table.containsKey(name)){
            return table.get(name);
        }else if (this.parent != null){
            return parent.findWholeScope(name);
        }
        return null;
    }

    public void put(String name, Type type) {
        this.table.put(name, type);
    }

    public void assign(String name, Value v) {
        Scope toAssign = this;
        while (!toAssign.valueTable.containsKey(name)) {
            toAssign = toAssign.parent;
        }

        toAssign.put(name, v);
    }

    public boolean isConst(String name) {
        if (valueTable.containsKey(name)) {
            System.out.println("----");
            System.out.println(contains);
            if (constTable.containsKey(name)) {
                System.out.println("contains");
                return constTable.get(name);
            } else {
                return false;
            }
        } 
        
        if (this.parent != null) {
            return parent.isConst(name);
        }
        return false;        
    }

    public HashMap<String, Type> getTable() {
        return table;
    }

    public List<Scope> getChildScope() {
        return childScopes;
    }

    public int getID() {
        return ID;
    }

    public Scope getGlobalScope() {
        if (this.parent != null) {
            return this.parent.getGlobalScope();
        } else {
            return this;
        }
    }

    public Scope getParent() {
        return parent;
    }

    public void setCaller(Scope caller) {
        this.caller = caller;
    }

    public Scope getCaller() {
        return caller;
    }

    Scope() {
        ID = globalID;
        globalID ++;
        table = new HashMap<>();
        valueTable = new HashMap<>();
        constTable = new HashMap<>();
        childScopes = new ArrayList<>();
    }
}
