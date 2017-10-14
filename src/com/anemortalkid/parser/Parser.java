package com.anemortalkid.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.anemortalkid.ast.BinaryExprAST;
import com.anemortalkid.ast.CallAST;
import com.anemortalkid.ast.ExprAST;
import com.anemortalkid.ast.ForExprAST;
import com.anemortalkid.ast.FunctionAST;
import com.anemortalkid.ast.IfExprAST;
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
		binopPrecedence.put('>', 10);
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
		getNextToken(); // consume the number
		return numberExprAST;
	}

	private ExprAST parseParenExpr() {
		getNextToken(); // eat

		ExprAST v = parseExpression();
		if (v == null) {
			return null;
		}

		if (currToken == null || currToken.getCharacterValue() != ')') {
			return logError("Expected: \')\'.");
		}
		getNextToken(); // eat

		return v;
	}

	private ExprAST parseIdentifierExpr() {
		String idName = currToken.getIdentifier();

		getNextToken(); // consume identifier

		if (!isOpenParen(currToken)) // simple ref
		{
			return new VariableExprAST(idName);
		}

		getNextToken(); // consume (
		List<ExprAST> args = new ArrayList<>();

		if (!isCloseParen(currToken)) // has args
		{
			while (true) {
				ExprAST arg = parseExpression();
				args.add(arg);
				if (currToken.getCharacterValue().equals(')')) {
					break;
				}
				if (!currToken.getCharacterValue().equals(',')) {
					return logError("Expected: \',\'.");
				}
				getNextToken(); // advance
			}
		}

		getNextToken(); // eat parenthesis

		return new CallAST(idName, args);
	}

	private boolean isCloseParen(Token token) {
		if (token.getTokenType() == Type.EOF) {
			return false;
		}
		if (token.getCharacterValue() == null) {
			return false;
		}
		return token.getCharacterValue() == ')';
	}

	private boolean isOpenParen(Token token) {
		if (token.getTokenType() == Type.EOF) {
			return false;
		}
		if (token.getCharacterValue() == null) {
			return false;
		}
		return token.getCharacterValue() == '(';
	}

	private boolean isEqualSign(Token token) {
		if (token.getTokenType() == Type.EOF) {
			return false;
		}
		if (token.getCharacterValue() == null) {
			return false;
		}
		return token.getCharacterValue() == '=';
	}

	private ExprAST parsePrimary() {
		switch (currToken.getTokenType()) {
		case IDENTIFIER:
			return parseIdentifierExpr();
		case NUMERIC:
			return parseNumberExpr();
		case IF:
			return parseIfExpr();
		case FOR:
			return parseForExpr();
		case UNKNOWN:
			if (currToken.getCharacterValue() == '(') {
				return parseParenExpr();
			}
		default:
			return logError("Unknown token when expecting an expression.");
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
			return logErrorP("Expected function name in prototype.");
		}

		String fName = currToken.getIdentifier();
		getNextToken();// move along m8

		if (currToken.getCharacterValue() != '(') {
			return logErrorP("Expected \'(\' in prototype.");
		}

		List<String> args = new ArrayList<String>();
		while (getNextToken().getTokenType() == Type.IDENTIFIER) {
			args.add(currToken.getIdentifier());
		}
		if (currToken.getCharacterValue() != ')') {
			return logErrorP("Expected \')\' in prototype.");
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

	private ExprAST parseIfExpr() {
		getNextToken(); // consume if

		ExprAST condition = parseExpression();
		if (condition == null) {
			return null;
		}

		if (currToken.getTokenType() != Type.THEN) {
			return logError("Expected \'then\'.");
		}

		getNextToken(); // consume then

		ExprAST then = parseExpression();
		if (then == null) {
			return null;
		}

		if (currToken.getTokenType() != Type.ELSE) {
			return logError("Expected \'then\'.");
		}

		getNextToken(); // consume else

		ExprAST elseExpr = parseExpression();

		return new IfExprAST(condition, then, elseExpr);
	}

	private ExprAST parseForExpr() {
		getNextToken(); // consume for

		if (currToken.getTokenType() != Type.IDENTIFIER) {
			return logError("Expected identifier after for");
		}

		String identifierStr = currToken.getIdentifier();
		getNextToken(); // consume identifier

		if (!isEqualSign(currToken)) {
			return logError("Expected \'=\' after for.");
		}

		getNextToken(); // consume equal

		ExprAST start = parseExpression();
		if (start == null) {
			return null;
		}

		if (!isComma(currToken)) {
			return logError("Expected \',\' after for start value.");
		}

		getNextToken(); // consume comma

		ExprAST end = parseExpression();
		if (end == null) {
			return null;
		}

		// parse step, which is optional
		ExprAST step = null;
		if (isComma(currToken)) {
			getNextToken(); // consume commma
			step = parseExpression();
			if (step == null) {
				return null;
			}
		}

		ExprAST body = parseExpression();
		if (body == null) {
			return null;
		}

		return new ForExprAST(start, end, step, body);
	}

	private boolean isComma(Token token) {
		if (token.getTokenType() == Type.EOF) {
			return false;
		}
		if (token.getCharacterValue() == null) {
			return false;
		}
		return token.getCharacterValue() == ',';
	}

	private ExprAST logError(String msg) {
		System.err.println(msg + " Found:" + currToken);
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
