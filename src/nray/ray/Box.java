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
    Vec min, max;
    Vec color = Vec.ZERO;
    boolean hasChildren = false;
    float lastS = Float.NEGATIVE_INFINITY;
    public static Vec X_COLOR = new Vec(.1, 0, 0);
    public static Vec Y_COLOR = new Vec(0, .1, 0);
    public static Vec Z_COLOR = new Vec(0, 0, .1);
    Box(Vec min,Vec max){

        this.min = min;
        this.max = max;
        sanitize();
    }

    public void sanitize(){
        float temp = 0;
        if(min.x>max.x){
            temp = max.x;
            max.x = min.x;
            min.x = temp;
        }
        if(min.y>max.y){
            temp = max.y;
            max.y = min.y;
            min.y = temp;
        }
        if(min.z>max.z){
            temp = max.z;
            max.z = min.z;
            min.z = temp;
        }
    }

    public void clearS(){
        lastS = Float.NEGATIVE_INFINITY;
    }
    
    public float volume(){
        Vec temp;
        
        temp = max.sub(min);

        return temp.x*temp.y*temp.z;
    }
    
}
