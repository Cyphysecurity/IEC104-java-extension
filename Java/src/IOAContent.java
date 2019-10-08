/* @XI
 * this object contains the content pair in time series for each IOA
 * in the format of <frameTime, measurementValue>
 */
public class IOAContent {
    private double packetTime;
    private double measurement;

    public IOAContent(double T, double M) {
        packetTime = T;
        measurement = M;
    }

    public void setPacketTime(double T) {
        this.packetTime = T;
    }

    public double getPacketTime() {
        return packetTime;
    }

    public void setMeasurement(double M) {
        this.measurement = M;
    }

    public double getMeasurement() {
        return measurement;
    }

}
