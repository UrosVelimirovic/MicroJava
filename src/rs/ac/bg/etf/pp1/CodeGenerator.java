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
		Code.load(factorSub_var.getDesignator().obj);
	}
	
	@Override
	public void visit(Factor factor) {
		if(factor.getUnary() instanceof Unary_minus)
			Code.put(Code.neg);
	}
	
	@Override
	public void visit(FactorSub_new_array factorSub_new_array) {
		
		// size
		Code.put(Code.dup); 
		// size, size
		// Add 1 na velicinu niza da bi imali prostor za duzinu niza.
		Code.put(Code.const_1); 
		// size, size , 1
		Code.put(Code.add); 
		//size, size + 1
		
		Code.put(Code.newarray);
		if(factorSub_new_array.getType().struct.equals(Tab.charType))
			Code.put(0);	
		else
			Code.put(1);
		
		// size, adr
		Code.put(Code.dup_x1); 
		// adr, size, adr
		Code.put(Code.dup_x2); 
		// adr, adr, size, adr
		Code.put(Code.pop); 
		// adr, adr, size
		Code.loadConst(0); 
		// adr, adr, size, 0
		Code.put(Code.dup_x1); 
		// adr, adr, 0, size, 0
		Code.put(Code.pop); 
		// adr, adr, 0, size
		Code.put(Code.astore); 
		// adr
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
	public void visit(Designator_elem designator_elem) {
		// imamo expr na vrhu steka
		
		// Index
		Code.loadConst(1);
		// Index, 1
		Code.put(Code.add);
		// Index + 1
	}
	
/*----------------------------------------------------------------------------------------------------------------*/
	
	//Designator statements
	
	@Override
	public void visit(DesignatorStatement_assign designatorStatement_assign) {
		// astore ocekuje adr, index, val 
		// znaci index moramo plus plus
//		if(designatorStatement_assign.getDesignator().obj.getKind() == Obj.Elem) {
//			// adr, index, val 
//			Code.put(Code.dup_x1);
//			// adr, val, index, val
//			Code.put(Code.pop);
//			// adr, val, index
//			Code.loadConst(1);
//			// adr, val, index, 1
//			Code.put(Code.add);
//			// adr, val, index + 1
//			Code.put(Code.dup_x1);
//			// adr, index + 1, val, index + 1
//			Code.put(Code.pop);
//			// adr, index + 1, val
//		}
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
