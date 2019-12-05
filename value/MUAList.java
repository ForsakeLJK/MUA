package src.mua.value;

//import java.util.ArrayList;

public class MUAList extends MUAValue{
	private String list = "";

	public String getList() {
		return list;
	}
	


	public MUAList (String content)
	{
		super("List");
		list = content;
	}
	
	@Override
	public String toString() {
		return list;
	}
	
}
