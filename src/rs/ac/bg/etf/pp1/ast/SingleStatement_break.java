// generated with ast extension for cup
// version 0.8
// 17/1/2026 18:18:14


package rs.ac.bg.etf.pp1.ast;

public class SingleStatement_break extends SingleStatement {

    public SingleStatement_break () {
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
        buffer.append("SingleStatement_break(\n");

        buffer.append(tab);
        buffer.append(") [SingleStatement_break]");
        return buffer.toString();
    }
}
