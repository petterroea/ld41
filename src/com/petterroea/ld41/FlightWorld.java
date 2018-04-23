package com.petterroea.ld41;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

public class FlightWorld {
	public static final int WIDTH = 5000;
	public static final int HEIGHT = 5000;
	
	private BufferedImage image;
	private int[] heightValues = new int[WIDTH*HEIGHT];
	
	private ArrayList<City> cities = new ArrayList<City>();
	
	private float regionScale = 5;
	private GamePuppetMaster master;
	
	//Current whereabouts of the player
	private City position;
	
	public FlightWorld(GamePuppetMaster master) {
		this.master = master;
	}
	
	public Route generateRoute() {
		Route route = new Route();
		
		Random rand = new Random();
		
		City start = cities.get(rand.nextInt(cities.size()));
		route.addWaypoint(start);
		
		int nextIndex = rand.nextInt(cities.size()-1);
		if(cities.get(nextIndex) == start) {
			nextIndex++;
		}
		route.addWaypoint(cities.get(nextIndex));
		
		return route;
	}
	
	public Route generateRoute(Waypoint start) {
		Route route = new Route();
		
		Random rand = new Random();
		
		route.addWaypoint(start);
		
		int nextIndex = rand.nextInt(cities.size()-1);
		if(cities.get(nextIndex) == start) {
			nextIndex++;
		}
		route.addWaypoint(cities.get(nextIndex));
		
		return route;
	}
	
	public void render(Graphics g, float x, float y, float scale, int dx1, int dy1, int dx2, int dy2) {
		float ratio = (float)(dy2-dy1)/(float)(dx2-dx1);
		
		
		
		int sx1 = (int)((x*WIDTH)-(WIDTH/2)*scale);
		int sy1 = (int)((y*HEIGHT)-(HEIGHT/2)*scale*ratio);
		
		int sx2 = (int)((x*WIDTH)+(WIDTH/2)*scale);
		int sy2 = (int)((y*HEIGHT)+(HEIGHT/2)*scale*ratio);
		
		boolean didFinish = g.drawImage(image, 
								//Viewport
								dx1, dy1, dx2, dy2,
								//Source image
								sx1, sy1, sx2, sy2, 
								
								null);
		
		g.setColor(Color.white);
		Rectangle visibleRect = new Rectangle(sx1, sy1, sx2-sx1, sy2-sy1);
		
		float scaleX = (float)(dx2-dx1)/(float)(sx2-sx1);
		float scaleY = (float)(dy2-dy1)/(float)(sy2-sy1);
		
		for(City c : cities) {
			if(visibleRect.contains(c.getX(), c.getY())) {
				int drawX = (int)((c.getX()-sx1)*scaleX)+dx1;
				int drawY = (int)((c.getY()-sy1)*scaleY)+dy1;
				g.drawLine(drawX-2, drawY, drawX+2, drawY);
				g.drawLine(drawX, drawY-2, drawX, drawY+2);
				g.drawString(c.getName(), drawX, drawY);
			}
		}
	}
	
	public Iterator<City> cityIterator() {
		return cities.iterator();
	}
	
