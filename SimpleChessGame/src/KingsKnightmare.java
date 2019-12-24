////////////////////////////////////////////////////////
//
// File name: KingsKnightmare.java
//
// @authors: abhanshu, Nick Klabjan
//
// Description: Uses different search methods to find 
//              the solution, if there is one, to a 
//				game similar to chess only involving
//				a knight and a king.
///////////////////////////////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

/**
 * Data structure to store each node.
 */
class Location {
	private int x;
	private int y;
	private Location parent;

	public Location(int x, int y, Location parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Location getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Location) {
			Location loc = (Location) obj;
			return loc.x == x && loc.y == y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * (hash + x);
		hash = 31 * (hash + y);
		return hash;
	}
}

public class KingsKnightmare {
	//represents the map/board
	private static boolean[][] board;
	//represents the goal node
	private static Location king;
	//represents the start node
	private static Location knight;
	//y dimension of board
	private static int n;
	//x dimension of the board
	private static int m;
	//enum defining different algo types
	enum SearchAlgo{
		BFS, DFS, ASTAR;
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			//loads the input file and populates the data variables
			SearchAlgo algo = loadFile(args[0]);
			if (algo != null) {
				switch (algo) {
					case DFS :
						executeDFS();
						break;
					case BFS :
						executeBFS();
						break;
					case ASTAR :
						executeAStar();
						break;
					default :
						break;
				}
			}
		}
	}
	
	/**
	 * Implementation of Astar algorithm for the problem
	 */
	private static void executeAStar() {
		//all the possible moves in the X direction for the knight
		int movesX[] = new int[8];
		movesX[0] = 2;
		movesX[1] = 1;
		movesX[2] = -1;
		movesX[3] = -2;
		movesX[4] = -2;
		movesX[5] = -1;
		movesX[6] = 1; 
		movesX[7] = 2;
		//all the possible moves in the Y direction for the knight
		int movesY[] = new int[8];
		movesY[0] = 1;
		movesY[1] = 2;
		movesY[2] = 2;
		movesY[3] = 1;
		movesY[4] = -1;
		movesY[5] = -2;
		movesY[6] = -2; 
		movesY[7] = -1;

		//PriorityQ to act as the Frontier where different locations will be stored
		PriorityQ<Location> q = new PriorityQ<Location>();	
		//heuristic function of the root node, ie. knight
		int heuristicFun = Math.abs(knight.getX()-king.getX()) + Math.abs(knight.getY()-king.getY());
		//the current location to be expanded
		Location curr;
		//number of locations expanded
		int numExpLocs = 0;
		//the total cost of a location
		int totalCost = 0;
		//stores the cost from the knight to the current location
		int costFromKnight = 0;
		//location of child node
		Location newLoc;
		//an arrayList to store the expanded locations
		ArrayList<Location> explored = new ArrayList<Location>();
		//adds the root node, ie knight, to the frontier
		q.add(knight, heuristicFun);
		//while the frontier isn't empty
		while (!q.isEmpty()) {
			//saves location to curr
			curr = q.peek().getKey();
			//the total cost, ie,  g(n) + h(n)
			totalCost = q.peek().getValue();
			//the heuristic Function for the curr node
			heuristicFun = Math.abs(curr.getX()-king.getX()) + Math.abs(curr.getY()-king.getY());
			//calculates the cost from the knight to curr, ie g(n)
			costFromKnight = totalCost - heuristicFun;
			//if the current location is same location as king, ie. solution path is found
			if (curr.equals(king)) {
				//arraylist to store the solution path
				ArrayList<Location> solutionPath = new ArrayList<Location>();
				//adds all the parents of curr to the solution path
				while(curr != null) {
					solutionPath.add(curr);
					curr = curr.getParent();
				}
				//prints out the solution path in reverse order
				for(int j = solutionPath.size() - 1; j >= 0; j--) {
					System.out.println(solutionPath.get(j));
				}
				//prints out number of expanded nodes 
				System.out.println("Expanded Nodes: " + numExpLocs);
				return;
			}
			//for each move that the knight could make
			for (int i = 0; i < 8; i++) {
				//stores the new location of the knight after one move
				newLoc = new Location(curr.getX() + movesX[i], curr.getY() + movesY[i], curr);
				//checks to see if the move is valid
				if (newLoc.getX() < m && newLoc.getX() >= 0 && newLoc.getY() >= 0 && newLoc.getY() < n) {
					//update the heuristic function for that new location
					heuristicFun = Math.abs(newLoc.getX()-king.getX()) + Math.abs(newLoc.getY()-king.getY());
					//updates the total cost for that new location
					totalCost = costFromKnight + heuristicFun + 3; 
					//if that location is valid on the board, not in frontier, and not already expanded
					if (!board[newLoc.getY()][newLoc.getX()] && !q.exists(newLoc) && !explored.contains(newLoc)) {
						//add the new location to the frontier
						q.add(newLoc, totalCost);
					}
					//if the location was already expanded or in the frontier, then update it if necessary
					else if ((3 + costFromKnight) < (q.getPriorityScore(newLoc) - heuristicFun)) {
							//modifies the entry of the new  location
							q.modifyEntry(newLoc, totalCost);
						}
				}
			}
			//removes the first element of queue
			q.poll();
			//adds element to explored
			explored.add(curr);
			//updates number of explored locations
			numExpLocs++;
		}
		//prints out when 
		System.out.println("NOT REACHABLE");
		System.out.println("Expanded Nodes: " + numExpLocs);
	}

	/**
	 * Implementation of BFS algorithm
	 */
	private static void executeBFS() {
		//array that stores the possible moves in the X direction
		int movesX[] = new int[8];
		movesX[0] = 2;
		movesX[1] = 1;
		movesX[2] = -1;
		movesX[3] = -2;
		movesX[4] = -2;
		movesX[5] = -1;
		movesX[6] = 1; 
		movesX[7] = 2;
		//array that stores the possible moves in the Y direction 
		int movesY[] = new int[8];
		movesY[0] = 1;
		movesY[1] = 2;
		movesY[2] = 2;
		movesY[3] = 1;
		movesY[4] = -1;
		movesY[5] = -2;
		movesY[6] = -2; 
		movesY[7] = -1;
		
		//queue to store the frontier of locations
		Queue<Location> q = new LinkedList<Location>();	
		//current location set to the root node
		Location curr = knight;
		//number of locations expanded
		int numExpLocs = 0;
		//arraylist storing the locations already explored
		ArrayList<Location> explored = new ArrayList<Location>();
		//adds the root location to the frontier
		q.add(curr);
		//while the frontier isn't empty
		while (!q.isEmpty()) {
			//removes first element of queue and sets it to curr
			curr = q.remove();
			//updates number of expanded locations
			numExpLocs++;
			//adds location to explored 
			explored.add(curr);
			//for each possible move
			for (int i = 0; i < 8; i++) {
				//creates a new location with the given move
				Location newLoc = new Location(curr.getX() + movesX[i], curr.getY() + movesY[i], curr);
				//if the new location is legal
				if (newLoc.getX() < m && newLoc.getX() >= 0 && newLoc.getY() >= 0 && newLoc.getY() < n) {
					//if the location is valid on the board, not in frontier, and not already explored
					if (!board[newLoc.getY()][newLoc.getX()] && !q.contains(newLoc) && !explored.contains(newLoc)) {
						//if newlocation equals king's position
						if (newLoc.equals(king)) {
							//array list to store the solution path 
							ArrayList<Location> solutionPath = new ArrayList<Location>();
							//while the new location's parent isn't null add it to solution path
							while(newLoc != null) {
								solutionPath.add(newLoc);
								newLoc = newLoc.getParent();
							}
							//for each element in solution path print it in reverse order
							for(int j = solutionPath.size() - 1; j >= 0; j--) {
								System.out.println(solutionPath.get(j));
							}
							//prints out number of expanded nodes
							System.out.println("Expanded Nodes: " + numExpLocs);
							return;
						}
						//adds new location to frontier
						q.add(newLoc);
					}
				}
			}
		}
		//if the queue is empty there is not solution path, therefore print NOT REACHABLE
		System.out.println("NOT REACHABLE");
		System.out.println("Expanded Nodes: " + numExpLocs);
	}
	
	/**
	 * Implemention of DFS algorithm
	 */
	private static void executeDFS() {
		//array of moves possible in the x direction
		int movesX[] = new int[8];
		movesX[0] = 2;
		movesX[1] = 1;
		movesX[2] = -1;
		movesX[3] = -2;
		movesX[4] = -2;
		movesX[5] = -1;
		movesX[6] = 1; 
		movesX[7] = 2;
		//array of moves possible in y direction
		int movesY[] = new int[8];
		movesY[0] = 1;
		movesY[1] = 2;
		movesY[2] = 2;
		movesY[3] = 1;
		movesY[4] = -1;
		movesY[5] = -2;
		movesY[6] = -2; 
		movesY[7] = -1;
		
		//stack to store the frontier of locations
		Stack<Location> st = new Stack<Location>();
		//current location set to knight initially
		Location curr = knight;
		//number of explored locations
		int numExpLocs = 0;
		//Arraylist storing explored locations
		ArrayList<Location> explored = new ArrayList<Location>();
		//pushes root location, ie knight, to the frontier
		st.push(curr);
		//while stack isn't empty
		while (!st.isEmpty()) {
			//sets the last location in stack to curr
			curr = st.pop();
			//adds current location to explored
			explored.add(curr);
			//increments number of explored locations
			numExpLocs++;
			//for each move
			for (int i = 0; i < 8; i++) {
				//create a new location given that move
				Location newLoc = new Location(curr.getX() + movesX[i], curr.getY() + movesY[i], curr);
				//if location is valid
				if (newLoc.getX() < m && newLoc.getX() >= 0 && newLoc.getY() >= 0 && newLoc.getY() < n) {
					//if location is valid on board, not in stack, and not already explored
					if (!board[newLoc.getY()][newLoc.getX()] && !st.contains(newLoc) && !explored.contains(newLoc)) {
						//if new loc is equal to the king's location
						if (newLoc.equals(king)) {
							//arraylist to store the solution path
							ArrayList<Location> solutionPath = new ArrayList<Location>();
							//while the parent of new location isn't null add it to solutoin path
							while(newLoc != null) {
								solutionPath.add(newLoc);
								newLoc = newLoc.getParent();
							}
							//for each element in solution path print it in reverse
							for(int j = solutionPath.size() - 1; j >= 0; j--) {
								System.out.println(solutionPath.get(j));
							}
							//print expanded nodes
							System.out.println("Expanded Nodes: " + numExpLocs);
							return;
						}
						//adds new location to frontier
						st.push(newLoc);
					}
				}
			}
		}
		//prints NOT REACHABLE if stack is empty
		System.out.println("NOT REACHABLE");
		System.out.println("Expanded Nodes: " + numExpLocs);
	}
	
	/**
	 * 
	 * @param filename
	 * @return Algo type
	 * This method reads the input file and populates all the 
	 * data variables for further processing
	 */
	private static SearchAlgo loadFile(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			SearchAlgo algo = SearchAlgo.valueOf(sc.nextLine().trim().toUpperCase());
			n = sc.nextInt();
			m = sc.nextInt();
			sc.nextLine();
			board = new boolean[n][m];
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					if (line.charAt(j) == '1') {
						board[i][j] = true;
					} else if (line.charAt(j) == 'S') {
						knight = new Location(j, i, null);
					} else if (line.charAt(j) == 'G') {
						king = new Location(j, i, null);
					}
				}
			}
			sc.close();
			return algo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
