package com.petterroea.ld41;

import com.petterroea.ld41.gui.Screen;
import com.petterroea.ld41.gui.Segment;

public interface GamePuppetMaster {
	public int getGameWidth();
	public int getGameHeight();
	public void transitionScreen(Screen newScreen);
	public void playSegment(Segment[] segments);
	public void putSegmentsInFront(Segment[] segments);
	public FlightWorld getFlightWorld();
	public Vector2 getGameMousePosition();
	public boolean isMouseDown();
	public void showAcquaintanceDialog(String actorName, String string);
}
