
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
 * Face.java
 *
 * A single face, assumes that the verticies are presented in a right-handed way.
 * Works for either triangles or quads, uses tringle strip (OpenGL)
 * Can produce an ArrayList of triangles for use with the nray.ray package
 */

package nray.obj;

/**
 *
 * @author dalexander
 */
import java.util.*;
import nray.*;
import nray.ray.*;
public class Face{
    
    float[][] normals = new float[4][3];
    float[][] verts = new float[4][3];
    float[][] texs = new float[4][2];
    
    int[] vertnums = new int[4];
    
    boolean textured = false;
    int count = 3;
    float scale;
    
    Face(Vec one, Vec two, Vec three, Vec four,int[] vertnums,Vec tone, Vec ttwo, Vec tthree, Vec tfour,Vec none, Vec ntwo, Vec nthree, Vec nfour){
        this(one,two,three,four,vertnums);
        texs[0] = tone.toArray();
        normals[0] = none.toArray();
        texs[1] = ttwo.toArray();
        normals[1] = ntwo.toArray();
        texs[2] = tthree.toArray();
        normals[2] = nthree.toArray();
        if(nfour!=null){
            count = 4;
            texs[3] = tfour.toArray();
            normals[3] = nfour.toArray();
        } else {
            count = 3;
        }
        textured = true;
    }
    
    Face(Vec one, Vec two, Vec three, Vec four,int[] vertnums,Vec tone, Vec ttwo, Vec tthree, Vec tfour){
        this(one,two,three,four,vertnums);
        texs[0] = tone.toArray();
        texs[1] = ttwo.toArray();
        texs[2] = tthree.toArray();
        if(tfour!=null){
            count = 4;
            texs[3] = tfour.toArray();
        } else {
            count = 3;
        }
        textured = true;
    }
    
    Face(Vec one, Vec two, Vec three, Vec four,int[] vertnums){
        verts[0] = one.toArray();
        verts[1] = two.toArray();
        verts[2] = three.toArray();
        if(four!=null){
            count = 4;
            verts[3] = four.toArray();
        }
        this.vertnums = vertnums;
    }
    
    public void scale(float scale){
        this.scale = scale;
        for(int x = 0;x<verts.length;x++){
            for(int y = 0;y<verts[x].length;y++){
                verts[x][y] *= scale;
                
            }
        }
    }
    
    public ArrayList<Triangle> getTriangles(){
        //1,2,0 2,0,3
        ArrayList<Triangle> temp = new ArrayList<Triangle>();
        
        temp.add(new Triangle(new Vec(verts[1]),new Vec(verts[2]),new Vec(verts[0]),new Vec(normals[1]),new Vec(normals[2]), new Vec(normals[0])));
        if(count>3){
            temp.add(new Triangle(new Vec(verts[2]),new Vec(verts[3]),new Vec(verts[0]),new Vec(normals[2]),new Vec(normals[3]), new Vec(normals[0])));
        }

        return temp;
    }
    
    public boolean intersects(Face f){

        Vec sum = new Vec();
        Vec[] vs = new Vec[count];
        for(int x = 0;x<count;x++){
            vs[x] = new Vec(verts[x]);
            sum = sum.add(vs[x]);
        }
        
        sum.divideBy((float)count);
        
        Vec center = sum;
        Vec distance = vs[0];
        
        for(int x = 1;x<count;x++){
           Vec tvec = vs[x];
           tvec.sub(center);
           tvec.abs();
           if(tvec.mag()>distance.mag()){
               distance = tvec;
           }
        }
        
        float opDistance = distance.mag();
        for(int x = 0;x<f.count;x++){
            if(new Vec(f.verts[0]).sub(center).mag()<opDistance){
                return true;
            }
        }
        
        return false;
    }
    
    public void translate(float x,float y,float z){
        for(int i = 0;i<count;i++){
            verts[i][0] += x;
            verts[i][1] += y;
            verts[i][2] += z;
        }
    }
    
}