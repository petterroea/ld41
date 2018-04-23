package com.petterroea.ld41.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import com.petterroea.ld41.Actor;
import com.petterroea.ld41.Boot;
import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.assets.AssetManager;
import com.petterroea.ld41.romance.AcquaintanceManager;
import com.petterroea.ld41.scene.ScenePlayScreen;

public class FlightEndScreen extends Screen {
	
	private GamePuppetMaster master;

	public FlightEndScreen(GamePuppetMaster master) {
		this.master = master;
	}

	@Override
	public void render(Graphics g, int deltaMs) {
		g.setColor(Boot.KYUNKYUNCOLOR);
		g.fillRect(0, 0, master.getGameWidth(), master.getGameHeight());
		
		g.setColor(Color.white);
		g.setFont(g.getFont().deriveFont(25f));
		g.drawString("Flight finished!", master.getGameWidth()/2-g.getFontMetrics().stringWidth("Flight finished!")/2, 40);
		
		g.setFont(g.getFont().deriveFont(12f));
		Iterator<String> acquaintances = AcquaintanceManager.SINGLETON.getAcquaintances();
		
		int index = 0;
		while(acquaintances.hasNext()) {
			int xOffset = (index/5)*200;
			int yOffset = (index%5)*40;
			String acquaintance = acquaintances.next();
			Actor a = AssetManager.getActor(acquaintance);
			g.drawImage(a.getPortrait(), 100 + xOffset, 50 + yOffset, 64, 64, null);
			g.drawString(a.getName(), 100 + xOffset + 70, 50 + yOffset + 32);
			
			int oldVal = AcquaintanceManager.SINGLETON.getAcquaintanceValue(acquaintance);
			int newVal = AcquaintanceManager.SINGLETON.getNewAcquaintancePoints(acquaintance);
			g.drawString(AcquaintanceManager.SINGLETON.getAcquaintanceValue(acquaintance) + "", 100 + xOffset + 70, 50 + yOffset + 32 + 15);
			g.drawString((newVal >= 0 ? "+" + newVal : newVal) + "", 100 + xOffset + 70+50, 50 + yOffset + 32 + 15);
			
			//GameOver?
			if(oldVal + newVal >= 100) {
				master.transitionScreen(new GameOverScreen(master, a));
			}
			index++;
		}
		
		g.drawString("Press any button to continue", master.getGameWidth()/2-g.getFontMetrics().stringWidth("Press any key to continue")/2, master.getGameHeight()-50);
	}

	@Override
	public void doLogic(int deltaMs) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canTransition() {
		return true;
	}
	
	public void keyPressed(KeyEvent e) {
		master.transitionScreen(new FlightSelectScreen(master));
	}

}
