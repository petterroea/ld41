package com.petterroea.ld41;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class TextSegment implements Segment{
	private static int msPerLetter = 25;
	
	private String message, name;
	private long renderStart = 0;
	public TextSegment(String name, String message) {
		this.message = message;
		this.name = name;
	}
	
	public void render(Graphics g, int beginOffset, GamePuppetMaster master) {
		if(renderStart == 0) {
			renderStart = System.currentTimeMillis();
		}
		long sinceStart = System.currentTimeMillis() - renderStart;
		String toRender = message;
		//System.out.println(sinceStart);
		if(sinceStart/msPerLetter < message.length()) {
			toRender = message.substring(0, (int)(sinceStart/msPerLetter));
		}
		
		g.setColor(Color.WHITE);
		if(name.equalsIgnoreCase("thought")) {
			g.setFont(g.getFont().deriveFont(Font.ITALIC, 12.0f));
		} else {
			g.setFont(g.getFont().deriveFont(12.0f));
			g.drawString(name, 10, beginOffset);
		}
		String[] lines = toRender.split("\n");
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i];
			g.drawString(line, 10, 30+beginOffset+i*15);
		}
	}
	
	public void begin() {
		renderStart = System.currentTimeMillis();
	}
	
	public boolean hasEnded() {
		return System.currentTimeMillis() - renderStart > message.length()*msPerLetter;
	}
	
	public void forceDone() {
		renderStart = System.currentTimeMillis()-1-message.length()*msPerLetter;
	}

	public boolean hasStarted() {
		return renderStart != 0;
	}

	@Override
	public boolean isAutomationSegment() {
		return false;
	}
}
