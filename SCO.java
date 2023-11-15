import java.io.Serializable;

/*
 * This class represent a single SCO object (type 45)
 * which has all the relevant fields/attributes that belong to this particular IOA.
 */
public class SCO implements Serializable {
    private int sco_on;
    private int sco_qu;
    private int sco_se;

    // Default constructor
    public SCO() {
        initialize();
    }

    public void initialize(){
        this.sco_on = -1;
        this.sco_qu = -1;
        this.sco_se = -1;
    }

    /*
     * Individual setter and getter for each data member are provided below.
     * This methods simply provides a convenient interface (single point)
     * for all data members of this object
     */
    public void setFieldName(String name, String value) {

        switch (name) {
            case "iec60870_asdu.sco.on": sco_on = Integer.parseInt(value);
                break;
            case "iec60870_asdu.sco.qu": sco_qu = Integer.parseInt(value);
                break;
            case "iec60870_asdu.sco.se": sco_se = Integer.parseInt(value);
                break;
            default: break;
        }

    }

    public void setSco_on(int sco_on) {
        this.sco_on = sco_on;
    }

    public void setSco_qu(int sco_qu) {
        this.sco_qu = sco_qu;
    }

    public void setSco_se(int sco_se) {
        this.sco_se = sco_se;
    }

    public int getSco_on() {
        return sco_on;
    }

    public int getSco_qu() {
        return sco_qu;
    }

    public int getSco_se() {
        return sco_se;
    }

    @Override
    public String toString() {
        return "\n\tSCO:{sco_on=" + sco_on + ", sco_qu=" + sco_qu + ", sco_se=" + sco_se + "}";
    }
}

