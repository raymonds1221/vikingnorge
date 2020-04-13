package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.FirstAidItem;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;

public class FirstAidAdapter extends BaseAdapter {
	private Context context;
	private List<FirstAidItem> firstAids;
	
	public FirstAidAdapter(Context context, List<FirstAidItem> firstAids) {
		this.context = context;
		this.firstAids = firstAids;
	}

	@Override
	public int getCount() {
		return firstAids.size();
	}

	@Override
	public Object getItem(int position) {
		return firstAids.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		FirstAidItem firstAid = firstAids.get(position);
		
		if(view == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.list_first_aid, null);
			holder.firstaid_picture = (ImageView) view.findViewById(R.id.firstaid_picture);
			holder.firstaid_desc = (TextView) view.findViewById(R.id.firstaid_desc);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.firstaid_picture.setImageResource(firstAid.getImageResourceId());
		holder.firstaid_picture.setScaleType(ImageView.ScaleType.FIT_XY);
		holder.firstaid_desc.setText(Html.fromHtml(firstAid.getDescription()));
		holder.firstaid_desc.setMovementMethod(new ScrollingMovementMethod());
		return view;
	}
	
	private class ViewHolder {
		public ImageView firstaid_picture;
		public TextView firstaid_desc;
	}

}
