package com.petterroea.ld41;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.petterroea.ld41.LoadingThread.LoadingState;
import com.petterroea.ld41.assets.AssetManager;
import com.petterroea.ld41.gui.LoadingScreen;
import com.petterroea.ld41.gui.Screen;
import com.petterroea.ld41.gui.Segment;
import com.petterroea.ld41.gui.TextSegment;
import com.petterroea.ld41.romance.AcquaintanceManager;
import com.petterroea.ld41.scene.CallbackSegment;
import com.petterroea.ld41.scene.CallbackHandler;

public class Boot extends Applet implements Runnable, GamePuppetMaster, KeyListener, MouseListener, MouseMotionListener{
	
	public Boot() {
		
	}
	public static void main(String[] args) {
		System.out.println("Kyun Kyun racing club");
		
		JFrame frame = new JFrame("LD41");
		frame.setSize(1280, 760);
		frame.setResizable(false);
		
		Boot boot = new Boot();
		frame.add(boot);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
		
		if(DEBUG)
			frame.setLocation(1920+100, 100);
		
		boot.start();
	}
	
	public static boolean DEBUG = false;
	
	private boolean shouldRun = true;
	private Thread gameThread = null;
	private BufferedImage backBuffer = null;
	private static int internalScaleFactor = 2;
	private Screen currentScreen = new LoadingScreen(this);
	private Screen nextScreen = null;
	private LinkedList<Integer> renderTimes = new LinkedList<Integer>();
	private LinkedList<Integer> logicTimes = new LinkedList<Integer>();
	private LinkedList<Segment> segments = new LinkedList<Segment>();
	
	private LoadingThread generation;
	private FlightWorld flightWorld;
	private boolean shouldTryTransition = true;
	private Vector2 lastMousePos = new Vector2(0, 0);
	private static boolean mouseButtonState = false;
	
	//Acquaintance dialog stuff
	private String acquaintanceDialogMsg = null;
	private Actor acquaintanceDialogActor = null;
	private long acquaintanceDialogLifetime = 0;
	
	public static final Color KYUNKYUNCOLOR = new Color(255, 84, 255);
	
	@Override
	public void start() {
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.requestFocus();
		gameThread = new Thread(this);
		
		//Start loading immediately
		flightWorld = new FlightWorld(this);
		generation = new LoadingThread(flightWorld);
		generation.generate();
		
		AcquaintanceManager.SINGLETON = new AcquaintanceManager(this);
		
		//Start the game
		gameThread.start();
		//playSegment(new TextSegment[] {new TextSegment("Boeing-chan", "Kyun! Welcome to the game!\nThis game is a dating simulator for planes\nkill me")});
	}
	
	@Override
	public void stop() {
		shouldRun = false;
	}

