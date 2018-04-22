package com.petterroea.ld41;

import java.util.Random;

public class City {
	//For name generation
	
	private final String[] prefixes = {"Las", "Saint", "East", "North", "Mountain"};
	private final String[] postfixes = {"ville", "town", "field", "wood"};
	private final String[] names = {"Gokk", "West", "South", "North", "East", "Green", "Clean", "City"};
	
	
	private int x, y;
	private String name;
	public City(int x, int y) {
		this.x = x;
		this.y = y;
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		int prefixVal = rand.nextInt(prefixes.length+1);
		if(prefixVal!=prefixes.length) {
			sb.append(prefixes[prefixVal]);
			sb.append(" ");
		}
		int nameVal = rand.nextInt(names.length);
		sb.append(names[nameVal]);
		int postfixVal = rand.nextInt(postfixes.length+1);
		if(postfixVal != postfixes.length) {
			sb.append(postfixes[postfixVal]);
		}
		this.name = sb.toString();
		System.out.println("Generated city with name " + this.name);
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
}
