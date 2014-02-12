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
 * Intersector.java
 *
 * 
 * This is the main workhorse of the ray tracer, it many methods with different
 * arguments to see if a ray and an triangle (each defined in different ways) intersect
 * it also can check for intersection between an AABB and a ray.  Only the Ray,Triangle
 * and Ray,Box intersection methods are actually used as of November 19 2007.
 *
 * It is somewhat optimized, which is why you want an intersector context (the temp variables are reused)
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import nray.*;
public class Intersector {
    
    public Intersector(){
        
    }
    
    private Vec e1 = new Vec(), e2 = new Vec(), p = new Vec(), s = new Vec(), q = new Vec(), p0;

    //ray box intersection
    //private static Vec S = new Vec(), T = new Vec();
    public boolean cast(Ray r,Box b){
        //if(b.lastS>r.t) return false;
        
        RayTracer.castAgainstB++;
        float maxS = -Float.MAX_VALUE;
        float minT = r.t;//Float.MAX_VALUE;
        Vec mMin = b.min;
        Vec mMax = b.max;
        float ps, pt;
        
        r.boxHits++;
        
        Vec hit = new Vec();
        
        if(r.ray.x==0){
            if(r.origin.x < mMin.x || r.origin.x > mMax.x){
                return false;
            }
            hit.x = r.origin.x;
        } else {
            float s = (mMin.x - r.origin.x)/r.ray.x;
            float t = (mMax.x - r.origin.x)/r.ray.x;
            //ps = Math.abs(s);
            //pt = Math.abs(t);
            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s; //maxS = Math.max(maxS,s); is slower.
            if( t < minT ) minT = t;
            
            hit.x = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }
        
        if(r.ray.y==0){
            if(r.origin.y < mMin.y || r.origin.y > mMax.y){
                return false;
            }
            hit.y = r.origin.y;
        } else {
            float s = (mMin.y - r.origin.y)/r.ray.y;
            float t = (mMax.y - r.origin.y)/r.ray.y;
            //ps = Math.abs(s);
            //pt = Math.abs(t);
            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s;
            if( t < minT ) minT = t;
            
            hit.y = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }
        
        if(r.ray.z==0){
            if(r.origin.z < mMin.z || r.origin.z > mMax.z){
                return false;
            }
            hit.z = r.origin.z;
        } else {
            float s = (mMin.z - r.origin.z)/r.ray.z;
            float t = (mMax.z - r.origin.z)/r.ray.z;
            //ps = Math.abs(s);
            //pt = Math.abs(t);
            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s;
            if( t < minT ) minT = t;
            
            hit.z = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }


        //r.Ibox = b;
        r.s = maxS;
        r.boxT = minT;
        b.lastS = maxS;
        return true;
    }
    

    
    //Checks for triangle-ray intersection with a given triangle and ray, and
    //sets values on the Ray if an intersection is found
    public boolean cast(Triangle tri, Ray r){
        //increments for heatmaps
        RayTracer.castAgainstR++;
        r.triHits++;
        tri.hit++;

        p0 = tri.p1;
        e1 = tri.e1;
        e2 = tri.e2;
        
        r.ray.cross(e2,p);
        float a = e1.dot(p);
        
        if(a==0f){
            return false;
        }
        
        float f = 1f/a;
        
        r.origin.sub(p0,s);  //rayOrigin-p0
        float u = f*s.dot(p);
        //if(u < 0f || u > 1f) return false;
        
        s.cross(e1,q);
        float v = f*r.ray.dot(q);
        //if(v < 0f || u+v > 1f) return false;
        
        float t = f*e2.dot(q);
        
        if(v < 0f || u < 0f || u+v > 1f) return false;
        
        if (t>=0f){

            //if(Math.abs(t)>=Math.abs(r.t)) return true;
            if(t>=r.t) return true; //we've already hit a closer triangle than this one
            
            Vec pos;
            pos=(e1.scaleNew(u));
            pos.addInPlace(e2.scaleNew(v));
            pos.addInPlace(p0);

            r.intersection = pos;
            r.Itri = tri;

            r.t = t;
            r.u = u;
            r.v = v;
            r.hitBackfacing = a<0;
            
            return true;
            
        } else {

        }
        return false;
    }
    
    

}
