package src.mua;

import java.util.Scanner;

import src.mua.dataSpace.DataSpace;
import src.mua.parse.Parser;

public class Main {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		DataSpace space = new DataSpace(); // global space 
		Parser parser = new Parser(space, space);  // main parser, global == local
		
		parser.parse("make \"run [ [a] [repeat 1 :a] ] make \"pi 3.14159", in);

		while(in.hasNextLine()) {
			String str = in.nextLine() + " "; 
			parser.parse(str, in);
		}

	}

}
