package com.anemortalkid.driver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.anemortalkid.lexer.Lexer;
import com.anemortalkid.parser.Parser;

public class Driver {

	public static void main(String[] args) throws IOException, URISyntaxException {
		URL urlResource = Driver.class.getResource("source.kal");
		String source = Files.readAllLines(Paths.get(urlResource.toURI())).stream().collect(Collectors.joining("\n"));
		Lexer lexer = new Lexer(source);
		Parser parsey = new Parser(lexer);
		parsey.parse();
	}
}
