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
 * Ray.java
 *
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import nray.Vec;

public class Ray{
    int bounces = 1;
    Vec ray, origin; //ray is the direction vector, origin in the origin vector
    Vec color = new Vec(); //Color of this ray (defaults to black)
    Vec boxColor = new Vec(); //Color of this ray (defaults to black)
    Vec intersection = null; //location of intersection
    Vec rayInv = new Vec();
    float t = Float.MAX_VALUE; //distance from triangle scaled by ray
    float boxT = Float.MAX_VALUE;
    Triangle Itri; //triangle of intersection
    Box Ibox;
    int boxIndex;
    int boxHits = 0;
    int triHits = 0;
    float u = -1,v = -1;
    float s = Float.MAX_VALUE;
    //float minT = Float.MAX_VALUE;
    boolean castAny = false;
    RTObject object;
    boolean hitBackfacing = false;

    static int maxReasonableHits = 1;



    Ray(Vec r,Vec or){
        ray = r;
        origin = or;
        rayInv.set(1/r.x, 1/r.y, 1/r.z);
    }
    
    public void init(Vec r,Vec or){
        ray = r;
        origin = or;
        rayInv.set(1/r.x, 1/r.y, 1/r.z);
        color.set(0f,0f,0f);
        boxColor.set(0f,0f,0f);
        intersection = null;
        Itri = null;
        t = Float.MAX_VALUE;
        s = Float.MAX_VALUE;
        boxT = Float.MAX_VALUE;
        boxHits = 0;
        triHits = 0;
        u = -1;
        v = -1;
        object = null;
        
        boxIndex = -1;
        castAny = false;


    }
    
}
