package com.petterroea.ld41.scene;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.gui.Button;
import com.petterroea.ld41.gui.Segment;

public class MultipleChoiceSegment implements Segment {
	
	private ScenePlayScreen playScreen;
	private String title;
	private LinkedList<Button> buttons = new LinkedList<Button>();
	private GamePuppetMaster master;

	public MultipleChoiceSegment(GamePuppetMaster master, ScenePlayScreen scenePlayScreen, String title, JSONArray jsonArray) {
		this.playScreen = scenePlayScreen;
		this.title = title;
		this.master = master;
		//Parse buttons
		int buttonCount = 0;
		for(Object o : jsonArray) {
			JSONObject jobj = (JSONObject)o;
			JSONArray script = (JSONArray)jobj.get("segments");
			Segment[] segments = scenePlayScreen.parseSegments(script);
			
			Button b = new Button((String)jobj.get("text"),
					20 + (buttonCount/2)*70, 
					master.getGameHeight()-80+(buttonCount%2)*40, new CallbackHandler() {
				@Override
				public void handleCallback() {
					master.putSegmentsInFront(segments);
				}
			});
			
			buttons.add(b);
					
			buttonCount++;
		}
	}

	@Override
	public void render(Graphics g, int beginOffset, GamePuppetMaster master) {
		g.setColor(Color.white);
		g.setFont(g.getFont().deriveFont(15f));
		g.drawString(title, master.getGameWidth()/2-g.getFontMetrics().stringWidth(title)/2, beginOffset + 30);
		for(Button b : buttons) {
			b.render(g, master);
		}
	}

	@Override
	public void begin() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasEnded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void forceDone() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAutomationSegment() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseReleased() {
		for(Button b : buttons) {
			if(b.checkRelease(master.getGameMousePosition())) {
				System.out.println("Collided!");
				return;
			}
		}
	}

}
