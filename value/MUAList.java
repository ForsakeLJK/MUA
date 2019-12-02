package src.mua.value;

import java.util.ArrayList;

public class MUAList extends MUAValue{
	private ArrayList<MUAValue> list;

	public ArrayList<MUAValue> getList() {
		return list;
	}

	public MUAList (String content)
	{
		super("List");
		
	}
}
