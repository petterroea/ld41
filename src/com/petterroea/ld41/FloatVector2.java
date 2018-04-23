package com.petterroea.ld41;

public class FloatVector2 {
	public float X, Y;
	public FloatVector2(float x, float y) {
		this.X = x;
		this.Y = y;
	}
	public FloatVector2 add(FloatVector2 vector) {
		return new FloatVector2(this.X+vector.X, this.Y+vector.Y);
	}
	public FloatVector2 sub(FloatVector2 vector) {
		return new FloatVector2(this.X-vector.X, this.Y-vector.Y);
	}
	public FloatVector2 divide(int scalar) {
		return new FloatVector2(this.X/scalar, this.Y/scalar);
	}
	public float dot() {
		return this.X*this.X+this.Y*this.Y;
	}
	public float length() {
		return (float) Math.sqrt(dot());
	}
	public FloatVector2 times(float scalar) {
		return new FloatVector2(X*scalar, Y*scalar);
	}
	public Vector2 toIntVector() {
		return new Vector2((int)X, (int)Y);
	}
	public FloatVector2 normalize() {
		float len = length();
		return new FloatVector2(X/len, Y/len);
	}
	public FloatVector2 minus(FloatVector2 fv) {
		return new FloatVector2(X-fv.X, Y-fv.Y);
	}
}
