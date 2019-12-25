package src.mua.value;

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
	
	@Override
	// return string w/o brackets
	public String toString() {
		return list.substring(1, list.length()-1);
	}
	
}
