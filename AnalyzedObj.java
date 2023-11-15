import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.Collectors;
/*
 * This is a supported class for ConstructAnalysisData.java.
 * It holds relevant packet data per packet signature that are needed to perform
 * it can also hold APDU/ASDU/IOA level depending on the fields initialized
 */
public class AnalyzedObj {
	private int tcpFlagsSyn;
	private int tcpFlagsFin;
	private int tcpFlagsReset;
	private int tcpFlagsAck;
	private double firstTimeSeen;
	private double lastTimeSeen;
	private double averageTimeDelta;
	private double epochTime;
	private double relativeTime;
	private String srcIP;
	private String dstIP;
	private String direction;
	private String rtu;
	private String server;
	private String apciFormatType;
	private String apciUType;
	private double pkt_rate;	// # of pkts per second, not counting malformed packet
	private int apduLen;
	private int numIoa;			// # of IOAs, not the measurement values
	private int numSys;
	private int numStation;		// # of common addresses
	private int numS;
	private int numU;
	private int numI;
	private int totalNumIx;
	private int numTimesReported;
	private int causetx;
	//private CntMap cotMap;
	private Map<String, Integer> cotMap;		// store the occurrence of cause-of-transmission
	private Map<String, Integer> apciUTypeMap;	// store the occurrence of APCI U types, key = type, value = occurrence count
	private Map<String, Integer> asduTypeMap;	// store the occurrence of ASDU U types, key = type, value = occurrence count
	private ArrayList<String> asduTypeArr;		// store all the occurred ASDU type IDs
	private ArrayList<String> ioaArr;
	private ArrayList<String> sysArr;
	private ArrayList<String> stationArr;
	// @XI stores IOA measurements and corresponding timings
	// map used for IOA measurements in time series, keys: IOA, values: list of IOAContent of the corresponding IOA num
	private HashMap<Integer, List<IOAContent>> ioaMap;
	// map used for IOA measurements in time series
	// key1: "system" string = asdu.addr + "-" + IOA, values: list of IOAContent of the corresponding IOA num
	// key2: Type x: asdu.addr-IOA
	private HashMap<String, List<IOAContent>> ioaMap2;
	// map used for IOA measure ranges, key: IOA, value: IOARange object
	private HashMap<Integer, IOARange> ioaRangeMap;
	// map used for IOA measure ranges, key: "system" string = asdu.addr + "-" + IOA
	private HashMap<String, IOARange> ioaRangeMap2;
	// other fields when AnalyzedObj is used for individual packet/APDU/ASDU
	private int asduType;
	private int asduAddr;
	private double apdu_rate;

	// @XI

	// Default constructor
	public AnalyzedObj() {}
	public AnalyzedObj(int choice) {
		initialize(choice);
	}

	public void initialize(int choice) {

		this.epochTime = -1.0;
		this.relativeTime = -1.0;
		this.srcIP = "";
		this.dstIP = "";
		this.apciFormatType = "";
		this.apduLen = 0;
		if (choice < 10) {
			this.rtu = "";
			this.server = "";
			this.tcpFlagsSyn = -1;
			this.tcpFlagsFin = -1;
			this.tcpFlagsAck = -1;
			this.tcpFlagsReset = -1;
			this.direction = "";
			this.firstTimeSeen = -1.0;
			this.lastTimeSeen = -1.0;
			this.averageTimeDelta = -1.0;
			this.pkt_rate = -1.0;
			this.apciUType = "";
			this.numIoa = 0;
			this.numSys = 0;
			this.numStation = 0;
			this.numS = 0;
			this.numU = 0;
			this.numI = 0;
			this.totalNumIx = 0;
			this.numTimesReported = 0;
			this.causetx = 0;
			this.cotMap = new HashMap<>();
			this.apciUTypeMap = new HashMap<>();
			this.asduTypeMap = new HashMap<>();
			this.asduTypeArr = new ArrayList<>();
			this.ioaArr = new ArrayList<>();
			this.sysArr = new ArrayList<>();
			this.stationArr = new ArrayList<>();
			// @XI
			this.ioaRangeMap = new HashMap<>();
			this.ioaRangeMap2 = new HashMap<>();
			this.ioaMap = new HashMap<>();
			this.ioaMap2 = new HashMap<>();
			this.asduType = 0;
			this.asduAddr = 0;
			this.apdu_rate = 0;
		} else if (choice == 10) {
			this.tcpFlagsSyn = -1;
			this.tcpFlagsFin = -1;
			this.tcpFlagsAck = -1;
			this.tcpFlagsReset = -1;
			this.direction = "";
			this.numIoa = 0;
			this.ioaArr = new ArrayList<>();
			this.numS = 0;
			this.numU = 0;
			this.numI = 0;
		}

	}
	// @XI

