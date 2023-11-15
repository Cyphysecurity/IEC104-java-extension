import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

/*
 * This is the main class for ReadPackets.java to run various analysis
 * Input: ArrayList<Packet> pkts
 * Output: Customized Analysis Data
 */
public class ConstructAnalysisData {

	private int numMalformedPackets;
	private int numOfPacketsAnalyzed;
	private AnalyzeIOAClusterDistribution ioaCluster;
	// @XI
	private PktsStatistics pktStats;
	private TimeSeries timeSeries; 
	private CsvFileWriter csvFileWriter;
	private CsvFileWriter1 csvFileWriter1; // @XI
	private int clusterType;
	private HashMap<String, AnalyzedObj> analyzedDataMap;
	private ArrayList<Packet> pkts;
	private List<AnalyzedObj> apdus;
	private List<AnalyzedObj> pkts_out;
	private AnalyzedAPCI apciAnalysis;
	private AnalyzedAPDU apduAnalysis;

	// Constructor
	public ConstructAnalysisData(ArrayList<Packet> pkts) {
		this.pkts = pkts;
		//initialize();
	}

	/* (10) Packet level analysis"
	+ "\n(11) APDU level analysis" */
	public void initialize(int choice) {
		this.numMalformedPackets = 0;
		this.numOfPacketsAnalyzed = pkts.size();
		if (analyzedDataMap == null) {
			analyzedDataMap = new HashMap<>();
		} else {
			analyzedDataMap.clear();
		}
		if (choice < 10 && choice != 7) {
			this.clusterType = -1;
			this.ioaCluster = new AnalyzeIOAClusterDistribution();
			this.pktStats = new PktsStatistics();
			this.timeSeries = new TimeSeries();
			this.csvFileWriter = new CsvFileWriter();// @XI

		}
		if (choice == 7) {
			this.apciAnalysis = new AnalyzedAPCI();
		} else if (choice == 10) {
		} else if (choice == 11) {
			this.apduAnalysis = new AnalyzedAPDU();
			this.csvFileWriter = new CsvFileWriter();
			this.csvFileWriter1 = new CsvFileWriter1();
			this.apdus = new ArrayList<>();
			// @XI
		}
		// @XI

	}

	/*
	 * This method builds cluster of IOA objects all of which belong to the same group.
	 * Group membership is based on: srcIP, dstIP, ACPI format type, ASDU TypeID, and number of IOA's
	 * Future works could allow more customization as to fit the requirements of a particular analysis.
     */
	public void constructIOACluster(int choice) {
		initialize(choice);
		ioaCluster.buildIOACluster(pkts, analyzedDataMap, choice);
		numMalformedPackets = ioaCluster.getNumMalformedPackets();
		setClusterType(0);
	}

	/*
	 * This method builds cluster of IOA objects all of which belong to the same group.
	 * Group membership is based on: srcIP
     */
	public void constructIOAClusterPerRTU(int choice) {
		initialize(choice);
		ioaCluster.buildIOAClusterPerRTU(pkts, analyzedDataMap, choice);
		numMalformedPackets = ioaCluster.getNumMalformedPackets();
		setClusterType(1);
	}

	/* @XI
	 * This method extracts the measurement values in time:value pairs for all IOAs
	 * export for each DIRECTIONAL flow <srcIP, dstIP>
	 * analysis.constructIOAValues();
	 * analysis.printIOAValues();
	 */
	public void constructIOAValues(int choice) throws IOException {
		initialize(choice);
		ioaCluster.buildIOAValues(pkts, analyzedDataMap, choice);
		numMalformedPackets = ioaCluster.getNumMalformedPackets();
		setClusterType(2);
		//HashMap<String, AnalyzedObj> resultMap = analyzedDataMap;
		csvFileWriter.measurementToCsv(analyzedDataMap);
	}
	/* @XI
	 * This method extracts the measurement values in time:value pairs for all IOAs
	 * export for each NON-DIRECTIONAL flow between ip1 and ip2
	 * analysis.constructIOAValues();
	 * analysis.printIOAValues();
	 */
	public void constructIOAValues2(int choice) throws IOException {
		initialize(choice);
		ioaCluster.buildIOAValues(pkts, analyzedDataMap, choice);
		numMalformedPackets = ioaCluster.getNumMalformedPackets();
		setClusterType(2);
		//HashMap<String, AnalyzedObj> resultMap = analyzedDataMap;
		csvFileWriter.measurementToCsv(analyzedDataMap);
	}

