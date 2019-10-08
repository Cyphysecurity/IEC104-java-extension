import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GenericObj<K, V>{
	/*
	 * keyValuePairMap can be used to hold for examples:
	 * <packetSignature:Array of IOA list>, or
	 * <IOACount:time> 
	 * which is a <key:value> map, where key and value could be any object type, 
	 * as long as neither key nor value is of any primitive type such as int, char etc..
	 * 
	 * For the examples above, then to instantiate a GenericObj, one would do:
	 * GenericObj<String, Array<Integer>> genObj = new GenericObj<>(); 
	 * Which indicates that packetSignature is a String object, and 
	 * IOA list is an array list.
	 * 
	 */
	private TreeMap<K, V> keyValuePairMap;
	// Default constructor
	GenericObj(){
		this.keyValuePairMap = new TreeMap<>();
	}
	// Setters, Getters, and utility methods
	public TreeMap<K, V> getKeyValuePairMap() {
		return keyValuePairMap;
	}

	public void setKeyValuePairMap(TreeMap<K, V> keyValuePairMap) {
		this.keyValuePairMap = keyValuePairMap;
	}

	public V getValueFromMapWithKey(K key){
		return keyValuePairMap.get(key);
	}
	
	public void putKeyValuePairIntoMap(K key, V value){
		keyValuePairMap.put(key, value);
	}
	
	public boolean isKeyInMap(K key) {
		if (keyValuePairMap.containsKey(key))
			return true;
		else
			return false;
	}
	/*
	 * Write line by line to designated file 
	 * where each line is <key:value>, with an option to print
	 * the same line to console
	 */
	public void writeToFile(BufferedWriter writer, boolean printOption, boolean reverseKeyValue) {
		String str;
		for (Map.Entry<K, V> entry : keyValuePairMap.entrySet()) {
			if (!reverseKeyValue){
				str = String.format("%d:%.6f\n", entry.getKey(), entry.getValue());
			}else{
				str = String.format("%d:%.6f\n", entry.getValue(), entry.getKey());
			}
			try {
				writer.write(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (printOption){
				System.out.print(str);
			}	
		}
	}
	
	public String toStringCSV() {
		String str = "";
		for (Map.Entry<K, V> entry : keyValuePairMap.entrySet()) {
			str += String.format("%d:%.6f\n", entry.getKey(), entry.getValue());
		}
        return str;
	}

	@Override
	public String toString() {
		return "GenericObj [keyValuePairMap=" + keyValuePairMap + "]";
	}
	
	
}
