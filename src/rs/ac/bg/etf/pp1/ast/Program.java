// generated with ast extension for cup
// version 0.8
// 31/0/2026 19:26:52


package src.rs.ac.bg.etf.pp1.ast;

public class Program implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private Stirng I1;

    public Program (Stirng I1) {
        this.I1=I1;
        if(I1!=null) I1.setParent(this);
    }

    public Stirng getI1() {
        return I1;
    }

    public void setI1(Stirng I1) {
        this.I1=I1;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(I1!=null) I1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(I1!=null) I1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(I1!=null) I1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Program(\n");

        if(I1!=null)
            buffer.append(I1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Program]");
        return buffer.toString();
    }
}
