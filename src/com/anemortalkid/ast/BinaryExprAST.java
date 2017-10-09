package com.anemortalkid.ast;

public class BinaryExprAST implements ExprAST {

	private char operation;
	private ExprAST lhs;
	private ExprAST rhs;

	public BinaryExprAST(char operation, ExprAST lhs, ExprAST rhs) {
		this.operation = operation;
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public char getOperation() {
		return operation;
	}

	public ExprAST getLhs() {
		return lhs;
	}

	public ExprAST getRhs() {
		return rhs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BinaryExprAST [operation=").append(operation).append(",   lhs=").append(lhs).append(",   rhs=")
				.append(rhs).append("]");
		return builder.toString();
	}

}
