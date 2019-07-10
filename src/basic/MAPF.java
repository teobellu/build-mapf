package basic;

import java.util.Random;

public class MAPF {
	
	public int k;
	public int[] xs;
	public int[] ys;
	public int[] xg;
	public int[] yg;
	public Map map;
	
	public void generateStartsGoals() {
		Random rand = new Random();

        int x;
        int y;
        
        int[] xs = new int[k];
        int[] ys = new int[k];
        int[] xg = new int[k];
        int[] yg = new int[k];
        
        boolean[][] starts = new boolean[map.width()][map.height()];
        boolean[][] goals = new boolean[map.width()][map.height()];


        // Choose random goal locations
        for (int i = 0; i < k; i++)
        {
            x = rand.nextInt(map.width());
            y = rand.nextInt(map.height());
            if (goals[x][y] || !map.get(x, y))
                i--;
            else
            {
                goals[x][y] = true;
                xg[i] = x;
                yg[i] = y;
            }
        }
        
        // Choose random start locations
        for (int i = 0; i < k; i++)
        {
            x = rand.nextInt(map.width());
            y = rand.nextInt(map.height());
            if (starts[x][y] || !map.get(x, y))
                i--;
            else
            {
            	starts[x][y] = true;
            	xs[i] = x;
                ys[i] = y;
            }
        }          
        
        this.xs = xs;
        this.ys = ys;
        this.xg = xg;
        this.yg = yg;
	}

}
