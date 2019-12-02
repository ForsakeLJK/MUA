package src.mua.value;

public abstract class MUAValue {
	private String type = "";
	
	public MUAValue() {};
	
	public MUAValue(String type)
	{
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}
