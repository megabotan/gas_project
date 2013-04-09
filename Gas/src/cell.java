import java.util.ArrayList;

import javax.media.opengl.GL2;

public class cell {
	Vector3D position;
	Vector3D diag;
	public boolean active;
	double particlesCount=0;
	public ArrayList<Triangle> Triangles = new ArrayList<Triangle>();
	public cell(Vector3D position,Vector3D diag)
	{
		this.position=position;
		this.diag=diag;
		active=false;
		
	}
	public void Draw(GL2 gl)
	{
		if(active){
		gl.glPolygonMode (GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
	gl.glBegin(GL2.GL_QUADS);	//bot
    	gl.glColor3f(0,1,1);
    	gl.glVertex3d(0+position.X,0+position.Y,0+position.Z);      
        gl.glVertex3d(diag.X+position.X,0+position.Y,0+position.Z);       
        gl.glVertex3d(diag.X+position.X,0+position.Y,diag.Z+position.Z);      
        gl.glVertex3d(0+position.X,0+position.Y,diag.Z+position.Z);     
    gl.glEnd();
    gl.glDisable(GL2.GL_TEXTURE_2D);
    
    gl.glBegin(GL2.GL_QUADS);	//top
		//gl.glColor3f(0,1,1);
		gl.glVertex3d(0+position.X,diag.Y+position.Y,0+position.Z);      
		gl.glVertex3d(diag.X+position.X,diag.Y+position.Y,0+position.Z);       
		gl.glVertex3d(diag.X+position.X,diag.Y+position.Y,diag.Z+position.Z);      
		gl.glVertex3d(0+position.X,diag.Y+position.Y,diag.Z+position.Z);     
    gl.glEnd();
    gl.glBegin(GL2.GL_LINES);
    	gl.glVertex3d(0+position.X,0+position.Y,0+position.Z);      
    	gl.glVertex3d(0+position.X,diag.Y+position.Y,0+position.Z);  
    
      	gl.glVertex3d(diag.X+position.X,0+position.Y,0+position.Z);      
    	gl.glVertex3d(diag.X+position.X,diag.Y+position.Y,0+position.Z);  
  
      	gl.glVertex3d(diag.X+position.X,0+position.Y,diag.Z+position.Z);      
    	gl.glVertex3d(diag.X+position.X,diag.Y+position.Y,diag.Z+position.Z);  
    
      	gl.glVertex3d(0+position.X,0+position.Y,diag.Z+position.Z);      
    	gl.glVertex3d(0+position.X,diag.Y+position.Y,diag.Z+position.Z);  
    gl.glEnd();
	}
	}
	public void addTriangle(Triangle triangle) {
		Triangles.add(triangle);	
	}
	
	
}
