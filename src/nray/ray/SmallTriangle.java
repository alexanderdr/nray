package nray.ray;

/**
 * Created with IntelliJ IDEA.
 * User: Dar
 * Date: 2/18/14
 * Time: 3:13 PM
 * Only what's needed to do ray-triangle intersection.  To minimize the stress on the memory and cache systems.
 */
import nray.Vec;
public class SmallTriangle {

    float px, py, pz;
    float e1x, e1y, e1z;
    float e2x, e2y, e2z;
    Triangle parentRef; //would be nice to be able to omit this... but it probably doesn't push us over the cache line size (16byte obj + 36 bytes of floats + 8/4 bytes of reference)

    public SmallTriangle(Vec p1, Vec p2, Vec p3){
        px = p1.x;
        py = p1.y;
        pz = p1.z;
        Vec t1 = p2.sub(p1);
        Vec t2 = p3.sub(p1);
        e1x = t1.x;
        e1y = t1.y;
        e1z = t1.z;
        e2x = t2.x;
        e2y = t2.y;
        e2z = t2.z;
    }

    public SmallTriangle(Triangle t){

        parentRef = t;

        Vec p1 = t.p1;
        Vec t1 = t.e1;
        Vec t2 = t.e2;

        px = p1.x;
        py = p1.y;
        pz = p1.z;
        e1x = t1.x;
        e1y = t1.y;
        e1z = t1.z;
        e2x = t2.x;
        e2y = t2.y;
        e2z = t2.z;
    }
}
