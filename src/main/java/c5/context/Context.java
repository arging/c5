package c5.context;

import c5.consts.C5Consts;

/**
 * scoring context.
 * 
 * @author li
 * @since 2014
 */
public class Context {

	private String label = C5Consts.UNKNOWN;

	private String fact;

	private double[] classSum;

	private double confidence;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFact() {
		return fact;
	}

	public void setFact(String fact) {
		this.fact = fact;
	}

	public double getClassSum(int index) {
		return classSum[index];
	}

	public void setClassSum(int index, double value) {
		this.classSum[index] = value;
	}

	public void addClassSum(int index, double value) {
		this.classSum[index] += value;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(int index) {
		this.confidence = classSum[index];
	}

	public void initClassSum(int size) {
		classSum = new double[size];
	}

	public void resetClassSum() {
		for (int i = 0; i < classSum.length; i++) {
			classSum[i] = 0D;
		}
	}
}
