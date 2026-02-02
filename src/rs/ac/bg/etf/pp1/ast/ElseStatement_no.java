// generated with ast extension for cup
// version 0.8
// 2/1/2026 22:14:1


package rs.ac.bg.etf.pp1.ast;

public class ElseStatement_no extends ElseStatement {

    public ElseStatement_no () {
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
        buffer.append("ElseStatement_no(\n");

        buffer.append(tab);
        buffer.append(") [ElseStatement_no]");
        return buffer.toString();
    }
}
