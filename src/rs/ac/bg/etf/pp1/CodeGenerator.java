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

	//Condition 
	
	private int returnRelOp(Relop relop) {
		if(relop instanceof Relop_eq)
			return Code.eq;
		else if(relop instanceof Relop_ne)
			return Code.ne;
		else if(relop instanceof Relop_lt)
			return Code.lt;
		else if(relop instanceof Relop_le)
			return Code.le;
		else if(relop instanceof Relop_gt)
			return Code.gt;
		else if(relop instanceof Relop_ge)
			return Code.ge;
		else
			return -1; // greska
	}
	
	private Stack<Integer> skipCondFact = new Stack<>();
	private Stack<Integer> skipCondition = new Stack<>();
	private Stack<Integer> skipThen = new Stack<>();
	private Stack<Integer> skipElse = new Stack<>();

	@Override
	public void visit(CondFact_expr condFact_expr) {
		Code.loadConst(0); // ukoliko je jednako sa nulom, to je false za ceo if i onda skace
		// znaci false jump ako nije ispunjeno not equal sto znaci ispunjeno equal onda skaci.
		Code.putFalseJump(Code.ne, 0); //netacna
		skipCondFact.push(Code.pc - 2);
		//tacna
	}
	
	@Override
	public void visit(CondFact_expr_relop_expr condFact_expr_relop_expr) {
		Code.putFalseJump(returnRelOp(condFact_expr_relop_expr.getRelop()), 0); //netacna
		skipCondFact.push(Code.pc - 2);
		//tacna
	}
	
	@Override
	public void visit(CondTerm condTerm) {
		Code.putJump(0); //tacne bacamo na THEN
		skipCondition.push(Code.pc - 2);
		//ovde vracam netacne
		while(!skipCondFact.empty())
			Code.fixup(skipCondFact.pop());
		//netacne
	}
	
	@Override
	public void visit(Condition condition) {
		//netcni
		Code.putJump(0); //netacne bacamo na ELSE jer je ovo poslednja grana or
		skipThen.push(Code.pc - 2);
		//THEN
		while(!skipCondition.empty())
			Code.fixup(skipCondition.pop());
		//tacne
	}
	
	@Override
	public void visit(ElseStatement_no elseStatement_no) {
		//tacne
		Code.fixup(skipThen.pop());
		//tacne + netacne
	}
	
	@Override
	public void visit(Else else_) {
		//tacne
		Code.putJump(0); //tacne bacamo na kraj ELSE
		skipElse.push(Code.pc - 2);
		Code.fixup(skipThen.pop());
		//netacne
	}
	
	@Override
	public void visit(ElseStatement_yes elseStatement_yes) {
		//netcane
		Code.fixup(skipElse.pop()); //varacamo tacne koji su preskocili ELSE
		//netacne + tacne
	}
	
