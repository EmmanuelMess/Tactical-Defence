package com.emmanuelmess.tacticaldefence.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.tacticaldefence.Debug;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.TacticalDefence;
import com.emmanuelmess.tacticaldefence.activities.CreditsActivity;
import com.emmanuelmess.tacticaldefence.activities.game.GameActivity;
import com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity;
import com.emmanuelmess.tacticaldefence.game.IO.saving.FileIOMemory;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.AICastle;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.PlayerCastle;
import com.emmanuelmess.tacticaldefence.game.inanimate.defences.Defence;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.PowerUp;
import com.emmanuelmess.tacticaldefence.game.players.AI;
import com.emmanuelmess.tacticaldefence.game.players.Player;
import com.emmanuelmess.tacticaldefence.game.render.RenderBirthdayPresent;
import com.emmanuelmess.tacticaldefence.game.render.RenderTutorial;
import com.google.android.gms.games.Games;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.acra.ACRA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static com.emmanuelmess.API.twodmensions.Contain.isContainedInRectF;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.activities.game.GameActivity.gamePlayTime;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.TYPES.values;

/**
 * @author Emmanuel
 *         2015-01-25, at 01:52 PM.
 */
public class Game {

	public static final GeometryFactory GF = new GeometryFactory();

	public static RectF frame;
	public static int selected = -1;
	public static boolean showMoveAction, isMove, isAction;
	public static boolean won, lost;
	public static int money;
	public static boolean isPaused;
	public static RectF moveR, actionR;
	public static Defence[] defencesA;
	public static Bitmap[] piecesBitmap;
	private static boolean showMenu;
	private static PlayerCastle castle;
	private static AICastle aiCastle;
	private static int whatToShow = 0;
	private static List<Piece> deadPieces;
	private static List<Defence> defences;
	private static int level;
	private final Context context;
	private final Bitmap buttonFrameUp;
	private final Frame frameO;
	private final Paint paint = new Paint();
	private final Bitmap menuArrow;
	private final Bitmap[][] items;
	private final float[] buttonCoordinates;
	private final Bitmap pauseButton;
	private final Rect boundsPoints = new Rect();
	private final Rect[] pauseT = new Rect[]{new Rect(), new Rect(), new Rect()};
	private final Bitmap[] move, attack, heal;
	private final Chronometer redMoneyC = new Chronometer();
	public boolean isDestroying, isRestarting;
	private Player player;
	private AI ai;
	private FileIOMemory save;
	private int menu;
	private int item;
	private Bitmap wonB, lostB;
	private String[] pauseText;
	private boolean redMoney;
	private RenderTutorial rt;
	private int tutorialState = -1;
	private Piece[] deadPiecesA;
	private RenderBirthdayPresent bdp;

