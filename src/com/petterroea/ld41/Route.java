package com.petterroea.ld41;

import java.util.Iterator;
import java.util.LinkedList;

public class Route {
	
	private LinkedList<Waypoint> route = new LinkedList<Waypoint>();
	public Route() {
		
	}
	
	public void addWaypoint(Waypoint point) {
		route.add(point);
	}
	
	public Iterator<Waypoint> points() {
		return route.iterator();
	}
	
	public Waypoint first() {
		return route.getFirst();
	}
	
	public Waypoint last() {
		return route.getLast();
	}

	public float getDistance() {
		Iterator<Waypoint> waypointIterator = route.iterator();
		Waypoint start = waypointIterator.next();
		float distance = 0;
		while(waypointIterator.hasNext()) {
			Waypoint next = waypointIterator.next();
			int xDot = start.getX()-next.getX();
			int yDot = start.getY()-next.getY();
			distance += Math.sqrt(xDot*xDot + yDot*yDot);
			start = next;
		}
		return distance;
	}
	
	public Vector2 getMiddle() {
		Vector2 pos = new Vector2(0,0);
		
		for(Waypoint point : route) {
			pos = pos.add(point.getVector());
		}
		
		return pos.divide(route.size());
	}
	
	public float getRequiredZoomLevel() {
		Vector2 middle = getMiddle();
		
		int maxXDiff = 0;
		int maxYDiff = 0;
		
		for(Waypoint w : route) {
			Vector2 len = w.getVector().sub(middle);
			if(Math.abs(len.X) > maxXDiff) {
				maxXDiff = Math.abs(len.X);
			}
			if(Math.abs(len.Y) > maxYDiff) {
				maxYDiff = Math.abs(len.Y);
			}
		}
		
		float reqXScale = (float)(maxXDiff/2)/(float)(FlightWorld.WIDTH/2);
		float reqYScale = (float)(maxYDiff/2)/(float)(FlightWorld.HEIGHT/2);
		
		return reqXScale > reqYScale ? reqXScale : reqYScale;
	}
}
