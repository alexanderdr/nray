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
 * Options.java
 *
 * A bunch of global options.
 * It's entirely possible this should expose a singleton instance which we swap out between frames
 */

package nray;

/**
 *
 * @author dalexander
 */
public class Options {
    public static boolean subbox = true;
    public static boolean UV = false;
    public static boolean normal = false;
    public static boolean bvhcolor = false;
    public static boolean reflection = false;
    public static boolean colorhits = false;
    public static boolean colorthits = false;
    public static boolean colorphits = false;
    public static boolean reverseCast = true;
    public static boolean bvhcull = true;
    public static boolean hardGather = false;
    public static boolean colorBlack = false;
    public static boolean colorTime = false;
    public static boolean oneRenderThread = false;
    public static boolean photonLighting = false;
    public static boolean rotateLight = false;
    public static boolean lightFollowsCamera = false;
    /** Creates a new instance of Options */
    public Options() {
    }
    
}
