import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * This is the main class or entry of taking packets and convert it to an abstract modeling
 * Dependencies: Packets, APCI, ASDU, IOA, and ConstructAnalysisData classes
 * Functional Description: Given an input jason file, this class produces an array of packets as objects,
 * each object contains all packet relevant information from all applicable layers: frame, IP, TCP, 104apci, and 104asdu.
 * Plus, for each object that has 104asdu layer, than the object also contains all associated IOA's.
 * Input parameters: <json file name>
 * Output: Customized Packets Analysis data
 */
public class ReadPackets {

	private ArrayList<Packet> pkts;
	private ArrayList<Packet> malformedPkts;

	// date divider when input file is "combined"
	// Epoch time in seconds, time zone: PDT
	private static final double START_84 = 1501830000;
	private static final double END_84 = 1501916399;
	private static final double START_88 = 1502175600;
	private static final double END_88 = 1502261999;
	private static final double START_89 = 1502262000;
	private static final double END_89 = 1502348399;
	private static final double START_811 = 1502434800;
	private static final double END_811 = 1502521199;


	// Default constructor
	public ReadPackets() {
		pkts = new ArrayList<>();
		malformedPkts = new ArrayList<>();
	}

	public static void main(String[] args) {
		long start = System.nanoTime();
		BufferedReader reader = null;
		BufferedWriter writer = null;
		File inFile = null;
				
		try {
			if (args.length == 1) {
				inFile = new File(args[0]);
				if ((!inFile.exists())) {
					System.out.println("Invalid input json file, program terminted");
					System.exit(1);
				}
				reader = new BufferedReader(new FileReader(inFile));
			}else {
				System.err.println("Missing json input file, please see usage below!");
				System.err.println("\"Usage: java ReadPackets <filename.json>\"");
				System.exit(1);
			}
		
			ReadPackets readPackets = new ReadPackets();
			readPackets.parseJson(reader);
			System.out.format("*************parseJson() finished!!! Cost %.3f minutes***********\n", (float)((System.nanoTime() - start)/1000000000)/60);
			

			ConstructAnalysisData analysis = new ConstructAnalysisData(readPackets.pkts);
			ConstructAnalysisData malformedAnalysis = new ConstructAnalysisData(readPackets.malformedPkts);
			long mid = System.nanoTime();
			System.out.format("\n*******time for reading and parsing all packets is: %.3f minutes", (float)((mid - start)/1000000000)/60);
			int choice;
			Scanner input = new Scanner(System.in);

			do {
				do {
					String menu = "\n Please select an option to perform"

							+ "\n(1) ASDU types per IOA per flow"
							+ "\n(2) IOA Cluster Per RTU"
							+ "\n(3) IOA Values per IP "
							+ "\n(4) Packet Basic Statistics Analysis"
							+ "\n(5) Time Series Analysis"
							+ "\n(6) IOA Measurement Range (not precise, debugging needed here)"
							+ "\n(7) APCI component analysis"
							+ "\n(8) IOAClusterPerFlow() - generate data for unsupervised learning"
							+ "\n(9) operation on malformed packets"
							+ "\n(10) Packet level analysis"
							+ "\n(11) APDU level analysis"
							+ "\n(0) Program exit";

					System.out.println(menu);
					choice = input.nextInt();
				} while (choice < 0 || choice > 11);
				switch(choice) {

					case 1:
						analysis.constructASDUTypePerIOA(choice);
						analysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("*****IOA clusters and ASDU types per flow per ASDU address***********\n");
						analysis.writeToFile(writer);
						break;
					case 2:
						analysis.constructIOAClusterPerRTU(choice);
						analysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("***IOA's Cluster distribution sets based on \"srcIP\":\n");
						analysis.writeToFile(writer);
						break;
					case 3:
						// @XI
						// IOA values for plotting time series
						analysis.constructIOAValues(choice);
						System.out.println("Details of IOA measurements in time series are too many to print to screen. Please refer to the generated CSV");
						analysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("***IOA's values in time based on srcIP***\n");
						analysis.writeToFile(writer);
						break;
					case 4:
						analysis.constructPktStats(choice);
						break;
					case 5:
						analysis.constructTimeSeries(choice);
						break;
					case 6:
						analysis.constructIOAMeasureRange(choice);
						analysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("***IOA's value range based on srcIP***" + System.getProperty("line.separator"));
						analysis.writeToFile(writer);
						break;
					case 7:
						analysis.constructAPCIAnalysis(choice);
						break;
					case 8:
						analysis.constructIOAClusterPerFlow(choice);
						analysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("***IOA's Cluster distribution sets based on \"flow\":\n");
						analysis.writeToFile(writer);
						break;
					case 9:
						malformedAnalysis.constructMalformedFlow(choice);
						malformedAnalysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("***IOA's Cluster distribution sets based on \"flow\" for malformed packets:\n");
						malformedAnalysis.writeToFile(writer);
						break;
					case 10:
						analysis.constructPacketAnalysis(choice);;
						break;
					case 11:
						analysis.constructAPDUAnalysis(choice);
						break;
					default:
						if (choice != 0) {
							System.out.println("invalid choice!");
						}
						break;
				} // switch
			} while (choice != 0);	// outer do

			// @XI
						
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
			}
		}
		long end = System.nanoTime();
		long runningTime = (end - start) / 1000000000;
		System.out.format("Running time = %.3f minutes", (float)runningTime/60);
	}

	// @XI
	// Feature from Kelvin's original parser
	// output: txt file, log of IPs, direction, etc.
	private BufferedWriter txtGenerator (File inFile) {
		System.out.println("enter textGenerator...");
		BufferedWriter writer = null;
		try {
			FileWriter outFile;
			String outFileName = "IEC104-Analysis_";
			//System.out.println(outFileName);
			System.out.println("current JSON file is: " + inFile);
			outFileName += inFile + getDateTime() + ".txt";
			System.out.println("output log file is: " + outFileName);
			outFile = new FileWriter(outFileName);
			writer = new BufferedWriter(outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer;
	}
	
	public ArrayList<Packet> getPkts() {
		return pkts;
	}

	public void setPkts(ArrayList<Packet> pkts) {
		this.pkts = pkts;
	}

	/*
	 * String representation of the object
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
	 * Output: Array of packets with each packet element has all
	 * relevant information for each layer: frame, eth, ip, tcp, 104apci, and 104asdu
	 */
	private void parseJson(BufferedReader reader) {
		System.out.println("************* Start reading in packets... *********************");
		Packet packet = null;
		APCI apci = null;
		ASDU asdu = null;
		//ASDU asdu = new ASDU();
		IOA ioa = null;
		String line = null;
		String fieldName = "";
		String tmpStr = "";
        int curlyBraces = 0;
		Set<String> ipSet = new HashSet<>();

		Set<String> fields = new HashSet<>();
		for (PacketFieldsToParse p : PacketFieldsToParse.values()) {
			fields.add(p.getFieldName());
		}
		System.out.println("The following are the parsed fields:\n" + fields.toString());

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
				//String fldInLine = line.split(":")[0];

				// Does each line have the fields we need to get
				for (PacketFieldsToParse p : PacketFieldsToParse.values()) {
					fieldName = "";

					if (line.contains(p.getFieldName())) {
					//if (fldInLine.equals(p.getFieldName())) {
						fieldName = p.getFieldName();
						//System.out.println("Found: " + line + ", " + fieldName);
						break;
					}
				}
				if (fieldName != ""){
					// Found field, add its value to its own object
					if (fieldName.equals("frame.time_epoch")) {
						packet = new Packet();
						packet.setFieldValue(fieldName, getValue(line));
					} else if (fieldName.contains("frame") || fieldName.contains("eth") || fieldName.contains("ip")
					|| fieldName.contains("tcp")) {
						packet.setFieldValue(fieldName, getValue(line));
					}
					/*
					else if (fieldName.equals("frame.time_relative")) {
						packet.setFieldValue(fieldName, getValue(line));
					}else if (fieldName.equals("ip.src")) {
						packet.setFieldValue(fieldName, getValue(line));
						String ip = getValue(line);
						if (!ipSet.contains(ip)) {
							ipSet.add(ip);
							System.out.println(ip);
						}
					} else if (fieldName.equals("ip.dst")) {
						packet.setFieldValue(fieldName, getValue(line));
						//System.out.println(getValue(line));
					} else if (fieldName.contains("tcp")) {
						packet.setFieldValue(fieldName, getValue(line));
					}
					/*else if (fieldName.contains("frame") || fieldName.contains("eth") || fieldName.contains("ip")
							|| fieldName.contains("tcp")) {
						packet.setFieldValue(fieldName, getValue(line));

						// @XI, extract IPs involved and put into a set
						if (fieldName.equals("ip.src:")) {
							String ip = getValue(line);
							if (!ipSet.contains(ip)) {
								ipSet.add(ip);
								System.out.println(ip);
							}

						}
					} */
					else if (fieldName.equals("104asdu.start")) {
						apci = new APCI();
						packet.addApciObj(apci);
						apci.setFieldValue(fieldName, getValue(line));
					}else if (fieldName.contains("104apci.")) {
						apci.setFieldValue(fieldName, getValue(line));
					}else if (fieldName.equals("104asdu.typeid")) {
						asdu = new ASDU();
						apci.addASDU(asdu);
						asdu.setFieldName(fieldName, getValue(line));
					}else if (fieldName.equals("104asdu.sq") || fieldName.equals("104asdu.numix")
							|| fieldName.equals("104asdu.causetx") || fieldName.equals("104asdu.nega")
							|| fieldName.equals("104asdu.test") || fieldName.equals("104asdu.oa")
							|| fieldName.equals("104asdu.addr")) {
						asdu.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("IOA:")) {
						ioa = new IOA();
						asdu.addIOA(ioa);
						fieldName = "IOA";
						ioa.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("104asdu.ioa") || fieldName.equals("104asdu.float") || fieldName.equals("104asdu.vti.v") || fieldName.equals("104asdu.vti.t") || fieldName.equals("104asdu.normval") || fieldName.equals("104asdu.cp56time")
							|| fieldName.equals("104asdu.qds.ov") || fieldName.equals("104asdu.qds.bl") || fieldName.equals("104asdu.qds.sb") || fieldName.equals("104asdu.qds.nt") || fieldName.equals("104asdu.qds.iv")
							|| fieldName.equals("104asdu.qos.ql") || fieldName.equals("104asdu.qos.se") || fieldName.equals("104asdu.qoi") || fieldName.equals("104asdu.siq.spi") || fieldName.equals("104asdu.siq.bl") || fieldName.equals("104asdu.siq.sb")
							|| fieldName.equals("104asdu.siq.nt") || fieldName.equals("104asdu.siq.iv") || fieldName.equals("104asdu.diq.dpi") || fieldName.equals("104asdu.bitstring")
							|| fieldName.equals("104asdu.coi_r") || fieldName.equals("104asdu.coi_i")) {
						if (ioa == null) {
							asdu.setFieldName(fieldName, getValue(line));
						} else {
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
						pkts.add(packet);
						if (packet.isMalformed() == true) {
							malformedPkts.add(packet);
						}
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
	 * (as in IP addr) Returns the fieldValue or xx:xx:xx:xx
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

	/* @XI
	 * get timestamp to add into file name
	 */
	public static String getDateTime() {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
		df.setTimeZone(TimeZone.getTimeZone("CST"));
		return df.format(now);
	}


}
