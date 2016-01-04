package com.emmanuelmess.tacticaldefence.game.inanimate;

import android.graphics.RectF;

import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.PieceGroup;

import java.util.ArrayList;
import java.util.List;

import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.DEAD;
import static java.lang.Math.atan2;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

/**
 * @author Emmanuel
 *         on 2015-03-02, at 12:51 PM.
 */
public class Helper {

	public static float[][] searchForPiecesInRectF(RectF r, PieceGroup group) {
		List<float[]> f = new ArrayList<>();

		for (Piece piece : group.getAll()) {
			if (piece != null && piece.getState() != DEAD
					&& (r.contains(piece.getCoordinates()[0] - piece.getBitmap().getWidth()/2, piece.getCoordinates()[1] - piece.getBitmap().getHeight()/2)
					|| r.contains(piece.getCoordinates()[0] + piece.getBitmap().getWidth()/2, piece.getCoordinates()[1] - piece.getBitmap().getHeight()/2)
					|| r.contains(piece.getCoordinates()[0] + piece.getBitmap().getWidth()/2, piece.getCoordinates()[1] + piece.getBitmap().getHeight()/2)
					|| r.contains(piece.getCoordinates()[0] - piece.getBitmap().getWidth()/2, piece.getCoordinates()[1] - piece.getBitmap().getHeight()/2)))
				f.add(piece.getCoordinates());
		}

		return f.toArray(new float[f.size()][2]);
	}

	public static Piece getNearestEnemy(RectF r, PieceGroup search, float x, float y) {
		float[][] e = searchForPiecesInRectF(r, search);
		double f = 0;
		float[] d = null;

		for (float[] anE : e) {
			if (f < sqrt(pow(x - anE[0], 2) + pow(y - anE[1], 2))) {
				f = sqrt(pow(x - anE[0], 2) + pow(y - anE[1], 2));
				d = anE;
			}
		}

		if (d == null) return null;
		else return search.getPieceByCoordinates(d[0], d[1]);
	}

	public static Piece getNearestEnemy(float e[][], PieceGroup search, float x, float y) {
		double f = 0;
		float[] d = null;

		for (float[] anE : e) {
			if (f < sqrt(pow(x - anE[0], 2) + pow(y - anE[1], 2))) {
				f = sqrt(pow(x - anE[0], 2) + pow(y - anE[1], 2));
				d = anE;
			}
		}

		if (d == null) return null;
		else return search.getPieceByCoordinates(d[0], d[1]);
	}

	public static double angle(float x1, float y1, float x2, float y2) {
		return toDegrees(atan2(y2 - y1, x2 - x1));
	}


}
