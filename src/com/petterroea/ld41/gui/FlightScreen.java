package com.petterroea.ld41.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.petterroea.ld41.Actor;
import com.petterroea.ld41.Boot;
import com.petterroea.ld41.FlightWorld;
import com.petterroea.ld41.FloatVector2;
import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.Route;
import com.petterroea.ld41.Vector2;
import com.petterroea.ld41.Waypoint;
import com.petterroea.ld41.assets.AssetManager;

public class FlightScreen extends Screen {

	private GamePuppetMaster master;
	private Route route;
	private BufferedImage myPlane;
	private FlightWorld world;
	private long lastDialouge = 0;
	
	private final float ZOOMLEVEL = 0.3f;
	
	//Navigation info
	private Waypoint nextWaypoint;
	private Iterator<Waypoint> waypointIterator;
	private LinkedList<ActorEntity> actors = new LinkedList<ActorEntity>();

	//Rotation and position of plane
	private float planeRotationRadians = 0;
	private FloatVector2 planePosition;
	
	public FlightScreen(Route route, GamePuppetMaster master, HashMap<Actor, Route> actorRoutes) {
		this.master = master;
		this.route = route;
		world = master.getFlightWorld();
		myPlane = AssetManager.getImage("jet_above");
		
		Iterator it = actorRoutes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        actors.push(new ActorEntity((Actor) pair.getKey(), master, (Route)pair.getValue()));
	    }
		
		planePosition = world.getCurrentPosition().getVector().toFloatVector();
		waypointIterator = route.points();
		waypointIterator.next();
		nextWaypoint = waypointIterator.next();
	}

	float accumS = 0.0f;
	@Override
	public void render(Graphics g, int deltaMs) {
		accumS += (float)deltaMs/1000f;
		g.setColor(Color.black);
		g.fillRect(0, 0, master.getGameWidth(), master.getGameHeight());
		
		Vector2 position = interpolatePosition();
		world.render(g, (float)position.X/(float)FlightWorld.WIDTH, (float)position.Y/(float)FlightWorld.HEIGHT, ZOOMLEVEL, 0, 0, master.getGameWidth(), master.getGameHeight());
		
		//Draw actors
		for(ActorEntity e : actors) {
			e.render(g, world, (float)position.X/(float)FlightWorld.WIDTH, (float)position.Y/(float)FlightWorld.HEIGHT, ZOOMLEVEL);
		}
		
		//Draw rotated plane or something
		Graphics2D g2 = (Graphics2D)g;
		
		AffineTransform trans = new AffineTransform();
		trans.setTransform(new AffineTransform());
		//trans.translate(100f+Math.sin(accumS)*200f, 1f);
		trans.translate(master.getGameWidth()/2, master.getGameHeight()/2);
		trans.rotate( -planeRotationRadians +(float)Math.PI/2f);
		trans.translate(-myPlane.getWidth()/2, -myPlane.getHeight()/2);
		
		//trans.translate(master.getGameWidth()/2, master.getGameHeight()/2);
		
		g2.drawImage(myPlane, trans , null);
	}
	
	private Vector2 interpolatePosition() {
		return planePosition.toIntVector();
		//return world.getCurrentPosition().getVector();
	}

	@Override
	public void doLogic(int deltaMs) {
		for(ActorEntity ent : actors) {
			//System.out.println("Doing actora");
			ent.doLogic(deltaMs);
		}
		
		float deltaS = (float)deltaMs/1000f;
		
		//Angle to next waypoint
		FloatVector2 fv = nextWaypoint.getVector().toFloatVector();
		float nextPointAngle = (float) Math.atan2(-1f*(fv.Y-planePosition.Y), 1f*(fv.X-planePosition.X));
		//System.out.println(nextPointAngle);
		
		if(nextPointAngle < planeRotationRadians) {
			planeRotationRadians -= Math.PI*0.5f*deltaS;
		} else {
			planeRotationRadians += Math.PI*0.5f*deltaS;
		}
		
		planePosition = planePosition.add(new FloatVector2((float)Math.cos(planeRotationRadians), -1f*(float)Math.sin(planeRotationRadians)).times(deltaS*80f));
		
		float lenToDest = planePosition.minus(fv).length() ;
		//System.out.println(lenToDest);
		if(lenToDest < 20f) {
			if(!waypointIterator.hasNext()) {
				master.transitionScreen(new FlightEndScreen(master));
			}
		}
	}

	@Override
	public boolean canTransition() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if(System.currentTimeMillis() - lastDialouge < 3000L)
			return;
		
		Vector2 position = interpolatePosition();

		for(ActorEntity a : actors) {
			if(a.attemptClick(this, master, (float)position.X/(float)FlightWorld.WIDTH, (float)position.Y/(float)FlightWorld.HEIGHT, ZOOMLEVEL))
				return;
		}
	}

	public void setTimeout() {
		lastDialouge = System.currentTimeMillis();
	}

}
