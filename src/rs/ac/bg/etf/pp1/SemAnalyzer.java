/*----------------------------------------------------------------------------------------------------------------*/

package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

/*----------------------------------------------------------------------------------------------------------------*/

public class SemAnalyzer extends VisitorAdaptor {
	
	private boolean errorDetected = false;
	Logger log = Logger.getLogger(getClass());
	private Obj currentProgram;
	private Obj mainMethod;
	private Struct currentType;
	private int constant;
	private Struct constantType;
	private Struct boolType = Tab.find("bool").getType();
	private Obj currentMethod;

/*----------------------------------------------------------------------------------------------------------------*/

	/* LOG MESSAGES */
	public void report_error(String message, SyntaxNode info) {
		errorDetected  = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	public boolean passed() {
		return !errorDetected;
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	/* SEMANTIC PASS CODE */

	@Override
	public void visit(ProgramName programName) {
		currentProgram = Tab.insert(Obj.Prog, programName.getI1(), Tab.noType);
		Tab.openScope();
	}
	
	@Override
	public void visit(Program program) {
		Tab.chainLocalSymbols(currentProgram);
		Tab.closeScope();
		currentProgram = null;
		
		if(mainMethod == null) // || mainMethod.getLevel() > 0)
			report_error("Program nema adekvatnu main metodu", program);
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	/* CONST DECLARATIONS */
	
	@Override
	public void visit(ConstDecl constDecl) {
		Obj constObj = Tab.find(constDecl.getI1());
		if(constObj != Tab.noObj) {
			report_error("Dvostruka definicija konstante: " + constDecl.getI1(), constDecl);
		}
		else {
			if(constantType.assignableTo(currentType)) {
				constObj = Tab.insert(Obj.Con, constDecl.getI1(), currentType);
				constObj.setAdr(constant);
			}
			else {
				report_error("Neadekvatna dodela konstanti: " + constDecl.getI1(), constDecl);
			}
		}
	}
	
	@Override
	public void visit(Constant_number constant_number) {
		constant = constant_number.getN1();
		constantType = Tab.intType;
	}
	
	@Override
	public void visit(Constant_character constant_character) {
		constant = constant_character.getC1();
		constantType = Tab.charType;
	}
	
	@Override
	public void visit(Constant_bool constant_bool) {
		constant = constant_bool.getB1();
		constantType = boolType;
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	/* VAR DECLARATIONS */
	
	@Override
	public void visit(VarDecl_var varDecl_var) {
		Obj varObj = null;
		if(currentMethod == null)
			varObj = Tab.find(varDecl_var.getI1());
		else
			varObj = Tab.currentScope().findSymbol(varDecl_var.getI1());
		
		if(varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var, varDecl_var.getI1(), currentType);
		}
		else {
			report_error("Dvostruka definicija promenljive: " + varDecl_var.getI1(), varDecl_var);
		}
	}
	
	@Override
	public void visit(VarDecl_array varDecl_array) {
		Obj arrayObj = null;
		if(currentMethod == null)
			arrayObj = Tab.find(varDecl_array.getI1());
		else
			arrayObj = Tab.currentScope().findSymbol(varDecl_array.getI1());
		
		if(arrayObj == null || arrayObj == Tab.noObj) {
			arrayObj = Tab.insert(Obj.Var, varDecl_array.getI1(), new Struct(Struct.Array, currentType));
		}
		else {
			report_error("Dvostruka definicija niza: " + varDecl_array.getI1(), varDecl_array);
		}
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	/* METHOD DECLARATIONS */
	
	@Override
	public void visit(MethodReturnTypeAndName_void methodReturnTypeAndName_void) {
		currentMethod = Tab.insert(Obj.Meth, methodReturnTypeAndName_void.getI1(), Tab.noType);
		Tab.openScope();
		
		if(methodReturnTypeAndName_void.getI1().equalsIgnoreCase("main"))
			mainMethod = currentMethod;
	}
	
	@Override
	public void visit(MethodReturnTypeAndName_Type methodReturnTypeAndName_Type) {
		currentMethod = Tab.insert(Obj.Meth, methodReturnTypeAndName_Type.getI2(), currentType);
		Tab.openScope();
	}
	
	@Override
	public void visit(MethodDecl methodDecl) {
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		currentMethod = null;
	}
	
/*----------------------------------------------------------------------------------------------------------------*/
	
	@Override
	public void visit(Type type) {
		Obj typeObj = Tab.find(type.getI1());
		if(typeObj == Tab.noObj) {
			report_error("Nepostojeci tip podatka: " + type.getI1(), type);
			currentType = Tab.noType;
		}
		else if(typeObj.getKind() != Obj.Type) {
			report_error("Neadekvatan tip podatka: " + type.getI1(), type);
			currentType = Tab.noType;
		}
		else
			currentType = typeObj.getType();
	}
}
/*----------------------------------------------------------------------------------------------------------------*/