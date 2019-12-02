package src.mua.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.*;

import src.mua.dataSpace.DataSpace;
import src.mua.value.MUABool;
import src.mua.value.MUAList;
import src.mua.value.MUANumber;
import src.mua.value.MUAValue;
import src.mua.value.MUAWord;


// use stack
public class Parser {
	private final ArrayList<String> operationList = new ArrayList<String>();
	
	private DataSpace space = null;
	
	private ArrayList<String> lineList = null;
	private String str = "";
	private Stack<MUAValue> stackVal = null;
	
	public Parser(DataSpace space)
	{
		List<String> list = Arrays.asList( 
			"make", "thing", "erase", "isname", "print", "read",
			"add", "sub", "mul", "div", "mod",
			"eq", "gt", "lt",
			"and", "or", 
			"not");
		operationList.addAll(list);
		
		this.space = space;
	}
	
	public void stackPush(MUAValue val)
	{
		stackVal.push(val);
	}
	
	public MUAValue stackPop()
	{
		return stackVal.pop();
	}
	
	public void parse(String in, Scanner inStream)
	{
		lineList = new ArrayList<String>();
		stackVal = new Stack<MUAValue>();
		
		str = in;
		preprocess();
		split();
		Collections.reverse(lineList);
		execute(inStream);
		// check every token's type and execute

	}
	
	private void execute(Scanner inStream)
	{
		MUAValue tmpVal;
		String type;
		for(String token : lineList)
		{
			type = tokenTypeCheck(token);
			switch(type) {
				case "Word":
					tmpVal = new MUAWord(token.substring(1), space);
					stackVal.push(tmpVal);
					break;
				case "Number":
					tmpVal = new MUANumber(token);
					stackVal.push(tmpVal);
					break;
				case "Bool":
					tmpVal = new MUABool(token);
					stackVal.push(tmpVal);
					break;
				case "List":
					tmpVal = new MUAList(token);
					stackVal.push(tmpVal);
					break;
				case "Operation":
					Operation.operate(this, token, space, inStream);
					break;
				default:break;
			}
		}
	}
	

	public String tokenTypeCheck(String token)
	{
		String type = "";
		
		if(token.charAt(0) == '\"')
			type = "Word";
		else if(isNum(token))
			type = "Number";
		else if(token.equals("true") || token.equals("false"))
			type = "Bool";
		else if(token.charAt(0) == '[') // need to amplify
			type = "List";
		else if(isOp(token))
			type = "Operation";
		else
			type = "Unknown";
		
		return type;
	}
	
	private boolean isNum(String token)
	{
		boolean res = false;
		Pattern intPattern = Pattern.compile("^[-\\+]?[\\d]*$");
		Pattern doublePattern = Pattern.compile("^[-\\+]?\\d*[.]\\d*$");
		
		if(intPattern.matcher(token).matches() || doublePattern.matcher(token).matches())
			res = true;

		return res;
	}
	
	private boolean isOp(String token)
	{
		return operationList.contains(token);
	}
//	private boolean isBool(String token)
//	{
//		return (token == "true") || (token == "false"); 
//	}
	
	//private boolean isList()
	// delete "//" , find lists, substitute : to thing "
	// 		about lists: rewrite a split
	private void preprocess()
	{
		//str += "wow";
	}
	
	private void split()
	{
		int off = 0;
		int next = 0;
		int i = 0;
		String tmpSub = "";
		
		next = str.indexOf(" ", off);
		while(next != -1 || off != str.length()) {
			if(next != -1) {
				tmpSub = str.substring(off, next);
				// how about :::::d?
				if(tmpSub.charAt(0)==':')
				{
					i = 0;
					//for(i = 0; tmpSub.charAt(i)==':'; i++)
					while(tmpSub.charAt(i) == ':') {
						lineList.add("thing");
						i++;
					}
					lineList.add("\""+ tmpSub.substring(i));
				}
				else
					lineList.add(tmpSub);
				
				off = next + 1;
				
				if(str.charAt(off) == '[')
					next = str.indexOf("]", off) + 1;
				else
					next = str.indexOf(" ", off);
			} 
			else {
				tmpSub = str.substring(off, str.length());
				if(tmpSub.charAt(0)==':')
				{
					i = 0;
					//for(i = 0; tmpSub.charAt(i)==':'; i++)
					while(tmpSub.charAt(i) == ':') {
						lineList.add("thing");
						i++;
					}
					lineList.add("\""+ tmpSub.substring(i));
				}
				else
					lineList.add(tmpSub);
				off = str.length();
			}
		}
	}
	
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		DataSpace space = new DataSpace();
		Parser p = new Parser(space);
		
		String testLine = in.nextLine();
		while(!testLine.equals("$"))
		{
			p.parse(testLine, in);
//			System.out.print("Splitted: ");
//			for(String k:p.lineList) {
//				System.out.print(k +" ");
//			}
//			System.out.print("\n");
			testLine = in.nextLine();
		}

	}
}
