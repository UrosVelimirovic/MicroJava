// generated with ast extension for cup
// version 0.8
// 3/1/2026 19:47:20


package rs.ac.bg.etf.pp1.ast;

public class CaseList_recursive extends CaseList {

    private CaseList CaseList;
    private CaseClause CaseClause;

    public CaseList_recursive (CaseList CaseList, CaseClause CaseClause) {
        this.CaseList=CaseList;
        if(CaseList!=null) CaseList.setParent(this);
        this.CaseClause=CaseClause;
        if(CaseClause!=null) CaseClause.setParent(this);
    }

    public CaseList getCaseList() {
        return CaseList;
    }

    public void setCaseList(CaseList CaseList) {
        this.CaseList=CaseList;
    }

    public CaseClause getCaseClause() {
        return CaseClause;
    }

    public void setCaseClause(CaseClause CaseClause) {
        this.CaseClause=CaseClause;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CaseList!=null) CaseList.accept(visitor);
        if(CaseClause!=null) CaseClause.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CaseList!=null) CaseList.traverseTopDown(visitor);
        if(CaseClause!=null) CaseClause.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CaseList!=null) CaseList.traverseBottomUp(visitor);
        if(CaseClause!=null) CaseClause.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CaseList_recursive(\n");

        if(CaseList!=null)
            buffer.append(CaseList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(CaseClause!=null)
            buffer.append(CaseClause.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CaseList_recursive]");
        return buffer.toString();
    }
}
