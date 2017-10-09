package com.anemortalkid.ast;

import java.math.BigDecimal;

public class NumberExprAST implements ExprAST {

	private BigDecimal value;

	public NumberExprAST(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NumberExprAST [value=").append(value).append("]");
		return builder.toString();
	}

}
