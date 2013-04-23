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


import edu.purdue.bbc.util.SimplePair;
import edu.purdue.bbc.util.Pair;
import edu.purdue.cc.bionet.util.Molecule;
import edu.purdue.cc.bionet.util.Sample;
import edu.purdue.cc.bionet.util.SampleGroup;
import edu.purdue.cc.bionet.ui.CorrelationDisplayPanel;

import java.util.TreeSet;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.*;

public class SampleTest extends TestCase {
  private Sample sample1;
  private Sample sample2;
  private Sample sample3;
  private Sample sample4;
  private Molecule molecule1;
  private Molecule molecule2;
  private Molecule molecule3;
  private Molecule molecule4;

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

    molecule1 = new Molecule( "Molecule1" );
    molecule2 = new Molecule( "Molecule2" );
    molecule3 = new Molecule( "Molecule3" );
    molecule4 = new Molecule( "Molecule4" );
  }

  public void testSampleOrder( ) {
    Collection<Sample> samples =  new TreeSet( );

    samples.add( sample1 );
    samples.add( sample2 );
    samples.add( sample3 );
    samples.add( sample4 );

    Iterator<Sample> sampleIter = samples.iterator( );
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample1" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample2" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample3" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample4" ));

    samples.clear( );
    samples.add( sample3 );
    samples.add( sample2 );
    samples.add( sample4 );
    samples.add( sample1 );

    sampleIter = samples.iterator( );
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample1" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample2" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample3" ));
    assertTrue( sampleIter.next( ).toString( ).equals( "Sample4" ));
  }

  public void testFold( ) {
    Pair<SampleGroup> sampleGroups = 
      new SimplePair( new SampleGroup( "Group 1" ),
                      new SampleGroup( "Group 2" ));

    sampleGroups.getFirst( ).add( sample1 );
    sampleGroups.getFirst( ).add( sample3 );
    sampleGroups.getSecond( ).add( sample2 );
    sampleGroups.getSecond( ).add( sample4 );

    sample1.setValue( molecule1, 1.0 );
    sample3.setValue( molecule1, 1.0 );
    sample2.setValue( molecule1, 1000.0 );
    sample4.setValue( molecule1, 1000.0 );
    assertTrue( CorrelationDisplayPanel.getRegulation( molecule1, sampleGroups, 2.0, false ) > 0 );

    sample1.setValue( molecule2, 1000.0 );
    sample3.setValue( molecule2, 1000.0 );
    sample2.setValue( molecule2, 1.0 );
    sample4.setValue( molecule2, 1.0 );
    assertTrue( CorrelationDisplayPanel.getRegulation( molecule2, sampleGroups, 2.0, false ) < 0 );
  }

  public void testEmptyGroupFold( ) {
    Pair<SampleGroup> sampleGroups = 
      new SimplePair( new SampleGroup( "Group 1" ),
                      new SampleGroup( "Group 2" ));

    sampleGroups.getFirst( ).add( sample1 );
    sampleGroups.getFirst( ).add( sample3 );
    sampleGroups.getSecond( ).add( sample2 );
    sampleGroups.getSecond( ).add( sample4 );

    sample1.setValue( molecule3, 0.0 );
    sample3.setValue( molecule3, 0.0 );
    sample2.setValue( molecule3, 1000.0 );
    sample4.setValue( molecule3, 1000.0 );
    // test when all samples in group 1 are 0;
    assertTrue( CorrelationDisplayPanel.getRegulation( molecule3, sampleGroups, 2.0, false ) > 0 );

    sample1.setValue( molecule4, 1000.0 );
    sample3.setValue( molecule4, 1000.0 );
    sample2.setValue( molecule4, 0.0 );
    sample4.setValue( molecule4, 0.0 );
    // test when all samples in group 2 are 0;
    assertTrue( CorrelationDisplayPanel.getRegulation( molecule4, sampleGroups, 2.0, false ) < 0 );

    sample1.setValue( molecule3, 4.0 );
    sample3.setValue( molecule3, 4.0 );
    sample2.setValue( molecule3, 6.0 );
    sample4.setValue( molecule3, 6.0 );
    assertEquals( CorrelationDisplayPanel.getRegulation( molecule3, sampleGroups, 2.0, false ), 0 );

    sample1.setValue( molecule2, 4.0 );
    sample3.setValue( molecule2, 4.0 );
    sample2.setValue( molecule2, 6.0 );
    sample4.setValue( molecule2, 6.0 );
    assertEquals( CorrelationDisplayPanel.getRegulation( molecule2, sampleGroups, 2.0, false ), 0 );
  }
  public void testLogarithmicFold( ) {
    Pair<SampleGroup> sampleGroups = 
      new SimplePair( new SampleGroup( "Group 1" ),
                      new SampleGroup( "Group 2" ));

    sampleGroups.getFirst( ).add( sample1 );
    sampleGroups.getFirst( ).add( sample3 );
    sampleGroups.getSecond( ).add( sample2 );
    sampleGroups.getSecond( ).add( sample4 );

    sample1.setValue( molecule1, 4.0 );
    sample3.setValue( molecule1, 4.0 );
    sample2.setValue( molecule1, 6.0 );
    sample4.setValue( molecule1, 6.0 );
    assertTrue( CorrelationDisplayPanel.getRegulation( molecule1, sampleGroups, 2.0, true ) > 0 );

    sample1.setValue( molecule2, 6.0 );
    sample3.setValue( molecule2, 6.0 );
    sample2.setValue( molecule2, 4.0 );
    sample4.setValue( molecule2, 4.0 );
    assertTrue( CorrelationDisplayPanel.getRegulation( molecule2, sampleGroups, 2.0, true ) < 0 );
  }
}
