// generated with ast extension for cup
// version 0.8
// 15/1/2026 21:53:56


package rs.ac.bg.etf.pp1.ast;

public class MethodDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private MethodReturnTypeAndName MethodReturnTypeAndName;
    private FormParsList FormParsList;
    private VarDeclListInMethod VarDeclListInMethod;
    private StatementList StatementList;

    public MethodDecl (MethodReturnTypeAndName MethodReturnTypeAndName, FormParsList FormParsList, VarDeclListInMethod VarDeclListInMethod, StatementList StatementList) {
        this.MethodReturnTypeAndName=MethodReturnTypeAndName;
        if(MethodReturnTypeAndName!=null) MethodReturnTypeAndName.setParent(this);
        this.FormParsList=FormParsList;
        if(FormParsList!=null) FormParsList.setParent(this);
        this.VarDeclListInMethod=VarDeclListInMethod;
        if(VarDeclListInMethod!=null) VarDeclListInMethod.setParent(this);
        this.StatementList=StatementList;
        if(StatementList!=null) StatementList.setParent(this);
    }

    public MethodReturnTypeAndName getMethodReturnTypeAndName() {
        return MethodReturnTypeAndName;
    }

    public void setMethodReturnTypeAndName(MethodReturnTypeAndName MethodReturnTypeAndName) {
        this.MethodReturnTypeAndName=MethodReturnTypeAndName;
    }

    public FormParsList getFormParsList() {
        return FormParsList;
    }

    public void setFormParsList(FormParsList FormParsList) {
        this.FormParsList=FormParsList;
    }

    public VarDeclListInMethod getVarDeclListInMethod() {
        return VarDeclListInMethod;
    }

    public void setVarDeclListInMethod(VarDeclListInMethod VarDeclListInMethod) {
        this.VarDeclListInMethod=VarDeclListInMethod;
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
        if(MethodReturnTypeAndName!=null) MethodReturnTypeAndName.accept(visitor);
        if(FormParsList!=null) FormParsList.accept(visitor);
        if(VarDeclListInMethod!=null) VarDeclListInMethod.accept(visitor);
        if(StatementList!=null) StatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodReturnTypeAndName!=null) MethodReturnTypeAndName.traverseTopDown(visitor);
        if(FormParsList!=null) FormParsList.traverseTopDown(visitor);
        if(VarDeclListInMethod!=null) VarDeclListInMethod.traverseTopDown(visitor);
        if(StatementList!=null) StatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodReturnTypeAndName!=null) MethodReturnTypeAndName.traverseBottomUp(visitor);
        if(FormParsList!=null) FormParsList.traverseBottomUp(visitor);
        if(VarDeclListInMethod!=null) VarDeclListInMethod.traverseBottomUp(visitor);
        if(StatementList!=null) StatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDecl(\n");

        if(MethodReturnTypeAndName!=null)
            buffer.append(MethodReturnTypeAndName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormParsList!=null)
            buffer.append(FormParsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclListInMethod!=null)
            buffer.append(VarDeclListInMethod.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementList!=null)
            buffer.append(StatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDecl]");
        return buffer.toString();
    }
}
