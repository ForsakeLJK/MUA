package src.mua.parse;

import java.util.ArrayList;
import java.util.Scanner;

import src.mua.dataSpace.DataSpace;
import src.mua.value.MUABool;
import src.mua.value.MUAList;
import src.mua.value.MUANumber;
import src.mua.value.MUAValue;
import src.mua.value.MUAWord;

// make, thing/:, erase, isname, print, read
// add, sub, mul, div, mod
// eq, gt, lt
// and, or
// not
public class Operation {
	
	private static String fetchStrVal(MUAValue Val)
	{
		String res = "";
		switch(Val.getType()) {
		case "Word":
			MUAWord tmpWord = (MUAWord) Val;
			res = tmpWord.toString();
			break;
		case "Number":
			MUANumber tmpNum = (MUANumber) Val;
			res = tmpNum.toString();
			break;
		case "Bool":
			MUABool tmpBool = (MUABool) Val;
			res = tmpBool.toString();
			break;
		case "List":
			MUAList tmpList = (MUAList) Val;
			res = tmpList.toString();
			break;
		}
		
		return res;
	}

	private static String fetchStrContent(MUAValue Val) {
		String res = "";
		switch (Val.getType()) {
		case "Word":
			MUAWord tmpWord = (MUAWord) Val;
			res = tmpWord.toString();
			break;
		case "Number":
			MUANumber tmpNum = (MUANumber) Val;
			res = tmpNum.toString();
			break;
		case "Bool":
			MUABool tmpBool = (MUABool) Val;
			res = tmpBool.toString();
			break;
		case "List":
			MUAList tmpList = (MUAList) Val;
			res = tmpList.getList();
			break;
		}

		return res;
	}
	
	// space here denotes the global space, whereas localSpace the local space
	public static void operate (Parser p, String opStr, DataSpace space, DataSpace localSpace, Scanner inStream)
	{
		
		if(opStr.equals("eq")||opStr.equals("gt")||opStr.equals("lt"))
			opComp(p, opStr);
		else if(opStr.equals("not")||opStr.equals("and")||opStr.equals("or"))
			opLogic(p, opStr);
		else if(opStr.equals("add")||opStr.equals("sub")||opStr.equals("mul")||opStr.equals("div")||opStr.equals("mod"))
			opArithmetic(p, opStr);
		else 
			opOther(p, opStr, space, localSpace, inStream);
	}
	
	private static void opArithmetic(Parser p, String opStr)
	{
		MUANumber tmpNum1 = null;
		MUANumber tmpNum2 = null;
		MUANumber resNum = null;
		
		MUAWord tmpWord1 = null;
		MUAWord tmpWord2 = null;
		
		MUAValue tmpVal1;
		MUAValue tmpVal2;
		
		tmpVal1 = p.stackPop();
		tmpVal2 = p.stackPop();
		
		if(tmpVal1.getType().equals("Word"))
		{
			tmpWord1 = (MUAWord) tmpVal1;
			if(tmpWord1.checkConvertToNum())
			{
				tmpNum1 = new MUANumber(tmpWord1.getContent());
			}
		}
		else
		{
			tmpNum1 = (MUANumber) tmpVal1;
		}
		
		if(tmpVal2.getType().equals("Word"))
		{
			tmpWord2 = (MUAWord) tmpVal2;
			if(tmpWord2.checkConvertToNum())
			{
				tmpNum2 = new MUANumber(tmpWord2.getContent());
			}
		}
		else
		{
			tmpNum2 = (MUANumber) tmpVal2;
		}
		
		switch(opStr)
		{
			case "add":
				resNum = new MUANumber(String.valueOf(tmpNum1.getVal() + tmpNum2.getVal()));
				break;
			case "sub":
				resNum = new MUANumber(String.valueOf(tmpNum1.getVal() - tmpNum2.getVal()));
				break;
			case "mul":
				resNum = new MUANumber(String.valueOf(tmpNum1.getVal() * tmpNum2.getVal()));
				break;
			case "div":
				resNum = new MUANumber(String.valueOf(tmpNum1.getVal() / tmpNum2.getVal()));
				break;
			case "mod":
				resNum = new MUANumber(String.valueOf(tmpNum1.getVal() % tmpNum2.getVal()));
				break;
		}		

		p.stackPush(resNum);
	}
	
