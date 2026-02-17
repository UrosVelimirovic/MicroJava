// generated with ast extension for cup
// version 0.8
// 17/1/2026 12:42:23


package rs.ac.bg.etf.pp1.ast;

public class ActParsList_epsilon extends ActParsList {

    private ActParsListBegin ActParsListBegin;

    public ActParsList_epsilon (ActParsListBegin ActParsListBegin) {
        this.ActParsListBegin=ActParsListBegin;
        if(ActParsListBegin!=null) ActParsListBegin.setParent(this);
    }

    public ActParsListBegin getActParsListBegin() {
        return ActParsListBegin;
    }

    public void setActParsListBegin(ActParsListBegin ActParsListBegin) {
        this.ActParsListBegin=ActParsListBegin;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParsListBegin!=null) ActParsListBegin.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParsListBegin!=null) ActParsListBegin.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParsListBegin!=null) ActParsListBegin.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParsList_epsilon(\n");

        if(ActParsListBegin!=null)
            buffer.append(ActParsListBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ActParsList_epsilon]");
        return buffer.toString();
    }
}
