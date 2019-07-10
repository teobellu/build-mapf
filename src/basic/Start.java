package basic;

public class Start {
	
	public static void main(String[] args) {
		
		/**
		 * Generate the map
		 */
		Map map = new Map(20, 11);
		map.freeAll();
		int shift = 4;
		for (int k = 19; k >= shift;)
			map.lock(k--, 3);
		for (int k = 0; k <= (19 - shift);)
			map.lock(k++, 7);
		map.free(16, 3);
		map.free(15, 3);
		map.free(3, 7);
		map.free(4, 7);
		
		/**
		 * Generate the problem
		 */
		MAPF mapf = new MAPF();
		mapf.map = map;
		
		for(int i = 0; i < 5; ) {
			
			/**
			 * Set the number of agents
			 */
			if (i == 0) mapf.k = 1;
			if (i == 1) mapf.k = 4;
			if (i == 2) mapf.k = 7;
			
			/**
			 * Generates start goal position
			 */
			mapf.generateStartsGoals();
			
			/**
			 * Run EPEA* w/ diagonalMovesAllowed = false, edgeConflicts = false
			 */
			Path[] solution = CBS.solve(mapf).get();
			
			/**
			 * Show solution
			 */
			int graphSpeed = 100; // 50 fast, 500 slow
			View.show(map, solution, 50, graphSpeed, false);
			
			/**
			 * Exit
			 */
			if (++i == 5)
				System.exit(0);
		}
	}

}