	public void generate() {
		System.out.println("Generating world");
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = image.getGraphics();
		
		OpenSimplexNoise noise = new OpenSimplexNoise(System.currentTimeMillis());
		OpenSimplexNoise noise2 = new OpenSimplexNoise(System.currentTimeMillis()+1000);
		
		//Static colors
		Color waterColor = Color.blue;
		Color darkWaterColor = new Color(waterColor.getRed(), waterColor.getGreen(), waterColor.getBlue()-30);
		Color groundColor = Color.green;
		Color darkGroundColor = new Color(groundColor.getRed(), groundColor.getGreen()-40, groundColor.getBlue());
		Color lightGroundColor = new Color(groundColor.getRed()+100, groundColor.getGreen(), groundColor.getBlue()+100);
		//Color lightGroundColor = Color.orange;
		
		for(int x = 0; x < WIDTH; x++) {
			float progress = ((float)x/(float)WIDTH)*80f;
			LoadingThread.LoadingProgress = progress;
			for(int y = 0; y < HEIGHT; y++) {
				double simplex = noise.eval(x/2000.0*regionScale, y/2000.0*regionScale)*2; //Continent
				simplex += noise2.eval(x/1500.0,  y/1500.0)*2;
				
				simplex += noise.eval(x/500.0*regionScale, y/500.0*regionScale);
				simplex += noise2.eval(x/300.0*regionScale, y/300.0*regionScale)*0.5;
				
				simplex = simplex / 4.0;
				
				heightValues[x+y*WIDTH] = (int)(simplex*10000.0);
				//System.out.println(simplex);
				if(simplex < 0.1) {
					if(simplex < 0.05) {
						image.setRGB(x, y, darkWaterColor.getRGB());
					} else {
						image.setRGB(x, y, waterColor.getRGB());
					}
				} else if(simplex > 0.6) {
					image.setRGB(x, y, Color.white.getRGB());
				
				} else {
					image.setRGB(x, y, groundColor.getRGB());
				}
			}
		}
		
		for(int x = 0; x < WIDTH; x++) {
			for(int y = 0; y < HEIGHT; y++) {
				int simplex = heightValues[x+y*WIDTH];
				//System.out.println(simplex);
				if(simplex < 1000) {
					if(simplex < 500) {
						image.setRGB(x, y, darkWaterColor.getRGB());
					} else {
						image.setRGB(x, y, waterColor.getRGB());
					}
				} else if(simplex > 6000) {
					image.setRGB(x, y, Color.white.getRGB());
				} else {
					Color baseColor = groundColor;
					if(simplex > 5000) {
						baseColor = Color.gray;
					}
					int xVec = 0;
					int yVec = 0;
					if(x < WIDTH-1) {
						xVec += heightValues[(x+1)+y*WIDTH];
					}
					if(x > 0) {
						xVec -= heightValues[(x-1)+y*WIDTH];
					}
					
					if(y < HEIGHT-1) {
						yVec += heightValues[x+(y+1)*WIDTH];
					}
					if(y > 0) {
						yVec -= heightValues[x+(y-1)*WIDTH];
					}
					
					//System.out.println(xVec + ", " + yVec);
					int vecAvg = (xVec+yVec)/2;
					if(vecAvg > 10) {
						vecAvg = 10;
					} else if(vecAvg < -10) {
						vecAvg = -10;
					}
					//System.out.println(vecAvg);
					if(vecAvg > 2) {
						vecAvg -= 2;
						image.setRGB(x, y, new Color(
								Math.max(0, Math.min(baseColor.getRed()-vecAvg*10, 255)), 
								Math.max(0, Math.min(baseColor.getGreen()-vecAvg*10, 255)), 
								Math.max(0, Math.min(baseColor.getBlue()-vecAvg*10, 255))).getRGB()
								);
					} else if(vecAvg < -2) {
						vecAvg += 2;
						image.setRGB(x, y, new Color(
								Math.max(0, Math.min(baseColor.getRed()+vecAvg*-10, 255)), 
								Math.max(0, Math.min(baseColor.getGreen()+vecAvg*-10, 255)), 
								Math.max(0, Math.min(baseColor.getBlue()+vecAvg*-10, 255))).getRGB()
								);
					} else {
						image.setRGB(x, y, baseColor.getRGB());
					}
				}
			}
		}
		
		addAirports();
		
		LoadingThread.LoadingProgress = 90f;
		/*
		for(City c : cities) {
			g.setColor(Color.WHITE);
			g.drawLine(c.getX()-1, c.getY(), c.getX()-1, c.getY());
			g.drawLine(c.getX(), c.getY()-1, c.getX(), c.getY()+1);
			g.setFont(g.getFont().deriveFont(10f));
			g.drawString(c.getName(), c.getX(), c.getY());
		}*/
		LoadingThread.LoadingProgress = 100f;
		/*
		try {
			System.out.println("Writing to file");
			ImageIO.write(image, "PNG", new File("generated.png"));
			System.out.println("Generated file written");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done!");*/
	}
	Random rand = new Random();
	private void addAirports() {
		for(int x = 0; x < WIDTH; x++) {
			for(int y = 0; y < HEIGHT; y++) {
				int simplex = heightValues[x+y*WIDTH];
				//System.out.println(simplex);
				if(simplex < 1000) {

				} else if(simplex > 6000) {

				} else {
					int xVec = 0;
					int yVec = 0;
					if(x < WIDTH-1) {
						xVec += heightValues[(x+1)+y*WIDTH];
					}
					if(x > 0) {
						xVec -= heightValues[(x-1)+y*WIDTH];
					}
					
					if(y < HEIGHT-1) {
						yVec += heightValues[x+(y+1)*WIDTH];
					}
					if(y > 0) {
						yVec -= heightValues[x+(y-1)*WIDTH];
					}
					
					//System.out.println(xVec + ", " + yVec);
					int vecAvg = (xVec+yVec)/2;
					if(vecAvg > 10) {
						vecAvg = 10;
					} else if(vecAvg < -10) {
						vecAvg = -10;
					}
					//System.out.println(vecAvg);
					if(vecAvg < 1 && vecAvg > -1 && simplex < 5000) {
						if(rand.nextInt(20000) < 10) {
							cities.add(new City(x, y));
						}
					}
				}
			}
		}
	}

