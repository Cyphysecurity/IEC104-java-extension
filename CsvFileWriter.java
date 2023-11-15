/* @XI
 * This csv file writer converts data into csv 
 * 1. by unit of individual AnalyzedObj
 * 2. by packet
 * 3. by analyzedDataMap Key (packetSignature, ...)
 * To be called in ConstructAnalysisData.java 
 */


import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.Path;

public class CsvFileWriter {
    private static final String DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    FileWriter fWriter = null;
    Path curPath = Paths.get(System.getProperty("user.dir"));
    Path outPath = Paths.get(curPath.toString(), "output");

    // TO-DO: ASDU type and CoT type not correct in AnalyzedIOAClusterDistribution
    // Write measurements in information objects versus time to CSV files
    public void measurementToCsv(HashMap<String, AnalyzedObj> dMap) throws IOException {
        // define header
        String HEADER = "srcIP,dstIP,ASDU_Type,CauseTx,ASDU_addr,IOA,Time,Measurement";
        /*
            FileWriter outFile;
			Path curPath = Paths.get(System.getProperty("user.dir"));
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
             */
        // export all CSV files of measurement values to a folder named by time
        Path valuesOutPath = Paths.get(outPath.toString(), ReadPackets.getDateTime());
        try {
            if (!Files.exists(valuesOutPath)) {
                Files.createDirectory(valuesOutPath);
            }
        } catch (IOException e) {
            System.out.println("Failed to create a directory!");
            e.printStackTrace();
        }
        for (String ip : dMap.keySet()) {
            AnalyzedObj s = dMap.get(ip);

            String filename = ip + ".csv";    // each source is a separate CSV file
            Path outfPath = Paths.get(valuesOutPath.toString(), filename);
            System.out.println("output log file is: " + outfPath);

            try {
                //fWriter = new FileWriter(filename);
                fWriter = new FileWriter(outfPath.toString());
                fWriter.append(HEADER);
                fWriter.append(System.lineSeparator());

                // start writing all the fields into .csv
                // separated by DELIMITER, end with System.lineSeparator()
                HashMap<String, List<IOAContent>> ioaMap = s.getIoaMap2();
                for (HashMap.Entry<String, List<IOAContent>> entry : ioaMap.entrySet()) {
                    List<IOAContent> l = entry.getValue();
                    String asdu_system = entry.getKey();
                    String asdu_type = asdu_system.split(":")[0].split("-")[0];
                    String cot = asdu_system.split(":")[0].split("-")[1];
                    String system = asdu_system.split(":")[1];
                    String asdu_addr = system.split("-")[0];
                    String ioa = system.split("-")[1];

                    for (int i = 0; i < l.size(); i++) {
                        // HEADER: "srcIP,dstIP,ASDU_Type,CauseTx,ASDU_addr,IOA,Time,Measurement"
                        String line = s.getSrcIP() + DELIMITER + s.getDstIP() + DELIMITER + asdu_type + DELIMITER + cot + DELIMITER + asdu_addr + DELIMITER + ioa + DELIMITER + Double.toString(l.get(i).getPacketTime()) + DELIMITER + Double.toString(l.get(i).getMeasurement());
                        fWriter.append(line);
                        fWriter.append(System.lineSeparator());
                        System.out.println(line);
                    }
                }
                System.out.printf("%s was created successfully!%n", filename);

            } catch (Exception e) {
                System.out.println("Error in CsvFileWriter!!!");
                e.printStackTrace();
            } finally {
                try {
                    fWriter.flush();
                    fWriter.close();
                } catch (IOException e) {
                    System.out.println("Error while flushing/closing filewriter!!!");
                    e.printStackTrace();
                }
            } // finally
        } // for
    } // measurementToCsv

