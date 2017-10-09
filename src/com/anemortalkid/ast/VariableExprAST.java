package com.anemortalkid.ast;

public class VariableExprAST implements ExprAST {

	private String name;

	public VariableExprAST(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableExprAST [name=").append(name).append("]");
		return builder.toString();
	}
}
