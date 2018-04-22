package com.petterroea.ld41.scene;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.petterroea.ld41.Actor;
import com.petterroea.ld41.FlightSelectScreen;
import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.Screen;
import com.petterroea.ld41.Segment;
import com.petterroea.ld41.TextSegment;
import com.petterroea.ld41.assets.AssetManager;

public class ScenePlayScreen extends Screen {
	private String currentSceneName;
	private GamePuppetMaster puppetMaster;
	
	//Scene related things
	BufferedImage backdrop;
	
	float dim = 0.0f;
	Animation dimAnimation = null;
	
	LinkedList<SceneObject> objects = new LinkedList<SceneObject>();
	LinkedList<Actor> actors = new LinkedList<Actor>();
	
	public ScenePlayScreen(String sceneName, GamePuppetMaster puppetMaster) {
		this.puppetMaster = puppetMaster;
		this.currentSceneName = sceneName;
		transitionScene(sceneName);
	}
	
	private void transitionScene(String sceneName) {
		JSONObject sceneObj = AssetManager.getScene(sceneName);
		backdrop = AssetManager.getImage((String)sceneObj.get("backdrop"));
		objects.clear();
		actors.clear();
		
		//Read initial settings
		JSONObject settings = (JSONObject) sceneObj.get("settings");
		if(settings.containsKey("dim"))
			dim = (float)((double) settings.get("dim"));
		System.out.println("Dim: " + dim);
		
		//Parse sequences
		
		LinkedList<Segment> segments = new LinkedList<Segment>();
		
		JSONArray segmentList = (JSONArray) sceneObj.get("segments");
		
		for(Object o: segmentList) {
			JSONObject obj = (JSONObject)o;
			if(obj.containsKey("name")) { //Normal dialouge
				segments.add(new TextSegment((String)obj.get("name"), (String)obj.get("text")));
			}
			else if(obj.containsKey("dim")) {
				segments.add(new CallbackSegment(new SegmentCallbackHandler() {
					@Override
					public void handleCallback() {
						dimAnimation = new Animation(dim, (float)((double)obj.get("dim")), (float)((double)obj.get("duration")) );
					}
				}));
			} else if(obj.containsKey("transition")) {
				String target = (String) obj.get("transition");
				switch(target.toLowerCase()) {
					case "flightselect":
						segments.add(new CallbackSegment(new SegmentCallbackHandler() {
							@Override
							public void handleCallback() {
								puppetMaster.transitionScreen(new FlightSelectScreen(puppetMaster));
							}
						}));
						break;
					default:
						throw new RuntimeException("request for transition to unknown screen");
				}
			}
		}
		
		//Parse objects
		JSONArray objectList = (JSONArray) sceneObj.get("objects");
		for(Object o : objectList) {
			JSONObject jobj = (JSONObject)o;
			objects.add(new SceneObject(AssetManager.getImage((String)jobj.get("file")), (String)jobj.get("dock")));
		}
		
		//Fetch actors
		
		JSONArray actorList = (JSONArray) sceneObj.get("actors");
		for(Object o : actorList) {
			String s = (String)o;
			actors.add(AssetManager.getActor(s));
			System.out.println("Added actor " + s);
		}
		
		Segment[] segmentArray = new Segment[segments.size()];
		segmentArray = segments.toArray(segmentArray);
		puppetMaster.playSegment(segmentArray);
	}

	@Override
	public void render(Graphics g, int deltaMs) {
		g.drawImage(backdrop, 0, 0, null);
		//Handle animations
		if(dimAnimation != null) {
			if(dimAnimation.hasEnded()) {
				dimAnimation = null;
			} else {
				dim = dimAnimation.getInterpolatedValue();
			}
		}
		for(SceneObject o : objects) {
			o.render(g, puppetMaster);
		}
		int actorStartOffset = puppetMaster.getGameWidth()-((actors.size()-1)*50)-200;
		int actorIndex = 0;
		for(Actor a : actors) {
			g.drawImage(a.getPortrait(), actorStartOffset + actorIndex++*50, puppetMaster.getGameHeight()-300, null);
			//System.out.println("drawing at " + (actorStartOffset + actorIndex++*50) + ", " + (puppetMaster.getGameHeight()-200));
		}
		if(dim > 0.001f) {
			g.setColor(new Color(0, 0, 0, (int)(dim*2.55)));
			g.fillRect(0, 0, puppetMaster.getGameWidth(), puppetMaster.getGameHeight());
		}
	}

	@Override
	public void doLogic(int deltaMs) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canTransition() {
		return true;
	}

}
