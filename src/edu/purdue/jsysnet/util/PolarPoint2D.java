package edu.purdue.jsysnet.util;

import java.awt.geom.Point2D;

/**
 * A Class for extending Point2D to include Polar Coordinates. Although this class has public members x, y, r, and
 * theta, it is not recommended to modify them directly, this may have unpredictable results. Instead use the
 * setLocation( ) and move( ) methods to manipulate the location of the point.
 */
public class PolarPoint2D extends Point2D.Double {

	public double r;
	public double theta;
	public final static boolean POLAR = true;
	public final static boolean RECTANGULAR = false;
	private Point2D origin;

	/**
	 * Creates a PolarPoint2D using polar notation.
	 * 
	 * @param r The r coordinate of the point in polar notation.
	 * @param theta The angle of the point from the origin (in radians).
	 * @param polar true if this point is being created with polar coordinates.
	 */
	public PolarPoint2D( double r, double theta, boolean polar ) {
		this.setOrigin( 0, 0 );
		if ( polar ) {
			this.setLocation( r, theta, POLAR );
		} else {
			this.setLocation( r, theta );
		}
	}

	public PolarPoint2D( double r, double theta, Point2D origin ) {
		this.setOrigin( origin );
		this.setLocation( r, theta, POLAR );
	}

	public PolarPoint2D( double r, double theta, double originX, double originY ) {
		this.setOrigin( originX, originY );
		this.setLocation( r, theta, POLAR );
	}

	/**
	 * Creates a PolarPoint2D using x/y notation.
	 * 
	 * @param x The x coordinate of the new point.
	 * @param y The y coordinate of the new point.
	 */
	public PolarPoint2D( double x, double y ) {
		this.setOrigin( 0, 0 );
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
		this.origin = new Point2D.Double( 0, 0 );
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
		double relativeX = x - this.origin.getX( );
		double relativeY = y - this.origin.getY( );
		this.r = Math.hypot( relativeX, relativeY );
		this.theta = Math.atan2( relativeY, relativeX );
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
	 * Sets the location of this PolarPoint2D to the specified polar coordinates relative to the
	 * origin point.
	 * 
	 * @param r The r coordinate of the new location.
	 * @param theta The angle theta of the new location.
	 * @param polar true if these coordinates are in polar notation.
	 */
	public void setLocation( double r, double theta, boolean polar ) {
		if ( polar ) {
			this.r = r;
			this.theta = theta;
			this.x = r * Math.cos( theta ) + origin.getX( );
			this.y = r * Math.sin( theta ) + origin.getY( );
		} else {
			this.setLocation( r, theta );
		}
	}

	/**
	 * Leaves the point in it's current location, but changes the location of the origin,
	 * recalculating r and theta.
	 * 
	 * @param origin the new Point2D to be used as the origin.
	 */
	public void setOrigin( Point2D origin ) {
		this.origin = origin;
		this.setLocation( this.x, this.y );
	}

	public void setOrigin( double originX, double originY ) {
		this.setOrigin( new Point2D.Double( originX, originY ));
	}

	/**
	 * Creates a new object of the same class and with the same contents as this object.
	 * 
	 * @return a clone of this instance.
	 */
	public Object clone( ) {
		return ( Object )new PolarPoint2D( this.r, this.theta, this.origin );
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

	public void move( Point2D p ) {
		this.setLocation( this.x + p.getX( ), this.y + p.getY( ));
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

	public void move( double r, double theta, Point2D origin ) {
		this.origin = (Point2D)origin.clone( );
		this.setLocation( r, theta, POLAR );
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
