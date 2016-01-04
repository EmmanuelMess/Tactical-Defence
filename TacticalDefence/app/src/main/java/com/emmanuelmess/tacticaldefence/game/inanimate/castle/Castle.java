package com.emmanuelmess.tacticaldefence.game.inanimate.castle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.API.Move;
import com.emmanuelmess.API.twodmensions.DrawHelper;
import com.emmanuelmess.API.twodmensions.ScaleBitmap;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.IO.audio.sonification.Sonification;
import com.emmanuelmess.tacticaldefence.game.inanimate.Arrow;
import com.emmanuelmess.tacticaldefence.game.inanimate.Helper;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.PieceGroup;
import com.emmanuelmess.tacticaldefence.game.players.AI;
import com.emmanuelmess.tacticaldefence.game.players.Player;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

import static com.emmanuelmess.API.twodmensions.GeometryUtils.RectFtoPolygon;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.game.inanimate.Arrow.TYPES;
import static com.emmanuelmess.tacticaldefence.game.inanimate.Arrow.getArrowVelocity;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.DEAD;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * @author Emmanuel
 *         2015-02-05, at 01:32 AM.
 */
public abstract class Castle {

	public static final int TOWER_RANGE = 100;
	public final int ARCHERS_RANGE = 75;
	protected final Chronometer[][] chr = new Chronometer[][]{
			{new Chronometer(), new Chronometer(), new Chronometer()},
			{new Chronometer(), new Chronometer(), new Chronometer(), new Chronometer(), new Chronometer()}
	};
	protected final PointF[][] attack = new PointF[][]{
			{new PointF(0, 0), new PointF(0, 0), new PointF(0, 0)},
			{new PointF(0, 0), new PointF(0, 0), new PointF(0, 0), new PointF(0, 0), new PointF(0, 0)}
	};
	private final RectF r = new RectF();
	private final Arrow arrow;
	private final Sonification a;
	protected int hp;
	protected int maxHP;
	protected PointF[] archers = new PointF[5];
	protected PointF[] towers = {new PointF(), new PointF(), new PointF()};
	protected RectF[] archersRange = {new RectF(), new RectF(), new RectF(), new RectF(), new RectF()};
	protected boolean[][] newArrow = {{true, true, true}, {true, true, true, true, true}};
	protected Geometry geometry;
	protected PreparedPolygon prepGeom;
	private Context context;

	protected Castle(Context context) {
		this.context = context;
		arrow = new Arrow(context);
		arrow.setArrowType(TYPES.SIMPLE);
		a = new Sonification(context);
	}

	public Bitmap create(boolean noHitbox, boolean isAlly, boolean[] offensiveConf, boolean[] defensiveConfig) {
		Bitmap drawableCastle;
		final RectF[] allyCastle = new RectF[2];
		allyCastle[0] = new RectF(0, DD.getHeight()/5f, (DD.getWidth()/10f)*2f, (DD.getHeight()/5f)*4f);
		float tower1 = allyCastle[0].height()/3f;
		allyCastle[1] = new RectF(0, tower1/4f, DD.getWidth()/2f - tower1, DD.getHeight() - tower1/4f);
		float w = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_walls_tower_ally).getHeight()/8f/2f;
		final RectF[][] rects = new RectF[][]{{allyCastle[0]},
				{new RectF(0, allyCastle[0].top - w, allyCastle[0].right + w, allyCastle[0].bottom + w),
						new RectF(allyCastle[0].right - allyCastle[0].height()/5f - w, allyCastle[0].top - allyCastle[0].height()/5f - w, allyCastle[0].right + allyCastle[0].height()/5f + w,
								allyCastle[0].top + allyCastle[0].height()/5f + w),
						new RectF(allyCastle[0].right - allyCastle[0].height()/5f - w, allyCastle[0].bottom - allyCastle[0].height()/5f - w,
								allyCastle[0].right + allyCastle[0].height()/5f + w, allyCastle[0].bottom + allyCastle[0].height()/5f + w)},
				{new RectF(allyCastle[0].right - allyCastle[0].height()/5f, allyCastle[0].top - allyCastle[0].height()/5f,
						allyCastle[0].right + allyCastle[0].height()/5f, allyCastle[0].top + allyCastle[0].height()/5f),
						new RectF(allyCastle[0].right - allyCastle[0].height()/5f, allyCastle[0].bottom - allyCastle[0].height()/5f,
								allyCastle[0].right + allyCastle[0].height()/5f, allyCastle[0].bottom + allyCastle[0].height()/5f)},
				null,
				{allyCastle[1],
						new RectF(allyCastle[1].left + allyCastle[1].centerX() - tower1, allyCastle[1].top - tower1/4f,
								allyCastle[1].left + allyCastle[1].centerX(), allyCastle[1].top + tower1/4f*3),
						new RectF(allyCastle[1].left + allyCastle[1].centerX() - tower1, allyCastle[1].bottom - tower1/4f*3,
								allyCastle[1].left + allyCastle[1].centerX(), allyCastle[1].bottom + tower1/4f),
						new RectF(allyCastle[1].right - tower1, allyCastle[1].centerY() - tower1/2f, allyCastle[1].right, allyCastle[1].centerY() + tower1/2f)},
				null};
		float wall = rects[1][1].width() - rects[2][0].width();

