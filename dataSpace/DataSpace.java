package src.mua.dataSpace;

import java.util.HashMap;

import src.mua.value.MUAValue;
import src.mua.value.MUAWord;

// 4 hashmaps (word, list, bool, number)
// use method containKey to see if a name is in the namespace
// use method get to find value
public class DataSpace {
	private HashMap<String, MUAValue> dataMap = new HashMap<String, MUAValue>();
	
	public void addBond(MUAWord key, MUAValue val)
	{
		dataMap.put(key.getContent(), val);
	}
	
	public void replaceBond(MUAWord key, MUAValue newVal)
	{
		dataMap.replace(key.getContent(), newVal);
	}
	
	public void deleteBond(MUAWord key)
	{
		dataMap.remove(key.getContent());
	}
	
	public boolean inNameSpace(MUAWord key)
	{
		
		return dataMap.containsKey(key.getContent());
	}
	
	public MUAValue fetchVal(MUAWord key)
	{
		return dataMap.get(key.getContent());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
