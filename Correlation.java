import java.lang.Math;
import java.lang.Double;
import java.util.ArrayList;
import java.util.Arrays;

public class Correlation {

	public static final int PEARSON = 0;
	public static final int SPEARMAN = 1;
	public static final int KENDALL = 2;
	private double pearsonCorrelation = Double.NaN;
	private double spearmanCorrelation = Double.NaN;
	private double kendallCorrelation = Double.NaN;
	private Molecule [ ] molecules = new Molecule[ 2 ];
	
	public Correlation( Molecule molecule1, Molecule molecule2 ) {
		this.molecules[ 0 ] = molecule1;
		this.molecules[ 1 ] = molecule2;
		this.molecules[ 0 ].addCorrelation( this );
		this.molecules[ 1 ].addCorrelation( this );
	}

	public Correlation( Molecule [] molecules ){
		this.molecules[ 0 ] = molecules[ 0 ];
		this.molecules[ 1 ] = molecules[ 1 ];
		this.molecules[ 0 ].addCorrelation( this );
		this.molecules[ 1 ].addCorrelation( this );
	}

	public Molecule [ ] getMolecules( ) {
		return this.molecules;
	}

	public double getValue( int method ) {
		
		switch ( method ) {
			case PEARSON:
				return this.getPearsonCorrelation( );

			case SPEARMAN:
				return this.getSpearmanCorrelation( );

			case KENDALL:
				return this.getKendallCorrelation( );

			default:
				return -1;
		}
	}
	private double getPearsonCorrelation( ) {
	 return this.getPearsonCorrelation( false );
	}
	private double getPearsonCorrelation( boolean recalc ) {
		/*
				mol0 = sample values in molecule 0
				mol1 = sample values in molecule 1
				count = number of samples in each molecule
				sx2 = sum ( x*x for x in mol0 )
				sy2 = sum ( x*x for x in mol1 )
				sx = sum ( mol0 )
				sy = sum ( mol1 )
				sxy = sum (mol0[x] * mol1[x] for x in range( count ))

				if ( count <= 3 ):
					correlationValue = 0
				else:
																							sxy - (sx*sy / count)
					correlationValue = ----------------------------------------------------
														 sqrt(( sx2 - (sx*sx/count)) * ( sy2 - (sy*sy/count)))
				
		*/
		if ( recalc || Double.isNaN( pearsonCorrelation )) { //See if this value has already been calculated
			int S = 1, count = 0;
			String currentValueString0, currentValueString1;
			double currentValue0, currentValue1=0, sx=0, sy=0, sxy=0, sx2=0, sy2=0;

			// Get the sample data from the molecules.
			while ((( currentValueString0 = this.molecules[0].getAttribute( "S" + S )) != null )
							&& (( currentValueString1 = this.molecules[1].getAttribute( "S" + S++ )) != null )) {
				currentValue0 = Double.parseDouble( currentValueString0 );
				currentValue1 = Double.parseDouble( currentValueString1 );
				sx += currentValue0;
				sy += currentValue1;
				sxy += currentValue0 * currentValue1;
				sx2 += currentValue0 * currentValue0;
				sy2 += currentValue1 * currentValue1;
				}
			count = S - 2;

			// If there are 3 samples or less, the correlation is 0, else, use the equation.
			this.pearsonCorrelation = ( count <= 3 ) ? 0 :
				                        ( sxy - ( sx*sy / count )) / 
						                    ( Math.sqrt( sx2 - ( sx * sx / count )) * 
							                    ( sy2 - ( sy * sy / count )));
		}
		return this.pearsonCorrelation;

	}

	private double getSpearmanCorrelation( ) {
		return this.getSpearmanCorrelation( false );
	}
	private double getSpearmanCorrelation( boolean recalc ) {
		/*
			mol0 = sample values in molecule 0
			mol1 = sample values in molecule 1
			count = number of samples in each molecule
			sdata0 = sorted version of mol0
			sdata1 = sorted version of mol1

			                   
			correlationValue = ?
						             
			
		*/

		if ( recalc || Double.isNaN( this.spearmanCorrelation ) ) { //See if this value has already been calculated
			
			int S = 1, count = 0;
			String currentValueString0, currentValueString1;
			ArrayList <String> ValueList0 = new ArrayList <String>( ),
			                   ValueList1 = new ArrayList <String>( );
			double [ ] mol0, mol1, sdata0, sdata1;
//			int[ ] order0, order1;

			// Get the sample data from the molecules.
			while ((( currentValueString0 = this.molecules[0].getAttribute( "S" + S )) != null )
							&& (( currentValueString1 = this.molecules[1].getAttribute( "S" + S++ )) != null )) {
					ValueList0.add( currentValueString0 );
					ValueList1.add( currentValueString1 );
			}
			count = ValueList0.size( );
			mol0 = new double[ count ];
			mol1 = new double[ count ];
			sdata0 = new double[ count ];
			sdata1 = new double[ count ];
//			order0 = new int[ count ];
//			order1 = new int[ count ];

			// Convert and make 2 copies of the sample data.
			for ( int i=0; i < count; i++ ) {
				mol0[ i ] = sdata0[ i ] = Double.parseDouble( ValueList0.get( i ));
				mol1[ i ] = sdata1[ i ] = Double.parseDouble( ValueList1.get( i ));
			}
			// Sort one copy of each.
			Arrays.sort( sdata0 );
			Arrays.sort( sdata1 );

			double sd = 0;
			int tmp, tmp1;
			for ( int i=0; i < count; i++ ) {
//				order0[ i ] = arrayIndexOf( sdata0, mol0[ i ] );
//				order1[ i ] = arrayIndexOf( sdata1, mol1[ i ]);
				tmp = arrayIndexOf( sdata0, mol0[ i ]);
				tmp1 = arrayIndexOf( sdata1, mol1[ i ]);
				sdata0[ tmp ] = sdata1[ tmp1 ] = Double.NaN;
				tmp -= tmp1;
				sd += tmp*tmp;
			}
			this.spearmanCorrelation = 1 - ( 6*sd ) / ( count * ( count*count - 1 ));
		}
		return this.spearmanCorrelation;
	}

