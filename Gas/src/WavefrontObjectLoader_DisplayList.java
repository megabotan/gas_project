
import java.io.*;
import java.nio.*;
import java.util.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import static javax.media.opengl.GL2.*;

public class WavefrontObjectLoader_DisplayList {

    private String OBJModelPath;                                    //the path to the model file
    private ArrayList<float[]> vData = new ArrayList<float[]>();    //list of vertex coordinates
    private ArrayList<float[]> vtData = new ArrayList<float[]>();   //list of texture coordinates
    private ArrayList<float[]> vnData = new ArrayList<float[]>();   //list of normal coordinates
    private ArrayList<int[]> fv = new ArrayList<int[]>();           //face vertex indices
    private ArrayList<int[]> ft = new ArrayList<int[]>();           //face texture indices
    private ArrayList<int[]> fn = new ArrayList<int[]>();           //face normal indices
    private FloatBuffer modeldata;                                  //buffer which will contain vertice data
    private int FaceFormat;                                         //format of the faces triangles or quads
    private int FaceMultiplier;                                     //number of possible coordinates per face
    private int PolyCount = 0;                                      //the model polygon count
    private boolean init  = true;
    public Vector3D min,max;

    public ArrayList<float[]> getVertices()
    {
    	return vData;
    } 
    public ArrayList<int[]> getIndices()
    {
    	return fv;
    }
    
    public WavefrontObjectLoader_DisplayList(String inModelPath) {
        System.out.println("LOADING WAVEFRONT OBJECT MODEL "+inModelPath);
        OBJModelPath = inModelPath;
        LoadOBJModel(OBJModelPath);
        SetFaceRenderType();
        System.out.println("POLYGON COUNT FOR MODEL="+PolyCount);
        System.out.println("VERTEX COUNT FOR MODEL="+vData.size());
        System.out.println("TEXTURE COORDINATE COUNT FOR MODEL="+vtData.size());
        System.out.println("NORMAL COUNT FOR MODEL="+vnData.size());
        
        if(vData.size()>0)
        {
        	min=new Vector3D(vData.get(0)[0],vData.get(0)[1],vData.get(0)[2]);
        	max=new Vector3D(vData.get(0)[0],vData.get(0)[1],vData.get(0)[2]);
        	for(int i=0;i<vData.size();i++)
        	{
        		if(min.X>vData.get(i)[0])
        		{        			
        			min.X=vData.get(i)[0];
        		}
        		if(min.Y>vData.get(i)[1])
        		{        			
        			min.Y=vData.get(i)[1];
        		}
        		if(min.Z>vData.get(i)[2])
        		{        			
        			min.Z=vData.get(i)[2];
        		}
        		
        		if(max.X<vData.get(i)[0])
        		{        			
        			max.X=vData.get(i)[0];
        		}
        		if(max.Y<vData.get(i)[1])
        		{        			
        			max.Y=vData.get(i)[1];
        		}
        		if(max.Z<vData.get(i)[2])
        		{        			
        			max.Z=vData.get(i)[2];
        		}
        	}
        }
    }
    public Vector3D[] getBorders()
    {
    	Vector3D[] result=new Vector3D[2];
    	result[0]=min;
    	result[1]=max;
    	return result;
    }

