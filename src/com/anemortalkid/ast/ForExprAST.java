package com.anemortalkid.ast;

public class ForExprAST implements ExprAST {

	private ExprAST start;
	private ExprAST condition;
	private ExprAST step;

	private ExprAST body;

	public ForExprAST(ExprAST start, ExprAST condition, ExprAST step, ExprAST body) {
		this.start = start;
		this.condition = condition;
		this.step = step;
		this.body = body;
	}

	public ExprAST getStart() {
		return start;
	}

	public ExprAST getCondition() {
		return condition;
	}

	public ExprAST getStep() {
		return step;
	}

	public ExprAST getBody() {
		return body;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ForExprAST [start=").append(start).append(",   condition=").append(condition)
				.append(",   step=").append(step).append(",   body=").append(body).append("]");
		return builder.toString();
	}

}
