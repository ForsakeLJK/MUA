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
	
	private DataSpace space = null;  // global data space to store variables
	private DataSpace localSpace = null; // local data space 
	
	private ArrayList<String> tokenList = null;  // input tokens as list
	private ArrayList<ArrayList<String>> lineList = null; // store different lines
	private String str = "";	// input code line which will be converted into list
	private Stack<MUAValue> stackVal = null; // stack storing values
	
	public Parser(DataSpace space, DataSpace local)
	{
		/*Init parser. Load all operations. Get space.*/
		List<String> list = Arrays.asList( 
			"make", "thing", "erase", "isname", 
			"print", "read", "readlist", "repeat",
			"add", "sub", "mul", "div", "mod",
			"eq", "gt", "lt",
			"and", "or", 
			"not"
			);
		operationList.addAll(list);
		
		this.localSpace = local;
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
	
	public MUAValue parse(String in, Scanner inStream)
	{
		MUAValue retVal = null;
		
		boolean redo = false;
		//Init vars
		tokenList = new ArrayList<String>();
		stackVal = new Stack<MUAValue>(); 
		lineList = new ArrayList<ArrayList<String>>();
		//ArrayList<ArrayList<String>> listOLists = new ArrayList<ArrayList<String>>();
		
		str = in;
		str = preprocess(str); // do nothing temporarily
		//System.out.print("After preprocessing: " + str + "\n");
		lexer(inStream);      //
		Collections.reverse(tokenList);  // reverse the whole list
		//System.out.print("after lexing and reversing:\n");
		//System.out.print(tokenList.toString());
		//System.out.print("\n");
		
		redo = split();
		
		//if(redo)
		//{
			while(redo && inStream.hasNextLine())  // if 
			{
				//System.out.print("redoing...\n");
				// re-init
				tokenList.clear();
				lineList.clear();
				stackVal.clear();
				// update str
				str += " ";
				str += inStream.nextLine();
				// redo
				preprocess(str);
				lexer(inStream);
				Collections.reverse(tokenList);  // reverse the whole list
				//System.out.print("after lexing and reversing:\n");
				//System.out.print(tokenList.toString());
				//System.out.print("\n");
				
				redo = split();
			}		
		//}

		
		//if(!inStream.hasNextLine())
		//	System.out.print("Bad thing happened!");
		
		// reverse line list for executing
		Collections.reverse(lineList);
		//System.out.print("Split successfully!\n");
		//System.out.print(lineList.toString() + "\n");
		//System.out.print("\n");
		retVal = execute(inStream);
		// check every token's type and execute
		
		return retVal;
	}
	
	private MUAValue execute(Scanner inStream)
	{
		MUAValue tmpVal;
		String type;
		boolean stop_flag = false; 
		MUAValue retVal = null;
		
		for(ArrayList<String> line : lineList)
		{
			for(String token : line)
			{
				type = tokenTypeCheck(token);
				MUAValue tmpRetVal = null;
				MUABool tmpBool1;
				MUAList tmpList1;
				MUAList tmpList2;
				String tmpStr;
				String tmpStr1;
				// if it's a MUAValue, push it into stack
				switch(type) 
				{
					case "Word":
						// trim " here
						// variable always in the local space
						// noting in main parser local == global
						// here it must be make "variable <value>
						tmpVal = new MUAWord(token.substring(1), space, localSpace);
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
						// operate() can access both global and local space
						Operation.operate(this, token, space, localSpace, inStream);
						break;
					case "Function":
						// fetch arglist
						// bind arg to tmpSpace
						DataSpace tmpSpace = new DataSpace();
						String f_code = bindArgAndFetchCode(token, tmpSpace);
						// new parser with space and tmpSpace
						// fetch code
						// parse
						//System.out.println("Code to run: " + f_code);
						//DataSpace tmpSpace = new DataSpace();
						Parser tmpParser = new Parser(space, tmpSpace);
						tmpRetVal  = tmpParser.parse(f_code, inStream);
						
						// if retVal == null ?
						if(retValCnt(token, "Function")==0)
							;
						else  // if retVal exists, push
							stackVal.push(tmpRetVal);
						
						break;
					case "Stop":
						stop_flag = true;
						break;
					case "Output":
						retVal = stackVal.pop();
						break;
					case "Export":
						MUAWord expVar = (MUAWord) stackVal.pop();
						MUAValue expVal = localSpace.fetchVal(expVar);
						if(space.inNameSpace(expVar))
							space.replaceBond(expVar, expVal);
						else
							space.addBond(expVar, expVal);
						break;
					case "If":
						/* test code:
							make "n 5
							print :n
						    if lt :n 2
						      [print sub :n 2]
						      [print add :n 1]
						 */
						tmpParser = new Parser(space, localSpace);  // space in if is the same as its parent parser
						tmpBool1 = (MUABool)stackPop(); // boolean
						tmpList1 = (MUAList)stackPop();  // condition 1 (when true)
						tmpList2 = (MUAList)stackPop();  // condition 2 (when false)
						tmpStr = tmpList1.getList().substring(1, tmpList1.getList().length()-1); // code 1
						tmpStr1 = tmpList2.getList().substring(1, tmpList2.getList().length()-1); // code 2
						// run conditional code
						if(tmpBool1.getVal())
						{
							tmpRetVal = tmpParser.parse(tmpStr, inStream);
						}
						else
						{
							tmpRetVal = tmpParser.parse(tmpStr1, inStream);
						}
						
						if(tmpRetVal == null)
							;
						else
							retVal = tmpRetVal;
						break;
					default:break;
				}
				if(stop_flag)
					break;
			}
			stackVal.clear();
			if(stop_flag)
				break;
		}
		
		return retVal;
	}
	

	private String bindArgAndFetchCode(String token, DataSpace newSpace) {
		// bind arg to this function
		MUAWord f_name = new MUAWord(token, space, localSpace);
		MUAList f_list = (MUAList)f_name.fetchBindVal();
		String f_list_content = f_list.toString().substring(1, f_list.toString().length()-1);  // eliminate []
		// first pair of [] must be argList
		String f_argList = f_list_content.substring(f_list_content.indexOf('[') + 1, f_list_content.indexOf(']', f_list_content.indexOf('[')));
		MUAWord arg_name = null;
		String f_raw_code = f_list_content.substring(f_list_content.indexOf(']')+1, f_list_content.length());
		//System.out.println(f_raw_code);
		String f_code = f_raw_code.substring(f_raw_code.indexOf('[')+1, f_raw_code.lastIndexOf(']'));
		
		Pattern emptyPattern = Pattern.compile("[\\s+]?");
		if(emptyPattern.matcher(f_argList).matches())
			; // do nothing
		else   // bind into local
		{
			String[] splitted = f_argList.trim().split("\\s+");
			//cnt = splitted.length;
			for(String str : splitted) {
				//arg_name = new MUAWord();
				arg_name = new MUAWord(str, space, newSpace);
				arg_name.setBond(stackVal.pop());
				//newSpace.addBond(, stackVal.pop());
			}
		}
		
		return f_code;
	}

	public String tokenTypeCheck(String token)
	{
		String type = "";
		MUAWord f_name = new MUAWord(token, space, localSpace);
		
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
		else if(token.equals("stop"))
			type = "Stop";
		else if(token.equals("output"))
			type = "Output";
		else if(token.equals("if"))
			type = "If";
		else if(token.equals("export"))
			type = "Export";
		else if(f_name.checkBond())
			type = "Function";
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
	private String preprocess(String s)
	{
		//str += "wow";
		s = s.replaceAll("\t", " ");
		
		return s;
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
								tmpStr = preprocess(tmpStr);
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
						||token.equals("repeat"))
					return 0;
				else if(token.equals("thing")||token.equals("isname")||token.equals("read")||token.equals("readlist")
						|| token.equals("add")||token.equals("sub")||token.equals("mul")||token.equals("div")||token.equals("mod")
						|| token.equals("eq")||token.equals("gt")||token.equals("lt")||token.equals("and")||token.equals("or")
						|| token.equals("not"))
					return 1;
				break;
			case "Function":
				// need to add code later
				MUAWord f_name = new MUAWord(token, space, localSpace);
				MUAList f_list = (MUAList)f_name.fetchBindVal();
				
				if(f_list.toString().contains("output"))
					return 1;
				else
					return 0;				
				//break;
			case "Stop":
				return 0;
			case "Output":
				return 0;
			case "If":
				return 0;
			case "Export":
				return 0;
			default:
				break;
		}
		
		return -1; // cnt error
	}
	
	private int argCnt(String argList)
	{
		int cnt = 0;
	
		Pattern emptyPattern = Pattern.compile("[\\s+]?");
		if(emptyPattern.matcher(argList).matches())
			return cnt;
		else
		{
			String[] splitted = argList.trim().split("\\s+");
			//cnt = splitted.length;
			for(String str : splitted) {
				//System.out.println(a);
				cnt++;
			}
			
			return cnt;
			//System.out.println(splitted.toString());
		}
		//return -1;           
	}
	
	private int operandCnt(String token, String type)
	{
		switch(type)
		{
			case "Operation":
				if(token.equals("make")||token.equals("add")||token.equals("sub")||token.equals("mul")
					||token.equals("div")||token.equals("mod")||token.equals("eq")||token.equals("gt")
					||token.equals("lt")||token.equals("and")||token.equals("or")
					||token.equals("repeat"))
					return 2;
				else if(token.equals("thing")||token.equals("erase")||token.equals("isname")
					||token.equals("print")
					||token.equals("not"))
					return 1;
				else if(token.equals("read")||token.equals("readlist"))
					return 0;
				break;
			case "Function":
				// need to add code later
				MUAWord f_name = new MUAWord(token, space, localSpace);
				MUAList f_list = (MUAList)f_name.fetchBindVal();
				String f_code = f_list.toString().substring(1, f_list.toString().length()-1);  // eliminate []
				// first pair of [] must be argList
				String f_argList = f_code.substring(f_code.indexOf('[') + 1, f_code.indexOf(']', f_code.indexOf('[')));
				
				return argCnt(f_argList);
				
				//break;
			case "Output":
				return 1;
			case "Stop":
				return 0;
			case "If":
				return 3;
			case "Export":
				return 1;
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
			if(tmpType.equals("Operation") || tmpType.equals("Function") 
				|| tmpType.equals("Stop") || tmpType.equals("Output") 
				|| tmpType.equals("If") || tmpType.equals("Export"))  
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
		Parser p = new Parser(space, space);
		
		String testLine = in.nextLine();
		p.parse(testLine, in);
		//p.split();


	}
}
