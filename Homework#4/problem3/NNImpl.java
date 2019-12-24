import java.util.*;

/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 */

public class NNImpl {
    private ArrayList<Node> inputNodes; //list of the output layer nodes.
    private ArrayList<Node> hiddenNodes;    //list of the hidden layer nodes
    private ArrayList<Node> outputNodes;    // list of the output layer nodes

    private ArrayList<Instance> trainingSet;    //the training set

    private double learningRate;    // variable to store the learning rate
    private int maxEpoch;   // variable to store the maximum number of epochs
    private Random random;  // random number generator to shuffle the training set

    /**
     * This constructor creates the nodes necessary for the neural network
     * Also connects the nodes of different layers
     * After calling the constructor the last node of both inputNodes and
     * hiddenNodes will be bias nodes.
     */

    NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Random random, Double[][] hiddenWeights, Double[][] outputWeights) {
        this.trainingSet = trainingSet;
        this.learningRate = learningRate;
        this.maxEpoch = maxEpoch;
        this.random = random;

        //input layer nodes
        inputNodes = new ArrayList<>();
        int inputNodeCount = trainingSet.get(0).attributes.size();
        int outputNodeCount = trainingSet.get(0).classValues.size();
        for (int i = 0; i < inputNodeCount; i++) {
            Node node = new Node(0);
            inputNodes.add(node);
        }

        //bias node from input layer to hidden
        Node biasToHidden = new Node(1);
        inputNodes.add(biasToHidden);

        //hidden layer nodes
        hiddenNodes = new ArrayList<>();
        for (int i = 0; i < hiddenNodeCount; i++) {
            Node node = new Node(2);
            //Connecting hidden layer nodes with input layer nodes
            for (int j = 0; j < inputNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
                node.parents.add(nwp);
            }
            hiddenNodes.add(node);
        }

        //bias node from hidden layer to output
        Node biasToOutput = new Node(3);
        hiddenNodes.add(biasToOutput);

        //Output node layer
        outputNodes = new ArrayList<>();
        for (int i = 0; i < outputNodeCount; i++) {
            Node node = new Node(4);
            //Connecting output layer nodes with hidden layer nodes
            for (int j = 0; j < hiddenNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
                node.parents.add(nwp);
            }
            outputNodes.add(node);
        }
    }

    /**
     * Get the prediction from the neural network for a single instance
     * Return the idx with highest output values. For example if the outputs
     * of the outputNodes are [0.1, 0.5, 0.2], it should return 1.
     * The parameter is a single instance
     */
    public int predict(Instance instance) {
    	// max value
    	double max = 0;
    	// returns index of highest output
    	int index = 0;
    	
    	generateNetwork(instance);
    	// for each output node
    	for (int i = 0; i < outputNodes.size(); i++) {
    		// if output is greater than max value
    		if (outputNodes.get(i).getOutput() > max) {
    			max = outputNodes.get(i).getOutput();
    			index = i;
    		}
    	}
        return index;
    }


    /**
     * Train the neural networks with the given parameters
     * <p>
     * The parameters are stored as attributes of this class
     */

    public void train() {
    	int epoch = 0;
    	// double array for output deltas and hidden deltas
		double[][] outputDeltas = new double[outputNodes.size()][hiddenNodes.size()];
    	double[][] hiddenDeltas = new double[hiddenNodes.size()][inputNodes.size()];
    	
    	// while epoch is less than max epoch
    	while (epoch < maxEpoch) {
    		Collections.shuffle(trainingSet, random);
    		
    		// for each instance
    		for (Instance instance : trainingSet) {
    			generateNetwork(instance);    			
    			// for each output nodes
    			for (int i = 0; i < outputNodes.size(); i++) {
    				// for each hidden node
    				for (int j = 0; j < hiddenNodes.size(); j++) {
    					outputNodes.get(i).calculateDelta(learningRate, instance, outputNodes, hiddenNodes.get(j), instance.classValues.get(i));
    					outputDeltas[i][j] = outputNodes.get(i).getDelta();
    				}
    			}
    			// for each hidden node
    			for (int i = 0; i < hiddenNodes.size() - 1; i++) {
    				// for each input node
    				for (int j = 0; j < inputNodes.size(); j++) {
    					hiddenNodes.get(i).calculateDelta(learningRate, instance, outputNodes, inputNodes.get(j), null);
    					hiddenDeltas[i][j] = hiddenNodes.get(i).getDelta();
    				}
    			}
    			// for each output node
    			for (int i = 0; i < outputNodes.size(); i++)
    				outputNodes.get(i).updateWeight(learningRate, outputDeltas[i]);
    			// for each hidden node
    			for (int i = 0; i < hiddenNodes.size(); i++)
    				hiddenNodes.get(i).updateWeight(learningRate, hiddenDeltas[i]);
    		}
    		totalLoss(epoch);
    		epoch++;
    	}
    }

    /**
     * Calculate the cross entropy loss from the neural network for
     * a single instance.
     * The parameter is a single instance
     */
    private double loss(Instance instance) {
        double loss = 0;
        // for each output node
    	for (int i = 0; i < outputNodes.size(); i++) 
        	loss += instance.classValues.get(i) * Math.log(outputNodes.get(i).getOutput());
        return loss * -1;
    }
    
    /**
     * 
     * @param epoch
     */
    private void totalLoss(int epoch) {
    	double tL = 0;
    	// for each instance in the trainingSet
    	for (Instance instance : trainingSet) {
			inputNodes = new ArrayList<Node>();
			// for each attribute
			for (int i = 0; i < instance.attributes.size(); i++) {
				Node node = new Node(0);
				node.setInput(instance.attributes.get(i));
				inputNodes.add(node);
			}
			Node biasToHidden = new Node(1);
	        inputNodes.add(biasToHidden);
	        
	        // for each hidden node
    		for (int i = 0; i < hiddenNodes.size(); i++)
    			hiddenNodes.get(i).calculateOutput(inputNodes, null);
    		// for each output node
    		for (int i = 0; i < outputNodes.size(); i++)
    			outputNodes.get(i).calculateOutput(hiddenNodes, outputNodes);
    		
			tL += loss(instance);
		}
		System.out.print("Epoch: " + epoch + ", Loss: ");
		System.out.printf("%.8e\n", tL/trainingSet.size());
    }
    
    /**
     * Creates the neural network by initializing the inputs and 
     * generating the outputs
     * 
     * @param instance 
     */   
    private void generateNetwork(Instance instance) {
		inputNodes = new ArrayList<Node>();
		// for each attribute
		for (int i = 0; i < instance.attributes.size(); i++) {
			Node node = new Node(0);
			node.setInput(instance.attributes.get(i));
			inputNodes.add(node);
		}
		Node biasToHidden = new Node(1);
        inputNodes.add(biasToHidden);
        
        // for each hidden node
        for (int i = 0; i < hiddenNodes.size(); i++)
			hiddenNodes.get(i).calculateOutput(inputNodes, null);
        // for each output node
		for(int i = 0; i < outputNodes.size(); i++)
			outputNodes.get(i).calculateOutput(hiddenNodes, outputNodes);
    }
}
