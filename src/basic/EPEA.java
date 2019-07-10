package basic;

import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;

public class EPEA {
	
	// 2 ^ -7 = 0.0078125, avoid freeze and crash with sleeping
	private static final double SYSTEM_GARBAGE_PROBABILITY = 0.0078125; 
	private static final double SYSTEM_SLEEP_PROBABILITY = 0.0078125;
	static final int SYSTEM_SLEEP_DURATION = 1; // milliseconds
	
	static boolean iterationControlsOn = false;
	static boolean diagonalMovesAllowed = false;
	static boolean planUnderConstraints = false;
	static int numberOfSleeps = 0;
	
	public static MAPF mapfInstance;
	
	private static void iterationControls() {
		double r = Math.random();
		if (r <= SYSTEM_GARBAGE_PROBABILITY) 	System.gc();
		if (r <= SYSTEM_SLEEP_PROBABILITY)		try {	Thread.sleep(SYSTEM_SLEEP_DURATION);
														numberOfSleeps++;						} 
												catch (InterruptedException e) {};
	}
	
	public static synchronized Optional<Path> solve(int agent) {
		int xStart = mapfInstance.xs[agent];
		int yStart = mapfInstance.ys[agent];
		int xGoal = mapfInstance.xg[agent];
		int yGoal = mapfInstance.yg[agent];
		iterationControlsOn = true;
		diagonalMovesAllowed = false;
		planUnderConstraints = false;
		return solver(xStart, yStart, xGoal, yGoal, null);
	}
	
	public static synchronized Optional<Path> solve(int agent, int[] constraints) {
		int xStart = mapfInstance.xs[agent];
		int yStart = mapfInstance.ys[agent];
		int xGoal = mapfInstance.xg[agent];
		int yGoal = mapfInstance.yg[agent];
		iterationControlsOn = true;
		diagonalMovesAllowed = false;
		planUnderConstraints = true;
		return solver(xStart, yStart, xGoal, yGoal, constraints);
	}
	
	private static Optional<Path> solver(int xStart, int yStart, int xGoal, int yGoal, int[] constraints) {
		int i, j, t, xChild, yChild, gChild, hChild, gMin = 0;
		EPEANodeSimple current;
		Map map = mapfInstance.map;
		
		PriorityQueue<EPEANodeSimple> open = new PriorityQueue<EPEANodeSimple>();
		HashSet<EPEANodeSimple> closed = new HashSet<EPEANodeSimple>();
		
		EPEANodeSimple start = new EPEANodeSimple();
		start.g = 0;
		start.h = distance(xStart, yStart, xGoal, yGoal);;
		start.x = xStart;
		start.y = yStart;
		
		open.add(start);
		
		if (planUnderConstraints) {
			gMin = constraints[0];
		}
		
		while(!open.isEmpty()) {
			// pop the best node in open
			current = open.poll();
			
			// do some optional controls
			if (iterationControlsOn)
				iterationControls();
			
			// goal test
			if(current.x == xGoal && current.y == yGoal && current.g >= gMin) { 
				System.gc();
				return Optional.of(current.buildPath());
			}
			
			// children generation
			for(i = -1; i <= 1; i++)
				nextMove: for(j = -1; j <= 1; j++) {
					// diagonal moves are optional
					if (!diagonalMovesAllowed && i != 0 && j != 0)
						continue;
					
					xChild = current.x + i;
					yChild = current.y + j;
					
					// out of map
					if (xChild < 0 || yChild < 0 || xChild >= map.width() || yChild >= map.height())
						continue;
					
					// obstacle in the map
					if (!map.get(xChild,yChild))
						continue;
					
					gChild = current.g + 1;
					hChild = distance(xChild, yChild, xGoal, yGoal);
					
					// operator selection function (OSF)
					if 		(current.deltaf == 0 && current.h - hChild == 1)	{}
					else if (current.deltaf == 1 && current.h - hChild == 0)	{}
					else if (current.deltaf == 2 && current.h - hChild == -1) 	{}
					else	continue;
					
					// constraints
					if (planUnderConstraints) {
						for (t = 1; t < constraints.length; t+=3) {
							if (xChild == constraints[t] &&
								yChild == constraints[t+1] &&
								gChild == constraints[t+2])
									continue nextMove;
						}
					}
				
					// child is found
					EPEANodeSimple childNode = new EPEANodeSimple();
					childNode.g = gChild;
					childNode.h = hChild;
					childNode.x = xChild;
					childNode.y = yChild;
					childNode.father = current;
					
					// check duplicates
					if (open.contains(childNode) || closed.contains(childNode))
						continue;
					
					open.offer(childNode);
				}
			
			// re-insert the node to open
			if (current.canBeReInsert()) {
				current.deltaf++;
				open.offer(current);
			}	
			// because meanwhile the node is in open
			else
				closed.add(current);
		}
		
		return Optional.empty();
	}
	
	private static int distance(int x1, int y1, int x2, int y2) {
		x1 = Math.abs(x1 - x2);
		y1 = Math.abs(y1 - y2);
		return diagonalMovesAllowed ? Math.max(x1, y1) : (x1 + y1);
	}

}

class EPEANodeSimple implements Comparable<EPEANodeSimple>{
	
	int g;
	int h;
	int deltaf = 0; // visit in EPEA*
	int x;
	int y;
	
	EPEANodeSimple father; // may not used in EPEA* for MDD
	
	public boolean canBeReInsert() {
		return deltaf < 2;
	}
	
	public Path buildPath() {
		Path path = new Path(g + 1);
		var node = this;
		for (; node != null; node = node.father)
			path.set(node.x, node.y, g--);
		return path;
	}
	
	@Override
	public int compareTo(EPEANodeSimple aNode) {
		switch(Integer.compare(g + h + deltaf, aNode.g + aNode.h + aNode.deltaf)) {
			case -1: return -1;
			case 1: return 1;
		}
		// here the two nodes have the same f
		if (h < aNode.h) return -1;
		if (h > aNode.h) return 1;
		if (deltaf < aNode.deltaf) return -1;
		if (deltaf > aNode.deltaf) return 1;
		return 0;
	}
	
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
            return true;
        }
		if (anObject instanceof EPEANodeSimple) {
			EPEANodeSimple aNode = (EPEANodeSimple)anObject;
            return 
            	x == aNode.x && 
            	y == aNode.y && 
            	(EPEA.planUnderConstraints ? g == aNode.g : true);
		}
        return false;
	}
	
	@Override
	public int hashCode() {
		int result = 31;
		// ensure equals implies same hashCode
		result = 37 * result + x;
		result = 37 * result + y;
		result = 37 * result + h;
		return result;
	}
	
	@Override
	public String toString() {
		return "{g:" + g + ", h:" + h + ", point:<:" + x + "," + y + ">, deltaf:" + deltaf + "}";
	}
	
}