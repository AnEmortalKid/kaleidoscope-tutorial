package com.anemortalkid.lexer;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.anemortalkid.token.Token;

public class Lexer {

	private Character lastChar = ' ';

	private StringBuilder consumed = new StringBuilder();
	public Token getToken() {

		while (lastChar != null && Character.isWhitespace(lastChar)) {
			lastChar = getChar();
		}

		if (lastChar == null) {
			return Token.EOF;
		}

		// tokenize alphanumerics
		if (Character.isJavaIdentifierStart(lastChar)) {

			String identifierStr = "" + lastChar;

			while (isAlphaNumeric(lastChar = getChar())) {
				identifierStr += lastChar;
			}

			switch (identifierStr) {
			case "def":
				return Token.def();
			case "extern":
				return Token.extern();
			default:
				return Token.identifier(identifierStr);
			}
		}

		if (isNumeric(lastChar)) { // Number [0-9.]+
			String numString = "";
			do {
				numString += lastChar;
				lastChar = getChar();
			} while (isNumeric(lastChar));

			return Token.numeric(numString);
		}

		// fastforward comments
		if (lastChar == '#') {

			do {
				lastChar = getChar();
			} while (lastChar != null && lastChar != '\n' && lastChar != '\r');

			if (lastChar != null) {
				return getToken();
			}

		}

		char op = lastChar;
		lastChar = getChar(); // advance
		return Token.unknown(op);
	}

	public static boolean isAlphaNumeric(Character c) {
		if (c == null) {
			return false;
		}
		return Character.isAlphabetic(c) || Character.isDigit(c);
	}

	public static boolean isNumeric(Character c) {
		if (c == null) {
			return false;
		}

		return Character.isDigit(c) || c == '.';
	}

	private StringReader reader;

	private Character getChar() {
		int next = -1;
		try {
			next = reader.read();
			if (next == -1) {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		consumed.append((char) next);
		return (char) next;
	}

	public Lexer(String input) {
		reader = new StringReader(input);
	}
	
	public void printConsumed()
	{
		System.out.println(consumed.toString());
	}

	public static void main(String[] args) {

		Lexer lexer = new Lexer("a+b");
		Token t;
		while ((t = lexer.getToken()) != null && t != Token.EOF) {
			System.out.println(t);
		}
	}
}
