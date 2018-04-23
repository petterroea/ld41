package com.petterroea.ld41.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.management.modelmbean.RequiredModelMBean;

import com.petterroea.ld41.Actor;
import com.petterroea.ld41.Boot;
import com.petterroea.ld41.FlightWorld;
import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.LoadingThread;
import com.petterroea.ld41.LoadingThread.LoadingState;
import com.petterroea.ld41.Route;
import com.petterroea.ld41.Vector2;
import com.petterroea.ld41.assets.AssetManager;
import com.petterroea.ld41.romance.AcquaintanceManager;
import com.petterroea.ld41.scene.CallbackSegment;
import com.petterroea.ld41.scene.Animation;
import com.petterroea.ld41.scene.CallbackHandler;

public class FlightSelectScreen extends Screen {
	
	private GamePuppetMaster master;
	private FlightWorld world;
	private BufferedImage flightSelectGui;
	private static boolean isTutorialShown = false;
	private LinkedList<Route> routes;
	private Random myRandom = new Random();
	private int selectedFlightIndex = -1;
	private int lastSelectedFlightIndex = -1;
	
	//Animation of camera movement
	private float currentWorldX = 0.5f;
	private float currentWorldY = 0.5f;
	private float currentZoom = 1f;
	
	private Animation xAnim = null;
	private Animation yAnim = null;
	private Animation zoomAnim = null;
	
	private Button goButton = null;
	
	private HashMap<Actor, Route> actorRoutes = new HashMap<Actor, Route>();
	
	public FlightSelectScreen(GamePuppetMaster master) {
		this.master = master;
		this.world = master.getFlightWorld();
		
		this.flightSelectGui = AssetManager.getImage("airport_select_gui");
		
		goButton = new Button("Go!", 5, master.getGameHeight()-50, new CallbackHandler() {
			@Override
			public void handleCallback() {
				if(selectedFlightIndex != -1) {
					master.transitionScreen(new FlightScreen(routes.get(selectedFlightIndex), master, actorRoutes));
				}
			}
		});
	}
	
	public void onTransition() {
		routes = new LinkedList<Route>();
		for(int i = 0; i < 4+myRandom.nextInt(4); i++) {
			routes.add(world.generateRoute(world.getCurrentPosition()));
		}
		//Generate routes for acquaintances
		String[] actors = AssetManager.getActorNames();
		for(String actorName : actors) {
			Actor actor = AssetManager.getActor(actorName);
			Route r = world.generateRoute();
			actorRoutes.put(actor,  r);
		}
		AcquaintanceManager.SINGLETON.commitRelations();
	}


	double accumulatedS = 0.0;
	@Override
	public void render(Graphics g, int deltaMs) {
		accumulatedS += (double)deltaMs/1000.0;
		g.setColor(Color.black);
		g.fillRect(0, 0, master.getGameWidth(), master.getGameHeight());
		if(selectedFlightIndex != lastSelectedFlightIndex) {
			Route selectedRoute = routes.get(selectedFlightIndex);
			
			Vector2 middleVec = selectedRoute.getMiddle();
			float zoom = selectedRoute.getRequiredZoomLevel();
			lastSelectedFlightIndex = selectedFlightIndex;
			
			xAnim = new Animation(currentWorldX, (float)middleVec.X/(float)FlightWorld.WIDTH, 0.5f);
			yAnim = new Animation(currentWorldY, (float)middleVec.Y/(float)FlightWorld.HEIGHT, 0.5f);
			zoomAnim = new Animation(currentZoom, zoom*2f*1.4f, 0.5f);
		}
		
		if(xAnim != null) {
			currentWorldX = xAnim.getInterpolatedValue();
			if(xAnim.hasEnded())
				xAnim = null;
		}
		if(yAnim != null) {
			currentWorldY = yAnim.getInterpolatedValue();
			if(yAnim.hasEnded())
				yAnim = null;
		}
		if(zoomAnim != null) {
			currentZoom = zoomAnim.getInterpolatedValue();
			if(zoomAnim.hasEnded())
				zoomAnim = null;
		}		
		
		world.render(g, currentWorldX, currentWorldY, currentZoom, flightSelectGui.getWidth()-10, 0, master.getGameWidth(), master.getGameHeight());
		if(selectedFlightIndex != -1) {
			world.renderRoute(g, Color.red, routes.get(selectedFlightIndex), currentWorldX, currentWorldY, currentZoom, flightSelectGui.getWidth()-10, 0, master.getGameWidth(), master.getGameHeight());
		}
		Iterator it = actorRoutes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        if(!AcquaintanceManager.SINGLETON.hasAcquaintance(((Actor)pair.getKey()).getInternalName())) {
	        	if(Boot.DEBUG) {
	        		world.renderRoute(g, Color.blue, (Route)pair.getValue(), currentWorldX, currentWorldY, currentZoom, flightSelectGui.getWidth()-10, 0, master.getGameWidth(), master.getGameHeight());
	        	}
	        } else {
	        	world.renderRoute(g, Boot.KYUNKYUNCOLOR, (Route)pair.getValue(), currentWorldX, currentWorldY, currentZoom, flightSelectGui.getWidth()-10, 0, master.getGameWidth(), master.getGameHeight());
	        }
	    }
		
