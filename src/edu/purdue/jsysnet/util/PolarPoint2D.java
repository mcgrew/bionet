package edu.purdue.jsysnet.util;

import java.awt.geom.Point2D;

/**
 * A Class for extending Point2D to include Polar Coordinates. Although this class has public members x, y, r, and
 * theta, it is not recommended to modify them directly, this may have unpredictable results. Instead use the
 * setLocation( ) and move( ) methods to manipulate the location of the point.
 */
class PolarPoint2D extends Point2D.Double {

	public double r;
	public double theta;

	/**
	 * Creates a PolarPoint2D using polar notation.
	 * 
	 * @param r The r coordinate of the point in polar notation.
	 * @param theta The angle of the point from the origin (in radians).
	 * @param polar true if this point is being created with polar coordinates.
	 */
	public PolarPoint2D( double r, double theta, boolean polar ) {
		if ( polar ) {
			this.setLocation( r, theta, true );
		} else {
			this.setLocation( r, theta );
		}
	}

	/**
	 * Creates a PolarPoint2D using x/y notation.
	 * 
	 * @param x The x coordinate of the new point.
	 * @param y The y coordinate of the new point.
	 */
	public PolarPoint2D( double x, double y ) {
		this.setLocation( x, y );
	}

	/**
	 * Creates a new PolarPoint2D at location (0,0)
	 */
	public PolarPoint2D( ) {
		this.x = 0;
		this.y = 0;
		this.r = 0;
		this.theta = 0;
	}

	/**
	 * Creates a new PolarPoint2D with the same location as the specified Point2d.
	 * 
	 * @param p The Point2D to copy the location of.
	 */
	public PolarPoint2D( Point2D p ) {
		this( p.getX( ), p.getY( ));
	}

	/**
	 * Sets the location of this PolarPoint2D to the specified coordinates.
	 * 
	 * @param x The new x coordinate of this PolarPoint2D.
	 * @param y The new y coordinate of this PolarPoint2D.
	 */
	public void setLocation( double x, double y ) {
		this.x = x;
		this.y = y;
		this.r = Math.hypot( x, y );
		this.theta = Math.atan2( y, x );
	}

	/**
	 * Sets the location of this PolarPoint2D to the same coordinates as the specified Point2D object. 
	 * 
	 * @param p The Point2D to copy the location of.
	 */
	public void setLocation( Point2D p ) {
		this.setLocation( p.getX( ), p.getY( ));
	}

	/**
	 * Sets the location of this PolarPoint2D to the specified polar coordinates.
	 * 
	 * @param r The r coordinate of the new location.
	 * @param theta The angle theta of the new location.
	 * @param polar true if these coordinates are in polar notation.
	 * @return 
	 */
	public void setLocation( double r, double theta, boolean polar ) {
		if ( polar ) {
			this.r = r;
			this.theta = theta;
			this.x = r * Math.cos( theta );
			this.y = r * Math.sin( theta );
		} else {
			this.setLocation( r, theta );
		}
	}

	/**
	 * Creates a new object of the same class and with the same contents as this object.
	 * 
	 * @return a clone of this instance.
	 */
	public Object clone( ) {
		return ( Object )new PolarPoint2D( this.x, this.y );
	}

	/**
	 * Moves this point by the specified distances. 
	 * 
	 * @param x The distance to move the PolarPoint2D on the x axis.
	 * @param y The distance to move the PolarPoint2D on the y axis.
	 */
	public void move( double x, double y ) {
		this.setLocation( this.x + x, this.y + y );
	}

	/**
	 * Moves this point by the specified distances.
	 * 
	 * @param r The distance to move the PolarPoint2D from the origin.
	 * @param theta The amount to change the theta angle of the PolarPoint2D.
	 * @param polar true if these coordinates are in polar notation.
	 */
	public void move( double r, double theta, boolean polar ) {
			this.setLocation( this.r + r, this.theta + theta, polar );
	}

	/**
	 * Returns the r coordinate of this PolarPoint2D in double precision.
	 * 
	 * @return The r coordinate of this PolarPoint2D.
	 */
	public double getR( ) {
		return this.r;	
	}

	/**
	 * Returns the theta angle of this PolarPoint2D in double precision.
	 * 
	 * @return The theta angle of this PolarPoint2d.
	 */
	public double getTheta( ) {
		return this.theta;
	}

	public void scale( double value ) {
		this.setLocation( this.r * value, this.theta );
	}

	public void scale( double xValue, double yValue ) {
		this.setLocation( this.x * xValue, this.y * yValue );
	}

	public void scaleX( double value ) {
		this.setLocation( this.x * value, this.y );
	}

	public void scaleY( double value ) {
		this.setLocation( this.x, this.y * value );
	}
}
