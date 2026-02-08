// generated with ast extension for cup
// version 0.8
// 8/1/2026 17:1:41


package rs.ac.bg.etf.pp1.ast;

public class FormParsList_epsilon extends FormParsList {

    public FormParsList_epsilon () {
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
        buffer.append("FormParsList_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [FormParsList_epsilon]");
        return buffer.toString();
    }
}
