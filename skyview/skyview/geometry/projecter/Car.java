package skyview.geometry.projecter;

import skyview.geometry.*;

import static java.lang.Double.NaN;
import static java.lang.Math.*;

import skyview.geometry.Projecter;
import skyview.geometry.Deprojecter;
import skyview.geometry.Transformer;

import skyview.geometry.sampler.Clip;


/** This class implements the Cartesian (rectangular)
 *  projection.  Note that the tangent point
 *  is assumed to be at the north pole.
 *  This class assumes preallocated arrays for
 *  maximum efficiency.
 */

public final class Car extends Projecter {
   
    private Straddle myStraddler = new CarStraddle(this);
    
    /** Get the name of the compontent */
    public String getName() {
	return "Car";
    }
    
    /** Return a description of the component */
    public String getDescription() {
	return "Transform from the celestial sphere to the plane described by Lon/Lat directly";
    }
    
    /** Get the inverse transformation */
    public Deprojecter inverse() {
	return new  Car.CarDeproj();
    }
    
    /** Get tile offsets */
    public double getXTiling() {
	return 2*Math.PI;
    }
    /** Get tile offsets */
    public double getYTiling() {
	return 2*Math.PI;
    }
    
    /** Is this an inverse of some other transformation? */
    public boolean isInverse(Transformer t) {
        return t.getName().equals("CarDeproj");
    }
    
    /** Find the shadow to this point */
    public double[] shadowPoint(double x, double y) {
	if (x > 0) {
	    return new double[]{x-2*Math.PI, y};
	} else if (x < 0) {
	    return new double[]{x+2*Math.PI, y};
	} else {
	    return new double[]{2*Math.PI, y};
	}
    }
    
    /** Do the transformation */
    public final void transform(double[] sphere, double[] plane) {
	
	if (Double.isNaN(sphere[2]) ) {
	    plane[0] = NaN;
	    plane[1] = NaN;
	} else {
	    plane[0] = atan2(sphere[1], sphere[0]);
	    plane[1] = asin(sphere[2]);
	}
    }
    
    /** The entire plane is valid */
    public boolean allValid() {
	return true;
    }
  
    /** Does this area appear to straddle the standard region? */
    public boolean straddle(double[][] vertices) {
	return myStraddler.straddle(vertices);
    }
    
    public boolean straddleable() {
	return true;
    }
    
    /** Return a set of straddle points.
     */
    public double[][][] straddleComponents(double[][] inputs) {
	return myStraddler.straddleComponents(inputs);
    }

    /** Return the Tissot indicatrix for point */
    public double[] tissot(double x, double y) {
        // Depends only on the latitude which we assume to
        // be in the range |b| <= pi/2
        return new double[] {1./Math.sin(y), 1, 0};
    }
		      
    public class CarDeproj extends Deprojecter {
        /** Get the name of the compontent */
        public String getName() {
	    return "CarDeproj";
        }
    
        /** Is this an inverse of some other transformation? */
        public boolean isInverse(Transformer t) {
	    return t.getName().equals("Car");
        }
	
        /** Return a description of the component */
        public String getDescription() {
	    return "Transform from the Lat/Lon to the corresponding unit vector.";
	}
	
	/** Get the inverse */
	public Projecter inverse() {
	    return Car.this;
	}
	
	public double getXTiling() {
	    return 2*Math.PI;
	}
	
	public double getYTiling() {
	    return 2*Math.PI;
	}
    
        /** Deproject a point from the plane to the sphere.
         *  @param plane a double[2] vector in the tangent plane.
         *  @param spehre a preallocated double[3] vector.
         */
        public final void transform(double[] plane, double[] sphere) {
	
	    if (Double.isNaN(plane[0])) {
	        sphere[0] = NaN;
	        sphere[1] = NaN;
	        sphere[2] = NaN;
	    
	    } else {
	     
	        double sr = sin(plane[0]);
	        double cr = cos(plane[0]);
	        double sd = sin(plane[1]);
	        double cd = cos(plane[1]);
	    
	        sphere[0] = cr*cd;
	        sphere[1] = sr*cd;
	        sphere[2] = sd;
	    }
	}
    }
}
