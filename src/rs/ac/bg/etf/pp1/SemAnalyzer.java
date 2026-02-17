/*----------------------------------------------------------------------------------------------------------------*/

package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*----------------------------------------------------------------------------------------------------------------*/

	// HELP

	// report_info pomaze pri debagovanju.
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
    
    private boolean inside_for_loop = false;    
    private int loopCnt = 0;
    
    private boolean inside_case = false;
    private int caseCnt = 0;
	int nVars;
    
   
    private boolean extendedAssignableTo(Struct struct1, Struct struct2) { // We are saying struct2 = struct1
    	if(struct1.assignableTo(struct2))
    		return true;
    	else if(struct2.getKind() == Struct.Enum && struct1.getKind() == Struct.Int)
    		return true;
    	else if(struct2.getKind() == Struct.Int && struct1.getKind() == Struct.Enum)
    		return true;
    	
    	return false;
    }
    
    
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
		nVars = Tab.currentScope().getnVars();
		Tab.chainLocalSymbols(currentProgram);
		Tab.closeScope();
		currentProgram = null;
		
		if(mainMethod == null || mainMethod.getLevel() > 0)
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
			if(extendedAssignableTo(constantType,currentType)) {
				constObj = Tab.insert(Obj.Con, constDecl.getI1(), currentType);
				constObj.setAdr(constant);
				report_info("Deklarisanje simbolicke konstante: " + constObj.getName() + " preko simbolickog cvora " + ObjPrinter.objToString(constObj), constDecl);
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
			report_info("Deklarisanje globalne promenljive: " + varObj.getName() + " preko objektnog cvora ." + ObjPrinter.objToString(varObj), varDecl_var);
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
		methodReturnTypeAndName_void.obj = currentMethod;
		Tab.openScope();
		
		if(methodReturnTypeAndName_void.getI1().equalsIgnoreCase("main"))
			mainMethod = currentMethod;
	}
	
	@Override
	public void visit(MethodReturnTypeAndName_Type methodReturnTypeAndName_Type) {
		currentMethod = Tab.insert(Obj.Meth, methodReturnTypeAndName_Type.getI2(), currentType);
		methodReturnTypeAndName_Type.obj = currentMethod;
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
	
	/* Type */
	
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
		
		type.struct = currentType;
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	
	/* CONTEXT CONDITIONS */

/*----------------------------------------------------------------------------------------------------------------*/

	//Designator
	@Override
	public void visit(Designator_var designator_var) {
		Obj varObj = Tab.find(designator_var.getI1());
		if(varObj == Tab.noObj) {
			report_error("Pristup nedefinisanoj promenljivoj: " + designator_var.getI1(), designator_var);
			designator_var.obj = Tab.noObj;
		}
		else if(varObj.getKind() != Obj.Var && varObj.getKind() != Obj.Con && varObj.getKind() != Obj.Meth) {
			report_error("Neadekvatna promenljiva: " + designator_var.getI1(), designator_var);
			designator_var.obj = Tab.noObj;
		} 
		else {
			designator_var.obj = varObj; 
			
			// report info
			String reportType = null;
			switch(varObj.getKind()) {
			case Obj.Var:
				if(varObj.getLevel() == 0) // globalna
					reportType = "globalna promenljiva";
				else if(varObj.getFpPos() == 1)
					reportType = "formalni argument funkcije";
				else
					reportType = "lokalna promenljiva";
				break;
			case Obj.Con:
				reportType="simbolicka konstanta";
				break;
			}
			
			if(reportType != null)
				report_info("Pristup tipu: (" + reportType + ") preko objektnog cvora " + ObjPrinter.objToString(varObj), designator_var);
		}
	}
	
	@Override
	public void visit(DesignatorArrayName designatorArrayName) {
		Obj arrObj = Tab.find(designatorArrayName.getI1());
		if(arrObj == Tab.noObj) {
			report_error("Pristup nedefinisanoj promenljivi niza: " + designatorArrayName.getI1(), designatorArrayName);
			designatorArrayName.obj = Tab.noObj;
		}
		else if(arrObj.getKind() != Obj.Var && arrObj.getType().getKind() != Struct.Array) {
			report_error("Neadekvatna promenljiva niza: " + designatorArrayName.getI1(), designatorArrayName);
			designatorArrayName.obj = Tab.noObj;
		}
		else {
			designatorArrayName.obj = arrObj;
		}
	}
	
	@Override 
	public void visit(DesignatorArrayLengthHelper designatorArrayLengthHelper) {
		Obj arrObj = Tab.find(designatorArrayLengthHelper.getI1());
		if(arrObj == Tab.noObj) {
			report_error("Pristup nedefinisanoj promenljivi niza: " + designatorArrayLengthHelper.getI1(), designatorArrayLengthHelper);
			designatorArrayLengthHelper.obj = Tab.noObj;
		}
		else if(arrObj.getKind() != Obj.Var && arrObj.getType().getKind() != Struct.Array) {
			report_error("Neadekvatna promenljiva niza: " + designatorArrayLengthHelper.getI1(), designatorArrayLengthHelper);
			designatorArrayLengthHelper.obj = Tab.noObj;
		}
		else {
			designatorArrayLengthHelper.obj = arrObj;
		}
	}
	
	@Override
	public void visit(Designator_arraylength designator_arraylength) {
		Obj arrObj = designator_arraylength.getDesignatorArrayLengthHelper().obj;

		designator_arraylength.obj = new Obj(Obj.Con, "length", Tab.intType);
	}
	
	@Override
	public void visit(Designator_elem designator_elem) {
		Obj arrObj = designator_elem.getDesignatorArrayName().obj;
		if(arrObj == Tab.noObj)
			designator_elem.obj = Tab.noObj;
		else if(!designator_elem.getExpr().struct.equals(Tab.intType)
				&& !(designator_elem.getExpr().struct.getKind() == Struct.Enum)) {
			report_error("Indeksiranje sa ne int vrednosti. [Designator_elem]", designator_elem);
			designator_elem.obj = Tab.noObj;
		} 
		else {
			designator_elem.obj = new Obj(Obj.Elem, arrObj.getName() + "[$]", arrObj.getType().getElemType());
			report_info("Pristup elemenut niza: " + arrObj.getName() + " preko objektnog cvora " + ObjPrinter.objToString(arrObj), designator_elem);
		}
	}
	
	
	@Override
	public void visit(Designator_enumdotident designator_enumdotident) {
		Obj enumObj = Tab.find(designator_enumdotident.getI1());
		
		if(enumObj == Tab.noObj) {
			report_error("Pristup nedefinisanom nabrajanju: " + designator_enumdotident.getI1(), designator_enumdotident);
			designator_enumdotident.obj = Tab.noObj;
		}
		else if(enumObj.getKind() != Obj.Type && enumObj.getType().getKind() != Struct.Enum) {
			report_error("Navedeni identifikator nije Enum: " + designator_enumdotident.getI1(), designator_enumdotident);
			designator_enumdotident.obj = Tab.noObj;
		} else {
			
			String constIdent = designator_enumdotident.getI2();
			for(Obj field: enumObj.getType().getMembers()){
				if(field.getName().equals(constIdent)){
					designator_enumdotident.obj = field;
					return;
				}
			}
			report_error("Enum : " + enumObj.getName() + " nema polje: " + constIdent, designator_enumdotident);
			designator_enumdotident.obj = Tab.noObj;
		}
	}
	
/*----------------------------------------------------------------------------------------------------------------*/
	
	// Designator statements.
	
	@Override
	public void visit(DesignatorStatement_assign designatorStatement_assign) {
		int kind = designatorStatement_assign.getDesignator().obj.getKind();
		if(kind != Obj.Var && kind != Obj.Elem) 
			report_error("Dodela u neadekvatnu promenljivu: " + designatorStatement_assign.getDesignator().obj.getName(), designatorStatement_assign);
		else if(!extendedAssignableTo(designatorStatement_assign.getExpr().struct, designatorStatement_assign.getDesignator().obj.getType()))
			report_error("Neadekvatna dodela vrednosti u promenljivu: " + designatorStatement_assign.getDesignator().obj.getName(), designatorStatement_assign);
	}
	@Override
	public void visit(DesignatorStatement_inc designatorStatement_inc) {
		int kind = designatorStatement_inc.getDesignator().obj.getKind();
		if(kind != Obj.Var && kind != Obj.Elem) 
			report_error("Inkrement neadekvatne promenljive: " + designatorStatement_inc.getDesignator().obj.getName(), designatorStatement_inc);
		else if(!designatorStatement_inc.getDesignator().obj.getType().equals(Tab.intType))
			report_error("Inkrement ne int promenljive: " + designatorStatement_inc.getDesignator().obj.getName(), designatorStatement_inc);
	}
	
	@Override
	public void visit(DesignatorStatement_dec designatorStatement_dec) {
		int kind = designatorStatement_dec.getDesignator().obj.getKind();
		if(kind != Obj.Var && kind != Obj.Elem) 
			report_error("Dekrement neadekvatne promenljive: " + designatorStatement_dec.getDesignator().obj.getName(), designatorStatement_dec);
		else if(!designatorStatement_dec.getDesignator().obj.getType().equals(Tab.intType))
			report_error("Dekrement ne int promenljive: " + designatorStatement_dec.getDesignator().obj.getName(), designatorStatement_dec);
	}
	
	@Override
	public void visit(DesignatorStatement_meth designatorStatement_meth) {
		if(designatorStatement_meth.getDesignator().obj.getKind() != Obj.Meth)
			report_error("Poziv neadekvatne metode: " + designatorStatement_meth.getDesignator().obj.getName(), designatorStatement_meth);
		else {
			List<Struct> fpList = new ArrayList<>();
			for(Obj local: designatorStatement_meth.getDesignator().obj.getLocalSymbols())
				if(local.getKind() == Obj.Var && local.getLevel() == 1 && local.getFpPos() == 1)
					fpList.add(local.getType());
			
			ActParsCounter apc = new ActParsCounter();
			designatorStatement_meth.getActParsList().traverseBottomUp(apc);
			
			List<Struct> apList = apc.getActParsList();
			
			if(fpList.size() != apList.size()
				 && designatorStatement_meth.getDesignator().obj.getLevel() != apList.size()) {
				report_error("Broj parametara pri pozivu metode " + designatorStatement_meth.getDesignator().obj.getName() + " nije odgovarajuc!", designatorStatement_meth);
			} else {
				for (int i = 0; i < fpList.size(); i ++) {
					Struct fps = fpList.get(i);
					Struct aps = apList.get(i);
					if(!extendedAssignableTo(aps,fps)) {
						report_error("Greska pri pozivu metode " 
										+ 
										designatorStatement_meth.getDesignator().obj.getName() 
										+ 
										"pokusaj dodele vrednosti parametra tipa: "
										+ 
										aps.getKind()
										+
										"parametru tipa: "
										+
										fps.getKind()
										,
										designatorStatement_meth);
					}
				}
				// report info
				report_info("Poziv globalne metode: " 
							+ designatorStatement_meth.getDesignator().obj.getName() 
							+ " preko objektnog cvora "
							+ ObjPrinter.objToString(designatorStatement_meth.getDesignator().obj)
							, designatorStatement_meth);
			}
		}
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	// Statements
	@Override
	public void visit(SingleStatement_read singleStatement_read) {
		int kind = singleStatement_read.getDesignator().obj.getKind();
		Struct type = singleStatement_read.getDesignator().obj.getType();
		if(kind != Obj.Var && kind != Obj.Elem)
			report_error("Read operacija neadekvatne promenljive: " + singleStatement_read.getDesignator().obj.getName(), singleStatement_read);
		else if(!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType))
			report_error("Read operacija ne int/char/bool promenljive: " + singleStatement_read.getDesignator().obj.getName(), singleStatement_read);
	}
	
	@Override
	public void visit(SingleStatement_print1 singleStatement_print1) {
		Struct type = singleStatement_print1.getExpr().struct;
		if(!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType))
			report_error("Print operacija ne int/char/bool izraza", singleStatement_print1);
	}
	
	@Override
	public void visit(SingleStatement_print2 singleStatement_print2) {
		Struct type = singleStatement_print2.getExpr().struct;
		if(!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType))
			report_error("Print operacija ne int/char/bool izraza", singleStatement_print2);
	}
	
	@Override
	public void visit(SingleStatement_returnSemiColon singleStatement_returnSemiColon) {
		if (currentMethod == null){
			report_error("Detektovana return naredba van scope-a funkcije! ", singleStatement_returnSemiColon);
		} else if(currentMethod.getType() != Tab.noType) {
			report_error("return naredba nema povratni parametar! ", singleStatement_returnSemiColon);
		} 
	}
	
	@Override
	public void visit(SingleStatement_returnExpr singleStatement_returnExpr) {
		if (currentMethod == null){
			report_error("Detektovana return naredba van scope-a funkcije! ", singleStatement_returnExpr);
		} else if (currentMethod.getType() == Tab.noType) {
			report_error("return naredba sa parametrom ne moze stajati u funkciji sa povratnim tipom void! ", singleStatement_returnExpr);
		
		} else if (!currentMethod.getType().equals(singleStatement_returnExpr.getExpr().struct)) {
			// Edge case, da li je return expression dodeljivo tipu funkcije
			if(extendedAssignableTo(singleStatement_returnExpr.getExpr().struct, currentMethod.getType()))
				return;
			report_error("Parametar return naredbe nije istog tipa kao povratni tip funkcije! ", singleStatement_returnExpr);
		}
	}
	
	@Override
	public void visit(ForNonTerm forNonTerm) {
		inside_for_loop = true;
		loopCnt++;
	}
	
	@Override
	public void visit(SingleStatement_for singleStatement_for) {
		loopCnt--;
		if (loopCnt == 0) {
			inside_for_loop = false;
		}
	}
	
	@Override
	public void visit(SingleStatement_break singleStatement_break) {
		if(!inside_for_loop && !inside_case)
			report_error("Break naredba se ne nalazi unutar tela petlje ili case statement-a.", singleStatement_break);
	}
	
	@Override
	public void visit(SingleStatement_continue singleStatement_continue) {
		if(!inside_for_loop)
			report_error("Continue naredba se ne nalazi unutar tela petlje.", singleStatement_continue);
	}
