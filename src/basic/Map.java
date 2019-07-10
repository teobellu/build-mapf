package basic;

import java.util.Arrays;

public class Map {
	
	private boolean[] data;
	
	private int width;
	
	// saved in order to avoid calculations
	private int height; 
	
	public Map(int width, int height) {
		data = new boolean[width * height];
		this.width = width;
		this.height = height;
	}
	
	public void free(int x, int y) {
		data[x + y * width] = true;
	}
	
	public void freeAll() {
		Arrays.fill(data, true);
	}
	
	public void lock(int x, int y) {
		data[x + y * width] = false;
	}
	
	public boolean get(int x, int y) {
		return data[x + y * width];
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
            return true;
        }
		if (anObject instanceof Path) {
            Map aMap = (Map)anObject;
            return Arrays.equals(data, aMap.data);
		}
        return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
	
	@Override
	public String toString() {
		if (data.length == 0)
			return "{}";
		
		String s = "";
		
		int y = data.length / width - 1;
		int x;
		for(;;) {
			for(x = 0; x < width;)
				s += data[x++ + y * width] ? "." : "@";
			if (y-- == 0)
				return s;
			s += "\n";
		}
	}
	

}
