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
 * RayLight.java
 *
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import nray.Vec;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.*;
public class RayLight {
    RayScene rs;
    Vec pos;
    int numPhotons;
    
    ArrayList<Vec> pheap = new ArrayList<Vec>();
    
    public RayLight(RayScene rs,int numPhotons,Vec pos){
        this.numPhotons = numPhotons;
        this.rs = rs;
        this.pos = pos;
    }
    
    public void castPhotons(){
        
        pheap = new ArrayList<Vec>();
        bheap = new ArrayList<Vec>();
        
        ArrayList<RTObject> targets = rs.objects;
        
        Vec range = new Vec();

        Intersector it = new Intersector();

        /*for(RTObject target: targets){
        
            //RTObject target = targets.get((int)(0f));//Math.random()*targets.size()));
            Vec dir = target.pos.sub(pos);

            Box box = target.bvh.aabbs.get(1);
            range.x = box.max.x - box.min.x;
            range.y = box.max.y - box.min.y;
            range.z = box.max.z - box.min.z;

            Vec photon = new Vec();//new Vec(box.min.sub(pos));
            Vec minDist = box.min.sub(pos);

            int xlimit, ylimit, zlimit;
            xlimit = ylimit = zlimit = 15;



            for(int i = 0;i<xlimit;i++){
                for(int j = 0;j<ylimit;j++){
                    for(int k = 0;k<zlimit;k++){
                        photon.set(minDist.x+range.x*(((float)i)/xlimit),
                                minDist.y+range.y*(((float)j)/ylimit),
                                minDist.z+range.z*(((float)k)/zlimit));
                        Vec p = photon.normalize();
                        //System.out.println(photon);
                        Ray r = new Ray(p,pos);

                        rs.cast(r, it);

                        if(r.intersection!=null){
                            Ray tray = r;
                            int bounces = 3;
                            //System.out.println(tray.object +" "+rs.objects.get(2));
                            Ray lastGood = r;

                            //photon retransmission
                            while(tray.object.equals(rs.objects.get(0))){

                                //System.out.println("retransmit");

                                Vec fracDir = RayTracer.refract(tray.ray, RayTracer.calcNormal(tray), 1.5f);

                                Ray refractionRay = new Ray(fracDir,tray.intersection.add(fracDir.scaleNew(.0001f)));

                                rs.cast(refractionRay);

                                if(refractionRay.intersection!=null){
                                    if(tray.Itri.equals(refractionRay.Itri)){
                                        System.out.println("sigh...");
                                    }
                                    tray = refractionRay;
                                    if(!tray.hitBackfacing){
                                        lastGood = tray;
                                    }
                                } else {
                                    if(tray.equals(r)){
                                        System.out.println("missed");
                                        lastGood = null;
                                    }
                                    break;
                                }
                            }

                            if(lastGood==null) continue;

                            if(tray.hitBackfacing){
                                //System.out.println("doh");
                                continue;
                            }

                            addPhoton(lastGood.intersection);
                        }

                    }
                }
            }
        }*/
            
        /*for(int i = 0;i<numPhotons;i++){
            
            RTObject target = targets.get((int)(Math.random()*targets.size()));
            Vec dir = target.pos.sub(pos);
            Box box = target.bvh.aabbs.get(1);
            
            float xdiff = box.min.x - box.max.x;
            float ydiff = box.min.y - box.max.y;
            float zdiff = box.min.z - box.max.z;
            
            dir.addInPlace(new Vec(Math.random()*xdiff-(xdiff/2),Math.random()*ydiff-(ydiff/2),Math.random()*zdiff-(zdiff/2)));
            //dir.addInPlace(new Vec(Math.random()*2-1,Math.random()*2-1,Math.random()*2-1));
            dir.normalizeInPlace();
            Ray r = new Ray(dir, pos);
            rs.cast(r);
            
            if(r.intersection!=null){
                addPhoton(r.intersection);
            }
        }*/

        for(float phi = 0; phi < Math.PI * 2; phi += Math.PI / 400){
            for(float theta = 0; theta < Math.PI * 2; theta += Math.PI / 400){

                Vec dir = new Vec(Math.cos(theta)*Math.sin(phi),Math.cos(phi),Math.sin(theta)*Math.sin(phi));//target.pos.sub(pos);
                if(dir.mag() > 1.0001 || dir.mag() < .9999){
                    System.out.println("I have work to do");
                }
                //dir.addInPlace(new Vec(Math.random()*xdiff-(xdiff/2),Math.random()*ydiff-(ydiff/2),Math.random()*zdiff-(zdiff/2)));
                //dir.addInPlace(new Vec(Math.random()*2-1,Math.random()*2-1,Math.random()*2-1));
                //dir.normalizeInPlace();
                Ray r = new Ray(dir, pos);
                rs.cast(r, it);

                if(r.intersection!=null){
                    addPhoton(r.intersection);
                }
            }
        }
        
        //System.out.println("Stored "+numPhotons+" photons in "+pheap.size()+" size heap "+hitPhotons+" hits ");
        
        buildTree();
        pheap = bheap;
        
        System.out.println("Heap has "+hitPhotons+" photons in "+pheap.size());
        /*
        int missed = 0;
        for(int i = 0;i<pheap.size();i++){
            Vec v = pheap.get(i);
            if(v==null){
                System.out.println(i+": null");
                missed++;
            }
        }*/
        
        //System.out.println(missed);
        //printTree();
    }
    
    
    
