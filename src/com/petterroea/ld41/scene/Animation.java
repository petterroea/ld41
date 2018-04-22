package com.petterroea.ld41.scene;

public class Animation {
	long begin;
	long duration;
	float start;
	float end;
	public Animation(float start, float end, float duration) {
		begin = System.currentTimeMillis();
		this.duration = (long) (duration*1000f);
		this.start = start;
		this.end = end;
	}
	
	public float getInterpolatedValue() {
		if(hasEnded())
			return end;
		
		long timePassed = System.currentTimeMillis() - begin;
		float progress = (float)timePassed/(float)duration;
		return start + (end-start)*progress;
	}
	
	public boolean hasEnded() {
		return System.currentTimeMillis() > begin + duration;
	}
}
