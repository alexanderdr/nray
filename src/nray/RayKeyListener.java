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
 * RayKeyListener.java
 *
 * Primarily used for moving the camera and toggling rendering options.
 *
 */

package nray;

/**
 *
 * @author dalexander
 */

import java.awt.event.*;
public class RayKeyListener extends KeyAdapter{
    
    float sensitivity = 1.0f;
    Camera camera;
    /** Creates a new instance of RayKeyListener */
    public RayKeyListener(Camera camera) {
        this.camera = camera;
    }
    
    //This function manipulates the camera
    public void keyPressed(KeyEvent ke){

        switch(ke.getKeyCode()){
            case KeyEvent.VK_UP:
                camera.translateForward(-.2f);
                break;
            case KeyEvent.VK_DOWN:
                camera.translateForward(.2f);
                break;
            case KeyEvent.VK_LEFT:
                camera.translateRight(.2f);
                break;
            case KeyEvent.VK_RIGHT:
                camera.translateRight(-.2f);
                break;
        }

        //While it would be possible to bind these cases to the earlier switch, it seems to be more readable this way
        switch(ke.getKeyChar()){
            case 't':
                camera.rotateX(((float)Math.PI)/6f);
                break;
            case 'g':
                camera.rotateX(-((float)Math.PI)/6f);
                break;
            case 'f':
                camera.rotateY(((float)Math.PI)/6f);
                break;
            case 'h':
                camera.rotateY(-((float)Math.PI)/6f);
                break;
            case 'k':
                System.out.println(camera.pos);
                break;
            case 'u':
                Options.UV=!Options.UV;
                break;
            case 'n':
                Options.normal=!Options.normal;
                break;
            case 'c':
                Options.colorhits=!Options.colorhits;
                break;
            case 'p':
                Options.colorphits=!Options.colorphits;
                break;
            case 'x':
                Options.colorthits=!Options.colorthits;
                break;
            case 'o':
                Options.colorTime=!Options.colorTime;
                break;
            case 'P':
                Options.hardGather=!Options.hardGather;
                break;
            case 'r':
                Options.reflection=!Options.reflection;
                break;
            case 'B':
                Options.bvhcolor=!Options.bvhcolor;
                break;
            case 'z':
                Options.bvhcull=!Options.bvhcull;
                break;
            case 'F':
                Options.fastAndWrong = !Options.fastAndWrong;
                break;
            case 'l':
                Options.colorBlack=!Options.colorBlack;
                break;
            case 'M':
                Options.oneRenderThread=!Options.oneRenderThread;
                System.out.println("Multi-threading: "+(!Options.oneRenderThread));
                break;
            case 'R':
                Options.reverseCast=!Options.reverseCast;
                System.out.println("Initial ray reverse cast: " + Options.reverseCast);
                break;
            case 'L':
                Options.photonLighting = !Options.photonLighting;
                break;
            case '1':
                Options.rotateLight = !Options.rotateLight;
                break;
            case '2':
                Options.lightFollowsCamera = !Options.lightFollowsCamera;
                break;
            case '?':
                HelpDialogue.display();
                break;
        }

    }
    
    public void keyReleased(KeyEvent ke){
    }
    
}
