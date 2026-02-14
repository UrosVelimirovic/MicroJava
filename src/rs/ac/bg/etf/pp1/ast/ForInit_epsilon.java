// generated with ast extension for cup
// version 0.8
// 14/1/2026 18:33:47


package rs.ac.bg.etf.pp1.ast;

public class ForInit_epsilon extends ForInit {

    public ForInit_epsilon () {
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
        buffer.append("ForInit_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [ForInit_epsilon]");
        return buffer.toString();
    }
}
