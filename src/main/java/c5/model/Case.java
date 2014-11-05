package c5.model;

import c5.enums.CaseType;

/**
 * Input variable for scoring.
 * 
 * @author li
 * @since 2014
 */
public class Case {

	private CaseType type;

	private String discrete;

	private double continuous;

	public Case(CaseType type) {
		this.type = type;
	}

	public Case(CaseType type, String discrete) {
		this.type = type;
		this.discrete = discrete;
	}

	public Case(CaseType type, double continuous) {
		this.type = type;
		this.continuous = continuous;
	}

	public CaseType getType() {
		return type;
	}

	public String getDiscrete() {
		return discrete;
	}

	public double getContinuous() {
		return continuous;
	}
}
