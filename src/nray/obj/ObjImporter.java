/*
 *  Copyright 2014 Derek Alexander
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * ObjImporter.java
 *
 * This is a tool for importing objects.  Currently it only imports a large
 * subset of the geometry for .obj.  Any .obj file containing a face with
 * more than 5 verticies will cause problems.  Thankfully both Maya and ZBrush
 * export 3 and 4 vertex faces.
 *
 * This is a singleton class because at the moment there are no configuration
 * options, and even if there were it would be unlikely they would need to change
 * within the same program.  If it becomes a requirement to have different sets
 * of configuration options the static loading will remain the same and use
 * the default configuration and then this class will have a public constructor.
 *
 *
 */

package nray.obj;

/**
 *
 * @author dalexander
 */
import java.io.*;
import java.util.*;
import nray.*;
public class ObjImporter {
    
    /** Creates a new instance of ObjImporter */
    private ObjImporter() {
    }

    public static Mesh importObj(String filename){
        File f = new File(filename);
        String matName;
        ArrayList<Vec> v = new ArrayList<Vec>();
        ArrayList<Vec> vt = new ArrayList<Vec>();
        ArrayList<Vec> vn = new ArrayList<Vec>();
        ArrayList<Face> faces = new ArrayList<Face>();
        int line = 1;
        try{
            BufferedReader in = new BufferedReader(new FileReader(f));
            int[] temp = new int[4];
            
            while(in.ready()){
                String input = in.readLine();
                line++;
                //Comment in the .obj
                if((input.trim().length()==0)||(input.trim().charAt(0)=='#')){
                    continue;
                }
                
                StringTokenizer stz = new StringTokenizer(input);
                String type = stz.nextToken();
                
                if(type.equals("v")){ //Vertex 
                    float x = 0f,y = 0f,z = 0f;
                    x = Float.parseFloat(stz.nextToken());
                    y = Float.parseFloat(stz.nextToken());
                    z = Float.parseFloat(stz.nextToken());
                    v.add(new Vec(x,y,z));
                } else if(type.equals("vt")){ //Texture element
                    float x = 0f,y = 0f,z = 0f;
                    x = Float.parseFloat(stz.nextToken());
                    if(stz.hasMoreTokens()){
                        y = Float.parseFloat(stz.nextToken());
                        if(stz.hasMoreTokens()){
                            z = Float.parseFloat(stz.nextToken());
                        }
                    }
                    vt.add(new Vec(x,y,z));
                } else if(type.equals("vn")){ //normal
                    float x = 0f,y = 0f,z = 0f;
                    x = Float.parseFloat(stz.nextToken());
                    y = Float.parseFloat(stz.nextToken());
                    z = Float.parseFloat(stz.nextToken());
                    Vec t = new Vec(x,y,z);
                    //because meshlab normals apparently aren't normalized
                    if(t.mag() > 1.0001 || t.mag() < .9999){
                        t.normalizeInPlace();
                    }
                    vn.add(t);
                } else if(type.equals("f")){ //A face
                    int t, t2 = -1, t3 = -1;
                    
                    Vec tone=Vec.ZERO,ttwo=Vec.ZERO,tthree=Vec.ZERO,tfour=Vec.ZERO;
                    Vec none=Vec.ZERO,ntwo=Vec.ZERO,nthree=Vec.ZERO;
                    StringTokenizer aux = new StringTokenizer(stz.nextToken(),"/");
                    t = Integer.parseInt(aux.nextToken())-1;
                    if(aux.countTokens()>=2){
                        t2 = Integer.parseInt(aux.nextToken())-1;
                    }
                    if(aux.countTokens()>=1){
                        t3 = Integer.parseInt(aux.nextToken())-1;
                    }
                    //System.out.println(t3);
                    Vec one = v.get(t);
                    
                    if(t2>0){
                        tone = vt.get(t2);
                    }
                    if(t3>0){
                        none = vn.get(t3);
                    }
                    temp[0] = t;
                    aux = new StringTokenizer(stz.nextToken(),"/");
                    t = Integer.parseInt(aux.nextToken())-1;
                    if(aux.countTokens()>=2){
                        t2 = Integer.parseInt(aux.nextToken())-1;
                    }
                    if(aux.countTokens()>=1){
                        t3 = Integer.parseInt(aux.nextToken())-1;
                    }
                    Vec two = v.get(t);
                    if(t2>0){
                        ttwo = vt.get(t2);
                    }
                    if(t3>0){
                        ntwo = vn.get(t3);
                    }
                    temp[1] = t;
                    aux = new StringTokenizer(stz.nextToken(),"/");
                    t = Integer.parseInt(aux.nextToken())-1;
                    if(aux.countTokens()>=2){
                        t2 = Integer.parseInt(aux.nextToken())-1;
                    }
                    if(aux.countTokens()>=1){
                        t3 = Integer.parseInt(aux.nextToken())-1;
                    }
                    Vec three = v.get(t);
                    if(t2>0){
                        tthree = vt.get(t2);
                    }
                    if(t3>0){
                        nthree = vn.get(t3);
                    }
                    temp[2] = t;
                    Vec four = null, nfour = null;
                    if(stz.hasMoreTokens()){ //Faces don't always have 4 elements
                        aux = new StringTokenizer(stz.nextToken(),"/");
                        t = Integer.parseInt(aux.nextToken())-1;
                        if(aux.countTokens()>=2){
                            t2 = Integer.parseInt(aux.nextToken())-1;
                        }
                        if(aux.countTokens()>=1){
                            t3 = Integer.parseInt(aux.nextToken())-1;
                        }
                        four = v.get(t);
                        if(t2>0){
                            tfour = vt.get(t2);
                        }
                        if(t3>0){
                            nfour = vn.get(t3);
                        }
                        temp[3] = t;
                    }
                    faces.add(new Face(one,two,three,four,temp,tone,ttwo,tthree,tfour,none,ntwo,nthree,nfour));
                    
                } else if(type.equals("mtllib")){
                    matName = stz.nextToken();
                } else {
                    //System.out.println("Ignored line");
                }
                
            }
            return new Mesh(v,vn,faces);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(line);
        }
        return null;
    }
    
    public static Mesh importTexturedObj(String obj, String tex){
        Texture t = new Texture(tex);
        Mesh temp = importObj(obj);
        temp.setTexture(t);
        return temp;
    }
     
}

