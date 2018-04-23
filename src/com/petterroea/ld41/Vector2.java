package com.petterroea.ld41;

public class Vector2 {
	public int X, Y;
	public Vector2(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	public Vector2 add(Vector2 vector) {
		return new Vector2(this.X+vector.X, this.Y+vector.Y);
	}
	public Vector2 sub(Vector2 vector) {
		return new Vector2(this.X-vector.X, this.Y-vector.Y);
	}
	public Vector2 divide(int scalar) {
		return new Vector2(this.X/scalar, this.Y/scalar);
	}
	public float dot() {
		return this.X*this.X+this.Y*this.Y;
	}
	public float length() {
		return (float) Math.sqrt(dot());
	}
	public FloatVector2 toFloatVector() {
		return new FloatVector2(X, Y);
	}
}
