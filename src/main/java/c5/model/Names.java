package c5.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure for ".names" file.
 * 
 * @author li
 * @since 2014
 */
public class Names {

	private int attrCount = 0;

	private int lableAttIndex = -1;

	private final List<String> targetClassifies = new ArrayList<String>();

	private final List<Attribute> attributes = new ArrayList<Attribute>();

	public int getAttrCount() {
		return attrCount;
	}

	public int getLableAttIndex() {
		return lableAttIndex;
	}

	public void setLableAttIndex(int lableAttIndex) {
		this.lableAttIndex = lableAttIndex;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(Attribute attribute) {
		this.attrCount++;
		this.attributes.add(attribute);
	}

	public List<String> getTargetClassifies() {
		return targetClassifies;
	}

	public String getTargetClass(int index) {
		return targetClassifies.get(index);
	}

	public int getTargetClassSize() {
		return targetClassifies.size();
	}

	public void addTargetClassify(String targetClassify) {
		this.targetClassifies.add(targetClassify);
	}

	public Attribute getAttribute(String attributeName) {
		int index = getArrtibuteIndex(attributeName);
		return index >= 0 ? this.attributes.get(index) : null;
	}

	public int getArrtibuteIndex(String attributeName) {
		int index = 0;
		for (Attribute attribute : this.attributes) {
			if (attribute.getName().equals(attributeName)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public boolean containsInAttribute(String attributeName) {
		return getAttribute(attributeName) != null;
	}

	public int getTargetClassIndex(String targetClass) {
		int index = 0;
		for (String str : targetClassifies) {
			if (str.equals(targetClass)) {
				return index;
			}
			index++;
		}
		return -1;
	}
}
