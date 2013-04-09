import java.util.Random;


public class Vector3D {
	
	public double X,Y,Z;

	public Vector3D(double X,double Y,double Z)
	{
		this.X=X;
		this.Y=Y;
		this.Z=Z;
	}
	public Vector3D()
	{
		this.X=0;
		this.Y=0;
		this.Z=0;
	}
	public Vector3D(Vector3D vect) {
		this.X=vect.X;
		this.Y=vect.Y;
		this.Z=vect.Z;
	}
	public void normalize()
	{
		double length=Math.sqrt(X*X+Y*Y+Z*Z);
		X/=length;
		Y/=length;
		Z/=length;
	}
	public double length()
	{
		return Math.sqrt(X*X+Y*Y+Z*Z);
	}
	public void randomize(Random rand)
	{
		X=rand.nextDouble()-0.5;
		Y=rand.nextDouble()-0.5;
		Z=rand.nextDouble()-0.5;
	}
	public Vector3D add(Vector3D vect)
	{
		Vector3D result=new Vector3D(X+vect.X,Y+vect.Y,Z+vect.Z);
		return result;
	}
	public Vector3D subtract(Vector3D vect)
	{
		Vector3D result=new Vector3D(X-vect.X,Y-vect.Y,Z-vect.Z);
		return result;
	}
	public Vector3D crossProduct(Vector3D vect)
	{
		Vector3D result=new Vector3D(Y*vect.Z-Z*vect.Y,Z*vect.X-X*vect.Z,X*vect.Y-Y*vect.X);
		return result;
	}
	public Vector3D multiply(double a)
	{
		Vector3D result=new Vector3D(X*a,Y*a,Z*a);
		return result;
	}
	public double multiply(Vector3D a)
	{
		double result=X*a.X+Y*a.Y+Z*a.Z;
		return result;
	}
	public double angle(Vector3D a)
	{
		double result=X*a.X+Y*a.Y+Z*a.Z;
		result=result/(length()*a.length());
		result= Math.acos(result);
		return result;
	}
	
}
