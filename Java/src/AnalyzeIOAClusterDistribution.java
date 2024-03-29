import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Analysis class to perform IOA cluster distribution 
 * This class performs the required analysis as given by the user.
 * Currently, it supports two analysis: IOA cluster per RTU only 
 * and IOA cluster per srcIP, dstIP, APCI format type, asdu TypeID, number of IOAs
 * More can be added as needed.
 * 
 */
public class AnalyzeIOAClusterDistribution implements APCIControlFieldType, ASDUType {
    /*
     * packetSignature defines a unique pattern from which the analysis
     * is based on; e.g. packetSignature could be sourceIP + destIP + APCI type + ASDU Type.
     * Or, packetSignature could just be sourcePI or APCI Type.
     * In other words, whichever ways we may define it to be in order to match a specific
     * type of packets together for the analysis
     */
	private String packetSignature;
	private int numMalformedPackets;
	/*
	 * analysisObj represents a group of packets that matched the specific 
	 * packetSignature as described above.
	 */
	private AnalyzedObj analysisObj;

	// Constructor
	public AnalyzeIOAClusterDistribution() {
		initialize();
	}

	public void initialize() {
		this.packetSignature = "";
		this.numMalformedPackets = 0;
		this.analysisObj = null;
	}

	public String getPacketSignature() {
		return packetSignature;
	}

	public void setPacketSignature(String packetSignature) {
		this.packetSignature = packetSignature;
	}

	public int getNumMalformedPackets() {
		return numMalformedPackets;
	}

	public void setNumMalformedPackets(int numMalformedPackets) {
		this.numMalformedPackets = numMalformedPackets;
	}

	public AnalyzedObj getAnalysisObj() {
		return analysisObj;
	}

	public void setAnalysisObj(AnalyzedObj analysisObj) {
		this.analysisObj = analysisObj;
	}

	/*
	 * Construct IOA cluster data based on: srcIP, dstIP, APCI format type, 
	 * asdu TypeID, and number of IOAs
	 */
	public void buildIOACluster(ArrayList<Packet> pkts, HashMap<String, AnalyzedObj> analyzedDataMap, int choice) {
		String srcIP = "";
		String dstIP = "";

		for (Packet p : pkts) {
			if (p.isMalformed()) {
				numMalformedPackets += 1;
				continue;
			}

			analysisObj = new AnalyzedObj(choice);
			srcIP = p.getIp_src();
			analysisObj.setSrcIP(srcIP);
			dstIP = p.getIp_dst();
			analysisObj.setDstIP(dstIP);
			packetSignature = srcIP + dstIP;
			
			for (APCI a : p.getApciObj()) {
				packetSignature += "," + Integer.toString(a.getType());
				if (a.getType() == I_FORMAT) {
					analysisObj.setApciFormatType("I-FORMAT");
					analysisObj.setDirection("C->S");
				} else if (a.getType() == S_FORMAT) {
					analysisObj.setApciFormatType("S-FORMAT");
					analysisObj.setDirection("S->C");
				} else if (a.getType() == U_FORMAT) {
					analysisObj.setApciFormatType("U-FORMAT");
					if (a.getuType() == TESTFR_CON) {
						analysisObj.setApciUType("TESTFR_CON");
						analysisObj.setDirection("C->S");
					} else if (a.getuType() == TESTFR_ACT) {
						analysisObj.setApciUType("TESTFR_ACT");
						analysisObj.setDirection("S->C");
					}
				} else {
					analysisObj.setApciFormatType("Invalid Format");
					//Skip ASDU since this may be a bad APCI
					continue;
				}

				for (ASDU u : a.getAsduObj()) {
					packetSignature += "," + Integer.toString(u.getIoaObj().size()) + ","
							+ Integer.toString(u.getCausetx());
					for (IOA i : u.getIoaObj()) {
						String ioa = Integer.toString(i.getIoa());
						packetSignature += "," + ioa;
						analysisObj.addIOA(ioa);// NEW
					}
				}
			}

			if (!analyzedDataMap.containsKey(packetSignature)) {
				analysisObj.incrementNumReported();
				analysisObj.setFirstTimeSeen(p.getFrame_time_epoch());
				analyzedDataMap.put(packetSignature, analysisObj);
			} else {
				analyzedDataMap.get(packetSignature).incrementNumReported();
				analyzedDataMap.get(packetSignature).setLastTimeSeen(p.getFrame_time_epoch());
			}
			analysisObj = null;
		}
	}

