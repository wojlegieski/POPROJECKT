package MyMath;

public class MVector3D extends MPoint3d {
    public MVector3D(double x, double y, double z) {
        super(x, y, z);
    }
    public MVector3D(double[] vector) {
        super(vector);
    }
    public MVector3D vecp(MVector3D vector) {
        MVector3D v = new MVector3D(this.y*vector.z-this.z*vector.y, this.z*vector.x-this.x*vector.z, this.x*vector.y-this.y*vector.x );
        return v;
    }
    public double abs(){
        return Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z);
    }
    public double vsin(MVector3D vector) {
        return (this.vecp(vector).abs())/(this.abs()*vector.abs());
    }
    public double scalp(MVector3D vector){
        return vector.x*this.x+vector.y*this.y+vector.z*this.z;
    }
    public double vcos(MVector3D vector){
        return this.scalp(vector)/(this.abs()*vector.abs());
    }
    public MVector3D revese() {
        return new MVector3D(-this.x,-this.y,-this.z);
    }
    public double agl(MVector3D vector,MVector3D vtop) {
        if (this.abs() * vector.abs()==0){
            return 0;
        }
        double cosAngle = this.scalp(vector) / (this.abs() * vector.abs());
        int flag;
        if(this.diffvec(vtop.revese()).abs()<this.diffvec(vtop).abs()){
            flag = 1;
        }else flag = -1;
        return Math.acos(cosAngle)*flag;
    }
    public MVector3D setlength(double length) {
        return new MVector3D(this.x*length/abs(),this.y*length/abs(),this.z*length/abs());

    }
    public MVector3D normalize() {
        return new MVector3D(this.x/abs(),this.y/abs(),this.z/abs());
    }
}
