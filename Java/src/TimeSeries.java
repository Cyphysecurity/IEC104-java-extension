

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
/*
 * @XI
 * This class generates time series data group (and plots) for pkts, similar to AnalyzeIOAClusterDistribution
 * it generates the analysis results to feed into ConstructAnalysisData.java
 * Currently, it supports the following analysis:
 * 1. generate time series for new source and destination IPs
 * 2. pkt size in time series
 * 3. pkt number captured per second in time series
 * more can be added as needed
 */

public class TimeSeries {
	private double startT;			// store start time 
	private HashSet<String> srcIP;	// store existed source IP
	private HashSet<String> dstIP;	// store existed dest IP
	private HashSet<String> ip;		// existed any IP
	private HashSet<String> flowSet;	// existed flows
	private HashMap<Double, String> srcIpTime; 		// key = time, value = number of src IP occurred so far
	private HashMap<Double, String> dstIpTime; 		// key = time, value = number of dst IP occurred so far
	private HashMap<Double, String> ipTime;			// src/dst IP occurred so far
	private HashMap<Double, String> flowTime;		// key = time, value = flow (srcIP,dstIP)
	private HashMap<Double, Integer> pktSizeTime;		// value = packet size
	private HashMap<Double, Double> pktNumRateTime;	// value = number of packets / second
	private HashMap<Integer, ArrayList<AnalyzedASDU>> asduTypeTime;	// key = ASDU Type, value = list of analysis based on this type, <Time, Cnt,...>
	private int srcCnt;	// count distinct src IPs
	private int dstCnt;	// count distinct dst IPs
	private int ipCnt;	// count distinct IPs
	private int pktCnt; // count packet number

	public TimeSeries() {
		initialize();
	}

	public void initialize() {
		srcIP = new HashSet<>();
		dstIP = new HashSet<>();
		ip = new HashSet<>();
		flowSet = new HashSet<>();
		srcIpTime = new HashMap<>();
		dstIpTime = new HashMap<>();
		ipTime = new HashMap<>();
		flowTime = new HashMap<>();
		pktSizeTime = new HashMap<>();
		pktNumRateTime = new HashMap<>();
		asduTypeTime = new HashMap<>();
		srcCnt = 0;
		dstCnt = 0;
		ipCnt = 0;
		startT = 0.0;
		pktCnt = 0;

	}


	// 1. generate time series for new source and destination IPs
	public void ipNum(ArrayList<Packet> pkts) {
		srcIpTime.put(0.0, "");
		dstIpTime.put(0.0, "");
		ipTime.put(0.0, "");
		System.out.println("*********start calculate IP in time*******");
		for (Packet p : pkts) {
			String src = p.getIp_src();
			String dst = p.getIp_dst();
			double t = p.getFrame_time_epoch();

			// if IP not occurred before, add into set
			if (!srcIP.contains(src)) {
				srcIP.add(src);
				//srcCnt++;
				srcIpTime.put(t, src);
			}
			if (!dstIP.contains(dst)) {
				dstIP.add(dst);
				//dstCnt++;
				dstIpTime.put(t, dst);
			}

			if (!ip.contains(src)) {
				//ipCnt++;
				ip.add(src);
				ipTime.put(t, src);
			}
			if (!ip.contains(dst)) {
				//ipCnt++;
				ip.add(dst);
				ipTime.put(t, dst);
			}


			//System.out.format("Packet from %s to %s\n", src, dst);

		} // for packet
		
	}

	// 1.1 generate time series for new flows (srcIP, dstIP)
	public void flowNum(ArrayList<Packet> pkts) {
		flowTime.put(0.0, "");
		System.out.println("*********start calculate flow in time*******");
		for (Packet p : pkts) {
			String src = p.getIp_src();
			String dst = p.getIp_dst();
			String flow = src + "-" + dst;
			double t = p.getFrame_time_epoch();

			// if IP not occurred before, add into set
			if (!flowSet.contains(flow)) {
				flowSet.add(flow);
				flowTime.put(t, flow);
			}

			//System.out.format("Packet from %s to %s\n", src, dst);
		} // for packet

	}


	// 2. generate time series for individual packet size
	public void pktSize(ArrayList<Packet> pkts) {
		pktSizeTime.put(0.0, 0);
		for (Packet p : pkts) {
			double t = p.getFrame_time_epoch();
			int pktSize = p.getFrame_len();

			pktSizeTime.put(t, pktSize);
		} // for packet
	}