	public void setTcpFlagsAck(int tcpFlagsAck) {
		this.tcpFlagsAck = tcpFlagsAck;
	}

	public int getTcpFlagsAck() {
		return tcpFlagsAck;
	}

	public void setTcpFlagsSyn(int tcpFlagsSyn) {
		this.tcpFlagsSyn = tcpFlagsSyn;
	}

	public int getTcpFlagsSyn() {
		return tcpFlagsSyn;
	}

	public int getTcpFlagsFin() {
		return tcpFlagsFin;
	}

	public void setTcpFlagsFin(int tcpFlagsFin) {
		this.tcpFlagsFin = tcpFlagsFin;
	}

	public void setTcpFlagsReset(int tcpFlagsReset) {
		this.tcpFlagsReset = tcpFlagsReset;
	}

	public int getTcpFlagsReset() {
		return tcpFlagsReset;
	}

	public HashMap<Integer, List<IOAContent>> getIoaMap() {
		return ioaMap;
	}

	public void setIoaMap(HashMap<Integer, List<IOAContent>> ioaMap) {
		this.ioaMap = ioaMap;
	}

	public HashMap<String, List<IOAContent>> getIoaMap2() {
		return ioaMap2;
	}

	public void setIoaMap2(HashMap<String, List<IOAContent>> ioaMap2) {
		this.ioaMap2 = ioaMap2;
	}

	public HashMap<Integer, IOARange> getIoaRangeMap() {return ioaRangeMap; };

	public void setIoaRangeMap(HashMap<Integer, IOARange> ioaRangeMap) { this.ioaRangeMap = ioaRangeMap; }

	public void setIoaRangeMap2(HashMap<String, IOARange> ioaRangeMap2) {
		this.ioaRangeMap2 = ioaRangeMap2;
	}

	public HashMap<String, IOARange> getIoaRangeMap2() {
		return ioaRangeMap2;
	}

	// @XI
	// return sorted IOA array
	public ArrayList<String> getIoaArr() {
		Collections.sort(ioaArr);
		return ioaArr;
	}

	public void setIoaArr(ArrayList<String> ioaArr) {
		this.ioaArr = ioaArr;
	}

	private void addString (ArrayList<String> aList, String s) {
		if (aList == null) {
			aList = new ArrayList<String>();
		}
		aList.add(s);
	}

	public void addIOA(String ioa) {
		addString(ioaArr, ioa);
	}

	public ArrayList<String> getSysArr() {
		return sysArr;
	}

	public void setSysArr(ArrayList<String> sysArr) {
		this.sysArr = sysArr;
	}

	public void addSys(String sys) {
		addString(sysArr, sys);
	}

	public ArrayList<String> getStationArr() {
		return stationArr;
	}

	public void setStationArr(ArrayList<String> stationArr) {
		this.stationArr = stationArr;
	}

	public void addStation(String st) {
		addString(stationArr, st);
	}

	public Map<String, Integer> getApciUTypeMap() {return  apciUTypeMap;}
	public ArrayList<String> getAsduTypeArr() {
		return asduTypeArr;
	}

	public void setApciUTypeMap(Map<String, Integer> apciUTypeMap) {this.apciUTypeMap = apciUTypeMap;}
	public void setAsduTypeArr(ArrayList<String> asduTypeArr) {
		this.asduTypeArr = asduTypeArr;
	}

	public void addApciUType(String utype) {
		apciUTypeMap.put(utype, apciUTypeMap.getOrDefault(utype, 0) + 1);
	}
	public void addAsduType(String id) {
		if (asduTypeArr == null) {
			asduTypeArr = new ArrayList<String>();
		}
		asduTypeArr.add(id);
	}

	public void setCausetx(int causetx) {
		this.causetx = causetx;
	}

	public int getCausetx() {
		return causetx;
	}

	public void setCotMap(Map<String, Integer> causetxMap) {
		//this.cotMap = new CntMap(causetxMap);
		this.cotMap = causetxMap;
	}

	public Map<String, Integer> getCotMap() {
		return cotMap;
	}

	public void addCot(String c) {
		cotMap.put(c, cotMap.getOrDefault(c, 0) + 1);
	}

