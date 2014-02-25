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

    //list of photon hits
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

        int halfPhotonsAtEquator = 600;

        //This should project each RTObject's outer AABB on to a sphere, and only cast photon in directions where the sphere is active
        for(float phi = 0; phi < Math.PI * 2; phi += Math.PI / halfPhotonsAtEquator){
            float thetaStep = (float)(Math.PI / halfPhotonsAtEquator);//(float)(Math.PI / (halfPhotonsAtEquator * Math.max(.01f, Math.sin(phi)) ) );
            for(float theta = 0; theta < Math.PI * 2; theta += thetaStep ){

                Vec dir = new Vec(Math.cos(theta)*Math.sin(phi),Math.cos(phi),Math.sin(theta)*Math.sin(phi));

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

                break;
            case 1:
                Arrays.sort(hits,new Ycomp());

                break;
            case 2:
                Arrays.sort(hits,new Zcomp());

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
            depth++;
        }
        for(int i = pheap.size();i<index;i++){
            pheap.add(null);
        }
        pheap.add(index,pos);*/
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
            for(Vec v:bheap){
                if(v==null) continue;
                if(pos.sub(v).mag()<distance.mag()){
                    hits++;
                }
            }
            return hits;
        } else {
            //System.out.println("hm");
            int ghits =  gather(pos,distance.realMagnitude(),1,0,0);

            /*int hits = 0;
            for(Vec v:bheap){
                if(v==null) continue;
                if(pos.sub(v).mag()<distance.mag()){
                    hits++;
                }
            }
            if(ghits != hits){
                System.out.println("We have a problem");
                gather(pos, distance, 1, 0, 0);
            }*/
            return ghits;
        }
    }
    

    
    private int gather(Vec pos,float distance,int index,int depth,int failures){
        
        if(index>=bheap.size()) return 0;
        //if(failures>4) return 0;

        Vec v = bheap.get(index);
        if(v==null) {
            if(index*2<bheap.size()){
                System.out.println("This should not be");
            }
            return 0;
        }


        int hit = 0;
        int fail = 0;
        //if((Math.abs(v.x-pos.x)<distance.x)&&(Math.abs(v.y-pos.y)<distance.y)&&(Math.abs(v.z-pos.z)<distance.z)){
        if(pos.sub(v).realMagnitude() < distance){
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

                if(pos.x-distance<=v.x){
                    hit+=gather(pos,distance,leftChild(index),depth+1,failures+fail);
                }
                if(pos.x+distance>=v.x){
                    hit+=gather(pos,distance,rightChild(index),depth+1,failures+fail);
                }
                //if(pos.x+Math.pow(distance.x,2)>=v.x){
                //    hit+=gather(pos,distance,rightChild(index),depth+1,failures+fail);
                //}
                break;
            case 1:

                if(pos.y-distance<=v.y){
                    hit+=gather(pos,distance,leftChild(index),depth+1,failures+fail);
                }
                if(pos.y+distance>=v.y){
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
                if(pos.z-distance<=v.z){
                    hit+=gather(pos,distance,leftChild(index),depth+1,failures+fail);
                }
                if(pos.z+distance>=v.z){
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


        return hit;
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