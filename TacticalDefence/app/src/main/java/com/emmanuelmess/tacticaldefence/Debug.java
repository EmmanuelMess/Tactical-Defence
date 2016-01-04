package com.emmanuelmess.tacticaldefence;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.emmanuelmess.API.Utils;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.inanimate.defences.Defence;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.players.AI;
import com.emmanuelmess.tacticaldefence.game.players.Player;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * This class contains debugging constants, use {@link TacticalDefence#isDebug()}
 *
 * @author Emmanuel
 *         on 2015-07-18, at 16:46.
 */
public class Debug {
	public static final int AI_MONEY = 1000;
	public static final long GAMEPLAY_TIME = 60000;
	public static final boolean NO_AI = false;
	public static final boolean DRAW_HITBOXES = false;

	private static Paint paint = new Paint(Color.argb(255, 255, 255, 255));

	public static void drawHitboxes(Canvas canvas) {
		if (TacticalDefence.isDebug()) {
			for (Piece p : Utils.merge(Player.getPieceGroup().getAll(), AI.getPieceGroup().getAll())) {
				canvas.drawCircle(p.getCoordinates()[0], p.getCoordinates()[1], p.height()/2f, paint);
			}

			for (Defence d : Game.defencesA)
				canvas.drawRect(d.getRectF(), paint);

			for (int i = 0; i < 2; i++) {
				Coordinate[] g = (i == 0? Game.getEnemyCastle():Game.getAllyCastle()).getGeometry().getCoordinates();
				Path pa = new Path();

				pa.moveTo((int) g[0].x, (int) g[0].y);

				for (Coordinate c : g) {
					pa.lineTo((int) c.x, (int) c.y);
				}

				pa.close();
				canvas.drawPath(pa, paint);

				pa.reset();
			}
		}
	}

	/*
	public static class Test {
		public static void run() {
			if(TacticalDefence.isDebug()) {
				//used for testing
			}
		}

	}
	*/
}