	public void constructIOAMeasureRange(int choice) {
		initialize(choice);
		ioaCluster.buildIOARangePerRTU(pkts, analyzedDataMap, choice);
		numMalformedPackets = ioaCluster.getNumMalformedPackets();
		setClusterType(3);
	}

	// Extract feature vector
	public void constructFeaturePerFlow(int choice) {
		initialize(choice);
		//ioaCluster.extractFeaturesPerSession(pkts, analyzedDataMap, choice); // initial directional design
		ioaCluster.extractFeaturesPerFlow(pkts, analyzedDataMap, choice); // adjusted non-directional design
		numMalformedPackets = ioaCluster.getNumMalformedPackets();
		setClusterType(4);
	}

	public void constructMalformedFlow(int choice) {
		initialize(choice);
		ioaCluster.buildIOAClusterPerMalformedFlow(pkts, analyzedDataMap, choice);
		setClusterType(4);

	}

	// buildASDUTypePerFlowIoa
	public void constructASDUTypePerIOA(int choice) {
		initialize(choice);
		ioaCluster.buildASDUTypePerFlowIoa(pkts, analyzedDataMap, choice);
		setClusterType(5);
	}

	public void constructPktStats(int choice) {
		initialize(choice);
		pktStats.computeAPCIType(pkts);
		csvFileWriter.distinctIOANum(pktStats.getIoaNumSet());
		csvFileWriter.malformedIOANum(pktStats.getMalformedIoaNumSet());
		//csvFileWriter.distinctSystem(pktAnalysis.getSystemNameSet());
	}

	// Current workaround: choose the sub-function and uncomment
	public void constructTimeSeries(int choice, File inFile) {
		initialize(choice);

		/*
		 * QUALIFIER TIME SERIES
		 */
		timeSeries.generateQualifierTimeSeries(pkts);
		/*
		 * APDU TIME SERIES
		 */
		//timeSeries.generateAPDUTimeSeries(pkts);
		/*
		 * IOA TIME SERIES
		 */
		//timeSeries.generateIOATimeSeries(pkts);
		/*
		 * flow <srcIP, dstIP> incremental
		 */
		//timeSeries.flowNum(pkts);
		//HashMap<Double, String> flowMap = timeSeries.getFlowTime();
		//csvFileWriter.anyTime(flowMap, "flow");
		//csvFileWriter.continueWriter(flowMap, "flow_vs_time.csv");

		/*
		 * endpoint IP incremental
		timeSeries.ipNum(pkts);
		HashMap<String, Double> ipMap = timeSeries.getIpTime();
		csvFileWriter.anyTime(ipMap, "endpoint_ip", inFile.toString());
		System.out.println(Arrays.asList(ipMap));
		 */

		/*
		 old code for IP incremental
		 */
		//HashMap<Double, String> srcMap = timeSeries.getSrcIpTime();
		//csvFileWriter.anyTime(srcMap, "srcIP");
		//csvFileWriter.continueWriter(srcMap, "srcIP_vs_time.csv"); // if any existing csv file needs appending
		//HashMap<Double, String> dstMap = timeSeries.getDstIpTime();

		//System.out.println(Arrays.asList(srcMap)); System.out.println(Arrays.asList(dstMap));
		//csvFileWriter.ipTime(srcMap, "srcIP");
		//csvFileWriter.ipTime(dstMap, "dstIP");
		//csvFileWriter.ipTime(ipMap, "New_IP", ipSet);

		/*
		 * pkt size and rate incremental
		 */
		//timeSeries.pktSize(pkts);
		//HashMap<Double, Integer> pktSizeMap = timeSeries.getPktSizeTime();
		//csvFileWriter.pktSize(pktSizeMap);
		//timeSeries.pktNumRate(pkts);
		//HashMap<Double, Double> pktRateMap = timeSeries.getPktNumRateTime();
		//csvFileWriter.pktNumRate(pktRateMap); */

		/*
		 * APDU count and rate incremental
		 */
		//timeSeries.analyzeASDUType(pkts);
		//HashMap<Integer, ArrayList<AnalyzedASDU>> cntASDUMap = timeSeries.getAsduTypeTime();
		//csvFileWriter.analysisPerASDUType(cntASDUMap);
		//HashMap<String, ArrayList<AnalyzedObj>> apduRateMap = timeSeries.apduRatePerAsduType(pkts, choice);
		//csvFileWriter.apduRatePerAsduType(apduRateMap);

	}

