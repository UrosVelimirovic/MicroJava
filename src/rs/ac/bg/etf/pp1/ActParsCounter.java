package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class ActParsCounter extends VisitorAdaptor {
	
	private List<Struct> finalActParsList = new ArrayList<>();
	
	private Stack<List<Struct>> actParsLists = new Stack<>();

	@Override
	public void visit(ActParsListBegin actParsListBegin) {
		actParsLists.push(new ArrayList<>());
	}
	
	@Override
	public void visit(ActPars actPars) {
		actParsLists.peek().add(actPars.getExpr().struct);
	}
	
	@Override
	public void visit(ActParsList_rec actParsList_rec) {
		finalActParsList = actParsLists.pop();
	}
	
	@Override
	public void visit(ActParsList_epsilon actParsList_epsilon) {
		finalActParsList = actParsLists.pop();
	}
	
	public List<Struct> getActParsList() {
		return finalActParsList;
	}
}
