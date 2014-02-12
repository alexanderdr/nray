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
 * RayScene.java
 *
 * 
 * This just contains a list of objects and casts any ray cast to it
 * to each of its objects in turn
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import java.util.*;
import nray.*;
public class RayScene {
    
    ArrayList<RTObject> objects = new ArrayList<RTObject>();
    
    public RayScene(){

    }
    
    public void addRTObject(RTObject ro){
        objects.add(ro);
    }
    
    //This is the primary method used for ray casting with all objects
    public void cast(Ray r, Intersector it){
        //eventually this will be optimized to eliminate recasting the ray against
        //more distant objects when a hit has already been found
        Vec last = null;//new Vec();
        for(RTObject rto:objects){
            rto.cast(r, it);
            if((r.intersection!=null)&&(!r.intersection.equals(last))){
                r.object = rto;
            }
            last = r.intersection;
        }
    }

    public void reverseCast(Ray r, Intersector it, int index, RTObject lastObject){
        //eventually this will be optimized to eliminate recasting the ray against
        //more distant objects when a hit has already been found
        Vec last = null;//new Vec();
        for(RTObject rto:objects){
            if(lastObject == rto){
                rto.reverseCast(r, it, index);
            } else {
                rto.cast(r, it);
            }
            if((r.intersection!=null)&&(!r.intersection.equals(last))){
                r.object = rto;
            }
            last = r.intersection;
        }
    }
    
    public void clearS(){
        for(RTObject rto: objects){
            rto.clearS();
        }
    }
}
