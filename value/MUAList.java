package src.mua.value;

import java.util.ArrayList;
import java.util.Stack;

import src.mua.dataSpace.*;

//import java.util.ArrayList;

public class MUAList extends MUAValue{
	private String list = "";

	// return string w/ brackets
	public String getList() {
		return list;
	}
	


	public MUAList (String content)
	{
		super("List");
		list = content;
	}

	public boolean isEmpty()
	{
		String cont = list.substring(1, list.length()-1);
		if(cont.trim().isEmpty())
			return true;
		return false;
	}

	public ArrayList<MUAValue> lexListContent(DataSpace glo, DataSpace loc)
	{
		ArrayList<MUAValue> res = new ArrayList<MUAValue>();
		String cont = list.substring(1, list.length()-1).trim();
		MUAList tmpList;
		MUAWord tmpWord;

		if(cont.trim().isEmpty())
			return res;

		int off = 0;
		int next = 0;
		Stack<String> tmpStack = new Stack<String>();
		String tmpSub = ""; // temporary substring

		// skip all white spaces at beginning
		while (cont.charAt(off) == ' ')
			off++;
		/* update next */
		/* to next whitespace */
		if (off < cont.length()) 
		{
			/* if it's a list */
			if (cont.charAt(off) == '[') 
			{
				int k = off + 1;
				tmpStack.push("[");
				// find the matching ]
				while (true) 
				{
					if (k < cont.length()) 
					{
						if (cont.charAt(k) == '[')
							tmpStack.push("[");
						else if (cont.charAt(k) == ']')
							tmpStack.pop();
					}
					if (tmpStack.empty())
						break;
					else
						k++;
				}
				if (k < cont.length() - 1)
					next = k + 1; // add the entire list including [] into tokenList
				else
					next = cont.length();
			} 
			else
				next = cont.indexOf(" ", off);
		} 

		while (next != -1 || off != cont.length()) 
		{
			if (next != -1) {
				tmpSub = cont.substring(off, next);
				tmpSub = tmpSub.trim();

				if(tmpSub.charAt(0) == '[')
				{
					tmpList = new MUAList(tmpSub);
					res.add(tmpList);
				}
				else
				{
					tmpWord = new MUAWord(tmpSub, glo, loc);
					res.add(tmpWord);
				}

				/* update off */
				/* to next not-whitespace */
				off = next + 1;
				if (off < cont.length()) {
					while (cont.charAt(off) == ' ') {
						off++;
						if (off >= cont.length())
							break;
					}
				}

				/* update next */
				/* to next whitespace */
				if (off < cont.length()) 
				{
					/* if it's a list */
					if (cont.charAt(off) == '[') 
					{
						int k = off + 1;
						tmpStack.push("[");
						// find the matching ]
						while (true) {
							if (k < cont.length()) 
							{
								if (cont.charAt(k) == '[')
									tmpStack.push("[");
								else if (cont.charAt(k) == ']')
									tmpStack.pop();
							}
							if (tmpStack.empty())
								break;
							else
								k++;
						}
						if (k < cont.length() - 1)
							next = k + 1; // add the entire list including [] into res
						else
							next = cont.length();
					} 
					else
						next = cont.indexOf(" ", off);
				} 
				else
					break;
			} 
			else 
			{ // the last token
				tmpSub = cont.substring(off, cont.length());
				if (tmpSub.charAt(0) == '[') {
					tmpList = new MUAList(tmpSub);
					res.add(tmpList);
				} else 
				{
					tmpWord = new MUAWord(tmpSub, glo, loc);
					res.add(tmpWord);
				}
				off = cont.length();
			}
		}

		return res;
	}
	
	@Override
	// return string w/o brackets
	public String toString() {
		return list.substring(1, list.length()-1);
	}

	public static void main(String[] args) {
		MUAList list = new MUAList("[]");

		MUAWord tmpWord;
		MUAList tmpList;
		String tmpSub;
		DataSpace glo = new DataSpace();
		DataSpace loc = new DataSpace();
		ArrayList<MUAValue> res = list.lexListContent(glo, loc);

		for (MUAValue val : res) {
			if (val.getType() == "Word") {
				tmpWord = (MUAWord) val;
				tmpSub = tmpWord.toString();
			} else {
				tmpList = (MUAList) val;
				tmpSub = tmpList.getList();
			}

			System.out.print(tmpSub + "$");
		}
		
		System.out.print(String.valueOf(res.isEmpty()));
	}
	
}