	// 3. generate time series for packet # / second
	public void pktNumRate(ArrayList<Packet> pkts) {
		pktNumRateTime.put(0.0, 0.0);
		for (Packet p : pkts) {
			pktCnt++;
			double t = p.getFrame_time_relative();
			double pktRate = pktCnt / t; 

			pktNumRateTime.put(t, pktRate);
		} // for packet
	}

	/* 4. generate time series for APDU # / second and APDU length
	 * for each ASDU type per flow
	 * result<K, V>: key = signature, value = AnalyzedObj
	 * signature = src,dst,typeId
	 */
	public HashMap<String, ArrayList<AnalyzedObj>> apduRatePerAsduType(ArrayList<Packet> pkts, int choice) {
		HashMap<String, ArrayList<AnalyzedObj>> result = new HashMap<>();
		AnalyzedObj aObj;
		String signature = "";
		HashMap<String, Double> firstTimeMap = new HashMap<>();	// store the firstTimeSeen for each signature
		HashMap<String, Integer> apduCntMap = new HashMap<>();  // store apduCnt for each signature
		for (Packet p : pkts) {
		    /*
			if (p.isMalformed()) {
				continue;
			}
			*/

			String src = p.getIp_src();
			String dst = p.getIp_dst();
			double epochT = p.getFrame_time_epoch();
			for (APCI a : p.getApciObj()) {
				aObj = new AnalyzedObj(choice);
				aObj.setApduLen(a.getApdulen());
				if (a.getType() == 0) {        // filter I-type APCI
					for (ASDU asdu : a.getAsduObj()) {

						int asdu_type = asdu.getType_id();
						signature = src + "," + dst + "," + Integer.toString(asdu_type);
						aObj.setAsduType(asdu_type);
						aObj.setSrcIP(src);
						aObj.setDstIP(dst);
						aObj.setEpochTime(epochT);
						if (!result.containsKey(signature)) { // adding 1st
							aObj.setApdu_rate(0);
							aObj.setNumI(1);	// # of I = # of APDU in certain ASDU type
							firstTimeMap.put(signature, epochT);
							apduCntMap.put(signature, 1);
							result.put(signature, new ArrayList<AnalyzedObj>(Arrays.asList(aObj)));
						} else {			 // this signature's been recorded before
							ArrayList<AnalyzedObj> existedList = result.get(signature);		// get current data of this signature
							apduCntMap.put(signature, apduCntMap.get(signature) + 1);		// update apdu#
							aObj.setNumI(apduCntMap.get(signature));
							aObj.setApdu_rate(aObj.getNumI() / (epochT - firstTimeMap.get(signature)));
							existedList.add(aObj);
						}
						aObj = null;

					} // for asdu
				}
			} // for apci

		} // for packet


		return result;
	}


	/* 5. generate two sets and time series based on ASDU type
	 * generate the followings:
	 * distinct IOA set
	 * distinct "system" set
	 * ASDU and "system" count and count rate per type in time series
	 */

