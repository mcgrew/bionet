/*

This file is part of BioNet.

BioNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BioNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with BioNet.  If not, see <http://www.gnu.org/licenses/>.

*/


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
