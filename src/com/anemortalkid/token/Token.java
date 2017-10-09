package com.anemortalkid.token;

import java.math.BigDecimal;

public class Token {

	private Type tokenType;
	private String identifier;
	private BigDecimal numericValue;
	private Character characterValue;

	private Token(Type type) {
		this.tokenType = type;
	}

	public Type getTokenType() {
		return tokenType;
	}

	public void setTokenType(Type tokenType) {
		this.tokenType = tokenType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public BigDecimal getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(BigDecimal numericValue) {
		this.numericValue = numericValue;
	}

	public void setCharacterValue(Character characterValue) {
		this.characterValue = characterValue;
	}

	public Character getCharacterValue() {
		return characterValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Token [tokenType=").append(tokenType).append(",   identifier=").append(identifier)
				.append(",   numericValue=").append(numericValue).append(",   characterValue=").append(characterValue)
				.append("]");
		return builder.toString();
	}

	public static Token EOF = new Token(Type.EOF);

	public static Token def() {
		Token t = new Token(Type.DEF);
		return t;
	}

	public static Token extern() {
		Token t = new Token(Type.EXTERN);
		return t;
	}

	public static Token identifier(String identifierStr) {
		Token t = new Token(Type.IDENTIFIER);
		t.setIdentifier(identifierStr);
		return t;
	}

	public static Token numeric(String numString) {
		Token t = new Token(Type.NUMERIC);
		t.setNumericValue(new BigDecimal(numString));
		return t;
	}

	public static Token unknown(char c) {
		Token t = new Token(Type.UNKNOWN);
		t.setCharacterValue(c);
		return t;
	}

}
