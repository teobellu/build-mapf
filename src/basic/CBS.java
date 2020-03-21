package basic;

import java.util.Arrays;
import java.util.Optional;
import java.util.PriorityQueue;

public class CBS {
	
	static boolean useFlowtimeMeasure = true;
	
	private static MAPF mapfInstance;

	public static synchronized Optional<Path[]> solve(MAPF mapf) {
		// initialize the search
		CBS.mapfInstance = mapf;
		EPEA.mapfInstance = mapf;
		int i, agent;
		int[] conflict;
		CBSNode current;
		Optional<CBSNode> child;
		
		// start the search
		PriorityQueue<CBSNode> open = new PriorityQueue<CBSNode>();
		
		CBSNode root = generateRoot();
		open.add(root);
		
		while(!open.isEmpty()) {
			
			// pop the best node in open
			current = open.poll();
			
			// conflict <a1, a2, x, y, t>
			conflict = Path.getFirstConflict(current.solution);
			
			// goal test
			if (conflict == null) {
				System.gc();
				return Optional.of(current.solution);
			}
			
			for(i = 0; i < 2;) {
				agent = conflict[i++];
				
				child = generateChild(current, conflict, agent);
				
				if (child.isPresent()) {
					open.offer(child.get());
				}
			}
		}
		
		return Optional.empty();
	}
	
	private static CBSNode generateRoot() {
		var root = new CBSNode(mapfInstance.k);
		
		// find a path for each agent without using constraints
		int i;
		var paths = new Path[mapfInstance.k];
		for(i = 0; i < mapfInstance.k;)
			paths[i] = EPEA.solve(i++).orElseThrow();
		
		// set the solution measure
		root.solution = paths;
		root.gUpdate();
		return root;
	}
	
	private static Optional<CBSNode> generateChild(CBSNode father, int[] conflict, int agent) {
		var child = new CBSNode(father);
		
		// add the new constraints
		child.setConstraint(father, agent, conflict);

		// get all the constraints
		var constraints = child.getConstraints(agent);
		
		// compute the new path
		var optionalPath = EPEA.solve(agent, constraints);
		
		// no path implies no child
		if (optionalPath.isEmpty())
			return Optional.empty();

		// update agent's path and solution cost
		child.updatePath(father, optionalPath.get(), agent);
		
		return Optional.of(child);
	}
}

class CBSNode implements Comparable<CBSNode>{
	
	// idea is unlink the entire node and wait for the Java garbage collectors
	CBSConstraintLink constraintLink; 
	public Path[] solution;
	public int g = 0; // solution cost
	private int h; // not used
	
	public CBSNode(int k) {
		solution = new Path[k];
	}
	
	public CBSNode(CBSNode father) {
		solution = Arrays.copyOf(father.solution, father.solution.length);
	}
	
	public void setConstraint(CBSNode father, int agent, int[] conflict) {
		constraintLink = new CBSConstraintLink(father.constraintLink, agent, conflict);
	}
	
	public int[] getConstraints(int agent) {
		return constraintLink.getConstraints(agent);
	}
	
	public void updatePath(CBSNode father, Path path, int agent) {
		solution[agent] = path;
		
		// the method modifies also the solution cost
		int oldPathSize = father.solution[agent].size();
		int newPathSize = path.size();
		
		g = (CBS.useFlowtimeMeasure) ?
				father.g - oldPathSize + newPathSize : 
				Math.max(father.g, newPathSize);
	}
	
	public void gUpdate() {
		g = (CBS.useFlowtimeMeasure) ? 
				Path.flowtime(solution) : 
				Path.makespan(solution);
	}
	
	@Override
	public int compareTo(CBSNode aNode) {
		switch(Integer.compare(g + h, aNode.g + aNode.h)) {
			case -1: return -1;
			case 1: return 1;
		}
		// here the two nodes have the same f
		if (h < aNode.h) return -1;
		if (h > aNode.h) return 1;
		return 0;
	}
	
}

class CBSConstraintLink {
	
	// final because we cannot modify them
	final CBSConstraintLink father;
	final int agent;
	final int x;
	final int y;
	final int t;
	
	CBSConstraintLink(CBSConstraintLink father, int agent, int[] conflict){
		this.father = father;
		this.agent = agent;
		this.x = conflict[2];
		this.y = conflict[3];
		this.t = conflict[4];
	}
	
	int[] getConstraints(int agent) {
		// in this way we safe memory
		// Java garbage collectors will delete this array soon
		
		// compute the size of the output
		int size = 0, tMin = 0;
		var link = this;
		for (; link != null; link = link.father) {
			if (link.agent == agent) {
				// constraint at time t implies path of length at least t+1
				tMin = Math.max(tMin, link.t + 1);
				size++;
			}
		}
		// three times the computed size + 1 for tMin
		int constraints[] = new int[(size << 1) + size + 1];
		
		// compute constraints using back propagation
		size = 0;
		constraints[size++] = tMin;
		for (link = this; link != null; link = link.father) {
			if (link.agent == agent) {
				constraints[size++] = link.x;
				constraints[size++] = link.y;
				constraints[size++] = link.t;
			}
		}
		return constraints;
	}
	
}