	/*
	 * Construct IOA cluster data per srcIP/RTU only
	 */
	public void buildIOAClusterPerRTU(ArrayList<Packet> pkts, HashMap<String, AnalyzedObj> analyzedDataMap, int choice) {
		String srcIP = "";
		String dstIP = "";
		ArrayList<String> ioaList;
		int ioa;

		for (Packet p : pkts) {
			if (p.isMalformed()) {
				numMalformedPackets += 1;
				continue;
			}

			analysisObj = new AnalyzedObj(choice);
			ioaList = new ArrayList<>();
			srcIP = p.getIp_src();
			analysisObj.setSrcIP(srcIP);
			dstIP = p.getIp_dst();
			analysisObj.setDstIP(dstIP);
			packetSignature = srcIP;

			for (APCI a : p.getApciObj()) {
				if (a.getType() == I_FORMAT) {
					analysisObj.setApciFormatType("I-FORMAT");
					analysisObj.setDirection("C->S");
				}else if (a.getType() == S_FORMAT) {
					analysisObj.setApciFormatType("S-FORMAT");
					analysisObj.setDirection("S->C");
				}else if (a.getType() == U_FORMAT) {
					analysisObj.setApciFormatType("U-FORMAT");
					if (a.getuType() == TESTFR_CON){
						analysisObj.setApciUType("TESTFR_CON");
						analysisObj.setDirection("C->S");
					}else if (a.getuType() == TESTFR_ACT) {
						analysisObj.setApciUType("TESTFR_ACT");
						analysisObj.setDirection("S->C");
					}
				}else {
					analysisObj.setApciFormatType("Invalid Format");
					continue;
				}
				for (ASDU u : a.getAsduObj()) {
					for (IOA i : u.getIoaObj()) {
						ioa = i.getIoa();
						ioaList.add(Integer.toString(ioa));
					}
				}
			}

			if (!analyzedDataMap.containsKey(packetSignature)) {
				analysisObj.incrementNumReported();
				analysisObj.setFirstTimeSeen(p.getFrame_time_epoch());
				if (!ioaList.isEmpty()) {
					analysisObj.setIoaArr(ioaList);
				}
				analyzedDataMap.put(packetSignature, analysisObj);
			} else {
				AnalyzedObj aObj = analyzedDataMap.get(packetSignature);
				aObj.incrementNumReported();
				aObj.setLastTimeSeen(p.getFrame_time_epoch());
				if (aObj.getIoaArr() == null) {
					aObj.setIoaArr(ioaList);
				} else {
					for (String s : ioaList) {
						if (!aObj.getIoaArr().contains(s)) {
							aObj.addIOA(s);
						}
					}
				}
			}
			// Set analysisObj to null preparing for next packet
			analysisObj = null;
		}
	}

	// @XI
	// construct IOA/"system" cluster data per flow <srcIP, dstIP>
	// can be used later for unsupervised learning in clustering
	// one tuple =
	// <flow direction, #packet, firstSeenTime, lastSeenTime, sum of lengths of all APDUs(payload size),
	// # of distinct IOAs, # of distinct "systems", # of S_APDUs, U_APDUs, I_APDUs,
	// list of distinct IOAs, list of distinct "systems">
	// direction: 1 = from central to outstation, 0 = from outstation to central

