package com.petterroea.ld41.gui;

import java.awt.Color;
import java.awt.Graphics;

import com.petterroea.ld41.GamePuppetMaster;

public interface Segment {
	public void render(Graphics g, int beginOffset, GamePuppetMaster master);
	
	public void begin() ;
	
	public boolean hasEnded() ;
	
	public void forceDone() ;

	public boolean hasStarted() ;
	
	public boolean isAutomationSegment();

	public void mouseReleased();
}
