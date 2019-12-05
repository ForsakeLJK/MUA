package src.mua;

import java.util.Scanner;

import src.mua.dataSpace.DataSpace;
import src.mua.parse.Parser;

public class Main {

	/*
	Test code1:
 	make "a 6
	print :a
	make "b "a
	print thing :b
	make "c mul add :a 13 :a
	print sub :c "6
	make "d read
	1234
	print isname "d
	print :d
	make "x eq :d 1234
	print :x
	erase "d
	print not isname "d
	*/	
	/*
	Test code2:
	make "a 1
	repeat 4 [make "a add :a 1 print :a]
	print :a
	make "n 5
    make "f [
     [n]
     [
      if lt :n 2
       [output 1]
       [output mul :n f sub :n 1]
     ]
    ]
	print f :n
	print :n
	make "f [
	 [n]
	 [
	  if eq :n 5
	   [stop]
	   [print :n f add :n 1]
	 ]
	]
	f 1
	*/
	    
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		DataSpace space = new DataSpace(); 
		Parser parser = new Parser(space);
		// while in.hasNextLine?
		while(in.hasNextLine()) {
			String str = in.nextLine(); // no "\n" occurs
			parser.parse(str, in);
		}

	}

}
