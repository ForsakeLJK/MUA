package src.mua.value;

public class MUABool extends MUAValue{
	private Boolean val = false;

	public boolean getVal() {
		return val;
	}
	

	public MUABool(String valStr)
	{
		super("Bool");
		this.val = (valStr.equals("true"));
	}
	
	@Override
	public String toString() {
		return val.toString();
	}
}
