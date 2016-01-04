package com.emmanuelmess.tacticaldefence.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emmanuelmess.API.twodmensions.DrawHelper;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.activities.fragments.SidebarFragment;

import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;

/**
 * @author Emmanuel
 *         on 2015-05-29, at 23:39.
 */
public class ExplanationsActivity extends FragmentActivity implements SidebarFragment.InterfaceDataCommunicator {

	private final static int SIDEBAR_WIDTH = 90;//TODO 2015-12-16 this shouldn't be hardcoded
	ExplanationsCollectionPagerAdapter mExplanationsCollectionPagerAdapter;
	PagerTitleStrip pagerTitleStrip;
	private ViewPager mViewPager;
	private int element = -1;
	private boolean isRight;
	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_explanations);

		context = getApplicationContext();

		// ViewPager and its adapters use support library
		// fragments, so use getSupportFragmentManager.
		mExplanationsCollectionPagerAdapter = new ExplanationsCollectionPagerAdapter(getSupportFragmentManager());

		pagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
		pagerTitleStrip.setBackgroundColor(Color.WHITE);
		pagerTitleStrip.setTextColor(Color.BLACK);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mExplanationsCollectionPagerAdapter);
	}

	@Override
	public void updateData(int item, boolean isRight) {
		element = item;
		this.isRight = isRight;

		mViewPager.setBackgroundColor(isRight? getApplicationContext().getResources().getColor(R.color.dark_red):Color.BLUE);
		mViewPager.getAdapter().notifyDataSetChanged();
	}

	public static class ExplanationsCollectionPagerAdapter extends FragmentStatePagerAdapter {

		Fragment[] f = new Fragment[3];

		public ExplanationsCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new ExplanationsObjectFragment();
			Bundle args = new Bundle();
			// Our object is just an integer :-P
			args.putInt(ExplanationsObjectFragment.ARG_OBJECT, i);
			fragment.setArguments(args);
			f[i] = fragment;
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			switch (position) {
				case 0:
					return context.getString(R.string.pieces);
				case 1:
					return context.getString(R.string.powers);
				case 2:
					return context.getString(R.string.castle);

				default:
					throw new NullPointerException("Non-existent tag");
			}
		}
	}

	public static class ExplanationsObjectFragment extends Fragment {
		public static final String ARG_OBJECT = "explanation";

		public ExplanationsObjectFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater,
		                         ViewGroup container, Bundle savedInstanceState) {
			// The last two arguments ensure LayoutParams are inflated properly.
			View rootView = inflater.inflate(R.layout.fragment_explanatons_explanation, container, false);
			int element = ((ExplanationsActivity) getActivity()).element;
			boolean isRight = ((ExplanationsActivity) getActivity()).isRight;
			Fragment sidebar1 = new SidebarFragment(), sidebar2 = new SidebarFragment();
			Bundle argsS1 = new Bundle(), argsS2 = new Bundle();
			int[] b = new int[0], b2 = new int[0];
			String title = "", desc = "", d1 = "", d2 = "";

			switch (getArguments().getInt(ARG_OBJECT)) {
				case 0:
					b = new int[]{R.drawable.ally_tower, R.drawable.ic_menu_thrower};
					b2 = new int[]{R.drawable.ally_healer, R.drawable.ally_distractor};
					d1 = "HP: ";
					d2 = context.getString(R.string.damage) + ": ";

					if (isRight) {
						switch (element) {
							case 0:
								title = context.getString(R.string.towers);
								desc = context.getString(R.string.desc_1);
								d1 += "100";//TODO programatically
								d2 += "20";
								break;
							case 1:
								title = context.getString(R.string.ballista);
								desc = context.getString(R.string.desc_2);
								d1 += "100";
								d2 += "100";
								break;
							default:
								d1 = "";
								d2 = "";
								break;
						}
					} else {
						switch (element) {
							case 0:
								title = context.getString(R.string.healer);
								desc = context.getString(R.string.desc_3);
								d1 += "100";
								d2 = context.getString(R.string.heal) + ": 100";
								break;
							case 1:
								title = context.getString(R.string.distractor);
								desc = context.getString(R.string.desc_4);
								d1 += "300";
								d2 += "0";
								break;
							default:
								d1 = "";
								d2 = "";
								break;
						}
					}
					break;
				case 1:
					b = new int[]{R.drawable.ic_menu_powerup_attack};
					b2 = new int[]{R.drawable.ic_menu_powerup_heal, R.drawable.ic_menu_powerup_defence};
					d2 = "Cost: ";

					if (isRight) {
						switch (element) {
							case 0:
								title = context.getString(R.string.magical_arrow);
								desc = context.getString(R.string.desc_11);
								d1 = context.getString(R.string.damage) + ": 40";
								d2 += "75";
								break;
							default:
								d1 = "";
								d2 = "";
								break;
						}
					} else {
						switch (element) {
							case 0:
								title = context.getString(R.string.heal);
								desc = context.getString(R.string.desc_12);
								d2 += "50";
								break;
							case 1:
								title = context.getString(R.string.magical_defence);
								desc = context.getString(R.string.desc_13);
								d1 = context.getString(R.string.resistance) + ": 100";
								d2 += "75";
								break;
							default:
								d1 = "";
								d2 = "";
								break;
						}
					}
					break;
				case 2:
					b = new int[]{R.drawable.upgrade_tower_1, R.drawable.upgrade_archers, R.drawable.upgrade_tower_ii_2};
					b2 = new int[]{R.drawable.upgrade_wall, R.drawable.upgrade_rampart};
					d1 = context.getString(R.string.costs) + ": ";
					d2 = context.getString(R.string.adds) + ": ";

					if (isRight) {
						switch (element) {
							case 0:
								title = context.getString(R.string.defence_towers);
								desc = context.getString(R.string.desc_21);
								d1 += "100";
								d2 += "2";
								break;
							case 1:
								title = context.getString(R.string.archers);
								desc = context.getString(R.string.desc_22);
								d1 += "300";
								d2 += "5";
								break;
							case 2:
								title = context.getString(R.string.defensive_towers_ii);
								desc = context.getString(R.string.desc_23);
								d1 += "150";
								d2 += "1";
								break;
							default:
								d1 = "";
								d2 = "";
								break;
						}

						d2 += " " + context.getString(R.string.points_of_attack);
					} else {
						switch (element) {
							case 0:
								title = context.getString(R.string.improved_walls);
								desc = context.getString(R.string.desc_24);
								d1 += "100";
								d2 += "100";
								break;
							case 1:
								title = context.getString(R.string.ramparts);
								desc = context.getString(R.string.desc_25);
								d1 += "500";
								d2 += "500";
								break;
							default:
								d1 = "";
								d2 = "";
								break;
						}

						d2 += d2.equals("")? "":" HP";
					}
					break;
			}

			if (element == -1) {
				d1 = "";
				d2 = "";
			}

			String text = "\n" + d1 + "\n" + d2;
			((TextView) rootView.findViewById(R.id.textView3)).setText(text);
			((TextView) rootView.findViewById(R.id.textView3)).setTextColor(Color.WHITE);

			((TextView) rootView.findViewById(R.id.textView5)).setText(title);
			((TextView) rootView.findViewById(R.id.textView5)).setTextColor(Color.WHITE);

			((TextView) rootView.findViewById(R.id.textView6)).setText(desc);
			((TextView) rootView.findViewById(R.id.textView6)).setTextColor(Color.WHITE);

			argsS1.putBoolean(SidebarFragment.IS_RIGHT, true);
			argsS1.putInt(SidebarFragment.WIDTH, SIDEBAR_WIDTH);
			argsS1.putIntArray(SidebarFragment.BITMAP, b);
			sidebar1.setArguments(argsS1);

			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.replace(R.id.fragmentFrame, sidebar1).commit();

			argsS2.putBoolean(SidebarFragment.IS_RIGHT, false);
			argsS2.putInt(SidebarFragment.WIDTH, SIDEBAR_WIDTH);
			argsS2.putIntArray(SidebarFragment.BITMAP, b2);
			sidebar2.setArguments(argsS2);

			FragmentTransaction transaction2 = getChildFragmentManager().beginTransaction();
			transaction2.replace(R.id.fragmentFrame2, sidebar2).commit();

			return rootView;
		}

		public static class RenderPicture extends View {

			public RenderPicture(Context context, AttributeSet attrSet) {
				super(context, attrSet);
			}

			protected void onDraw(Canvas canvas) {
				setBackgroundColor(Color.RED);
			}

		}

		public static class RenderData extends View {

			DrawHelper dh;
			Paint paint = new Paint();
			Rect r = new Rect();
			int item;

			public RenderData(Context context, AttributeSet attrSet) {
				super(context, attrSet);

				dh = new DrawHelper();
			}

			@Override
			protected void onDraw(Canvas canvas) {
				//TODO see SidebarFragment float h = dd.getWidth() / 4f - (5 * 2 + 10) / 2f;
				dh.drawHealthBar(90, DD.getHeight() - 40, DD.getWidth() - 90*2, 10, 100, Color.RED, Color.GREEN, canvas);

				String hp = "HP: ";
				paint.setTextSize(36);
				paint.getTextBounds(hp, 0, hp.length(), r);
				canvas.drawText(hp, DD.getWidth()/2f - r.width()/2f, DD.getHeight() - 35 - r.height()/2f, paint);
			}

		}

	}

}