	public Game(Context context) {
		this.context = context;

		money = Player.getPointsForLevel(GameMenuActivity.getLevel());
		level = GameMenuActivity.getLevel();

		try {
			save = new FileIOMemory(context);
		} catch (IOException e) {
			ACRA.getErrorReporter().handleException(e);
		}

		castle = new PlayerCastle(context, save, false);
		aiCastle = new AICastle(context, false);
		aiCastle.setLevel(level);
		deadPieces = new ArrayList<>();
		defences = new ArrayList<>();
		{
			buttonFrameUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_game_arrow);
			Matrix matrix = new Matrix();
			matrix.postRotate(180);
			items = new Bitmap[][]{new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_pieces),
					BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_powerup),
					BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_defences)},
					new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.ally_tower),
							BitmapFactory.decodeResource(context.getResources(), R.drawable.ally_healer),
							BitmapFactory.decodeResource(context.getResources(), R.drawable.ally_distractor),
							BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_thrower)},
					new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_powerup_heal),
							BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_powerup_defence),
							BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_powerup_attack)},
					new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_defences)}};
			menuArrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_arrow);
		}
		pauseButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_game_pause);
		buttonCoordinates = new float[]{DD.getWidth() - pauseButton.getWidth(), DD.getHeight() - pauseButton.getHeight()};
		{
			move = new Bitmap [] {BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_move),
					BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_move_selected)};
			attack = new Bitmap [] {BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_attack),
					BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_attack_selected)};// TODO: 2015-10-15 move to the object Frame
			heal = new Bitmap [] {BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_heal),
					BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_heal_selected)};
			float d = pauseButton.getWidth(), h = pauseButton.getHeight()/10f*9f;
			actionR = new RectF(DD.getWidth() - (move[0].getWidth() + attack[0].getWidth()) - d, DD.getHeight() - h - move[0].getHeight(), DD.getWidth() - attack[0].getWidth() - d,
					DD.getHeight() - h);
			moveR = new RectF(DD.getWidth() - d - attack[0].getWidth(), DD.getHeight() - h - attack[0].getHeight(), DD.getWidth() - d, DD.getHeight() - h);
		}
		wonB = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_won);
		lostB = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_lost);

		pauseText = new String[]{context.getResources().getString(R.string.paused), context.getResources().getString(R.string.tap_return),
				context.getResources().getString(R.string.back_exit)};

		player = new Player(context);
		ai = new AI(context, castle, aiCastle);

		if (GameMenuActivity.getLevel() == 0) {
			rt = new RenderTutorial(context);
		}

		if (RenderBirthdayPresent.isBirthdayToday()) bdp = new RenderBirthdayPresent(context);

		frameO = new Frame(context, items, buttonFrameUp, menuArrow);

		gamePlayTime.start();
	}

	public static int getLevel() {
		return level;
	}

	public static void drawDeadPiece(Piece p) {
		deadPieces.add(p);
	}

	public static AICastle getEnemyCastle() {
		if (aiCastle == null) throw new NullPointerException("EnemyCastle is null!");

		return aiCastle;
	}

	public static PlayerCastle getAllyCastle() {
		if (castle == null) throw new NullPointerException("YourCastle is null!");

		return castle;
	}

	public static void setWhatToShow(SHOW show) {
		whatToShow = show.ordinal();
	}

	public static void addDefence(Defence d) {
		defences.add(d);
	}

	public void onDraw(Canvas canvas) {
		//BACKGROUND
		canvas.drawColor(Color.GREEN);
		for (Piece p : deadPiecesA) p.draw(canvas);
		for (Defence d : defencesA) d.draw(canvas);

		//middle
		castle.draw(canvas);
		castle.drawLife();
		aiCastle.draw(canvas);
		aiCastle.drawLife();

		ai.onDraw(canvas);
		player.onDraw(canvas);

		ai.onForegroundDraw(canvas);
		player.onForegroundDraw(canvas);

		//Foreground
		{
			String m = "$" + money;

			if (redMoney) {
				paint.setColor(Color.RED);

				if (!redMoneyC.hasStarted()) redMoneyC.start();
				if (redMoneyC.getElapsedTime() > 500) {
					paint.setColor(Color.BLACK);
					redMoneyC.stop();
					redMoney = false;
				}
			} else if (money == 0) paint.setColor(Color.RED);
			else paint.setColor(Color.BLACK);

			paint.setTextSize(24);
			paint.getTextBounds(m, 0, m.length(), boundsPoints);
			canvas.drawText(m, 0, m.length(), 0, DD.getHeight() - boundsPoints.bottom, paint);
		}

		if (!showMenu) {
			frameO.onDrawMenu(canvas, false, -1, selected);
			menu = 0;
		} else {
			int v = menu%10;
			// menu = int (<current menu n> *10 + <selected item n>)
			//MENUS
			if (menu <= 0) {
				item = 0;
				selected = -1;
			} else if (menu < 10) item = menu;
				//ITEMS IN MENUS
			else if (menu < 20) {
				if (money >= Piece.Types.getPrice(Piece.Types.TYPES.values()[v])) selected = menu;
				else nullSelected();
			} else if (menu < 30) {
				if (money >= PowerUp.Types.getPrice(PowerUp.Types.TYPES.values()[v]))
					selected = menu;
				else nullSelected();
			} else if (menu < 40) {
				if (money >= Defence.Types.getPrice(Defence.Types.TYPES.values()[v]))
					selected = menu;
				else nullSelected();
			} else throw new IllegalArgumentException("CANNOT resolve for: " + menu);

			if (menu >= 10) menu /= 10;

			frameO.onDrawMenu(canvas, true, menu, selected);
		}

		if (showMoveAction)
			frameO.onDrawButtons(canvas, SHOW.values()[whatToShow], isAction? 1:(isMove? 0:-1));

		if (won) {
			isPaused = true;
			canvas.drawBitmap(wonB, DD.getWidth()/2 - wonB.getWidth()/2, DD.getHeight()/2 - wonB.getHeight()/2, null);
			save.setLevelDone(GameMenuActivity.getLevel());
			save.setMoney(money);

			try {
				save.saveToFile();
			} catch (IOException e) {
				ACRA.getErrorReporter().handleException(e, false);
			}
		} else if (lost) {
			isPaused = true;
			canvas.drawBitmap(lostB, DD.getWidth()/2 - lostB.getWidth()/2, DD.getHeight()/2 - lostB.getHeight()/2, null);
		}

		if (bdp != null && bdp.stillDraw()) bdp.draw(canvas);
		else if (GameMenuActivity.getLevel() == 0) {
			tutorialState = rt.onDraw(canvas, tutorialState, showMenu, (int) frameO.getMenuFrameHeight(), selected, moveR, isMove, actionR, isAction,
					items[0][0].getWidth() + 20f + items[0][1].getWidth()/2f, menu);
		}

		if (!isPaused)
			canvas.drawBitmap(pauseButton, buttonCoordinates[0], buttonCoordinates[1], null);
		else if (isPaused && !won && !lost) {
			paint.setColor(Color.BLUE);

			paint.setTextSize(48);
			paint.getTextBounds(pauseText[0], 0, pauseText[0].length(), pauseT[0]);
			paint.setTextSize(36);//36 for spacing
			paint.getTextBounds(pauseText[1], 0, pauseText[1].length(), pauseT[1]);
			paint.getTextBounds(pauseText[2], 0, pauseText[2].length(), pauseT[2]);

			int h = DD.getHeight()/2 - (pauseT[0].height() + pauseT[1].height() + pauseT[2].height())/2;

			paint.setTextSize(48);
			canvas.drawText(pauseText[0], DD.getWidth()/2 - pauseT[0].width()/2, h, paint);
			paint.setTextSize(34);
			canvas.drawText(pauseText[1], DD.getWidth()/2 - pauseT[1].width()/2, h + pauseT[0].height(), paint);
			canvas.drawText(pauseText[2], DD.getWidth()/2 - pauseT[2].width()/2, h + pauseT[0].height() + pauseT[1].height(), paint);
		} else if(won) {
			if(level == 0)
				Games.Achievements.unlock(((TacticalDefence) ((GameActivity) context).getApplication()).getGoogleApi(), context.getString(R.string.achievement_finished_tutorial));
			else if(level == GameMenuActivity.getMaximumLevel()) {
				Games.Achievements.unlock(((TacticalDefence) ((GameActivity) context).getApplication()).getGoogleApi(), context.getString(R.string.achievement_finished_game));

				if (Player.getPieceGroup().getAmountDead().size() == 0)
					Games.Achievements.unlock(((TacticalDefence) ((GameActivity) context).getApplication()).getGoogleApi(), context.getString(R.string.achievement_no_loses));
			}
		}

		if (Debug.DRAW_HITBOXES) Debug.drawHitboxes(canvas);
	}

	public void onUpdate() {
		showMoveAction = Player.getPieceGroup().getAll().length != Player.getPieceGroup().getAmountDead().size();

		deadPiecesA = deadPiecesA != null && deadPiecesA.length == deadPieces.size()?
				deadPiecesA:deadPieces.toArray(new Piece[deadPieces.size()]);

		defencesA = defencesA != null && defencesA.length == defences.size()?
				defencesA:defences.toArray(new Defence[defences.size()]);

		if (!isPaused) {
			if (!showMenu) {
				frame = new RectF(0, 0, DD.getWidth(), buttonFrameUp.getHeight());
				selected = -1;
			} else frame = new RectF(0, 0, DD.getWidth(), frameO.getMenuFrameHeight());
			if (gamePlayTime != null && gamePlayTime.isPaused()) gamePlayTime.resume();
		} else if (gamePlayTime != null && !gamePlayTime.isPaused()) gamePlayTime.pause();

		player.onUpdate(isPaused);
		ai.onUpdate(isPaused);
	}

	@SuppressWarnings({"SameReturnValue"})
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (!isPaused) {
				if (isContainedInRectF(buttonCoordinates[0], buttonCoordinates[1], buttonCoordinates[0] + pauseButton.getWidth(), buttonCoordinates[1] + pauseButton.getHeight(),
						event)) {
					isPaused = true;
					return true;
				}

				if (frame != null) {//prevents NPE when RenderGameActivity.onFling() occurs
					if (!showMenu) {
						if (isContainedInRectF(new RectF(0, 0, DD.getWidth(), buttonFrameUp.getHeight()), event))
							showMenu = true;
					} else {
						if (isContainedInRectF(new RectF(frame.left, 0, frame.right, frame.bottom - buttonFrameUp.getHeight()), event)) {
							for (int i = 0; i < items[item].length && items[item][i] != null; i++) {
								if (isContainedInRectF(10 + (items[item][i].getWidth() + 10)*(i), frame.top, 10 + (items[item][i].getWidth() + 10)*(i + 1), frame.bottom, event)) {
									if (menu == 0)
										menu = i + 1;
									else if (menu <= 3)
										menu = Integer.parseInt(menu + "" + i);
								} else if (isContainedInRectF(DD.getWidth() - 20 - menuArrow.getWidth(), 0, DD.getWidth() - 10, 20 + menuArrow.getHeight(), event))
									menu = 0;
							}
						} else if (isContainedInRectF(new RectF(frame.left, frame.bottom - buttonFrameUp.getHeight(), frame.right, frame.bottom), event))
							showMenu = false;
					}
				}

				if (showMoveAction) {
					if (isContainedInRectF(actionR, event)) {
						isAction = true;
						isMove = false;
					} else if (isContainedInRectF(moveR, event)) {
						isMove = true;
						isAction = false;
					}
				}
				player.onTouchEvent(event);

				if (bdp != null && bdp.stillDraw())
					bdp.onTouchEvent(event, GameMenuActivity.getLevel());
				else if (GameMenuActivity.getLevel() == 0) rt.onTouchEvent(event);
			} else {
				if (won) {
					if (level != GameMenuActivity.getMaximumLevel())
						context.startActivity(new Intent(context, GameMenuActivity.class));
					else context.startActivity(new Intent(context, CreditsActivity.class));
				} else if (lost) {
					isRestarting = true;
					reconstruct();
				} else isPaused = false;
			}

		}

		return true;
	}

	@SuppressWarnings({"SameReturnValue", "UnusedParameters"})
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		final short DISTRACTION_LIMIT = 50;

		if (!isPaused && SDK_INT < KITKAT) {
			if (e1.getY() < e2.getY() && e1.getX() - e2.getX() < DISTRACTION_LIMIT && !showMenu) {
				selected = -1;
				showMenu = true;
			} else if (e1.getY() > e2.getY() && e1.getX() - e2.getX() < DISTRACTION_LIMIT && showMenu) {
				selected = -1;
				showMenu = false;
			}
		}
		return true;
	}

	public boolean onBackPressed() {
		if (!isPaused && (rt == null || tutorialState != -1)) {
			isPaused = true;
			return false;
		} else {
			return true;
		}
	}

	private void nullSelected() {
		selected = -1;
		redMoney = true;
	}

	public void destroy() {//GameThread t) {
		isDestroying = true;
		//t.setRunning(false);
		player.destroy();
		ai.destroy();
		showMenu = showMoveAction = isMove = isAction = won = lost = isPaused = false;
		selected = -1;
		money = whatToShow = 0;
		deadPieces = null;
		defences = null;
		defencesA = null;
		if (!isRestarting) {
			level = -1;
			castle = null;
			aiCastle = null;
		}
		piecesBitmap = null;
	}

	public void reconstruct() {//GameThread t) {
		destroy();//t);

		deadPieces = new ArrayList<>();
		defences = new ArrayList<>();

		money = Player.getPointsForLevel(GameMenuActivity.getLevel());

		float d = pauseButton.getWidth(), h = pauseButton.getHeight()/10f*9f;
		actionR = new RectF(DD.getWidth() - (move[0].getWidth() + attack[0].getWidth()) - d, DD.getHeight() - h - move[0].getHeight(), DD.getWidth() - attack[0].getWidth() - d,
				DD.getHeight() - h);
		moveR = new RectF(DD.getWidth() - d - attack[0].getWidth(), DD.getHeight() - h - attack[0].getHeight(), DD.getWidth() - d, DD.getHeight() - h);

		player = new Player(context);
		ai = new AI(context, castle, aiCastle);

		castle.reconstruct();
		aiCastle.reconstruct();

		isDestroying = false;
		onUpdate();
		//t.setRunning(true);
	}

	public enum SHOW {ATTACK, HEAL, NONE}

	private class Frame {

		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
		private Bitmap[] frame;
		private Chronometer chr = new Chronometer();
		private boolean currentState = false;
		private float frameHeight;
		private Point selection;

		private Frame(Context context, Bitmap[][] items, Bitmap buttonFrameUp, Bitmap menuArrow) {
			//menu
			{
				selection = new Point((int) 10f + items[0][0].getWidth(), (int) (items[0][0].getHeight()/2f + 10f));
				frame = new Bitmap[items.length + 1];
				Rect boundsCost = new Rect();
				frameHeight = items[0][0].getHeight() + 15 + buttonFrameUp.getHeight();
				RectF frameR;

				for (int item = 0; item < items.length; item++) {
					frame[item + 1] = Bitmap.createBitmap(DD.getWidth(), (int) frameHeight, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(frame[item + 1]);

					paint.setColor(context.getResources().getColor(R.color.dark_yellow));
					canvas.drawRect(0, 0, DD.getWidth(), frameHeight, paint);

					for (int i = 0; i < items[item].length && items[item][i] != null; i++) {
						canvas.drawBitmap(items[item][i], 10f + (items[item][0].getWidth() + 10f)*i, 10f, null);

						if (item != 0) {
							String s;
							paint.setColor(Color.BLACK);
							paint.setTextSize(16);
							s = "$";
							switch (item) {
								case 1:
									s += Piece.Types.getPrice(values()[((i + 1)*2) - 2]);
									break;
								case 2:
									s += PowerUp.Types.getPrice(PowerUp.Types.TYPES.values()[i]);
									break;
								case 3:
									s += Defence.Types.getPrice(Defence.Types.TYPES.values()[i]);
									break;
							}
							paint.getTextBounds(s, 0, s.length(), boundsCost);
							canvas.drawText(s, 10f + (items[item][0].getWidth() + 10f)*i, 10f + items[item][i].getHeight() + boundsCost.height(), paint);
						}
					}

					if (item != 0)
						canvas.drawBitmap(menuArrow, DD.getWidth() - 10 - menuArrow.getWidth(), 10, paint);

					canvas.drawBitmap(buttonFrameUp, DD.getWidth()/2f - buttonFrameUp.getWidth()/2f, items[item][0].getHeight() + 15, paint);
				}

				frame[0] = Bitmap.createBitmap(DD.getWidth(), buttonFrameUp.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(frame[0]);
				frameR = new RectF(0, 0, DD.getWidth(), buttonFrameUp.getHeight());
				Matrix matrix = new Matrix();
				matrix.postRotate(180);
				Bitmap buttonFrameDown = Bitmap.createBitmap(buttonFrameUp, 0, 0, buttonFrameUp.getWidth(), buttonFrameUp.getHeight(), matrix, true);

				paint.setColor(context.getResources().getColor(R.color.dark_yellow));
				canvas.drawRect(frameR, paint);
				canvas.drawBitmap(buttonFrameDown, DD.getWidth()/2f - buttonFrameDown.getWidth()/2f, 0, null);
			}
		}

		private void onDrawMenu(Canvas canvas, boolean showMenu, int menu, int selected) {
			Bitmap current = chr.hasStarted()? frame[1]:frame[!showMenu? 0:menu + 1];
			float down = 0, up = 0;

			if (!chr.hasStarted()) {
				if (showMenu != currentState) {
					chr.start();
					currentState = showMenu;
					current = frame[currentState? 0:1];
				}
			} else {
				if (chr.getElapsedTime() >= 150f) {
					chr.stop();
					current = frame[currentState? 1:0];
				} else {
					float d = (current.getHeight() - frame[0].getHeight())/150f*chr.getElapsedTime();

					down = frame[0].getHeight() - current.getHeight() + d;
					up = -d;
				}
			}

			canvas.drawBitmap(current, 0, chr.hasStarted()? (currentState? down:up):0, paint);

			if (!chr.hasStarted() && selected != -1) {
				paint.setColor(Color.GREEN);
				canvas.drawCircle(10 + (selection.x - 10f)/2f + Integer.parseInt(String.valueOf(String.valueOf(selected).charAt(1)))*selection.x,
						selection.y, 7f, paint);
			}
		}

		private float getMenuFrameHeight() {
			return frameHeight;
		}

		/*
		 * @param selected -1 for none, 0 for move, 1 for action
		 */
		private void onDrawButtons(Canvas canvas, SHOW whatToShow, int selected) { // TODO: 2015-10-17 this doesn't work correctly at all
			switch (whatToShow) {
				case ATTACK:
					if (selected == 1) canvas.drawBitmap(attack[1], actionR.left, actionR.top, null);
					else canvas.drawBitmap(attack[0], actionR.left, actionR.top, null);

					break;

				case HEAL:
					if (selected == 1) canvas.drawBitmap(heal[1], actionR.left, actionR.top, null);
					else canvas.drawBitmap(heal[0], actionR.left, actionR.top, null);

					break;

				case NONE:
					break;
			}

			if (selected == 0) canvas.drawBitmap(move[1], moveR.left, moveR.top, null);
			else canvas.drawBitmap(move[0], moveR.left, moveR.top, null);
		}

	}

}