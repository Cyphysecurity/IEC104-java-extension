import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/*
 * This is the entry (main) class of taking packets and convert them to Java objects
 * Dependencies: Packets, APCI, ASDU, IOA, and ConstructAnalysisData classes
 * Functional Description: Given an input json file, this class produces an array of packets as objects,
 * each packet object contains all packet relevant information from all applicable layers: frame, IP, TCP, 104apci, and 104asdu.
 * Plus, for each object that has 104asdu layer, than the object also contains all associated IOA's.
 *
 * Usage: java ReadPackets <json file name> <runOption>
 * 
 * Where runOption could be one of the following: (subject to change as it is still WIP at this time)
 * "1 - To construct IOA cluster for each src IP"
 * "2 - To construct IOA cluster for srcIP and ASDU address"
 * "3 - To construct APCI FormatType DOT file for State Diagram"
 * "4 - To construct APCI FormatTypes Frequency Analysis"
 * "5 - To construct IOA Accumulation Over Time"
 * "6 - To construct TCP Length Over Time"
 * "7 - To construct ASDU Cause of Transmission Analysis"
 * "8 - To construct ASDU Type ID Analysis"
 * "Default run-option is - To construct IOA cluster for each src IP"
 * 
 * Output: Customized Packets Analysis data in object forms as well as written to file,  
 * 		   and/or plot data (depends on selected option), 
 * 		    
 */
public class ReadPackets {

	private ArrayList<Packet> pkts;
	
	public ReadPackets() {
		pkts = new ArrayList<>();
	}

