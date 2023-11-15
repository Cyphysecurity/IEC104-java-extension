import java.io.Serializable;

/*
 * This class represent a single QOS object
 * which has all the relevant fields/attributes that belong to this particular IOA.
 */
public class QOS implements Serializable {

    private int qos_ql;
    private int qos_se;

    // Default constructor
    public QOS() {
        initialize();
    }

    public void initialize(){
        this.qos_ql = -1;
        this.qos_se = -1;
    }

    /*
     * Individual setter and getter for each data member are provided below.
     * This methods simply provides a convenient interface (single point)
     * for all data members of this object
     */
    public void setFieldName(String name, String value) {
        switch (name) {
            case "iec60870_asdu.qos.ql": qos_ql = Integer.parseInt(value);
                break;
            case "iec60870_asdu.qos.se": qos_se = Integer.parseInt(value);
                break;
            default: break;
        }
    }

    public int getQos_ql() {
        return qos_ql;
    }

    public void setQos_ql(int qos_ql) {
        this.qos_ql = qos_ql;
    }

    public int getQos_se() {
        return qos_se;
    }

    public void setQos_se(int qos_se) {
        this.qos_se = qos_se;
    }

    @Override
    public String toString() {
        return "\n\tQOS:{qos_ql=" + qos_ql + ", qos_se=" + qos_se + "}";
    }
}