	private static void opOther(Parser p, String opStr, DataSpace space, DataSpace localSpace, Scanner inStream)
	{
		MUABool tmpBool1 = null;
		MUAWord tmpWord1 = null;
		MUAValue tmpVal1 = null;
		MUAWord name = null;
		MUAValue bindVal = null;
		MUAList tmpList1 = null;
		MUANumber tmpNum1 = null;
		Parser tmpParser = null;
		ArrayList<MUAValue> tmpValArr = null;
		char[] tmpCharArr = null;
		
		String tmpStr;
		switch (opStr) {
			case "make": 
				name = (MUAWord) p.stackPop();
				bindVal = p.stackPop();
				name.setBond(bindVal, localSpace, space);
				break; 
			case "thing":
				name = (MUAWord) p.stackPop();
				p.stackPush(name.fetchBindVal());
				break;
			case "erase": 
				name = (MUAWord) p.stackPop();
				name.eraseBond();
				break;
			case "isname": 
				tmpWord1 = (MUAWord) p.stackPop();
				tmpBool1 = new MUABool(String.valueOf(tmpWord1.checkBond()));
				p.stackPush(tmpBool1);
				break;
			case "print":
				tmpVal1 = p.stackPop();
				System.out.println(fetchStrVal(tmpVal1));
				break;
			case "read": 
				//Scanner in = new Scanner(System.in);
				tmpStr = inStream.nextLine();
				tmpWord1 = new MUAWord(tmpStr, space, localSpace);
				p.stackPush(tmpWord1);
				break;
			case "readlist":
				tmpStr = "[ ";
				tmpStr += inStream.nextLine();
				tmpStr += " ]";  // leave a space before ]
				tmpList1 = new MUAList(tmpStr);
				p.stackPush(tmpList1);
				break;
			case "repeat":
				// test code:
				// make "a 1
				// repeat 4 [make "a add :a 1 print :a]
				// result: 2 3 4 5
				//tmpSpace = new DataSpace();
				tmpParser = new Parser(space, localSpace);    // space in repeat is the same as its parent parser
				tmpNum1 = (MUANumber)p.stackPop();  // repeat times
				tmpList1 = (MUAList)p.stackPop(); // repeat code
				tmpStr = tmpList1.getList().substring(1, tmpList1.getList().length()-1);  // code to repeat
				
				//System.out.print("code to run is:\n");
				//System.out.print(tmpStr+"\n");
				// repeat codes tmpNum1 times
				for(int i = 0; i<(int)tmpNum1.getVal(); i++)
				{
					tmpParser.parse(tmpStr, inStream);
				}
				break;
			case "sentence":
				tmpStr = "[";
				tmpStr += fetchStrVal(p.stackPop());
				tmpStr += " " + fetchStrVal(p.stackPop());  // if it's a list, the string'll be w/o []
				tmpStr += "]";
				tmpList1 = new MUAList(tmpStr);
				p.stackPush(tmpList1);
				break;
			case "list":
				tmpStr = "[";
				tmpStr += fetchStrContent(p.stackPop());  // if it's a list, the string'll be w/ []
				tmpStr += " " + fetchStrContent(p.stackPop());
				tmpStr += "]";
				tmpList1 = new MUAList(tmpStr);
				p.stackPush(tmpList1);
				break;
			case "join":
				tmpStr = "[";
				tmpStr += fetchStrVal(p.stackPop()).trim(); // it must be a list, and return a string w/o []
				if(!tmpStr.equals("["))
					tmpStr += " ";
				tmpStr += fetchStrContent(p.stackPop()); // if it's a list, the string'll be w/ []
				tmpStr += "]";
				tmpList1 = new MUAList(tmpStr);
				p.stackPush(tmpList1);
				break;
			case "word":
				tmpStr = fetchStrVal(p.stackPop()).trim();
				tmpStr += fetchStrVal(p.stackPop()).trim();
				tmpWord1 = new MUAWord(tmpStr.trim(), space, localSpace);
				p.stackPush(tmpWord1);
				break;
			case "isempty":
				tmpVal1 = p.stackPop();
				switch (tmpVal1.getType()) {
					case "Word":
						tmpWord1 = (MUAWord) tmpVal1;
						tmpBool1 = new MUABool(String.valueOf(tmpWord1.isEmpty()));
						break;
					case "List":
						tmpList1 = (MUAList) tmpVal1;
						tmpBool1 = new MUABool(String.valueOf(tmpList1.isEmpty()));
						break;
					default:
						break;
				}
				p.stackPush(tmpBool1);
				break;
			case "islist":
				tmpVal1 = p.stackPop();
				if(tmpVal1.getType().equals("List"))
					tmpBool1 = new MUABool("true");
				else
					tmpBool1 = new MUABool("false");
				p.stackPush(tmpBool1);
				break;
			case "first":
				tmpVal1 = p.stackPop();

				switch (tmpVal1.getType()){
					case "List":
						tmpList1 = (MUAList) tmpVal1;
						tmpValArr = tmpList1.lexListContent(space, localSpace);
						tmpVal1 = tmpValArr.get(0);
						if (tmpVal1.getType().equals("List")) {
							tmpList1 = (MUAList) tmpVal1;
							p.stackPush(tmpList1);
						} else if (tmpVal1.getType().equals("Word")) {
							tmpWord1 = (MUAWord) tmpVal1;
							p.stackPush(tmpWord1);
						}
						break;
					case "Word":
						tmpWord1 = (MUAWord) tmpVal1;
						tmpWord1 = new MUAWord(tmpWord1.getContent().substring(0, 1), space, localSpace);
						p.stackPush(tmpWord1);
						break;
					case "Number":
						tmpNum1 = (MUANumber) tmpVal1;
						tmpWord1 = new MUAWord(tmpNum1.getOriginalStr().substring(0, 1), space, localSpace);
						p.stackPush(tmpWord1);
						break;
					case "Bool":
						tmpBool1 = (MUABool) tmpVal1;
						tmpWord1 = new MUAWord(tmpBool1.toString().substring(0, 1), space, localSpace);
						p.stackPush(tmpWord1);
						break;
					default:
						System.out.println("first-unknown-ValType");
						break;
				}

				break;
			case "butfirst":

				break;
			case "butlast":

				break;
		}
	}
	