	public void constructAPDUAnalysis(int choice) {
		initialize(choice);
		apduAnalysis.basics(pkts, apdus, choice);
		String h = "srcIP,dstIP,epoch_time,relative_time,APDU_Type,APDU_Length";
		csvFileWriter1.apduExport(h, apdus);
	}


	// @XI
	private void printIOAClusterHeader() {
		if (clusterType == 0) {
			System.out.println("\nIOA's Cluster distribution sets based on \"srcIP, dstIP, apciFormatType, asduTypeID, numberOfIAs, and IOA\":");
			System.out.println("----------------------------------------------------------------------------------------------------------");
			System.out.printf("%-16s %-16s %-10s %-10s %-11s %-10s %-10s\n", "srcIP", "dstIP", "Direction", "Format",
					"Occurences", "Delta(s)", "IOA's");
		} else if (clusterType == 1) {
			System.out.println("\nIOA's Cluster distribution sets based on \"srcIP\":");
			System.out.println("-------------------------------------------------");
			System.out.printf("%-16s %-10s %-10s %-11s %-10s %-10s\n", "srcIP", "Direction", "Format",
					"Occurences", "Delta(s)", "IOA's");
		} // @XI
		else if (clusterType == 2) {
			System.out.println("\nIOA's Value Cluster distribution sets based on \"srcIP\":");
			System.out.println("-------------------------------------------------");
			System.out.printf("%-16s %-10s %-10s %-11s %-10s %-10s\n", "srcIP", "Direction", "Format",
					"Occurences", "Delta(s)", "IOA's");
		} else if (clusterType == 3) {
			System.out.println("\nIOA's Value Range Cluster distribution sets based on \"srcIP\":");
			System.out.println("-------------------------------------------------");
			System.out.printf("%-16s %-10s %-11s %-10s %-10s\n", "srcIP", "Direction", "Occurences", "Delta(s)", "IOA_range");
		} else if (clusterType == 4) {
			System.out.println("\nIOA's Cluster distribution sets based on flow (srcIP, dstIP):");
			System.out.println("-------------------------------------------------");
			System.out.printf("%-16s %-16s %-10s %-10s %-11s %-10s \n", "srcIP", "dstIP", "Direction", "numReported",
					"Delta(s)", "asduTypeIds");			//		"Occurences", "Delta(s)", "IOA's");
					//srcIP, dstIP, direction, numTimesReported, averageTimeDelta, getArrayAsString(asduTypeArr)
		} else if (clusterType == 5) {
			System.out.println("\nIOA's Cluster distribution and ASDU types per flow per ASDU address ");
			System.out.println("-------------------------------------------------");
			System.out.printf("%-16s %-16s %-10s %-10s %-10s %-11s {%s}\n", "srcIP", "dstIP", "apciFormat", "asduAddr", "asduType", "numReported", "IOAs");
		} // @XI
	}

