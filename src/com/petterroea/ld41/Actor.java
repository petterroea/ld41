package com.petterroea.ld41;

import java.awt.image.BufferedImage;

import org.json.simple.JSONObject;

import com.petterroea.ld41.assets.AssetManager;

public class Actor {
	private BufferedImage portraitImage;
	private String name;
	private String personality;
	
	public Actor(JSONObject data) {
		String portraitName = (String) data.get("portrait");
		portraitImage = AssetManager.getImage(portraitName);
		name = (String) data.get("name");
		personality = (String) data.get("personality");
		
	}
	
	public BufferedImage getPortrait() {
		return portraitImage;
	}
}
 