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
 * Mesh.java
 *
 * This class represents a series of faces (usually imported) it is drawable
 * and tries to optimize by creating a list of the faces.  Also applying textures
 * to the Faces if the Faces have their texture coordinates set.
 *
 */

package nray.obj;

/**
 *
 * @author dalexander
 */

import java.util.*;
import nray.*;
import nray.ray.Triangle;
public class Mesh{
    
    ArrayList<Vec> verts = new ArrayList<Vec>();
    ArrayList<Vec> normals = new ArrayList<Vec>();
    ArrayList<Face> faces = new ArrayList<Face>();
    float scale = 1;
    //static int listCount = 0;
    int list;
    boolean compiled = false;
    
    Texture tex = null;
    
    /** Creates a new instance of Mesh */
    public Mesh(ArrayList<Vec> verts,ArrayList<Face> faces) {
        this.verts = verts;
        this.faces = faces;
    }
    
    public Mesh(ArrayList<Vec> verts,ArrayList<Vec> normals,ArrayList<Face> faces) {
        this.verts = verts;
        this.faces = faces;
        this.normals = normals;
    }
    
    public void sanityCheck(){
        ArrayList<Vec> vertList = new ArrayList<Vec>();
        ArrayList<Vec> normList = new ArrayList<Vec>();
        for(Face f:faces){
            for(int i = 0;i<f.count;i++){
                float[] v3 = f.verts[i];
                Vec v = new Vec(v3);
                float[] n3 = f.normals[i];
                Vec n = new Vec(n3);
                if(!vertList.contains(v)){
                    vertList.add(v);
                    normList.add(n);
                } else {
                    int index = vertList.indexOf(v);
                    Vec nt = normList.get(index);
                    if(!nt.equals(n)){
                        //System.out.println("Well, this is a problem");
                        //System.exit(-1);
                    }
                }
            }
        }
    }
    
    public ArrayList<Triangle> getTriangles(){
        ArrayList<Triangle> list = new ArrayList<Triangle>();
        for(Face f: faces){
            list.addAll(f.getTriangles());
        }
        return list;
    }
    
    public void setTexture(Texture t){
        tex = t;
    }
    
    public void setScale(float scale){
        this.scale = scale;
        for(Face f:faces){
            f.scale(scale);
        }
    }

    public void translate(Vec v){
        for(Vec current : verts){
            current.addInPlace(v);
        }
        for(Face f : faces){
            f.translate(v.x, v.y, v.z);
        }
    }
    
}