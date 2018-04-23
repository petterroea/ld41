package com.petterroea.ld41.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.LoadingThread;
import com.petterroea.ld41.LoadingThread.LoadingState;
import com.petterroea.ld41.assets.AssetManager;
import com.petterroea.ld41.scene.ScenePlayScreen;

public class MainMenuScreen extends Screen{
	
	GamePuppetMaster gameInfo;
	
	public MainMenuScreen(GamePuppetMaster info) {
		this.gameInfo = info;
	}

	long deltaTime = 0;
	@Override
	public void render(Graphics g, int deltaMs) {
		deltaTime += deltaMs;
		
		g.setColor(new Color(255, 84, 255));
		g.fillRect(0, 0, gameInfo.getGameWidth(), gameInfo.getGameHeight());
		
		//Draw the girls
		String[] actors = AssetManager.getActorNames();
		int actorIndex = 0;
		for(String actor : actors) {
			BufferedImage girlImg = AssetManager.getActor(actor).getPortrait();
			g.drawImage(girlImg, 
					gameInfo.getGameWidth()/2 + (actorIndex%2==0 ? -1 : 1)*((actorIndex/2)*25), 
					(actorIndex/2)*50+100, 
					actorIndex%2==0 ? -girlImg.getWidth() : girlImg.getWidth(), 
							girlImg.getHeight(), null);
			actorIndex++;
		}
		
		BufferedImage logo = AssetManager.getImage("logo");
		g.drawImage(logo, gameInfo.getGameWidth()/2-logo.getWidth()/2, gameInfo.getGameHeight()/3-logo.getHeight()/2, logo.getWidth(), logo.getHeight(), null);
		
		g.setColor(Color.white);
		g.drawString("Press any button to start...", 
				gameInfo.getGameWidth()/2-(g.getFontMetrics().stringWidth("Press any button to start...")/2), 
				gameInfo.getGameHeight()-20);
	}

	@Override
	public void doLogic(int deltaMs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canTransition() {
		// TODO Auto-generated method stub
		return LoadingThread.state != LoadingState.NONE;
	}
	
	public void keyPressed(KeyEvent e) {
		gameInfo.transitionScreen(new ScenePlayScreen("defaultScene", gameInfo));
	}

}
