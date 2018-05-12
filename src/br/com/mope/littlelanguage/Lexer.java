package br.com.mope.littlelanguage;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

class Lexer {
	// TODO current token should be an Token instead of int
	public static String tokenCurrent = "";
	public int token = 0;

	private StreamTokenizer input;

	public Lexer(InputStream in) {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		input = new StreamTokenizer(r);
		input.resetSyntax();
		input.eolIsSignificant(true);

		input.wordChars('a', 'z');
		input.wordChars('A', 'Z');
		input.wordChars('0', '9');
		input.wordChars('.', '.');
		input.wordChars('_', '_');
		input.wordChars('-', '-');
		input.wordChars('\"', '\"');
		input.whitespaceChars('\u0000', '\u0020');
		input.ordinaryChar('	');
		input.ordinaryChar('!');
		input.ordinaryChar('=');
		input.ordinaryChar('<');
		input.ordinaryChar('>');
		input.ordinaryChar(';');
		input.ordinaryChar('(');
		input.ordinaryChar(')');
		input.ordinaryChar(',');
		input.ordinaryChar(':');
		input.ordinaryChar('*');
	}

	public Token nextToken() throws Exception {
		final Token token = new Token();
		try {
			int nextIdToken = input.nextToken();
			System.out.println("CURRENT INPUT " + input);
			System.out.println("CURRENT VAL: " + input.sval);
			token.setId(nextIdToken);
			token.setCaracter(input.sval);

			switch (nextIdToken) {
			case StreamTokenizer.TT_EOF:
				token.setName(Token.EOF);
				tokenCurrent = Token.EOF;
				break;
			case StreamTokenizer.TT_EOL:
				tokenCurrent = Token.EOL;
				token.setName(Token.EOL);
				break;
			case StreamTokenizer.TT_WORD:
				tokenCurrent = input.sval;
				if (input.sval.equalsIgnoreCase(Token.RETURN)) {
					token.setName(Token.RETURN);
				} else if (input.sval.equalsIgnoreCase(Token.FOR)) {
					token.setName(Token.FOR);
				} else if (input.sval.equalsIgnoreCase(Token.AND)) {
					token.setName(Token.AND);
				} else if (input.sval.equalsIgnoreCase(Token.IF)) {
					token.setName(Token.IF);
				} else if (input.sval.equalsIgnoreCase(Token.ELSE)) {
					token.setName(Token.ELSE);
				} else if (input.sval.equals("-")) {
					token.setName(Token.SUB);
				} else if (input.sval.equals("==")) {
					token.setName(Token.IF_EQUAL);
				} else if (input.sval.equals("!=")) {
					token.setName(Token.IF_NOT_EQUAL);
				} else if (input.sval.equals("init")) {
					token.setName(Token.INIT);
				} else if (input.sval.equals("Integer")) {
					token.setName(Token.TYPE);
				} else if (input.sval.equals("Long")) {
					token.setName(Token.TYPE);
				} else if (input.sval.equals("String")) {
					token.setName(Token.TYPE);
				} else if (input.sval.equals("Date")) {
					token.setName(Token.TYPE);
				} else if (input.sval.equals("Boolean")) {
					token.setName(Token.TYPE);
				}
				break;
			case '	':
				tokenCurrent = "	";
				token.setName(Token.TAB);
				break;
			case ' ':
				tokenCurrent = " ";
				token.setName(Token.SPACE);
				break;
			case '(':
				tokenCurrent = "(";
				token.setName(Token.BEGIN);
				break;
			case '_':
				tokenCurrent = "_";
				token.setName(Token.DOT);
				break;
			case ',':
				tokenCurrent = ",";
				token.setName(Token.COMMA);
				break;
			case '!':
				tokenCurrent = "!";
				token.setName(Token.IF_NOT_EQUAL);
				break;
			case '=':
				tokenCurrent = "=";
				token.setName(Token.EQUAL);
				break;
			case ')':
				tokenCurrent = ")";
				token.setName(Token.END);
				break;
			case '"':
				tokenCurrent = "\"";
				token.setName(Token.MARK);
				break;
			case ':':
				tokenCurrent = ":";
				token.setName(Token.TWO_DOTS);
				break;
			case ';':
				tokenCurrent = ";";
				token.setName(Token.DOT_COMMA);
				break;
			case '<':
				tokenCurrent = "<";
				token.setName(Token.MINOR);
				break;
			case '>':
				tokenCurrent = ">";
				token.setName(Token.MAJOR);
				break;
			case '+':
				tokenCurrent = "+";
				token.setName(Token.MORE);
				break;
			case '-':
				tokenCurrent = "-";
				token.setName(Token.SUB);
				break;
			case '*':
				tokenCurrent = "*";
				token.setName(Token.TIMES);
				break;
			case StreamTokenizer.TT_NUMBER:
				System.err.println("entrou no TT_NUMBER");
				break;
			default:
				token.setName(Token.INVALID);
				throw new Exception("ERRO LEXICO: Cadeia de caracteres invalida.");
			}
		} catch (IOException e) {
			token.setName(Token.EOF);
		}
		return token;
	}
}
