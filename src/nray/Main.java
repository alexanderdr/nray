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
 * Main.java
 *
 */

package nray;

/**
 *
 * @author dalexander
 */
import nray.ray.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
public class Main {

    public static Frame frame;

    /** Creates a new instance of Main */
    public Main() {

        RayScene rs;
        RayTracer rt;

        Camera camera = new Camera(0,2,-5f);
        Camera controlledCamera = new Camera(camera);

        rs = new RayScene();
        new SceneLoader("defaultscene.txt", rs);

        int rayViewWidth = 400;
        int rayViewHeight = 400;

        rt = new RayTracer(rs, camera, rayViewWidth, rayViewHeight);
        Frame f = new Frame();
        f.setSize(416,438);
        f.setLocation(0,0);
        f.setVisible(true);
        Graphics fg = f.getGraphics();

        RayMouseAdapter mouseListener = new RayMouseAdapter(controlledCamera);
        f.addMouseMotionListener(mouseListener);
        f.addMouseListener(mouseListener);
        f.addKeyListener(new RayKeyListener(controlledCamera));
        f.addWindowListener(new java.awt.event.WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent e){
                System.exit(0);
            }
        });

        Frame console = new Frame();
        console.setLocation(0,600);

        console.setVisible(true);
        console.setSize(500,200);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos,true);
        try{
            long time, last = System.currentTimeMillis()-1;
            long sec = last;
            int count = 0;
            float averageFPS = 0;
            int framesOut = 0;
            final int threadCount = 40; //this needs to be a multiple of our ray image size
            RunnerThread[] runners = new RunnerThread[threadCount];
            BufferedImage[] imageSlices = new BufferedImage[threadCount];
            Graphics[] graphicsArray = new Graphics[threadCount];

            BufferedImage intermediateImage = new BufferedImage(rayViewWidth, rayViewHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics iig = intermediateImage.getGraphics();

            for(int curThreadIndex = 0; curThreadIndex < threadCount; curThreadIndex++){
                BufferedImage bi = new BufferedImage(rayViewWidth,rayViewHeight/threadCount,BufferedImage.TYPE_INT_ARGB);
                imageSlices[curThreadIndex] = bi;
                graphicsArray[curThreadIndex] = bi.getGraphics();
            }

            System.out.println("The render loop is now going");
            while(true){
                time = System.currentTimeMillis();
                if(!Options.oneRenderThread){
                    for(int curThreadIndex = 0; curThreadIndex < threadCount; curThreadIndex++){
                        runners[curThreadIndex] = new RunnerThread(rt, imageSlices[curThreadIndex], 0, (curThreadIndex * rayViewHeight) / threadCount, rayViewWidth, ((curThreadIndex + 1) * rayViewHeight) / threadCount);
                        runners[curThreadIndex].start();
                    }
                    for(int curThreadIndex = 0; curThreadIndex < threadCount; curThreadIndex++){
                        runners[curThreadIndex].join();
                        iig.drawImage(imageSlices[curThreadIndex], 0, (curThreadIndex * rayViewHeight) / threadCount, rayViewWidth, rayViewHeight / threadCount, null);
                    }
                } else {
                    rt.render(intermediateImage, iig, 0, 0, rayViewWidth, rayViewHeight);
                }

                camera.set(controlledCamera); //defers any camera position changes to between frames, prevents tearing
                fg.drawImage(intermediateImage, 8, 30, null);

                try{
                    last = System.currentTimeMillis();
                } catch (Exception e){
                    e.printStackTrace();
                }

                if(Options.rotateLight){
                    int millis = (int)(last - time);
                    float radPerMillis = (float)(Math.PI / 4)/1000; //rotate 1/4th of a radian per second
                    rt.rotateLight(millis * radPerMillis);
                }

                if(Options.lightFollowsCamera){
                    rt.setLightToCamera();
                }

                count++;
                if(System.currentTimeMillis()-sec>1000){
                    framesOut++;
                    averageFPS = (averageFPS*(framesOut-1))/framesOut + (float)((double)count*1000)/(System.currentTimeMillis()-sec)/framesOut;

                    baos.reset();
                    out.printf("current:%.3f av:%.3f sec:%d\n",((double)count*1000)/(System.currentTimeMillis()-sec),averageFPS,framesOut);

                    Graphics go = console.getGraphics();
                    go.clearRect(0, 0, 500, 200);
                    go.setColor(Color.BLACK);
                    go.drawString(baos.toString(), 15, 45);

                    count = 0;
                    sec = System.currentTimeMillis();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main m = new Main();
    }
    
}

class RunnerThread extends Thread{
    int startx, starty, endx, endy;
    RayTracer rt;
    BufferedImage bi;
    Graphics g;
    public RunnerThread(RayTracer rt, BufferedImage bi, int startx, int starty, int endx, int endy){
        this.startx = startx;
        this.starty = starty;
        this.endx = endx;
        this.endy = endy;
        this.rt = rt;
        this.bi = bi;
        this.g = bi.getGraphics();
    }
    public void run(){
        rt.render(bi, g, startx, starty, endx, endy);
    }
}
