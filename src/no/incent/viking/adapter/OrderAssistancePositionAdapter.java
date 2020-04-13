package no.incent.viking.adapter;

import no.incent.viking.pojo.Geocode;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Filter;

public class OrderAssistancePositionAdapter extends ArrayAdapter<Geocode> {
	private Context context;
	private int layoutResourceId;
	private List<Geocode> geoCodes;
	private OnItemClickListener onItemClickListener;

	public OrderAssistancePositionAdapter(Context context, int layoutResourceId, List<Geocode> geoCodes) {
		super(context, layoutResourceId, geoCodes);
		
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.geoCodes = geoCodes;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final Geocode geoCode = geoCodes.get(position);
		
		if(view == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(layoutResourceId, null);
		}
		
		if(geoCode.getFormattedAddress() != null || !geoCode.getFormattedAddress().equals(""))
			((TextView) view).setText(geoCode.getFormattedAddress());
		else
			((TextView) view).setText(geoCode.getAdministrativeArea());
		
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onItemClickListener != null) {
					onItemClickListener.onItemClick(geoCode);
				}
			}
		});
		
		return view;
	}
	
	@Override
	public Filter getFilter() {
		return filter;
	}
	
	private Filter filter = new Filter() {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			if(constraint != null) {
				FilterResults filterResults = new FilterResults();
				filterResults.values = geoCodes;
				filterResults.count = geoCodes.size();
				
				return filterResults;
			}
			
			return new FilterResults();
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if(constraint != null) {
				if(results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		}
	};
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public static interface OnItemClickListener {
		public void onItemClick(Geocode geoCode);
	}
}
