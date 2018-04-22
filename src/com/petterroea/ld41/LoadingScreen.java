package com.petterroea.ld41;

import java.awt.Color;
import java.awt.Graphics;

import com.petterroea.ld41.LoadingThread.LoadingState;

public class LoadingScreen extends Screen{
	
	GamePuppetMaster gameInfo;
	
	public LoadingScreen(GamePuppetMaster info) {
		this.gameInfo = info;
		
	}

	@Override
	public void render(Graphics g, int deltaMs) {
		// TODO Auto-generated method stub
		
		if(LoadingThread.state == LoadingState.NONE) {
			g.setColor(new Color(255, 84, 255));
			g.fillRect(0, 0, gameInfo.getGameWidth(), gameInfo.getGameHeight());
			g.setColor(Color.white);
			g.drawString("Loading your procedural world....", gameInfo.getGameWidth()/2, gameInfo.getGameHeight()/2);
		} else {
			gameInfo.transitionScreen(new MainMenuScreen(gameInfo));
		}
		
	}

	@Override
	public void doLogic(int deltaMs) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void load() {
		
	}

	@Override
	public boolean canTransition() {
		return true;
	}

}
