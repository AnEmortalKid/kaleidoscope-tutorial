package com.anemortalkid.ast;

import java.util.List;

public class CallAST implements ExprAST {

	private String callee;
	private List<ExprAST> arguments;

	public CallAST(String callee, List<ExprAST> arguments) {
		this.callee = callee;
		this.arguments = arguments;
	}

	public List<ExprAST> getArguments() {
		return arguments;
	}

	public String getCallee() {
		return callee;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CallAST [callee=").append(callee).append(",   arguments=").append(arguments).append("]");
		return builder.toString();
	}

}
