package c5.model;

import java.util.ArrayList;
import java.util.List;

import c5.enums.AttributeType;

/**
 * Attribute defined in ".names" file.
 * 
 * @author li
 * @since 2014
 */
public class Attribute {

	private String name;

	private AttributeType type;

	private final List<String> discretes = new ArrayList<String>();

	public Attribute(String name, AttributeType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public AttributeType getType() {
		return type;
	}

	public List<String> getDiscretes() {
		return discretes;
	}

	public void addDiscrete(String discrete) {
		discretes.add(discrete);
	}

	public int getDiscreteIndex(String discrete) {
		for (int i = 0; i < discretes.size(); i++) {
			if (discretes.get(i).equals(discrete)) {
				return i;
			}
		}
		return -1;
	}
}