	public void buildIOAClusterPerFlow(ArrayList<Packet> pkts, HashMap<String, AnalyzedObj> analyzedDataMap, int choice) {
		String srcIP = "";
		String dstIP = "";
		double pktNum;
		double pktRate;
		ArrayList<String> asduTypeList;
		ArrayList<String> ioaList;
		ArrayList<String> sysList;
		int asduType;
		int ioa;
		int common_addr;

		for (Packet p : pkts) {
			if (p.isMalformed()) {
				numMalformedPackets += 1;
				continue;
			}


			analysisObj = new AnalyzedObj(choice);
			asduTypeList = new ArrayList<>();
			ioaList = new ArrayList<>();
			sysList = new ArrayList<>();
            int numS = 0; // number of S,U,I types all for APDUs
            int numU = 0;
            int numI = 0;

			srcIP = p.getIp_src();
			/* locate missing flows......
			if (srcIP.equals("192.168.111.34") || srcIP.equals("192.168.111.40") || srcIP.equals("192.168.111.79") || srcIP.equals("192.168.111.45") ||srcIP.equals("192.168.111.46") ||srcIP.equals("192.168.111.47") ||srcIP.equals("192.168.111.48")) {
				System.out.format("srcIP is: %s", srcIP);
			} */
			analysisObj.setSrcIP(srcIP);
			dstIP = p.getIp_dst();
			analysisObj.setDstIP(dstIP);
			packetSignature = srcIP + dstIP;
			// flows coming from central station, set as 1; from outstation, set as 0
			if (srcIP.equals("192.168.250.2") || srcIP.equals("192.168.250.1") || srcIP.equals("192.168.250.3") || srcIP.equals("192.168.250.4")) {
                analysisObj.setDirection("1");
            } else {
                analysisObj.setDirection("0");
            }
            analysisObj.setApduLen(p.getTcp_pdu_size() - 2);
			for (APCI a : p.getApciObj()) {
				if (a.getType() == I_FORMAT) {
					analysisObj.setApciFormatType("I-FORMAT");
					numI++;
				}else if (a.getType() == S_FORMAT) {
					analysisObj.setApciFormatType("S-FORMAT");
					numS++;
				}else if (a.getType() == U_FORMAT) {
					analysisObj.setApciFormatType("U-FORMAT");
					numU++;
					if (a.getuType() == TESTFR_CON){
						analysisObj.setApciUType("TESTFR_CON");
					}else if (a.getuType() == TESTFR_ACT) {
						analysisObj.setApciUType("TESTFR_ACT");
					} else if (a.getuType() == STOPDT_ACT) {
					    analysisObj.setApciUType("STOPDT_ACT");
                    } else if (a.getuType() == STOPDT_CON) {
					    analysisObj.setApciUType("STOPDT_CON");
                    } else if (a.getuType() == STARTDT_ACT) {
					    analysisObj.setApciUType("STARTDT_ACT");
                    } else if (a.getuType() == STOPDT_CON) {
					    analysisObj.setApciUType(("STARTDT_CON"));
                    }
				}else {
					analysisObj.setApciFormatType("Invalid Format");
					continue;
				}
				for (ASDU u : a.getAsduObj()) {
					asduType = u.getType_id();
					/*
					if (asduType == 50) {
						System.out.format("*********TYPE %d CAUGHT!!!\n", asduType);
						System.out.format("src ip = %s	dst ip = %s\n", srcIP, dstIP);
						System.out.format("pkt epoch time = %s\n", p.getFrame_time_epoch());
					}*/
					if(!asduTypeList.contains(Integer.toString(asduType))) {
						asduTypeList.add(Integer.toString(asduType));
					}
				    common_addr = u.getAddr();
					for (IOA i : u.getIoaObj()) {
						ioa = i.getIoa();
						ioaList.add(Integer.toString(ioa));
						AsduSystem s = new AsduSystem(common_addr, ioa);
                        if (!sysList.contains(s.toString())) {
                            sysList.add(s.toString());
                        }
					}
				} // for asdu
			} // for apci


			if (!analyzedDataMap.containsKey(packetSignature)) {
				analysisObj.incrementNumReported();
				analysisObj.setFirstTimeSeen(p.getFrame_time_epoch());
				analysisObj.setPkt_rate(0.0);

				if (!asduTypeList.isEmpty()) {
					analysisObj.setAsduTypeArr(asduTypeList);
				}
				if (!ioaList.isEmpty()) {
					analysisObj.setIoaArr(ioaList);
				}
				if (!sysList.isEmpty()) {
				    analysisObj.setSysArr(sysList);
                }
                analysisObj.setNumI(numI);
				analysisObj.setNumS(numS);
				analysisObj.setNumU(numU);
				analysisObj.setNumIoa(ioaList.size());
				analysisObj.setNumSys(sysList.size());
				analyzedDataMap.put(packetSignature, analysisObj);
			} else {
				AnalyzedObj aObj = analyzedDataMap.get(packetSignature);
				aObj.incrementNumReported();
				aObj.setLastTimeSeen(p.getFrame_time_epoch());
				pktRate = aObj.getNumTimesReported()/(aObj.getLastTimeSeen() - aObj.getFirstTimeSeen());
				aObj.setPkt_rate(pktRate);

				// update ioa list
				if (aObj.getIoaArr() == null) {
					aObj.setIoaArr(ioaList);
				} else {
					for (String s : ioaList) {
						if (!aObj.getIoaArr().contains(s)) {
							aObj.addIOA(s);
						}
					}
				}

				// update system list
				if (aObj.getSysArr() == null) {
				    aObj.setSysArr(sysList);
                } else {
				    for (String s1 : sysList) {
				        if (!aObj.getSysArr().contains(s1)) {
				            aObj.addSys(s1);
                        }
                    }
                }

                // update ASDU type ID list
				if (aObj.getAsduTypeArr() == null) {
					aObj.setAsduTypeArr(asduTypeList);
				} else {
					for (String id : asduTypeList) {
						if (!aObj.getAsduTypeArr().contains(id)) {
							aObj.addAsduType(id);
						}
					}
				}

                // update ioa and system numbers
                aObj.setNumIoa(aObj.getIoaArr().size());
				aObj.setNumSys(aObj.getSysArr().size());

				// update apdu len
                aObj.setApduLen(aObj.getApduLen() + analysisObj.getApduLen());

                // update S,U,I APDU numbers
                aObj.setNumS(aObj.getNumS() + numS);
                aObj.setNumI(aObj.getNumI() + numI);
                aObj.setNumU(aObj.getNumU() + numU);

				// testing flow pkt rate
				/*
				if (packetSignature.equals("192.168.250.3192.168.111.33")) {
					System.out.format("flow is: %s\nfirt time = %.18f, last time = %.18f\n, # of occurrence = %d, pkt_rate = %.10f\n", packetSignature, aObj.getFirstTimeSeen(), aObj.getLastTimeSeen(), aObj.getNumTimesReported(), aObj.getPkt_rate());
				}*/

			}


			// Set analysisObj to null preparing for next packet
			analysisObj = null;
		}
	}

