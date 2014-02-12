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

package nray;

/**
 * Created with IntelliJ IDEA.
 * User: dalexander
 * Date: 2/11/14
 * Time: 12:25 PM
 */
import java.io.*;

import nray.obj.*;
import nray.ray.*;
import java.util.*;
public class SceneLoader {
    public SceneLoader(String filename, RayScene rs){
        try{
            File f = new File(filename);
            BufferedReader in = new BufferedReader(new FileReader(f));

            parseReader(filename, in, rs);

        } catch(FileNotFoundException fnfe){
            //config file not found... this is a problem
            //don't have a graceful way to handle it though without the assumption other things exist
            fnfe.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseReader(String filename, BufferedReader in, RayScene rs){
        int lineNum = 1;
        try{
            while(in.ready()){
                lineNum++;
                String line = in.readLine().trim();
                if(line.length() == 0) continue;
                if(line.charAt(0) == ';') continue; //; at the start of a line represents a comment
                StringTokenizer stz = new StringTokenizer(line);
                String command = stz.nextToken();
                if(command.equals("load")){
                    String nameToLoad = stz.nextToken();
                    Vec offset = new Vec();
                    boolean shifted = false;
                    if(stz.hasMoreTokens()){
                        shifted = true;
                        offset.set(Float.parseFloat(stz.nextToken()), Float.parseFloat(stz.nextToken()), Float.parseFloat(stz.nextToken()));
                    }
                    Mesh mesh = ObjImporter.importObj(nameToLoad);
                    if(shifted){
                        //we need to translate before we create the RTObject, because the RTObject generates its BVH in the constructor
                        mesh.translate(offset);
                    }
                    RTObject rayObject = new RTObject(mesh);
                    System.out.println("Loaded object from "+nameToLoad+" with "+rayObject.triangleCount+" triangles");
                    rs.addRTObject(rayObject);
                }
            }
        } catch(IOException ioe){
            System.err.println("Error loading file " + filename + " at line "+lineNum);
            ioe.printStackTrace();
        }
    }

}
