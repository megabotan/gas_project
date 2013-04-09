
public class Camera {
	Vector3D pos,lookAt;
	double width, height;
	double sensivity = 0.005;
	public Camera(double X, double Y, double Z, double Xat, double Yat, double Zat) 
	{
		pos=new Vector3D(X,Y,Z);
		lookAt=new Vector3D(Xat,Yat,Zat);
	}
	
	public double getXPos() 
	{		
		return pos.X;
	}

	public double getYPos() 
	{		
		return pos.Y;
	}

	public double getZPos() 
	{		
		return pos.Z;
	}

	public double getXLPos() 
	{
		return lookAt.X;
	}

	public double getYLPos() 
	{		
		return lookAt.Y;
	}

	public double getZLPos() 
	{
		return lookAt.Z;
	}

	public void moveForward(double distance) 
	{		
		Vector3D delta=lookAt.subtract(pos);
		pos.X+=(distance/delta.length())*(delta.X);
		pos.Y+=(distance/delta.length())*(delta.Y);
		pos.Z+=(distance/delta.length())*(delta.Z);
		lookAt=delta.add(pos);		
	}

	public void strafeRight(double distance) 
	{
		Vector3D delta=lookAt.subtract(pos);
		pos.X+=-delta.Z*distance/delta.length();
		pos.Z+=delta.X*distance/delta.length();
		lookAt=delta.add(pos);		
	}
	
	public void normalizeLook()
	{
		Vector3D delta=lookAt.subtract(pos);
		delta.normalize();
		lookAt=delta.add(pos);	
	}
	
	public void mouseLook(double dx,double dy)
	{
		
		lookUp(dy*sensivity);
		lookLeft(dx*sensivity);
		/*dx*=sensivity;
		dy*=sensivity;
		normalizeLook();
		Vector3D delta=lookAt.subtract(pos);
		delta.Y=Math.sin(Math.asin(delta.Y)-dy);
		delta.X=Math.cos(Math.acos(delta.X)-dx);
		delta.Z=Math.cos(Math.acos(delta.Z)+dx);
		lookAt=delta.add(pos);*/
	}
	
	
	private void lookUp(double degree)
	{
		Vector3D delta=lookAt.subtract(pos);
		Vector3D deltaChange=delta.crossProduct(new Vector3D(-delta.Z,0,delta.X));
		deltaChange.normalize();
		deltaChange=deltaChange.multiply(degree);
		if((deltaChange.X+delta.X)*delta.X<0)
		{
			return;			
		}
		delta=delta.add(deltaChange);
		lookAt=delta.add(pos);
	}
	
	private void lookLeft(double degree)
	{
		Vector3D delta=lookAt.subtract(pos);
		Vector3D deltaChange=delta.crossProduct(new Vector3D(0,1,0));
		deltaChange.normalize();
		deltaChange=deltaChange.multiply(degree);
		delta=delta.add(deltaChange);
		lookAt=delta.add(pos);
	}
}
