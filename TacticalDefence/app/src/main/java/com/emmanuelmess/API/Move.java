package com.emmanuelmess.API;

import com.emmanuelmess.tacticaldefence.game.Game;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * @author Emmanuel
 *         on 2015-02-13, at 09:33 AM.
 */
public class Move {

	private final Chronometer chr;
	private float[] m = new float[5];


	public Move() {
		chr = new Chronometer();
	}

	public static float[] move(float xA, float yA, float xB, float yB, double dAC) {
		double dAB = sqrt(pow(xA - xB, 2) + pow(yA - yB, 2));

		double xC = xA + (xB - xA)*dAC/dAB;
		double yC = yA + (yB - yA)*dAC/dAB;

		return new float[]{(float) xC, (float) yC};
	}

	public Chronometer getChr() {
		return chr;
	}

	public void setV(float v) {
		m[4] = v;
	}

	private void setCoords(float x1, float y1, float x2, float y2) {
		m = new float[]{x1 == -1? m[0]:x1, y1 == -1? m[1]:y1, x2 == -1? m[2]:x2, y2 == -1? m[3]:y2, m[4]};
	}

	private float[] getCoords() {
		return m;
	}

	public float[] movement() {
		if (!chr.hasStarted()) chr.start();

		if (Game.isPaused) chr.pause();
		else if (chr.isPaused()) chr.resume();

		double d = chr.getElapsedTime()*m[4]/1000f;

		float[] c = Move.move(m[0], m[1], m[2], m[3], d);

		setCoords(c[0], c[1], c[2], c[3]);

		return getCoords();
	}

	private float getMovedDistance() {
		return chr.getElapsedTime()*m[4]/1000f;
	}

	private double getDistance() {
		return sqrt(pow(m[0] - m[2], 2) + pow(m[1] - m[3], 2));
	}

	public boolean isStopped() {
		return getMovedDistance() >= getDistance();
	}
}
