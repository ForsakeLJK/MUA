package src.mua.value;

import src.mua.dataSpace.DataSpace;

public class MUAWord extends MUAValue{
	private String content = "";
	private String bindLocation = "";
	private boolean hasBond = false;
	private MUAValue bindVal = null;
	private DataSpace space = null;
	private DataSpace localSpace = null;
	
	// here space denotes the global space
	// use content to construct
	public MUAWord(String content, DataSpace space, DataSpace local) {
		super("Word");
		this.content = content; // let parser trim \"
		this.space = space;
		this.localSpace = local;
		findBond();  // check whether it has bond or not
	};
	
	public void eraseBond()
	{
		if(bindLocation.equals("global"))
			space.deleteBond(this);
		else if(bindLocation.equals("local"))
			localSpace.deleteBond(this);

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
		if(localSpace.inNameSpace(this)) {
			hasBond = true;
			bindVal = localSpace.fetchVal(this);
			bindLocation = "local";
		}
		else if(space.inNameSpace(this))
		{
			hasBond = true;
			bindVal = space.fetchVal(this);
			bindLocation = "global";
		}
	}
	
	public void setBond(MUAValue val)
	{
		if(!hasBond) // add to local
		{	
			localSpace.addBond(this, val);			
		}
		else
		{
			if(bindLocation.equals("global"))
				space.replaceBond(this, val);
			else if(bindLocation.equals("local"))
				localSpace.replaceBond(this, val);
		}

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
		
	}
}
