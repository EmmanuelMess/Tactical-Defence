package com.emmanuelmess.tacticaldefence.game.inanimate.defences.types;

import android.content.Context;

import com.emmanuelmess.tacticaldefence.game.inanimate.defences.Defence;

/**
 * @author Emmanuel
 *         on 2015-06-29, at 22:16.
 */
public class BarricadeDefence extends Defence {

	public BarricadeDefence(Context context, float x1, float y1) {
		super(context, x1, y1, Types.TYPES.BARRICADE);
	}

}
