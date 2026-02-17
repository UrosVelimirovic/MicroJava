// generated with ast extension for cup
// version 0.8
// 17/1/2026 18:18:14


package rs.ac.bg.etf.pp1.ast;

public class Designator_arraylength extends Designator {

    private DesignatorArrayLengthHelper DesignatorArrayLengthHelper;

    public Designator_arraylength (DesignatorArrayLengthHelper DesignatorArrayLengthHelper) {
        this.DesignatorArrayLengthHelper=DesignatorArrayLengthHelper;
        if(DesignatorArrayLengthHelper!=null) DesignatorArrayLengthHelper.setParent(this);
    }

    public DesignatorArrayLengthHelper getDesignatorArrayLengthHelper() {
        return DesignatorArrayLengthHelper;
    }

    public void setDesignatorArrayLengthHelper(DesignatorArrayLengthHelper DesignatorArrayLengthHelper) {
        this.DesignatorArrayLengthHelper=DesignatorArrayLengthHelper;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorArrayLengthHelper!=null) DesignatorArrayLengthHelper.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorArrayLengthHelper!=null) DesignatorArrayLengthHelper.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorArrayLengthHelper!=null) DesignatorArrayLengthHelper.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Designator_arraylength(\n");

        if(DesignatorArrayLengthHelper!=null)
            buffer.append(DesignatorArrayLengthHelper.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Designator_arraylength]");
        return buffer.toString();
    }
}