	// general functions operating on all types of CntMaps
	public void setCntMap(String mapType, Map<String, Integer> mapContent) {
		switch (mapType) {
			case "asduType":
				this.asduTypeMap = mapContent;
				break;
			case "causeTx":
				this.cotMap = mapContent;
				break;
			case "uType":
				this.apciUTypeMap = mapContent;
				break;

		}
	}
	public Map<String, Integer> getCntMap(String mapType) {
		switch (mapType) {
			case "asduType":
				return asduTypeMap;
			case "causeTx":
				return cotMap;
			case "uType":
				return apciUTypeMap;
		}
		return new HashMap<>();
	}

	public void updateMap(String mapType, Map<String, Integer> mapContent) {
		Map<String, Integer> oldMap = this.getCntMap(mapType);
		if (oldMap == null) {
			this.setCntMap(mapType, mapContent);
		}
		if (oldMap != null)  {
			for (String k : mapContent.keySet()) {
				//oldMap.addNItem(k, mapContent.get(k));
				oldMap.put(k, oldMap.getOrDefault(k, 0) + mapContent.get(k));
				//oldMap.put(k, oldMap.getOrDefault(k, 0) + 1);
			}
			this.setCntMap(mapType, oldMap);
		}

	}



	// @XI
	/* double packetTime;
    double measurement;

    public IOAContent(double T, double M)
	HashMap<String, List<IOAContent>> ioaMap;
    */
	public void addIOAContent(int ioa, double float_value, double time) {
		if (ioaMap.isEmpty()) {
			ioaMap = new HashMap<Integer, List<IOAContent>>();
		}
		IOAContent m = new IOAContent(time, float_value);
		if (!ioaMap.containsKey(ioa)) {
			List<IOAContent> measurements = new ArrayList<>();
			measurements.add(m);
			ioaMap.put(ioa, measurements);
		} else {
			ioaMap.get(ioa).add(m);
		}
		
	}

	public String getIoaRangeAsString() {
		String str = "[";
		for (int n : ioaRangeMap.keySet()) {
			IOARange range = ioaRangeMap.get(n);
			str += Integer.toString(n) + ":" + Double.toString(range.getMinVal()) + " to " + Double.toString(range.getMaxVal()) + ", ";
		}
		str += "]";
		return str;
	}

	public String getIoaRangeAsString2() {
		String str = "[";
		for (String n : ioaRangeMap2.keySet()) {
			IOARange range = ioaRangeMap2.get(n);
			str += n + ":" + Double.toString(range.getMinVal()) + " to " + Double.toString(range.getMaxVal()) + ", ";
		}
		str += "]";
		return str;
	}
	// @XI


	public String getArrayAsString(ArrayList<String> a) {
		String str = "";
		if (a == null || a.isEmpty()) {
			return "none";
		}
		Collections.sort(a); // For readability only - sort IOA before displaying them. Not really needed for analysis
		for (int i = 0; i < a.size(); i++) {
			str += i == 0 ? a.get(i) : " " + a.get(i);
		}

		return str;
	}

	public String getMapAsString(Map<String, Integer> inMap) {
		StringBuilder sb = new StringBuilder();
		if (inMap == null || inMap.isEmpty()) {
			return "";
		} else {
			for (String k : inMap.keySet()) {
				sb.append(k + "=" + Integer.toString(inMap.get(k)) + "-");
			}
		}

		return sb.toString();
	}
	public String getApciFormatType() {
		return apciFormatType;
	}

	public void setApciFormatType(String apciFormatType) {
		this.apciFormatType = apciFormatType;
	}

	public double getFirstTimeSeen() {
		return firstTimeSeen;
	}

	public void setFirstTimeSeen(double firstTimeSeen) {
		this.firstTimeSeen = firstTimeSeen;
	}

	public double getLastTimeSeen() {
		return lastTimeSeen;
	}

	public void setLastTimeSeen(double lastTimeSeen) {
		this.lastTimeSeen = lastTimeSeen;
	}

	public double getEpochTime() {
		return epochTime;
	}

	public void setEpochTime(double epochTime) {
		this.epochTime = epochTime;
	}

	public double getRelativeTime() {
		return relativeTime;
	}

	public void setRelativeTime(double relativeTime) {
		this.relativeTime = relativeTime;
	}

	public double getAverageTimeDelta() {
		return averageTimeDelta;
	}

	public void setAverageTimeDelta(double averageTimeDelta) {
		this.averageTimeDelta = averageTimeDelta;
	}

