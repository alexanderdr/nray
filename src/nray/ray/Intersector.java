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

        float maxS = Float.NEGATIVE_INFINITY;
        float minT = r.t;//Float.MAX_VALUE;
        
        r.boxHits++;

        //This doesn't seem to convince the jit to produce clean simd code, just do it the old way
        /*float sx = (b.ix - r.origin.x) * r.rayInv.x;
        float sy = (b.iy - r.origin.y) * r.rayInv.y;
        float sz = (b.iz - r.origin.z) * r.rayInv.z;

        float tx = (b.ax - r.origin.x) * r.rayInv.x;
        float ty = (b.ay - r.origin.y) * r.rayInv.y;
        float tz = (b.az - r.origin.z) * r.rayInv.z;*/

        if(r.ray.x==0f){
            if(r.origin.x < b.ix || r.origin.x > b.ax){
                return false;
            }
            //hit.x = r.origin.x;
        } else {
            float s = (b.ix - r.origin.x) * r.rayInv.x;
            float t = (b.ax - r.origin.x) * r.rayInv.x;

            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s; //maxS = Math.max(maxS,s); is slower.
            if( t < minT ) minT = t;

            //hit.x = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }

        if(r.ray.y==0f){
            if(r.origin.y < b.iy || r.origin.y > b.ay){
                return false;
            }
            //hit.y = r.origin.y;
        } else {
            float s = (b.iy - r.origin.y) * r.rayInv.y;
            float t = (b.ay - r.origin.y) * r.rayInv.y;

            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s;
            if( t < minT ) minT = t;

            //hit.y = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }

        if(r.ray.z==0f){
            if(r.origin.z < b.iz || r.origin.z > b.az){
                return false;
            }
            //hit.z = r.origin.z;
        } else {
            float s = (b.iz - r.origin.z) * r.rayInv.z;
            float t = (b.az - r.origin.z) * r.rayInv.z;

            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s;
            if( t < minT ) minT = t;

            //hit.z = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }

        //r.Ibox = b;

        r.s = maxS;
        r.boxT = minT;
        //b.lastS = maxS;
        return true;
    }

    public boolean cast(Ray r,BinaryBox b){

        float maxS = Float.NEGATIVE_INFINITY;//-Float.MAX_VALUE;
        float minT = r.t;//Float.MAX_VALUE;
        //Vec mMin = b.min;
        //Vec mMax = b.max;

        r.boxHits++;

        //Vec hit = new Vec();

        if(r.ray.x==0f){
            if(r.origin.x < b.ix || r.origin.x > b.ax){
                return false;
            }
            //hit.x = r.origin.x;
        } else {
            float s = (b.ix - r.origin.x) * r.rayInv.x;
            float t = (b.ax - r.origin.x) * r.rayInv.x;

            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s; //maxS = Math.max(maxS,s); is slower.
            if( t < minT ) minT = t;

            //hit.x = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }

        if(r.ray.y==0f){
            if(r.origin.y < b.iy || r.origin.y > b.ay){
                return false;
            }
            //hit.y = r.origin.y;
        } else {
            float s = (b.iy - r.origin.y) * r.rayInv.y;
            float t = (b.ay - r.origin.y) * r.rayInv.y;

            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s;
            if( t < minT ) minT = t;

            //hit.y = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }

        if(r.ray.z==0f){
            if(r.origin.z < b.iz || r.origin.z > b.az){
                return false;
            }
            //hit.z = r.origin.z;
        } else {
            float s = (b.iz - r.origin.z) * r.rayInv.z;
            float t = (b.az - r.origin.z) * r.rayInv.z;

            if(s > t){
                float temp = s;
                s = t;
                t = temp;
            }

            if( s > maxS ) maxS = s;
            if( t < minT ) minT = t;

            //hit.z = s;
            if(minT < 0f || maxS > minT){
                return false;
            }
        }

        //r.Ibox = b;

        r.s = maxS;
        r.boxT = minT;
        //b.lastS = maxS;
        return true;
    }

    
    //Checks for triangle-ray intersection with a given triangle and ray, and
    //sets values on the Ray if an intersection is found
    public boolean cast(Triangle tri, Ray r){
        //increments for heatmaps
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
        
        r.origin.sub(p0,s);
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

    //this thankfully causes hotspot in openjdk 7u40 to produce some simd instructions on x86-64 at least.
    public boolean cast(SmallTriangle tri, Ray r){
        r.triHits++;

        //cross
        //r.ray.cross(e2,p);
        float px = r.ray.y*tri.e2z - r.ray.z*tri.e2y;
        float py = r.ray.z*tri.e2x - r.ray.x*tri.e2z;
        float pz = r.ray.x*tri.e2y - r.ray.y*tri.e2x;

        //dot
        //float a = e1.dot(p);
        float a = px*tri.e1x + py*tri.e1y + pz*tri.e1z;

        if(a==0f){
            return false;
        }

        float f = 1f/a;

        //sub
        //r.origin.sub(p0,s);
        float sx = r.origin.x - tri.px;
        float sy = r.origin.y - tri.py;
        float sz = r.origin.z - tri.pz;

        //dot
        //float u = f*s.dot(p);
        float u = f*(sx*px + sy*py + sz*pz);

        //if(u < 0f || u > 1f) return false;

        //cross
        //s.cross(e1,q);
        float qx = sy*tri.e1z - sz*tri.e1y;
        float qy = sz*tri.e1x - sx*tri.e1z;
        float qz = sx*tri.e1y - sy*tri.e1x;

        //dot
        //float v = f*r.ray.dot(q);
        float v = f*(r.ray.x*qx + r.ray.y*qy + r.ray.z*qz);


        //if(v < 0f || u+v > 1f) return false;

        //dot again
        //float t = f*e2.dot(q);
        float t = f*(tri.e2x*qx + tri.e2y*qy + tri.e2z*qz);


        if(v < 0f || u < 0f || u+v > 1f) return false;

        if (t>=0f){

            if(t>=r.t) return true; //we've already hit a closer triangle than this one

            Vec pos;
            //pos=(e1.scaleNew(u));
            //pos.addInPlace(e2.scaleNew(v));
            //pos.addInPlace(p0);
            pos = new Vec(tri.e1x * u, tri.e1y * u, tri.e1z * u);
            pos.addInPlace(tri.e2x * v, tri.e2y * v, tri.e2z * v);
            pos.addInPlace(tri.px, tri.py, tri.pz);

            r.intersection = pos;
            r.Itri = tri.parentRef;

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
