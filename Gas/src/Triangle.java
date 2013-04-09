import javax.media.opengl.GL2;


public class Triangle {
	public Vector3D first,second,third;
	
	
	public Triangle(Vector3D first,Vector3D second,Vector3D third)
	{
		this.first=first;
		this.second=second;
		this.third=third;
	}
	public Vector3D intersectLine(Vector3D point1,Vector3D point2)
	{
		Vector3D p1=new Vector3D(point1);
		Vector3D p2=new Vector3D(point2);
		Vector3D pa=new Vector3D(first);
		Vector3D pb=new Vector3D(second);
		Vector3D pc=new Vector3D(third);
		
		Vector3D n=new Vector3D(pa.subtract(pc).crossProduct(pb.subtract(pc)));
		n.normalize();
		double alpha=(n.multiply(pc.subtract(p2)))/(n.multiply(p1.subtract(p2)));
		if(alpha<0 || alpha>1)
		{
			return null;
		}
		Vector3D p=new Vector3D(p2.add(p1.subtract(p2).multiply(alpha)));
		double angle=0;
		angle+= pa.add(p.multiply(-1)).angle(pb.add(p.multiply(-1)));
		angle+= pa.add(p.multiply(-1)).angle(pc.add(p.multiply(-1)));
		angle+= pb.add(p.multiply(-1)).angle(pc.add(p.multiply(-1)));
		if(Math.abs(angle-Math.PI*2)>0.1)
		{
			return null;
		}
		
		return p;
			
		
		
	}
	public void Draw(GL2 gl) {
		
		gl.glBegin(GL2.GL_TRIANGLES);	//bot
	    	gl.glColor3f(0,0,1);
	    	gl.glVertex3d(first.X,first.Y,first.Z);      
	        gl.glVertex3d(second.X,second.Y,second.Z);       
	        gl.glVertex3d(third.X,third.Y,third.Z);     
        gl.glEnd();
		
		
		
	}
}
