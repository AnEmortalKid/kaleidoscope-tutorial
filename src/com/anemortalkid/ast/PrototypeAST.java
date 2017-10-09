package com.anemortalkid.ast;

import java.util.List;

/**
 * Represents the function signature
 * 
 * @author JMonterrubio
 *
 */
public class PrototypeAST {

	private String name;
	private List<String> args;

	public PrototypeAST(String name, List<String> args) {
		this.name = name;
		this.args = args;
	}

	public String getName() {
		return name;
	}

	public List<String> getArgs() {
		return args;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrototypeAST [name=").append(name).append(",   args=").append(args).append("]");
		return builder.toString();
	}

}
