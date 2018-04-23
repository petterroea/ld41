package com.petterroea.ld41;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONObject;

import com.petterroea.ld41.assets.AssetManager;
import com.petterroea.ld41.romance.AcquaintanceManager;

public class Actor {
	private BufferedImage portraitImage;
	private BufferedImage topdownSprite;
	private String name;
	private String personality;
	private String internalName;
	
	private JSONObject scenes;
	
	public Actor(JSONObject data) {
		String portraitName = (String) data.get("portrait");
		portraitImage = AssetManager.getImage(portraitName);
		
		String topdownName = (String) data.get("topdownSprite");
		topdownSprite = AssetManager.getImage(topdownName);
		
		name = (String) data.get("name");
		internalName = (String) data.get("internalName");
		personality = (String) data.get("personality");
		
		scenes = (JSONObject)data.get("scenes");
		
	}
	
	public BufferedImage getPortrait() {
		return portraitImage;
	}
	
	public BufferedImage getTopdownSprite() {
		return topdownSprite;
	}
	
	public String getName() {
		return name;
	}
	
	public String getInternalName() {
		return internalName;
	}

	public String getScene() {
		ArrayList<String> validScenes = new ArrayList<String>();
		
		Set<String> keys = (Set<String>)scenes.keySet();
		for(String key : keys) {
			if(!key.equals("default")) {
				boolean isOkSet = true;
				JSONObject obj = (JSONObject)scenes.get(key);
				
				if(obj.containsKey("minLevel")) {
					if(AcquaintanceManager.SINGLETON.getAcquaintanceValue(this.internalName) < (int)(long)(obj.get("minLevel")))
						isOkSet = false;
				}
				if(obj.containsKey("maxLevel")) {
					if(AcquaintanceManager.SINGLETON.getAcquaintanceValue(this.internalName) >= (int)(long)(obj.get("maxLevel")))
						isOkSet = false;
				}
				if(obj.containsKey("isKnown")) {
					boolean val = (boolean)obj.get("isKnown");
					if(AcquaintanceManager.SINGLETON.hasAcquaintance(internalName) != val)
						isOkSet = false;
				}
				if(obj.containsKey("knowsOther")) {
					String otherName = (String)obj.get("knowsOther");
					if(!AcquaintanceManager.SINGLETON.hasAcquaintance(otherName))
						isOkSet = false;
				}
				if(obj.containsKey("notKnowsOther")) {
					String otherName = (String)obj.get("notKnowsOther");
					if(AcquaintanceManager.SINGLETON.hasAcquaintance(otherName))
						isOkSet = false;
				}
				if(obj.containsKey("maxLevelWith")) {
					JSONObject nemesisData = (JSONObject)obj.get("maxLevelWith");
					String actorName = (String)nemesisData.get("name");
					int level = (int)(long)nemesisData.get("value");
					if(AcquaintanceManager.SINGLETON.getAcquaintanceValue(actorName) >= level)
						isOkSet = false;
				}
				if(obj.containsKey("minLevelWith")) {
					JSONObject nemesisData = (JSONObject)obj.get("minLevelWith");
					String actorName = (String)nemesisData.get("name");
					int level = (int)(long)nemesisData.get("value");
					if(AcquaintanceManager.SINGLETON.getAcquaintanceValue(actorName) < level)
						isOkSet = false;
				}
				
				if(isOkSet) {
					validScenes.add((String)obj.get("name"));
				}
			}
		}
		
		if(validScenes.size() == 0) {
			JSONObject defaultScene = (JSONObject)scenes.get("default");
			return (String)defaultScene.get("name");
		}
		Random rand = new Random();
		return validScenes.get(rand.nextInt(validScenes.size()));
	}
}
 