package no.incent.viking.map;

import no.incent.viking.R;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.MapView;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class TrafficItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private Context context;
	private List<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
	private MapView mapView;
	private OnMarkerClickListener listener;
	
	public TrafficItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		this.mapView = mapView;
	}
	
	public void addItem(OverlayItem item) {
		overlayItems.add(item);
		populate();
	}
	
	public void addAll(List<OverlayItem> items) {
		overlayItems.addAll(items);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlayItems.get(i);
	}

	@Override
	public int size() {
		return overlayItems.size();
	}
	
	@Override
	public boolean onTap(int index) {
		final OverlayItem item = getItem(index);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.traffic_marker, null);
		view.setTag("map_dialog");
		TextView road_name = (TextView) view.findViewById(R.id.road_name);
		road_name.setText(item.getTitle());
		
		MapView.LayoutParams layoutParams = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT,
				item.getPoint(), 0, -20, MapView.LayoutParams.BOTTOM_CENTER);
		view.setLayoutParams(layoutParams);
		
		for(int i=0;i<mapView.getChildCount();i++) {
			if(mapView.getChildAt(i).getTag().equals("map_dialog")) {
				mapView.removeViewAt(i);
			}
		}
		
		mapView.addView(view);
		
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mapView.removeView(v);
				int roadId = Integer.parseInt(item.getSnippet());
				
				if(listener != null)
					listener.onMarkerClick(roadId);
			}
		});
		return true;
	}
	
	public static interface OnMarkerClickListener {
		public void onMarkerClick(int roadId);
	}
	
	public void setOnMarkerClickListener(OnMarkerClickListener listener) {
		this.listener = listener;
	}
}
