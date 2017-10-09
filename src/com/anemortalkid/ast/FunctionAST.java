package com.anemortalkid.ast;

/**
 * Represents a function definition
 * 
 * @author JMonterrubio
 *
 */
public class FunctionAST {

	private PrototypeAST proto;
	private ExprAST body;

	public FunctionAST(PrototypeAST proto, ExprAST body) {
		this.proto = proto;
		this.body = body;
	}

	public PrototypeAST getProto() {
		return proto;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FunctionAST [proto=").append(proto).append(",   body=").append(body).append("]");
		return builder.toString();
	}

	public ExprAST getBody() {
		return body;
	}

}
