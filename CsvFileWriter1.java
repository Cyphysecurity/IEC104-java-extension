/* @XI
 * Compared to CsvFileWriter, this version performs more efficient IO operations
 * This csv file writer converts data into csv
 * 1. by unit of individual AnalyzedObj
 * 2. by packet
 * 3. by analyzedDataMap Key (packetSignature, ...)
 * To be called in ConstructAnalysisData.java
 *
 */
import java.util.*;
import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class CsvFileWriter1 {
    /*
    private static final String DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private String HEADER;
    */
    private boolean append = false;
    private boolean autoFlush = true;
    private String charset = "UTF-8";
    private String filePath;
    private File file;
    private FileOutputStream fos;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    private PrintWriter pw;

    public CsvFileWriter1() {
        initialize();
    }

    public void initialize() {
        //this.HEADER = "";
        System.out.format("CsvFileWriter1: append mode is: %b\nautoFlush mode is: %b\n", append, autoFlush);
        this.filePath = "apdu_level.csv";
        this.file = new File(filePath);
        try {
            boolean result = file.createNewFile();
            if (result) {
                System.out.println("Successfully created file: " + file.getAbsolutePath());
            } else {
                System.out.println("File already existed: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.fos = new FileOutputStream(file, append);
            this.osw = new OutputStreamWriter(fos, charset);
            this.bw = new BufferedWriter(osw);
            this.pw = new PrintWriter(bw, autoFlush);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Export APDU level result from AnalyzedAPDU
    public void apduExport(String header, List<AnalyzedObj> apdus) {
        //"srcIP,dstIP,epoch_time,relative_time,APDU_Type,APDU_Length"
        try {
            pw.println(header);
            for (AnalyzedObj ao : apdus) {
                pw.println(ao.toString1());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Exported to file: " + filePath);
            pw.close();
        }

    }



    public static void main(String[] args) {

    }
}
