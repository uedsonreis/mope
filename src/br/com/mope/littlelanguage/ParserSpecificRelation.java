package br.com.mope.littlelanguage;


import java.io.InputStream;

class ParserSpecificRelation {

	private Token token;
	private Lexer lexer = null;
//	private String tokenString = "";
	private CreateDomain mobiLanguage;

	public ParserSpecificRelation(InputStream in) {
		this.lexer = new Lexer(in);
		this.mobiLanguage = new CreateDomain("DefaultDomain");
	}

	public Expression parse() throws Exception {
		nextToken();
		Expression exp = expression();
		// expect( Lexer.EOF, Lexer.EOL);
		return exp;
	}

	private Expression expression() throws Exception {

		while (!this.token.getName().equals(Token.EOL)) {
			System.out.println("this.token: " + this.token);
			nextToken();
		}
		return this.mobiLanguage;
	}

	private void nextToken() throws Exception {
		this.token = lexer.nextToken();
//		this.tokenString = Lexer.tokenCurrent;
//		System.out.println("Token atual -> " + this.tokenName(this.token));
		System.out.println("Token atual -> " + this.token.getName());
	}
//
//	private String tokenName(int token) {
//		if(token == Token.EOF.getId()){
//			return Token.EOF.getName();
//		} else if(token == Token.EOL.getId()){
//			return Token.EOL.getName();
//		} else {
//			return "default";
//		}
//	}
}
