/*----------------------------------------------------------------------------------------------------------------*/

package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
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
    
    Map<String, Integer> studentAges = new HashMap<>();

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
	public void visit(Designator_elem designator_elem) {
		Obj arrObj = designator_elem.getDesignatorArrayName().obj;
		if(arrObj == Tab.noObj)
			designator_elem.obj = Tab.noObj;
		else if(!designator_elem.getExpr().struct.equals(Tab.intType)) {
			report_error("Indeksiranje sa ne int vrednosti. [Designator_elem]", designator_elem);
			designator_elem.obj = Tab.noObj;
		} 
		else {
			designator_elem.obj = new Obj(Obj.Elem, arrObj.getName() + "[$]", arrObj.getType().getElemType());
		}
	}
	
	@Override
	public void visit(Designator_arraylength designator_arraylength) {
		Obj arrObj = Tab.find(designator_arraylength.getI1());
		
		if(arrObj == Tab.noObj) {
			report_error("Pristup nedefinisanoj promenljivi niza: " + designator_arraylength.getI1(), designator_arraylength);
			designator_arraylength.obj = Tab.noObj;
		}
		else if(arrObj.getKind() != Obj.Var && arrObj.getType().getKind() != Struct.Array) {
			report_error("Neadekvatna promenljiva niza: " + designator_arraylength.getI1(), designator_arraylength);
			designator_arraylength.obj = Tab.noObj;
		}
		else {
			designator_arraylength.obj = arrObj; // TODO
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
			for(Obj field: enumObj.getType().getMembers())
			{
				if(field.getName().equals(constIdent))
				{
					designator_enumdotident.obj = field;
					return;
				}
			}
			report_error("Enum : " + enumObj.getName() + " nema polje: " + constIdent, designator_enumdotident);
			designator_enumdotident.obj = Tab.noObj;
		}
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
		else
			factorSub_meth.struct = factorSub_meth.getDesignator().obj.getType();
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
		if(left.equals(Tab.intType) && right.equals(Tab.intType))
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
		if(left.equals(Tab.intType) && right.equals(Tab.intType))
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
}
/*----------------------------------------------------------------------------------------------------------------*/

