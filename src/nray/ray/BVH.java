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
 * Octree.java
 *
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import java.util.*;
import nray.Vec;
public class BVH {
    int targetRes;
    int boxCount = 0;
    ArrayList<Triangle> triangles;
    ArrayList<Box> aabbs = new ArrayList<Box>();
    ArrayList<ArrayList<Triangle>> boxTris = new ArrayList<ArrayList<Triangle>>();
    ArrayList<ArrayList<SmallTriangle>> boxTrisSmall = new ArrayList<ArrayList<SmallTriangle>>();

    BinaryBox root;

    public BVH(ArrayList<Triangle> tris,int resolution){
        targetRes = resolution;
        this.triangles = tris;
        generate();
    }
    int totalTris = 0;
    public void generate(){
        Vec tmin = new Vec(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
        Vec tmax = new Vec(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
        Vec temp = new Vec(tmax);
        for(Triangle t: triangles){
            
            RTObject.updateExtremes(t,tmin,tmax);
            //temp = new Vec(tmax);
        }
        //aabbs.add(0,new Box(tmin,tmax));
        aabbs.add(0,null);
        aabbs.add(1,new Box(tmin,tmax));
        //boxTris.add(0,triangles);
        boxTris.add(0,null);
        boxTris.add(1,triangles);
        
        split2(1);
        int i = 2;
        int max = 3;
        //while(aabbs.get(i/2).hasChildren){
        while(i<=max){
            if(aabbs.get(i)==null){
                i++;
                aabbs.add(null);
                boxTris.add(null);
                aabbs.add(null);
                boxTris.add(null);
                continue;
            }
            boolean didSplit = split2(i);
            if(didSplit){
                boxCount+=2;
                max = i*2+1;
            } else {
                if(i<max){
                    aabbs.add(null);
                    boxTris.add(null);
                    aabbs.add(null);
                    boxTris.add(null);
                }
            }
            i++;
            //split!
        }

        System.out.println(aabbs.size()+" boxes... more or less "+boxCount+" boxes, "+totalTris+" triangles");

        //Clone our lists of larger triangles with smaller triangles
        for(ArrayList<Triangle> alt: boxTris){
            if(alt != null){
                ArrayList<SmallTriangle> smalltris = new ArrayList<SmallTriangle>();
                for(Triangle t: alt){
                    smalltris.add(new SmallTriangle(t));
                }
                boxTrisSmall.add(smalltris);
            } else {
                boxTrisSmall.add(null);
            }
        }

        root = new BinaryBox(aabbs.get(1));
        buildBinaryBoxTree(1, root);

        /*System.out.println(i);
        for(int j = 1;j<aabbs.size();j++){
            System.out.println("Box index: "+j);
            System.out.println("    "+aabbs.get(j).min.x+" "+aabbs.get(j).min.y+" "+aabbs.get(j).min.z+" ");
            System.out.println("    "+aabbs.get(j).max.x+" "+aabbs.get(j).max.y+" "+aabbs.get(j).max.z+" ");
        }*/
    }

    public void buildBinaryBoxTree(int index, BinaryBox cur){
        Box b = aabbs.get(index);
        if(b == null) return;

        ArrayList<SmallTriangle> tris = boxTrisSmall.get(index);
        if(tris != null && tris.size() > 0){
            cur.triangles = tris;
        }

        if(b.hasChildren){
            cur.addLeftChild(aabbs.get(leftChild(index)));
            cur.addRightChild(aabbs.get(rightChild(index)));
            if(cur.left != null){
                buildBinaryBoxTree(leftChild(index), cur.left);
            }
            if(cur.right != null){
                buildBinaryBoxTree(rightChild(index), cur.right);
            }
        }
    }

    final float growthLimit = 1.20f;
    
    public boolean split2(int aabbIndex){
        Box b = aabbs.get(aabbIndex);
        ArrayList<Triangle> tris = boxTris.get(aabbIndex);
        
        if(tris.size()<targetRes){
            totalTris += boxTris.get(aabbIndex).size();
            return false;
        }

            float[] points = new float[tris.size()];
            ArrayList<Triangle> greaterX = new ArrayList<Triangle>();
            ArrayList<Triangle> lessX = new ArrayList<Triangle>();
            ArrayList<Triangle> greaterY = new ArrayList<Triangle>();
            ArrayList<Triangle> lessY = new ArrayList<Triangle>();
            ArrayList<Triangle> greaterZ = new ArrayList<Triangle>();
            ArrayList<Triangle> lessZ = new ArrayList<Triangle>();
            
                //Begin X checks
                for(int i = 0;i<tris.size();i++){
                    points[i] = tris.get(i).mid.x;
                }
                Arrays.sort(points);
                float median;
                if(points.length%2==0){
                    median = (points[points.length/2]+points[points.length/2-1])/2;
                } else {
                    median = points[points.length/2];
                }
                for(Triangle t:tris){
                    if(t.max.x>=median){
                        greaterX.add(t);
                    }else if(t.min.x<median){
                        lessX.add(t);
                    }
                }

                Vec tmin = new Vec(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
                Vec tmax = new Vec(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
                for(Triangle t: greaterX){
                    RTObject.updateExtremes(t,tmin,tmax);
                }
                //if(greaterX.size()==0||lessX.size()==0) {
                //    System.out.println("lulwut? greater");
                //}
                tmax.x = Math.min(tmax.x,b.ax);
                tmax.y = Math.min(tmax.y,b.ay);
                tmax.z = Math.min(tmax.z,b.az);
                //tmin.x = median;
                //tmin.x = Math.max(tmin.x,median);
                tmin.y = Math.max(tmin.y,b.iy);
                tmin.z = Math.max(tmin.z,b.iz);
                
                if(tmin.x>tmax.x){
                    float temp = tmin.x;
                    tmin.x = tmax.x;
                    tmax.x = temp;
                }
                
                Box maxBoxX = new Box(tmin,tmax);
                
                tmin = new Vec(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
                tmax = new Vec(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
                for(Triangle t: lessX){
                    RTObject.updateExtremes(t,tmin,tmax);
                }
                //if(lessX.size()==0) System.out.println("lulwut? lesser");
                
                //tmax.x = median;
                //tmax.x = Math.min(tmax.x,median);
                tmax.y = Math.min(tmax.y,b.ay);
                tmax.z = Math.min(tmax.z,b.az);
                
                tmin.x = Math.max(tmin.x,b.ix);
                tmin.y = Math.max(tmin.y,b.iy);
                tmin.z = Math.max(tmin.z,b.iz);
                
                if(tmin.x>tmax.x){
                    float temp = tmin.x;
                    tmin.x = tmax.x;
                    tmax.x = temp;
                }

                Box minBoxX = new Box(tmin,tmax);


                //This is not really much of a problem because we aren't duplicating triangles
                //if(maxBoxX.min.x < minBoxX.max.x){

                //    System.out.println("We have a box overlap problem on X... "+
                //            maxBoxX.min.x +" "+ minBoxX.max.x+" "+lessX.size());

                    //minBoxX.max.x = maxBoxX.min.x;
                //}
                
                //Begin Y checks
                for(int i = 0;i<tris.size();i++){
                    points[i] = tris.get(i).mid.y;
                }
                Arrays.sort(points);
                if(points.length%2==0){
                    median = (points[points.length/2]+points[points.length/2-1])/2;
                } else {
                    median = points[points.length/2];
                }
                //System.out.println("median:"+median);
                for(Triangle t:tris){
                    if(t.max.y>=median){
                        greaterY.add(t);
                    }else if(t.min.y<median){
                        lessY.add(t);
                    }
                }

                tmin = new Vec(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
                tmax = new Vec(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
                for(Triangle t: greaterY){
                    RTObject.updateExtremes(t,tmin,tmax);
                }
                //tmin.y = median;
                
                tmax.x = Math.min(tmax.x,b.ax);
                tmax.y = Math.min(tmax.y,b.ay);
                tmax.z = Math.min(tmax.z,b.az);
                //tmin.y = Math.max(tmin.y,median);
                tmin.x = Math.max(tmin.x,b.ix);
                tmin.z = Math.max(tmin.z,b.iz);
                
                if(tmin.y>tmax.y){
                    float temp = tmin.y;
                    tmin.y = tmax.y;
                    tmax.y = temp;
                }
                
                Box maxBoxY = new Box(tmin,tmax);

                tmin = new Vec(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
                tmax = new Vec(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
                for(Triangle t: lessY){
                    RTObject.updateExtremes(t,tmin,tmax);
                }
                
                //tmax.y = median;
                //tmax.y = Math.min(tmax.y,median);
                tmax.x = Math.min(tmax.x,b.ax);
                tmax.z = Math.min(tmax.z,b.az);
                tmin.x = Math.max(tmin.x,b.ix);
                tmin.y = Math.max(tmin.y,b.iy);
                tmin.z = Math.max(tmin.z,b.iz);
                
                if(tmin.y>tmax.y){
                    float temp = tmin.y;
                    tmin.y = tmax.y;
                    tmax.y = temp;
                }
                
                //tmax.y = median;
                Box minBoxY = new Box(tmin,tmax);

                
                //Begin Z checks
                for(int i = 0;i<tris.size();i++){
                    points[i] = tris.get(i).mid.z;
                }
                Arrays.sort(points);
                if(points.length%2==0){
                    median = (points[points.length/2]+points[points.length/2-1])/2;
                } else {
                    median = points[points.length/2];
                }
                for(Triangle t:tris){
                    if(t.max.z>median&&t.min.z<median){
                        //sharedZ.add(t);
                        //continue;
                    }
                    if(t.max.z>=median){
                        greaterZ.add(t);
                    }else if(t.min.z<median){
                        lessZ.add(t);
                    }
                    
                }

                /*if(greaterZ.size()==0||lessZ.size()==0){
                    System.out.println(greaterZ.size() +" > "+lessZ.size());
                }*/

                tmin = new Vec(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
                tmax = new Vec(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
                
                for(Triangle t: greaterZ){
                    RTObject.updateExtremes(t,tmin,tmax);
                }
                //tmin.z = median;
                
                tmax.x = Math.min(tmax.x,b.ax);
                tmax.y = Math.min(tmax.y,b.ay);
                tmax.z = Math.min(tmax.z,b.az);
                
                //tmin.z = Math.max(tmin.z,median);
                tmin.y = Math.max(tmin.y,b.iy);
                tmin.x = Math.max(tmin.x,b.ix);
                
                if(tmin.z>tmax.z){
                    float temp = tmin.z;
                    tmin.z = tmax.z;
                    tmax.z = temp;
                }
                
                Box maxBoxZ = new Box(tmin,tmax);

                tmin = new Vec(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
                tmax = new Vec(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
                for(Triangle t: lessZ){
                    RTObject.updateExtremes(t,tmin,tmax);
                }

                //tmax.z = median;
                //tmax.z = Math.min(tmax.z,median);
                
                tmax.y = Math.min(tmax.y,b.ay);
                tmax.x = Math.min(tmax.x,b.ax);
                
                tmin.x = Math.max(tmin.x,b.ix);
                tmin.y = Math.max(tmin.y,b.iy);
                tmin.z = Math.max(tmin.z,b.iz);
                
                if(tmin.z>tmax.z){
                    float temp = tmin.z;
                    tmin.z = tmax.z;
                    tmax.z = temp;
                }
                //tmax.z = median;
                Box minBoxZ = new Box(tmin,tmax);
                
                
                Box winminBox,winmaxBox;
                ArrayList<Triangle> winless, wingreater, shared;
                
                /*if(xratio>growthLimit&&yratio>growthLimit&&zratio>growthLimit){
                    return false;
                }*/
                

                float curCost = b.volume()*tris.size();
                //float curVol = b.volume();
                
                float overhead = 4f;
                float fixedOverhead = 0f;

                float xCost = (greaterX.size()+overhead)*maxBoxX.volume()+(lessX.size()+overhead)*minBoxX.volume() + fixedOverhead;
                float yCost = (greaterY.size()+overhead)*maxBoxY.volume()+(lessY.size()+overhead)*minBoxY.volume() + fixedOverhead;
                float zCost = (greaterZ.size()+overhead)*maxBoxZ.volume()+(lessZ.size()+overhead)*minBoxZ.volume() + fixedOverhead;
                
                if(greaterX.size()+lessX.size()!=tris.size()||
                        greaterY.size()+lessY.size()!=tris.size()||
                        greaterZ.size()+lessZ.size()!=tris.size()){
                    System.out.println("unsplit set");
                }
                
                if(xCost>curCost&&yCost>curCost&&zCost>curCost){
                    //System.out.println(">> "+curCost+" "+xCost+" "+yCost+" "+zCost+" "+tris.size());
                    //if(tris.size()>10){
                    //    System.out.println("good times");
                    //}
                    //System.out.println("<< "+aabbs.size()+" boxes");
                    totalTris += boxTris.get(aabbIndex).size();
                    //System.out.println(boxTris.get(aabbIndex).size());
                    return false;
                }
                
                if(xCost<yCost&&xCost<zCost){
                    //System.out.println("Improvement: "+curCost/xCost);
                    winminBox = minBoxX;
                    winmaxBox = maxBoxX;
                    winless = lessX;
                    wingreater = greaterX;

                    b.color = Box.X_COLOR;
                } else if(yCost<zCost){
                    //System.out.println("Improvement: "+curCost/yCost);
                    winminBox = minBoxY;
                    winmaxBox = maxBoxY;
                    winless = lessY;
                    wingreater = greaterY;

                    b.color = Box.Y_COLOR;
                } else {
                    winminBox = minBoxZ;
                    winmaxBox = maxBoxZ;
                    winless = lessZ;
                    wingreater = greaterZ;

                    b.color = Box.Z_COLOR;
                }
                
                //System.out.println(winmaxBox.min);
                
                //if(winmaxBox.min.x==0||winmaxBox.max.x==0||winminBox.min.x==0||winminBox.max.x==0){
                if(winless.size()==0||wingreater.size()==0){
                    System.out.println("chose unsplit set...");
                }
                
                aabbs.add(leftChild(aabbIndex),winminBox);
                aabbs.add(rightChild(aabbIndex),winmaxBox);
                boxTris.add(leftChild(aabbIndex),winless);
                boxTris.add(rightChild(aabbIndex),wingreater);
                //System.out.println(shared.size()+" orphan triangles");
                boxTris.set(aabbIndex,null);
                b.hasChildren = true;
            //}
        //System.out.println("<< "+aabbs.size()+" boxes");
        return true;
    }

    public void cast(Ray r, Intersector it){

        int depth;
        depth = 0;
        //boolean hit = it.cast(r, aabbs.get(1));
        boolean hit = it.cast(r, root);

        if(hit){
            //if(r.s>r.t) return;
            //cast(r, it, 1);
            cast(r, it, root);
        }
    }
    
    public void clearS(){
        clearS(1);
        System.out.println("s is cleared");
    }
    
    private void clearS(int index){
        Box b = aabbs.get(index);
        if(b==null) return;
        //b.clearS();
        if(b.hasChildren){
            clearS(index*2);
            clearS(index*2+1);
        }
        
    }

    //sindex = start index; box with triangle that was hit by previous ray

    public void reverseCast(Ray r, Intersector it, BinaryBox guessBox){

        BinaryBox b = guessBox;

        //This only seems to happen when reverse casting against a scene with multiple objects
        if(b == null){
            return;
        }

        if(it.cast(r,b)){
            //r.boxIndex = sindex;
            r.hitBox = guessBox;
            r.boxColor.addInPlace(b.color);
            if (b.left != null || b.right != null){//it has children
                cast(r,it,guessBox);
            } else {
                ArrayList<SmallTriangle> tris = guessBox.triangles;

                for(int i = 0; i < tris.size(); i++){
                    it.cast(tris.get(i), r);
                }

                if(nray.Options.fastAndWrong){
                    if(r.Itri != null){
                        return;
                    }
                }
            }
        } else {

        }

        float testS = r.s;

        //float lastS = b.lastS;
        //if(!nray.Options.fastAndWrong){
        BinaryBox curBox = b;
        while(curBox.parent != null){
            BinaryBox other;
            BinaryBox temp = curBox.parent;
            if(temp.left == curBox){
                other = temp.right;
            } else {
                other = temp.left;
            }
            b = other;
            r.boxColor.addInPlace(b.color);

            if(b!=null&&it.cast(r, b)){

                if(r.s > r.t){
                    curBox = curBox.parent;
                    continue;
                }

                cast(r,it,other);

                if(r.hitBox != null && r.hitBox.depth < curBox.depth){
                    r.hitBox = curBox;
                }
            }
            curBox = temp;
        }

    }

    public void reverseCast(Ray r, Intersector it, int sindex){

        Box b = aabbs.get(sindex);
        int other;
        int index = sindex;

        //This only seems to happen when reverse casting against a scene with multiple objects
        if(b == null){
            return;
        }

        if(it.cast(r,b)){
            r.boxIndex = sindex;
            r.boxColor.addInPlace(b.color);
            if (b.hasChildren){
                cast(r,it,sindex);
            } else {
                ArrayList<SmallTriangle> tris = boxTrisSmall.get(index);

                //if(tris.size()>10) System.out.println(tris.size());
                /*for(SmallTriangle t:tris){
                    it.cast(t,r);
                }*/

                for(int i = 0; i < tris.size(); i++){
                    it.cast(tris.get(i), r);
                }

                if(nray.Options.fastAndWrong){
                    if(r.Itri != null){
                        return;
                    }
                }
            }
        } else {

        }

        float testS = r.s;
        
        //float lastS = b.lastS;

        while(index > 1){
            //mathematical trickery, mostly equivalent, 2 item lookup table the same, offset to the sibling node
            other = ((index&1)*-2)+1;//(index & 1) == 1 ? -1 : 1;
            b = aabbs.get(index+other);
            r.boxColor.addInPlace(b.color);

            if(b!=null&&it.cast(r, b)){

                if(r.s > r.t){
                    index /= 2;
                    continue;
                }

                cast(r,it,index+other);

                if(r.boxIndex<0){
                    r.boxIndex = index;
                }
            }
            index /= 2;

        }

    }

    public void cast(Ray r, Intersector it, BinaryBox curBox){

        //depth++;
        //if(!hit) return;

        r.boxColor.addInPlace(curBox.color);

        if(r.hitBox==null||(curBox.depth>r.hitBox.depth&&r.Itri==null)){
            r.hitBox = curBox;
        }

        ArrayList<SmallTriangle> tris = curBox.triangles;
        if(tris!=null&&tris.size()>0){

            float oldT = r.t;

            for(int i = 0; i < tris.size(); i++){
                it.cast(tris.get(i), r);
            }

            if(r.t<oldT){
                r.hitBox = curBox;//r.boxIndex = index;
            }
        } else {
            BinaryBox leftBox = curBox.left;
            BinaryBox rightBox = curBox.right;

            if(leftBox == null && rightBox == null) return; //no actual children

            boolean hitLeft = it.cast(r, leftBox);
            float dLeft = r.s;
            boolean hitRight = it.cast(r, rightBox);
            float dRight = r.s;

            if(!nray.Options.bvhcull){
                if(hitLeft)   cast(r, it, leftBox);
                if(hitRight)  cast(r, it, rightBox);
                return;
            }

            //this almost never happens so it's not worth the test
            /*if(r.t < dLeft && r.t < dRight){
                return; //don't care, can't hit anything
            }*/

            if(hitLeft){
                if(hitRight){
                    if(dLeft<dRight){

                        cast(r,it,leftBox);
                        float oldT = r.t;
                        if(r.t<dRight){
                            return;
                        }
                        cast(r,it,rightBox);
                    } else {

                        cast(r,it,rightBox);
                        if(r.t<dLeft){
                            return;
                        }
                        float oldT = r.t;
                        cast(r,it,leftBox);
                    }
                } else {
                    cast(r,it,leftBox);
                }
            } else if(hitRight) {
                cast(r,it,rightBox);
            }
        }

    }

    public void cast(Ray r, Intersector it, int index){
        //depth++;
        //if(!hit) return;
        Box curBox = aabbs.get(index);
        r.boxColor.addInPlace(curBox.color);
        if(index>r.boxIndex&&r.Itri==null){
            r.boxIndex = index;
        }

        ArrayList<SmallTriangle> tris = boxTrisSmall.get(index);
        if(tris!=null&&tris.size()>0){

            float oldT = r.t;

            for(int i = 0; i < tris.size(); i++){
                it.cast(tris.get(i), r);
            }
            if(r.t<oldT){
                r.boxIndex = index;
            }
        } else if(curBox.hasChildren){
            Box leftBox = aabbs.get(leftChild(index));
            Box rightBox =  aabbs.get(rightChild(index));

            boolean hitLeft = it.cast(r, leftBox);
            float dLeft = r.s;
            boolean hitRight = it.cast(r, rightBox);
            float dRight = r.s;

            if(!nray.Options.bvhcull){
                if(hitLeft)   cast(r, it, leftChild(index));
                if(hitRight)  cast(r, it, rightChild(index));
                return;
            }

            //this almost never happens so it's not worth the test
            /*if(r.t < dLeft && r.t < dRight){
                return; //don't care, can't hit anything
            }*/

            if(hitLeft){
                if(hitRight){
                    if(dLeft<dRight){

                        cast(r,it,leftChild(index));
                        float oldT = r.t;
                        if(r.t<dRight){
                            return;
                        }
                        cast(r,it,rightChild(index));
                    } else {

                        cast(r,it,rightChild(index));
                        if(r.t<dLeft){
                            return;
                        }
                        float oldT = r.t;
                        cast(r,it,leftChild(index));
                    }
                } else {
                    cast(r,it,leftChild(index));
                }
            } else if(hitRight) {
                cast(r,it,rightChild(index));
            }
        }

    }
    
    public int leftChild(int index){
        return index*2;
    }
    public int rightChild(int index){
        return index*2+1;
    }

    public BVH clone(){
        BVH copy = null;
        try{
            copy = (BVH)super.clone();
        } catch (CloneNotSupportedException cnse){
            cnse.printStackTrace();
        }
        copy.boxTrisSmall = (ArrayList<ArrayList<SmallTriangle>>)boxTrisSmall.clone();
        copy.boxTris = (ArrayList<ArrayList<Triangle>>)boxTris.clone();

        return copy;
    }

}
