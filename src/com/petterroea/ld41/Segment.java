package com.petterroea.ld41;

import java.awt.Color;
import java.awt.Graphics;

public interface Segment {
	public void render(Graphics g, int beginOffset, GamePuppetMaster master);
	
	public void begin() ;
	
	public boolean hasEnded() ;
	
	public void forceDone() ;

	public boolean hasStarted() ;
	
	public boolean isAutomationSegment();
}
