// generated with ast extension for cup
// version 0.8
// 17/1/2026 18:18:14


package rs.ac.bg.etf.pp1.ast;

public class CaseClause implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private CaseBegin CaseBegin;
    private Integer N2;
    private StatementList StatementList;

    public CaseClause (CaseBegin CaseBegin, Integer N2, StatementList StatementList) {
        this.CaseBegin=CaseBegin;
        if(CaseBegin!=null) CaseBegin.setParent(this);
        this.N2=N2;
        this.StatementList=StatementList;
        if(StatementList!=null) StatementList.setParent(this);
    }

    public CaseBegin getCaseBegin() {
        return CaseBegin;
    }

    public void setCaseBegin(CaseBegin CaseBegin) {
        this.CaseBegin=CaseBegin;
    }

    public Integer getN2() {
        return N2;
    }

    public void setN2(Integer N2) {
        this.N2=N2;
    }

    public StatementList getStatementList() {
        return StatementList;
    }

    public void setStatementList(StatementList StatementList) {
        this.StatementList=StatementList;
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
        if(CaseBegin!=null) CaseBegin.accept(visitor);
        if(StatementList!=null) StatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CaseBegin!=null) CaseBegin.traverseTopDown(visitor);
        if(StatementList!=null) StatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CaseBegin!=null) CaseBegin.traverseBottomUp(visitor);
        if(StatementList!=null) StatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CaseClause(\n");

        if(CaseBegin!=null)
            buffer.append(CaseBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+N2);
        buffer.append("\n");

        if(StatementList!=null)
            buffer.append(StatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CaseClause]");
        return buffer.toString();
    }
}
