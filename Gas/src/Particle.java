import java.util.Random;

import javax.media.opengl.GL2;

public class Particle {
	Vector3D position,velocity;
	
	
	public Particle(Random rand, Vector3D pos, Vector3D diag)
	{
		this.position=pos.add(new Vector3D(diag.X*rand.nextDouble(),diag.Y*rand.nextDouble(),diag.Z*(rand.nextDouble()*0.2)));
		velocity=new Vector3D();
		velocity.randomize(rand);
		velocity.Z=Math.abs(velocity.Z);
		velocity=velocity.multiply(0.002*diag.length());
	}
	public double Update(Vector3D worldPosition, Vector3D diag, cell[][][] cells,Random rand, GL2 gl)
	{
		position=position.add(velocity);
		double pressure=checkTextureIntersect(worldPosition,diag,cells,gl);
		checkWorldIntersect(worldPosition,diag,rand);
		return pressure;
	}
	
	
	
	private double checkTextureIntersect(Vector3D worldPosition, Vector3D diag, cell[][][] cells, GL2 gl) {
		// TODO Auto-generated method stub
		int cellX,cellY,cellZ;
		cellX=(int) ((position.X-worldPosition.X)/diag.X*cells.length);
		cellY=(int) ((position.Y-worldPosition.Y)/diag.Y*cells[0].length);
		cellZ=(int) ((position.Z-worldPosition.Z)/diag.Z*cells[0][0].length);
		cellX=Math.max(0, cellX);
		cellX=Math.min(cells.length-1, cellX);
		cellY=Math.max(0, cellY);
		cellY=Math.min(cells[0].length-1, cellY);
		cellZ=Math.max(0, cellZ);
		cellZ=Math.min(cells.length-1, cellZ);
		for(Triangle triangle : cells[cellX][cellY][cellZ].Triangles)
		{
			if(intersectTriangle(triangle)!=null)
			{
				
				Vector3D pa=new Vector3D(triangle.first);
				Vector3D pb=new Vector3D(triangle.second);
				Vector3D pc=new Vector3D(triangle.third);
				
				Vector3D n=new Vector3D(pa.subtract(pc).crossProduct(pb.subtract(pc)));
				
				
				n.normalize();
				
				return Math.abs(2*n.multiply(velocity)*n.Z);
				
			}
		}
		return 0;
		
		
		//cells[cellX][cellY][cellZ].Draw(gl);
	}
	
	private Vector3D intersectTriangle(Triangle triangle) {
		
		
		Vector3D p1=new Vector3D(position.subtract(velocity));
		Vector3D p2=new Vector3D(position);
		Vector3D pa=new Vector3D(triangle.first);
		Vector3D pb=new Vector3D(triangle.second);
		Vector3D pc=new Vector3D(triangle.third);
		
		Vector3D n=new Vector3D(pa.subtract(pc).crossProduct(pb.subtract(pc)));
		n.normalize();
		
		Vector3D p=triangle.intersectLine(p1, p2);
		if(p==null)
			{
				return null;
			}
		double dist=n.multiply(p.subtract(p2));
		Vector3D p3=new Vector3D(p2.add(n.multiply(2*dist)));
		position=p3;
		velocity=velocity.add(n.multiply(-2*n.multiply(velocity)));
		//velocity=velocity.multiply(-1);
		return p;
		
		
	}
	private void checkWorldIntersect(Vector3D worldPosition, Vector3D diag,Random rand) 
	{
		if(position.X<worldPosition.X || position.X>worldPosition.X+diag.X)
		{
			velocity.X=-velocity.X;
		}
		if(position.Y<worldPosition.Y || position.Y>worldPosition.Y+diag.Y)
		{
			velocity.Y=-velocity.Y;
		}
		if(position.Z<worldPosition.Z)// || position.Z>worldPosition.Z+diag.Z)
		{
			velocity.Z=-velocity.Z;
		}
		if( position.Z>worldPosition.Z+diag.Z)
		{
			Particle(rand,worldPosition,diag);
		}
	
		
	}
	private void Particle(Random rand, Vector3D worldPosition, Vector3D diag) {
		this.position=worldPosition.add(new Vector3D(diag.X*rand.nextDouble(),diag.Y*rand.nextDouble(),0));
		velocity=new Vector3D();
		velocity.randomize(rand);
		velocity.Z=Math.abs(velocity.Z);
		velocity=velocity.multiply(0.01);
	}
	public void Draw(GL2 gl)
	{
		/*gl.glBegin(GL2.GL_POINTS);
			gl.glColor3f(0,1,1);
	    	gl.glVertex3d(position.X,position.Y,position.Z);  
	      	 
    	gl.glEnd();*/
		gl.glBegin(GL2.GL_LINES);
			//if(velocity.Z>0)
				gl.glColor3d(0.2,0.2,0.2);
			//else
				//gl.glColor3d(0,1,0);
    		gl.glVertex3d(position.X,position.Y,position.Z);  
    		gl.glVertex3d(position.X+velocity.X*3,position.Y+velocity.Y*3,position.Z+velocity.Z*3);
    	gl.glEnd();
		
	}
}
