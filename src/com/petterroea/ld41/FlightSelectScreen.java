package com.petterroea.ld41;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.petterroea.ld41.LoadingThread.LoadingState;
import com.petterroea.ld41.assets.AssetManager;

public class FlightSelectScreen extends Screen {
	
	private GamePuppetMaster master;
	private FlightWorld world;
	private BufferedImage flightSelectGui;
	
	public FlightSelectScreen(GamePuppetMaster master) {
		this.master = master;
		this.world = master.getFlightWorld();
		this.flightSelectGui = AssetManager.getImage("airport_select_gui");
	}

	double accumulatedS = 0.0;
	@Override
	public void render(Graphics g, int deltaMs) {
		accumulatedS += (double)deltaMs/1000.0;
		g.setColor(Color.red);
		g.fillRect(0, 0, master.getGameWidth(), master.getGameHeight());
		world.render(g, (float)(Math.sin(accumulatedS))*0.5f+0.5f, (float)Math.cos(accumulatedS)*0.5f+0.5f, (float)Math.sin(accumulatedS*0.3)*0.3f+0.5f, flightSelectGui.getWidth()-10, 0, master.getGameWidth(), master.getGameHeight());
		
		boolean result = g.drawImage(flightSelectGui, 0, 0, null);
	}

	@Override
	public void doLogic(int deltaMs) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canTransition() {
		return LoadingThread.state == LoadingState.WORLD;
	}

}