/*----------------------------------------------------------------------------------------------------------------*/
	
	// For petlja
	
	private Stack<Integer> forCondStart = new Stack<>();
	private Stack<Integer> forStepStart = new Stack<>();
	private Stack<Integer> forBodyJump = new Stack<>();
	private Stack<Boolean> forHasStep = new Stack<>();
	private Stack<Boolean> forHasCondition = new Stack<>();
	private Stack<List<Integer>> forBreakJumps = new Stack<>();
	private Stack<List<Integer>> forContinueJumps = new Stack<>();
	private Stack<String> breakTargets = new Stack<>();
	
	// Switch statement
	private Stack<SingleStatement_switch> switchOwners = new Stack<>();
	private Stack<List<Integer>> switchBreakJumps = new Stack<>();
	private Stack<Integer> switchPendingFalseJump = new Stack<>();
	private Stack<Integer> switchPendingFallthroughJump = new Stack<>();

	private SingleStatement_switch findEnclosingSwitch(SyntaxNode node) {
		SyntaxNode current = node;
		while (current != null && !(current instanceof SingleStatement_switch)) {
			current = current.getParent();
		}
		return (SingleStatement_switch) current;
	}

	
	@Override
	public void visit(ForInit_DesignatorStatement forInit_DesignatorStatement) {
	    // After init executes, we mark where condition checking begins
	    forCondStart.push(Code.pc);
	}
	
	@Override
	public void visit(ForInit_epsilon forInit_epsilon) {
	    // No init, just mark where condition checking begins
	    forCondStart.push(Code.pc);
	}
	
	@Override
	public void visit(ForCondition_Condition forCondition_Condition) {
	    // Condition was evaluated
	    // skipThen has the address to fixup for false jump to exit
	    // Now we need to jump over the step code to get to the body
	    Code.putJump(0);
	    forBodyJump.push(Code.pc - 2);
	    
	    // Mark where step code will be generated
	    forStepStart.push(Code.pc);
	    
	    // Mark that this for loop has a condition
	    forHasCondition.push(true);
	}
	
	@Override
	public void visit(ForCondition_epsilon forCondition_epsilon) {
	    // No condition, jump directly over step to body
	    Code.putJump(0);
	    forBodyJump.push(Code.pc - 2);
	    
	    // Mark where step code will be generated
	    forStepStart.push(Code.pc);
	    
	    // Mark that this for loop has no condition
	    forHasCondition.push(false);
	}
	
	@Override
	public void visit(ForStep_DesignatorStatement forStep_DesignatorStatement) {
	    // Step code has been generated
	    // Now jump back to condition
	    Code.putJump(forCondStart.peek());
	    
	    // Fix up the jump to body - it should land here (after step code)
	    Code.fixup(forBodyJump.pop());
	    
	    // Mark that this for loop has a step
	    forHasStep.push(true);
	}
	
	@Override
	public void visit(ForStep_epsilon forStep_epsilon) {
	    // No step code, fix up jump to body to land here
	    Code.fixup(forBodyJump.pop());
	    
	    // Mark that this for loop has no step
	    forHasStep.push(false);
	}
	
	@Override
	public void visit(ForNonTerm forNonTerm) {
	    // Initialize break and continue jumps lists for this for loop
	    forBreakJumps.push(new ArrayList<>());
	    forContinueJumps.push(new ArrayList<>());
	    breakTargets.push("for");
	}
	
	@Override 
	public void visit(SingleStatement_for singleStatement_for)
	{
	    // Body has finished executing
	    boolean hasStep = forHasStep.pop();
	    boolean hasCondition = forHasCondition.pop();
	    
	    if (hasStep) {
	        // Fixup all continue jumps to point to step
	        List<Integer> continues = forContinueJumps.pop();
	        for (Integer jumpAddr : continues) {
	            Code.fixup(jumpAddr);
	        }
	        
	        // Jump back to step (which then jumps to condition)
	        Code.putJump(forStepStart.pop());
	    } else {
	        // Fixup all continue jumps to point to condition
	        List<Integer> continues = forContinueJumps.pop();
	        for (Integer jumpAddr : continues) {
	            Code.fixup(jumpAddr);
	        }
	        
	        // Jump back to condition directly
	        Code.putJump(forCondStart.peek());
	        forStepStart.pop(); // Pop but don't use
	    }
	    
	    // Fix up the condition's false jump to point here (exit point)
	    // This happens if there was a condition
	    if (hasCondition && !skipThen.empty()) {
	        Code.fixup(skipThen.pop());
	    }
	    
	    // Fixup all break jumps to point here (end of for loop)
	    List<Integer> breaks = forBreakJumps.pop();
	    for (Integer jumpAddr : breaks) {
	        Code.fixup(jumpAddr);
	    }
	    
	    // Now we're done with this for loop, pop the condition start
	    forCondStart.pop();
	    if (!breakTargets.isEmpty() && "for".equals(breakTargets.peek())) {
	    	breakTargets.pop();
	    }
	}
	
	@Override
	public void visit(SingleStatement_break singleStatement_break) {
	    // Break from the innermost loop or switch
	    Code.putJump(0);
	    int jumpAddr = Code.pc - 2;
	    if (!breakTargets.isEmpty() && "switch".equals(breakTargets.peek())) {
	    	if (!switchBreakJumps.isEmpty()) {
	    		switchBreakJumps.peek().add(jumpAddr);
	    	}
	    } else if (!forBreakJumps.isEmpty()) {
	    	forBreakJumps.peek().add(jumpAddr);
	    }
	}
	
	@Override
	public void visit(SingleStatement_continue singleStatement_continue) {
	    // Continue to the next iteration of the innermost for loop
	    if (!forContinueJumps.isEmpty()) {
	        Code.putJump(0);
	        forContinueJumps.peek().add(Code.pc - 2);
	    }
	}

/*----------------------------------------------------------------------------------------------------------------*/

	// Switch statement

	@Override
	public void visit(CaseBegin caseBegin) {
		SingleStatement_switch owner = findEnclosingSwitch(caseBegin);
		if (owner != null && (switchOwners.isEmpty() || switchOwners.peek() != owner)) {
			switchOwners.push(owner);
			switchBreakJumps.push(new ArrayList<>());
			switchPendingFalseJump.push(-1);
			switchPendingFallthroughJump.push(-1);
			breakTargets.push("switch");
		}
		
		if (!switchPendingFalseJump.isEmpty()) {
			int pendingFalse = switchPendingFalseJump.peek();
			if (pendingFalse != -1) {
				Code.fixup(pendingFalse);
				switchPendingFalseJump.pop();
				switchPendingFalseJump.push(-1);
			}
		}
		
		CaseClause caseClause = (CaseClause) caseBegin.getParent();
		int caseValue = caseClause.getN2();
		Code.put(Code.dup);
		Code.loadConst(caseValue);
		Code.putFalseJump(Code.eq, 0);
		switchPendingFalseJump.pop();
		switchPendingFalseJump.push(Code.pc - 2);
		
		if (!switchPendingFallthroughJump.isEmpty()) {
			int pendingFall = switchPendingFallthroughJump.peek();
			if (pendingFall != -1) {
				Code.fixup(pendingFall);
				switchPendingFallthroughJump.pop();
				switchPendingFallthroughJump.push(-1);
			}
		}
	}
	
	@Override
	public void visit(CaseClause caseClause) {
		if (!switchPendingFallthroughJump.isEmpty()) {
			Code.putJump(0);
			switchPendingFallthroughJump.pop();
			switchPendingFallthroughJump.push(Code.pc - 2);
		}
	}
	
	@Override
	public void visit(SingleStatement_switch singleStatement_switch) {
		if (!switchOwners.isEmpty() && switchOwners.peek() == singleStatement_switch) {
			int pendingFalse = switchPendingFalseJump.pop();
			if (pendingFalse != -1) {
				Code.fixup(pendingFalse);
			}
			int pendingFall = switchPendingFallthroughJump.pop();
			if (pendingFall != -1) {
				Code.fixup(pendingFall);
			}
			List<Integer> breaks = switchBreakJumps.pop();
			for (Integer jumpAddr : breaks) {
				Code.fixup(jumpAddr);
			}
			switchOwners.pop();
			if (!breakTargets.isEmpty() && "switch".equals(breakTargets.peek())) {
				breakTargets.pop();
			}
		}
		Code.put(Code.pop);
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

}
/*----------------------------------------------------------------------------------------------------------------*/