    public void buildIOAClusterPerMalformedFlow(ArrayList<Packet> pkts, HashMap<String, AnalyzedObj> analyzedDataMap, int choice) {
        String srcIP = "";
        String dstIP = "";

        double pktRate = 0.0;
        double pktNum = 0.0;
        ArrayList<String> ioaList;
        ArrayList<String> sysList;

        int ioa;
        int common_addr;

        for (Packet p : pkts) {
            if (p.isMalformed()) {
                numMalformedPackets += 1;
            }

            // update packet counting rate
            pktNum++;

            analysisObj = new AnalyzedObj(choice);
            ioaList = new ArrayList<>();
            sysList = new ArrayList<>();
            int numS = 0; // number of S,U,I types all for APDUs
            int numU = 0;
            int numI = 0;
            srcIP = p.getIp_src();
            if (srcIP.equals("192.168.111.34") || srcIP.equals("192.168.111.40") || srcIP.equals("192.168.111.45") || srcIP.equals("192.168.111.46") || srcIP.equals("192.168.111.47") || srcIP.equals("192.168.111.48") || srcIP.equals("192.168.111.79")) {
            	System.out.println("missing IP found!!! " + srcIP);
			}
            analysisObj.setSrcIP(srcIP);
            dstIP = p.getIp_dst();
            analysisObj.setDstIP(dstIP);
            packetSignature = srcIP + dstIP;
            if (srcIP.equals("192.168.250.2") || srcIP.equals("192.168.250.1") || srcIP.equals("192.168.250.3") || srcIP.equals("192.168.250.4")) {
                analysisObj.setDirection("1");
            } else {
                analysisObj.setDirection("0");
            }
            analysisObj.setApduLen(p.getTcp_pdu_size() - 2);
            for (APCI a : p.getApciObj()) {
                if (a.getType() == I_FORMAT) {
                    analysisObj.setApciFormatType("I-FORMAT");
                    numI++;
                }else if (a.getType() == S_FORMAT) {
                    analysisObj.setApciFormatType("S-FORMAT");
                    numS++;
                }else if (a.getType() == U_FORMAT) {
                    analysisObj.setApciFormatType("U-FORMAT");
                    numU++;
                    if (a.getuType() == TESTFR_CON){
                        analysisObj.setApciUType("TESTFR_CON");
                    }else if (a.getuType() == TESTFR_ACT) {
                        analysisObj.setApciUType("TESTFR_ACT");
                    } else if (a.getuType() == STOPDT_ACT) {
                        analysisObj.setApciUType("STOPDT_ACT");
                    } else if (a.getuType() == STOPDT_CON) {
                        analysisObj.setApciUType("STOPDT_CON");
                    } else if (a.getuType() == STARTDT_ACT) {
                        analysisObj.setApciUType("STARTDT_ACT");
                    } else if (a.getuType() == STOPDT_CON) {
                        analysisObj.setApciUType(("STARTDT_CON"));
                    }
                }else {
                    analysisObj.setApciFormatType("Invalid Format");
                    continue;
                }
                for (ASDU u : a.getAsduObj()) {
                    common_addr = u.getAddr();
                    for (IOA i : u.getIoaObj()) {
                        ioa = i.getIoa();
                        ioaList.add(Integer.toString(ioa));
                        AsduSystem s = new AsduSystem(common_addr, ioa);
                        if (!sysList.contains(s.toString())) {
                            sysList.add(s.toString());
                        }
                    }
                } // for asdu
            } // for apci


            if (!analyzedDataMap.containsKey(packetSignature)) {
                analysisObj.incrementNumReported();
                analysisObj.setFirstTimeSeen(p.getFrame_time_epoch());
                analysisObj.setPkt_rate(0.0);
                if (!ioaList.isEmpty()) {
                    analysisObj.setIoaArr(ioaList);
                }
                if (!sysList.isEmpty()) {
                    analysisObj.setSysArr(sysList);
                }
                analysisObj.setNumI(numI);
                analysisObj.setNumS(numS);
                analysisObj.setNumU(numU);
                analysisObj.setNumIoa(ioaList.size());
                analysisObj.setNumSys(sysList.size());
                analyzedDataMap.put(packetSignature, analysisObj);
            } else {
                AnalyzedObj aObj = analyzedDataMap.get(packetSignature);
                aObj.incrementNumReported();
                aObj.setLastTimeSeen(p.getFrame_time_epoch());
				pktRate = aObj.getNumTimesReported()/(aObj.getLastTimeSeen() - aObj.getFirstTimeSeen());
				aObj.setPkt_rate(pktRate);

                // update ioa list
                if (aObj.getIoaArr() == null) {
                    aObj.setIoaArr(ioaList);
                } else {
                    for (String s : ioaList) {
                        if (!aObj.getIoaArr().contains(s)) {
                            aObj.addIOA(s);
                        }
                    }
                }

                // update system list
                if (aObj.getSysArr() == null) {
                    aObj.setSysArr(sysList);
                } else {
                    for (String s1 : sysList) {
                        if (!aObj.getSysArr().contains(s1)) {
                            aObj.addSys(s1);
                        }
                    }
                }

                // update ioa and system numbers
                aObj.setNumIoa(aObj.getIoaArr().size());
                aObj.setNumSys(aObj.getSysArr().size());

                // update apdu len
                aObj.setApduLen(aObj.getApduLen() + analysisObj.getApduLen());

                // update S,U,I APDU numbers
                aObj.setNumS(aObj.getNumS() + numS);
                aObj.setNumI(aObj.getNumI() + numI);
                aObj.setNumU(aObj.getNumU() + numU);

            }
            // Set analysisObj to null preparing for next packet
            analysisObj = null;
        }
    }

