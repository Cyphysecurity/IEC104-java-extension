/* @XI
 * This class contains any small functions help testing
 */

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class utility {
    // This function helps print IOA measurement in time series to screen
    // for testing purpose
    public void printIOAMap(HashMap<Integer, List<IOAContent>> map) {
        for (int ioa : map.keySet()) {
            System.out.format("IOA number: %d%n", ioa);
            List<IOAContent> list = map.get(ioa);
            for (IOAContent c : list) {
                System.out.format("time = %.8f; measurement = %.3f\n", c.getPacketTime(), c.getMeasurement());
            } // for content
            System.out.println();
        } // for ioa
    }

    /* This function takes all CSV files containing the distinct systems for each PCAP
     * compare and keep distinct systems from all PCAP
     * Result: generate a HashSet, containing only the distinct systems from input PCAPs
     */
    public static HashSet<String> finalDistinctSystems(HashSet<String> cur, String csvfile) {
        HashSet<String> result = new HashSet<>();

        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csvfile));
            while ((line = br.readLine()) != null) {
                if (!cur.contains(line)) {
                    result.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input CSV file cannot cannot be found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("BufferedReader cannot read lines!");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("BufferedReader cannot be closed!");
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    // This function generates CSV for distinct "system"s of all previous results
    public static void generateSystemCSV() {
        System.out.println("********************************************************************************************");
        System.out.println("Start combining distinct systems for all PCAPS......");
        final String NEW_LINE_SEPARATOR = "\n";
        // find distinct systems from all PCAP results
        String eleven = "";
        String[] csvfiles = {"D:\\IntellijProjects\\ICCP\\src\\distinctSystem_11_8.csv", "D:\\IntellijProjects\\ICCP\\src\\distinctSystem_4_8.csv", "D:\\IntellijProjects\\ICCP\\src\\distinctSystem_8_8.csv", "D:\\IntellijProjects\\ICCP\\src\\distinctSystem_10_8.csv", "D:\\IntellijProjects\\ICCP\\src\\distinctSystem_9_8.csv"};
        HashSet<String> result = new HashSet<>();
        // handle each csvfile one by one
        for (String csvfile : csvfiles) {
            result = finalDistinctSystems(result, csvfile);
        }

        String HEADER = "system";
        FileWriter fWriter = null;
        String filename = "finalSystem.csv";

        try {
            fWriter = new FileWriter(filename);
            fWriter.append(HEADER);
            fWriter.append(NEW_LINE_SEPARATOR);

            for (String systemString : result) {
                fWriter.append(systemString);
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

    // This function gets date and time in certain format
    public static String getDateTime() {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        df.setTimeZone(TimeZone.getTimeZone("CST"));
        return df.format(now);
    }

    // regex
    public static boolean match(String pattern, String line){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(line);
        return m.find();
    }

    public static void main(String[] args) {
        String a = "2-1000";
        String b = "3-1005";
        String c = "4-1000";
        String d = "2-1000";
        HashSet<String> s = new HashSet<>();
        s.add(a);
        s.add(b);
        s.add(c);
        /*if (s.contains(d)) {
            System.out.println("HashSet can tell string \"2-1000\" and string \"2-1000\" are the same");
        }*/

        HashSet<Integer> i = new HashSet<>();
        i.add(1000);
        i.add(10);
        i.add(1000);
        i.add(1000);
        System.out.println(i.size());

        // generateSystemCSV();

        //System.out.println(getDateTime());

        //System.out.println(match("(\\b9\\b)", "9"));

    }

}