    private void LoadOBJModel(String ModelPath) {
        try {
            BufferedReader br = null;
            
            //br = new BufferedReader(new InputStreamReader((new Object()).getClass().getResourceAsStream(ModelPath)));
            br = new BufferedReader(new FileReader(ModelPath));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {         //read any descriptor data in the file
                    // Zzzz ...
                } else if (line.equals("")) {
                    // Ignore whitespace data
                } else if (line.startsWith("v ")) {  //read in vertex data
                    vData.add(ProcessData(line));
                } else if (line.startsWith("vt ")) { //read texture coordinates
                    vtData.add(ProcessData(line));
                } else if (line.startsWith("vn ")) { //read normal coordinates
                    vnData.add(ProcessData(line));
                } else if (line.startsWith("f ")) {  //read face data
                    ProcessfData(line);
                }
            }
            br.close();
            System.out.println("MODEL "+ModelPath+" SUCCESSFULLY LOADED!");
            
            
            
            
        } catch (IOException e) {
        	System.out.println(e);
        }
    }

    private float[] ProcessData(String read) {
        final String s[] = read.split("\\s+");
        return (ProcessFloatData(s)); //returns an array of processed float data
    }

    private float[] ProcessFloatData(String sdata[]) {
        float data[] = new float[sdata.length - 1];
        for (int loop = 0; loop < data.length; loop++) {
            data[loop] = Float.parseFloat(sdata[loop + 1]);
        }
        return data; //return an array of floats
    }

    private void ProcessfData(String fread) {
        PolyCount++;
        String s[] = fread.split("\\s+");
        if (fread.contains("//")) { //pattern is present if obj has only v and vn in face data
            for (int loop = 1; loop < s.length; loop++) {
                s[loop] = s[loop].replaceAll("//", "/0/"); //insert a zero for missing vt data
            }
        }
        ProcessfIntData(s); //pass in face data
    }

    private void ProcessfIntData(String sdata[]) {
        int vdata[] = new int[sdata.length - 1];
        int vtdata[] = new int[sdata.length - 1];
        int vndata[] = new int[sdata.length - 1];
        for (int loop = 1; loop < sdata.length; loop++) {
            String s = sdata[loop];
            String[] temp = s.split("/");
            vdata[loop - 1] = Integer.valueOf(temp[0]);         //always add vertex indices
            if (temp.length > 1) {                              //we have v and vt data
                vtdata[loop - 1] = Integer.valueOf(temp[1]);    //add in vt indices
            } else {
                vtdata[loop - 1] = 0;                           //if no vt data is present fill in zeros
            }
            if (temp.length > 2) {                              //we have v, vt, and vn data
                vndata[loop - 1] = Integer.valueOf(temp[2]);    //add in vn indices
            } else {
                vndata[loop - 1] = 0;                           //if no vn data is present fill in zeros
            }
        }
        fv.add(vdata);
        ft.add(vtdata);
        fn.add(vndata);
    }

    private void SetFaceRenderType() {
        final int temp[] = (int[]) fv.get(0);
        if (temp.length == 3) {
            FaceFormat = GL_TRIANGLES; 	//the faces come in sets of 3 so we have triangular faces
            FaceMultiplier = 3;
        } else if (temp.length == 4) {
            FaceFormat = GL_QUADS; 		//the faces come in sets of 4 so we have quadrilateral faces
            FaceMultiplier = 4;
        } else {
            FaceFormat = GL_POLYGON; 	//fall back to render as free form polygons
        }
    }

    private void ConstructInterleavedArray(GL2 inGL) {
        final int tv[] = (int[]) fv.get(0);
        final int tt[] = (int[]) ft.get(0);
        final int tn[] = (int[]) fn.get(0);
        //if a value of zero is found that it tells us we don't have that type of data
        if ((tv[0] != 0) && (tt[0] != 0) && (tn[0] != 0)) {
            ConstructTNV(); //we have vertex, 2D texture, and normal Data
            inGL.glInterleavedArrays(GL_T2F_N3F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] != 0) && (tn[0] == 0)) {
            ConstructTV(); //we have just vertex and 2D texture Data
            inGL.glInterleavedArrays(GL_T2F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] == 0) && (tn[0] != 0)) {
            ConstructNV(); //we have just vertex and normal Data
            inGL.glInterleavedArrays(GL_N3F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] == 0) && (tn[0] == 0)) {
            ConstructV();
            inGL.glInterleavedArrays(GL_V3F, 0, modeldata);
        }
    }

    private void ConstructTNV() {
        int[] v, t, n;
        float tcoords[] = new float[2]; //only T2F is supported in interLeavedArrays!!
        float coords[] = new float[3];
        int fbSize = PolyCount * (FaceMultiplier * 8); //3v per poly, 2vt per poly, 3vn per poly
        modeldata = GLBuffers.newDirectFloatBuffer(fbSize);
        modeldata.position(0);
        for (int oloop = 0; oloop < fv.size(); oloop++) {
            v = (int[]) (fv.get(oloop));
            t = (int[]) (ft.get(oloop));
            n = (int[]) (fn.get(oloop));
            for (int iloop = 0; iloop < v.length; iloop++) {
                //fill in the texture coordinate data
                for (int tloop = 0; tloop < tcoords.length; tloop++)
                    //only T2F is supported in interleavedarrays!!
                    tcoords[tloop] = ((float[]) vtData.get(t[iloop] - 1))[tloop];
                modeldata.put(tcoords);
                //fill in the normal coordinate data
                for (int vnloop = 0; vnloop < coords.length; vnloop++)
                    coords[vnloop] = ((float[]) vnData.get(n[iloop] - 1))[vnloop];
                modeldata.put(coords);
                //fill in the vertex coordinate data
                for (int vloop = 0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[]) vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }

    private void ConstructTV() {
        int[] v, t;
        float tcoords[] = new float[2]; //only T2F is supported in interLeavedArrays!!
        float coords[] = new float[3];
        int fbSize = PolyCount * (FaceMultiplier * 5); //3v per poly, 2vt per poly
        modeldata = GLBuffers.newDirectFloatBuffer(fbSize);
        modeldata.position(0);
        for (int oloop = 0; oloop < fv.size(); oloop++) {
            v = (int[]) (fv.get(oloop));
            t = (int[]) (ft.get(oloop));
            for (int iloop = 0; iloop < v.length; iloop++) {
                //fill in the texture coordinate data
                for (int tloop = 0; tloop < tcoords.length; tloop++)
                    //only T2F is supported in interleavedarrays!!
                    tcoords[tloop] = ((float[]) vtData.get(t[iloop] - 1))[tloop];
                modeldata.put(tcoords);
                //fill in the vertex coordinate data
                for (int vloop = 0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[]) vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }

    private void ConstructNV() {
        int[] v, n;
        float coords[] = new float[3];
        int fbSize = PolyCount * (FaceMultiplier * 6); //3v per poly, 3vn per poly
        modeldata = GLBuffers.newDirectFloatBuffer(fbSize);
        modeldata.position(0);
        for (int oloop = 0; oloop < fv.size(); oloop++) {
            v = (int[]) (fv.get(oloop));
            n = (int[]) (fn.get(oloop));
            for (int iloop = 0; iloop < v.length; iloop++) {
                //fill in the normal coordinate data
                for (int vnloop = 0; vnloop < coords.length; vnloop++)
                    coords[vnloop] = ((float[]) vnData.get(n[iloop] - 1))[vnloop];
                modeldata.put(coords);
                //fill in the vertex coordinate data
                for (int vloop = 0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[]) vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }

    private void ConstructV() {
        int[] v;
        float coords[] = new float[3];
        int fbSize = PolyCount * (FaceMultiplier * 3); //3v per poly
        modeldata = GLBuffers.newDirectFloatBuffer(fbSize);
        modeldata.position(0);
        for (int oloop = 0; oloop < fv.size(); oloop++) {
            v = (int[]) (fv.get(oloop));
            for (int iloop = 0; iloop < v.length; iloop++) {
                //fill in the vertex coordinate data
                for (int vloop = 0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[]) vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }

    public void drawModel(GL2 inGL) {
        if (init) {
            ConstructInterleavedArray(inGL);
            cleanup();
            init = false;
        }
        inGL.glColor3f(1,1,1);
        inGL.glDrawArrays(FaceFormat, 0, PolyCount * FaceMultiplier);
    }

    private void cleanup() {
    	/*
        vData.clear();
        vtData.clear();
        vnData.clear();
        fv.clear();
        ft.clear();
        fn.clear();
        modeldata.clear();*/
    }

    public static int loadWavefrontObjectAsDisplayList(GL2 inGL,String inFileName) {
        int tDisplayListID = inGL.glGenLists(1);
        WavefrontObjectLoader_DisplayList tWaveFrontObjectModel = new WavefrontObjectLoader_DisplayList(inFileName);
        inGL.glNewList(tDisplayListID,GL_COMPILE);
        tWaveFrontObjectModel.drawModel(inGL);
        inGL.glEndList();
        return tDisplayListID;
    }

}