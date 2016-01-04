package com.emmanuelmess.tacticaldefence.game.inanimate.pieces;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.HealerPiece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters.FighterPiece;

import java.util.ArrayList;
import java.util.List;

import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.ACTIONING;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.DEAD;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.SELECTED;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.UNSELECTED;

public class PieceGroup {

	private final List<Piece> pa = new ArrayList<>();
	private ArrayList<Piece> dead = new ArrayList<>();
	private int selected = -1, healers = 0;

	@SuppressWarnings("UnusedReturnValue")
	public int add(@NonNull Piece p) {
		pa.add(p);
		if (p instanceof HealerPiece) healers++;
		return pa.size() - 1;
	}

	public Piece[] getAll() {
		Piece[] pi = new Piece[pa.size()];
		pi = pa.toArray(pi);
		return pi;
	}

	public void setAllUnselected() {
		if (getPieceByIdentifier(selected) != null && getPieceByIdentifier(selected).getState() != DEAD && getPieceByIdentifier(selected).getState() != ACTIONING)
			getPieceByIdentifier(selected).setState(UNSELECTED);
		selected = -1;
	}

	public Piece[] getAllUnselected() {
		Piece[] p = selected == -1? new Piece[pa.size()]:new Piece[pa.size() - 1];
		boolean res = false;

		if (pa.get(0) == null) return p;
		else {
			for (int f = 0; f < pa.size(); f++) {
				if (f == selected) res = true;
				else {
					if (!res) p[f] = pa.get(f);
					else p[f - 1] = pa.get(f);
				}
			}
		}

		return p;
	}

	private void setState(int identifier, Piece.state state) {
		getPieceByIdentifier(identifier).setState(state);

		if (state == SELECTED) setSelected(identifier);
	}

	public int getSelected() {
		int i = 0;
		for (Piece p : getAll()) {
			if (p != null && p.getState() != DEAD)
				setState(i, p.getState());
			i++;
		}
		return selected;
	}

	public void setSelected(int selecte) {
		if (pa.get(selecte) == null) throw new IllegalArgumentException();
		selected = selecte;
		setOthersToUnselected(selected);
	}

	public int getID(Piece p) {
		for (int i = 0; i < pa.size(); i++)
			if (pa.get(i).equals(p)) return i;
		return -1;
	}

	public Piece getPieceByIdentifier(int identifier) {
		if (pa.size() <= identifier || identifier == -1) return null;
		else return pa.get(identifier);
	}

	public Piece getPieceByCoordinates(float x, float y) {
		for (Piece p : getAll()) {
			if (p != null && p.getState() != DEAD && p.isBitmapSelectedByCoordinates(x, y))
				return p;
		}
		return null;
	}

	private void setOthersToUnselected(int identifier) {
		for (Piece p : getAll()) {
			if (p != null && p.getState() != DEAD && p.getState() != ACTIONING && p != getPieceByIdentifier(identifier))
				p.setState(UNSELECTED);
		}
	}

	public void drawAlive(Canvas canvas) {//HealerPieces gets drawn in front so that healing animation is atop healed
		for (Piece p : getAll())
			if (p != null && p.getState() != DEAD && !(p instanceof HealerPiece)) p.draw(canvas);
		for (Piece p : getAll())
			if (p != null && p.getState() != DEAD && p instanceof HealerPiece) p.draw(canvas);
	}

	public void drawAction(Canvas canvas) {
		for (Piece p : getAll())
			//if(p.getState() == ACTIONING) {
			if (p instanceof FighterPiece)
				((FighterPiece) p).attack(canvas);
			else if (p instanceof HealerPiece)
				((HealerPiece) p).heal();
		//}
	}

	public int getAmountHealers() {
		int deaths = 0;
		for (Piece dead : getAmountDead())
			if (dead != null && dead.getState() == DEAD && dead instanceof HealerPiece)
				deaths++;

		return healers - deaths;
	}

	public ArrayList<Piece> getAmountDead() {
		int length = 0;

		for (Piece p : getAll()) {
			if (p != null && p.getState() == DEAD) {
				length++;
				if (length > dead.size()) dead.add(p);
			}
		}

		return dead;
	}

}
