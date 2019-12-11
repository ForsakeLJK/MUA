package src.mua.value;

import src.mua.dataSpace.DataSpace;

public class MUAWord extends MUAValue{
	private String content = "";
	//private String bindLocation = "";
	private boolean hasBondLoc = false;
	private boolean hasBondGlo = false;
	private MUAValue bindValLoc = null;
	private MUAValue bindValGlo = null;
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
		//if(bindLocation.equals("global"))
		//	space.deleteBond(this);
		//else if(bindLocation.equals("local"))
		localSpace.deleteBond(this);

		//bindValLoc = null;
		//hasBondLoc = false;
		
		findBond();  // reset bondVal etc. to avoid local == global 
	}
	
	public boolean checkBond()
	{
		return hasBondLoc || hasBondGlo;
	}
	
	public MUAValue fetchBindVal()
	{
		if(hasBondLoc)
			return bindValLoc;
		else if(hasBondGlo)
			return bindValGlo;
		
		return null;
	}
	
	private void findBond()
	{
		if(localSpace.inNameSpace(this)) {
			hasBondLoc = true;
			bindValLoc = localSpace.fetchVal(this);
			//bindLocation = "local";
		}
		else
		{
			hasBondLoc = false;
		}
		
		if(space.inNameSpace(this))
		{
			hasBondGlo = true;
			bindValGlo = space.fetchVal(this);
			//bindLocation = "global";
		}
		else
		{

			hasBondGlo = false;
		}
	}
	
	public void setBond(MUAValue val, DataSpace loc, DataSpace glo)
	{
		
		// set the space 
		localSpace = loc;
		space = glo;
		
		findBond();
		
		if(!hasBondLoc) // add to local
		{	
			localSpace.addBond(this, val);			
		}
		else
		{
			//if(bindLocation.equals("global"))
			//	space.replaceBond(this, val);
			//else if(bindLocation.equals("local"))
			localSpace.replaceBond(this, val);
		}

		bindValLoc = val;
		hasBondLoc = true;
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
