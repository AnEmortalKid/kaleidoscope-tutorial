package com.anemortalkid.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.anemortalkid.ast.BinaryExprAST;
import com.anemortalkid.ast.CallAST;
import com.anemortalkid.ast.ExprAST;
import com.anemortalkid.ast.FunctionAST;
import com.anemortalkid.ast.NumberExprAST;
import com.anemortalkid.ast.PrototypeAST;
import com.anemortalkid.ast.VariableExprAST;
import com.anemortalkid.lexer.Lexer;
import com.anemortalkid.token.Token;
import com.anemortalkid.token.Type;

public class Parser {

	private static Map<Character, Integer> binopPrecedence = new TreeMap<>();
	static {
		binopPrecedence.put('<', 10);
		binopPrecedence.put('+', 20);
		binopPrecedence.put('-', 20);
		binopPrecedence.put('*', 40);
	}

	private Token currToken;
	private Lexer lexer;

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	Token getNextToken() {
		return currToken = lexer.getToken();
	}

	// numberexpr ::= number
	private ExprAST parseNumberExpr() {
		NumberExprAST numberExprAST = new NumberExprAST(currToken.getNumericValue());
		return numberExprAST;
	}

	private ExprAST parseParenExpr() {
		getNextToken(); // eat

		ExprAST v = parseExpression();
		if (v == null) {
			return null;
		}

		if (currToken == null || currToken.getCharacterValue() != ')') {
			return logError("Expected: \')\' but found: " + currToken);
		}
		getNextToken(); // eat

		return v;
	}

	private ExprAST parseIdentifierExpr() {
		String idName = currToken.getIdentifier();

		getNextToken(); // consume identifier

		if(currToken.getTokenType() == Type.EOF || currToken.getCharacterValue() != '(') // simple ref
		{
			return new VariableExprAST(idName);
		}

		getNextToken(); // consume (
		List<ExprAST> args = new ArrayList<>();

		if (!currToken.getCharacterValue().equals(')')) // has args
		{
			while (true) {
				ExprAST arg = parseExpression();
				args.add(arg);
				if (currToken.getCharacterValue().equals(')')) {
					break;
				}
				if (!currToken.getCharacterValue().equals(',')) {
					return logError("Expected: \',\' but found: " + currToken);
				}
				getNextToken(); // advance
			}
		}

		getNextToken(); // eat parenthesis

		return new CallAST(idName, args);
	}

	private ExprAST parsePrimary() {
		switch (currToken.getTokenType()) {
		case IDENTIFIER:
			return parseIdentifierExpr();
		case NUMERIC:
			return parseNumberExpr();
		case UNKNOWN:
			if (currToken.getCharacterValue() == '(') {
				return parseParenExpr();
			}
		default:
			return logError("Unknown token when expecting an expression. Found: " + currToken);
		}
	}

	private int getTokenPrecedence() {
		if (currToken.getTokenType() != Type.UNKNOWN) {
			return -1;
		}

		Character charOp = currToken.getCharacterValue();
		Integer charVal = binopPrecedence.get(charOp);
		return charVal != null ? charVal : -1;
	}

	private ExprAST parseExpression() {

		ExprAST lhs = parsePrimary();
		if (lhs == null) {
			return null;
		}

		return parseBinOpRHS(0, lhs);
	}

	private ExprAST parseBinOpRHS(int exprPrec, ExprAST lhs) {

		ExprAST newLHS = lhs;
		while (true) {
			int tokPrec = getTokenPrecedence();

			// this is a binop that binds at least as tightly as the current op.
			// Consume it, otherwise we're done.
			if (tokPrec < exprPrec)
				return newLHS;

			Character binOp = currToken.getCharacterValue();
			getNextToken(); // eat op

			ExprAST rhs = parsePrimary();
			if (rhs == null) {
				return null;
			}

			// if binop binds less tightly with rhs than the next operator, let
			// the pending operator take the current rhs as its lhs
			int nextPrec = getTokenPrecedence();
			if (tokPrec < nextPrec) {
				rhs = parseBinOpRHS(tokPrec + 1, rhs);
				if (rhs == null) {
					return null;
				}
			}

			newLHS = new BinaryExprAST(binOp, lhs, rhs);
		}
	}

	private PrototypeAST parsePrototype() {
		if (currToken.getTokenType() != Type.IDENTIFIER) {
			return logErrorP("Expected function name in prototype. Found: " + currToken);
		}

		String fName = currToken.getIdentifier();
		getNextToken();// move along m8

		if (currToken.getCharacterValue() != '(') {
			return logErrorP("Expected \'(\' in prototype. Found: " + currToken);
		}

		List<String> args = new ArrayList<String>();
		while (getNextToken().getTokenType() == Type.IDENTIFIER) {
			args.add(currToken.getIdentifier());
		}
		if (currToken.getCharacterValue() != ')') {
			return logErrorP("Expected \')\' in prototype. Found: " + currToken);
		}

		getNextToken(); // eat )

		return new PrototypeAST(fName, args);
	}

	private FunctionAST parseDefinition() {
		getNextToken(); // eat def

		PrototypeAST proto = parsePrototype();
		if (proto == null) {
			return null;
		}

		ExprAST body = parseExpression();
		if (body == null) {
			return null;
		}

		return new FunctionAST(proto, body);
	}

	private PrototypeAST parseExtern() {
		getNextToken(); // consume extern
		return parsePrototype();
	}

	private FunctionAST parseTopLevelExpr() {
		ExprAST expression = parseExpression();

		if (expression == null) {
			return null;
		}

		// make anonymous proto
		PrototypeAST emptyProto = new PrototypeAST("", new ArrayList<>());
		return new FunctionAST(emptyProto, expression);
	}

	private ExprAST logError(String msg) {
		System.err.println(msg);
		System.err.println("\tConsumed:");
		lexer.printConsumed();
		return null;
	}

	private PrototypeAST logErrorP(String msg) {
		System.err.println(msg);
		System.err.println("\tConsumed:");
		lexer.printConsumed();
		return null;
	}

	private void handleDefinition() {
		FunctionAST definition = parseDefinition();
		if (definition != null) {
			System.out.println("Parsed def: " + definition);
		} else {
			getNextToken();
		}
	}

	private void handleExtern() {
		PrototypeAST extern = parseExtern();
		if (extern != null) {
			System.out.println("Parsed extern: " + extern);
		} else {

			getNextToken();
		}
	}

	private void handleTopLevelExpression() {
		FunctionAST topLevelExpr = parseTopLevelExpr();
		if (topLevelExpr != null) {
			System.out.println("parsed top level expr: " + topLevelExpr);
		} else {
			getNextToken();//
		}
	}

	private boolean hasMore = true;

	public void parse() {

		while (hasMore) {
			if (currToken == null) {
				getNextToken(); // go forth and carry on!
			}

			switch (currToken.getTokenType()) {
			case EOF:
				return; // done
			case DEF:
				handleDefinition();
				break;
			case EXTERN:
				handleExtern();
				break;
			case UNKNOWN:
				if (currToken.getCharacterValue() == ';') { // ignore semicolons
					getNextToken();
					break;
				}
			default:
				handleTopLevelExpression();
			}
		}
	}

}
