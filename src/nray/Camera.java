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
 * Camera.java
 *
 * A generic camera using Euler angles
 *
 */

package nray;

/**
 *
 * @author dalexander
 */
public class Camera{
    
    float rotx = 0,roty = 0,rotz = 0;
    static float[] invRot = new float[16];

    private final float toRad = ((float)Math.PI)/180;

    Vec pos;
    /** Creates a new instance of Camera */
    public Camera() {
        pos = new Vec();
    }

    public Camera(float x, float y, float z){
        pos = new Vec(x, y, z);
    }

    public Camera(Camera other){
        this.set(other);
    }

    public void set(Camera other){
        pos = new Vec(other.pos);
        rotx = other.rotx;
        roty = other.roty;
        rotz = other.rotz;
    }
    
    public void translate(Vec v){
        pos.x -= v.x;
        pos.y -= v.y;
        pos.z -= v.z;
    }
    
    public void rotateY(float f){
        roty+=f * toRad;
    }
    
    public void rotateX(float f){
        rotx+=f * toRad;
    }

    public void translateForward(float f){
        pos.x -= Math.sin(roty)*Math.cos(rotx)*f;
        pos.y -= Math.sin(rotx)*Math.cos(rotz)*f+Math.sin(rotz)*Math.cos(rotx)*f;
        pos.z -= Math.cos(rotx)*Math.cos(roty)*f;

    }
    
    public void translateRight(float f){

        pos.x -= -Math.cos(rotz)*Math.cos(roty)*f;
        pos.z -= Math.sin(roty)*f;

    }

    //technically the caller could manipulate the returned object, but it would take time to clone
    public Vec getPos(){
        return pos;
    }

    public float getRotXRad(){
        return rotx;
    }

    public float getRotYRad(){
        return roty;
    }

    public float getRotZRad(){
        return rotz;
    }

}
