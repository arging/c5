package c5;

/**
 * Result for c5 scoring.
 * 
 * @author li
 * @since 2014
 */
public class Result {

	/** label */
	private String label;

	/** in fact target */
	private String fact;

	/** prediction of the target */
	private String target;

	/** score */
	private double score;

	public Result(String label, String fact, String target, double score) {
		this.label = label;
		this.fact = fact;
		this.target = target;
		this.score = score;
	}

	public String getLabel() {
		return label;
	}

	public String getFact() {
		return fact;
	}

	public String getTarget() {
		return target;
	}

	public double getScore() {
		return score;
	}
}
