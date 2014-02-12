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
 * RayMouseAdapter.java
 *
 * This object translate's the user's mouse motion into rotation
 * 
 */

package nray;

/**
 *
 * @author dalexander
 */

import java.awt.event.*;
public class RayMouseAdapter implements MouseMotionListener, MouseListener{
    
    boolean leftMouseDown = false;
    int lastx = -1, lasty = -1;

    
    //Sensitivity should be changed rather than base multiplier when adjusting the sensitivity of the mouse
    float sensitivity = .5f;
    private float baseMultiplier = 1.0f;

    Camera camera;

    /** Creates a new instance of RayMouseAdapter */
    public RayMouseAdapter(Camera camera) {
        this.camera = camera;
    }
    
    public void mouseMoved(MouseEvent me){
        //System.out.println("Moved");
    }
    
    
    public void mouseDragged(MouseEvent me){
        //Rotate the camera if the user is dragging the mouse around on the canvas
        if(leftMouseDown){
            
            int x = me.getX();
            int y = me.getY();

            int xdiff = x-lastx;
            int ydiff = y-lasty;
            
            camera.rotateX(-ydiff*baseMultiplier*sensitivity);
            camera.rotateY(-xdiff*baseMultiplier*sensitivity);

            lastx = x;
            lasty = y;

        }
        
    }
    
    public void mouseExited(MouseEvent me){
        //System.out.println("Exited");
    }
    
    public void mouseEntered(MouseEvent me){
        //System.out.println("Entered");
    }
    
    public void mouseReleased(MouseEvent me){
        //System.out.println("Released");
        if(me.getButton()==MouseEvent.BUTTON1){
            leftMouseDown = false;
        }
    }
    
    public void mousePressed(MouseEvent me){
        //System.out.println("Pressed");
        if(me.getButton()==MouseEvent.BUTTON1){
            leftMouseDown = true;
            //Need to set the starting x and y coordinates for relative movement otherwise there's a jerk from -1 -1 (or the last mouse released location) at first press
            lastx = me.getX();
            lasty = me.getY();
        }
    }
    
    public void mouseClicked(MouseEvent me){
        //System.out.println("Clicked");
    }
}