    ArrayList<Vec> bheap = new ArrayList<Vec>();
    public void buildTree(){
        Vec[] hits = new Vec[pheap.size()];
        hits = pheap.toArray(hits);
        buildTree(hits,1,0);
        
    }
    
    public void buildTree(Vec[] hits,int index,int depth){
        
        if(hits.length==1){
            for(int i = bheap.size();i<index;i++){
                bheap.add(null);
            }
            if(bheap.size()>index){
                bheap.set(index, hits[0]);
            } else {
                bheap.add(index,hits[0]);
            }
            return;
        } else if(hits.length==0){
            return;
        }
        
        float last;
        switch(depth%3){
            case 0:
                Arrays.sort(hits,new Xcomp());
                /*last = hits[0].x;
                for(int i = 1;i<hits.length;i++){
                    if(last>hits[i].x){
                        System.out.println("... troubledoubleX");
                    }
                    last = hits[i].x;
                }*/
                break;
            case 1:
                Arrays.sort(hits,new Ycomp());
                /*last = hits[0].y;
                for(int i = 1;i<hits.length;i++){
                    if(last>hits[i].y){
                        System.out.println("... troubledoubleY");
                    }
                    last = hits[i].y;
                }*/
                break;
            case 2:
                Arrays.sort(hits,new Zcomp());
                /*last = hits[0].z;
                for(int i = 1;i<hits.length;i++){
                    if(last>hits[i].z){
                        System.out.println("... troubledoubleZ");
                    }
                    last = hits[i].z;
                }*/
                break;
            default:
                System.out.println("Bad..");
        }
        
        float median;
        if(hits.length==0){
            System.out.println("huh...");
        }
        
        if(hits.length%2==0){
            median = (hits[hits.length/2].x+hits[(hits.length/2)-1].x)/2;
        } else {
            median = hits[hits.length/2].x;
        }
        
        for(int i = bheap.size();i<index;i++){
            bheap.add(null);
        }
        
        if(bheap.size()>index){
            bheap.set(index,hits[hits.length/2]);
        } else {
            bheap.add(index,hits[hits.length/2]);
        }
        
        Vec[] left = new Vec[(hits.length)/2];
        Vec[] right = new Vec[(hits.length-1)-(hits.length)/2];
        
        System.arraycopy(hits, 0, left, 0, (hits.length)/2);
        System.arraycopy(hits, (hits.length/2)+1, right, 0, (hits.length-1)-(hits.length)/2);
        
        buildTree(left,index*2,depth+1);
        buildTree(right,index*2+1,depth+1);
    }
    
    public void printTree(){
        rprintTree(1,0,5);
    }
    
    public void rprintTree(int index,int level, int recurse){
        
        if(recurse<=0) return;
        
        for(int i = 0;i<level;i++) System.out.print("  ");
        
        if(pheap.get(index)==null){ 
            System.out.println("null");
            return;
        }
        
        System.out.println(pheap.get(index));
        rprintTree(leftChild(index),level+1,recurse-1);
        rprintTree(rightChild(index),level+1,recurse-1);
    }
    
    int hitPhotons = 0;
    public void addPhoton(Vec pos){
        hitPhotons++;
        int index = 1;
        int depth = 0;
        //System.out.println(pos);
        /*while(index<pheap.size()){
            Vec v = pheap.get(index);
            if(v==null) break;
            switch(depth%3){
                case 0:
                    if(pos.x<v.x){
                        index = leftChild(index);
                    } else {
                        index = rightChild(index);
                    }
                    break;
                case 1:
                    if(pos.y<v.y){
                        index = leftChild(index);
                    } else {
                        index = rightChild(index);
                    }
                    break;
                case 2:
                    if(pos.z<v.z){
                        index = leftChild(index);
                    } else {
                        index = rightChild(index);
                    }
                    break;
            }
            
        }
        for(int i = pheap.size();i<index;i++){
            pheap.add(null);
        }*/
        //pheap.add(index,pos);
        pheap.add(pos);
    }
    