	private static void opComp(Parser p, String opStr)
	{

		MUABool resBool = null;
		
		MUANumber tmpNum1 = null;
		MUANumber tmpNum2 = null;
		
		MUAWord tmpWord1 = null;
		MUAWord tmpWord2 = null;
		
		MUAValue tmpVal1;
		MUAValue tmpVal2;
		
		tmpVal1 = p.stackPop();
		tmpVal2 = p.stackPop();
		
		if(tmpVal1.getType().equals("Word") && tmpVal2.getType().equals("Word"))
		{
			tmpWord1 = (MUAWord) tmpVal1;
			tmpWord2 = (MUAWord) tmpVal2;
			
			switch(opStr)
			{
				case "eq":
					resBool = new MUABool(String.valueOf(tmpWord1.getContent().equals(tmpWord2.getContent())));
					break;
				case "gt":
					resBool = new MUABool(String.valueOf((tmpWord1.getContent().compareTo(tmpWord2.getContent()) > 0)));
					break;
				case "lt":
					resBool = new MUABool(String.valueOf((tmpWord1.getContent().compareTo(tmpWord2.getContent()) > 0)));
					break;
			}
		}
		else if(tmpVal1.getType().equals("Number") && tmpVal2.getType().equals("Number"))
		{
			tmpNum1 = (MUANumber) tmpVal1;
			tmpNum2 = (MUANumber) tmpVal2;
			
			switch(opStr)
			{
				case "eq":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() == tmpNum2.getVal()));
					break;
				case "gt":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() > tmpNum2.getVal()));
					break;
				case "lt":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() < tmpNum2.getVal()));
					break;
			}
		}
		else if(tmpVal1.getType().equals("Word") && tmpVal2.getType().equals("Number"))
		{
			tmpWord1 = (MUAWord) tmpVal1;
			tmpNum1 = new MUANumber(tmpWord1.getContent());
			tmpNum2 = (MUANumber) tmpVal2;
			
			switch(opStr)
			{
				case "eq":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() == tmpNum2.getVal()));
					break;
				case "gt":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() > tmpNum2.getVal()));
					break;
				case "lt":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() < tmpNum2.getVal()));
					break;
			}
		}
		else if(tmpVal1.getType().equals("Number") && tmpVal2.getType().equals("Word"))
		{
			tmpWord2 = (MUAWord) tmpVal2;
			tmpNum2 = new MUANumber(tmpWord2.getContent());
			tmpNum1 = (MUANumber) tmpVal1;
			
			switch(opStr)
			{
				case "eq":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() == tmpNum2.getVal()));
					break;
				case "gt":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() > tmpNum2.getVal()));
					break;
				case "lt":
					resBool = new MUABool(String.valueOf(tmpNum1.getVal() < tmpNum2.getVal()));
					break;
			}
		}
		

		p.stackPush(resBool);
	}
	
	private static void opLogic(Parser p, String opStr)
	{
		MUABool tmpBool1 = null;
		MUABool tmpBool2 = null;
		MUABool resBool = null;
		
		MUAWord tmpWord1 = null;
		
		MUAValue tmpVal1;
		MUAValue tmpVal2;
		
		if(opStr.equals("not"))
		{
			tmpVal1 = p.stackPop();
			if(tmpVal1.getType().equals("Word"))
			{
				tmpWord1 = (MUAWord) tmpVal1;
				if(tmpWord1.checkConvertToBool())
				{
					tmpBool1 = new MUABool(tmpWord1.getContent());
				}
			}
			else
			{
				tmpBool1 = (MUABool) tmpVal1;
			}
			resBool = new MUABool(String.valueOf(!tmpBool1.getVal()));
		}
		else
		{
			tmpVal1 = p.stackPop();
			tmpVal2 = p.stackPop();

			if(tmpVal1.getType().equals("Word"))
			{
				MUAWord tmpWord = (MUAWord) tmpVal1;
				if(tmpWord.checkConvertToBool())
				{
					tmpBool1 = new MUABool(tmpWord.getContent());
				}
			}
			else
			{
				tmpBool1 = (MUABool) tmpVal1;
			}
			
			if(tmpVal2.getType().equals("Word"))
			{
				MUAWord tmpWord = (MUAWord) tmpVal2;
				if(tmpWord.checkConvertToBool())
				{
					tmpBool2 = new MUABool(tmpWord.getContent());
				}
			}
			else
			{
				tmpBool2 = (MUABool) tmpVal2;
			}
			
			switch(opStr)
			{
				case "and":
					resBool = new MUABool(String.valueOf(tmpBool1.getVal() && tmpBool2.getVal()));
					break;
				case "or":
					resBool = new MUABool(String.valueOf(tmpBool1.getVal() || tmpBool2.getVal()));
					break;
			}
		}
		
		p.stackPush(resBool);
	}
	
}