		rects[5] = new RectF[]{new RectF(allyCastle[0].right - allyCastle[0].height()/5f, allyCastle[0].top - allyCastle[0].height()/5f,
				allyCastle[0].right + allyCastle[0].height()/5f - wall*2, allyCastle[0].top + allyCastle[0].height()/5f),
				new RectF(allyCastle[0].right - allyCastle[0].height()/5f - w, allyCastle[0].top - allyCastle[0].height()/5f - w,
						allyCastle[0].right + allyCastle[0].height()/5f + w - wall*2, allyCastle[0].top + allyCastle[0].height()/5f + w)};
		rects[3] = new RectF[]{new RectF(0, rects[0][0].centerY() - rects[5][0].height()/2f, rects[5][0].left,
				rects[0][0].centerY() + rects[5][0].height()/2f),
				new RectF(0, rects[0][0].centerY() - rects[5][1].height()/2f, rects[5][1].left,
						rects[0][0].centerY() + rects[5][1].height()/2f)};

		double[][] castleInt = new double[][]{{R.drawable.game_castle_ally, rects[0][0].width(), rects[0][0].height()},
				{R.drawable.game_castle_walls_ally, rects[1][0].width(), rects[1][0].height()},
				{R.drawable.game_castle_tower_ally, rects[2][0].width(), rects[2][0].height()},
				{R.drawable.game_castle_walls_tower_ally, rects[1][1].width(), rects[1][1].height()},
				{R.drawable.game_castle_rampart_ally, rects[4][0].width(), rects[4][0].height()},
				{R.drawable.game_castle_rampart_walls_ally, rects[4][0].width(), rects[4][0].height()},
				{R.drawable.game_castle_tower_ally, rects[4][1].width(), rects[4][1].height()},
				{R.drawable.game_castle_walls_tower_ally, rects[4][2].width(), rects[4][2].height()},
				{R.drawable.game_castle_tower_ii_ally, rects[5][0].width(), rects[5][0].height()},
				{R.drawable.game_castle_walls_tower_ii_ally, rects[5][1].width(), rects[5][1].height()},
		};
		Bitmap[] castle = new Bitmap[castleInt.length];

		/*archers*/
		if (offensiveConf[1]) {
			if (!defensiveConfig[2]) {
				float x = rects[0][0].right - 20;

				for (int i = 0; i < archers.length; i++)
					archers[i] = new PointF(x, DD.getHeight()/2f - 40 + 20*i);//todo test
			} else {
				archers = new PointF[]{new PointF(rects[4][0].width()/4f, rects[4][0].centerY())};
				/*//TODO
				for (int i = 0; i < r.length - 1; i++)  {
					archers[i][1] = rects[0][0].top + heightBarrier*(3.5f + i) + 5*i;

					r[i].set(archers[i][0], archers[i][0] - 25, archers[i][0] + 100, archers[i][1] + 25);
				}
				*/
			}
		}


