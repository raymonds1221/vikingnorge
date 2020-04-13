package no.incent.viking.adapter;

import no.incent.viking.pojo.PhoneCategory;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PhoneCategoryAdapter extends ArrayAdapter<PhoneCategory> {
	private Context context;
	private int layoutResourceId;
	private List<PhoneCategory> phoneCategories;
	
	public PhoneCategoryAdapter(Context context, int layoutResourceId, List<PhoneCategory> phoneCategories) {
		super(context, layoutResourceId, phoneCategories);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.phoneCategories = phoneCategories;
	}
	
	private class ViewHolder {
		public TextView content;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View v = view;
		ViewHolder holder = null;
		PhoneCategory phoneCategory = phoneCategories.get(position);
		
		if(v == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(layoutResourceId, null);
			holder.content = (TextView) v;
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		holder.content.setText(phoneCategory.getName() + " (" + phoneCategory.getTelephone() + ")");
		
		return v;
	}
}
