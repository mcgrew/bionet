package edu.purdue.jsysnet.util;

/**
 * A class for finding whether a particular value falls within a Range.
 */
public class Range {
	private double min;
	private double max;

	/**
	 * Creates a new Range from 0 to 1;
	 */
	public Range( ) {
		this( 0, 1 );
	}

	/**
	 * Creates a new Range with the given values.
	 * 
	 * @param min The minimum value for this Range
	 * @param max The maximum value for this Range
	 */
	public Range( int min, int max ) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Creates a new Range with the given values.
	 * 
	 * @param min The minimum value for this Range
	 * @param max The maximum value for this Range
	 */
	public Range( float min, float max ) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Creates a new Range with the given values.
	 * 
	 * @param min The minimum value for this Range
	 * @param max The maximum value for this Range
	 */
	public Range( double min, double max ) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Sets the minimum value for this Range
	 * 
	 * @param min The new min value for this Range.
	 */
	public void setMinimum( int min ) {
		this.min = max;
	}

	/**
	 * Sets the minimum value for this Range
	 * 
	 * @param min The new min value for this Range.
	 */
	public void setMinimum( float min ) {
		this.min = max;
	}

	/**
	 * Sets the minimum value for this Range
	 * 
	 * @param min The new min value for this Range.
	 */
	public void setMinimum( double min ) {
		this.min = max;
	}

	/**
	 * Sets the maximum value for this Range
	 * 
	 * @param min The new min value for this Range.
	 */
	public void setMaximum( int max ) {
		this.max = max;
	}

	/**
	 * Sets the maximum value for this Range
	 * 
	 * @param min The new min value for this Range.
	 */
	public void setMaximum( float max ) {
		this.max = max;
	}

	/**
	 * Sets the maximum value for this Range
	 * 
	 * @param min The new minimum value for this Range.
	 */
	public void setMaximum( double max ) {
		this.max = max;
	}

	/**
	 * Sets a new minimum and maximum value for this Range.
	 * 
	 * @param min The new minimum value for this Range.
	 * @param max The new maximum value for this Range.
	 */
	public void setRange( int min, int max ) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Sets a new minimum and maximum value for this Range.
	 * 
	 * @param min The new minimum value for this Range.
	 * @param max The new maximum value for this Range.
	 */
	public void setRange( float min, float max ) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Sets a new minimum and maximum value for this Range.
	 * 
	 * @param min The new minimum value for this Range.
	 * @param max The new maximum value for this Range.
	 */
	public void setRange( double min, double max ) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Returns the minimum value of this Range.
	 * 
	 * @return The minimum value of this Range.
	 */
	public double getMin( ) {
		return this.min;
	}

	/**
	 * Returns the maximum value of this Range.
	 * 
	 * @return The maximum value of this Range.
	 */
	public double getMax( ) {
		return this.max;
	}

	/**
	 * Determines whether the specified value is within the Range.
	 * 
	 * @param value The value to check.
	 * @return true if value is within the range, false otherwise.
	 */
	public boolean contains( int value ) {
		return ( value >= this.min && value <= this.max );
	}
	
	/**
	 * Determines whether the specified value is within the Range.
	 * 
	 * @param value The value to check.
	 * @return true if value is within the range, false otherwise.
	 */
	public boolean contains( float value ) {
		return ( value >= this.min && value <= this.max );
	}
	
	/**
	 * Determines whether the specified value is within the Range.
	 * 
	 * @param value The value to check.
	 * @return true if value is within the range, false otherwise.
	 */
	public boolean contains( double value ) {
		return ( value >= this.min && value <= this.max );
	}

	/**
	 * Determines whether the specified value is between min and max
	 *	(not inclusive).
	 * 
	 * @param value The value to check
	 * @return true if value is between min and max, false otherwise.
	 */
	public boolean isInside( int value ) {
		return ( value > this.min && value < this.max );
	}

	/**
	 * Determines whether the specified value is between min and max
	 *	(not inclusive).
	 * 
	 * @param value The value to check
	 * @return true if value is between min and max, false otherwise.
	 */
	public boolean isInside( float value ) {
		return ( value > this.min && value < this.max );
	}

	/**
	 * Determines whether the specified value is between min and max
	 *	(not inclusive).
	 * 
	 * @param value The value to check
	 * @return true if value is between min and max, false otherwise.
	 */
	public boolean isInside( double value ) {
		return ( value > this.min && value < this.max );
	}

}