	@Override
	public void run() {
		long lastFpsUpdate = System.currentTimeMillis();
		long lastUpdate = System.currentTimeMillis();
		int frames = 0;
		int lastFps = 0;
		
		
		while(shouldRun) {
			if(nextScreen != null && shouldTryTransition) {
				if(!nextScreen.canTransition()) {
					segments.clear();
					shouldTryTransition = false;
					playSegment(new Segment[] {new TextSegment("System-chan", "Woah, beat me to it!\nPlease wait for the game to properly load\n\nJust a sec! Promise!"), 
							new CallbackSegment(new CallbackHandler() {
								@Override
								public void handleCallback() {
									shouldTryTransition = true;
								}
							})});
				} else {
					currentScreen = nextScreen;
					nextScreen = null;
					currentScreen.onTransition();
				}
			}
			if(backBuffer == null || this.getWidth()/internalScaleFactor != backBuffer.getWidth()|| this.getHeight()/internalScaleFactor != backBuffer.getHeight() ) {
				backBuffer = new BufferedImage(this.getWidth()/internalScaleFactor, this.getHeight()/internalScaleFactor, BufferedImage.TYPE_INT_RGB);
				System.out.println("Created new backbuffer of size " + backBuffer.getWidth() + ", " + backBuffer.getHeight());
			}
			
			Graphics g = backBuffer.getGraphics();
			int deltaMs = (int)(System.currentTimeMillis()-lastUpdate);
			lastUpdate = System.currentTimeMillis();
			
			//Draw stuff
			long preLogic = System.nanoTime();
			currentScreen.doLogic(deltaMs);
			long postLogic = System.nanoTime();
			currentScreen.render(g, deltaMs);
			
			//Draw text segment
			if(segments.size() != 0) {
				Segment segment = segments.getFirst();
				while(segment != null && segment.isAutomationSegment()) {
					segment.begin();
					segments.removeFirst();
					if(segments.size() != 0) {
						segment = segments.getFirst();
					} else {
						segment = null;
					}
					
				}
				if(segment != null) {
					if(LoadingThread.state != LoadingState.NONE) {
						BufferedImage hud = AssetManager.getImage("hud_text");
						g.drawImage(hud, 0, getGameHeight()-hud.getHeight(), null);
						if(segment.hasEnded() && (System.currentTimeMillis()/600)%2==0) {
							BufferedImage arrow = AssetManager.getImage("arrow");
							g.drawImage(arrow, getGameWidth() - 10 - arrow.getWidth(), getGameHeight()-90, null);
						}
					}
					
					if(!segment.hasStarted()) {
						segment.begin();
					}
					segment.render(g, getGameHeight()-110, this);
				}
			}
			long postRender = System.nanoTime();
			
			//Draw acquaintance dialog
			if(acquaintanceDialogActor != null) {
				long drawDuration = System.currentTimeMillis() - acquaintanceDialogLifetime;
				BufferedImage hud = AssetManager.getImage("hud_text");
				g.setColor(Color.WHITE);
				g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
				if(drawDuration < 1000L) {
					int offsetPos = (int)(drawDuration)/10;
					g.drawImage(hud, 0, -hud.getHeight()-50+offsetPos, null);
					g.drawImage(acquaintanceDialogActor.getPortrait(), 10, offsetPos-140, 96, 96,  null);
					g.drawString(acquaintanceDialogMsg, 130, offsetPos-70);
					
				} else if(drawDuration > 5000L) {
					int offsetPos = (int)(1000L-(drawDuration-5000L))/10;
					g.drawImage(hud, 0, -hud.getHeight()-50+offsetPos, null);
					g.drawImage(acquaintanceDialogActor.getPortrait(), 10, offsetPos-140, 96, 96, null);
					g.drawString(acquaintanceDialogMsg, 130, offsetPos-70);
					if(drawDuration > 6000L) {
						acquaintanceDialogActor = null;
					}
				} else {
					int offsetPos = (int)(1000)/10;
					g.drawImage(hud, 0, -hud.getHeight()-50+offsetPos, null);
					g.drawImage(acquaintanceDialogActor.getPortrait(), 10, offsetPos-140, 96, 96, null);
					g.drawString(acquaintanceDialogMsg, 130, offsetPos-70);
				}
			}
			
			//End
			
			if(DEBUG) {
				//Push analytics
				renderTimes.add((int)(postRender-postLogic));
				logicTimes.add((int)(postLogic-preLogic));
				if(logicTimes.size()>60) {
					logicTimes.removeFirst();
				}
				if(renderTimes.size()>60) {
					renderTimes.removeFirst();
				}
				
				//Draw analytics
				Iterator<Integer> render = renderTimes.iterator();
				Iterator<Integer> logic = logicTimes.iterator();
				
				int analyticsIndex = 0;
				while(render.hasNext() && logic.hasNext()) {
					int nowRender = render.next();
					int nowLogic = logic.next();
					float renderMillis = (float)nowRender/1000f/1000f;
					float logicMillis = (float)nowLogic/1000f/1000f;
					if(nowRender > nowLogic) {
						g.setColor(Color.red);
						g.drawLine(analyticsIndex, backBuffer.getHeight(), analyticsIndex, backBuffer.getHeight()-(int)renderMillis);
						g.setColor(Color.green);
						g.drawLine(analyticsIndex, backBuffer.getHeight(), analyticsIndex, backBuffer.getHeight()-(int)logicMillis);
					} else {
						g.setColor(Color.green);
						g.drawLine(analyticsIndex, backBuffer.getHeight(), analyticsIndex, backBuffer.getHeight()-(int)logicMillis);
						g.setColor(Color.red);
						g.drawLine(analyticsIndex, backBuffer.getHeight(), analyticsIndex, backBuffer.getHeight()-(int)renderMillis);
					}
					g.setColor(Color.black);
					g.drawLine(0, backBuffer.getHeight()-60, 60, backBuffer.getHeight()-60);
					analyticsIndex++;
				}
			}
			
			
			//Draw loading bar
			
			if(LoadingThread.LoadingProgress < 100) {
				g.setColor(Color.gray);
				g.fillRect(0, getGameHeight()-5, getGameWidth(), 5);
				g.setColor(new Color(255, 30, 255));
				float posInWindow = ((float)LoadingThread.LoadingProgress/100f)*(float)getGameWidth();
				g.fillRect(0, getGameHeight()-5, (int)posInWindow, 5);
			}
			
			
			this.getGraphics().drawImage(backBuffer, 0, 0, this.getWidth(), this.getHeight(), null);
			
			if(System.currentTimeMillis()-lastFpsUpdate > 1000L) {
				System.out.println(frames);
				lastFps = frames;
				frames = 1;
				lastFpsUpdate = System.currentTimeMillis();
			} else {
				frames++;
			}
		}
	}

