package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.db.DBNewsAdapter;
import no.incent.viking.adapter.NewsAdapter;
import no.incent.viking.pojo.User;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;

public class News extends LinearLayout {
	private Context context;
	private ListView news_list;
	private DBNewsAdapter newsAdapter;
	private ViewFlipper friend_flipper;
	private User user;
	
	public News(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.news, this);
		this.context = context;
		
		user = ((VikingApplication) context.getApplicationContext()).getUser();
		newsAdapter = new DBNewsAdapter(context);
		
		friend_flipper = (ViewFlipper) findViewById(R.id.friend_flipper);
		friend_flipper.setInAnimation(context, R.anim.fade_in);
		friend_flipper.setOutAnimation(context, R.anim.fade_out);
		
		news_list = (ListView) findViewById(R.id.news_list);
		
		loadNews();
		
		findViewById(R.id.info_news).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.submenu_btn_active);
				findViewById(R.id.info_profile).setBackgroundResource(R.drawable.submenu_btn);
				friend_flipper.setDisplayedChild(0);
			}
		});
		
		findViewById(R.id.info_profile).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.submenu_btn_active);
				findViewById(R.id.info_news).setBackgroundResource(R.drawable.submenu_btn);
				
				if(user == null) {
					friend_flipper.setDisplayedChild(2);
				} else {
					friend_flipper.setDisplayedChild(1);
				}
			}
		});
	}
	
	public void loadNews() {
		newsAdapter.open();
		List<no.incent.viking.pojo.News> newsList = newsAdapter.getAllNews();
		newsAdapter.close();
		NewsAdapter adapter = new NewsAdapter(context, R.layout.list_news, newsList);
		adapter.setDropDownViewResource(R.layout.list_news);
		news_list.setAdapter(adapter);
	}
	
	public void setUser(User user) {
		this.user = user;
		if(friend_flipper.getDisplayedChild() == 2)
			friend_flipper.setDisplayedChild(1);
	}
}
