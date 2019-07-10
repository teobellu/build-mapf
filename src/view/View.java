package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import basic.Map;
import basic.Path;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 *   THIS CLASS IS NOT PART OF THE CORE
 *   
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 *
 */

public class View {
	
	private static JFrame frame = new JFrame("CBS+EPEA*");
	private static List<JLabel> labels = new ArrayList<>();
	private static String msg;
	
	public static JFrame show(Map map, Path[] solution, int textSize, int sleep_between_frames, boolean freezeInit){
		VideoSolution vs = new VideoSolution();
		for (Path p : solution)
			vs.addPath(p);
		return show(map, vs, textSize, sleep_between_frames, freezeInit);
	}
	
	private static JFrame show(Map map, VideoSolution solution,  int textSize, int sleep_between_frames, boolean freezeInit){
		frame.setVisible(false);
		frame = new JFrame("CBS+EPEA*");
		labels = new ArrayList<>();
		if (solution == null)
			return null;
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JPanel panel = new JPanel(new GridLayout(0,1));
		
		for(int y = 0; y < map.height(); y++) {
			JLabel l = new JLabel();
			l.setFont(new Font("Courier new", Font.PLAIN, textSize));
			l.setBackground(Color.black);
			l.setOpaque(true);
			labels.add(l);
			panel.add(l);
			
		}
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		panel.setVisible(true);
		frame.pack();
		
		int videoLength = solution.makespan();
		
		msg = "<font color=#000000>";
		nexttime: for(int t = 0; true; t++) {
			if (t > 0) {
				if (t == 1) {
					frame.pack();
					frame.pack();
					JOptionPane.showMessageDialog(null, "Simulation is ready! Press OK", "Info", JOptionPane.INFORMATION_MESSAGE);
					
				}
				if (t == videoLength +1 || freezeInit) {
					frame.pack();
					break nexttime;
				}
				
				try {
					Thread.sleep(sleep_between_frames);
				} catch (InterruptedException e) {
					
				}
				frame.pack();
				msg = "<font color=#000000>";
			}
			
			for(int y = map.height() - 1; y >= 0; y--) {
				nextcell: for(int x = 0; x < map.width(); x++) {
					VideoVertex v = new VideoVertex(x, y);
					//a in v e in g?
					for(int a = 0; a < solution.agents(); a++) {
						VideoVertex cellta = solution.getVertex(a, t);
						VideoVertex goala = solution.getVertex(a, 10000);
						if (v.equals(cellta) && v.equals(goala)) {
							String m10 = "<font color=#FFFF00>";
							if (a < 10)
								m10+="&#9679;";
							msg += m10 + a;
							continue nextcell;
						}
					}
					//a in v?
					for(int a = 0; a < solution.agents(); a++) {
						VideoVertex cellta;
						cellta = solution.getVertex(a, t);
						if (v.equals(cellta)) {
							String m10 = "<font color=#FFFF00>";
							if (a < 10)
								m10+="&#9679;";
							msg += m10 + a;
							continue nextcell;
						}
					}
					//g in v?
					for(int a = 0; a < solution.agents(); a++) {
						VideoVertex cellta;
						cellta = solution.getVertex(a, 100000); //get last element
						if (cellta == null)
							x = 1/0;
						if (v.equals(cellta)) {
							String m10 = "<font color=#00FFFF>"; //00fff7
							if (a < 10)
								m10+="&#9679;";
							msg += m10 + a;
							continue nextcell;
						}
					}
					//v in V?
					if (map.get(x, y))
						msg = addColoredTextToString(msg, "&#9619;&#9619;", "FFFFFF"); //E8E8E8
					else
						msg = addColoredTextToString(msg, "&#9619;&#9619;", "#222222");
				}
			
				//if (!msg.equals(labels.get(map.y - y - 1).getText()))
					labels.get(map.height() - y - 1).setText("<html>"+msg);
				msg = "<font color=#000000>";
			}
			
		}
		
		return frame;
		
	}
	
	public static String addColoredTextToString(String string, String text, String color) {
		if (isLastColor(string, color))
			return string.concat(text);
		return string.concat("</font><font color=" + color + ">" + text);
	}
	
	public static boolean isLastColor(String text, String color) {
		int indexTextColor = text.lastIndexOf("color")+6;
		String textColor = text.substring(indexTextColor,indexTextColor+7);;
		return textColor.equals(color);
	}

	
}

class VideoSolution {
	
	public ArrayList<ArrayList<VideoVertex>> vs;
	
	public VideoSolution() {
		vs = new ArrayList<>();
	}
	
	public void addPath(Path p) {
		ArrayList<VideoVertex> pa = new ArrayList<VideoVertex>();
		for (int i = 0; i < p.size(); i++) {
			pa.add(new VideoVertex(p.x(i), p.y(i)));
		}
		vs.add(pa);
	}
	
	public int agents() {
		return vs.size();
	}

	public int makespan() {
		int makespan = 0;
		for(ArrayList<VideoVertex> ls : vs)
			makespan = Math.max(makespan, ls.size());
		return makespan;
	}
	
	public VideoVertex getVertex(int agentId, int time) {
		int pathSize = vs.get(agentId).size();
		return vs.get(agentId).get(time < pathSize ? time : pathSize - 1);
	}

}

class VideoVertex {
	
	public int x;
	
	public int y;
	
	public VideoVertex(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof VideoVertex))
			return false;
		VideoVertex objVer = (VideoVertex) obj;
		return x == objVer.x && y == objVer.y;
	}

}
