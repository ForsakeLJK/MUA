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
	/*
	Test Code3 :
	make "a 1
	repeat 5 [make "a add :a 1 print :a]
	print :a
	make "n 6
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
	if eq :n 4
	[stop]
	[print :n f add :n 1]
	]
	]
	f 1
	make "let [
	[a123128736128asd b71823681263ahsgdajd]
	[
	make :a123128736128asd :b71823681263ahsgdajd
	make "qisi 74
	export :a123128736128asd
	]
	]
	let "a 8
	print :a
	print isname "qisi
	make "gcd [
		[a b]
		[
			if eq :b 0
				[output :a]
				[output gcd :b mod :a :b]
		]
	]
	print gcd 36 21

	make "a 7
	make "let [
	[e f]
	[
	make :e :f
	export :e
	]
	]
	let "a 8
	print :a
*/
	/* 
	test code 3:
	make "a 2
	make "a (:a+3)
	print :a
	make "f [
 		[a]
 		[output (:a*:a)]
	]
	print f 2
	print (f 2 * 4)
	print (2*(2+3)/4)	
	*/ 
	
	/*
	 * make "run [ [a] [repeat 1 :a] ] make "pi 3.14159
	 */
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
