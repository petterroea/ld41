package com.petterroea.ld41.assets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//import org.json.*;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.petterroea.ld41.Actor;
import com.petterroea.util.FileUtils;

public class AssetManager {
	private static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	private static HashMap<String, JSONObject> scenes = new HashMap<String, JSONObject>();
	private static HashMap<String, Actor> actors = new HashMap<String, Actor>();
	private static HashMap<String, Clip> soundClips = new HashMap<String, Clip>();
	
	public AssetManager() { 
		
	}
	
	public static BufferedImage getImage(String name) {
		return images.get(name);
	}
	
	public static JSONObject getScene(String name) {
		return scenes.get(name);
	}
	
	public static Actor getActor(String name) {
		return actors.get(name);
	}
	
	public static String[] getActorNames() {
		String[] actorArray = new String[actors.size()];
		
		int index = 0;
		Iterator it = actors.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        actorArray[index++] = (String)pair.getKey();
	    }
	    
	    return actorArray;
	}
	
	public static void loadAssets() {
		long startTime = System.currentTimeMillis();
		loadImage("logo");
		loadImage("arrow");
		loadImage("hud_text");
		loadImage("airport_select_gui");
		loadImage("button");
		loadImage("buttonPressed");
		loadImage("jet_above");
		
		loadScene("defaultScene");
		
		loadActor("hana");
		loadActor("ichika");
		loadActor("hiyouki");
		System.out.println("Assets loaded in " + (System.currentTimeMillis()-startTime) + " ms");
	}
	
	private static void loadImage(String filename) {
		if(images.containsKey(filename)) {
			System.out.println("Skipping loading of file which is already loaded");
			return;
		}
		System.out.println("Loading " + filename);
		try {
			images.put(filename, ImageIO.read(AssetManager.class.getResourceAsStream(filename + ".png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void loadScene(String name) {
		System.out.println("Loading scene " + name);
		JSONParser parser = new JSONParser();
		
		try {
			String contents = FileUtils.readResource(AssetManager.class.getResourceAsStream("scenes/" + name + ".json"));
			JSONObject obj = (JSONObject) parser.parse(contents);
			
			scenes.put(name, obj);
			
			loadImage((String)obj.get("backdrop"));
			
			//Load all the objects
			JSONArray objectList = (JSONArray) obj.get("objects");
			for(Object o : objectList) {
				JSONObject jobj = (JSONObject)o;
				loadImage((String)jobj.get("file"));
			}
			
			//Load all the actors
			JSONArray actorList = (JSONArray) obj.get("actors");
			for(Object o : actorList) {
				String actor = (String)o;
				loadActor(actor);
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void loadActor(String string) {
		if(actors.containsKey(string)) {
			System.out.println("Skipping already loaded actor " + string);
			return;
		}
		System.out.println("Loading actor " + string);
		
		JSONParser parser = new JSONParser();
		
		try {
			String contents = FileUtils.readResource(AssetManager.class.getResourceAsStream("actors/" + string + ".json"));
			JSONObject obj = (JSONObject) parser.parse(contents);
			
			loadImage((String)obj.get("portrait"));
			loadImage((String)obj.get("topdownSprite"));
			
			actors.put(string, new Actor(obj));
			
			//Load scenes
			JSONObject scenes = (JSONObject)obj.get("scenes");
			Set<String> s = (Set<String>)scenes.keySet();
			for(String name : s) {
				JSONObject sceneObj = (JSONObject)scenes.get(name);
				String setName = (String)sceneObj.get("name");
				loadScene(setName);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
