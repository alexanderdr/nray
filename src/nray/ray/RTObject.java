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
 * RTObject.java
 *
 * 
 * This object is made up of a list of triangles, and contains a bounding volume heirarchy
 * (currently just an AABB).
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import nray.*;
import java.util.*;
import nray.obj.*;
public class RTObject {
    Vec pos = new Vec();
    ArrayList<Triangle> tris = new ArrayList<Triangle>();
    Box aabb;
    ArrayList<Triangle> left, right;
    Box leftaabb, rightaabb;
    
    BVH bvh;
    
    float eta = 1.5f;
    
    public int triangleCount;
    
    public RTObject(){
    }
    
    public float median(float[] nums){
        if(nums.length%2==1){
            return (nums[nums.length/2]+nums[(nums.length+1)/2])/2f;
        } else {
            return nums[nums.length/2];
        }
    }
    
    public RTObject(Mesh m){
        m.sanityCheck();
        tris.addAll(m.getTriangles());
        System.out.println("Object with: "+tris.size()+" triangles");
        
        Vec tmin = new Vec();
        Vec tmax = new Vec();
        int total = tris.size();
        float[] maxSortX = new float[tris.size()*2];
        int i = 0;
        for(Triangle t: tris){
            maxSortX[i++] = t.max.x;
            maxSortX[i++] = t.min.x;
            updateExtremes(t,tmin,tmax);
            //++i;
        }
        
        triangleCount = i/2;
        
        Arrays.sort(maxSortX);
        
        //System.out.println((tmin.x+tmax.x)/2f+" "+median(maxSortX)+" "+Arrays.toString(maxSortX));
        Vec pivot = new Vec((tmin.x+tmax.x)/2f,(tmin.y+tmax.y)/2f,(tmin.z+tmax.z)/2f);
        left = new ArrayList<Triangle>();
        right = new ArrayList<Triangle>();
        for(Triangle t: tris){
            if(t.max.x>pivot.x){
                right.add(t);
            }
            
            if(t.min.x<pivot.x){
                left.add(t);
            }
        }
        Vec leftbound = new Vec(tmax);
        leftbound.x = pivot.x;
        leftaabb = new Box(tmin,leftbound);
        Vec rightstart = new Vec(tmin);
        rightstart.x = pivot.x;
        rightaabb = new Box(rightstart, tmax);
        System.out.println(left.size() +" "+right.size());
        System.out.println(leftaabb.min + " | " + leftaabb.max +" | "+rightaabb.min +" | "+rightaabb.max);
        aabb = new Box(tmin,tmax);
        
        bvh = new BVH(tris,4);
    }
    
    public static void updateExtremes(Triangle t,Vec tmin,Vec tmax){
            if(t.p1.x<tmin.x){
                tmin.x = t.p1.x;
            }

            if (t.p1.x>tmax.x){
                tmax.x = t.p1.x;
            }
            
            if(t.p1.y<tmin.y){
                tmin.y = t.p1.y;
            }
            if (t.p1.y>tmax.y){
                tmax.y = t.p1.y;
            }
            
            if(t.p1.z<tmin.z){
                tmin.z = t.p1.z;
            }
            if (t.p1.z>tmax.z){
                tmax.z = t.p1.z;
            }
            
            if(t.p2.x<tmin.x){
                tmin.x = t.p2.x;
            }
            if (t.p2.x>tmax.x){
                tmax.x = t.p2.x;
            }
            
            if(t.p2.y<tmin.y){
                tmin.y = t.p2.y;
            }
            if (t.p2.y>tmax.y){
                tmax.y = t.p2.y;
            }
            
            if(t.p2.z<tmin.z){
                tmin.z = t.p2.z;
            }
            if (t.p2.z>tmax.z){
                tmax.z = t.p2.z;
            }
            
            if(t.p3.x<tmin.x){
                tmin.x = t.p3.x;
            }
            if (t.p3.x>tmax.x){
                tmax.x = t.p3.x;
            }
            
            if(t.p3.y<tmin.y){
                tmin.y = t.p3.y;
            }
            if (t.p3.y>tmax.y){
                tmax.y = t.p3.y;
            }
            
            if(t.p3.z<tmin.z){
                tmin.z = t.p3.z;
            }
            if (t.p3.z>tmax.z){
                tmax.z = t.p3.z;
            }
            //t.max = tmax;
            //t.min = tmin;
    }
    

    public static int count = 0;
    public static float icullRate = 0;
    public void cast(Ray r, Intersector it){
        bvh.cast(r, it);

    }
    
    public void clearS(){
        bvh.clearS();
    }
    
    public void reverseCast(Ray r, Intersector it, int index){
        bvh.reverseCast(r, it, index);
    }
    
}


