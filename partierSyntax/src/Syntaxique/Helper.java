package Syntaxique;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Helper {
	public static String expression_error = "";
	public static void print(String s) {
		System.out.print(s);
	}
	public static void println(String s) {
		System.out.println(s);
	}
	
	public static void printArray(ArrayList<String> array) {
		for( String s : array ) {
			print(s);
			print(" ");
		}
		println("");
	}
	
	public static String putChar(String str , char c , int pos ) {
		StringTokenizer n = new StringTokenizer(str , ":=");
		Helper.print(""+n.countTokens());
		return "r";
	}
	
	public static String insertChar(String str , String c , int index) {
		String s = "";
		int i = 0;
		char[] chars = str.toCharArray();
		
		for( char ch : chars ) {
			if(index == i) {
				s = s + c;
			}
			s = s + ch;
			i++;
		}
		
		return s;
	}
	
	public static boolean areIdentifiers(ArrayList<String> tokens) {
		ArrayList<String> valid = new ArrayList<String>(0);
		for( String token : tokens ) {
			String temp = pushSpace(token , ",");
			
			for(String m : temp.split(" ")) {
				if(!m.trim().isEmpty())
					valid.add(m);
			}
		}
		
		
		if(valid.size() == 1) {
			if( isIdentifier(valid.get(0)) ) {
				return true;
			}else {
				Validator.setError("ds");
				return false;
			}
		}
		
		for( int current = 0 ; current < valid.size() ;current++ ) {			
			
			if( current%2 == 0 ) {
				if( !isIdentifier( valid.get(current) ) ) {
					Helper.expression_error = valid.get(current) + " must be a valid identifier";
					return false;
				}
			}else {
				if( !valid.get(current).equals(",") ) {

					Helper.expression_error = "expected ',' ";
					return false;
				}
				
				if( current == 0 || current >= valid.size() - 1 ) {
					Helper.expression_error = "',' can not be in the start or end of identifiers";
					return false;
				}
				
				if( valid.get(current - 1).equals(",") ) {
					Helper.expression_error = "',' must be a seperator between identifiers , can't repeat it";
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static String pushSpace(String word , String sub) {
		String str = word;
		int index = 0;
		while( (index = str.indexOf(sub, index)) != -1 ) {
			if( index != 0 ) {
				str = Helper.insertChar( str , " " , index );
				str = Helper.insertChar( str , " " , index + sub.length() + 1 );
				index = index + sub.length() + 2;
			}else {
				str = Helper.insertChar( str , " " , index + sub.length() );
				index = index + sub.length() + 1;
			}
		}
		
		return str;
	}
	
	public static boolean match(String str , String regExp) {
		Pattern p = Pattern.compile(regExp);
		
		Matcher m = p.matcher(str);
		
		return m.matches();
	}
	
	static public boolean isIdentifier(String str) {
		if( Validator.isKeyWord(str) ) {
			return false;
		}
		
		String regExp = "^([a-zA-Z_$][a-zA-Z\\d_$]*)$";
		return match(str, regExp);
	}
	
	public static boolean isExpression(ArrayList<String> arr) {
		ArrayList<String> air1 = new ArrayList<String>(0);
		ArrayList<String> air2 = new ArrayList<String>(0);
		
		air1.add("+");air1.add("-");air2.add("/");air2.add("*");air2.add(">");air2.add("<");
		
		int before = -1;
		int current = -1;
		int next;
		for( int i = 0 ; i < arr.size()  ; i++ ) {
			current = i;
			next = i + 1;
			if( isValue(arr.get(current)) ) {
				if( before != -1 ) {
					if( isValue( arr.get(before) ) ) {
						Helper.expression_error = "unexpected identifier '"+ arr.get(current) +"' , expected a valid operator";
						return false;
					}
					if(next >= arr.size()) {
						return true;
					}
				}
			}
			
			if( air1.contains(arr.get(current)) ) {
				if( next >= arr.size() ) {
					Helper.expression_error = "unexpected end of expression. " + arr.get(current) + " need identifier in right side";
					return false;
				}
			}
			
			if( air2.contains( arr.get(current) ) ) {
				if( before == -1 ) {
					Helper.expression_error = "expected variable before '" + arr.get(current) + "'";
					return false;
				}else {
					if( air2.contains(arr.get(before)) || !Helper.isValue( arr.get(before) ) ) {
						Helper.expression_error = "expected identifier/value after '" + arr.get(before) +"'";
						return false;
					}
					
					if( next >= arr.size() ) {
						Helper.expression_error = "unexpected end of expression";
						return false;
					}
				}
			}
			
			if( !Helper.isValue( arr.get(current) ) && ( !air1.contains(arr.get(current)) && !air2.contains( arr.get(current) )  ) ) {
				Helper.expression_error = "expressions must include identifiers/values/operators(airethmitic - logical) , '"+arr.get(current) +"' seems not valid";
				return false;
			}
			
			before = i;
		}
		
		return true;
	}
	
	static public boolean isType(String str) {
		return ( str.equals("float") || str.equals("integer") );
	}
	
	static public boolean isValue(String word) {
		return Helper.isIdentifier(word) || Helper.isValidInt(word);
	}
	
	static public boolean isValidInt(String str) {
		String regExp = "\\d+";
		return match(str, regExp);
	}
	
	public static ArrayList<String> splitString(String word , String[] deliminers) {
		ArrayList<String> tokens = new ArrayList<String>(0);
		String temp = word;
		for( String del : deliminers ) {
			temp = Helper.pushSpace(temp , del);
		}
		String[] s = temp.split(" ");
		
		for(String h : s) {
			tokens.add(h);
		}
		
		return tokens;
		
	}
}
