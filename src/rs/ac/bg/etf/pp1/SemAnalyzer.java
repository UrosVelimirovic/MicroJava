/*----------------------------------------------------------------------------------------------------------------*/

package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import java.util.HashSet;
import java.util.Set;

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
	
	private Struct currentEnum;
	private int enumAutoValue = 0;
	private String currentEnumName;
    Set<Integer> uniqueValuesInCurrentEnum;

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

	/* ENUM DECLARATIONS */
	
	@Override
	public void visit(EnumDeclList enumDeclList) {
		Tab.chainLocalSymbols(currentEnum);
		Tab.closeScope();
		
		currentEnum = null;
		enumAutoValue = 0;
		currentEnumName = null;
	    uniqueValuesInCurrentEnum.clear();
	}
	
	@Override
	public void visit(EnumName enumName) {
		Obj enumObj = Tab.find(enumName.getI1());
		if(enumObj != Tab.noObj) {
			report_error("Dvostruka definicija nabrajanja: " + enumName.getI1(), enumName);
		}
		else {
			currentEnum = new Struct(Struct.Enum);
			currentEnumName = enumName.getI1();
			if(uniqueValuesInCurrentEnum == null)
				uniqueValuesInCurrentEnum = new HashSet<>();
			
			enumObj = Tab.insert(Obj.Type, enumName.getI1(), currentEnum);
			Tab.openScope();
		}
	}
	
	@Override
	public void visit(EnumDecl_var enumDecl_var) {
		Obj enumConstObj = Tab.currentScope().findSymbol(enumDecl_var.getI1());
		if(enumConstObj != Tab.noObj && enumConstObj != null)
			report_error("Dvostruka definicija konstante: " + enumDecl_var.getI1() + " za nabrajanje: " + currentEnumName, enumDecl_var);
		else if(uniqueValuesInCurrentEnum.contains(enumAutoValue))
			report_error("Konstanta nabrajanja sa vrednoscu " + enumAutoValue + " unutar nabrajanja " + currentEnumName + " vec postoji", enumDecl_var);
		else {
			enumConstObj = Tab.insert(Obj.Con, enumDecl_var.getI1(), Tab.intType);
			enumConstObj.setAdr(enumAutoValue);
			uniqueValuesInCurrentEnum.add(enumAutoValue);
			enumAutoValue++;
		}
	}
	
	@Override
	public void visit(EnumDecl_equal enumDecl_equal) {
		Obj enumConstObj = Tab.currentScope().findSymbol(enumDecl_equal.getI1());
		if(enumConstObj != Tab.noObj && enumConstObj != null)
			report_error("Dvostruka definicija konstante: " + enumDecl_equal.getI1() + " za nabrajanje: " + currentEnumName, enumDecl_equal);
		else if(uniqueValuesInCurrentEnum.contains(enumDecl_equal.getN2()))
			report_error("Konstanta nabrajanja sa vrednoscu " + (enumDecl_equal.getN2()) + " unutar nabrajanja " + currentEnumName + " vec postoji", enumDecl_equal);
		else {
			enumConstObj = Tab.insert(Obj.Con, enumDecl_equal.getI1(), Tab.intType);
			enumConstObj.setAdr(enumDecl_equal.getN2());
			uniqueValuesInCurrentEnum.add(enumDecl_equal.getN2());
			enumAutoValue = enumDecl_equal.getN2();
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
	
	/* FORMPAR DECLARATIONS */
	
	@Override
	public void visit(FormPars_var formPars_var) {
		Obj varObj = null;
		if(currentMethod == null)
			report_error("Semanticka greska. [FormPars_var]", formPars_var);
		else
			varObj = Tab.currentScope().findSymbol(formPars_var.getI2());
		
		if(varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var, formPars_var.getI2(), currentType);
			varObj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);
		}
		else{
			report_error("Dvostruka definicija formalnog parametra: " + formPars_var.getI2(), formPars_var);
		}
	}
	
	@Override
	public void visit(FormPars_array formPars_array) {
		Obj varObj = null;
		if(currentMethod == null)
			report_error("Semanticka greska. [FormPars_array]", formPars_array);
		else
			varObj = Tab.currentScope().findSymbol(formPars_array.getI2());
		
		if(varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var, formPars_array.getI2(), new Struct(Struct.Array, currentType));
			varObj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);
		}
		else{
			report_error("Dvostruka definicija formalnog parametra(niza): " + formPars_array.getI2(), formPars_array);
		}
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