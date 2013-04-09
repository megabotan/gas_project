import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
 
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.*;
 
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;


 
 
public class GasMain implements GLEventListener, KeyListener,MouseListener, MouseMotionListener,MouseWheelListener {
 
    static GLU glu = new GLU();
 
    static GLCanvas canvas = new GLCanvas();
 
    static Frame frame = new Frame("Gas");
 
    static Animator animator = new Animator(canvas);
    
    Camera camera;
    
    Robot robot;
    
    boolean[] keys = new boolean[256];
 
    Particle particles[];
    
    int numberOfParticles=20000;
    
    Random rand;
    
    WavefrontObjectLoader_DisplayList list;
    
    double cameraSpeed=0.02;
    
    cell[][][] cells;
    
    int cellsX=40,cellsY=40,cellsZ=40;
    
    double dragCoeff=0;
    double ticks=0;
    
    public void display(GLAutoDrawable gLDrawable) {
        ticks++;
    	final GL2 gl = gLDrawable.getGL().getGL2();
        keyboardChecks();
        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
     
        gl.glLoadIdentity();
        glu.gluLookAt(camera.getXPos(),camera.getYPos() ,camera.getZPos(), 
                camera.getXLPos(), camera.getYLPos(),camera.getZLPos(),
                0.0, 1.0, 0.0);
        
        Vector3D worldPosition=list.getBorders()[0].multiply(2).add(list.getBorders()[1].multiply(-1));
    	Vector3D diag=list.getBorders()[1].add(list.getBorders()[0].multiply(-1)).multiply(3);
    	
        drawWalls(gl,worldPosition,diag);
        gl.glPolygonMode (GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        list.drawModel(gl);
        
        
        
        for(int i=0;i<numberOfParticles;i++)
        {
        	if(ticks>1000)
        	{
        		dragCoeff+=particles[i].Update(worldPosition,diag,cells,rand,gl);
        	}else
        	{
        		particles[i].Update(worldPosition,diag,cells,rand,gl);
        	}
        	particles[i].Draw(gl);
        	
        }
        
        print(worldPosition.add(diag).X,worldPosition.add(diag.multiply(1.01)).Y,worldPosition.add(diag).Z,Double.toString(dragCoeff/(ticks*numberOfParticles)*2000000),gl);
        
    }
    private void print(double x, double y,double z, String s,GL2 gl)
    {
	    //set the position of the text in the window using the x and y coordinates
	    gl.glRasterPos2d(x,y);
	    //get the length of the string to display
	    int len =s.length();
	    GLUT glut = new GLUT();
	    //loop to display character by character
	    for (int i = 0; i < len; i++) 
	    {
	    	glut.glutBitmapCharacter(5,s.charAt(i));
	    }
    };
    
    
    private Vector3D drawWalls(GL2 gl,Vector3D position,Vector3D diag) {
    	
    	
    	
    	
    	//gl.glPolygonMode (GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
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
        return diag;
	}

	private void keyboardChecks() {
    	
    	if(keys[KeyEvent.VK_W])
    	{    		
    		camera.moveForward(cameraSpeed);
    	}
    	if(keys[KeyEvent.VK_S])
    	{    		
    		camera.moveForward(-cameraSpeed);
    	}
    	if(keys[KeyEvent.VK_ESCAPE])
    	{
    		exit();  		
    	}
    	if(keys[KeyEvent.VK_D])
    	{
    		camera.strafeRight(cameraSpeed);		
    	}
    	if(keys[KeyEvent.VK_A])
    	{
    		camera.strafeRight(-cameraSpeed);		
    	}
    	
		
	}

	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
    }
 
    public void init(GLAutoDrawable gLDrawable) {
    	
        GL2 gl = gLDrawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        ((Component) gLDrawable).addKeyListener(this);
        GasMain.canvas.addMouseListener(this);
        GasMain.canvas.addMouseMotionListener(this);
        GasMain.canvas.addMouseWheelListener(this);
        Robot r=null ;        
        try {            
        	r=new Robot ();        
        }catch (final AWTException e){System .out .println ("Trouble stating Robot" );}        
        this .robot =r;        
        if (robot == null )System .out .println ("Error robot has not been initialized" );
        robot.mouseMove(100, 100);
        
        JFileChooser fd = new JFileChooser(".");
        FileNameExtensionFilter   filter = new FileNameExtensionFilter ("Obj files", "obj");
        fd.setFileFilter(filter);
        int returnVal = fd.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        String fileName=fd.getSelectedFile().getName();
        
        
        list=new WavefrontObjectLoader_DisplayList(fileName);
        }else
        {
        	exit();
        }
        Vector3D diag=list.getBorders()[1].add(list.getBorders()[0].multiply(-1)).multiply(3);
        Vector3D pos=list.getBorders()[0].multiply(2).add(list.getBorders()[1].multiply(-1));
        
        
        
        
        
        particles=new Particle[numberOfParticles];
        rand=new Random();
        for(int i=0;i<numberOfParticles;i++)
        {
        	particles[i]=new Particle(rand,pos,diag);
        }
        
