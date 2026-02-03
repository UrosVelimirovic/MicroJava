// generated with ast extension for cup
// version 0.8
// 3/1/2026 22:1:0


package rs.ac.bg.etf.pp1.ast;

public class ActParsMore_epsilon extends ActParsMore {

    public ActParsMore_epsilon () {
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
        buffer.append("ActParsMore_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [ActParsMore_epsilon]");
        return buffer.toString();
    }
}
