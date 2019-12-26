package src.mua.dataSpace;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import src.mua.value.MUABool;
import src.mua.value.MUAList;
import src.mua.value.MUANumber;
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

	public void clearAll() {
		dataMap.clear();
	}

	public void saveSpace(File file) {
		try {
			FileWriter saver = new FileWriter(file, true);
		
			dataMap.entrySet().forEach(entry->{try {
					saver.write("make \"" + entry.getKey() + " " + fetchStrContent(entry.getValue(), true) + " ");
			} catch (Exception e) {
					System.out.println(e.getMessage());
			}
			});

			saver.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static String fetchStrContent(MUAValue Val, boolean list_with_brackets) {
		String res = "";
		switch (Val.getType()) {
		case "Word":
			MUAWord tmpWord = (MUAWord) Val;
			res = "\"" + tmpWord.toString();
			break;
		case "Number":
			MUANumber tmpNum = (MUANumber) Val;
			res = tmpNum.getOriginalStr();
			break;
		case "Bool":
			MUABool tmpBool = (MUABool) Val;
			res = tmpBool.toString();
			break;
		case "List":
			MUAList tmpList = (MUAList) Val;
			if (list_with_brackets)
				res = tmpList.getList();
			else
				res = tmpList.toString();
			break;
		}

		return res;
	}

}
