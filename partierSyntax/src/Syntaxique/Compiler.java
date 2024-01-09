package Syntaxique;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.IOException;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.ArrayList;

public abstract class Compiler {
	
	String readycode = "";
	private static int __LINE__ = 0;
	private static ArrayList<String> lines = new ArrayList<String>(0);
	private static ArrayList< ArrayList<String> > vlines = new ArrayList<ArrayList<String>>(0);
	private static boolean hasErrors = false;

	
	private static boolean hasNext() {
		if( __LINE__ >= vlines.size()  ) {
			return false;
		}
		return true;
	}
	private static ArrayList<String> getNextLine() {
		return vlines.get(__LINE__++);
	}
	
	
	private static void compile(ArrayList< ArrayList<String> > lines) {		
		if(Validator.isHeader(lines.get(0))) {
			__LINE__++;
		}else{
			Helper.println(Validator.getError() + " [ln : " + (__LINE__  + 1)+ "]");
			__LINE__++;
			hasErrors = true;
		}
		
		while( Compiler.hasNext() ) {
			if( !Validator.validate(Compiler.getNextLine()) ) {
				Helper.println(Validator.getError() + " [ln : " + (__LINE__  + 1)+ "]");
				hasErrors = true;
			}
		}
		
		if( !Validator.isAllOpensClosed() ) {
			Helper.println(Validator.getError() + " [ln : " + (__LINE__ )+ "]");
			hasErrors = true;
		}
		
		if( !hasErrors ) {
			Helper.print("Bravo !");
		}
	}
	
	private static ArrayList<String> makeCodeReadyToCompile(String code) {
		String[] c  = code.split(" ");
		String[] dels = { ";" , "+" , "=" , "-" , "*" , "/" , ">" , "<" , "!" , ":" };
		ArrayList<String> del = new ArrayList<String>(0);
		
		for( String h : dels ) {
			del.add(h);
		}
		
		ArrayList<String> j = new ArrayList<String>(0);
		
		for( String word : c ) {
			if(word.trim().isEmpty()) {
				continue;
			}
			ArrayList<String> spliten = Helper.splitString(word , dels);
			
			for( String token : spliten ) {
				j.add(token);
			}
			
		}
		
		
		return j;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		
		
		Helper.println("1 - compile code1.txt");
		Helper.println("2 - compile code2.txt");
		Helper.println("3 - compile code3.txt");
		Helper.println("4 - compile code4.txt");
		Helper.println("5 - compile code5.txt");
		
		try (Scanner strReader = new Scanner(System.in)) {
			String str = strReader.nextLine();
			int x = 1;
			String fn = "";
			
			try {
				x = Integer.parseInt(str);
			}catch(NumberFormatException s) {
				s.printStackTrace();
			}
			
			if( !( x < 1 || x > 5)  ) {
				fn = fn + "code" + x + ".txt";
			}else {
				return;
			}
			
			System.out.flush();
			
			try {
				File file = new File(fn);
				try (Scanner reader = new Scanner(file)) {
					while(reader.hasNextLine()) {
						String line = reader.nextLine();
						
						if( !( line.trim().isEmpty() ) ) {
							lines.add(line);
						}else {
							lines.add("");
						}
					}
				}
			}catch (FileNotFoundException e) {
			     System.out.println("An error occurred.");
			      e.printStackTrace();
			}
		}
		if( lines.size() == 0 ) {
			Helper.print("the file is empty file");
			return;
		}
		
		vlines.add(new ArrayList<String>(0));
		int i = 0;
		for (String line : lines) {
			for (String token : makeCodeReadyToCompile(line)) {
				if(token.equals(";")) {
					vlines.add(new ArrayList<String>(0));
					i++;
					continue;
				}
				if( !token.trim().isEmpty() ) {
					vlines.get(i).add(token);
				}
			}
		}
		
		if( vlines.get( vlines.size()-1 ).size() == 0 ) {
			vlines.remove(vlines.size()-1);
		}
		
//		for( ArrayList<String> j : vlines ) {
//			Helper.printArray(j);
//		}
		compile(vlines);
		
		
	}

}
