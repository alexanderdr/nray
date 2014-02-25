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
 * Box.java
 *
 * Just a collection with some math helper functions
 *
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import nray.*;
public class Box{
    //Vec min, max;
    float ix, iy, iz;
    float ax, ay, az;
    Vec color = Vec.ZERO;
    boolean hasChildren = false;
    //float lastS = Float.NEGATIVE_INFINITY;
    public static Vec X_COLOR = new Vec(.1, 0, 0);
    public static Vec Y_COLOR = new Vec(0, .1, 0);
    public static Vec Z_COLOR = new Vec(0, 0, .1);
    Box(Vec min,Vec max){

        ix = min.x;
        iy = min.y;
        iz = min.z;

        ax = max.x;
        ay = max.y;
        az = max.z;

        sanitize();

    }

    public void sanitize(){
        float temp = 0;
        if(ix>ax){
            temp = ax;
            ax = ix;
            ix = temp;
        }
        if(iy>ay){
            temp = ay;
            ay = iy;
            iy = temp;
        }
        if(iz>az){
            temp = az;
            az = iz;
            iz = temp;
        }
    }

    //public void clearS(){
        //lastS = Float.NEGATIVE_INFINITY;
    //}
    
    public float volume(){
        Vec temp;

        //temp = max.sub(min);
        temp = new Vec(ax, ay, az).sub(new Vec(ix, iy, iz));

        return temp.x*temp.y*temp.z;
    }
    
}