/*----------------------------------------------------------------------------------------------------------------*/

	//Factor
	@Override
	public void visit(FactorSub_n factorSub_n) {
		factorSub_n.struct = Tab.intType;
	}
	
	@Override
	public void visit(FactorSub_c factorSub_c) {
		factorSub_c.struct = Tab.charType;
	}
	
	@Override
	public void visit(FactorSub_b factorSub_b) {
		factorSub_b.struct = boolType;
	}
	
	@Override
	public void visit(Factor factor) {
		if(factor.getUnary() instanceof Unary_minus) {
			if(factor.getFactorSub().struct.equals(Tab.intType))
				factor.struct = Tab.intType;
			else {
				report_error("Negacija ne-int vrednosti nije dozvoljena", factor);
				factor.struct = Tab.noType;
			}
		}
		else
			factor.struct = factor.getFactorSub().struct;
	}
	
	@Override 
	public void visit(FactorSub_var factorSub_var) {
		factorSub_var.struct = factorSub_var.getDesignator().obj.getType();
	}
	
	@Override
	public void visit(FactorSub_new_array factorSub_new_array) {
		if(!factorSub_new_array.getExpr().struct.equals(Tab.intType)) {
			report_error("Velicina niza nije int tipa.", factorSub_new_array);
			factorSub_new_array.struct = Tab.noType;
		}
		else
			factorSub_new_array.struct = new Struct(Struct.Array, currentType);		
	}
	
	@Override
	public void visit(FactorSub_expr factorSub_expr) {
		factorSub_expr.struct = factorSub_expr.getExpr().struct;
	}
	
	@Override
	public void visit(FactorSub_meth factorSub_meth) {
		if(factorSub_meth.getDesignator().obj.getKind() != Obj.Meth) {
			report_error("Poziv neadekvatne metode: " + factorSub_meth.getDesignator().obj.getName(), factorSub_meth);
			factorSub_meth.struct = Tab.noType;
		}
		else {
			factorSub_meth.struct = factorSub_meth.getDesignator().obj.getType();
			
			List<Struct> fpList = new ArrayList<>();
			for(Obj local: factorSub_meth.getDesignator().obj.getLocalSymbols())
				if(local.getKind() == Obj.Var && local.getLevel() == 1 && local.getFpPos() == 1)
					fpList.add(local.getType());
			
			ActParsCounter apc = new ActParsCounter();
			factorSub_meth.getActParsList().traverseBottomUp(apc);
			
			List<Struct> apList = apc.getActParsList();
			
			if(fpList.size() != apList.size()
				&& factorSub_meth.getDesignator().obj.getLevel() != apList.size()) {
				report_error("Broj parametara pri pozivu metode " + factorSub_meth.getDesignator().obj.getName() + " nije odgovarajuc!", factorSub_meth);
			} else {
				for (int i = 0; i < fpList.size(); i ++) {
					Struct fps = fpList.get(i);
					Struct aps = apList.get(i);
					if(!extendedAssignableTo(aps,fps)) {
						report_error("Greska pri pozivu metode " 
										+ 
										factorSub_meth.getDesignator().obj.getName() 
										+ 
										"pokusaj dodele vrednosti parametra tipa: "
										+ 
										aps.getKind()
										+
										"parametru tipa: "
										+
										fps.getKind()
										,
										factorSub_meth);
					}
				}
				// report info
				report_info("Poziv globalne metode: " 
							+ factorSub_meth.getDesignator().obj.getName() 
							+ " preko objektnog cvora "
							+ ObjPrinter.objToString(factorSub_meth.getDesignator().obj)
							, factorSub_meth);
			}
		}
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	// Expr
	
	@Override
	public void visit(MulopFactorList_factor mulopFactorList_factor) {
		mulopFactorList_factor.struct = mulopFactorList_factor.getFactor().struct;
	}
	
	@Override
	public void visit(MulopFactorList_mul mulopFactorList_mul) {
		Struct left = mulopFactorList_mul.getMulopFactorList().struct;
		Struct right = mulopFactorList_mul.getFactor().struct;
		if(left.equals(Tab.intType) && right.equals(Tab.intType)
				|| left.getKind() == Struct.Enum && right.equals(Tab.intType)
				|| left.equals(Tab.intType) && right.getKind() == Struct.Enum
				|| left.getKind() == Struct.Enum && right.getKind() == Struct.Enum
		)
			mulopFactorList_mul.struct = Tab.intType;
		else {
			report_error("Mulop operacija ne-int vrednosti.", mulopFactorList_mul);
			mulopFactorList_mul.struct = Tab.noType;
		}
	}
	
	
	@Override
	public void visit(Term term) {
		term.struct = term.getMulopFactorList().struct;
	}
	
	@Override
	public void visit(AddopTermList_term addopTermList_term) {
		addopTermList_term.struct = addopTermList_term.getTerm().struct;
	}
	
	@Override
	public void visit(AddopTermList_add addopTermList_add) {
		Struct left = addopTermList_add.getAddopTermList().struct;
		Struct right = addopTermList_add.getTerm().struct;
		if(left.equals(Tab.intType) && right.equals(Tab.intType)
			|| left.getKind() == Struct.Enum && right.equals(Tab.intType)
			|| left.equals(Tab.intType) && right.getKind() == Struct.Enum
			|| left.getKind() == Struct.Enum && right.getKind() == Struct.Enum
		)
			addopTermList_add.struct = Tab.intType;
		else {
			report_error("Addop operacija ne int vrednosti.", addopTermList_add);
			addopTermList_add.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(NonTernaryExpr nonTernaryExpr) {
		nonTernaryExpr.struct = nonTernaryExpr.getAddopTermList().struct;
	}
	
	@Override
	public void visit(TernaryExpr ternaryExpr) { // Vrednost drugog i treceg operatora moraju biti istog tipa.
		Struct left = ternaryExpr.struct = ternaryExpr.getExpr().struct;
		Struct right = ternaryExpr.struct = ternaryExpr.getExpr1().struct;
		
		if(left.equals(right)){
			ternaryExpr.struct = left;
		} else {
			report_error("Evaluacije ternarnog operatora nisu istog tipa!", ternaryExpr);
			ternaryExpr.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(Expr_NonTernary expr_NonTernary) {
		expr_NonTernary.struct = expr_NonTernary.getNonTernaryExpr().struct;
	}
	
	@Override
	public void visit(Expr_Ternary expr_Ternary) {
		expr_Ternary.struct = expr_Ternary.getTernaryExpr().struct;
	}
	
/*----------------------------------------------------------------------------------------------------------------*/

	// Condition.
	@Override
	public void visit(CondFact_expr condFact_expr) {
		if(!condFact_expr.getNonTernaryExpr().struct.equals(boolType)) {
			report_error("Logicki operand nije tipa bool.", condFact_expr);
			condFact_expr.struct = Tab.noType;
		}
		else {
			condFact_expr.struct = boolType;
		}
	}
	
	@Override
	public void visit(CondFact_expr_relop_expr condFact_expr_relop_expr) {
		Struct left = condFact_expr_relop_expr.getNonTernaryExpr().struct;
		Struct right = condFact_expr_relop_expr.getNonTernaryExpr().struct;
		if(left.compatibleWith(right)) {
			if(left.isRefType() || right.isRefType()) {
				if(condFact_expr_relop_expr.getRelop() instanceof Relop_eq || condFact_expr_relop_expr.getRelop() instanceof Relop_ne)
					condFact_expr_relop_expr.struct = boolType;
				else {
					report_error("Poredjenje ref tipova sa ne adekvatnim relacionim operatorom.", condFact_expr_relop_expr);
					condFact_expr_relop_expr.struct = Tab.noType;
				}
			}
			else
				condFact_expr_relop_expr.struct = boolType;
		}
		else {
			report_error("Logicki operandi nisu kompatibilni.", condFact_expr_relop_expr);
			condFact_expr_relop_expr.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(CondFactList_condfact condFactList_condfact) {
		condFactList_condfact.struct = condFactList_condfact.getCondFact().struct;
	}
	
	@Override
	public void visit(CondFactList_and condFactList_and) {
		Struct left = condFactList_and.getCondFactList().struct;
		Struct right = condFactList_and.getCondFact().struct;
		if(left.equals(boolType) && right.equals(boolType))
			condFactList_and.struct = boolType;
		else {
			report_error("AND(&&) operacija nad ne-bool vrednostima nije dozvoljena.", condFactList_and);
			condFactList_and.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(CondTerm condTerm) {
		condTerm.struct = condTerm.getCondFactList().struct;
	}
	
	@Override
	public void visit(CondTermList_ct condTermList_ct) {
		condTermList_ct.struct = condTermList_ct.getCondTerm().struct;
	}
	
	@Override
	public void visit(CondTermList_or condTermList_or) {
		Struct left = condTermList_or.getCondTermList().struct;
		Struct right = condTermList_or.getCondTerm().struct;
		if(left.equals(boolType) && right.equals(boolType))
			condTermList_or.struct = boolType;
		else {
			report_error("OR(||) operacija nad ne-bool vrednostima nije dozvoljena.", condTermList_or);
			condTermList_or.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(Condition condition) {
		condition.struct = condition.getCondTermList().struct;
		if(!condition.struct.equals(boolType))
			report_error("Uslov nije tipa bool.", condition);
	}

/*----------------------------------------------------------------------------------------------------------------*/
	// Case.
	
	@Override
	public void visit(CaseBegin caseBegin) {
		inside_case = true;
		caseCnt++;
	}
	
	@Override
	public void visit(CaseClause caseClause) {
		caseCnt--;
		if (caseCnt == 0) {
			inside_case = false;
		}
	}
	
	@Override
	public void visit(SingleStatement_switch singleStatement_switch) {
		if(!singleStatement_switch.getExpr().struct.equals(Tab.intType)
				&& !(singleStatement_switch.getExpr().struct.getKind() == Struct.Enum)) {
			report_error("Uslov u switch nije tipa int.", singleStatement_switch);
		}
	}
	
/*----------------------------------------------------------------------------------------------------------------*/
}
/*----------------------------------------------------------------------------------------------------------------*/

