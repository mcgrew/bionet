
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

package edu.purdue.cc.bionet.ui.renderer;

import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.GraphDecorator;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.graph.Graph;

/**
 * A class for rendering edges in the JUNG network graph. This renderer
 * is faster than the default edge renderer, which improves performance,
 * but it is less flexible.
 */
public class FastEdgeRenderer<V,E> extends BasicEdgeRenderer<V,E> {

	/**
	 * i Draws the edge e, whose endpoints are at (x1,y1) and (x2,y2), on the 
	 * graphics context.
	 * 
	 * @param rc The RenderContext to use.
	 * @param layout The Layout of the Graph.
	 * @param e The edge to be drawn.
	 */
	protected void drawSimpleEdge(RenderContext<V,E> rc, Layout<V,E> layout, E e) {
		GraphicsDecorator g = rc.getGraphicsContext();
		Paint oldPaint = g.getPaint( );
		Graph<V,E> graph = layout.getGraph();
		Pair<V> endpoints = graph.getEndpoints(e);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();
		boolean isLoop = v1.equals(v2);
		if ( isLoop ) {
			super.drawSimpleEdge( rc, layout, e );
		} else {
			g.setPaint( rc.getEdgeDrawPaintTransformer( ).transform( e ));
			Point2D p1 = layout.transform(v1);
			Point2D p2 = layout.transform(v2);
			g.drawLine( (int)p1.getX( ), (int)p1.getY( ), 
                  (int)p2.getX( ), (int)p2.getY( ));
		}
		g.setPaint( oldPaint );
	}
}

