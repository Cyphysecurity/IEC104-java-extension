import java.util.ArrayList;

/*
 * This class represent a single 104ASDU layer as an ASDU object 
 * which has all the relevant fields/attributes as found from 104ASDU layer.
 * Plus, it also has an array list that holds all IOA objects
 * of this particular ASDU object.
 */
public class ASDU {
	private int type_id;
	private int sq;
	private int numix;
	private int causetx;
	private int nega;
	private int test;
	private int oa;
	private int addr;
	ArrayList<IOA> ioaObj;
	
	// Default constructor
	public ASDU() {
		initialize();
	}
	public void initialize(){
		this.type_id = -1;
		this.sq = -1;
		this.numix = -1;
		this.causetx = -1;
		this.nega = -1;
		this.test = -1;
		this.oa = -1;
		this.addr = -1;
		this.ioaObj = new ArrayList<IOA>();
	}

	/*
	 * Individual setter and getter for each data member are provided below.
	 * This methods simply provides a convenient interface (single point) 
	 * for all data members of this object
	 */
	public void setFieldName(String name, String value) {
		//System.out.println("ASDU(), setFieldValue() for " + name + " = " + value);
		switch (name) {
		case "104asdu.typeid": type_id = Integer.parseInt(value);
			break;
		case "104asdu.sq": sq = Integer.parseInt(value);
			break;
		case "104asdu.numix": numix = Integer.parseInt(value);
			break;
		case "104asdu.causetx": causetx = Integer.parseInt(value);
			break;
		case "104asdu.nega": nega = Integer.parseInt(value);
			break;
		case "104asdu.test": test = Integer.parseInt(value); 
			break;
		case "104asdu.oa": oa = Integer.parseInt(value);
			break;
		case "104asdu.addr": addr = Integer.parseInt(value);
			break;
		default: break;
		}
		
	}
	
	/*
	 * Setter and getters method
	 */
	public void addIOA(IOA ioa){
		ioaObj.add(ioa);
	}

	public int getType_id() {
		return type_id;
	}

	public void setType_id(int type_id) {
		this.type_id = type_id;
	}

	public int getSq() {
		return sq;
	}

	public void setSq(int sq) {
		this.sq = sq;
	}

	public int getNumix() {
		return numix;
	}

	public void setNumix(int numix) {
		this.numix = numix;
	}

	public int getCausetx() {
		return causetx;
	}

	public void setCausetx(int causetx) {
		this.causetx = causetx;
	}

	public int getNega() {
		return nega;
	}

	public void setNega(int nega) {
		this.nega = nega;
	}

	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}

	public int getOa() {
		return oa;
	}

	public void setOa(int oa) {
		this.oa = oa;
	}

	public int getAddr() {
		return addr;
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public ArrayList<IOA> getIoaObj() {
		return ioaObj;
	}

	public void setIoaObj(ArrayList<IOA> ioaObj) {
		this.ioaObj = ioaObj;
	}

	@Override
	public String toString() {
		String asduStr = "{ASDU [type_id=" + type_id + ", sq=" + sq + ", numix=" + numix + ", causetx=" + causetx + ", nega="
				+ nega + ", test=" + test + ", oa=" + oa + ", addr=" + addr + "]";
		String ioaStr = "";
		for (IOA a: ioaObj){
			ioaStr += a.toString();
		}
		return asduStr + ioaStr + "\n}\n";
	}
}
