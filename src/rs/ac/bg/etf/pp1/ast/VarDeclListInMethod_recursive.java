// generated with ast extension for cup
// version 0.8
// 9/1/2026 21:45:44


package rs.ac.bg.etf.pp1.ast;

public class VarDeclListInMethod_recursive extends VarDeclListInMethod {

    private VarDeclListInMethod VarDeclListInMethod;
    private VarDeclList VarDeclList;

    public VarDeclListInMethod_recursive (VarDeclListInMethod VarDeclListInMethod, VarDeclList VarDeclList) {
        this.VarDeclListInMethod=VarDeclListInMethod;
        if(VarDeclListInMethod!=null) VarDeclListInMethod.setParent(this);
        this.VarDeclList=VarDeclList;
        if(VarDeclList!=null) VarDeclList.setParent(this);
    }

    public VarDeclListInMethod getVarDeclListInMethod() {
        return VarDeclListInMethod;
    }

    public void setVarDeclListInMethod(VarDeclListInMethod VarDeclListInMethod) {
        this.VarDeclListInMethod=VarDeclListInMethod;
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
        if(VarDeclListInMethod!=null) VarDeclListInMethod.accept(visitor);
        if(VarDeclList!=null) VarDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclListInMethod!=null) VarDeclListInMethod.traverseTopDown(visitor);
        if(VarDeclList!=null) VarDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclListInMethod!=null) VarDeclListInMethod.traverseBottomUp(visitor);
        if(VarDeclList!=null) VarDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclListInMethod_recursive(\n");

        if(VarDeclListInMethod!=null)
            buffer.append(VarDeclListInMethod.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclList!=null)
            buffer.append(VarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclListInMethod_recursive]");
        return buffer.toString();
    }
}
