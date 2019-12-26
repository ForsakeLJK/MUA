package src.mua.value;

public class MUANumber extends MUAValue{
	private Double val = (double) 0;
	private String strVal = null;
	
	public double getVal() {
		return val;
	}


	public MUANumber(String valStr)
	{
		super("Number");
		this.strVal = valStr;
		this.val = Double.valueOf(valStr);
	}
	


	@Override
	public String toString() {
		return strVal;
	}


	// no int and double
	public static void main(String[] args)
	{
		MUANumber num = new MUANumber("1234");
		System.out.println(num.getVal());

	}
}
