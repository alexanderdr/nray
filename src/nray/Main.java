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
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static Frame frame; //used by the jdialog in HelpDialog

    /** Creates a new instance of Main */
    public Main() {

        RayScene rs;
        RayTracer rt;

        Camera camera = new Camera(0,2,-5f);
        Camera controlledCamera = new Camera(camera);

        rs = new RayScene();
        new SceneLoader("defaultscene.txt", rs);

        //Should make this settable in a config.txt file
        int rayViewWidth = 400;
        int rayViewHeight = 400;

        float viewRatio = rayViewWidth/(float)rayViewHeight;

        rt = new RayTracer(rs, camera, rayViewWidth, rayViewHeight);

        Frame f = new Frame();
        f.setSize(rayViewWidth + 16, rayViewHeight + 38);
        f.setLocation(0,0);
        f.setVisible(true);
        f.setTitle("nray Tracer");
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
        console.setLocation(0,rayViewHeight + 40);

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
            //because we always want as many software threads as the hardware supports,
            //ideally should be set in the config file:
            final int threadCount = 8;
            RunnerThread[] runners = new RunnerThread[threadCount];

            BufferedImage intermediateImage = new BufferedImage(rayViewWidth, rayViewHeight, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics iig = intermediateImage.getGraphics();

            //these should ideally be settable in the config file as well
            //these needs to be a multiple of our ray image size
            final int chunkHeightsPerImage = 20;
            final int chunkWidthsPerImage = 8;
            final int chunkCount = chunkWidthsPerImage * chunkHeightsPerImage;
            RenderChunk[] chunks = new RenderChunk[chunkCount];

            BufferedImage[] imageSlices = new BufferedImage[chunkCount];
            Graphics[] graphicsArray = new Graphics[chunkCount];

            Object frameLock = new Object();
            AtomicInteger chunksLeft = new AtomicInteger(0);

            System.out.println("The render loop is now going");

            for(int curThreadIndex = 0; curThreadIndex < chunkCount; curThreadIndex++){
                BufferedImage bi = new BufferedImage(rayViewWidth/chunkWidthsPerImage, rayViewHeight/chunkHeightsPerImage, BufferedImage.TYPE_INT_ARGB_PRE);
                imageSlices[curThreadIndex] = bi;
                graphicsArray[curThreadIndex] = bi.getGraphics();
            }

            for(int curThreadIndex = 0; curThreadIndex < threadCount; curThreadIndex++){
                runners[curThreadIndex] = new RunnerThread(rt, chunks, chunksLeft, frameLock);//new RunnerThread(rt, imageSlices[curThreadIndex], 0, (curThreadIndex * rayViewHeight) / threadCount, rayViewWidth, ((curThreadIndex + 1) * rayViewHeight) / threadCount);
                runners[curThreadIndex].start();
            }

            while(true){

                time = System.currentTimeMillis();
                for(int y = 0; y < chunkHeightsPerImage; y++){
                    for(int x = 0;x < chunkWidthsPerImage; x++){

                        chunks[x + (y * chunkWidthsPerImage)] = new RenderChunk(imageSlices[x + (y * chunkWidthsPerImage)], x * (rayViewWidth / chunkWidthsPerImage), y * (rayViewHeight / chunkHeightsPerImage),
                                (x + 1) * (rayViewWidth / chunkWidthsPerImage), (y + 1) * (rayViewHeight / chunkHeightsPerImage), viewRatio);
                    }
                }
                chunksLeft.set(chunkCount - 1);
                if(!Options.oneRenderThread){
                    synchronized(frameLock){
                        frameLock.notifyAll();
                    }

                    for(int curChunkIndex = chunkCount - 1; curChunkIndex >= 0; curChunkIndex--){
                        synchronized(chunks[curChunkIndex]){
                            while(!chunks[curChunkIndex].isComplete()){
                                try{
                                    chunks[curChunkIndex].wait();
                                } catch (InterruptedException ie){
                                    ie.printStackTrace();
                                }
                            }
                        }
                        RenderChunk chunk = chunks[curChunkIndex];
                        iig.drawImage(imageSlices[curChunkIndex], chunk.startx, chunk.starty, chunk.endx - chunk.startx, chunk.endy - chunk.starty, null);
                    }
                } else {
                    //just do it with this thread
                    //it's worth noting that this is not as fast as the above code with a single thread running (it's 5-10% slower)
                    rt.render(intermediateImage, iig, 0, 0, rayViewWidth, rayViewHeight, viewRatio);
                }

                camera.set(controlledCamera); //defers any camera position changes to between frames, prevents tearing
                fg.drawImage(intermediateImage, 8, 30, null); //constants deal with the UI title bar and sides.

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

class RenderChunk{
    float widthHeightRatio;
    int startx, starty, endx, endy;
    RayTracer rt;
    BufferedImage bi;
    Graphics g;
    boolean isComplete = false;
    public RenderChunk(BufferedImage bi, int startx, int starty, int endx, int endy, float ratio){
        this.startx = startx;
        this.starty = starty;
        this.endx = endx;
        this.endy = endy;
        this.rt = rt;
        this.bi = bi;
        this.g = bi.getGraphics();
        this.widthHeightRatio = ratio;
    }

    public synchronized boolean isComplete(){
        return isComplete;
    }

    public synchronized void setComplete(boolean completeness){
        isComplete = completeness;
        if(isComplete){
            notifyAll();
        }
    }
}

class RunnerThread extends Thread{
    int startx, starty, endx, endy;
    RayTracer rt;
    BufferedImage bi;
    Graphics g;
    RenderChunk[] remainingChunks;
    Object frameLock;
    AtomicInteger chunkCount;
    public RunnerThread(RayTracer rt, RenderChunk[] remainingChunks, AtomicInteger chunkCount, Object frameLock){
        this.rt = rt;
        this.remainingChunks = remainingChunks;
        this.chunkCount = chunkCount;
        this.frameLock = frameLock;

    }



    public void run(){
        while(true){

            RenderChunk chunk = null;
            synchronized (remainingChunks){ //should be handled by the atomic
                if(chunkCount.get() >= 0){
                    chunk = remainingChunks[chunkCount.getAndDecrement()];
                }
            }
            if(chunk != null){
                rt.render(chunk.bi, chunk.g, chunk.startx, chunk.starty, chunk.endx, chunk.endy, chunk.widthHeightRatio);
                chunk.setComplete(true);
            } else {
                try{

                    synchronized(frameLock){
                        while(chunkCount.get() < 0){
                            frameLock.wait();
                            //busywaiting here is actually slower
                        }
                    }
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }

        }
    }
}
