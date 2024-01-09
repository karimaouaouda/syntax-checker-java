package Syntaxique;

import java.util.*;

public final class Core {
	private ArrayList<String> keywords = new ArrayList<String>(0);
	private ArrayList< ArrayList<String> > lines = new ArrayList< ArrayList<String> >(0);
	private ArrayList<String> airethmiticOperations = new ArrayList<String>(0);
	private ArrayList<String> logicalOperations = new ArrayList<String>(0);
	private ArrayList<String> Operators = new ArrayList<String>(0);
	
	public Core() {
		String[] kw = { "Var" , "integer" , "float" , "Const" , "if" , "then" , "begin" , "end" , "program" };
		String[] airOp = { "+" , "*" , "-" , "/" };
		String[] logOp = { "==" , ">" , "<" , "!=" , "<=" , ">="  };
		String[] ope = {":" , "="};
		
		for( String o : ope ) {
			Operators.add(o);
		}
		
		for(String op : airOp) {
			this.airethmiticOperations.add(op);
		}
		
		for(String op : logOp) {
			this.logicalOperations.add(op);
		}
		
		for(String keyword:kw) {
			keywords.add(keyword);
		}
		
		String[][] ls = {
				{"program" , "identifier"},
				{ "Var" , "identifiers" , ":" , "type" },
				{"Const" , "identifier" , "=" , "number"},
				{ "if" , "expression" , "then"  },
				{"expression"},
				{"identifier" , "=" , "expression"},
				{"begin"},
				{"end"}
		};
		
		for(int i = 0 ; i < ls.length ; i++) {
			this.lines.add( new ArrayList<String>(0) );
			for(int j = 0 ; j < ls[i].length ; j++) {
				this.lines.get(i).add(ls[i][j]);
			}
		}
		
	}
	
	public ArrayList<String> getHeader() {
		return lines.get(0);
	}
	
	public boolean haveKeyWord(String str) {
		if( this.keywords.contains(str) ) {
			return true;
		}else {
			return false;
		}
	}
	
	public ArrayList<String> getOperator(String type){
		if( type.equals("logical") ) {
			return this.logicalOperations;
		}else if( type.equals("airethmitic") ) {
			return this.airethmiticOperations;
		}else {
			return this.Operators;
		}
	}
	
	public ArrayList<String> getLine(String start) {
		for(ArrayList<String> line : this.lines) {
			if(line.get(0) == start) {
				return line;
			}
		}
		
		return null;
	}
	
	public ArrayList<ArrayList<String>> getLines(String...args) {
		ArrayList<ArrayList<String>> rules = new ArrayList<ArrayList<String>>(0);
		for(ArrayList<String> line : this.lines) {
			for( String arg : args ) {
				if(line.get(0) == arg) {
					rules.add(line);
				}
			}
		}
		
		return rules;
	}
}
