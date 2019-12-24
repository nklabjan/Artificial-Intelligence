////////////////////////////////////////////////////////
//
// @author Nick Klabjan
//
// File name: studentAI.java
//
// Dewscription: Implements the computer player for the game using
//				 alpha-beta search.
//
////////////////////////////////////////////////////////

/**
 * Description: Creates a computer player that uses alpha-beta search alg.
 * 
 * @author Nick_Klabjan
 *
 */
public class studentAI extends Player {
	//maxDepth to start the search at
    private int maxDepth;

    /**
     * Sets the maxDepth for the search algorithm.
     * 
     * @param maxDepth, the max depth to be search too
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Uses alpha-beta search to update the data member move (which will be returned by 
     * the getMove() method to the Match class that is controlling the game environment). 
     * 
     * @param state, the state of the board
     */
    public void move(BoardState state) {
        move = alphabetaSearch(state, maxDepth);
    }

    /**
     * Starts the alpha-beta search algorithm and keeps track of the best move.
     * 
     * @param state, the state of the board
     * @param maxDepth, the depth to be searched to
     * @return the best move 
     */
    public int alphabetaSearch(BoardState state, int maxDepth) {
    	//the current state of the board
    	BoardState curr;
    	//sets v and alpha to negative infinity
    	int v = Integer.MIN_VALUE;
    	int alpha = Integer.MIN_VALUE;
    	//sets beta to positive infinity
    	int beta = Integer.MAX_VALUE;
    	//the best move that will be made
    	int bMove = 0;
    	//stores the previous value of v
    	int prevV = v;
    	//keeps track of the number of possible moves
    	int posMoves = 0;
    	//allow to traverse through all the possible moves
    	int i = 0;
    	//while i is less than 6
    	while(i < 6) {
    		//if move, i, is legal for player 1
    		if (state.isLegalMove(1,i)) {
    			//increments possible moves
    			posMoves++;
    			//updates current state after move is applied
    			curr = state.applyMove(1,i);
    			//sets the new v value for that curr state
    			v = Math.max(v, minValue(curr, maxDepth, maxDepth - 1, alpha, beta));
    			//if the previous V value doesn't equal the current v value
    			if(prevV != v) {
    				//then we found a better move;
    				bMove = i;
    			}
    			//set v to the previous V
    			prevV = v;
    		}
    		//if the v value is greater than or equal to beta, then prune the tree
    		if (v >= beta) {
    			//return the bestMove
    			return bMove;
    		}
    		//set alpha to be the max between alpha and v
    		alpha = Math.max(alpha, v);
    		//increment the move to be looked at
    		i++;
    	}
    	//if there are no more possible moves
    	if(posMoves == 0) {
    		//return the sbe value of the current state
    		return sbe(state);
    	}
    	//returns the best move
    	return bMove;	
    }

    /**
     * This function will search for the minimax value associated with the best move for the MAX player. 
     * The search should be cut off when the current depth equals to the maximum allowed depth. 
     * It is important to note that we will also call the SBE function to evaluate the game state when the 
     * game is over, i.e., when someone has won the game. The only condition for determining a leaf node 
     * (besides having reached maximin depth) is that there are no legal moves for the player to make, 
     * effectively ending the game at that state.
     * 
     * @param state, the game state that the MAX player is currently searching from
     * @param maxDepth, the maximum search depth allowed
     * @param currentDepth, the current depth in the search tree
     * @param alpha, the alpha value
     * @param beta, the beta value
     * @return the minimaxValue corresponding to the best move for the Max player
     */
    public int maxValue(BoardState state, int maxDepth, int currentDepth, int alpha, int beta) {
    	//if the currentDepth is equal to 0
    	if (currentDepth == 0) {
    		//return the sbe for that state
    		return sbe(state);
    	}
    	//keeps track of possible moves
    	int posMoves = 0;
    	//current board state
    	BoardState curr;
    	//sets v to negative infinity 
    	int v = Integer.MIN_VALUE;
    	//counter for the moves
    	int i = 0;
    	//while i is less than 6
    	while (i < 6) {
    		//if the move is legal
    		if (state.isLegalMove(1,i)) {
    			//apply the move
    			curr = state.applyMove(1,i);
    			//increment posMoves
    			posMoves++;
    			//set the new value of v
    			v = Math.max(v, minValue(curr, maxDepth, currentDepth - 1, alpha, beta));
    		}
    		//if v is greater than or equal to beta
    		if (v >= beta) {
    			//returns sbe for that state
    			return v;
    		}
    		//set alpha to the max value between alpha and v
    		alpha = Math.max(alpha,v);
    		//increment i
    		i++;
    	}
    	//if no more possible moves 
    	if (posMoves == 0) {
    		//return score for that state
    		return sbe(state);
    	}
    	//return v
    	return v;
    }

    /**
     * This function will search for the minimax value associated with the best move for the MIN player. 
     * The search should be cut off when the current depth equals to the maximum allowed depth. 
     * It is important to note that we will also call the SBE function to evaluate the game state when the 
     * game is over, i.e., when someone has won the game. The only condition for determining a leaf node 
     * (besides having reached maximin depth) is that there are no legal moves for the player to make, 
     * effectively ending the game at that state.
     * 
     * @param state, the game state that the MIN player is currently searching from
     * @param maxDepth, the maximum search depth allowed
     * @param currentDepth, the current depth in the search tree
     * @param alpha, the alpha value
     * @param beta, the beta value
     * @return the minimaxValue corresponding to the best move for the MIN player
     */
    public int minValue(BoardState state, int maxDepth, int currentDepth, int alpha, int beta) {
    	//if the currentDepth is equal to 0
    	if (currentDepth == 0) {
    		//return the sbe for that state
    		return sbe(state);
    	}
    	//keeps track of possible moves
    	int posMoves = 0;
    	//current board state
    	BoardState curr = null;
    	//sets v to negative infinity 
    	int v = Integer.MAX_VALUE;
    	//counter for the moves
    	int i = 0;
    	//while i is less than 6
    	while (i < 6) {
    		//if the move is legal
    		if (state.isLegalMove(2,i)) {
    			//apply the move
    			curr = state.applyMove(2,i);
    			//increment possible moves
    			posMoves++;
    			//set the new value of v
    			v = Math.min(v, maxValue(curr, maxDepth, currentDepth-1, alpha, beta));
    		}
    		//if v is greater than or equal to beta
    		if (v <= alpha) {
    			//returns sbe for that state
    			return v;
    		}
    		//beta is set to the min of the values beta and v
    		beta = Math.min(beta,v);
    		//increment i
    		i++;
    	}
    	//if no more possible moves
    	if (posMoves == 0) {
    		//return score for that state
    		return sbe(state);
    	}
    	//return v
    	return v;
    }

    /**
     * Takes a board state as input and returns its SBE value.
     * 
     * @param state, a board state
     * @return  the number of stones in the storehouse of the current player minus 
     *          the number of stones in the opponent’s storehouse
     */
    public int sbe(BoardState state){
    	return state.score[0] - state.score[1];
    }


}