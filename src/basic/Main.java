package basic;

import view.View;

public class Main {
	
	public static void main(String[] args) {
		presentation();
	}
	
	private static void presentation() {
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
		
		MAPF mapf = new MAPF();
		
		mapf.map = map;
		
		for(int i = 0; i < 5; ) {
			
			if (i == 0) mapf.k = 1;
			if (i == 1) mapf.k = 4;
			if (i == 2) mapf.k = 7;
			
			mapf.generateStartsGoals();
			
			Path[] solution1 = CBS.solve(mapf).get();
			
			int vel = 100;
			
			View.show(map, solution1, 50, vel, false);
			
			if (++i == 5)
				System.exit(0);
		}
	}

}
