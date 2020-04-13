package no.incent.viking.adapter;

import no.incent.viking.R;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

public class DamageCauseAdapter extends ArrayAdapter<String> {
	private Context context;
	private int layoutResourceId;
	private String[] causes;
	private List<Boolean> itemChecked = new ArrayList<Boolean>();
	private boolean[] damage_causes;
	
	public DamageCauseAdapter(Context context, int layoutResourceId, String[] causes) {
		super(context, layoutResourceId, causes);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.causes = causes;
		
		for(int i=0;i<causes.length;i++) {
			itemChecked.add(i, false);
		}
		damage_causes = new boolean[itemChecked.size()];
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		String cause = causes[position];
		ViewHolder holder;
		
		if(view == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(layoutResourceId, null);
			holder.checkBox = (CheckBox) view.findViewById(R.id.check_cause);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				itemChecked.set(position, cb.isChecked());
			}
		});
		
		holder.checkBox.setText(cause);
		holder.checkBox.setChecked(itemChecked.get(position));
		return view;
	}
	
	public String[] getDamageCauses() {
		List<CharSequence> damage_causes = new ArrayList<CharSequence>();
		
		for(int i=0;i<itemChecked.size();i++) {
			if(itemChecked.get(i).booleanValue()) {
				damage_causes.add(causes[i]);
			}
		}
		
		return damage_causes.toArray(new String[damage_causes.size()]);
	}
	
	public boolean[] getAllDamageCause() {
		for(int i=0;i<itemChecked.size();i++) {
			damage_causes[i] = itemChecked.get(i).booleanValue();
		}
		return damage_causes;
	}
	
	private class ViewHolder {
		public CheckBox checkBox;
	}
}
