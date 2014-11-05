package c5.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import c5.enums.NodeType;

/**
 * C5 tree node.
 * 
 * @author li
 * @since 2014
 */
public class Node {

	private NodeType type;

	private Attribute attribute;

	private int attributeIndex;

	private String targetClass;

	private int targetClassIndex;

	private double cut;

	private double high;

	private double low;

	private double mid;

	private int forks;

	private final List<Double> classDist = new ArrayList<Double>();

	private final List<Set<String>> subSet = new ArrayList<Set<String>>();

	private final List<Node> branches = new ArrayList<Node>();

	private double errors;

	private Double totalCases = 0D;

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public int getAttributeIndex() {
		return attributeIndex;
	}

	public void setAttributeIndex(int attributeIndex) {
		this.attributeIndex = attributeIndex;
	}

	public void setTotalCases(Double totalCases) {
		this.totalCases = totalCases;
	}

	public String getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}

	public int getTargetClassIndex() {
		return targetClassIndex;
	}

	public void setTargetClassIndex(int targetClassIndex) {
		this.targetClassIndex = targetClassIndex;
	}

	public double getCut() {
		return cut;
	}

	public void setCut(double cut) {
		this.cut = cut;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getMid() {
		return mid;
	}

	public void setMid(double mid) {
		this.mid = mid;
	}

	public int getForks() {
		return forks;
	}

	public void setForks(int forks) {
		this.forks = forks;
	}

	public List<Double> getClassDist() {
		return classDist;
	}

	public double getClassDist(int index) {
		return classDist.get(index);
	}

	public int getClassDistSize() {
		return classDist.size();
	}

	public Double getTotalCases() {
		return totalCases;
	}

	public double getErrors() {
		return errors;
	}

	public void setErrors(double errors) {
		this.errors = errors;
	}

	public List<Set<String>> getSubSet() {
		return subSet;
	}

	public List<Node> getBranches() {
		return branches;
	}

	public Node getBranch(int index) {
		return branches.get(index);
	}

	public void addClassDist(double classCount) {
		this.totalCases += classCount;
		this.classDist.add(Double.valueOf(classCount));
	}

	public void addSubSet(Set<String> subSet) {
		this.subSet.add(subSet);
	}

	public void addBranch(Node tree) {
		this.branches.add(tree);
	}

	public int searchSubset(String value) {
		int index = 0;
		for (Set<String> set : subSet) {
			if (set.contains(value)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public int getDiscreteIndex(String discrete) {
		return attribute == null ? -1 : attribute.getDiscreteIndex(discrete);
	}
}
