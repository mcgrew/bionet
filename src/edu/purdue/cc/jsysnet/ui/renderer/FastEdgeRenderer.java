
/*

This file is part of JSysNet.

JSysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JSysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JSysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.jsysnet.ui.renderer;

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

public class FastEdgeRenderer<V,E> extends BasicEdgeRenderer<V,E> {

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
			p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
			p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
			int x1 = (int) p1.getX();
			int y1 = (int) p1.getY();
			int x2 = (int) p2.getX();
			int y2 = (int) p2.getY();
			g.drawLine( x1, y1, x2, y2 );
		}
		g.setPaint( oldPaint );
	}
}

