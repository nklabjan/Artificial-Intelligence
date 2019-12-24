import java.util.*;

/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details.
 * Feel free to modify the provided function signatures to fit your own implementation
 */

public class Node {
    private int type = 0; //0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
    public ArrayList<NodeWeightPair> parents = null; //Array List that will contain the parents (including the bias node) with weights if applicable
    private double inputValue = 0.0;
    private double outputValue = 0.0;
    private double outputGradient = 0.0;
    private double delta = 0.0; //input gradient
   // private List<Double> deltas = new ArrayList<Double>();

    //Create a node with a specific type
    Node(int type) {
        if (type > 4 || type < 0) {
            System.out.println("Incorrect value for node type");
            System.exit(1);

        } else {
            this.type = type;
        }

        if (type == 2 || type == 4) {
            parents = new ArrayList<>();
        }
    }

    //For an input node sets the input value which will be the value of a particular attribute
    public void setInput(double inputValue) {
        if (type == 0)
            this.inputValue = inputValue;
    }

    /**
     * Calculate the output of a node.
     * You can get this value by using getOutput()
     * 
     * @param outputNodes the list of outputNodes
     */
    public void calculateOutput(ArrayList<Node> inputs, ArrayList<Node> outputNodes) {
        // if the type is 2 or 4
    	if (type == 2 || type == 4) { 
        	double z = 0; 
        	// for each input
        	for (int i = 0; i < inputs.size(); i++)
        		z +=  inputs.get(i).getOutput() * parents.get(i).weight;
        	// if hidden node
            if (type == 2) {
            	if(z >= 0)
            		outputValue = z;
            	else
            		outputValue = 0;
            }
            // if output node
            if (type == 4) {
            	double weightedSums[] = new double[outputNodes.size()];
            	double denominator = 0;
            	// for each output Node
            	for (int i = 0; i < outputNodes.size(); i++) {
            		// for each input
            		for (int j = 0; j < inputs.size(); j++ ) {
            			weightedSums[i] += inputs.get(j).getOutput() * outputNodes.get(i).parents.get(j).weight;
            		}
            		denominator += Math.exp(weightedSums[i]);
            	}
                outputValue = Math.exp(z) / denominator;	
            }
        }
    }

    //Gets the output value
    public double getOutput() {
        if (type == 0) { 
        	return inputValue;
        } else if (type == 1 || type == 3) { 
            return 1.00;
        } else {
            return outputValue;
        }
    }

    //Calculate the delta value of a node.
    public void calculateDelta(double learningRate, Instance instance, ArrayList<Node> outputNodes, Node input, Integer classValue) {
        // if type is 2 or 4
    	if (type == 2 || type == 4) {
        	delta = 0;
        	// if hidden node
            if (type == 2) {
            	int g = 0;
            	if (this.getOutput() > 0)
            		g = 1;
            	// for each output Node
            	for (int i = 0; i < outputNodes.size(); i++) {
            		// for each output node's parent
            		for (int j = 0; j < outputNodes.get(i).parents.size(); j++) {
            			if (outputNodes.get(i).parents.get(j).node.equals(this))
            				delta += outputNodes.get(i).parents.get(j).weight * (instance.classValues.get(i) - outputNodes.get(i).getOutput());
            		}
            	}
            	delta = learningRate * input.getOutput() * g * delta;
            }
            // if output node
            if(type == 4) {
            	delta = learningRate * input.getOutput() * (classValue - outputValue);
            }
        }
    }
    /**
     * Getter method to to access delta
     * @return the delta value of the node
     */
   public double getDelta()
   {
	   return delta;
   }

    //Update the weights between parents node and current node
    public void updateWeight(double learningRate, double[] deltas) {
    	// if hidden or output node
        if (type == 2 || type == 4) {
        	// for each delta
            for (int i = 0; i < deltas.length; i++)
            	parents.get(i).weight += deltas[i];
        }
    }
}


