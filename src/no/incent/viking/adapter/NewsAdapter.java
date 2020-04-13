package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.News;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

public class NewsAdapter extends ArrayAdapter<News> {
	private Context context;
	private int layoutResourceId;
	private List<News> newsList;
	
	public NewsAdapter(Context context, int layoutResourceId, List<News> newsList) {
		super(context, layoutResourceId, newsList);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.newsList = newsList;
	}
	
	private class ViewHolder {
		public TextView news_title;
		public TextView news_date;
		public TextView news_fulltext;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final News news = newsList.get(position);
		
		if(view == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(layoutResourceId, null);
			holder.news_title = (TextView) view.findViewById(R.id.news_title);
			holder.news_date = (TextView) view.findViewById(R.id.news_date);
			holder.news_fulltext = (TextView) view.findViewById(R.id.news_fulltext);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.news_title.setText(news.getTitle());
		holder.news_date.setText(news.getCreationDate());
		holder.news_fulltext.setText(news.getFullText());
		//holder.news_fulltext.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		return view;
	}
	
}
