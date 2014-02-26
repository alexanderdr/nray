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
 * RayTracer.java
 *
 * 
 * This is the main Raytracer object that generates rays and sends them into the
 * scene.
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import nray.*;

import java.awt.*;
import java.awt.image.*;
public class RayTracer implements Cloneable {
    int width, height;
    RayScene s;
    
    BufferedImage bi;
    Graphics g;

    Camera camera;

    RayLight rlight;

    public RayTracer(RayScene s,Camera c,int width,int height){
        this.s = s;
        this.camera = c;
        this.width = width;
        this.height = height;

        rlight = new RayLight(s,10000,new Vec(0f,5f,-12f));
        rlight.castPhotons();
        
        bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        g = bi.getGraphics();
    }
    
    public void setSize(int x,int y){
        width = x;
        height = y;
    }

    public void render(){
        this.render(bi, g, 0, 0, width, height, 1);
    }

    public void render(BufferedImage bi, Graphics g, int startx, int starty, int endx, int endy, float ratio){
        Ray tempRay = new Ray(new Vec(),new Vec());
        Vec scratch = new Vec();
        Intersector it = new Intersector();

        Vec normal = new Vec();
        Vec right = new Vec();
        Vec up;
        
        float halfPI = ((float)Math.PI/2);
        
        normal.x = (float)(Math.sin(camera.getRotYRad())*Math.cos(camera.getRotXRad()));
        normal.y = (float)(Math.sin(camera.getRotXRad()));
        normal.z = (float)(Math.cos(camera.getRotYRad())*Math.cos(camera.getRotXRad()));
        
        right.x = (float)(Math.sin((camera.getRotYRad()-halfPI)));
        right.z = (float)(Math.cos((camera.getRotYRad()-halfPI)));
        
        up = right.cross(normal);
        
        right.scale(1.414f * ratio);
        up.scale(1.414f);
        
        Vec ul = new Vec();
        ul.addInPlace(normal);
        ul.addInPlace(right.scaleNew(-.5f));
        ul.addInPlace(up.scaleNew(.5f));
        
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, (endx-startx), (endy - starty));
        raycount = 0;
        hits = 0;
        Vec prev = new Vec(), prevprev = new Vec();

        Triangle lastTri = null;
        Box lastBox = null;
        missTime = 0;

