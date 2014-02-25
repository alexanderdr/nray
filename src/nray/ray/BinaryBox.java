package nray.ray;

import nray.Vec;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Dar
 * Date: 2/19/14
 * Time: 9:20 AM
 * A box that contains the intersection data, the triangles it contains, and the references to build a tree from
 */
public class BinaryBox {
    float ix, iy, iz; //min x/y/z
    float ax, ay, az; //max x/y/z
    BinaryBox parent, left, right; //parent and children
    ArrayList<SmallTriangle> triangles = null;
    Vec color;
    int depth = 0;
    public BinaryBox(Box b){
        ix = b.ix;//b.min.x;
        iy = b.iy;//b.min.y;
        iz = b.iz;//b.min.z;

        ax = b.ax;//b.max.x;
        ay = b.ay;//b.max.y;
        az = b.az;//b.max.z;

        color = b.color;
    }

    public void addLeftChild(Box b){
        if(b == null) return;
        left = new BinaryBox(b);
        left.parent = this;
        left.depth = depth + 1;
    }

    public void addLeftChild(BinaryBox b){
        if(b == null) return;
        left = b;
        left.parent = this;
        left.depth = depth + 1;
    }

    public void addRightChild(Box b){
        if(b == null) return;
        right = new BinaryBox(b);
        right.parent = this;
        right.depth = depth + 1;
    }

    public void addRightChild(BinaryBox b){
        if(b == null) return;
        right = b;
        right.parent = this;
        right.depth = depth + 1;
    }
}