	@Override
	public int getGameWidth() {
		// TODO Auto-generated method stub
		return backBuffer.getWidth();
	}

	@Override
	public int getGameHeight() {
		// TODO Auto-generated method stub
		return backBuffer.getHeight();
	}

	@Override
	public void transitionScreen(Screen newScreen) {
		nextScreen = newScreen;
	}

	@Override
	public void playSegment(Segment[] segments) {
		/*if(this.segments.size() != 0) {
			JOptionPane.showMessageDialog(null, "Failure: text segment forced when one is already playing");
			throw new RuntimeException("text segment already showing");
		}*/
		this.segments.clear();
		for(Segment segment : segments) {
			this.segments.add(segment);
		}
	}
	@Override
	public void keyTyped(KeyEvent e) { }
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(segments.size() != 0) {
			Segment segment = segments.getFirst();
			if(segment.hasEnded()) {
				segments.removeFirst();
			} else {
				segment.forceDone();
			}
		} else {
			currentScreen.keyPressed(e);
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if(segments.size() != 0)
			return;
		currentScreen.keyReleased(e);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(segments.size() != 0)
			return;
		currentScreen.mouseDragged(e);
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		lastMousePos.X = e.getX()/internalScaleFactor;
		lastMousePos.Y = e.getY()/internalScaleFactor;
		currentScreen.mouseMoved(e);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(segments.size() != 0)
			return;
		currentScreen.mouseClicked(e);
	}
	@Override
	public void mousePressed(MouseEvent e) {
		mouseButtonState = true;
		if(segments.size() != 0) {
			Segment segment = segments.getFirst();
			
			if(segment.hasEnded()) {
				segments.removeFirst();
			} else {
				segment.forceDone();
			}
		} else {
			currentScreen.mousePressed(e);
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButtonState = false;
		if(segments.size() != 0) {
			segments.getFirst().mouseReleased();
			return;
		}
		currentScreen.mouseReleased(e);
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		currentScreen.mouseEntered(e);
	}
	@Override
	public void mouseExited(MouseEvent e) {
		currentScreen.mouseExited(e);
	}
	@Override
	public FlightWorld getFlightWorld() {
		// TODO Auto-generated method stub
		return flightWorld;
	}
	@Override
	public Vector2 getGameMousePosition() {
		// TODO Auto-generated method stub
		return lastMousePos;
	}
	@Override
	public boolean isMouseDown() {
		// TODO Auto-generated method stub
		return mouseButtonState;
	}
	@Override
	public void showAcquaintanceDialog(String actorName, String string) {
		acquaintanceDialogActor = AssetManager.getActor(actorName.toLowerCase());
		acquaintanceDialogMsg = string;
		acquaintanceDialogLifetime = System.currentTimeMillis();
		System.out.println("Got acquaintance for actor " + actorName);
	}
	@Override
	public void putSegmentsInFront(Segment[] segments) {
		this.segments.removeFirst();
		for(int i = segments.length-1; i >= 0; i--) {
			this.segments.addFirst(segments[i]);
		}
	}
}
