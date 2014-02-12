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
 * Texture.java
 *
 * This class loads and manages a single texture in OpenGL, loading it from a file.
 * Not currently used in the raytracer, but it shouldn't be difficult to extend the ray coloring to use
 * textures since we already have UV coordinates
 *
 */

package nray;

/**
 *
 * @author dalexander
 */
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.nio.*;
import java.util.*;
import javax.imageio.spi.*;
public class Texture {
    
    int id = -1;
    int height, width;
    ByteBuffer data;
    
    boolean initalized = false;
    
    /** Creates a new instance of Texture */
    public Texture(String texName) {
        loadImage(texName);
    }
    
    public void loadImage (String iName) {
        try{
            IIORegistry iio = IIORegistry.getDefaultInstance();
            Iterator i = iio.getCategories();
            
            BufferedImage img = ImageIO.read(new File(iName));
            int[] pixels = new int[img.getWidth() * img.getHeight()];
            //Creates the closest thing Java has to a pointer.  Reasonable performance.
            ByteBuffer bb = ByteBuffer.allocateDirect(pixels.length*4);
            PixelGrabber pg = new PixelGrabber(img, 0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
            pg.grabPixels();
            
            width = img.getWidth();
            height = img.getHeight();
            
            //read it in backwards because OpenGL and Java use different systems...
            //ABGR is the default Java orientation of the color bytes, RBGA is what OpenGL wants (usually)
            //with the pixel grabber alpha is 24, blue is 0, green is 8 and red is 16
            for(int y = 0;y<height;y++){
                for(int x = 0;x<width;x++){
                    bb.put(((y*(width*4))+(x*4)+0),(byte)((pixels[((((height-y)-1)*width)+x)] >> 16)&0xFF)); //R
                    bb.put(((y*(width*4))+(x*4)+1),(byte)((pixels[((((height-y)-1)*width)+x)] >>  8)&0xFF)); //B
                    bb.put(((y*(width*4))+(x*4)+2),(byte)((pixels[((((height-y)-1)*width)+x)] >>  0)&0xFF)); //G
                    bb.put(((y*(width*4))+(x*4)+3),(byte)((pixels[((((height-y)-1)*width)+x)] >> 24)&0xFF)); //A
                }
            }

            data = bb;
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void load(){
                
    }
    
    public void unload(){

    }
    
}