	public static void main(String[] args) {
		/* 
		 * Project specific - DO NOT NEED for normal parsing needed
		 * Map source IP and IOA to a particular float variable types such as I, Q, P etc..
		 */
		PhysicalValuesMapping ipIoaToFloatMap = new PhysicalValuesMapping("RTUs_PointsVariablesv2.csv");
		BufferedReader reader = null;
		BufferedWriter writer = null;
		File inFile;
		String outFileName = "";
		String inFileName = "";
		String jobName = "";
		/*
		 * Default read and write directories
		 * DO not need to specify if using the 
		 * default Java project folder i.e., "projectname/"
		 */
		//String readDir = "C:\\Users\\kkm140030\\Documents\\RA-S2018\\Traces_XM\\2017\\json";	
		String readDir = "D:\\RA-S2018\\Traces_XM\\2018-data\\json";
		// String readDir = "D:\\RA-S2018\\Traces_XM\\S-Format";
		//String readDir = "E:\\RA-S2018\\Traces_XM\\2017-data\\Misc_Debug";
		Date now;
		int runOption = 1; // Default option

		try {
			/*
			 * Validate input arguments and file
			 */
			if (args.length == 1) {
				inFileName = Paths.get(readDir).toString() + "\\" + args[0];
				inFile = new File(inFileName);
				if ((!inFile.exists())) {
					System.out.println("Invalid input json file, program terminted");
					System.exit(1);
				}

				reader = new BufferedReader(new FileReader(inFile));
				// Output file has the same name, but different extension and
				// "job function" to be appended
				outFileName = args[0];
				// runOption = Integer.parseInt(args[1]);
				System.out.println( "Processing json file: \"" + inFileName + "\" ... Please wait...");
				now = new Date();
				System.out.println("Start of parsing time: " + now.toString());
			} else {
				System.err.println("\"Usage: java ReadPackets <input_filename.json>");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

			// Start reading packet one-by-one from the user provided json file
			ReadPackets readPackets = new ReadPackets();
			readPackets.parseJson(reader);
			// Just for time keeping sake
			now = new Date();
			System.out.println("End of parsing time: " + now.toString() + ", parsed a total of: " +  readPackets.pkts.size() + " packets.");
			// Start analysis of read packets
			ConstructAnalysisData analysis = new ConstructAnalysisData(readPackets.pkts);
			/*
			 * Debug
			 */
			//System.out.println(readPackets.toString());
			
			Scanner input = new Scanner(System.in);
			do {
				do {
					String menu = "\n Please select an option to perform" + 
							  "\n(1) IoaClusterPerSrcIp"
							+ "\n(2) IoaClusterPerSrcIpAndAsduAddrAsduId" 
							+ "\n(3) UFormatPerSrcIp"
							+ "\n(4) floatByAsduId" 
							+ "\n(5) floatsPerSrcIpAsduAddr"
							+ "\n(6) floatsPerPhysicalType"
							+ "\n(7) causeTxDist"
							+ "\n(8) causeTxDistByASDUTypeID"
							+ "\n(9) timeSeriesIOAbySrcIp"
							+ "\n(10) constructNGram"
							+ "\n(0) Exit";
					System.out.println(menu);
					runOption = input.nextInt();
					
				} while (runOption < 0 || runOption > 10);
		
				
				switch (runOption) {
				case 0: System.out.println("User Terminated Program!");
				    break;
				case 1:
					// Can be customized based on jobName, the called function
					// parses the jobName to carry out the desired job
					jobName = "IoaClusterPerSrcIp";
					analysis.constructIOACluster(outFileName, jobName);
					analysis.outputIOAClusterData(jobName, writer);
					//analysis.writeToFile(writer);
					break;
				case 2:
					jobName = "IoaClusterPerSrcIpAndAsduAddrAsduId";
					//writer = getFileWriter(outFileName, jobName);
					analysis.constructIOACluster(outFileName, jobName);
					analysis.outputIOAClusterData(jobName, writer);
					//analysis.writeToFile(writer);
					break;
				case 3:
					jobName = "UFormatPerSrcIp";
					//writer = getFileWriter(outFileName, jobName);
					analysis.constructUFormatAnalysis(outFileName, jobName);
					analysis.outputIOAClusterData(jobName, writer);
					//analysis.writeToFile(writer);
					break;
				/*
				 * These cases are specific for plotting data
				 */
				case 4:
					// To produce APCI Format Types plot data
					jobName = "floatByAsduId";
					//writer = getFileWriter(outFileName, jobName);
					analysis.constructFloatVarAnalysis(outFileName, jobName, ipIoaToFloatMap);
					//analysis.outputIOAClusterData(jobName, writer);
					break;
				case 5:
					jobName = "floatsPerSrcIpAsduAddr";
					//writer = getFileWriter(outFileName, jobName);
					analysis.constructFloatVarAnalysis(outFileName, jobName, ipIoaToFloatMap);
					//analysis.outputIOAClusterData(jobName, null);
					//analysis.outputIOAClusterData(jobName, writer);
					break;
				case 6:
					jobName = "floatsPerPhysicalType";
					analysis.constructFloatVarAnalysis(outFileName, jobName, ipIoaToFloatMap);
					break;
				case 7:
					jobName = "causeTx";
					analysis.constructCOTDistributionAalysis(outFileName, jobName, ipIoaToFloatMap);
					break;	
				case 8:
					jobName = "COTByASDUTypeID";
					analysis.constructCOTDistributionAalysis(outFileName, jobName, ipIoaToFloatMap);
					break;	
				case 9:
					jobName = "timeSeriesIOAbySrcIp";
					analysis.constructIOATimeSeries(outFileName, jobName);
					analysis.outputIOAClusterData(jobName, writer);
					break;		
				case 10:
					jobName = "Gram";
					analysis.constructNGram(outFileName, jobName);
					//analysis.outputIOAClusterData(jobName, writer);
					break;	
					
				default:
					System.out.println("Invalid run option: Please use any number between 0 to 10");
				}
		try{
			if (null != reader)
				reader.close();
			if (null != writer)
				writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != reader)
					reader.close();
				if (null != writer)
					writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		now = new Date();
		System.out.println("Finished at: " + now.toString());
		} while (runOption != 0); // outer do
		input.close();
	}


	public ArrayList<Packet> getPkts() {
		return pkts;
	}

	public void setPkts(ArrayList<Packet> pkts) {
		this.pkts = pkts;
	}

	/*
	 * String representation of the object
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < pkts.size(); i++) {
			str += "#" + Integer.toString(i + 1) + pkts.get(i);
		}
		return str;
	}

	/*
	 * Input: A pcap in json format file 
	 * Output: Array of packets with each
	 * packet object has all relevant information for each layer: frame, eth,
	 * ip, tcp, 104apci, and 104asdu
	 */
	private void parseJson(BufferedReader reader) {
		Packet packet = null;
		APCI apci = null;
		ASDU asdu = null;
		IOA ioa = null;
		String line = null;
		String fieldName = "";
		String tmpStr = "";
        int curlyBraces = 0;
		try {
			// Read json file line-by-line
			while ((line = reader.readLine()) != null) {
				// The next 5 lines acts as a queue of curly braces
				// For each {, increment the queue, else decrement
				// Once queue is empty, we are done with 1 packet
				if (line.contains("{") && !line.contains("}")){
					curlyBraces++;
				}else if (line.contains("}") && !line.contains("{")){
					curlyBraces--;
				}
				line = cleanString(line);
				// Does each line have the fields we need to get?
				for (PacketFieldsToParse p : PacketFieldsToParse.values()) {
					fieldName = "";
					if (line.contains(p.getFieldName())) {
						fieldName = p.getFieldName();
						break;
					}
				}
				// Starting from lowest IP stack layers, frame, ETH, etc..
				if (fieldName != "") {
					// Found field, add its value to its own object
					if (fieldName.equals("frame.time_epoch")) {
						packet = new Packet();
						packet.setFieldValue(fieldName, getValue(line));
					} else if (fieldName.contains("frame") || fieldName.contains("eth") || fieldName.contains("ip")
							|| fieldName.contains("tcp")) {
						packet.setFieldValue(fieldName, getValue(line));
					} else if (fieldName.equals("104asdu.start")) {
						apci = new APCI();
						packet.addApciObj(apci);
						apci.setFieldValue(fieldName, getValue(line));
					} else if (fieldName.contains("104apci.")) {
						apci.setFieldValue(fieldName, getValue(line));
					} else if (fieldName.equals("104asdu.typeid")) {
						asdu = new ASDU();
						apci.addASDU(asdu);
						// New 11/3/18:
						//ioa = new IOA();
						//asdu.addIOA(ioa);
						// New
						asdu.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("104asdu.sq") || fieldName.equals("104asdu.numix")
							|| fieldName.equals("104asdu.causetx") || fieldName.equals("104asdu.nega")
							|| fieldName.equals("104asdu.test") || fieldName.equals("104asdu.oa")
							|| fieldName.equals("104asdu.addr")) {
						asdu.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("IOA:")) {
						
						ioa = new IOA();
						asdu.addIOA(ioa);
						ioa.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("104asdu.ioa") || fieldName.equals("104asdu.float")
							|| fieldName.contains("104asdu.qds") || fieldName.contains("104asdu.siq") 
							|| fieldName.contains("104asdu.vti") || fieldName.contains("104asdu.qoi")
							|| fieldName.contains("104asdu.qos") || fieldName.contains("104asdu.diq")
							|| fieldName.contains("104asdu.coi") || fieldName.contains("104asdu.cp56time:")
							|| fieldName.contains("104asdu.bitstring") || fieldName.contains("normval")) {
						if (ioa == null){
							//System.out.println(line);
						}else{
							ioa.setFieldName(fieldName, getValue(line));
						}
					} else if (fieldName.equals("malformed")) {
						packet.setMalformed(true);
						if (ioa != null && asdu != null && !asdu.getIoaObj().contains(ioa)) {
							asdu.getIoaObj().add(ioa);
							ioa = null;
						}
						if (asdu != null && apci != null && !apci.getAsduObj().contains(asdu)) {
							apci.getAsduObj().add(asdu);
							asdu = null;
						}
						apci = null;
					}
				} else if (line.isEmpty() || line.equals("]") || curlyBraces == 0 ) {
					if (packet != null) {
						//System.out.println("Added packet, line: " + line + ", curly braces = " + curlyBraces);
						pkts.add(packet);
						packet = null;
						apci = null;
						asdu = null;
						ioa = null;
						curlyBraces = 0;
					}
				} else
					continue;
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Given a string format "fieldName:fieldValue" or "fieldName:xx:xx:xx:xx"
	 * (as in IP addr) returns the fieldValue or xx:xx:xx:xx as string
	 */
	private String getValue(String line) {
		String[] tempStr = line.split(":");
		if (tempStr.length == 2) {
			return tempStr[1];
		} else {
			String l = "";
			for (int i = 1; i < tempStr.length; i++) {
				if (i == tempStr.length - 1) {
					l += tempStr[i];
				} else
					l += tempStr[i] + ":";
			}
			return l;
		}
	}

	/*
	 * Given a string format "fieldName:fieldValue" or "fieldName:xx:xx:xx:xx"
	 * (as in IP addr) return the fieldName
	 */
	private String getKey(String line) {
		return line.split(":")[0];
	}

	/*
	 * Given a string with white spaces such as leading & trailing spaces,
	 * quotes, commas, '{', '}' Returns a string without white spaces
	 */
	private String cleanString(String line) {
		line = line.trim().replaceAll("\"", "");
		line = line.replaceAll(",", "");
		if (line.contains("cp56time:")) {
			return line;
		} else {
			line = line.replaceAll(": \\{", "");
			line = line.replaceAll(" ", "");
			line = line.replaceAll("0x", "");
			return line;
		}
	}

}
