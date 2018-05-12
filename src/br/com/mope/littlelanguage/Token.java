package br.com.mope.littlelanguage;

public class Token {

	public static final String INVALID = "Invalid";
	public static final String NO_TOKEN = "No Token";
	public static final String TYPE = "Primitive Type";
	public static final String RETURN = "Return";
	public static final String IF_EQUAL = "If Equal";
	public static final String IF_NOT_EQUAL = "If Not Equal";
	public static final String EQUAL = "Equal";
	public static final String INIT = "init";
	public static final String TWO_DOTS = "Two Dots";
	public static final String DOT_COMMA = "Dot and Comma";
	public static final String COMMA = "Comma";
	public static final String MARK = "Mark";
	public static final String DOT = "Dot";
	public static final String TIMES = "Times";
	public static final String MORE = "More";
	public static final String DIV = "Division";
	public static final String SUB = "Subtraction";
	public static final String MAJOR = "Major";
	public static final String MINOR = "Minor";
	public static final String FOR = "For";
	public static final String IF = "If";
	public static final String ELSE = "Else";
	public static final String AND = "And";
	public static final String OR = "Or";
	public static final String EOF = "EOF";
	public static final String EOL = "EOL";
	public static final String SPACE = "Space";
	public static final String TAB = "Tab";
	public static final String BEGIN = "Begin";
	public static final String END = "End";

	private int id = 0;
	private String name = NO_TOKEN;
	private String caracter;

	public Token() {}
	public Token(String name) {
		this.setName(name);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCaracter() {
		return caracter;
	}
	public void setCaracter(String caracter) {
		this.caracter = caracter;
	}

	@Override
	public int hashCode() {
		return 31 * 1 + this.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		Token other = (Token) obj;
		if (this.hashCode() != other.hashCode()) return false;
		return true;
	}

	@Override
	public String toString() {
		if ((this.name == null) && (this.caracter == null)) return super.toString();
		return this.name +"("+this.id +", "+ this.caracter+")";
	}
}
