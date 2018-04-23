package com.petterroea.ld41;

import java.util.Random;

public class Vor implements Waypoint{
	private int x, y;
	private String name;
	
	private final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public Vor(int x, int y) {
		this.x = x;
		this.y = y;
		
		Random rand = new Random();
		for(int i = 0; i < 4; i++) {
			name += alphabet.charAt(rand.nextInt(alphabet.length()));
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public Vector2 getVector() {
		return new Vector2(x, y);
	}
}
