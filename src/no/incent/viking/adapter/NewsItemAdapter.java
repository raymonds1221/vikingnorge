package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.Traffic;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsItemAdapter extends ArrayAdapter<Traffic> {
	private List<Traffic> traffics = new ArrayList<Traffic>();
	private Context context;
	private OnItemClickListener onItemClickListener;

	public NewsItemAdapter(Context ctx, int layoutItemResourceId, List<Traffic> traffics) {
		super(ctx, layoutItemResourceId, traffics);
		this.context = ctx;
		this.traffics = traffics;
	}

	public static class ViewHolder{
		public ImageView mapPin;
		public TextView newsID;
		public TextView newsContent;
		public ImageView readMore;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Traffic traffic = traffics.get(position);
		View v = convertView;
		ViewHolder holder;
		if (v == null) {		
			LayoutInflater vi = 
				(LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = vi.inflate(R.layout.list_topnews, null);
			
			holder = new ViewHolder();
			holder.mapPin = (ImageView) v.findViewById(R.id.map_pin);
			holder.newsID = (TextView) v.findViewById(R.id.news_id);
			holder.newsContent = (TextView) v.findViewById(R.id.news_content);
			holder.readMore = (ImageView) v.findViewById(R.id.read_more_btn);
			
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();
		
		if (traffic != null) {
			String shortText = "";
			if(traffic.getShortText().length() > 20) {
				shortText = traffic.getShortText().substring(0, 20) + "..";
			} else {
				shortText = traffic.getShortText();
			}
			
			holder.newsID.setText(traffic.getRoadName());
			holder.newsContent.setText(shortText);
		}
		
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onItemClickListener != null)
					onItemClickListener.onItemClick(traffic);
			}
		});
		return v;
	}
	
	public void setOnItemClick(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public static interface OnItemClickListener {
		public void onItemClick(Traffic traffic);
	}
	
}