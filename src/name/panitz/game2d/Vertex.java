package name.panitz.game2d;

import net.casqan.scifigame.extensions.VertexInt;

public class Vertex {
	public double x;
	public double y;
	public static final Vertex zero = new Vertex(0,0);
	public static final Vertex one = new Vertex(1,1);
	public static final Vertex up = new Vertex(0,-1);
	public static final Vertex down = new Vertex(0,1);
	public static final Vertex left = new Vertex(-1,0);
	public static final Vertex right = new Vertex(1,0);
	public static final Vertex half = new Vertex(0.5,0.5);

	public Vertex(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vertex(Vertex vertex) {
		this.x = vertex.x;
		this.y = vertex.y;
	}
	public void add(Vertex that) {
		x += that.x;
		y += that.y;
	}

	public static Vertex add(Vertex v1, Vertex v2){
		return new Vertex(v1.x + v2.x, v1.y + v2.y);
	}
	public static Vertex add(Vertex v1, VertexInt v2){
		return new Vertex(v1.x + v2.x, v1.y + v2.y);
	}
	public static Vertex sub(Vertex v1, Vertex v2){
		return new Vertex(v1.x - v2.x, v1.y - v2.y);
	}

	public void moveTo(Vertex that) {
		x = that.x;
		y = that.y;
	}

	public Vertex mult(double d) {
		return new Vertex(d * x, d * y);
	}

	@Override
	public String toString() {
		return "Vertex{" +
				"x=" + x +
				", y=" + y +
				'}';
	}

	//This is a basic lerp function
	//lerp is short for linear interpolation
	//It is used to smoothly move from one point to another
	public static Vertex Lerp(Vertex a, Vertex b, double t){
		return new Vertex(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t);
	}
	public double magnitude(){
		return Math.sqrt(x*x + y*y);
	}
	public Vertex normalized(){
		if (this.magnitude() == 0) return new Vertex(0,0);
		return new Vertex(this).mult(1d / this.magnitude());
	}
	public void Normalize(){
		var n = this.normalized();
		x = n.x;
		y = n.y;
	}
}