    public int leftChild(int index){
        return index*2;
    }
    public int rightChild(int index){
        return index*2+1;
    }
    
    public int gatherPhotons(Vec pos,Vec distance){
        if(nray.Options.hardGather){
            //System.out.println("Doh");
            int hits = 0;
            for(Vec v:pheap){
                if(v==null) continue;
                if(pos.sub(v).mag()<distance.mag()){
                    hits++;
                }
            }
            return hits;
        } else {
            //System.out.println("hm");
            return gather(pos,distance,1,0,0);
        }
    }
    

    
    private int gather(Vec pos,Vec distance,int index,int depth,int failures){
        
        if(index>=pheap.size()) return 0;
        //if(failures>4) return 0;
        
        Vec v = pheap.get(index);
        if(v==null) {
            if(index*2<pheap.size()){
                System.out.println("This should not be");
            }
            return 0;
        }


        int hit = 0;
        int fail = 0;
        //if((Math.abs(v.x-pos.x)<distance.x)&&(Math.abs(v.y-pos.y)<distance.y)&&(Math.abs(v.z-pos.z)<distance.z)){
        if(pos.sub(v).mag()<distance.mag()){
            hit++;
        } else {
            fail++;
        }
        
        /*
        index = leftChild(index);
        //} else {
        hit+=gather(pos,distance,index,depth+1,failures+fail)+hit;
        index = rightChild(index);
        hit+=gather(pos,distance,index,depth+1,failures+fail)+hit;*/

        
        switch(depth%3){
            case 0:
                if(pos.x-distance.x*2<=v.x){
                    hit+=gather(pos,distance,leftChild(index),depth+1,failures+fail);
                }
                if(pos.x+distance.x*2>=v.x){
                    hit+=gather(pos,distance,rightChild(index),depth+1,failures+fail);
                }
                //if(pos.x+Math.pow(distance.x,2)>=v.x){
                //    hit+=gather(pos,distance,rightChild(index),depth+1,failures+fail);
                //}
                break;
            case 1:
                
                if(pos.y-distance.y*2<=v.y){
                    hit+=gather(pos,distance,leftChild(index),depth+1,failures+fail);
                }
                if(pos.y+distance.y*2>=v.y){
                    hit+=gather(pos,distance,rightChild(index),depth+1,failures+fail);
                }
                
                //if(pos.y-Math.pow(distance.y,2)<=v.y){
                //    hit+=gather(pos,distance,leftChild(index),depth+1,failures+fail);
                //}
                //if(pos.y+Math.pow(distance.y,2)>=v.y){
                //    hit+=gather(pos,distance,rightChild(index),depth+1,failures+fail);
                //}

                break;
            case 2:
                if(pos.z-distance.z*2<=v.z){
                    hit+=gather(pos,distance,leftChild(index),depth+1,failures+fail);
                }
                if(pos.z+distance.z*2>=v.z){
                    hit+=gather(pos,distance,rightChild(index),depth+1,failures+fail);
                }
                //if(pos.z-Math.pow(distance.z,2)<=v.z){
                //    hit+=gather(pos,distance,leftChild(index),depth+1,failures+fail);
                //}
                //if(pos.z+Math.pow(distance.z,2)>=v.z){
                //    hit+=gather(pos,distance,rightChild(index),depth+1,failures+fail);
                //}

                break;
        }
        
        
        
        return hit;//gather(pos,distance,index,depth+1,failures+fail)+hit;
    }
    
}

class Xcomp implements Comparator<Vec>{
    public int compare(Vec one,Vec two){
        if(one.x<two.x){
            return -1;
        } else if(one.x==two.x){
            return 0;
        } else {
            return 1;
        }
    }
}

class Ycomp implements Comparator<Vec>{
    public int compare(Vec one,Vec two){
        if(one.y<two.y){
            return -1;
        } else if(one.y==two.y){
            return 0;
        } else {
            return 1;
        }
    }
}

class Zcomp implements Comparator<Vec>{
    public int compare(Vec one,Vec two){
        if(one.z<two.z){
            return -1;
        } else if(one.z==two.z){
            return 0;
        } else {
            return 1;
        }
    }
}