    // write number of "name" in time series into CSV
    public void anyTime(HashMap<String, Double> mapInTime, String name, String infile) {
        String HEADER = name + "," + "time";
        String filename = "output\\" + infile + "_" + name + "_vs_time.csv";

        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(System.lineSeparator());
            for (HashMap.Entry<String, Double> entry : mapInTime.entrySet()) {
                fWriter.append(entry.getKey());
                fWriter.append(DELIMITER);
                fWriter.append(Double.toString(entry.getValue()));
                fWriter.append(System.lineSeparator());
            } // for
            System.out.println(filename + " was created successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }
    }

    // append data into an existed CSV file
    public void continueWriter(HashMap<Double, String> mapInTime, String fName) {

        try {
            fWriter = new FileWriter(fName, true);
            for (HashMap.Entry<Double, String> entry : mapInTime.entrySet()) {
                fWriter.append(Double.toString(entry.getKey()));
                fWriter.append(DELIMITER);
                fWriter.append(entry.getValue());
                fWriter.append(System.lineSeparator());
            } // for
            System.out.println(fName + " was appended successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }
    }


    // write individual-packet-size in time series into CSV
    public void pktSize(HashMap<Double, Integer> pktSizeTime) {
        String HEADER = "Time, Pkt_size";
        String filename = "output\\pktSize_vs_Time.csv";

        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(System.lineSeparator());

            for (HashMap.Entry<Double, Integer> entry : pktSizeTime.entrySet()) {
                fWriter.append(Double.toString(entry.getKey()));
                fWriter.append(DELIMITER);
                fWriter.append(Integer.toString(entry.getValue()));
                fWriter.append(System.lineSeparator());
            } // for
            System.out.println(filename + " was created successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }
    }

    // write packet number rate in time series into CSV
    public void pktNumRate(HashMap<Double, Double> pktNumRateTime) {
        String HEADER = "Time, Pkt_rate";
        String filename = "output\\pktRate_vs_Time.csv";

        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(System.lineSeparator());

            for (HashMap.Entry<Double, Double> entry : pktNumRateTime.entrySet()) {
                fWriter.append(Double.toString(entry.getKey()));
                fWriter.append(DELIMITER);
                fWriter.append(Double.toString(entry.getValue()));
                fWriter.append(System.lineSeparator());
            } // for
            System.out.println(filename + " was created successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }
    }

    // collect all distinct IOA numbers in all packets
    public void distinctIOANum(HashSet<Integer> ioaNumSet) {
        String HEADER = "IOA#";
        String filename = "output\\distinctIOA.csv";

        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(System.lineSeparator());

            for (int n : ioaNumSet) {
                fWriter.append(Integer.toString(n));
                fWriter.append(System.lineSeparator());
            }
            System.out.println(filename + " was created successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }

    }

    // collect all distinct IOA numbers in all packets
    public void malformedIOANum(HashSet<Integer> malformedIoaSet) {
        String HEADER = "IOA#";
        String filename = "output\\malformedIOA.csv";

        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(NEW_LINE_SEPARATOR);

            for (int n : malformedIoaSet) {
                fWriter.append(Integer.toString(n));
                fWriter.append(NEW_LINE_SEPARATOR);
            }
            System.out.println(filename + " was created successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }

    }

    // collect all distinct "systems" in all packets
    // each "system" = common asdu addr + IOA number
    public void distinctSystem(HashSet<String> systemNameSet) {
        String HEADER = "system";
        String filename = "output\\distinctSystemNames.csv";

        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(NEW_LINE_SEPARATOR);

            for (String s : systemNameSet) {
                fWriter.append(s);
                fWriter.append(NEW_LINE_SEPARATOR);
            }
            System.out.println(filename + " was created successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }

    }

    /* this function writes counts of APDUs, APDUs' lengths and capture rates in timing per ASDU type
     * HashMap<String, ArrayList<AnalyzedObj>>, key = src + dst + asduType
     *
     */
    public void apduRatePerAsduType(HashMap<String, ArrayList<AnalyzedObj>> map) {
        String HEADER = "src,dst,asdu_type,time,apdu#,apdu_len,apdu_rate";
        String filename = "output\\apduRate_per_asduType.csv";
        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(NEW_LINE_SEPARATOR);

            for (HashMap.Entry<String, ArrayList<AnalyzedObj>> entry : map.entrySet()) {
                String signature = entry.getKey();
                String[] sig = signature.split(",");
                String src = sig[0];
                String dst = sig[1];
                String typeId = sig[2];
                ArrayList<AnalyzedObj> list = entry.getValue();
                for (AnalyzedObj obj : list) {
                    double t = obj.getEpochTime();
                    int apduCnt = obj.getNumI();
                    int apduLen = obj.getApduLen();
                    double rate = obj.getApdu_rate();
                    fWriter.append(src);
                    fWriter.append(DELIMITER);
                    fWriter.append(dst);
                    fWriter.append(DELIMITER);
                    fWriter.append(typeId);
                    fWriter.append(DELIMITER);
                    fWriter.append(Double.toString(t));
                    fWriter.append(DELIMITER);
                    fWriter.append(Integer.toString(apduCnt));
                    fWriter.append(DELIMITER);
                    fWriter.append(Integer.toString(apduLen));
                    fWriter.append(DELIMITER);
                    fWriter.append(Double.toString(rate));
                    fWriter.append(NEW_LINE_SEPARATOR);
                } // for objList
            } // for hashmap
            System.out.println(filename + " was created successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }

    }


    // this function writes counts of APDUs in each ASDU type in timing
    /*
    public void analysisPerASDUType(HashMap<Integer, ArrayList<AnalyzedASDU>> cntAsduTime) {
        String HEADER = "ASDU_Type, Time, Count, CountRate";
        FileWriter fWriter = null;
        String filename = "cntAsduType_vs_RelativeTime.csv";
        ArrayList<CntInTime> cntList = new ArrayList<>();

        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(NEW_LINE_SEPARATOR);

            for (HashMap.Entry<Integer, ArrayList<AnalyzedASDU>> entry : cntAsduTime.entrySet()) {
                cntList = entry.getValue();
                for (CntInTime element : cntList) {
                    fWriter.append(Integer.toString(entry.getKey()));   // write ASDU Type
                    fWriter.append(DELIMITER);
                    fWriter.append(Double.toString(element.getTime())); // write time
                    fWriter.append(DELIMITER);
                    fWriter.append(Integer.toString(element.getCnt())); // write count
                    fWriter.append(DELIMITER);
                    fWriter.append(Double.toString(element.getCntRate())); // write count rate
                    fWriter.append(NEW_LINE_SEPARATOR);
                }
            } // for
            System.out.println(filename + " was created successfully!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing filewriter!");
                e.printStackTrace();
            }
        }
    }
    */
    public static void main(String[] args) {
        
    }
}
