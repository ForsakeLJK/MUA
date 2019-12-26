package src.mua.value;

public class MUANumber extends MUAValue{
	private Double val = (double) 0;
	private String originalStr = null;  // unconverted to double, string
	
	public double getVal() {
		return val;
	}


	public MUANumber(String valStr)
	{
		super("Number");
		this.originalStr = valStr;
		this.val = Double.valueOf(valStr);
	}
	
	public String getOriginalStr()
	{
		return originalStr;
	}


	@Override
	public String toString() {
		return val.toString();
	}


	// no int and double
	public static void main(String[] args)
	{
		MUANumber num = new MUANumber("1234");
		System.out.println(num.toString());
		System.out.println(num.getOriginalStr());
	}
}
