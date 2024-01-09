package Syntaxique;

import java.util.*;


public final class Validator {

	private static Core CoreLanguage = new Core();
	private static String error = " s";
	private static ArrayList<String> opens = new ArrayList<String>(0);// le problem de begin/end will be solved used map
																		// (begin => position)( end => position )
	private static ArrayList<String> closes = new ArrayList<String>(0);// le problem de begin/
	private static boolean varPart = false;

	public static boolean isHeader(ArrayList<String> header) {
		return matchLine(header, CoreLanguage.getHeader());
	}

	public static String getError() {
		return error;
	}

	private static boolean matchLine(ArrayList<String> line, ArrayList<String> rules) {
		int i = 0;
		for (String rule : rules) {
			if (i >= line.size()) {
				error = "expected " + rule + " but ';' found ";
				return false;
			}
			if (rule.equals("expression")) {
				boolean ifs = false;
				for (int j = 0; j < i; j++) {
					line.remove(0);
				}

				ArrayList<String> nline = new ArrayList<String>(0);

				for (int i1 = 0; i1 < line.size(); i1++) {
					if (line.get(i1).equals("then")) {
						ifs = true;
						break;
					}
					nline.add(line.get(i1));
				}

				if (!Helper.isExpression(nline)) {
					error = "invalid expression :" + Helper.expression_error + ( (ifs)?" in 'if' statement" : "" );
					return false;
				} else {
					return true;
				}
			}

			if (rule.equals("identifiers")) {
				int f = 0;
				while (f < line.size()) {
					if( line.get(f).equals(":") ) {
						break;
					}
					f++;
				}

				if (f >= line.size()) {
					error = "expected ':', unexpected end of line";
					return false;
				}

				ArrayList<String> nline = new ArrayList<String>(0);

				for (int j = i; j < f; j++) {
					nline.add(line.get(j));
				}
				
				for(int j = 0 ; j < i ; j++) {
					line.remove(0);
				}
				if (!Helper.areIdentifiers(nline)) {
					Validator.error ="error:"+ Helper.expression_error;
					return false;
				}
				
				for(int j = 0 ; j < nline.size();j++) {
					line.remove(0);
				}
				

				i = 0;
				continue;
			}

			if (!matchRule(line.get(i), rule)) {
				error = "invalid word " + line.get(i) + "=> must be a valid '" + rule + "'";
				return false;
			}
			i++;
		}

		if (line.get(0).equals("if")) {
			Helper.println("dddd");
			return false;
		}

		if (i < line.size()) {
			error = "unexpected '" + line.get(i) + "' expected end of line or new line (if begin or end)";
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public static boolean validate(ArrayList<String> line) {
		// function that accept the line as an array of tokens (string) as an parameter
		// if the line start with Const (means programmer will open a constant)
		if ("Const".equals(line.get(0))) {
			// constants must be before any "begin" word
			// and must be before the variables part
			if (opens.size() > 0 || varPart == true) {
				error = "constants must declared just after program name";
				return false;
			}

			// let's get the statement structure of constant declaration
			ArrayList<String> rule = CoreLanguage.getLine("Const");

			// and match it with our line to see if is compatible
			return Validator.matchLine(line, rule);
		}

		// what if the line is begining wit Var keyword ?
		// programmer want to declare a variable
		if ("Var".equals(line.get(0))) {
			// like constant declaration variable declaration must be before any "begin"
			// keyword
			if (opens.size() > 0) {
				error = "variables must declare just after constants/program";
				return false;
			}

			// getting the rule
			ArrayList<String> rule = CoreLanguage.getLine("Var");
			varPart = true;

			// match with it (for more detailed comment back to constant)
			return Validator.matchLine(line, rule);
		}

		// if line start with begin
		if (line.get(0).equals("begin")) {
			// it's mean programmer will tap another statements after it so remove it and
			// start to check after it
			ArrayList<String> newline = (ArrayList<String>) line.clone();
			// don't forget to add it to our opens
			opens.add("begin");

			newline.remove(0);
			// validate the new line
			return Validator.validate(newline);
		}
		// what if we found end
		if (line.get(0).equals("end")) {
			ArrayList<String> rule = CoreLanguage.getLine("end");
			closes.add("end");

			return Validator.matchLine(line, rule);
		}
		// if it's an if statement
		if (line.get(0).equals("if")) {
			//if must be after begin
			if (opens.size() == 0) {
				error = "statemnts must be under begin tag";
				return false;
			}
			//get if rules
			ArrayList<String> rules = CoreLanguage.getLine("if");
			//
			if (Validator.matchLine(line, rules)) {
				ArrayList<String> newline = (ArrayList<String>) line.clone();

				for (int i = 0; i < line.size() ; i++) {
					if( line.get(i).equals("then") ) {
						break;
					}
					newline.remove(0);
				}
				if(newline.isEmpty()) {
					error = "expected 'then' after if statement";
					return false;
				}
				newline.remove(0);

				if (newline.size() == 0) {
					error = "expected a statement after if statemment";
					return false;
				}
				return Validator.validate(newline);
			} else {
				return false;
			}
		}

//		ArrayList<ArrayList<String>> rules = CoreLanguage.getLines("expression");
//		
//		for( ArrayList<String> rule : rules ) {
//			if( Validator.matchLine(line, rule) ) {
//				return true;
//			}
//		}

		if (Helper.isIdentifier(line.get(0))) {
			if (line.size() == 1) {
				return true;
			}
			if (line.get(1).equals("=")) {
				if (opens.size() == 0) {
					error = "statemnts must be under begin tag";
					return false;
				}
				ArrayList<String> rule = CoreLanguage.getLine("identifier");
				if (Validator.matchLine(line, rule)) {
					return true;
				}else {
					return false;
				}
			}
			
			if( !Helper.isExpression(line) ) {
				error = "invalid expression : " + Helper.expression_error;
				return false;
			}
		}
		
		
		if (Helper.isIdentifier(line.get(0))) {
			if (opens.size() == 0) {
				error = "statemnts must be under begin tag";
				return false;
			}
			ArrayList<String> rule = CoreLanguage.getLine("expression");
			if (Validator.matchLine(line, rule)) {
				return true;
			}
		}

		return true;
	}

	/**
	 * @param String word to match the rule with
	 */
	private static boolean matchRule(String word, String rule) {
		if (Validator.isKeyWord(rule)) {
			return rule.equals(word);
		}

		if (CoreLanguage.getOperator("op").contains(rule)) {
			if (word.equals(rule)) {
				return true;
			} else {
				return false;
			}
		}

		if (rule.equals("airOp")) {
			if (CoreLanguage.getOperator("airethmitic").contains(word)) {
				return true;
			} else {
				return false;
			}
		}

		if (rule.equals("logOp")) {
			if (CoreLanguage.getOperator("logical").contains(word)) {
				return true;
			} else {
				return false;
			}
		}

		if (rule.equals("=")) {
			if (word.equals("=")) {
				return true;
			} else {
				return false;
			}
		}

		switch (rule) {
		case "identifier":
			return Helper.isIdentifier(word);
		case "value":
			return Helper.isValue(word);
		case "number":
			return Helper.isValidInt(word);
		case "type":
			return Helper.isType(word);
		default:
			return false;
		}
	}

	public static boolean isAllOpensClosed() {
		if (opens.size() > closes.size()) {
			error = "unexpected end of file, there is an open tags does not closed";
			return false;
		} else if (opens.size() < closes.size()) {
			error = "unexpected end tag, all open tags are already closed";
			return false;
		} else {
			return true;
		}
	}

	public static boolean isKeyWord(String str) {
		if (Validator.CoreLanguage.haveKeyWord(str)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void setError(String s) {
		error = s;
	}

}
