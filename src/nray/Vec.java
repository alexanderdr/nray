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
 * Vec.java
 *
 * A 3 element float Vector with lots of utility methods.  Useful for any graphics work.
 */

package nray;

/**
 *
 * @author dalexander
 */
public final class Vec{
    
    public float x,y,z;

    public static final Vec ZERO = new Vec(0,0,0);
    
    /** Creates a new instance of Vec */
    public Vec(float x,float y,float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec(double x,double y,double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }
    
    public boolean equals(Object other){
        if(!(other instanceof Vec)) return super.equals(other);
        
        Vec o = (Vec)other;
        return (x==o.x&&y==o.y&&z==o.z);
    }
    
    public Vec(Vec v){
        this(v.x,v.y,v.z);
    }
    
    public Vec(float[] vals){
        this(vals[0],vals[1],vals[2]);
    }
    
    public Vec(){
        this(0,0,0);
    }
    
    public final void set(Vec other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
    
    public final void set(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
        //return this;
    }

    public final void reset(){
        this.set(0, 0, 0);
    }
    
    public final float[] toArray(){
        return new float[]{x,y,z};
    }
    
    public final Vec add(Vec other){
        Vec temp = new Vec();
        temp.x = x + other.x;
        temp.y = y + other.y;
        temp.z = z + other.z;
        return temp;
    }
    
    public final Vec add(Vec other,Vec temp){
        temp.x = x + other.x;
        temp.y = y + other.y;
        temp.z = z + other.z;
        return temp;
    }

    public final Vec scaleThenAddTo(float scale, Vec temp){
        temp.addInPlace(x*scale, y*scale, z*scale);
        return temp;
    }
    
    public final Vec addInPlace(Vec other){
        x = x + other.x;
        y = y + other.y;
        z = z + other.z;
        return this;
    }
    
    public final Vec addInPlace(float ox,float oy,float oz){
        x = x + ox;
        y = y + oy;
        z = z + oz;
        return this;
    }
    
    public final Vec subInPlace(Vec other){
        x = x - other.x;
        y = y - other.y;
        z = z - other.z;
        return this;
    }
    
    public final Vec sub(Vec other){
        return sub(other,new Vec());
    }
    
    public final Vec sub(Vec other,Vec temp){
        temp.x = x - other.x;
        temp.y = y - other.y;
        temp.z = z - other.z;
        return temp;
    }
    
    public final Vec neg(){
        Vec temp = new Vec();
        temp.x = -x;
        temp.y = -y;
        temp.z = -z;
        return temp;
    }
    
    public final Vec abs(){
        Vec temp = new Vec();
        temp.x = Math.abs(x);
        temp.y = Math.abs(y);
        temp.z = Math.abs(z);
        return temp;
    }
    
    //fast magnitude
    public final float mag(){
        return x*x + y*y + z*z;
    }
    
    public final float realMagnitude(){
        return (float)Math.sqrt(x*x + y*y + z*z);
    }
    
    public final void divideBy(float w){
        x /= w;
        y /= w;
        z /= w;
    }
    
    /*  Cx = AyBz - AzBy
        Cy = AzBx - AxBz
        Cz = AxBy - AyBx 
     **/
    public final Vec cross(Vec other){
        return cross(other,new Vec());
    }
    
    public final Vec cross(Vec other,Vec temp){
        temp.x = y*other.z - z*other.y;
        temp.y = z*other.x - x*other.z;
        temp.z = x*other.y - y*other.x;
        return temp;
    }
    
    public final float dot(Vec other){
        return x*other.x+y*other.y+z*other.z;
    }
    
    public final Vec scale(float f){
        x*=f;
        y*=f;
        z*=f;
        return this;
    }
    
    public final Vec scaleNew(float f){
        return new Vec(x*f,y*f,z*f);
    }

    public final Vec scaleInto(float f, Vec other){
        other.set(x*f, y*f, z*f);
        return other;
    }
    
    public final Vec normalize(){
        float dis = (float)Math.sqrt(mag());
        if(dis==0){
            dis=1;
        }
        Vec temp = new Vec(x/dis,y/dis,z/dis);

        return temp;
    }
    
    public final Vec lerp(Vec other, float weight){
        Vec temp = new Vec();
        
        temp.x = x*weight+other.x*(1-weight);
        temp.y = y*weight+other.y*(1-weight);
        temp.z = z*weight+other.z*(1-weight);

        return temp;
    }

    public final Vec lerpInPlace(Vec other, float weight){
        x = x*weight+other.x*(1-weight);
        y = y*weight+other.y*(1-weight);
        z = z*weight+other.z*(1-weight);
        return this;
    }
    
    public final Vec normalizeInPlace(){
        
        float dis = (float)Math.sqrt(mag());
        if(dis==0){
            dis=1;
        }
        x/=dis;
        y/=dis;
        z/=dis;
        return this;
    }
    
    public void makePositiveInPlace(){
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
    }
    
    public void minInPlace(Vec other){
        x = Math.min(x,other.x);
        y = Math.min(y,other.y);
        z = Math.min(z,other.z);
    }
    
    public void maxInPlace(Vec other){
        x = Math.max(x,other.x);
        y = Math.max(y,other.y);
        z = Math.max(z,other.z);
    }
    
    public String toString(){
        return "x: "+x+" y: "+y+" z: "+z;
    }

    public Vec mul(Vec other){
        return new Vec(x * other.x, y * other.y, z * other.z);
    }

    public Vec mulInPlace(Vec other){
        x *= other.x;
        y *= other.y;
        z *= other.z;
        return this;
    }
}
