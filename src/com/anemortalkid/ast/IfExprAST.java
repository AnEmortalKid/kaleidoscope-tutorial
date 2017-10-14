package com.anemortalkid.ast;

public class IfExprAST implements ExprAST {

	private ExprAST condition;
	private ExprAST thenBranch;
	private ExprAST elseBranch;

	public IfExprAST(ExprAST condition, ExprAST thenBranch, ExprAST elseBranch) {
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	public ExprAST getCondition() {
		return condition;
	}

	public ExprAST getThenBranch() {
		return thenBranch;
	}

	public ExprAST getElseBranch() {
		return elseBranch;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IfExprAST [condition=").append(condition).append(",   thenBranch=").append(thenBranch)
				.append(",   elseBranch=").append(elseBranch).append("]");
		return builder.toString();
	}

}
