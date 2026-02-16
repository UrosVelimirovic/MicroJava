// generated with ast extension for cup
// version 0.8
// 16/1/2026 19:30:47


package rs.ac.bg.etf.pp1.ast;

public class FormParsMore_comma extends FormParsMore {

    private FormPars FormPars;
    private FormParsMore FormParsMore;

    public FormParsMore_comma (FormPars FormPars, FormParsMore FormParsMore) {
        this.FormPars=FormPars;
        if(FormPars!=null) FormPars.setParent(this);
        this.FormParsMore=FormParsMore;
        if(FormParsMore!=null) FormParsMore.setParent(this);
    }

    public FormPars getFormPars() {
        return FormPars;
    }

    public void setFormPars(FormPars FormPars) {
        this.FormPars=FormPars;
    }

    public FormParsMore getFormParsMore() {
        return FormParsMore;
    }

    public void setFormParsMore(FormParsMore FormParsMore) {
        this.FormParsMore=FormParsMore;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FormPars!=null) FormPars.accept(visitor);
        if(FormParsMore!=null) FormParsMore.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormPars!=null) FormPars.traverseTopDown(visitor);
        if(FormParsMore!=null) FormParsMore.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormPars!=null) FormPars.traverseBottomUp(visitor);
        if(FormParsMore!=null) FormParsMore.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParsMore_comma(\n");

        if(FormPars!=null)
            buffer.append(FormPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormParsMore!=null)
            buffer.append(FormParsMore.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParsMore_comma]");
        return buffer.toString();
    }
}
