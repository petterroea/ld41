package com.petterroea.ld41.scene;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.petterroea.ld41.GamePuppetMaster;

public class SceneObject {
	private BufferedImage image;
	private String dockStr;
	public SceneObject(BufferedImage image, String dockStr) {
		this.image = image;
		this.dockStr = dockStr;
	}
	
	public void render(Graphics g, GamePuppetMaster master) {
		int xPos = 0;
		int yPos = 0;
		switch(dockStr.toLowerCase()) {
			case "bottomleft":
				xPos = 0;
				yPos = master.getGameHeight()-image.getHeight();
				break;
			default:
				throw new RuntimeException("Unknown dock direction " + dockStr.toLowerCase());
		}
		g.drawImage(image, xPos, yPos, null);
	}
}
