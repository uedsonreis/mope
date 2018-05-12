package br.com.mope.littlelanguage;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class LittleLanguage {

	private static final String PATH = "/Users/uedsonreis/Documents/Projetos/Java/workspaces/workspace/MoPE/input/";

	public static List<Token> loadLanguage(File file) {

		List<Token> tokens = null;

		try {
			Parser p = new Parser(new FileInputStream(file));
			tokens = p.parse();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tokens;
	}

	public static void main(String[] args) {
		File file = new File(PATH);
		for (File f : file.listFiles()) {
			if (f.getName().endsWith(".mar")) {
				List<Token> tokens = loadLanguage(file);
				System.out.println(tokens);
			}
		}			
	}
}