		g.drawImage(flightSelectGui, 0, 0, null);
		
		g.setFont(g.getFont().deriveFont(20f));
		g.setColor(Color.white);
		g.drawString("Select flight plan", 5, 25);
		
		int indexYOffset = 25;
		int routeNum = 0;
		for(Route r : routes) {
			indexYOffset += drawRouteMarker(r, indexYOffset, g, routeNum++);
			
		}
		
		goButton.render(g, master);
	}

	private int drawRouteMarker(Route r, int indexYOffset, Graphics g, int routeNum) {
		String routeStr = r.first().getName() + " -> " + r.last().getName();
		float distance = r.getDistance();
		int value = 50+(int)(distance/100);
		
		Rectangle thisArea = new Rectangle(10, indexYOffset+5, 150, 20+15);
		if(routeNum == selectedFlightIndex) {
			g.setColor(new Color(255, Boot.KYUNKYUNCOLOR.getGreen()-30, 255));
			g.fillRect(0, indexYOffset+5, 190, 20+15);
		}
		else if(thisArea.contains(master.getGameMousePosition().X, master.getGameMousePosition().Y)) {
			if(master.isMouseDown()) {
				g.setColor(new Color(255, Boot.KYUNKYUNCOLOR.getGreen()-30, 255));
			} else {
				g.setColor(Color.PINK);
			}
			
			g.fillRect(0, indexYOffset+5, 190, 20+15);
		}
		
		
		g.setFont(g.getFont().deriveFont(12f));
		g.setColor(Color.white);
		g.drawString(routeStr, 5, 20 + indexYOffset);
		g.drawString(value + "$", 5, 20+indexYOffset+15);
		return 20+15;
	}

	@Override
	public void doLogic(int deltaMs) {
		if(!isTutorialShown) {
			isTutorialShown = true;
			master.playSegment(new Segment[] {new TextSegment("Anna", "You need somewhere to go! \nSelect a route from the list on the left"),
					new TextSegment("Meta-chan", "(The routes of people you know are highlighted in pink on the map)")});
		}
	}

	@Override
	public boolean canTransition() {
		return LoadingThread.state == LoadingState.WORLD;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(!goButton.checkRelease(master.getGameMousePosition())) {
			int routeIndex = 0;
			for(Route r : routes) {
				Rectangle rect = new Rectangle(0, 25+(20+15)*routeIndex, 190, 20+15);	
				if(rect.contains(master.getGameMousePosition().X, master.getGameMousePosition().Y)) {
					selectedFlightIndex = routeIndex;
					return;
				}
				routeIndex++;
			}
		}
	}

}
