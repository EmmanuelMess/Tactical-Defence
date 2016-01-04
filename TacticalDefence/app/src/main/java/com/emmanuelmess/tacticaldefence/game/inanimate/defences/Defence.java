package com.emmanuelmess.tacticaldefence.game.inanimate.defences;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.emmanuelmess.API.twodmensions.GeometryUtils;
import com.emmanuelmess.tacticaldefence.R;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Random;
/**
 * @author Emmanuel
 *         on 2015-07-03, at 21:21.
 */
public abstract class Defence {

	Bitmap b;
	float[] c = new float[2];
	int maxHP = 100, HP = 100;
	RectF r;
	Geometry g;

	public Defence(Context context, float x1, float y1, Types.TYPES t) {
		b = Types.createImage(context.getResources(), t);
		c[0] = x1;
		c[1] = y1;
		r = new RectF(x1 - b.getWidth()/2f, y1 - b.getHeight()/2f, x1 + b.getWidth()/2f, y1 + b.getHeight()/2f);
		g = GeometryUtils.RectFtoPolygon(r);
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(b, c[0] - b.getWidth()/2f, c[1] - b.getHeight()/2f, null);
	}

	public boolean setDamage(int d) {
		HP -= d;
		HP = HP < 0? 0:HP;
		return HP == 0;
	}

	public Geometry getGeometry() {
		return g;
	}

	public RectF getRectF() {
		return r;
	}

	public static class Types {

		public static int getPrice(TYPES t) {
			switch (t) {
				case BARRICADE:
					return 125;

				default:
					throw new IllegalArgumentException("Non-existent defence!");
			}
		}

		public static Bitmap createImage(Resources res, TYPES type) throws IllegalArgumentException {
			Bitmap cs;
			Bitmap[] b;
			int height;
			Random rand = new Random();

			switch (type) {
				case BARRICADE:
					b = new Bitmap[]{BitmapFactory.decodeResource(res, R.drawable.game_barricade_1),
							BitmapFactory.decodeResource(res, R.drawable.game_barricade_2),
							BitmapFactory.decodeResource(res, R.drawable.game_barricade_3),
							BitmapFactory.decodeResource(res, R.drawable.game_barricade_4)};
					height = 6;
					break;

				default:
					throw new IllegalArgumentException("Non-existent type");
			}

			cs = Bitmap.createBitmap(b[0].getWidth(), b[0].getHeight()*height, Bitmap.Config.ARGB_8888);

			Canvas comboImage = new Canvas(cs);

			for (int i = 0; i < height; i++)
				comboImage.drawBitmap(b[rand.nextInt(b.length - 1)], 0f, b[0].getHeight()*i, null);

			return cs;
		}

		public enum TYPES {BARRICADE}

	}

}