	public void analyzeASDUType(ArrayList<Packet> pkts) {
		double t = 0.0;			// packet time
		double t_init = 0.0;	// time of the first AnalyzedASDU object in asduList
		double t_last = 0.0;	// time of the last AnalyzedASDU object in asduList
		int asduType = -1;
		int asduCnt = 0;
		double asduCntRate = 0.0;
		int asduAddr = -1;
		int ioaNum = -1;
		AsduSystem system = new AsduSystem();
		int sysCnt = -1;
		double sysCntRate = -1.0;

		ArrayList<AnalyzedASDU> asduList; // store analysis based on ASDU,
		ArrayList<AsduSystem> sysList;
		ArrayList<IOA> ioaList;
		for (Packet p : pkts) {
			t = p.getFrame_time_relative();
			for (APCI a : p.getApciObj()) {
				if (a.getType() == 0) {		// filter I-type APCI
					for (ASDU asdu : a.getAsduObj()) {
						asduType = asdu.getType_id();
						asduAddr = asdu.getAddr();
						asduList = new ArrayList<>();

						if (!asduTypeTime.containsKey(asduType)) {	// first occurrence
							// update ASDU count and rate
							asduCnt = 1;
							asduCntRate = 0.0;
							ioaList = new ArrayList<>();
							sysList = new ArrayList<>();
							sysCnt = 1;
							sysCntRate = 0.0;
							// update IOA and system related variables
							for (IOA x : asdu.getIoaObj()) {
								ioaNum = x.getIoa();
								ioaList.add(x);
								system = new AsduSystem(asduAddr, ioaNum);
								sysList.add(system);
								sysCnt++;
								if (t != 0) { sysCntRate = sysCnt / t; } else { sysCntRate = 0;}
							}
							asduList.add(new AnalyzedASDU(t, asduCnt, asduCntRate, sysList, sysCnt, sysCntRate, ioaList));
							asduTypeTime.put(asduType, asduList);

						} else {	// this ASDU type already existed in asduTypeTime
							asduList = asduTypeTime.get(asduType);							// get current time series list of AnalyzedASDU objects for this type
							t_last = asduList.get(asduList.size() - 1).getTime();			// get the last object's time
                            //t_init = asduList.get(0).getTime();                             // get the first object's time
                            // ASDU count and count rate update
                            asduCnt = asduList.get(asduList.size() - 1).getAsduCnt() + 1;
                            asduCntRate = asduCnt / t;
                            // update IOAlist and sysList
                            ioaList = asduList.get(asduList.size() - 1).getIoaList();
                            sysCnt = asduList.get(asduList.size() - 1).getSysCnt();
                            sysCntRate = asduList.get(asduList.size() - 1).getSysCntRate();
                            sysList = asduList.get(asduList.size() - 1).getSystemList();
                            for (IOA x : asdu.getIoaObj()) {
                                ioaNum = x.getIoa();
                                if (!isIoaMember(x, ioaList)) {   // if this ioa first occurrence
                                    ioaList.add(x);
                                }
                                system = new AsduSystem(asduAddr, ioaNum);
                                if (!isSysMember(system, sysList)) {
                                    sysList.add(system);
                                    sysCnt++;
                                    sysCntRate = sysCnt / t;
                                }
                            }

                            if (t == t_last) {      // this packet contains multiple ASDUs
                                asduList.get(asduList.size() - 1).setAsduCnt(asduCnt);
                                asduList.get(asduList.size() - 1).setAsduCntRate(asduCntRate);
                                asduList.get(asduList.size() - 1).setIoaList(ioaList);
                                asduList.get(asduList.size() - 1).setSysCnt(sysCnt);
                                asduList.get(asduList.size() - 1).setSysCntRate(sysCntRate);
                                asduList.get(asduList.size() - 1).setSystemList(sysList);
                            } else if (t > t_last) {    // this is a new packet, new time
                                asduList.add(new AnalyzedASDU(t, asduCnt, asduCntRate, sysList, sysCnt, sysCntRate, ioaList));  // create a new node in the list
                            } else if (t < t_last) {
                                System.out.println("time error in arraylist of AnalyzedASDU!!!");
                            }

                            asduTypeTime.put(asduType, asduList);
						} // else

					} // for ASDU
				}
			} // for APCI
		} // for PACKET
	}


	public void setSrcIpTime(HashMap<Double, String> srcIpTime) {
		this.srcIpTime = srcIpTime;
	}

	public HashMap<Double, String> getSrcIpTime() {
		return srcIpTime;
	}

	public void setDstIpTime(HashMap<Double, String> dstIpTime) {
		this.dstIpTime = dstIpTime;
	}

	public HashMap<Double, String> getDstIpTime() {
		return dstIpTime;
	}
	public void setIpTime(HashMap<Double, String> ipTime) {
		this.ipTime = ipTime;
	}
	public HashMap<Double, String> getIpTime() {
		return ipTime;
	}

	public HashSet<String> getIp() {
		return ip;
	}

	public HashMap<Double, String> getFlowTime() {
		return flowTime;
	}


	public void setPktSizeTime(HashMap<Double, Integer> pktSizeTime) {
		this.pktSizeTime = pktSizeTime;
	}

	public HashMap<Double, Integer> getPktSizeTime() {
		return pktSizeTime;
	}

	public void setPktNumRateTime(HashMap<Double, Double> pktNumRateTime) {
		this.pktNumRateTime = pktNumRateTime;
	}

	public HashMap<Double, Double> getPktNumRateTime() {
		return pktNumRateTime;
	}

	public void setAsduTypeTime(HashMap<Integer, ArrayList<AnalyzedASDU>> asduTypeTime) {
		this.asduTypeTime = asduTypeTime;
	}

	public HashMap<Integer, ArrayList<AnalyzedASDU>> getAsduTypeTime() {return asduTypeTime; }

    // instanceof relation to identify whether ioaList contains certain IOA object
    public boolean isIoaMember(IOA x, ArrayList<IOA> y) {
        for (IOA z : y) {
            if (z.getIoa() == x.getIoa()) {
                return true;
            }
        }
        return false;
    }

    // instanceof for sysList
    public boolean isSysMember(AsduSystem s, ArrayList<AsduSystem> sList) {
	    for (AsduSystem x : sList) {
	        if (s.toString().equals(x.toString())) {
	            return true;
            }
        }
        return false;
    }

}
