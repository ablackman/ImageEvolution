public class Circle implements Comparable {
	public int x;
	public int y;
	public double z;
	public int radius;
	public int r;
	public int g;
	public int b;
	public int a;
	
	public Circle(int x, int y, double z, int radius, int r, int g, int b, int a) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	@Override
	public int compareTo(Object otherCircle) {
		if(z > ((Circle)otherCircle).z) {
			return 1;
		} else if(z < ((Circle)otherCircle).z) {
			return -1;
		} else {
			return 0;
		}
	}
}
