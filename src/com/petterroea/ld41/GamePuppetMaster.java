package com.petterroea.ld41;

public interface GamePuppetMaster {
	public int getGameWidth();
	public int getGameHeight();
	public void transitionScreen(Screen newScreen);
	public void playSegment(Segment[] segments);
	public FlightWorld getFlightWorld();
}