	public void printIOAClusterData() {
		int totalOccurences = 0;
		int IpAddrWithCluster = 0;
		int IpAddrWithOutCluster = 0;
		printIOAClusterHeader();
		
		for (AnalyzedObj s : getAnalyzedDataMap().values()) {
			System.out.println(s.printAnalysisObj(clusterType));
			if (!s.getIoaArr().isEmpty()){
				IpAddrWithCluster++;
			} else {
				IpAddrWithOutCluster++;
			}
			totalOccurences += s.getNumTimesReported();
		}
		System.out.println("***Summary:");
		System.out.println("***Total Occurences: " + totalOccurences);
		System.out.printf(
				"***There were: %d malformed packets which were excluded from the above data, out of a total of %d packets\n",
				numMalformedPackets, numOfPacketsAnalyzed);
		System.out.println("***There were: " + IpAddrWithCluster + " IP addresses that had IOA's and " + IpAddrWithOutCluster + " IPs without IOA cluster");
		System.out.println("***Delta(s) is average time elapsed in seconds between each occurence of packets");
		System.out.println("***Note: IOA objects were collected in the order as they appeared on the ASDUs. However for readability, they were sorted before displayed as seen above");
	}

	public void writeToFile(BufferedWriter writer) {
		for (AnalyzedObj s : getAnalyzedDataMap().values()) {
			HashMap<Integer, List<IOAContent>> ioaMap = s.getIoaMap();
			// u = new utility();
			//u.printIOAMap(ioaMap);
			//System.out.println(s.printAnalysisObj(clusterType));
			//System.out.println("now dstIP = " + s.getDstIP());
			try {
				writer.write(s.toCSVString(clusterType));
				writer.newLine();
				// @XI
				//CsvFileWriter csvWriter = new CsvFileWriter();
				//csvWriter.measurementToCsv(s);
				// @XI
			} catch (IOException e) {
				System.err.println("Class ConstructAnalysisData, writeToFile() has failed to write to output file!");
				e.printStackTrace();
			}
		}
	}
	
	public int getNumMalformedPackets() {
		return numMalformedPackets;
	}

	public void setNumMalformedPackets(int numMalformedPackets) {
		this.numMalformedPackets = numMalformedPackets;
	}

	public HashMap<String, AnalyzedObj> getAnalyzedDataMap() {
		return analyzedDataMap;
	}

	public void setAnalyzedDataMap(HashMap<String, AnalyzedObj> analyzedDataMap) {
		this.analyzedDataMap = analyzedDataMap;
	}

	public int getNumOfPacketsAnalyzed() {
		return numOfPacketsAnalyzed;
	}

	public void setNumOfPacketsAnalyzed(int numOfPacketsAnalyzed) {
		this.numOfPacketsAnalyzed = numOfPacketsAnalyzed;
	}

	public AnalyzeIOAClusterDistribution getIoaCluster() {
		return ioaCluster;
	}

	public void setIoaCluster(AnalyzeIOAClusterDistribution ioaCluster) {
		this.ioaCluster = ioaCluster;
	}

	public int getClusterType() {
		return clusterType;
	}

	public void setClusterType(int clusterType) {
		this.clusterType = clusterType;
	}

	public ArrayList<Packet> getPkts() {
		return pkts;
	}

	public void setPkts(ArrayList<Packet> pkts) {
		this.pkts = pkts;
	}

	@Override
	public String toString() {
		return "ConstructAnalysisData [numMalformedPackets=" + numMalformedPackets + ", numOfPacketsAnalyzed="
				+ numOfPacketsAnalyzed + ", ioaCluster=" + ioaCluster + ", clusterType=" + clusterType
				+ ", analyzedDataMap=" + analyzedDataMap + ", pkts=" + pkts + "]";
	}


}
