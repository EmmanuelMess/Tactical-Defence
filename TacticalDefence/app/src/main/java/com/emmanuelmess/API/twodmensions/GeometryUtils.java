package com.emmanuelmess.API.twodmensions;

import android.graphics.RectF;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.util.GeometricShapeFactory;
/**
 * @author Emmanuel
 *         on 2015-08-29, at 12:29.
 */
public class GeometryUtils {

	private static RectF r = new RectF();

	public static Geometry RectFtoPolygon(RectF r) {
		GeometricShapeFactory gsf = new GeometricShapeFactory();
		gsf.setBase(new Coordinate(r.left, r.top));
		gsf.setNumPoints(4);
		gsf.setWidth(r.width());
		gsf.setHeight(r.height());

		return gsf.createRectangle();
	}

}
