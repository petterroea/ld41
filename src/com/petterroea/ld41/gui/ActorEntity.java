package com.petterroea.ld41.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

import com.petterroea.ld41.Actor;
import com.petterroea.ld41.FlightWorld;
import com.petterroea.ld41.FloatVector2;
import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.Route;
import com.petterroea.ld41.Vector2;
import com.petterroea.ld41.Waypoint;
import com.petterroea.ld41.scene.ScenePlayScreen;

public class ActorEntity {
	
	private GamePuppetMaster master;
	private Actor actor;
	
	//Navigation info
	private Waypoint nextWaypoint;
	private Iterator<Waypoint> waypointIterator;

	//Rotation and position of plane
	private float planeRotationRadians = 0;
	private FloatVector2 planePosition;
	private boolean stopped = false;
	
	private BufferedImage planeImage;
	
	public ActorEntity(Actor actor, GamePuppetMaster master, Route route) {
		this.master = master;
		this.actor = actor;
		planeImage = actor.getTopdownSprite();
		
		waypointIterator = route.points();
		planePosition = waypointIterator.next().getVector().toFloatVector();
		nextWaypoint = waypointIterator.next();
	}
	public void doLogic(int deltaMs) {
		if(stopped) {
			return;
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
		//System.out.println("Position: " + planePosition.X + ", " + planePosition.Y);
		
		float lenToDest = planePosition.minus(fv).length() ;
		//System.out.println(lenToDest);
		if(lenToDest < 20f) {
			if(!waypointIterator.hasNext()) {
				stopped = true;
			}
		}
	}
	
	public void render(Graphics g, FlightWorld world, float camX, float camY, float zoom) {
		Vector2 renderPos = world.transformLocation((int)planePosition.X, (int)planePosition.Y, camX, camY, zoom, 0, 0, master.getGameWidth(), master.getGameHeight());
		
		Rectangle rect = new Rectangle(renderPos.X-actor.getTopdownSprite().getWidth()/2, renderPos.Y-actor.getTopdownSprite().getHeight()/2, actor.getTopdownSprite().getWidth(), actor.getTopdownSprite().getHeight());
		
		//g.setColor(Color.red);
		//g.fillRect(rect.x, rect.y, rect.width, rect.height);
		
		Graphics2D g2 = (Graphics2D)g;
		
		AffineTransform trans = new AffineTransform();
		trans.setTransform(new AffineTransform());
		//trans.translate(100f+Math.sin(accumS)*200f, 1f);
		trans.translate(renderPos.X, renderPos.Y);
		trans.rotate( -planeRotationRadians +(float)Math.PI/2f);
		trans.translate(-planeImage.getWidth()/2, -planeImage.getHeight()/2);
		
		//trans.translate(master.getGameWidth()/2, master.getGameHeight()/2);
		
		g2.drawImage(planeImage, trans , null);
	}
	public boolean attemptClick(FlightScreen screen, GamePuppetMaster master, float camX, float camY, float zoom){
		Vector2 renderPos = master.getFlightWorld().transformLocation((int)planePosition.X, (int)planePosition.Y, camX, camY, zoom, 0, 0, master.getGameWidth(), master.getGameHeight());
		
		Rectangle rect = new Rectangle(renderPos.X-actor.getTopdownSprite().getWidth()/2, renderPos.Y-actor.getTopdownSprite().getHeight()/2, actor.getTopdownSprite().getWidth(), actor.getTopdownSprite().getHeight());
		
		if(rect.contains(master.getGameMousePosition().X, master.getGameMousePosition().Y)) {
			System.out.println("We hit!");
			screen.setTimeout();
			master.transitionScreen(new ScenePlayScreen(screen, master, actor.getScene()));
			return true;
		}
		
		return false;
	}
}
