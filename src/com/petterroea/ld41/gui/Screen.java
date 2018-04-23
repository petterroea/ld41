package com.petterroea.ld41.gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class Screen {
	public Screen() {
		
	}
	
	public void load() {
		
	}
	
	public void onTransition() {
		
	}
	
	public abstract void render(Graphics g, int deltaMs);
	public abstract void doLogic(int deltaMs);
	public abstract boolean canTransition();
	

	public void keyPressed(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		
		
	}

	public void mouseDragged(MouseEvent e) {
		
		
	}

	public void mouseMoved(MouseEvent e) {
		
		
	}

	public void mouseClicked(MouseEvent e) {
		
		
	}

	public void mousePressed(MouseEvent e) {
		
		
	}

	public void mouseReleased(MouseEvent e) {
		
		
	}

	public void mouseEntered(MouseEvent e) {
		
		
	}

	public void mouseExited(MouseEvent e) {
	
		
	}
	
}
