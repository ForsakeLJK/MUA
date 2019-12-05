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
	
	private ArrayList<String> tokenList = null;  // input tokens as list
	private ArrayList<ArrayList<String>> lineList = null; // store different lines
	private String str = "";	// input code line which will be converted into list
	private Stack<MUAValue> stackVal = null; // stack storing values
	
	public Parser(DataSpace space)
	{
		/*Init parser. Load all operations. Get space.*/
		List<String> list = Arrays.asList( 
			"make", "thing", "erase", "isname", 
			"print", "read", "readlist", "repeat",
			"add", "sub", "mul", "div", "mod",
			"eq", "gt", "lt",
			"and", "or", 
			"not",
			"if"
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
		boolean redo = false;
		//Init vars
		tokenList = new ArrayList<String>();
		stackVal = new Stack<MUAValue>(); 
		lineList = new ArrayList<ArrayList<String>>();
		//ArrayList<ArrayList<String>> listOLists = new ArrayList<ArrayList<String>>();
		
		str = in;
		preprocess(); // do nothing temporarily
		lexer(inStream);      //
		Collections.reverse(tokenList);  // reverse the whole list
		System.out.print("after lexing and reversing:\n");
		System.out.print(tokenList.toString());
		System.out.print("\n");
		
		redo = split();
		
		while(redo && inStream.hasNextLine())  // if 
		{
			System.out.print("redoing...\n");
			// re-init
			tokenList.clear();
			lineList.clear();
			stackVal.clear();
			// update str
			str += " ";
			str += inStream.nextLine();
			// redo
			preprocess();
			lexer(inStream);
			Collections.reverse(tokenList);  // reverse the whole list
			System.out.print("after lexing and reversing:\n");
			System.out.print(tokenList.toString());
			System.out.print("\n");
			
			redo = split();
		}
			
		System.out.print("after splitting successfully:\n");
		System.out.print(lineList.toString());
		System.out.print("\n");
//		execute(inStream);
		// check every token's type and execute

	}
	
	private void execute(Scanner inStream)
	{
		MUAValue tmpVal;
		String type;
		for(String token : tokenList)
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
		//else if(isFunction(token))  // check if it denotes a function
		else
			type = "Function";
		//else
			//type = "Unknown";
		
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
	private void lexer(Scanner inStream)
	{
		int off = 0;
		int next = 0;
		int i = 0;
		Stack<String> tmpStack = new Stack<String>();
		String tmpSub = "";
		// skip all white spaces at beginning
		while(str.charAt(off) == ' ') 
			off++;
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
						tokenList.add("thing");
						i++;
					}
					tokenList.add("\""+ tmpSub.substring(i));
				}
				else
					tokenList.add(tmpSub);
				
				off = next + 1;
				if(off < str.length())
				{
					while(str.charAt(off) == ' ')
					{
						off++;
						if(off >= str.length())
							break;
					}				
				}

				
				if(off < str.length()) 
				{
					if(str.charAt(off) == '[')
					{
						int k = off + 1;
						tmpStack.push("[");
						// find the matching ]
						while(true)
						{
							if(k < str.length())
							{
								if(str.charAt(k) == '[')
									tmpStack.push("[");
								else if(str.charAt(k) == ']')
									tmpStack.pop();								
							}
							else // try to read multiple-line list
							{
								String tmpStr;
								tmpStr = inStream.nextLine();
								//preprocess(tmpStr);
								//tmpStr = "$" + tmpStr ? 
								str += tmpStr;
								//str.replace()
								continue;
							}
							
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
						tokenList.add("thing");
						i++;
					}
					tokenList.add("\""+ tmpSub.substring(i));
				}
				else
					tokenList.add(tmpSub);
				off = str.length();
			}
		}
	}
	
	
	private int retValCnt(String token, String type)
	{
		switch(type)
		{
			case "Operation":
				if(token.equals("make") || token.equals("erase")||token.equals("print")
						||token.equals("repeat")||token.equals("if"))
					return 0;
				else if(token.equals("thing")||token.equals("isname")||token.equals("read")||token.equals("readlist")
						|| token.equals("add")||token.equals("sub")||token.equals("mul")||token.equals("div")||token.equals("mod")
						|| token.equals("eq")||token.equals("gt")||token.equals("lt")||token.equals("and")||token.equals("or")
						|| token.equals("not"))
					return 1;
				break;
			case "Function":
				// need to add code later
				break;
			default:
				break;
		}
		return -1; // cnt error
	}
	
	private int operandCnt(String token, String type)
	{
		switch(type)
		{
			case "Operation":
				if(token.equals("make")||token.equals("add")||token.equals("sub")||token.equals("mul")
					||token.equals("div")||token.equals("mod")||token.equals("eq")||token.equals("gt")
					||token.equals("lt")||token.equals("and")||token.equals("or"))
					return 2;
				else if(token.equals("thing")||token.equals("erase")||token.equals("isname")
					||token.equals("print")||token.equals("read")||token.equals("readlist")
					||token.equals("repeat")||token.equals("not"))
					return 1;
				else if(token.equals("if"))
					return 3;
				break;
			case "Function":
				// need to add code later
				break;
			default:
				break;
		}
		
		return -1; // -1 denotes error
	}
	// split tokenList into statements
	private boolean split()
	{
		ArrayList<String> tmpList = new ArrayList<String>();
		String tmpType = null;
		MUANumber dummyVal = new MUANumber("0");
		
		int retCnt = 0;  // count how many return values an op or func has
		int opCnt = 0; // count operands needed
		
		for(String token : tokenList)
		{
			tmpList.add(token);
			tmpType = tokenTypeCheck(token);
			// here type "funtion" means it's neither a MUAValue nor a Operation
			if(tmpType.equals("Operation") || tmpType.equals("Function"))  
			{
				// Need to check how many return values it has	
				// -1 denotes error
				retCnt = retValCnt(token, tmpType);
				opCnt = operandCnt(token, tmpType);
				for(int i=0; i<opCnt; i++)
				{
					if(!stackVal.empty())
						stackVal.pop();
					else
						return true;  // fail, redo
				}
				for(int i=0; i<retCnt; i++)
					stackVal.push(dummyVal);
			}
			else // if it's MUAValue, push
				stackVal.push(dummyVal);
			
			if(stackVal.empty()) {
				lineList.add(tmpList);
				tmpList = new ArrayList<String>(); // reset tmpList
			}
		}
		
		// to avoid error, after executing this func, clear the stack
		stackVal.clear();
		
		return false;  // success, no redo
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
