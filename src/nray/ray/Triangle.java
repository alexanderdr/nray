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
 * Triangle.java
 *
 * 
 * A triangle prepared to accept raycasts
 */

package nray.ray;

/**
 *
 * @author dalexander
 */
import nray.*;
public class Triangle {

    int hit = 0;

    Vec p1, p2, p3;
    Vec e1, e2;
    Vec n1, n2, n3;
    Vec normal;
    Vec max = new Vec();
    Vec min = new Vec();
    Vec mid = new Vec();
    public Triangle(Vec p1,Vec p2,Vec p3,Vec n1,Vec n2,Vec n3){
        this(p1,p2,p3);
        
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;

        if(n1.mag() == 0 || n2.mag() == 0 || n3.mag() == 0){
            e1 = p2.sub(p1);
            e2 = p3.sub(p1);
            normal = e1.cross(e2).normalizeInPlace();
            this.n1 = normal;
            this.n2 = normal;
            this.n3 = normal;
        }

    }
    
    public Triangle(Vec p1,Vec p2,Vec p3){
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        e1 = p2.sub(p1);
        e2 = p3.sub(p1);
        normal = e1.cross(e2).normalizeInPlace();

        this.n1 = normal;
        this.n2 = normal;
        this.n3 = normal;

        mid.addInPlace(p1);
        mid.addInPlace(p2);
        mid.addInPlace(p3);
        
        mid.scale(1/3f);
        
        max.set(p1);
        if(max.x<p2.x){
            max.x = p2.x;
        }
        if(max.x<p3.x){
            max.x = p3.x;
        }
        if(max.y<p2.y){
            max.y = p2.y;
        }
        if(max.y<p3.y){
            max.y = p3.y;
        }
        if(max.z<p2.z){
            max.z = p2.z;
        }
        if(max.z<p3.z){
            max.z = p3.z;
        }
        
        min.set(p1);
        if(min.x>p2.x){
            min.x = p2.x;
        }
        if(min.x>p3.x){
            min.x = p3.x;
        }
        if(min.y>p2.y){
            min.y = p2.y;
        }
        if(min.y>p3.y){
            min.y = p3.y;
        }
        if(min.z>p2.z){
            min.z = p2.z;
        }
        if(min.z>p3.z){
            min.z = p3.z;
        }
    }
    
    public void move(Vec v){
        p1.addInPlace(v);
        p2.addInPlace(v);
        p3.addInPlace(v);
    }
}