        for(int y = starty;y<endy;y++){

            float yratio = ((float)y)/(height-1);
            
            for(int x = startx;x<endx;x++){

                float xratio = ((float)x)/(width-1);
                float xm = right.x*xratio-up.x*yratio+ul.x;
                float ym = right.y*xratio-up.y*yratio+ul.y;
                float zm = right.z*xratio-up.z*yratio+ul.z;
                scratch.x = xm;
                scratch.y = ym;
                scratch.z = zm;
                scratch.normalizeInPlace();

                RTObject lastObject = tempRay.object;
                int boxIndex = tempRay.boxIndex;
                BinaryBox guessBox = tempRay.hitBox;

                lastTri = tempRay.Itri;
                lastBox = tempRay.Ibox;

                tempRay.init(scratch,camera.getPos());
                //interestingly new Ray() appears to be the same or slightly faster than .init()
                //even though it increases GC activity 8-fold (from .1% to .8%)
                //tempRay = new Ray(scratch, camera.getPos());

                long dTime = 0;
                if(guessBox != null && Options.reverseCast ){ //either boxIndex > 1 or guessBox != null
                    //rCast++;

                    //pre-loading the last triangle currently provides no real benefits
                    //if(lastTri!=null) {
                        //it.cast(lastTri,tempRay);
                        //tempRay.castAny = true;
                    //}
                    long startTime = 0;

                    if(Options.colorTime)  startTime = System.nanoTime();
                    s.reverseCast(tempRay, it, guessBox, lastObject);
                    if(Options.colorTime) dTime = System.nanoTime()-startTime;

                    long dTime2 = 0;
                    
                    if(tempRay.intersection!=null){
                        //Not sure if we care about this...
                        //if(tempRay.Itri==lastTri){
                        //    tempRay.boxIndex = boxIndex;
                        //}

                        /*if(tempRay.hitBackfacing){
                            tempRay.color.set(GREEN);
                        }*/
                        
                    } else {
                        if(nray.Options.bvhcolor){
                            tempRay.color.set(tempRay.boxColor);
                        }
                    }
                } else {
                    long startTime = 0;

                    if(Options.colorTime) startTime = System.nanoTime();
                    cast(tempRay, it);
                    if(Options.colorTime) dTime = System.nanoTime()-startTime;
                }

                if(!Options.colorTime && tempRay.intersection != null) {
                    colorRay(tempRay, it);
                } else if(Options.colorTime){
                    tempRay.color.x = Math.min(1.0f, ((float)dTime)/2000f);
                    tempRay.color.y = Math.min(1.0f, ((float)dTime)/25000f);
                    tempRay.color.z = Math.min(1.0f, ((float)dTime)/250000f);
                }

                //debugging for black dots in thin surfaces
                /*if((!prevprev.equals(Vec.ZERO))&&(prev.equals(Vec.ZERO))&&(!tempRay.color.equals(Vec.ZERO))){
                    int t = x;
                    int s = y;
                    if(s>t){
                        int temp = s;
                        s = t;
                        t = temp;
                    }
                    System.out.println("ohno "+(x-1)+" "+(y)+" "+((float)x+y-1)/199);
                    xratio = ((float)x-1)/(width-1);
                    xm = right.x*xratio-up.x*yratio+ul.x;
                    ym = right.y*xratio-up.y*yratio+ul.y;
                    zm = right.z*xratio-up.z*yratio+ul.z;
                    System.out.println(xm/ym+" "+xm+" "+ym+" "+zm);
                    scratch.x = xm;
                    scratch.y = ym;
                    scratch.z = zm;
                    scratch.normalizeInPlace();
                    //Vec result = castColorRay(scratch,Vec.ZERO);
                    //System.out.println(scratch.mag());
                    //tempRay.init(scratch,pos);

                    //cast(tempRay);
                }
                prevprev = new Vec(prev);
                prev = new Vec(tempRay.color);*/

                //on/off coloring for hit v. no hit.
                /*if(tempRay.Itri != null){
                    bi.setRGB(x - startx, y - starty, 0xFF0000FF);
                } else {
                    bi.setRGB(x - startx, y - starty, 0xFF000000);
                }*/
                //setting this or not setting this has no meaningful performance impact
                bi.setRGB(x - startx, y - starty, 0xFF<<24|((int)(tempRay.color.x*255))<<16|((int)(tempRay.color.y*255))<<8|((int)(tempRay.color.z*255)));
            }
        }

    }
    
    public Image getImage(){
        return bi;
    }
    
    //this produces a flat scene with either red or black pixels
    /*public Vec castColorRay(Vec ray,Vec origin){
        if(s.castRay(ray, origin).hit){
            return RED;
        } else {
            System.out.println();
        }
        return Vec.ZERO;
    }*/

    //would be used for transparency, currently unused
    public static Vec refract(Vec I,Vec N,float eta){
        Vec fracDir = new Vec();
        float theta = (float)Math.acos(I.dot(N)/(I.realMagnitude()*N.realMagnitude())); //(ray.normal)/(|ray|*|normal|)

        theta = (float)Math.sin(theta);

        theta = theta/eta;
        
        if(theta>1f||theta<-1f){ //total internal refraction
            return fracDir;
        }
        
        float theta2 = (float)Math.asin(theta);

        float f = N.dot(I.scaleNew(1f));
        Vec norm = N.scaleNew(f);
        Vec K = I.sub(norm);

        K.scale((float)Math.tan(theta2));
        
        //K.scale(theta2);
        //K.y = -1.414f;
        fracDir = norm.add(K).normalizeInPlace();
        
        return fracDir;
    }

    public static Vec calcNormal(Ray r){
        float w = 1-(r.u+r.v);
            
        Vec normal = r.Itri.n1.scaleNew(w);
        normal.addInPlace(r.Itri.n2.scaleNew(r.u));
        normal.addInPlace(r.Itri.n3.scaleNew(r.v));
        
        return normal;
    }

    //This version has no meaningful performance impact compared to the above
    public static Vec calcNormal(Ray r, Vec dest){
        float w = 1-(r.u+r.v);

        r.Itri.n1.scaleThenAddTo(w, dest);
        r.Itri.n2.scaleThenAddTo(r.u, dest);
        r.Itri.n3.scaleThenAddTo(r.v, dest);

        return dest;
    }

    Vec light = new Vec(0f,5f,-12f);

    //Rotate the light around the origin
    public void rotateLight(float theta){
        float x = light.x;
        float z = light.z;
        float nx = x * (float)Math.cos(theta) + z * (float)-Math.sin(theta);
        float nz = x * (float)Math.sin(theta) + z * (float)Math.cos(theta);
        light.x = nx;
        light.z = nz;
    }

    public void setLightToCamera(){
        light.set(camera.getPos());
    }

    static final Vec RED = new Vec(1f,0f,0f);
    static final Vec GREEN = new Vec(0f,1f,0f);
    static final Vec BLUE = new Vec(0f,0f,1f);

    Vec color = new Vec(.65f,.65f,.65f);
    Vec ambient = new Vec(.3f,.2f,.2f);
    Vec slight = new Vec(0,0,-.000001f);
    //Produces a shaded scene by re-casting intersected rays, is actually per-pixel
    //lightning
    int raycount = 0;
    int hits = 0;
    long missTime = 0;
    public void cast(Ray r, Intersector it){

        s.cast(r, it);
        if(r.intersection==null) {
            if(nray.Options.bvhcolor){
                r.color.set(r.boxColor);
            }
            return;
        }

    }

    public void colorRay(Ray r, Intersector it){

        Vec normal = calcNormal(r);

        if(nray.Options.colorphits){

            Vec dist = new Vec(1f,1f,1f);

            dist.subInPlace(normal); //this can produce bad behavior, creating with negative values with input (.3, .3, .3);
            dist.normalizeInPlace();
            dist.scale(.2f);

            int phits = rlight.gatherPhotons(r.intersection, dist);

            Vec tempColor;
            tempColor = RED.lerp(Vec.ZERO, Math.min(phits/10f,1f));

            if(phits>100){
                tempColor = BLUE.lerp(tempColor, Math.min((phits-100)/150f,1f));
                if(phits>250){
                    tempColor = GREEN.lerp(tempColor, Math.min((phits-250)/150f,1f));
                }
            }

            r.color.set(tempColor);
            return;
        }

        //r.color.addInPlace(ambient);

        if(nray.Options.normal){
            r.color = normal;
            r.color.x = Math.abs(r.color.x);
            r.color.y = Math.abs(r.color.y);
            r.color.z = (float)Math.pow(Math.abs(r.color.z),4);
            /*if(normal.mag()>1){
                System.out.println("warning, normal > 1:"+normal.mag());
            }*/
            return;
        }
        if(nray.Options.UV){
            r.color.x = 0;
            r.color.y = r.u;
            r.color.z = r.v;
            //System.out.println(normal);
            return;
        }

        if(Options.photonLighting){
            Vec dist = new Vec(1f,1f,1f);

            //Manipulating the subInPlace can allow some sub-surface scattering effects in conjunction with scale (to sample through thickness)
            dist.subInPlace(normal); //this can produce bad behavior with negative values with (.3, .3, .3);
            dist.normalizeInPlace();
            dist.scale(.25f);

            int phits = rlight.gatherPhotons(r.intersection, dist);
            r.color.addInPlace(.2f + Math.min(.8f, phits / 150f), .2f+ Math.min(.8f, phits / 150f), .2f+ Math.min(.8f, phits / 150f));
        } else {
            Vec dir = light.sub(r.intersection).normalize();

            //this code allows for self-shadowing (by casting shadow rays)
            //Vec nDelta = normal.scaleNew(.0001f); //a small amount above the surface we hit...
            //Ray lightRay = new Ray(dir,r.intersection.add(nDelta));
            //Vec H = (dir.add(r.ray)).scale(.5f);
            //s.cast(lightRay);
            //if(lightRay.intersection!=null){
                //This is a second way of allowing self-shadowing.  If we cast the lightRay above and conditionally do phong
            //} else {

                //Phong lighting ignorant of the rest of the scene.
                //dscale: diffuse, sscale: specular
                float dscale = normal.dot(dir);
                //float sscale = dir.dot(r.ray); //specular, not used

                //sscale *= .5f;

                if(dscale<0){
                    dscale = Math.max(dscale,0f);
                    r.color.addInPlace(0,dscale,0);
                } else {
                    //sscale = Math.max(sscale,0f);
                    //sscale = (float)Math.pow(sscale,7f);
                    r.color.addInPlace(color.x*dscale,color.y*dscale,color.z*dscale);
                }
                r.color.addInPlace(.2f,.2f,.2f);
                //r.color.addInPlace(color.x*sscale,color.y*sscale,color.z*sscale);
            //}
        }

        if(r.bounces>=1&& nray.Options.reflection){
            float f = normal.dot(r.ray);
            Vec temp = normal.scaleNew(f);
            Vec K = r.ray.sub(temp);
            K.scale(2f);
            Vec o = r.ray.scaleNew(-1f).add(K);

            Vec nDelta = normal.scaleNew(.00001f); //a small amount above the surface we hit...

            o.normalizeInPlace();
            //System.out.println(f+" "+temp+" "+K+" "+o);
            Vec refDir = o;

            Ray reflectionRay = new Ray(refDir,r.intersection.add(nDelta));
            reflectionRay.bounces = r.bounces - 1;
            cast(reflectionRay, it);
            //r.color.addInPlace(reflectionRay.color.scaleNew(.5f));
            //this alters reflection so if we hit no other objects there's no apparent reflection (instead of reflecting black)
            /*if(reflectionRay.color.equals(Vec.ZERO)){

            } else {
                r.color.addInPlace(reflectionRay.color);
                return;
            }*/
            r.color.lerpInPlace(reflectionRay.color, .65f);
        }

        if(nray.Options.bvhcolor){
            r.color.set(r.boxColor);
        }
        

        if(nray.Options.colorhits){
            Vec tempColor;
            
            tempColor = RED.lerp(Vec.ZERO, Math.min(r.boxHits/20f,1f));
            
            if(r.boxHits>100){
                tempColor = BLUE.lerp(tempColor, Math.min((r.boxHits-100)/100f,1f));
                
            }
            
            r.color.set(tempColor);
        }
        
        if(nray.Options.colorthits){
            /*r.color.x = 0f;
            r.color.y = 0f;
            r.color.z = Math.min(.02f*r.boxHits,1.0f);*/
            Vec tempColor;
            
            tempColor = RED.lerp(Vec.ZERO, Math.min(r.triHits/20f,1f));
            
            if(r.triHits>100){
                tempColor = BLUE.lerp(tempColor, Math.min((r.triHits-100)/150f,1f));
                if(r.triHits>250){
                    tempColor = GREEN.lerp(tempColor, Math.min((r.triHits-250)/150f,1f));
                }
                
            }
            
            r.color.set(tempColor);
        }

        //For testing when we somehow intersect with an object which is pure black (which shouldn't generally happen)
        /*if(r.color.x==0&&r.color.y==0&&r.color.z==0){
            //System.out.println("...");
            if(Options.colorBlack){
                r.color.set(GREEN);
            }
        }*/
        
        hits += r.boxHits;
        raycount++;
    }

    public RayTracer clone(){
        RayTracer copy = null;
        try{
            copy = (RayTracer)super.clone();
        } catch (CloneNotSupportedException cnse){
            cnse.printStackTrace();
        }
        copy.s = s.clone();

        return copy;
    }
    
}



