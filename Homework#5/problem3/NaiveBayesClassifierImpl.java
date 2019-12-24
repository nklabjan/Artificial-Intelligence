import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.lang.Math;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {
	private Instance[] m_trainingData;
	private int m_v;
	private double m_delta;
	public int m_sports_count, m_business_count;
	public int m_sports_word_count, m_business_word_count;
	private HashMap<String,Integer> m_map[] = new HashMap[2];

  /**
   * Trains the classifier with the provided training data and vocabulary size
   */
  @Override
  public void train(Instance[] trainingData, int v) {
  	  m_trainingData = trainingData;
  	  // keeps track of the value
  	  int val = 0;
  	  m_v = v;
  	  // keeps track of index value
  	  int in = 0;
  	  // hashmap for sports and businesses
  	  m_map[0] = new HashMap<>();
  	  m_map[1] = new HashMap<>();
  	  
  	  // for each instance in the training data
  	  for(Instance inst : trainingData) {
  		  // if the label is sports label 
  		  if(inst.label == Label.SPORTS)
  			  in = 0;
  		  //if the label is business label
  		  else
  			  in = 1;
  		  // keeps track of index of words
  		  int i = 0;
  		  // while i is less than length of words
  		  while(i < inst.words.length) {
  			  // if it doesn't exist 
  			  if(m_map[in].get(inst.words[i]) == null)
  				  m_map[in].put(inst.words[i], 1);
  			  // if word isn't null
  			  else {
  				  val = m_map[in].get(inst.words[i]);
  				  m_map[in].replace(inst.words[i], val + 1);
  			  }
  			  // increment i
  			  i++;
  		  }
  	  }
  }

  /*
   * Counts the number of documents for each label
   */
  public void documents_per_label_count(Instance[] trainingData){
	m_sports_count = 0;
    m_business_count = 0;
	for(Instance instance : trainingData) {
		  if(instance.label == Label.BUSINESS)
			  m_business_count++;
		  else
			  m_sports_count++;
    }
  }

  /*
   * Prints the number of documents for each label
   */
  public void print_documents_per_label_count(){
  	  System.out.println("SPORTS=" + m_sports_count);
  	  System.out.println("BUSINESS=" + m_business_count);
  }


  /*
   * Counts the total number of words for each label
   */
  public void words_per_label_count(Instance[] trainingData){
	// word count for sports  
	m_sports_word_count = 0;
	// word count for business
	m_business_word_count = 0;
	
	// for each instance in trainig Data
	for(Instance instance : trainingData) {
		if(instance.label == Label.BUSINESS)
			m_business_word_count += instance.words.length;
		else
			m_sports_word_count += instance.words.length;
	}
  }

  /*
   * Prints out the number of words for each label
   */
  public void print_words_per_label_count(){
  	  System.out.println("SPORTS=" + m_sports_word_count);
  	  System.out.println("BUSINESS=" + m_business_word_count);
  }

  /**
   * Returns the prior probability of the label parameter, i.e. P(SPORTS) or P(BUSINESS)
   */
  @Override
  public double p_l(Label label) {
	documents_per_label_count(m_trainingData);
	double prob = 0;
	
    // if the label is business
	if(label == Label.BUSINESS)
		prob = (double)m_business_count / (double)m_trainingData.length;
	// if label is sports
	else
		prob = (double)m_sports_count /  (double)m_trainingData.length;
	// return the probability
    return prob;
  }

  /**
   * Returns the smoothed conditional probability of the word given the label, i.e. P(word|SPORTS) or
   * P(word|BUSINESS)
   */
  @Override
  public double p_w_given_l(String word, Label label) {
	// stores return value  
    double prob = 0;
    // this is delta's value
    m_delta = 0.00001;
    // stores index 
    int in = 0;
    
    // if label is business, then index is 1
	if(label == Label.BUSINESS)
		in = 1;
	// if label is sports, then index is 0
	else
		in = 0;
    
	// iterator to iterate through values() of the label
	Iterator<Integer> iterator = m_map[in].values().iterator();
	// stores denominator value
    double denom = 0;
   
    // while there is a next value
    while(iterator.hasNext())
    	// add each value to denominator
    	denom += iterator.next();
    denom += m_delta * m_v;
    
    // if the word isn't null
    if(m_map[in].get(word) != null)
    	prob = (m_map[in].get(word) + m_delta) / denom;
    // if word is null
    else
    	prob = m_delta / denom;
    
    // returns probability value
    return prob;
  }

  /**
   * Classifies an array of words as either SPORTS or BUSINESS.
   */
  @Override
  public ClassifyResult classify(String[] words) {

	  
    ClassifyResult cr = new ClassifyResult();
    cr.label = Label.BUSINESS;
    cr.log_prob_sports = 0;
    cr.log_prob_business = 0;
    
    // counter to iterate through words
    int i = 0;
    
    //while there are words
    while(i < words.length) {
    	cr.log_prob_sports += Math.log(p_w_given_l(words[i], Label.SPORTS));
    	cr.log_prob_business += Math.log(p_w_given_l(words[i], Label.BUSINESS));
    	// increment i
    	i++;
    }
	cr.log_prob_sports += Math.log(p_l(Label.SPORTS));
	cr.log_prob_business += Math.log(p_l(Label.BUSINESS));
	
	// compares the two probabilities
	if(cr.log_prob_sports >= cr.log_prob_business)
		cr.label = Label.SPORTS;
	
	//returns a classifyResult object
    return cr; 
  }
  
  /*
   * Constructs the confusion matrix
   */
  @Override
  public ConfusionMatrix calculate_confusion_matrix(Instance[] testData){
	  
    int T1 = 0;
    int F1 = 0;
    int F2 = 0;
    int T2 = 0;
   
    // counter to traverse testData
    int i = 0;
    
    // while it doesnt exceed testData's length 
    while(i < testData.length) {
    	// creates a new ClassifyResult
    	ClassifyResult r = classify(testData[i].words);
    	// if the label is Sports
    	if(r.label == Label.SPORTS) {
    		if(testData[i].label == Label.SPORTS)
    			T1++;
    		else
    			F1++;
    	}
    	// if the label is business
    	else {
    		if(testData[i].label == Label.SPORTS)
    			F2++;
    		else
    			T2++;
    	}
    	i++;
    }
    // returns new Confusion Matrix
    return new ConfusionMatrix(T1,F1,F2,T2);
  }
}
