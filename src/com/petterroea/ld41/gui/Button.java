package com.petterroea.ld41.gui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.Vector2;
import com.petterroea.ld41.assets.AssetManager;
import com.petterroea.ld41.scene.CallbackHandler;
import com.sun.javafx.tk.FontMetrics;

public class Button {
	private String text;
	private int x, y, width, height;
	private BufferedImage normalImg;
	private BufferedImage pressedImg;
	private CallbackHandler callback;
	public Button(String str, int x, int y, CallbackHandler callback) {
		this.x = x;
		this.y = y;
		this.text = str;
		normalImg = AssetManager.getImage("button");
		pressedImg = AssetManager.getImage("buttonPressed");
		this.callback = callback;
		
		calculateSize();
	}
	private void calculateSize() {
		BufferedImage bimg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics g = bimg.getGraphics();
		g.setFont(g.getFont().deriveFont(13f));
		
		width = 20 + g.getFontMetrics().stringWidth(text) + 20;
		height = 15 + 20;
	}
	public boolean checkRelease(Vector2 position) {
		Rectangle rect = new Rectangle(x, y, width, height);
		if(rect.contains(position.X, position.Y)) {
			System.out.println("Button clicked");
			callback.handleCallback();
			return true;
		}
		return false;
	}
	public void render(Graphics g, GamePuppetMaster master) {
		BufferedImage buttonImg = normalImg;
		Rectangle rect = new Rectangle(x, y, width, height);
		if(rect.contains(master.getGameMousePosition().X, master.getGameMousePosition().Y)) {
			if(master.isMouseDown()) {
				buttonImg = pressedImg;
			}
		}
		
		//System.out.println(master.getGameMousePosition().X + ", " + master.getGameMousePosition().Y);
		g.setFont(g.getFont().deriveFont(13f));
		//Top left corner
		g.drawImage(buttonImg,
				//Destination
				x, y, x+5, y+5,
				//Source
				0, 0, 5, 5,
				null
				);
		//Top
		g.drawImage(buttonImg,
				//Destination
				x+5, y, x+width-5, y+5,
				//Source
				5, 0, 54, 5,
				null
				);
		//Top right corner
		g.drawImage(buttonImg,
				//Destination
				x+width-5, y, x+width, y+5,
				//Source
				64-5, 0, 64, 5,
				null
				);
		
		//Bottom left corner
		g.drawImage(buttonImg,
				//Destination
				x, y+height-5, x+5, y+height,
				//Source
				0, 64-5, 5, 64,
				null
				);
		//Bottom
		g.drawImage(buttonImg,
				//Destination
				x+5, y+height-5, x+width-5, y+height,
				//Source
				5, 59, 54, 64,
				null
				);
		//Bottom right corner
		g.drawImage(buttonImg,
				//Destination
				x+width-5, y+height-5, x+width, y+height,
				//Source
				64-5, 59, 64, 64,
				null
				);
		//Left
		g.drawImage(buttonImg,
				//Destination
				x, y+5, x+5, y+height-5,
				//Source
				0, 5, 5, 54,
				null
				);
		//Right
		g.drawImage(buttonImg,
				//Destination
				x+width-5, y+5, x+width, y+height-5,
				//Source
				59, 5, 64, 54,
				null
				);
		//Fill
		g.drawImage(buttonImg, 
				x+5, y+5, x+width-5, y+height-5,
				5, 5, 59, 59,
				null);
		
		g.drawString(text, x+10, y+10+10);
	}
}
