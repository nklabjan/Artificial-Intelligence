import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.lang.Math;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	// ordered list of class labels
	private List<String> labels;
	// ordered list of attributes
	private List<String> attributes;
	// map to ordered discrete values taken by attributes
	private Map<String, List<String>> attributeValues;
	// map for getting the index
	private HashMap<String, Integer> label_inv;
	private HashMap<String, Integer> attr_inv;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train:
	 *            the training set
	 */
	DecisionTreeImpl(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		buildTree(train.instances, attributes, majorityLabel(train.instances));
	}

	/**
	 * Recursive helper method that builds the decision tree.
	 * 
	 * @param instances
	 * @param attributes,
	 *            list of attributes
	 * @param deafaultLabel,
	 *            the label of the parent
	 * @return the root node of the decision tree
	 */
	private DecTreeNode buildTree(List<Instance> instances, List<String> attributes, String defaultLabel) {
		// temporary ArrayList to store the attributes
		// will allow us to remove the attributes without changing the parameter
		// list
		ArrayList<String> temporary = new ArrayList<String>();

		// traverses through list of attributes
		for (int i = 0; i < attributes.size(); i++) {
			// adds the attributes to the temporary ArrayList
			temporary.add(attributes.get(i));
		}

		// if there are no more training examples
		if (instances.isEmpty()) {
			// return a decision tree node
			return new DecTreeNode(defaultLabel, null, null, true);
		}

		// if all the instances have the same label
		if (sameLabel(instances)) {
			//return a decision tree node
			return new DecTreeNode(instances.get(0).label, null, null, true);
		}

		//if there are no more attributes
		if (attributes.isEmpty()) {
			return new DecTreeNode(majorityLabel(instances), null, null, true);
		}
		
		//stores best attribute
		String qq = null;
		//stores the max info gain
		double iGain = -1;
		
		// for each attribute
		for (int i = 0; i < attributes.size(); i++) {
			//if iGain is less then the info gain
			if (iGain < InfoGain(instances, attributes.get(i))) {
				//set new max info gain to iGain
				iGain = InfoGain(instances, attributes.get(i));
				//updates best attribute
				qq = attributes.get(i);
			}
		}
		
		//stores the roote node of the tree in r
		DecTreeNode r = new DecTreeNode(defaultLabel, qq, null, false);
		
		//for each attrubute value
		for (int i = 0; i < attributeValues.get(qq).size(); i++) {
			//List of examples, but a subset
			ArrayList<Instance> vex = new ArrayList<Instance>();
			//for each instance
			for (int j = 0; j < instances.size(); j++) {
				//if attribute value is same as qq
				if (instances.get(j).attributes.get(getAttributeIndex(qq)).equals(attributeValues.get(qq).get(i))) {
					//add instance to vex ArrayList
					vex.add(instances.get(j));
				}
			}
			
			//removes qq from temporary
			temporary.remove(qq);
			//new node created
			DecTreeNode n = buildTree(vex, temporary, majorityLabel(instances));
			//adds the child n to the root r
			r.addChild(n);
			//assigns new parentAttributevalue to to the node n
			n.parentAttributeValue = attributeValues.get(qq).get(i);
		}
		//sets r to be new root
		this.root = r;
		//returns r
		return r;
	}
	
	/**
	 * Checks if instances contain same labels.
	 * 
	 * @param instances, list of instances
	 * @return true if same, false if not
	 */
	boolean sameLabel(List<Instance> instances) {
		//for each instance in the list
		for (int i = 0; i < instances.size(); i++) {
			//if the labels aren't equal
			if(!instances.get(i).label.equals(instances.get(0).label)) {
				//return false 
				return false;
			}
		}
		//return true
		return true;
	}

	/**
	 * Returns the majority label.
	 * 
	 * @param instances, the list of instances
	 * @return a label
	 */
	String majorityLabel(List<Instance> instances) {
		//number of labels
		double nLab = 0;
		
		//for each instance 
		for (int i = 0; i < instances.size(); i++) {
			//if the labels are the same
			if (instances.get(i).label.equals(labels.get(0))) {
				//increment number of labels
				nLab = nLab + 1;
			}
		}
		
		//if the number of labels is greater than or equal to instances.size()/2
		if (nLab >= ((double) instances.size() / 2)) {
			//return first label
			return labels.get(0);
		}
		//returns second label
		return labels.get(1);
	}

	/**
	 * Calculate the entropy.
	 * 
	 * @param instances, the list of instances.
	 * @return the entropy
	 */
	double entropy(List<Instance> instances) {
		// the entropy to be returned
		double entropy = 0;
		// the number of labels
		double nLab = 0;

		// for each instance
		for (int i = 0; i < instances.size(); i++) {
			// if the labels equal each other
			if (labels.get(getLabelIndex(instances.get(0).label)).equals(instances.get(i).label)) {
				// increment number of labels
				nLab = nLab + 1;
			}
		}

		// stores nLab / instances.size()
		double num = 0;
		// needs to make sure that the size of instances and nLab are greater
		// than zero
		if (instances.size() > 0 && nLab > 0) {
			// calculates num
			num = nLab / instances.size();
		}

		// if num isn't equal to 0
		if (num != 0) {
			// calculates the entropy
			entropy += -(num * (Math.log(num) / Math.log(2)));
			// if num != 1
			if (num != 1) {
				// sets num1 to be 1-num
				double num1 = 1 - num;
				// calculates the entropy
				entropy += -((num1) * (Math.log(num1) / Math.log(2)));
			}
		}
		// returns entropy
		return entropy;
	}

	/**
	 * Calculates the conditional entropy.
	 * 
	 * @param instances,
	 *            list of instances
	 * @param attr,
	 *            the attribute
	 * @return the conditional entropy
	 */
	double conditionalEntropy(List<Instance> instances, String attr) {
		// the conditional entropy to be returned
		double condEntropy = 0;
		// the number of labels
		double nLab = 0;
		// the number of attributes
		double nAtt = 0;

		// for each attribute
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			// for each instance
			for (int j = 0; j < instances.size(); j++) {
				// if attributes match
				if (attributeValues.get(attr).get(i).equals(instances.get(j).attributes.get(getAttributeIndex(attr)))) {
					// increment attribute
					nAtt = nAtt + 1;
					// if labels match
					if (instances.get(0).label.equals(instances.get(j).label)) {
						// increment number of labels
						nLab = nLab + 1;
					}
				}
			}
			// stores nLab / nAtt
			double num = 0;
			// needs to make sure that nAtt and nLab are greater than zero
			if (nAtt > 0 && nLab > 0) {
				// calculates num
				num = nLab / nAtt;
			}

			// if num isn't equal to 0
			if (num != 0) {
				// calculates the conditional entropy
				condEntropy += -(num * (Math.log(num) / Math.log(2))) * (nAtt / instances.size());
				// if num != 1
				if (num != 1) {
					// sets num1 to be 1-num
					double num1 = 1 - num;
					condEntropy += -((num1) * (Math.log(num1) / Math.log(2))) * (nAtt / instances.size());
				}
			}
			// sets nAtt and nLab to 0
			nAtt = 0;
			nLab = 0;
		}
		// returns conditional entropy
		return condEntropy;
	}

	/**
	 * Calculates the info gain.
	 * 
	 * @param instances,
	 *            the list of instances
	 * @param attr,
	 *            the attribute
	 * @return the info gain
	 */
	double InfoGain(List<Instance> instances, String attr) {
		// Suggested helper function
		// returns the info gain of a list of examples, given the attribute attr
		return entropy(instances) - conditionalEntropy(instances, attr);
	}

	@Override
	public String classify(Instance instance) {
		for (int i = 0; i < this.root.children.size(); i++) {
			// if the parent attribute value is equal to the root's attributes
			if (this.root.children.get(i).parentAttributeValue
					.equals(instance.attributes.get(getAttributeIndex(this.root.attribute)))) {
				// call the recursive helper method
				return classifyHelper(instance, this.root.children.get(i));
			}
		}
		// if the attributes aren't equal
		return null;
	}

	/**
	 * Private recursive helper method for classify.
	 * 
	 * @param i,
	 *            an instance
	 * @param r,
	 *            node in decision tree
	 * @return the classification of the instance
	 */
	private String classifyHelper(Instance in, DecTreeNode r) {
		// if the node r is a leaf node
		if (r.terminal) {
			// return its
			return r.label;
		}

		// for each child of the node r
		for (int i = 0; i < r.children.size(); i++) {
			// if the parent attribute value is same as the attribute of r
			if (r.children.get(i).parentAttributeValue.equals(in.attributes.get(getAttributeIndex(r.attribute)))) {
				// recursively calls classifyHelper
				return classifyHelper(in, r.children.get(i));
			}
		}
		// if the attributes aren't the same return null
		return null;
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;

		// for each instance in training set
		for (int i = 0; i < attributes.size(); i++) {
			// prints out the attributes
			System.out.print(attributes.get(i) + " ");
			// calculates the Information gain
			double infoGain = InfoGain(train.instances, attributes.get(i));
			// prints out information gain with correct format
			System.out.format("%.5f\n", infoGain);

		}
	}

	@Override
	public void printAccuracy(DataSet test) {
		// the number of right labels
		float nRight = 0;
		// for each instance
		for (int i = 0; i < test.instances.size(); i++) {
			// if the class equals the label
			if (classify(test.instances.get(i)).equals(test.instances.get(i).label)) {
				// increment nRight
				nRight = nRight + 1;
			}
		}

		// converts number of instances to float
		float conv = (float) test.instances.size();
		// the percent to be printed out
		float answer = nRight / conv;
		// prints out answer to correct decimal
		System.out.format("%.5f\n", answer);
	}

	@Override
	/**
	 * Print the decision tree in the specified format Do not modify
	 */
	public void print() {
		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node with each line prefixed by 4 * k spaces.
	 * Do not modify
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else {
			int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
			value = attributeValues.get(parent.attribute).get(attributeValueIndex);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + p.label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + p.attribute + "?}");
			System.out.println(sb.toString());
			for (DecTreeNode child : p.children) {
				printTreeNode(child, p, k + 1);
			}
		}
	}

	/**
	 * Helper function to get the index of the label in labels list
	 */
	private int getLabelIndex(String label) {
		if (label_inv == null) {
			this.label_inv = new HashMap<String, Integer>();
			for (int i = 0; i < labels.size(); i++) {
				label_inv.put(labels.get(i), i);
			}
		}
		return label_inv.get(label);
	}

	/**
	 * Helper function to get the index of the attribute in attributes list
	 */
	private int getAttributeIndex(String attr) {
		if (attr_inv == null) {
			this.attr_inv = new HashMap<String, Integer>();
			for (int i = 0; i < attributes.size(); i++) {
				attr_inv.put(attributes.get(i), i);
			}
		}
		return attr_inv.get(attr);
	}

	/**
	 * Helper function to get the index of the attributeValue in the list for
	 * the attribute key in the attributeValues map
	 */
	private int getAttributeValueIndex(String attr, String value) {
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			if (value.equals(attributeValues.get(attr).get(i))) {
				return i;
			}
		}
		return -1;
	}
}