	// @XI
	// construct the time:measurement pair for each "system" per flow <srcIP, dstIP>
	// stored in analyzedDataMap:
	public void buildIOAValues(ArrayList<Packet> pkts, HashMap<String, AnalyzedObj> analyzedDataMap, int choice) {
		String srcIP = "";
		String dstIP = "";
		HashMap<String, List<IOAContent>> ioaMap2;
		int ioa;

		for (Packet p : pkts) {
			if (p.isMalformed()) {
				numMalformedPackets += 1;
				continue;
			}

			analysisObj = new AnalyzedObj(choice);
			srcIP = p.getIp_src();
			analysisObj.setSrcIP(srcIP);
			dstIP = p.getIp_dst();
			packetSignature = srcIP + ";" + dstIP;
			double frameTime = p.getFrame_time_epoch();
			ioaMap2 = new HashMap<>();

			for (APCI a : p.getApciObj()) {
				if (a.getType() == I_FORMAT) {
					analysisObj.setApciFormatType("I-FORMAT");
					analysisObj.setDirection("C->S");
				} /*else if (a.getType() == S_FORMAT) {
					analysisObj.setApciFormatType("S-FORMAT");
					analysisObj.setDirection("S->C");
				} else if (a.getType() == U_FORMAT) {
					analysisObj.setApciFormatType("U-FORMAT");
					if (a.getuType() == TESTFR_CON) {
						analysisObj.setApciUType("TESTFR_CON");
						analysisObj.setDirection("C->S");
					} else if (a.getuType() == TESTFR_ACT) {
						analysisObj.setApciUType("TESTFR_ACT");
						analysisObj.setDirection("S->C");
					}
				} */else {
					analysisObj.setApciFormatType("Not I Format");	// since this is IOA value analysis, only interested in I type
					continue;
				}

				for (ASDU u : a.getAsduObj()) {
					int asdu_typeId = u.getType_id();
					int asdu_causetx = u.getCausetx();
					/*
					if ((srcIP.equals("192.168.250.4") && dstIP.equals("192.168.111.96")) || (srcIP.equals("192.168.250.4") && dstIP.equals("192.168.111.96")))  {
						System.out.format("src = %s, dst = %s, asdu type = %d", srcIP, dstIP, asdu_typeId);
					}*/
					int common_addr = u.getAddr();
					for (IOA i : u.getIoaObj()) {
						ioa = i.getIoa();
						AsduSystem s = new AsduSystem(common_addr, ioa);
						s.setAsdu_type(asdu_typeId);
						s.setCausetx(asdu_causetx);

						if (asdu_typeId == 5 || asdu_typeId == 9 || asdu_typeId == 13 || asdu_typeId == 36 || asdu_typeId == 50
						// the following are types of "status"
							|| asdu_typeId == 1 || asdu_typeId == 3 || asdu_typeId == 30 || asdu_typeId == 31) {
							double float_value = 0.0;
							if (asdu_typeId == 13 || asdu_typeId == 36 || asdu_typeId == 50) {
								float_value = i.getFloat_value();
							} else if (asdu_typeId == 5) {
								float_value = i.getVti_value();
							} else if (asdu_typeId == 9) {
								float_value = i.getNorm_value();
							} else if (asdu_typeId == 1 || asdu_typeId == 30) {
								float_value = i.getSiq_spi();
							} else if (asdu_typeId == 3 || asdu_typeId == 31) {
								float_value = i.getDiq_dpi();
							}

							List<IOAContent> mList = new ArrayList<>();		// store time:float_value pairs
							mList.add(new IOAContent(frameTime, float_value));
							ioaMap2.put(s.toString2(), mList); // @XI
						}
						/*
						if (asdu_typeId == 9) {
							double float_value = i.getNorm_value();
							List<IOAContent> mList = new ArrayList<>();		// store time:float_value pairs
							mList.add(new IOAContent(frameTime, float_value));
							ioaMap2.put(s.toString2(), mList); // @XI

						} */


					} // for ioa
				} // for asdu
			} // for apci

			if (analysisObj.getApciFormatType().equals("Not I Format")) {
				continue;
			}

			if (!analyzedDataMap.containsKey(packetSignature)) {
				analysisObj.setDstIP(dstIP);
				analysisObj.incrementNumReported();
				analysisObj.setFirstTimeSeen(p.getFrame_time_epoch());
				if (!ioaMap2.isEmpty()) {
					analysisObj.setIoaMap2(ioaMap2);
				}
				analyzedDataMap.put(packetSignature, analysisObj);

			} else {
				AnalyzedObj aObj = analyzedDataMap.get(packetSignature);
				aObj.incrementNumReported();
				aObj.setLastTimeSeen(p.getFrame_time_epoch());
				if (aObj.getIoaMap2() == null) {
					aObj.setIoaMap2(ioaMap2);
				} else {
					// IP in analyzedDataMap, already exist IOAMap for this IP
					HashMap<String, List<IOAContent>> existedIOAMap = aObj.getIoaMap2();
					for (String n : ioaMap2.keySet()) {
						if (!existedIOAMap.containsKey(n)) { // add a new "system"
							existedIOAMap.put(n, ioaMap2.get(n));
						} else { // this IOA has been reported
							List<IOAContent> existedList = existedIOAMap.get(n);
							List<IOAContent> newList = ioaMap2.get(n);
							for (IOAContent c : newList) {
								existedList.add(c);
							} // for

						}
					}
				}
			}
			analysisObj = null;
		}
	}

