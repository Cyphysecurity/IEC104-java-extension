import java.io.*;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;
import java.nio.file.Paths;
import java.nio.file.Files;

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

	private static Path curPath = Paths.get(System.getProperty("user.dir"));
	private static Path outPath = Paths.get(curPath.toString(), "output");

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
			List<Integer> choiceList = new ArrayList<>();
			System.out.println(args[0]);
			if (args.length >= 2) { // add the function choices as command line arguments
				inFile = new File(args[0]);
				try {
					for (int i = 1; i < args.length; i++) {
						int ch = Integer.valueOf(args[i]);
						if (ch < 0 || ch > 11) {
							throw new IllegalArgumentException("Only function 0 to 11 are implemented.");
						}
						choiceList.add(Integer.valueOf(ch));
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					System.exit(1);
				}


				if ((!inFile.exists())) {
					System.out.println("Invalid input json file, program terminated");
					System.exit(1);
				}
				reader = new BufferedReader(new FileReader(inFile));
			}else {
				System.err.println("Missing json input file, please see usage below!");
				System.err.println("\"Usage: java ReadPackets <filename.json>\"");
				System.exit(1);
			}
		
			ReadPackets readPackets = new ReadPackets();

			// Use parseJson() to get readPackets.pkts

			readPackets.parseJson(reader);
			System.out.format("*************parseJson() finished!!! Cost %.3f minutes***********\n", (float)((System.nanoTime() - start)/1000000000)/60);

			// Use serialized packets instead of parse onsite with parseJson()
			/*
			String serializedFilePath = Paths.get(outPath.toString(), "serialized.txt").toString();
			FileInputStream fiStream = new FileInputStream(serializedFilePath);
			ObjectInputStream oiStream = new ObjectInputStream(fiStream);
			readPackets.pkts = (ArrayList<Packet>) oiStream.readObject();
			if (readPackets.pkts != null) {
				System.out.format("*************Packets deserialization finished!!! Cost %.3f minutes***********\n", (float)((System.nanoTime() - start)/1000000000)/60);
			}
			*/
			ConstructAnalysisData analysis = new ConstructAnalysisData(readPackets.pkts);
			ConstructAnalysisData malformedAnalysis = new ConstructAnalysisData(readPackets.malformedPkts);
			long mid = System.nanoTime();
			System.out.format("\n*******time for reading and parsing all packets is: %.3f minutes\n", (float)((mid - start)/1000000000)/60);
			//int choice;
			//Scanner input = new Scanner(System.in);

			for (int choice : choiceList) {
				//do {
				// do {
					String menu = "\n Please select an option to perform"

							+ "\n(1) ASDU types per IOA per flow"
							+ "\n(2) IOA Cluster Per RTU"
							+ "\n(3) IOA Values per IP "
							+ "\n(4) Packet Basic Statistics Analysis"
							+ "\n(5) Time Series Analysis"
							+ "\n(6) Serialization and export"
							+ "\n(7) Deserialization test"
							+ "\n(8) IOAClusterPerFlow() - generate data for unsupervised learning"
							+ "\n(9) operation on malformed packets"
							+ "\n(10) IOA Measurement Range (not precise, debugging needed here)"
							+ "\n(11) APDU level analysis"
							+ "\n(0) Program exit";

					System.out.println(menu);
					//choice = input.nextInt();
				// } while (choice < 0 || choice > 11);
				System.out.println("***** Executing function " + Integer.toString(choice) + " *****");
				switch (choice) {
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
						//writer = readPackets.txtGenerator(inFile);
						//writer.write("***IOA's values in time based on srcIP***\n");
						//analysis.writeToFile(writer);
						break;
					case 4:
						analysis.constructPktStats(choice);
						break;
					case 5:
						analysis.constructTimeSeries(choice, inFile);
						break;
					case 6:
						long start6 = System.nanoTime();
						readPackets.serializePackets(readPackets.pkts, inFile);
						long end6 = System.nanoTime();
						System.out.format("\n*******time for function 6 is: %.3f minutes\n", (float)((end6 - start6)/1000000000)/60);
						break;
					case 7:
						long start7 = System.nanoTime();
						String infile = "104JavaParser_serialized_data70.ser";
						readPackets.deserializePackets(readPackets.pkts, Paths.get(outPath.toString(), infile).toString());
						long end7 = System.nanoTime();
						System.out.format("\n*******time for function 7 is: %.3f minutes\n", (float)((end7 - start7)/1000000000)/60);
						break;
					case 8:
						long start8 = System.nanoTime();
						analysis.constructFeaturePerFlow(choice);
						analysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("***Feature vector for each \"flow\":\n");
						analysis.writeToFile(writer);
						long end8 = System.nanoTime();
						System.out.format("\n*******time for function 8 is: %.3f minutes\n", (float)((end8 - start8)/1000000000)/60);
						break;
					case 9:
						malformedAnalysis.constructMalformedFlow(choice);
						malformedAnalysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("***IOA's Cluster distribution sets based on \"flow\" for malformed packets:\n");
						malformedAnalysis.writeToFile(writer);
						break;
					case 10:
						analysis.constructIOAMeasureRange(choice);
						analysis.printIOAClusterData();
						writer = readPackets.txtGenerator(inFile);
						writer.write("***IOA's value range based on srcIP***" + System.getProperty("line.separator"));
						analysis.writeToFile(writer);
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
				//} while (choice != 0);	// outer do
			}
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
			// Path curPath = Paths.get(System.getProperty("user.dir"));
			Path outPath = Paths.get(curPath.toString(), "output");
			//String outFileName = "output\\104JavaParser_";

			String outFileName = "104JavaParser_";
			//System.out.println(outFileName);
			System.out.println("current JSON file is: " + inFile);
			String inName = inFile.toString().split("\\.")[0];
			outFileName += getDateTime() + "_" + inName + ".txt";
			Path outfPath = Paths.get(outPath.toString(), outFileName);
			System.out.println("output log file is: " + outfPath);
			outFile = new FileWriter(outfPath.toString());
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
		int inPktCnt = 0;
		Packet packet = null;
		APCI apci = null;
		ASDU asdu = null;
		//ASDU asdu = new ASDU();
		IOA ioa = null;
		SIQ siq_tree = null;
		SCO sco_tree = null;
		QOS qos_tree = null;
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

		// int weirdFlag = 0; // used for debugging apduLen_tree field parsing error; figure out that is malformed packets afterwards
		try {
			// Read json file line-by-line
			while ((line = reader.readLine()) != null) {
				/*
				if (weirdFlag > 0) { weirdFlag++;}
				if (weirdFlag < 10 && weirdFlag > 0) {
					System.out.println("Previous line is weirdo. The following line is: \n" + line);
				}
				if (line.contains("iec60870_104.apdulen_tree")) {
					weirdFlag++;
					System.out.println("*****The trouble line:\n" + line);
				}
				*/

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
				String siq_str = "iec60870_asdu.siq";
				String sco_str = "iec60870_asdu.sco";
				String qos_str = "iec60870_asdu.qos";
				List<String> quaStringList = new ArrayList<>(Arrays.asList(siq_str, sco_str, qos_str));

				// Does each line have the fields we need to get
				for (PacketFieldsToParse p : PacketFieldsToParse.values()) {
					fieldName = "";

					if (line.startsWith(p.getFieldName())) {
					//if (line.split(":")[0].equals(p.getFieldName())) {
					//if (line.equals(p.getFieldName())) {
					//if (fldInLine.equals(p.getFieldName())) {
						if (quaStringList.contains(p.getFieldName())) { // encounters qualifier strings
							fieldName = getQualifierField(line, p.getFieldName(), quaStringList);
						}/*
						if (p.getFieldName().equals(siq_str)) {
							if (line.length() > siq_str.length()) { // line.substring(0, siq_str.length()).equals(siq_str) &&
								fieldName = line.split(":")[0];
							} else {
								fieldName = p.getFieldName();
							}
						} else if (p.getFieldName().equals(sco_str)) {
							if (line.length() > sco_str.length()) { // line.substring(0, siq_str.length()).equals(siq_str) &&
								fieldName = line.split(":")[0];
							} else {
								fieldName = p.getFieldName();
							}
						} */
						else if (!line.equals("tcp.flags.fin_tree")) {
							fieldName = p.getFieldName();
						}
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
					|| fieldName.startsWith("tcp")) {
						packet.setFieldValue(fieldName, getValue(line));
						/*
						if (fieldName.contains("ip") && getValue(line).equals("172.31.3.27")) {
							System.out.println(line);
						}
						 */
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
					else if (fieldName.equals("iec60870_asdu.start")) {
						apci = new APCI();
						packet.addApciObj(apci);
						apci.setFieldValue(fieldName, getValue(line));
					}else if (fieldName.contains("iec60870_104.")) {

						try{apci.setFieldValue(fieldName, getValue(line));}
						catch (NumberFormatException e) {
							System.out.println("******the line is:\n" + line + "\n**** the field name is: \n" + fieldName);
							e.printStackTrace();
						}


					}else if (fieldName.equals("iec60870_asdu.typeid")) {
						asdu = new ASDU();
						apci.addASDU(asdu);
						asdu.setFieldName(fieldName, getValue(line));
					}else if (fieldName.equals("iec60870_asdu.sq") || fieldName.equals("iec60870_asdu.numix")
							|| fieldName.equals("iec60870_asdu.causetx") || fieldName.equals("iec60870_asdu.nega")
							|| fieldName.equals("iec60870_asdu.test") || fieldName.equals("iec60870_asdu.oa")
							|| fieldName.equals("iec60870_asdu.addr") || fieldName.equals("iec60870_asdu.rawdata")) {
						asdu.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("IOA:")) {
						ioa = new IOA();
						asdu.addIOA(ioa);
						fieldName = "IOA";
						ioa.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("iec60870_asdu.ioa") || fieldName.equals("iec60870_asdu.float") || fieldName.equals("iec60870_asdu.normval") || fieldName.equals("iec60870_asdu.vti.v") || fieldName.equals("iec60870_asdu.vti.t")
							|| fieldName.equals("iec60870_asdu.siq") || fieldName.equals("iec60870_asdu.sco") || fieldName.equals("iec60870_asdu.qos") || fieldName.equals("iec60870_asdu.cp56time")
							|| fieldName.equals("iec60870_asdu.qds.ov") || fieldName.equals("iec60870_asdu.qds.bl") || fieldName.equals("iec60870_asdu.qds.sb") || fieldName.equals("iec60870_asdu.qds.nt") || fieldName.equals("iec60870_asdu.qds.iv")
							|| fieldName.equals("iec60870_asdu.qoi") || fieldName.equals("iec60870_asdu.diq.dpi") || fieldName.equals("iec60870_asdu.bitstring")
							|| fieldName.equals("iec60870_asdu.coi_r") || fieldName.equals("iec60870_asdu.coi_i")) {
						if (ioa == null) {
							asdu.setFieldName(fieldName, getValue(line));
						} else {
							ioa.setFieldName(fieldName, getValue(line));
						}
					} else if (fieldName.equals("iec60870_asdu.siq_tree")) {
						siq_tree = new SIQ();
						ioa.setSiq_tree(siq_tree);
					} else if (fieldName.equals("iec60870_asdu.siq.spi") || fieldName.equals("iec60870_asdu.siq.bl") || fieldName.equals("iec60870_asdu.siq.sb")
							|| fieldName.equals("iec60870_asdu.siq.nt") || fieldName.equals("iec60870_asdu.siq.iv")) {
						siq_tree.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("iec60870_asdu.sco_tree")) {
						sco_tree = new SCO();
						ioa.setSco_tree(sco_tree);
					} else if (fieldName.equals("iec60870_asdu.sco.on") || fieldName.equals("iec60870_asdu.sco.qu") || fieldName.equals("iec60870_asdu.sco.se")) {
						sco_tree.setFieldName(fieldName, getValue(line));
					} else if (fieldName.equals("iec60870_asdu.qos_tree")) {
						qos_tree = new QOS();
						ioa.setQos_tree(qos_tree);
					} else if (fieldName.equals("iec60870_asdu.qos.ql") || fieldName.equals("iec60870_asdu.qos.se")) {
						qos_tree.setFieldName(fieldName, getValue(line));
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
						//System.out.println(++inPktCnt);
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
			Packet lastPkt = pkts.get(pkts.size() - 1);
			//System.out.println("src:" + lastPkt.getIp_src() + ",dst:" + lastPkt.getIp_dst() + ",frame_epoch_time:" + lastPkt.getFrame_time_epoch() + "tcpflagsfin:" + lastPkt.getTcp_flags_fin()
			//+ lastPkt.toString());
			System.out.println(lastPkt.toString());
			System.out.println("fieldName: " + fieldName + "value" + getValue(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Serialization */
	private void serializePackets(ArrayList<Packet> packets, File inFile) throws IOException, ClassNotFoundException {
		System.out.println("************ Serialization Ongoing... ...");

		String outFileName = "104JavaParser_serialized";
		System.out.println("current JSON file is: " + inFile);
		String inName = inFile.toString().split("\\.")[0];
		//outFileName += getDateTime() + "_" + inName + ".txt";
		outFileName += "_" + inName + ".ser";
		Path outfPath = Paths.get(outPath.toString(), outFileName);
		System.out.println("serialization output is: " + outfPath);

		//String outFileName = "serialized.txt";
		// Serialization
		//FileOutputStream foStream = new FileOutputStream(Paths.get(outPath.toString(), outFileName).toString());
		FileOutputStream foStream = new FileOutputStream(outfPath.toString());
		ObjectOutputStream ooStream = new ObjectOutputStream(foStream);
		ooStream.writeObject(packets);
		ooStream.flush();
		ooStream.close();
		System.out.println("************ Serialization Completed ***************");
	}

	/* Deserialization test */
	private void deserializePackets(ArrayList<Packet> packets, String fileStream) throws IOException, ClassNotFoundException {
		FileInputStream fiStream = new FileInputStream(fileStream);
		ObjectInputStream oiStream = new ObjectInputStream(fiStream);
		ArrayList<Packet> pcap = (ArrayList<Packet>) oiStream.readObject();
		oiStream.close();
		System.out.println("************ Deserialization Completed ***************");
		System.out.println("Does the number of packets match?" + Boolean.toString(pcap.size() == packets.size()));
		int numApdu = 0;
		int numAsdu = 0;
		int numI = 0;
		int numS = 0;
		int numU = 0;
		int numIOA = 0;
		for (Packet p1 : packets) {
			ArrayList<APCI> apciL1 = p1.getApciObj();
			for (APCI apci : apciL1) {
				numApdu += 1;
				ArrayList<ASDU> asduL1 = apci.getAsduObj();
				if (apci.getType() == 0) numI += 1;
				else if (apci.getType() == 1) numS += 1;
				else if (apci.getType() == 3) numU += 1;
				for (ASDU asdu : asduL1) {
					numAsdu += 1;
					ArrayList<IOA> ioaL1 = asdu.getIoaObj();
					for (IOA ioa : ioaL1) {
						numIOA += 1;
					}
				}
			}
		}
		for (Packet p2 : pcap) {
			ArrayList<APCI> apciL2 = p2.getApciObj();
			for (APCI apci : apciL2) {
				numApdu -= 1;
				ArrayList<ASDU> asduL2 = apci.getAsduObj();
				if (apci.getType() == 0) numI -= 1;
				else if (apci.getType() == 1) numS -= 1;
				else if (apci.getType() == 3) numU -= 1;
				for (ASDU asdu : asduL2) {
					numAsdu -= 1;
					ArrayList<IOA> ioaL2 = asdu.getIoaObj();
					for (IOA ioa : ioaL2) {
						numIOA -= 1;
					}
				}
			}
		}

		System.out.println("Does the number of APDU match? " + Boolean.toString(numApdu == 0));
		System.out.println("Does the number of S match? " + Boolean.toString(numS == 0));
		System.out.println("Does the number of U match? " + Boolean.toString(numU == 0));
		System.out.println("Does the number of ASDU match? " + Boolean.toString(numAsdu == 0));
		System.out.println("Does the number of I match? " + Boolean.toString(numI == 0));
		System.out.println("Does the number of IOA match? " + Boolean.toString(numIOA == 0));

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

	/*
	 * utility function: make judgement for qualifier situations
	 * whether field name is the bit string or in the tree, e.g. qos v.s. qos_tree/qos.ql/qos.se
	 */
	private String getQualifierField(String line, String curField, List<String> quaStrings) {
		String result = "";
		for (String qua : quaStrings) {
			if (curField.equals(qua)) {
				result = line.length() > qua.length() ? line.split(":")[0] : curField;
				break;
			}
		}
		return result;
	}

}
