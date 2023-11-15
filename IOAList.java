/* XI
 * This object creates lists for each flow
 * list1 = Arraylist, all the IOA numbers occured in this flow
 * list2 = map, keys: IOA number, values: list of IOAContent of the corresponding IOA num
 */
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class IOAList {
    Map<Integer, List<IOAContent>> IOAMap;
    
    // Default constructor
	public IOAList() {
		initialize();
	}
	
    /*
    double packetTime;
    double measurement;

    public IOAContent(double T, double M) {
    	*/
    public IOAList(ArrayList<String> ioaArr, double firstTimeSeen) {
    	
    }

    public void initialize() {
		this.IOAMap = new HashMap<>();
	}
    public static void main(String[] args) {
    	
    }
}
