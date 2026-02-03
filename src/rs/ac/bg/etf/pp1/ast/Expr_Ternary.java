// generated with ast extension for cup
// version 0.8
// 3/1/2026 22:1:0


package rs.ac.bg.etf.pp1.ast;

public class Expr_Ternary extends Expr {

    private TernaryExpr TernaryExpr;

    public Expr_Ternary (TernaryExpr TernaryExpr) {
        this.TernaryExpr=TernaryExpr;
        if(TernaryExpr!=null) TernaryExpr.setParent(this);
    }

    public TernaryExpr getTernaryExpr() {
        return TernaryExpr;
    }

    public void setTernaryExpr(TernaryExpr TernaryExpr) {
        this.TernaryExpr=TernaryExpr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(TernaryExpr!=null) TernaryExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(TernaryExpr!=null) TernaryExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(TernaryExpr!=null) TernaryExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Expr_Ternary(\n");

        if(TernaryExpr!=null)
            buffer.append(TernaryExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Expr_Ternary]");
        return buffer.toString();
    }
}
