package com.petterroea.ld41.scene;

import java.awt.Graphics;

import com.petterroea.ld41.GamePuppetMaster;
import com.petterroea.ld41.gui.Segment;

public class CallbackSegment implements Segment{
	
	private CallbackHandler handler;
	private boolean isCallbackDone = false;
	
	public CallbackSegment(CallbackHandler handler) {
		this.handler = handler;
	}

	@Override
	public void render(Graphics g, int beginOffset, GamePuppetMaster master) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		if(isCallbackDone)
			throw new RuntimeException("Callback called which is allready done");
		handler.handleCallback();
		isCallbackDone = true;
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
		return true;
	}

	@Override
	public void mouseReleased() {
		
	}

}
