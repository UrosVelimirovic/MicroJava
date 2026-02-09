// generated with ast extension for cup
// version 0.8
// 9/1/2026 21:45:44


package rs.ac.bg.etf.pp1.ast;

public class ConstVarEnumList_Enum extends ConstVarEnumList {

    private ConstVarEnumList ConstVarEnumList;
    private EnumDeclList EnumDeclList;

    public ConstVarEnumList_Enum (ConstVarEnumList ConstVarEnumList, EnumDeclList EnumDeclList) {
        this.ConstVarEnumList=ConstVarEnumList;
        if(ConstVarEnumList!=null) ConstVarEnumList.setParent(this);
        this.EnumDeclList=EnumDeclList;
        if(EnumDeclList!=null) EnumDeclList.setParent(this);
    }

    public ConstVarEnumList getConstVarEnumList() {
        return ConstVarEnumList;
    }

    public void setConstVarEnumList(ConstVarEnumList ConstVarEnumList) {
        this.ConstVarEnumList=ConstVarEnumList;
    }

    public EnumDeclList getEnumDeclList() {
        return EnumDeclList;
    }

    public void setEnumDeclList(EnumDeclList EnumDeclList) {
        this.EnumDeclList=EnumDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstVarEnumList!=null) ConstVarEnumList.accept(visitor);
        if(EnumDeclList!=null) EnumDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstVarEnumList!=null) ConstVarEnumList.traverseTopDown(visitor);
        if(EnumDeclList!=null) EnumDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstVarEnumList!=null) ConstVarEnumList.traverseBottomUp(visitor);
        if(EnumDeclList!=null) EnumDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstVarEnumList_Enum(\n");

        if(ConstVarEnumList!=null)
            buffer.append(ConstVarEnumList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EnumDeclList!=null)
            buffer.append(EnumDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstVarEnumList_Enum]");
        return buffer.toString();
    }
}