		for (int i = 0; i < castleInt.length; i++) {
			castle[i] = ScaleBitmap.getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), (int) castleInt[i][0]), (float) castleInt[i][1], (float) castleInt[i][2]);
		}

		Canvas comboImage;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
		float towerWall = (castle[7].getWidth() - castle[6].getWidth())/2f;

		if (!defensiveConfig[2]) {
			drawableCastle = Bitmap.createBitmap((int) Math.ceil(rects[1][2].right), DD.getHeight(), Bitmap.Config.ARGB_8888);
			comboImage = new Canvas(drawableCastle);

			if (!defensiveConfig[0])
				comboImage.drawBitmap(castle[0], rects[0][0].left, rects[0][0].top, paint);
			else comboImage.drawBitmap(castle[1], rects[1][0].left, rects[1][0].top, paint);

			if (offensiveConf[0]) {
				if (!defensiveConfig[0]) {
					comboImage.drawBitmap(castle[2], rects[2][0].left, rects[2][0].top, paint);
					comboImage.drawBitmap(castle[2], rects[2][1].left, rects[2][1].top, paint);
				} else {
					comboImage.drawBitmap(castle[3], rects[1][1].left, rects[1][1].top, paint);
					comboImage.drawBitmap(castle[3], rects[1][2].left, rects[1][2].top, paint);
				}
			}
			if (offensiveConf[2]) {
				if (!defensiveConfig[0])
					comboImage.drawBitmap(castle[8], rects[3][0].left, rects[3][0].top, paint);
				else comboImage.drawBitmap(castle[9], rects[3][1].left, rects[3][1].top, paint);
			}
		} else {
			drawableCastle = Bitmap.createBitmap((int) Math.ceil(rects[4][0].right), DD.getHeight(), Bitmap.Config.ARGB_8888);
			comboImage = new Canvas(drawableCastle);

			if (!defensiveConfig[0])
				comboImage.drawBitmap(castle[4], rects[4][0].left, rects[4][0].top, paint);
			else comboImage.drawBitmap(castle[5], rects[4][0].left, rects[4][0].top, paint);

			if (offensiveConf[0]) {
				if (!defensiveConfig[0]) {
					comboImage.drawBitmap(castle[6], rects[4][1].left, rects[4][1].top, paint);
					comboImage.drawBitmap(castle[6], rects[4][2].left, rects[4][2].top, paint);
				} else {
					comboImage.drawBitmap(castle[7], rects[4][1].left - towerWall, rects[4][1].top - towerWall, paint);
					comboImage.drawBitmap(castle[7], rects[4][2].left - towerWall, rects[4][2].top - towerWall, paint);
				}
			}
			if (offensiveConf[2]) {
				if (!defensiveConfig[0])
					comboImage.drawBitmap(castle[6], rects[4][3].left, rects[4][3].top, paint);
				else
					comboImage.drawBitmap(castle[7], rects[4][3].left - towerWall, rects[4][3].top - towerWall, paint);
			}
		}

		if (offensiveConf[1]) {
			paint.setColor(Color.BLACK);

			for (PointF archer : archers)
				comboImage.drawCircle(archer.x, archer.y, 5, paint);
		}

		//comboImage.drawLine(0,0,DD.getHeight(), DD.getWidth(), paint);

		if (!isAlly) {
			drawableCastle = DrawHelper.replaceColorBlueRed(context, drawableCastle);

			Matrix matrix = new Matrix();
			matrix.postRotate(180);
			drawableCastle = Bitmap.createBitmap(drawableCastle, 0, 0, drawableCastle.getWidth(), drawableCastle.getHeight(), matrix, true);
		}

		if (!noHitbox) {
			/*attack*/
			{
				/*towers*/
				{
					if (!defensiveConfig[2]) {
						towers[0].set(allyCastle[0].right, allyCastle[0].top);
						towers[1].set(allyCastle[0].right, allyCastle[0].bottom);
						towers[2].set(rects[3][0].right, rects[3][0].centerY());
					} else {
						towers[0].set(rects[4][1].centerX(), rects[4][1].centerY());
						towers[1].set(rects[4][2].centerX(), rects[4][2].centerY());
						towers[2].set(rects[4][3].centerX(), rects[4][3].centerY());
					}

					if (!isAlly)
						for (PointF p : towers)
							p.set(DD.getWidth() - p.x, p.y);
				}
				/*archers*/
				{
					if (offensiveConf[1]) {
						if (!isAlly)
							for (PointF archer : archers)
								archer.set(DD.getWidth() - archer.x, archer.y);

						for (int i = 0; i < archers.length; i++) {
							archersRange[i] = new RectF(archers[i].x - ARCHERS_RANGE, archers[i].y - ARCHERS_RANGE, archers[i].x + ARCHERS_RANGE, archers[i].y + ARCHERS_RANGE);
						}
					}
				}
			}

			/*hitbox*/
			{
				if (!defensiveConfig[2]) {
					geometry = RectFtoPolygon(rects[0][0]);
					if (offensiveConf[0]) {
						if (!defensiveConfig[0]) {
							geometry = GRU(geometry, rects[2][0]);
							geometry = GRU(geometry, rects[2][1]);
						} else {
							geometry = GRU(geometry, rects[1][1]);
							geometry = GRU(geometry, rects[1][2]);
						}
					}
				} else {
					geometry = new GeometryFactory()
							.createPolygon(new Coordinate[]{
									new Coordinate(0, rects[4][0].top),
									new Coordinate(rects[4][1].centerX(), rects[4][0].top),
									new Coordinate(rects[4][0].right, rects[4][0].centerY()),
									new Coordinate(rects[4][2].centerX(), rects[4][0].bottom),
									new Coordinate(0, rects[4][0].bottom),
									new Coordinate(0, rects[4][0].top)
							});
					if (offensiveConf[0]) {
						geometry = GRU(geometry, rects[4][1]);
						geometry = GRU(geometry, rects[4][2]);
					}
					if (offensiveConf[2])
						geometry = GRU(geometry, rects[4][3]);
				}

				if (!isAlly) {
					Coordinate[] c = geometry.getCoordinates();
					for (int i = 0; i < c.length; i++)
						c[i] = new Coordinate(DD.getWidth() - c[i].x, c[i].y);
					geometry = new GeometryFactory().createPolygon(c);
				}

				prepGeom = (PreparedPolygon) new PreparedGeometryFactory().create(geometry);

				//Debug.Test.run();
			}
		} else {
			archers = new PointF[5];
		}

		return drawableCastle;
	}

	public PreparedPolygon getPreparedGeometry() {
		return prepGeom;
	}

	public Geometry getGeometry() {
		return geometry;
	}


	public void reconstruct() {
		hp = maxHP;
	}

	public int getDamage(CastleUpgrades.OFFENSIVE_UPGRADES t) {
		Arrow.TYPES y;
		switch (t) {
			case TOWERS:
				y = Arrow.TYPES.TOWER;
				break;
			case ARCHERS:
				y = Arrow.TYPES.ARCHER;
				break;
			default:
				throw new IllegalArgumentException();
		}
		return Arrow.getArrowDamage(y);
	}

	protected void attack(Canvas canvas, boolean[] offensiveConf, boolean isAlly) {
		PieceGroup pg = isAlly? AI.getPieceGroup():Player.getPieceGroup();

		if (offensiveConf[0] || offensiveConf[2]) {
			for (int i = 0; i < towers.length; i++) {
				if ((!offensiveConf[0] && (i == 0 || i == 1)) || (!offensiveConf[2] && i == 2))
					continue;

				Piece pi = getNearestEnemy(TOWER_RANGE, isAlly, towers[i]);

				if (pi != null && (!newArrow[0][i] || pi.getState() != DEAD)) {
					if (newArrow[0][i]) {
						attack[0][i].set(pi.getCoordinates()[0], pi.getCoordinates()[1]);
						newArrow[0][i] = false;
					}

					Piece p = pg.getPieceByCoordinates(attack[0][i].x, attack[0][i].y);

					if (attackCore(chr[0][i], canvas, towers[i].x, towers[i].y, attack[0][i].x, attack[0][i].y)) {
						if (p != null)
							p.isAttacked(this, CastleUpgrades.OFFENSIVE_UPGRADES.TOWERS);
						newArrow[0][i] = true;
					}
				}
			}
		}

		if (offensiveConf[1]) {
			for (int i = 0; i < archers.length; i++) {
				Piece pi = Helper.getNearestEnemy(Helper.searchForPiecesInRectF(archersRange[i], pg), pg, archers[i].x, archers[i].y);

				if (pi != null && (!newArrow[1][i] || pi.getState() != DEAD)) {
					if (newArrow[1][i]) {
						attack[1][i].set(pi.getCoordinates()[0], pi.getCoordinates()[1]);
						newArrow[1][i] = false;
					}

					Piece p = pg.getPieceByCoordinates(attack[1][i].x, attack[1][i].y);

					if (attackCore(chr[1][i], canvas, archers[i].x, archers[i].y, attack[1][i].x, attack[1][i].y)) {
						if (p != null)
							p.isAttacked(this, CastleUpgrades.OFFENSIVE_UPGRADES.ARCHERS);
						newArrow[1][i] = true;
					}
				}
			}
		}
	}


	private float[][] getEnemies(float h, boolean isAlly, PointF p) {// TODO: 2015-10-12 lookout in circle, not in rect!
		r.set(p.x - h, p.y - h, p.x + h, p.y + h);

		return Helper.searchForPiecesInRectF(r, isAlly? AI.getPieceGroup():Player.getPieceGroup());
	}


	protected Piece getNearestEnemy(float h, boolean isAlly, PointF p) {
		float[][] e = getEnemies(h, isAlly, p);
		return Helper.getNearestEnemy(e, isAlly? AI.getPieceGroup():Player.getPieceGroup(), p.x, p.y);
	}

	protected boolean attackCore(Chronometer cr, Canvas canvas, float x1, float y1, float x2, float y2) {// TODO: 2015-10-12 make general method for all!
		if (!cr.hasStarted()) {
			cr.start();

			a.shoot(x1*100/DD.getWidth());
		}

		if (Game.isPaused) cr.pause();
		else if (cr.isPaused()) cr.resume();

		double d = cr.getElapsedTime()*getArrowVelocity(TYPES.TOWER)/1000f;

		float[] c = Move.move(x1, y1, x2, y2, d);

		if (d >= sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2))) {
			arrow.drawArrow((float) Helper.angle(x1, y1, x2, y2), x2, y2, canvas);
			cr.stop();

			a.impacted(x2*100/DD.getWidth());
			return true;
		}
		arrow.drawArrow((float) Helper.angle(x1, y1, x2, y2), c[0], c[1], canvas);

		return false;
	}

	private Geometry GRU(Geometry geo, RectF r) {
		return geo.union(RectFtoPolygon(r));
	}

}
