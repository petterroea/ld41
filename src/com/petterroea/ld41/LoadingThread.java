package com.petterroea.ld41;

import com.petterroea.ld41.assets.AssetManager;

public class LoadingThread implements Runnable{
	
	public enum LoadingState {
		NONE,
		ASSETS,
		WORLD
	}
	private FlightWorld world;
	public static LoadingState state = LoadingState.NONE;
	public static float LoadingProgress = 0;
	
	public LoadingThread(FlightWorld world) {
		this.world = world;
	}
	
	public void generate() {
		new Thread(this).start();
	}
	
	public LoadingState getLoadingState() {
		return state;
	}

	@Override
	public void run() {
		AssetManager.loadAssets();
		state = LoadingState.ASSETS;
		world.generate();
		state = LoadingState.WORLD;
	}
}
