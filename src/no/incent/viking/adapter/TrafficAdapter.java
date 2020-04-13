package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.Traffic;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class TrafficAdapter extends BaseExpandableListAdapter {
	private Context context;
	private String[] groups;
	private List<List<Traffic>> traffics;
	
	public TrafficAdapter(Context context, String[] groups, List<List<Traffic>> traffics) {
		this.context = context;
		this.groups = groups;
		this.traffics = traffics;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return traffics.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		Traffic traffic = traffics.get(groupPosition).get(childPosition);
		ViewHolderChild holder;
		
		if(view == null) {
			holder = new ViewHolderChild();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.list_traffic_child, null);
			holder.road_name = (TextView) view.findViewById(R.id.road_name);
			holder.short_text = (TextView) view.findViewById(R.id.short_text);
			view.setTag(holder);
		} else {
			holder = (ViewHolderChild) view.getTag();
		}
		
		holder.road_name.setText(traffic.getRoadName());
		if(traffic.getOptionalText() != null && traffic.getOptionalText().length() >= 20) {
			holder.short_text.setText(traffic.getOptionalText().substring(0, 20) + "..");
		} else {
			holder.short_text.setText(traffic.getOptionalText());
		}
		
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return traffics.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return groups.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolderGroup holder;
		
		if(view == null) {
			holder = new ViewHolderGroup();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.list_traffic_group, null);
			holder.traffic_group_name = (TextView) view.findViewById(R.id.traffic_group_name);
			view.setTag(holder);
		} else {
			holder = (ViewHolderGroup) view.getTag();
		}
		
		holder.traffic_group_name.setText(groups[groupPosition]);
		
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private class ViewHolderGroup {
		public TextView traffic_group_name;
	}
	
	private class ViewHolderChild {
		public TextView road_name;
		public TextView short_text;
	}
}
