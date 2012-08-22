
import edu.purdue.cc.bionet.util.Sample;
import edu.purdue.cc.bionet.util.SampleComparator;

import java.util.TreeSet;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.*;

public class SampleComparatorTest extends TestCase {
  private Sample sample1;
  private Sample sample2;
  private Sample sample3;
  private Sample sample4;
  public void setUp( ) {
    sample1 = new Sample( "Sample1" );
    sample2 = new Sample( "Sample2" );
    sample3 = new Sample( "Sample3" );
    sample4 = new Sample( "Sample4" );

    // odds are time 0, evens are time 1.
    // odds are group 1, evens are group2.
    sample1.setAttribute( "time", "0" );
    sample2.setAttribute( "time", "1" );
    sample3.setAttribute( "time", "0" );
    sample4.setAttribute( "time", "1" );
  }

  public void testSampleOrder( ) {
    Collection<Sample> samples =  new TreeSet( new SampleComparator( ));

    samples.add( sample1 );
    samples.add( sample2 );
    samples.add( sample3 );
    samples.add( sample4 );

    Iterator<Sample> sampleIter = samples.iterator( );
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample1" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample3" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample2" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample4" ));

    samples.clear( );
    samples.add( sample3 );
    samples.add( sample2 );
    samples.add( sample4 );
    samples.add( sample1 );

    sampleIter = samples.iterator( );
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample1" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample3" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample2" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample4" ));
  }
}