	private double getKendallCorrelation( ) {
		return this.getKendallCorrelation( false );
	}
	private double getKendallCorrelation( boolean recalc ) {
	/*
					mol0 = sample values in molecule 0
					mol1 = sample values in molecule 1
					count = number of samples in each molecule
					sdata0 = sorted version of mol0
					sdata1 = sorted version of mol1
					order0[n] = location of sdata0[n] in mol0
					order1[n] = location of sdata1[order0[n]] in mol1

					correlationValue = ?

	*/
		if ( recalc ||  Double.isNaN( this.kendallCorrelation )) { 

			int count, S = 1, tmp;
			String currentValueString0, currentValueString1;
			ArrayList <String> ValueList0 = new ArrayList <String>( ),
			                   ValueList1 = new ArrayList <String>( );
			double [ ] mol0, mol1, sdata0, sdata1;
			int[ ] order;
			int sc=0;

			// Get the sample data from the molecules.
			while ((( currentValueString0 = this.molecules[0].getAttribute( "S" + S )) != null )
							&& (( currentValueString1 = this.molecules[1].getAttribute( "S" + S++ )) != null )) {
					ValueList0.add( currentValueString0 );
					ValueList1.add( currentValueString1 );
			}
			count = ValueList0.size( );
			mol0 = new double[ count ];
			mol1 = new double[ count ];
			sdata0 = new double[ count ];
			sdata1 = new double[ count ];
			order = new int[ count ];

			// Convert and make 2 copies of the sample data.
			for ( int i=0; i < count; i++ ) {
				mol0[ i ] = sdata0[ i ] = Double.parseDouble( ValueList0.get( i ));
				mol1[ i ] = sdata1[ i ] = Double.parseDouble( ValueList1.get( i ));
			}
			// Sort one copy of each.
			Arrays.sort( sdata0 );
			Arrays.sort( sdata1 );
			/* Tough to explain this next part. Original C code:
				for(k = 0; k < sample_volume; k++)  
						{
					for(l = 0; l < sample_volume; l++)
							{
								if(dat1[k] == sdat1[l])
								{
									ord1[l] = k;			//sample k is ranked l in x
									sdat1[l] = 0;
									break;
								}
							}
						}

						for(k = 0; k < sample_volume; k++)	
						{
							for(l = 0; l < sample_volume; l++)
							{
								if(dat2[ord1[k]] == sdat2[l])
								{
									ord2[k] = l;			//sample that has a rank ord1[k] is ranked l in y
									sdat2[l] = 0;
									break;
								}
							}
						}
						double sd, sc;
						sd = 0;
						sc = 0;
						for(k = 1; k < sample_volume; k++)		//current
						{
							for(l = 0; l < k; l++)				//recorded
							{
								if(ord2[k] > ord2[l])
									sc++;
								else
									sd++;
							}
						}
			*/
			for ( int i=0; i < count; i++ ) {
				tmp = arrayIndexOf( mol0, sdata0[ i ] );
				order[ i ] = arrayIndexOf( sdata1, mol1[ tmp ]);
				sdata0[ i ] = sdata1[ order[ i ]] = Double.NaN;
				for ( int j=0; j < i; j++ )
				{
					// count the number of previous values which were less than the current.
					if  ( order[ j ] < order[ i ] ) {
						sc++;
					}
				}
			}
			/* The original formula: 
				cor_relation = (sc-sd)/(sample_volume*(sample_volume - 1)/2)	

				simplifying, using v for sample_volume
				if n = v - 1, then we can say
				sc + sd = 1 + 2 + 3 + ... + n = ( n*(n+1)/2 ) using 
				Gauss' formula for summing seqential integers from 1 to n.
				Therefore, sc + sd = v*(v-1)/2 and sd = v*(v-1)/2 - sc
				Substituting, we get
				(sc - ( v*(v-1)/2 - sc)) / ( v*(v-1)/2 )
				which in turn simplifies to:
				( 2*sc - ( v*(v-1)/2 )) / ( v*v-1)/2 )
				( 4*sc -  v*(v-1)) / ( v*(v-1) )
				( 4*sc ) / ( v*(v-1) ) - 1
			*/
			this.kendallCorrelation = ( 4.0*sc ) / ( count * ( count-1 )) - 1;
			
		}

		return this.kendallCorrelation;
	}

	private static int arrayIndexOf( double[ ] array, double value ) {
		for( int i=0,l=array.length; i < l; i++ ) {
				if ( array[ i ] == value ) {
					return i;
				}
		}
		return -1;
	}
}

