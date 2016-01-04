package com.emmanuelmess.tacticaldefence.activities.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.emmanuelmess.API.twodmensions.Contain;
import com.emmanuelmess.API.twodmensions.DrawHelper;
import com.emmanuelmess.tacticaldefence.R;

import java.util.Random;

/**
 * @author Emmanuel
 *         on 2015-06-07, at 13:59.
 */
public class SidebarFragment extends Fragment {

	public static final String BITMAP = "bitmap", BOTTOM_DATA = "bd", IS_RIGHT = "isRight", WIDTH = "width", IS_DATA_CROSSED = "data_crossed";
	public static InterfaceDataCommunicator interfaceDataCommunicator;
	RenderSidebarFragment v;

	public SidebarFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// The last two arguments ensure LayoutParams are inflates properly.
		View rootView = inflater.inflate(R.layout.fragment_sidebar, container, false);

		try {
			v = (RenderSidebarFragment) rootView.findViewById(R.id.view3);
			v.isRight = getArguments().getBoolean(IS_RIGHT);

			int [] mem = getArguments().getIntArray(BITMAP);
			v.b = new Bitmap[mem.length];
			for (int i = 0; i < v.b.length; i++)
				v.b[i] = BitmapFactory.decodeResource(getResources(), mem[i]);

			int width = getArguments().getInt(WIDTH);
			v.r = new RectF[v.b.length];
			for (int i = 0; i < v.r.length; i++) {
				v.r[i] = new RectF();
				v.r[i].set(10, 10 + (width - 10)*i, width - 10, (width - 10)*(i + 1));
			}

			if (getArguments().getStringArray(BOTTOM_DATA) != null) {
				String[] data = getArguments().getStringArray(BOTTOM_DATA);
				boolean[] dataCrossed = getArguments().getBooleanArray(IS_DATA_CROSSED);
				if (data.length != v.b.length || dataCrossed.length != v.b.length) throw new IllegalArgumentException("Illegal array length");

				Paint paint = new Paint();
				RectF[] rData = new RectF[v.r.length];
				paint.setTextSize(16);

				for(int i = 0; i < v.r.length; i++) {
					Rect bounds = new Rect();
					rData[i] = new RectF();

					paint.getTextBounds(data[i], 0, data[i].length(), bounds);
					bounds.offset((int) (v.r[i].centerX() - bounds.width()/2f), (int) (v.r[i].bottom - bounds.height() + 20));
					rData[i].set(bounds.left - 5, bounds.top, bounds.right + 5, bounds.bottom + 5);
				}

				v.data = data;
				v.dataCrossed = dataCrossed;
				v.rData = rData;
			}
		} catch (NullPointerException e) {
			throw new IllegalArgumentException();
		}

		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		try {
			interfaceDataCommunicator = (InterfaceDataCommunicator) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + " must implement InterfaceDataCommunicator");
		}
	}

	public interface InterfaceDataCommunicator {
		void updateData(int item, boolean isRight);
	}

	public static class RenderSidebarFragment extends View {
		private final Context context;
		private final Paint paint = new Paint();
		Bitmap[] b;
		RectF[] r;
		RectF[] rData;
		String[] data;
		boolean[] dataCrossed;
		private boolean isRight;
		private Random ran = new Random();

		public RenderSidebarFragment(Context context, AttributeSet attrs) {
			super(context, attrs);

			this.context = context;
		}

		@Override
		public boolean onTouchEvent(@NonNull MotionEvent event) {
			if (b != null) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					for (int i = 0; i < r.length; i++) {
						if (Contain.isContainedInRectF(r[i], event)) {
							//Call this in order to send data to interface
							interfaceDataCommunicator.updateData(i, isRight);
						}
					}
				}
			}
			return true;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (b != null) {
				setBackgroundColor(isRight? context.getResources().getColor(R.color.dark_red):Color.BLUE);

				for (int i = 0; i < r.length; i++) {
					paint.setColor(Color.LTGRAY);
					canvas.drawRoundRect(r[i], (float) Math.PI, (float) Math.PI, paint);

					if (data != null) {
						paint.setColor(Color.YELLOW);
						canvas.drawRoundRect(rData[i], (float) Math.PI, (float) Math.PI, paint);

						paint.setTextSize(16);
						paint.setColor(Color.BLACK);
						canvas.drawText(data[i], (rData[i].left+5) - 2, rData[i].bottom-5, paint);

						if (dataCrossed[i]) {
							paint.setStrokeWidth(2);
							canvas.drawLine(rData[i].left + (float) ran.nextGaussian()*3, rData[i].centerY() + (float) ran.nextGaussian()*2,
									rData[i].right + (float) ran.nextGaussian()*2, rData[i].centerY() + (float) ran.nextGaussian()*3, paint);
						}
						paint.reset();
					}

					DrawHelper.drawBitmap(b[i], r[i], canvas);
				}
			}
		}

	}

}