package com.anemortalkid.driver;

import com.anemortalkid.lexer.Lexer;
import com.anemortalkid.parser.Parser;

public class Driver {

	
	public static void main(String[] args) {
		Lexer lexer = new Lexer("extern sin(a); def fib(x y) x+y*z");
		Parser parsey = new Parser(lexer);
		parsey.parse();
		
	}
}
