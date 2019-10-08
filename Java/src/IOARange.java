/* @XI
 * This class defines an object: [measurement min value, max value]
 */

public class IOARange {
	private double minVal;
	private double maxVal;

	public IOARange() {
		this.minVal = Integer.MAX_VALUE;
		this.maxVal = Integer.MIN_VALUE;
	}
	public IOARange(double min, double max) {
		this.minVal = min;
		this.maxVal = max;
	}

	// setters and getters

	public void setMinVal(double min) {
		minVal = min;
	}

	public double getMinVal() {
		return minVal;
	}

	public void setMaxVal(double max) {
		maxVal = max;
	}

	public double getMaxVal() {
		return maxVal;
	}
}