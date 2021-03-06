package com.droidplanner.fragments.helpers;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.droidplanner.R;
import com.droidplanner.helpers.geoTools.Simplify;

public class GestureMapFragment extends Fragment implements OnGestureListener {
	private static final int TOLERANCE = 15;
	private static final int STROKE_WIDTH = 3;

	private double toleranceInPixels;
	
	public interface OnPathFinishedListner {

		void onPathFinished(List<Point> path);
	}

	private GestureOverlayView overlay;
	private OnPathFinishedListner listner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gesture_map_fragment, container,
				false);
		overlay = (GestureOverlayView) view.findViewById(R.id.overlay1);
		overlay.addOnGestureListener(this);
		overlay.setEnabled(false);
		
		overlay.setGestureStrokeWidth(scaleDpToPixels(STROKE_WIDTH));
		toleranceInPixels = scaleDpToPixels(TOLERANCE);
		return view;
	}

	private int scaleDpToPixels(double value) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) Math.round(value*scale);
	}

	public void enableGestureDetection() {
		overlay.setEnabled(true);
	}

	public void setOnPathFinishedListner(OnPathFinishedListner listner) {
		this.listner = listner;
	}

	@Override
	public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
		overlay.setEnabled(false);
		List<Point> path = decodeGesture();
		if (path.size() > 1) {
			path = Simplify.simplify(path, toleranceInPixels);
		}
		listner.onPathFinished(path);
	}

	private List<Point> decodeGesture() {
		List<Point> path = new ArrayList<Point>();
		extractPathFromGesture(path);		
		return path;
	}

	private void extractPathFromGesture(List<Point> path) {
		float[] points = overlay.getGesture().getStrokes().get(0).points;
		for (int i = 0; i < points.length; i += 2) {
			path.add(new Point((int) points[i], (int) points[i + 1]));
		}
	}

	@Override
	public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
	}

	@Override
	public void onGestureCancelled(GestureOverlayView arg0, MotionEvent arg1) {
	}

	@Override
	public void onGestureStarted(GestureOverlayView arg0, MotionEvent arg1) {
	}

}