	// Construct IOA cluster per srcIP with measurement ranges
	// DEBUG NOT FINISHED!!! 
	// Alternative: Calculate with extracted IOA values in time series from function 3 in Python scripts
	public void buildIOARangePerRTU(ArrayList<Packet> pkts, HashMap<String, AnalyzedObj> analyzedDataMap, int choice) {
		String srcIP = "";
		String dstIP = "";
		//ArrayList<String> ioaList;
		//HashMap<Integer, IOARange> ioaRangeMap;
		HashMap<String, IOARange> ioaRangeMap2;
		int ioa;
		int common_addr;
		AsduSystem system;
		double val;

		for (Packet p : pkts) {
			if (p.isMalformed()) {
				numMalformedPackets += 1;
				continue;
			}

			analysisObj = new AnalyzedObj(choice);
			//ioaList = new ArrayList<>();
			//ioaRangeMap = new HashMap<>();
			ioaRangeMap2 = new HashMap<>();
			srcIP = p.getIp_src();
			analysisObj.setSrcIP(srcIP);
			dstIP = p.getIp_dst();
			analysisObj.setDstIP(dstIP);
			packetSignature = srcIP;


				for (APCI a : p.getApciObj()) {
					if (a.getType() == I_FORMAT) {
						analysisObj.setApciFormatType("I-FORMAT");
						analysisObj.setDirection("C->S");
					}else if (a.getType() == S_FORMAT) {
						analysisObj.setApciFormatType("S-FORMAT");
						analysisObj.setDirection("S->C");
					}else if (a.getType() == U_FORMAT) {
						analysisObj.setApciFormatType("U-FORMAT");
						if (a.getuType() == TESTFR_CON){
							analysisObj.setApciUType("TESTFR_CON");
							analysisObj.setDirection("C->S");
						}else if (a.getuType() == TESTFR_ACT) {
							analysisObj.setApciUType("TESTFR_ACT");
							analysisObj.setDirection("S->C");
						}
					}else {
						analysisObj.setApciFormatType("Invalid Format");
						continue;
					}
					for (ASDU u : a.getAsduObj()) {
						common_addr = u.getAddr();

						for (IOA i : u.getIoaObj()) {
							ioa = i.getIoa();

							system = new AsduSystem(common_addr, ioa);
							val = i.getFloat_value();
							//ioaRangeMap.put(ioa, new IOARange(val, val));
							ioaRangeMap2.put(system.toString(), new IOARange(val, val));

						}

					} // for ASDU
				}// for APCI

				if (!analyzedDataMap.containsKey(packetSignature)) {
					analysisObj.incrementNumReported();
					analysisObj.setFirstTimeSeen(p.getFrame_time_epoch());
					/*
					if (!ioaRangeMap.isEmpty()) {
						analysisObj.setIoaRangeMap(ioaRangeMap);
					}
					*/
					if (!ioaRangeMap2.isEmpty()) {
						analysisObj.setIoaRangeMap2(ioaRangeMap2);
					}

					analyzedDataMap.put(packetSignature, analysisObj);
				} else {
					AnalyzedObj aObj = analyzedDataMap.get(packetSignature);
					aObj.incrementNumReported();
					aObj.setLastTimeSeen(p.getFrame_time_epoch());

					// ioaRangeMap() - group on IOA number only
					/*
					if (aObj.getIoaRangeMap() == null) {
						aObj.setIoaRangeMap(ioaRangeMap);
					} else { // ip in analyzedDataMap, already exist ioaRangeMap
						HashMap<Integer, IOARange> existedRangeMap = aObj.getIoaRangeMap();
						//HashMap<AsduSystem, IOARange> existedRangeMap = aObj.getIoaRangeMap2();
						for (int n : ioaRangeMap.keySet()) {
							if (!existedRangeMap.containsKey(n)) { // add a new IOA
								existedRangeMap.put(n, ioaRangeMap.get(n));
							} else { // this IOA has been reported
								IOARange existedRange = existedRangeMap.get(n);
								double newVal = ioaRangeMap.get(n).getMinVal(); // note: min = max
								// check and update range values
								if (newVal < existedRange.getMinVal()) {
									existedRange.setMinVal(newVal);
								}
								if (newVal > existedRange.getMinVal()) {
									existedRange.setMaxVal(newVal);
								}
							}
						}

					}
					*/
					// ioaRangeMap2() - group on "system" = common addr + IOA num
					if (aObj.getIoaRangeMap2() == null) {
						aObj.setIoaRangeMap2(ioaRangeMap2);
					} else { // ip in analyzedDataMap, already exist ioaRangeMap
						HashMap<String, IOARange> existedRangeMap2 = aObj.getIoaRangeMap2();
						for (String n : ioaRangeMap2.keySet()) {
							if (!existedRangeMap2.containsKey(n)) { // add a new system
								existedRangeMap2.put(n, ioaRangeMap2.get(n));
							} else { // this system has been reported
								IOARange existedRange2 = existedRangeMap2.get(n);
								double newVal = ioaRangeMap2.get(n).getMinVal(); // note: min = max
								// check and update range values
								if (newVal < existedRange2.getMinVal()) {
									existedRange2.setMinVal(newVal);
								}
								if (newVal > existedRange2.getMinVal()) {
									existedRange2.setMaxVal(newVal);
								}
							}
						}

					}
				}
				// Set analysisObj to null preparing for next packet
				analysisObj = null;

		}
	}

