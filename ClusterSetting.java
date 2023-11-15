/*
 * @XI
 * This class defines different settings of feature vectors for clustering
 * feature vectors are IP based.
 * The settings are:
 * 1. [#ip it connects to, #pkt/sec(avg, min, max), #IOA/pkt, #IOA]
 * 2. [APCI type, #pkt/sec, #IOA/pkt, #IOA]
 */
public class ClusterSetting implements APCIControlFieldType{
    private String packetSignature;
    private AnalyzedObj analysisObj;

    public ClusterSetting() {
        initialize();
    }

    public void initialize() {
        this.packetSignature = "";
        this.analysisObj = null;
    }



    // setters and getters
    public String getPacketSignature() {
        return packetSignature;
    }

    public void setPacketSignature(String packetSignature) {
        this.packetSignature = packetSignature;
    }

    public AnalyzedObj getAnalysisObj() {
        return analysisObj;
    }

    public void setAnalysisObj(AnalyzedObj analysisObj) {
        this.analysisObj = analysisObj;
    }
}
