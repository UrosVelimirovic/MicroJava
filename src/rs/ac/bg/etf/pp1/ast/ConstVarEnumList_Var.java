// generated with ast extension for cup
// version 0.8
// 2/1/2026 21:45:3


package src.rs.ac.bg.etf.pp1.ast;

public class ConstVarEnumList_Var extends ConstVarEnumList {

    private ConstVarEnumList ConstVarEnumList;
    private VarDeclList VarDeclList;

    public ConstVarEnumList_Var (ConstVarEnumList ConstVarEnumList, VarDeclList VarDeclList) {
        this.ConstVarEnumList=ConstVarEnumList;
        if(ConstVarEnumList!=null) ConstVarEnumList.setParent(this);
        this.VarDeclList=VarDeclList;
        if(VarDeclList!=null) VarDeclList.setParent(this);
    }

    public ConstVarEnumList getConstVarEnumList() {
        return ConstVarEnumList;
    }

    public void setConstVarEnumList(ConstVarEnumList ConstVarEnumList) {
        this.ConstVarEnumList=ConstVarEnumList;
    }

    public VarDeclList getVarDeclList() {
        return VarDeclList;
    }

    public void setVarDeclList(VarDeclList VarDeclList) {
        this.VarDeclList=VarDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstVarEnumList!=null) ConstVarEnumList.accept(visitor);
        if(VarDeclList!=null) VarDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstVarEnumList!=null) ConstVarEnumList.traverseTopDown(visitor);
        if(VarDeclList!=null) VarDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstVarEnumList!=null) ConstVarEnumList.traverseBottomUp(visitor);
        if(VarDeclList!=null) VarDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstVarEnumList_Var(\n");

        if(ConstVarEnumList!=null)
            buffer.append(ConstVarEnumList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclList!=null)
            buffer.append(VarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstVarEnumList_Var]");
        return buffer.toString();
    }
}
