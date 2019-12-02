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
	private final ArrayList<String> operationList = new ArrayList<String>(); // operations
	
	private DataSpace space = null;  // data space to store variables
	
	private ArrayList<String> lineList = null;  // input code line as list
	private String str = "";	// input code line which will be converted into list
	private Stack<MUAValue> stackVal = null; // stack storing values
	
	public Parser(DataSpace space)
	{
		/*Init parser. Load all operations. Get space.*/
		List<String> list = Arrays.asList( 
			"make", "thing", "erase", "isname", "print", "read", "readlist", "repeat",
			"add", "sub", "mul", "div", "mod",
			"eq", "gt", "lt",
			"and", "or", 
			"not"
			);
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
		preprocess(); // do nothing temporarily
		split();      //
		System.out.print("after splitting:");
		System.out.print(lineList.toString());
		System.out.print("\n");
//		Collections.reverse(lineList);  // reverse the whole list
//		execute(inStream);
		// check every token's type and execute

	}
	
	private void execute(Scanner inStream)
	{
		MUAValue tmpVal;
		String type;
		for(String token : lineList)
		{
			type = tokenTypeCheck(token);
			// if it's a MUAValue, push it into stack
			switch(type) {
				case "Word":
					// delete " here
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
				//case "Function":
					// fetch the corresponding list
					// new parser with the original spcae and a new stack needed
					// scope belongs to parser
					//MUAFunc.funcExecute(this, token, space, inStream);
					//break;
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
		else if(isFunction(token))  // check if it denotes a function
			type = "Function";
		else
			type = "Unknown";
		
		return type;
	}
	
	private boolean isFunction(String token)
	{
		// check if this token as a MUAWord has a bindVal
		//		if it has and its bindVal is a list, it is a function
		//		then return true
		return false;
		// else, it's not
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
	
	// check if a token is an op
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
	
	// substitute : to thing "
	// if [] exists, add it into list as a whole 
	private void split()
	{
		int off = 0;
		int next = 0;
		int i = 0;
		int tmp = 0;
		Stack<String> tmpStack = new Stack<String>();
		String tmpSub = "";
		
		next = str.indexOf(" ", off);
		while(next != -1 || off != str.length()) {
			if(next != -1) {
				tmpSub = str.substring(off, next);
				// how about :::::d?
				// : to thing "
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
				
				if(off < str.length()) 
				{
					if(str.charAt(off) == '[')
					{
						int k = off + 1;
						tmpStack.push("[");
						// find the matching ]
						while(true)
						{
							if(str.charAt(k) == '[')
								tmpStack.push("[");
							else if(str.charAt(k) == ']')
								tmpStack.pop();
							if(tmpStack.empty())
								break;
							else
								k++;
						}
						if(k < str.length()-1)
							next = k + 1;  // add the entire [] into list
						else
							next = str.length();
					}
					else
						next = str.indexOf(" ", off);  
				}
				else 
					break;
			} 
			else { // the last one
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
		p.parse(testLine, in);
		//p.split();


	}
}