        cells=new cell[cellsX][cellsY][cellsZ];
        for(int i=0;i<cellsX;i++)
        {
        	for(int j=0;j<cellsY;j++)
        	{
        		for(int k=0;k<cellsZ;k++)
        		{        			                                                                
        			cells[i][j][k]=new cell(new Vector3D(
        					pos.X+diag.X/cellsX*i,
        					pos.Y+diag.Y/cellsY*j,
        					pos.Z+diag.Z/cellsZ*k), 
        					new Vector3D(diag.X/cellsX,diag.Y/cellsY,diag.Z/cellsZ));
        		}
        	}
        	
        }
        
        for(int i=0;i<list.getIndices().size();i++)
        {
        	for(int k=0;k<list.getIndices().get(i).length-2;k++)
        	{
        		Vector3D min,max,first,second,third;
        		
        		first=new Vector3D(
        				list.getVertices().get(list.getIndices().get(i)[k]-1)[0],
        				list.getVertices().get(list.getIndices().get(i)[k]-1)[1],
        				list.getVertices().get(list.getIndices().get(i)[k]-1)[2]);
        		second=new Vector3D(
        				list.getVertices().get(list.getIndices().get(i)[k+1]-1)[0],
        				list.getVertices().get(list.getIndices().get(i)[k+1]-1)[1],
        				list.getVertices().get(list.getIndices().get(i)[k+1]-1)[2]);
        		third=new Vector3D(
        				list.getVertices().get(list.getIndices().get(i)[k+2]-1)[0],
        				list.getVertices().get(list.getIndices().get(i)[k+2]-1)[1],
        				list.getVertices().get(list.getIndices().get(i)[k+2]-1)[2]);
        		first=first.add(pos.multiply(-1));
        		second=second.add(pos.multiply(-1));
        		third=third.add(pos.multiply(-1));
        		min=new Vector3D(
        				Math.min(Math.min(first.X, second.X),third.X),
        				Math.min(Math.min(first.Y, second.Y),third.Y),
        				Math.min(Math.min(first.Z, second.Z),third.Z));
        		max=new Vector3D(
        				Math.max(Math.max(first.X, second.X),third.X),
        				Math.max(Math.max(first.Y, second.Y),third.Y),
        				Math.max(Math.max(first.Z, second.Z),third.Z));
        		min.X=min.X*cellsX/diag.X;
        		min.Y=min.Y*cellsY/diag.Y;
        		min.Z=min.Z*cellsZ/diag.Z;
        		
        		max.X=(int)Math.min(cellsX, max.X*cellsX/diag.X);
        		max.Y=(int)Math.min(cellsY, max.Y*cellsY/diag.Y);
        		max.Z=(int)Math.min(cellsZ, max.Z*cellsZ/diag.Z);
        		
        		min.X=(int)Math.max(0,min.X);
        		min.Y=(int)Math.max(0,min.Y);
        		min.Z=(int)Math.max(0,min.Z);
        		
        		for(int x=(int) min.X;x<=max.X;x++)
        		{
        			for(int y=(int) min.Y;y<=max.Y;y++)
            		{
        				for(int z=(int) min.Z;z<=max.Z;z++)
                		{
        					//System.out.println(x+" "+y+" "+z);
                			cells[x][y][z].addTriangle(new Triangle(first.add(pos),second.add(pos),third.add(pos)));
        					cells[x][y][z].active=true;
                			
                		}
            			
            		}
        			
        		}
        		
        		
        		
        	}
        	
        }
        
   
        
        
        
        
        
    }
 
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        GL2 gl = gLDrawable.getGL().getGL2();
        if (height <= 0) {
            height = 1;
        }
        float h = (float) width / (float) height;
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(90.0f, h, 0.0001, 10000.0);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        camera=new Camera(3,2,1.5,0,0,0);
        robot.mouseMove(100, 100);
    }
 
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //exit();
        }
        try
    	{
			keys[e.getKeyCode()] = true;
    	}
    	catch(Exception e1){};
    }
 
    public void keyReleased(KeyEvent e) {
    	try
    	{
			keys[e.getKeyCode()] = false;
    	}
    	catch(Exception e1){};
    }
 
    public void keyTyped(KeyEvent e) {
    }
 
    public static void exit() {
        animator.stop();
        frame.dispose();
        System.exit(0);
    }
 
    public static void main(String[] args) {
    	Toolkit t=Toolkit .getDefaultToolkit ();
        Image img =new BufferedImage (1, 1, BufferedImage .TYPE_INT_ARGB );       
        Cursor pointer =t.createCustomCursor (img , new Point (0,0), "none" );
        
        canvas.addGLEventListener(new GasMain());
        frame.add(canvas);
        frame.setSize(640, 480);
        frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        frame .setCursor (pointer );
        frame.setVisible(true);
        animator.start();
        canvas.requestFocus();
        canvas.requestFocusInWindow();
        
    }
 
    public void dispose(GLAutoDrawable gLDrawable) {
        // do nothing
    }

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {		
		double dx=arg0.getXOnScreen()-100;
        double dy=arg0.getYOnScreen()-100;
        robot.mouseMove(100, 100);
        camera.mouseLook(dx, dy);			
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		int notches = arg0.getWheelRotation();
		if(notches<0)
		{
			cameraSpeed/=2;
		}else if(notches>0)
		{
			cameraSpeed*=2;
			
		}
		System.out.println(notches);
	}
    
}