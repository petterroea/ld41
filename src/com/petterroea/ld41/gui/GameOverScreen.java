package com.petterroea.ld41.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.petterroea.ld41.Actor;
import com.petterroea.ld41.Boot;
import com.petterroea.ld41.GamePuppetMaster;

public class GameOverScreen extends Screen {
	GamePuppetMaster master;
	Actor actor;
	
	public GameOverScreen(GamePuppetMaster master, Actor actor) {
		this.master = master;
		this.actor = actor;
	}

	@Override
	public void render(Graphics g, int deltaMs) {
		g.setColor(Boot.KYUNKYUNCOLOR);
		g.fillRect(0, 0, master.getGameWidth(), master.getGameHeight());
		
		g.setColor(Color.white);
		g.setFont(g.getFont().deriveFont(20f));
		g.drawString("Congratulations! You won!", master.getGameWidth()/2-g.getFontMetrics().stringWidth("Congratulations! You won!"), 40);
		
		g.setFont(g.getFont().deriveFont(16f));
		g.drawString("You successfully won the heart of " + actor.getName(), master.getGameWidth()/2-g.getFontMetrics().stringWidth("You successfully won the heart of " + actor.getName()), 70);
		
		BufferedImage actorPic = actor.getPortrait();
		
		g.drawImage(actorPic, master.getGameWidth()/2-actorPic.getWidth(), master.getGameHeight()/2-actorPic.getHeight(), actorPic.getWidth()*2, actorPic.getHeight()*2, null);
	}

	@Override
	public void doLogic(int deltaMs) {
		
	}

	@Override
	public boolean canTransition() {
		return true;
	}

}
