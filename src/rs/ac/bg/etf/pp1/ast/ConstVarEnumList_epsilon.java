// generated with ast extension for cup
// version 0.8
// 31/0/2026 23:29:22


package src.rs.ac.bg.etf.pp1.ast;

public class ConstVarEnumList_epsilon extends ConstVarEnumList {

    public ConstVarEnumList_epsilon () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstVarEnumList_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [ConstVarEnumList_epsilon]");
        return buffer.toString();
    }
}
