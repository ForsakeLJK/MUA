package src.mua.value;

import src.mua.dataSpace.DataSpace;

public class MUAWord extends MUAValue{
	private String content = "";
	private boolean hasBond = false;
	private MUAValue bindVal = null;
	private DataSpace space = null;
	
	
	// use content to construct
	public MUAWord(String content, DataSpace space) {
		super("Word");
		this.content = content; // let parser trim \"
		this.space = space;
		findBond();  // check if it has bond
	};
	
	public void eraseBond()
	{
		space.deleteBond(this);
		bindVal = null;
		hasBond = false;
	}
	
	public boolean checkBond()
	{
		return hasBond;
	}
	
	public MUAValue fetchBindVal()
	{
		return bindVal;
	}
	
	private void findBond()
	{
		if(space.inNameSpace(this)) {
			hasBond = true;
			bindVal = space.fetchVal(this);
		}
	}
	
	public void setBond(MUAValue val)
	{
		if(!hasBond)
			space.addBond(this, val);
		else
			space.replaceBond(this, val);
		bindVal = val;
		hasBond = true;
	}
	
	// check if this word can be converted to number or bool
	public boolean checkConvertToNum() {
		int i;
		boolean isConvertibleToNum = false;
		
		for(i = 0; i < content.length(); i++)
		//{
			if(!(Character.isDigit(content.charAt(i))))
			//{
				//isConvertible = false;
				break;
			//}
		//}
		if(i == content.length())
			isConvertibleToNum = true;
		return isConvertibleToNum;
	}
	
	public boolean checkConvertToBool() {
		boolean isConvertibleToBool = false;
		isConvertibleToBool = (content.equals("true")||content.equals("false"));
		return isConvertibleToBool;
	}
	
	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return content;
	}

	public static void main(String[] args)
	{
		DataSpace space = new DataSpace();
		MUAWord word = new MUAWord("1234", space);
		System.out.println("Type: "+word.getType());
		System.out.println("ConvertibleToNum: " + word.checkConvertToNum());
		System.out.println("ConvertibleToBool: " + word.checkConvertToBool());
	}
}
