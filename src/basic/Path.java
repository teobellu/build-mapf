package basic;

import java.util.Arrays;
import java.util.BitSet;

public class Path {
	
	private int[] data;
	
	public Path(int size) {
		data = new int[size << 1];
	}
	
	public Path(int... elements) {
		data = elements.clone();
	}
	
	public Path(Path path) {
		data = Arrays.copyOf(path.data, path.data.length);
	}
	
	public void set(int x, int y, int t) {
		data[t <<= 1] = x;
		data[++t] = y;
	}
	
	public int x(int t) {
		return (t <<= 1) < data.length ? data[t] : data[data.length - 2];
	}
	
	public int y(int t) {
		return (t <<= 1) < data.length ? data[++t] : data[data.length - 1];
	}
	
	public int size() {
		return data.length >>> 1;
	}
	
	public static int flowtime(Path... paths) {
		int i = 0;
		for (var path : paths)
			i += path.size();
		return i;
	}
	
	public static int makespan(Path... paths) {
		int i = 0;
		for (var path : paths)
			i = Math.max(i, path.size());
		return i;
	}
	
	public static int[] getFirstConflict(Path... paths) {
		BitSet detector = new BitSet();
		int maxT = makespan(paths);
		
		int x, y, dxy, t, i;
		for (t = 0; t < maxT; t++) {
			for (i = 0; i < paths.length; i++) {
				x = paths[i].x(t);
				y = paths[i].y(t);
				// hashCode-like value using Cantor's pairing function
				dxy = (((x + y) * (x + y + 1)) >>> 1) + x;
		
				if (detector.get(dxy))
					// conflict found
					return getFirstConflict(paths, t, i, x, y, false);
				
				detector.set(dxy);
			}
			detector.clear();
		}
		return null;
	}
	
	private static int[] getFirstConflict(Path[] paths, int t, int iMax, int x, int y, boolean addHeader) {
		int i, xOther, yOther;
		for (i = 0; ; i++) {
			xOther = paths[i].x(t);
			yOther = paths[i].y(t);
			if (x == xOther && y == yOther) {
				// conflict found
				if (addHeader)
					return new int[] {0, i, iMax, x, y, t};
				else
					return new int[] {i, iMax, x, y, t};
			}
		}
	}
	
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
            return true;
        }
		if (anObject instanceof Path) {
            Path aPath = (Path)anObject;
            return Arrays.equals(data, aPath.data);
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
		
		String s = "{";
		
		int i = 0;
		for(;;) {
			s += "<" + data[i++] + "," + data[i++] + ">";
			if (i == data.length)
				return s + "}";
			s += " ";
		}
	}

}