	public void calculatedAvergTimeDelta() {
		if (lastTimeSeen != -1.0) {
			averageTimeDelta = (lastTimeSeen - firstTimeSeen) / numTimesReported;
		} else {
			averageTimeDelta = 0.0;
		}
	}

	public String getSrcIP() {
		return srcIP;
	}

	public void setSrcIP(String srcIP) {
		this.srcIP = srcIP;
	}

	public String getDstIP() {
		return dstIP;
	}

	public void setDstIP(String dstIP) {
		this.dstIP = dstIP;
	}

	public void setRtu(String rtu) {
		this.rtu = rtu;
	}

	public String getRtu() {
		return rtu;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getServer() {
		return server;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getNumTimesReported() {
		return numTimesReported;
	}

	public void setNumTimesReported(int numTimesReported) {
		this.numTimesReported = numTimesReported;
	}

	public void incrementNumReported() {
		numTimesReported++;
	}

	public String getApciUType() {
		return apciUType;
	}

	public void setApciUType(String apciUType) {
		this.apciUType = apciUType;
	}

	public int getApduLen() {
		return apduLen;
	}

	public void setApduLen(int apduLen) {
		this.apduLen = apduLen;
	}

	public double getPkt_rate() {
		return pkt_rate;
	}

	public void setPkt_rate(double pkt_rate) {
		this.pkt_rate = pkt_rate;
	}

	public int getNumIoa() {
		return numIoa;
	}

	public void setNumIoa(int numIoa) {
		this.numIoa = numIoa;
	}

	public void setNumStation(int numStation) {
		this.numStation = numStation;
	}

	public int getNumStation() {
		return numStation;
	}

	public int getNumI() {
		return numI;
	}

	public void setNumI(int numI) {
		this.numI = numI;
	}

	public int getTotalNumIx() {
		return totalNumIx;
	}

	public void setTotalNumIx(int totalNumIx) {
		this.totalNumIx = totalNumIx;
	}

	public int getNumS() {
		return numS;
	}

	public void setNumS(int numS) {
		this.numS = numS;
	}

	public int getNumSys() {
		return numSys;
	}

	public void setNumSys(int numSys) {
		this.numSys = numSys;
	}

	public int getNumU() {
		return numU;
	}

	public void setNumU(int numU) {
		this.numU = numU;
	}

	public int getAsduType() {
		return asduType;
	}

	public void setAsduType(int asduType) {
		this.asduType = asduType;
	}

	public int getAsduAddr() {
		return asduAddr;
	}

	public void setAsduAddr(int asduAddr) {
		this.asduAddr = asduAddr;
	}

	public double getApdu_rate() {
		return apdu_rate;
	}

	public void setApdu_rate(double apdu_rate) {
		this.apdu_rate = apdu_rate;
	}

	public String printAnalysisObj(int clusterType) {
		calculatedAvergTimeDelta();
		String str = "";
		if (clusterType == 0) {
			str = String.format("%-16s %-16s %-10s %-10s %-11d %-10.5f {%s}", srcIP, dstIP, direction, apciFormatType,
					numTimesReported, averageTimeDelta, getArrayAsString(ioaArr));
		} else if (clusterType == 1) {
			str = String.format("%-16s %-10s %-10s %-11d %-10.5f ", srcIP, direction, apciFormatType,
					numTimesReported, averageTimeDelta);
		} // @XI
		else if (clusterType == 2) {
			str = String.format("%-16s %-10s %-11d %-10.5f {%s}", srcIP, direction, numTimesReported, averageTimeDelta, getArrayAsString(stationArr));
		} else if (clusterType == 3) {
			str = String.format("%-16s %-10s %-11d %-10.5f {%s}", srcIP, direction, numTimesReported, averageTimeDelta, getIoaRangeAsString2());// @XI
		} else if (clusterType == 4) {
			str = String.format("%-16s %-16s %-10s %-11d %-10.5f {%s}", srcIP, dstIP, direction, numTimesReported, averageTimeDelta, getArrayAsString(asduTypeArr));
		} else if (clusterType == 5) {
			str = String.format("%-16s %-16s %-10s %-10d %-10d %-11d {%s}", srcIP, dstIP, apciFormatType, asduAddr, asduType, numTimesReported, getArrayAsString(ioaArr));
		}
		return str;
	}

	@Override
	public String toString() {
		calculatedAvergTimeDelta();
		return "AnalysisObj [firstTimeSeen=" + firstTimeSeen + ", lastTimeSeen=" + lastTimeSeen + ", averageTimeDelta="
				+ averageTimeDelta + ", srcIP=" + srcIP + ", dstIP=" + dstIP + ", direction=" + direction
				+ ", apciFormatType=" + apciFormatType + ", numOfPackets=" + numTimesReported + ", ioaListing="
				+ getArrayAsString(ioaArr) + "]";
	}

	// used in AnalyzedAPDU.java
	// //"srcIP,dstIP,epoch_time,relative_time,APDU_Type,APDU_Length"
	public String toString1() {
		return srcIP + "," + dstIP + "," + epochTime + "," + relativeTime + "," + apciFormatType + "," + apduLen;
	}

	public String toCSVString(int clusterType) {
		calculatedAvergTimeDelta();
		// String objStr = "firstTimeSeen:" + firstTimeSeen + ",lastTimeSeen:" +
		// lastTimeSeen + ",averageTimeDelta:"
		// + averageTimeDelta + ",srcIP:" + srcIP + ",dstIP:" + dstIP +
		// ",direction:" + direction + ",apciFormatType:" + apciFormatType
		// + ",numTimesReported:" + numTimesReported + ",ioaListing:" +
		// getIoaArrayAsString();
		String objStr = "";
		if (clusterType == 3) {
			/*objStr = String.format(
					"firstTimeSeen:%.8f,lastTimeSeen:%.8f,averageTimeDelta:%.8f,srcIP:%s,dstIP:%s,direction:%s,apciFormatType:%s,numOfPackets:%d,ioaRange:%s",
					firstTimeSeen, lastTimeSeen, averageTimeDelta, srcIP, dstIP, direction, apciFormatType,
					numTimesReported, getIoaRangeAsString());
					*/
			objStr = String.format(
					"srcIP:%s, averageTimeDelta:%.8f, direction:%s,numOfPackets:%d,ioaRange:%s" + System.lineSeparator(),
					srcIP, averageTimeDelta, direction, numTimesReported, getIoaRangeAsString2());
		} else if (clusterType == 0 || clusterType == 2) {

			objStr = String.format(
					"srcIP:%s,dstIP:%s,firstTimeSeen:%.8f,lastTimeSeen:%.8f,averageTimeDelta:%.8f,numOfPackets:%d",
					srcIP, dstIP, firstTimeSeen, lastTimeSeen, averageTimeDelta, numTimesReported);
		} else if (clusterType == 1) {
			objStr = String.format(
					"srcIP:%s,averageTimeDelta:%.8f,numOfPackets:%d,numOfStation:%d,numOfSys:%d,numOfIoa:%d,stationList:%s,sysList:%s",
					srcIP, averageTimeDelta, numTimesReported, numStation, numSys, numIoa, getArrayAsString(stationArr), getArrayAsString(sysArr));
		} else if (clusterType == 4) {
			objStr = String.format(
					"rtu:%s,server:%s,firstTimeSeen:%.8f,lastTimeSeen:%.8f,averageTimeDelta:%.8f,packetCaptureRate:%.8f,numOfPackets:%d,apduLen:%d,totalNumIx:%d,numS:%d,numI:%d,numU:%d,UTypeMap:%s,asduTypeMap:%s,CauseTxMap:%s,asduTypeList:%s,numIoa:%d,sysList:%s",
					rtu, server, firstTimeSeen, lastTimeSeen, averageTimeDelta, pkt_rate, numTimesReported, apduLen, totalNumIx, numS, numI, numU, getMapAsString(apciUTypeMap), getMapAsString(asduTypeMap), getMapAsString(cotMap), getArrayAsString(asduTypeArr), numIoa, getArrayAsString(sysArr));
		} else if (clusterType == 5) {
			objStr = String.format(
					"srcIP:%s,dstIP:%s,asduAddr:%d,asduType:%d,numOfPackets:%d,firstTimeSeen:%.8f,lastTimeSeen:%.8f,averageTimeDelta:%.8f,ioaList:%s",
					srcIP, dstIP, asduAddr, asduType, numTimesReported, firstTimeSeen, lastTimeSeen, averageTimeDelta, getArrayAsString(ioaArr));
		}
		System.out.println(objStr);
		return objStr;
	}

	// @XI
	// utilize stream API to turn each object into a row in exported CSV file
	/*
	public String toCsvRow() {
		return Stream.of(srcIP, dstIP, epochTime, relativeTime, apciFormatType, apduLen)
				.map(value -> value.replaceAll("\"", "\"\""))
				.map(value -> Stream.of("\"", ",").anyMatch(value::contains) ? "\"" + value + "\"" : value)
				.collect(Collectors.joining(","));
	} */



	// @XI

}
