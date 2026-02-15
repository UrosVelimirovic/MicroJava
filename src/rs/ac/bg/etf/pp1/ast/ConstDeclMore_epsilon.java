// generated with ast extension for cup
// version 0.8
// 15/1/2026 21:53:56


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclMore_epsilon extends ConstDeclMore {

    public ConstDeclMore_epsilon () {
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
        buffer.append("ConstDeclMore_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclMore_epsilon]");
        return buffer.toString();
    }
}
