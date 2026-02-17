package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPC;

	public int getmainPc() {
		return this.mainPC;
	}
	
	private void printNewLine() {
		int newLineChar = (int)('\n');
		Code.loadConst(newLineChar);
		Code.loadConst(1);
		Code.put(Code.bprint);
	}
/*----------------------------------------------------------------------------------------------------------------*/

	/* METHOD DECLARATIONS */
	
	@Override
	public void visit(MethodReturnTypeAndName_Type methodReturnTypeAndName_Type) {
		methodReturnTypeAndName_Type.obj.setAdr(Code.pc);
		if(methodReturnTypeAndName_Type.getI2().equalsIgnoreCase("main"))
			this.mainPC = Code.pc;
		
		Code.put(Code.enter);
		Code.put(methodReturnTypeAndName_Type.obj.getLevel()); //b1
		Code.put(methodReturnTypeAndName_Type.obj.getLocalSymbols().size()); //b2
	}
	
	@Override
	public void visit(MethodReturnTypeAndName_void methodReturnTypeAndName_void) {
		methodReturnTypeAndName_void.obj.setAdr(Code.pc);
		if(methodReturnTypeAndName_void.getI1().equalsIgnoreCase("main"))
			this.mainPC = Code.pc;
		
		Code.put(Code.enter);
		Code.put(methodReturnTypeAndName_void.obj.getLevel()); //b1
		Code.put(methodReturnTypeAndName_void.obj.getLocalSymbols().size()); //b2
	}
	
	@Override
	public void visit(MethodDecl methodDecl) {
		if(methodDecl.getMethodReturnTypeAndName().obj.getType() != Tab.noType) { // trap
			Code.put(Code.trap);
			Code.put(0); // poruka greske
		}
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	//Single statements
	
	@Override
	public void visit(SingleStatement_print1 singleStatement_print1) {
		Code.loadConst(0); // width 0
		if(singleStatement_print1.getExpr().struct.equals(Tab.charType))
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
		
		printNewLine();
		
	}
	
	@Override
	public void visit(SingleStatement_print2 singleStatement_print2) {
		Code.loadConst(singleStatement_print2.getN2());
		if(singleStatement_print2.getExpr().struct.equals(Tab.charType))
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
		
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	//Factor
	
	@Override
	public void visit(FactorSub_n factorSub_n) {
		Code.loadConst(factorSub_n.getN1());
	}
	
	@Override
	public void visit(FactorSub_c factorSub_c) {
		Code.loadConst(factorSub_c.getC1());
	}
	
	@Override
	public void visit(FactorSub_b factorSub_b) {
		Code.loadConst(factorSub_b.getB1());
	}

	@Override
	public void visit(FactorSub_var factorSub_var) {
		if(factorSub_var.getDesignator() instanceof Designator_arraylength)
	        return; // skip loading array.length on stack as something.
		Code.load(factorSub_var.getDesignator().obj);
	}
	
	@Override
	public void visit(Factor factor) {
		if(factor.getUnary() instanceof Unary_minus)
			Code.put(Code.neg);
	}
	
	@Override
	public void visit(FactorSub_new_array factorSub_new_array) {
		Code.put(Code.newarray);
		if(factorSub_new_array.getType().struct.equals(Tab.charType))
			Code.put(0);	
		else
			Code.put(1);
	}
	
	@Override
	public void visit(FactorSub_meth factorSub_meth) {
		// mora ovde offset jer call inkrementira pc.
		int offset = factorSub_meth.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	/* EXPR */
	
	@Override
	public void visit(AddopTermList_add addopTermList_add) {
		if(addopTermList_add.getAddop() instanceof Addop_plus)
			Code.put(Code.add);
		else if(addopTermList_add.getAddop() instanceof Addop_minus)
			Code.put(Code.sub);
	}
	
	
	@Override
	public void visit(MulopFactorList_mul mulopFactorList_mul) {
		if(mulopFactorList_mul.getMulop() instanceof Mulop_mul)
			Code.put(Code.mul);
		else if(mulopFactorList_mul.getMulop() instanceof Mulop_div)
			Code.put(Code.div);
		else if(mulopFactorList_mul.getMulop() instanceof Mulop_rem) // remainder(mod).
			Code.put(Code.rem);
	}
	
/*----------------------------------------------------------------------------------------------------------------*/
	
	//Designator
	
	@Override
	public void visit(DesignatorArrayName designatorArrayName) {
		Code.load(designatorArrayName.obj);
	}
	
	@Override 
	public void visit(DesignatorArrayLengthHelper designatorArrayLengthHelper) {
		Code.load(designatorArrayLengthHelper.obj);
	}
	
	@Override
	public void visit(Designator_arraylength designator_arraylength) {
		// addr
		Code.put(Code.arraylength);
		// len
	}
	
	@Override
	public void visit(Designator_elem designator_elem) {
	}
	
/*----------------------------------------------------------------------------------------------------------------*/
	
	//Designator statements
	
	@Override
	public void visit(DesignatorStatement_assign designatorStatement_assign) {
		Code.store(designatorStatement_assign.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_inc designatorStatement_inc) {
		if(designatorStatement_inc.getDesignator().obj.getKind() == Obj.Elem)
			Code.put(Code.dup2);
		else if(designatorStatement_inc.getDesignator().obj.getKind() == Obj.Fld)
			Code.put(Code.dup);
		Code.load(designatorStatement_inc.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(designatorStatement_inc.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_dec designatorStatement_dec) {
		if(designatorStatement_dec.getDesignator().obj.getKind() == Obj.Elem)
			Code.put(Code.dup2);
		else if(designatorStatement_dec.getDesignator().obj.getKind() == Obj.Fld)
			Code.put(Code.dup);
		Code.load(designatorStatement_dec.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(designatorStatement_dec.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_meth designatorStatement_meth) {
		// mora ovde offset jer call inkrementira pc.
		int offset = designatorStatement_meth.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		
		// Ako imamo izraz f(); sam sa sobom, ostavice djubre na steku.
		if(designatorStatement_meth.getDesignator().obj.getType() != Tab.noType)
			Code.put(Code.pop);
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	// Single statements.
	
	@Override
	public void visit(SingleStatement_returnSemiColon singleStatement_returnSemiColon) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(SingleStatement_returnExpr singleStatement_returnExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(SingleStatement_read singleStatement_read) {
		if(singleStatement_read.getDesignator().obj.getType().equals(Tab.charType))
			Code.put(Code.bread);
		else
			Code.put(Code.read);
		Code.store(singleStatement_read.getDesignator().obj);
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

}
/*----------------------------------------------------------------------------------------------------------------*/
