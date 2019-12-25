package src.mua.value;

import java.util.ArrayList;

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

	public ArrayList<MUAValue> lexListContent()
	{
		ArrayList<MUAValue> res = new ArrayList<MUAValue>();

		return res;
	}
	
	@Override
	// return string w/o brackets
	public String toString() {
		return list.substring(1, list.length()-1);
	}
	
}