	// this function computes ASDU types for each RTU (at IOA) per flow
	// signature = srcIP + dstIP + APCI type + ASDU address + ASDU type
	public void buildASDUTypePerFlowIoa(ArrayList<Packet> pkts, HashMap<String, AnalyzedObj> analyzedDataMap, int choice) {
		String srcIP = "";
		String dstIP = "";
		String asduAddr = "";
		String asduType = "";
		String ioa = "";
		ArrayList<String> ioaList;

		for (Packet p : pkts) {
			if (p.isMalformed()) {
				numMalformedPackets += 1;
				continue;
			}

			srcIP = p.getIp_src();
			dstIP = p.getIp_dst();

			for (APCI a : p.getApciObj()) {
				packetSignature = "";
				packetSignature = srcIP + dstIP;

				analysisObj = new AnalyzedObj(choice);
				analysisObj.setSrcIP(srcIP);
				analysisObj.setDstIP(dstIP);
				if (a.getType() == I_FORMAT) {
					analysisObj.setApciFormatType("I-FORMAT");
					packetSignature += analysisObj.getApciFormatType();
				} else if (a.getType() != I_FORMAT) {
					if (a.getType() == S_FORMAT) {
						analysisObj.setApciFormatType("S-FORMAT");
					} else if (a.getType() == U_FORMAT) {
						analysisObj.setApciFormatType("U-FORMAT");
					}
					/*
					packetSignature += analysisObj.getApciFormatType();
					analysisObj.setAsduAddr(-1);
					analysisObj.setAsduType(-1); */
					continue;
				}
				for (ASDU u : a.getAsduObj()) {
					asduAddr = Integer.toString(u.getAddr());
					asduType = Integer.toString(u.getType_id());
					packetSignature += asduAddr;
					packetSignature += asduType;
					analysisObj.setAsduAddr(u.getAddr());
					analysisObj.setAsduType(u.getType_id());
					ioaList = new ArrayList<>();
					for (IOA i : u.getIoaObj()) {
						ioa = Integer.toString(i.getIoa());
						ioaList.add(ioa);

					}

					if (!analyzedDataMap.containsKey(packetSignature)) {
						analysisObj.incrementNumReported();
						analysisObj.setFirstTimeSeen(p.getFrame_time_epoch());
						if (!ioaList.isEmpty()) {
							analysisObj.setIoaArr(ioaList);
						}
						analyzedDataMap.put(packetSignature, analysisObj);
					} else {
						AnalyzedObj aObj = analyzedDataMap.get(packetSignature);
						aObj.incrementNumReported();
						aObj.setLastTimeSeen(p.getFrame_time_epoch());
						ArrayList<String> curIoaArr = aObj.getIoaArr();
						if (aObj.getIoaArr() == null) {
							aObj.setIoaArr(ioaList);
						} else {
							for (String s : ioaList) {
								if (!curIoaArr.contains(s)) {
									aObj.addIOA(s);
								}
							}
						}
						Collections.sort(aObj.getIoaArr());
						analyzedDataMap.put(packetSignature, aObj);
					}
					analysisObj = null;

				}
			}
		}
	}

	// @XI

}
