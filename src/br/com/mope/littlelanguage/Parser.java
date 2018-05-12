package br.com.mope.littlelanguage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class Parser {

	private Lexer lexer = null;
	private Token token;

	public Parser(InputStream in) {
		this.lexer = new Lexer(in);
	}

	public List<Token> parse() throws Exception {

		final List<Token> tokens = new ArrayList<Token>();
		this.nextToken();

		while (!this.token.getName().equals(Token.EOF)) {
			System.out.println(this.token);
			tokens.add(this.token);
			this.nextToken();
		}
		return tokens;
	}

	private void nextToken() throws Exception {
		this.token = this.lexer.nextToken();
		System.out.println("Token atual -> " + this.token.getName());
	}
}