	public Image getFullImage() {
		// TODO Auto-generated method stub
		return image;
	}

	public Waypoint getCurrentPosition() {
		if(position == null) {
			City largest = cities.get(0);
			for(City c : cities) {
				if(c.getPopulation() > largest.getPopulation())
					largest = c;
			}
			position = largest;
		}
		return position;
	}

	public void renderRoute(Graphics g, Color color, Route route, float x, float y, float scale, int dx1, int dy1, int dx2, int dy2) {
		float ratio = (float)(dy2-dy1)/(float)(dx2-dx1);
		
		int sx1 = (int)((x*WIDTH)-(WIDTH/2)*scale);
		int sy1 = (int)((y*HEIGHT)-(HEIGHT/2)*scale*ratio);
		
		int sx2 = (int)((x*WIDTH)+(WIDTH/2)*scale);
		int sy2 = (int)((y*HEIGHT)+(HEIGHT/2)*scale*ratio);
		
		
		g.setColor(color);
		
		float scaleX = (float)(dx2-dx1)/(float)(sx2-sx1);
		float scaleY = (float)(dy2-dy1)/(float)(sy2-sy1);
		
		Iterator<Waypoint> waypoints = route.points();
		
		Waypoint firstWaypoint = waypoints.next();
		while(waypoints.hasNext()) {
			Waypoint nextWaypoint = waypoints.next();
			
			int drawXPrev = (int)((firstWaypoint.getX()-sx1)*scaleX)+dx1;
			int drawYPrev = (int)((firstWaypoint.getY()-sy1)*scaleY)+dy1;
			
			int drawXNext = (int)((nextWaypoint.getX()-sx1)*scaleX)+dx1;
			int drawYNext = (int)((nextWaypoint.getY()-sy1)*scaleY)+dy1;
			
			g.drawLine(drawXPrev, drawYPrev, drawXNext, drawYNext);
			firstWaypoint = nextWaypoint;
		}
	}

	public Vector2 transformLocation(int toTransformX, int toTransformY, float x, float y, float scale, int dx1, int dy1, int dx2, int dy2) {
		float ratio = (float)(dy2-dy1)/(float)(dx2-dx1);
		
		int sx1 = (int)((x*WIDTH)-(WIDTH/2)*scale);
		int sy1 = (int)((y*HEIGHT)-(HEIGHT/2)*scale*ratio);
		
		int sx2 = (int)((x*WIDTH)+(WIDTH/2)*scale);
		int sy2 = (int)((y*HEIGHT)+(HEIGHT/2)*scale*ratio);
		
		
		float scaleX = (float)(dx2-dx1)/(float)(sx2-sx1);
		float scaleY = (float)(dy2-dy1)/(float)(sy2-sy1);
		
		int transformedX = (int)((toTransformX-sx1)*scaleX)+dx1;
		int transformedY = (int)((toTransformY-sy1)*scaleY)+dy1;
		
		return new Vector2(transformedX, transformedY);
	}
}
