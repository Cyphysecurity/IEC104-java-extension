import java.util.ArrayList;

/*
 * This class represent a single 104APCI layer as an APCI object 
 * which has all the relevant fields/attributes from 104APCI layer.
 * Plus, it also has an array list that holds all 104asdu objects
 * of this particular APCI object.
 */
public class APCI {
	private int start; 
	private int apdulen;
	private int type;
	private int tx;
	private int rx;
	private int uType;
	private int numAsdu;
	ArrayList<ASDU> asduObj;
	
	// Default Constructor
	public APCI() {
		initialize();		
	}
	
	public void initialize(){
		this.start = -1;
		this.apdulen = -1;
		this.type = -1;
		this.tx = -1;
		this.rx = -1;
		this.uType = -1;
		this.numAsdu = 0;
		this.asduObj = new ArrayList<>();
	}
	/*
	 * Individual setter and getter for each data member are provided below.
	 * This methods simply provides a convenient interface (single point) 
	 * for all data members of this object
	 */
	public void setFieldValue(String name, String value) {
		//System.out.println("APCI(), setFieldValue() for " + name + " = " + value);
		switch (name) {
		case "104asdu.start": start = Integer.parseInt(value);
			break;
		case "104apci.apdulen": apdulen = Integer.parseInt(value);
			break;
		case "104apci.type": type = Integer.parseInt(value);
			break;
		case "104apci.tx": tx = Integer.parseInt(value);
			break;
		case "104apci.rx": rx = Integer.parseInt(value);
			break;
		case "104apci.utype": uType = Integer.parseInt(value, 10); 
			break;
		default: break;
		}
		
	}

	public void addASDU(ASDU asdu){
		asduObj.add(asdu);
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getApdulen() {
		return apdulen;
	}

	public void setApdulen(int apdulen) {
		this.apdulen = apdulen;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTx() {
		return tx;
	}

	public void setTx(int tx) {
		this.tx = tx;
	}

	public int getRx() {
		return rx;
	}

	public void setRx(int rx) {
		this.rx = rx;
	}
	
	public int getuType() {
		return uType;
	}

	public void setuType(int uType) {
		this.uType = uType;
	}

	public ArrayList<ASDU> getAsduObj() {
		return asduObj;
	}

	public void setAsduObj(ArrayList<ASDU> asduObj) {
		this.asduObj = asduObj;
	}
	
	public int getNumAsdu() {
		return numAsdu;
	}

	public void setNumAsdu(int numAsdu) {
		this.numAsdu = numAsdu;
	}

	@Override
	public String toString() {
		String apciStr = "APCI [start=" + start + ", apdulen=" + apdulen + ", type=" + type + ", tx=" + tx + ", rx=" + rx + ", uType=" + uType
				+ "]}\n";
		String asduStr="";
		for (ASDU u : asduObj){
			asduStr += u.toString();
		}
		
		return apciStr + asduStr ;
	}
}

