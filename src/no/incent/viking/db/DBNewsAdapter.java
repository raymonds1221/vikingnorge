package no.incent.viking.db;

import no.incent.viking.pojo.News;
import no.incent.viking.provider.NewsProvider;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBNewsAdapter {
	private Context context;
	
	public DBNewsAdapter(Context context) {
		this.context = context;
	}
	
	public synchronized void open() {
	}
	
	public void close() {
	}
	
	public void insertNews(News news) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_NEWS_TITLE, news.getTitle());
		contentValues.put(DBHelper.COLUMN_NEWS_URL, news.getUrl());
		contentValues.put(DBHelper.COLUMN_NEWS_CREATION_DATE, news.getCreationDate());
		contentValues.put(DBHelper.COLUMN_NEWS_PUBLICATION_DATE, news.getPublicationDate());
		contentValues.put(DBHelper.COLUMN_NEWS_CATEGORY, news.getCategory());
		contentValues.put(DBHelper.COLUMN_NEWS_SHORT_TEXT, news.getShortText());
		contentValues.put(DBHelper.COLUMN_NEWS_FULL_TEXT, news.getFullText());
		contentValues.put(DBHelper.COLUMN_NEWS_IMAGE, news.getImage());
		contentValues.put(DBHelper.COLUMN_NEWS_METADESC, news.getMetaDesc());
		contentValues.put(DBHelper.COLUMN_NEWS_METAKEY, news.getMetaKey());
		contentValues.put(DBHelper.COLUMN_NEWS_METADATA, news.getMetaData());
		
		context.getContentResolver().insert(NewsProvider.CONTENT_URI, contentValues);
	}
	
	public void insertAll(List<News> news) {
		ContentValues[] contentValues = new ContentValues[news.size()];
		
		int i = 0;
		for(News n: news) {
			contentValues[i] = new ContentValues();
			contentValues[i].put(DBHelper.COLUMN_NEWS_TITLE, n.getTitle());
			contentValues[i].put(DBHelper.COLUMN_NEWS_URL, n.getUrl());
			contentValues[i].put(DBHelper.COLUMN_NEWS_CREATION_DATE, n.getCreationDate());
			contentValues[i].put(DBHelper.COLUMN_NEWS_PUBLICATION_DATE, n.getPublicationDate());
			contentValues[i].put(DBHelper.COLUMN_NEWS_CATEGORY, n.getCategory());
			contentValues[i].put(DBHelper.COLUMN_NEWS_SHORT_TEXT, n.getShortText());
			contentValues[i].put(DBHelper.COLUMN_NEWS_FULL_TEXT, n.getFullText());
			contentValues[i].put(DBHelper.COLUMN_NEWS_IMAGE, n.getImage());
			contentValues[i].put(DBHelper.COLUMN_NEWS_METADESC, n.getMetaDesc());
			contentValues[i].put(DBHelper.COLUMN_NEWS_METAKEY, n.getMetaKey());
			contentValues[i].put(DBHelper.COLUMN_NEWS_METADATA, n.getMetaData());
			i++;
		}
		
		context.getContentResolver().bulkInsert(NewsProvider.CONTENT_URI, contentValues);
	}
	
	public List<News> getAllNews() {
		List<News> newsList = new ArrayList<News>();
		String[] columns = {DBHelper.COLUMN_NEWS_TITLE, DBHelper.COLUMN_NEWS_URL, DBHelper.COLUMN_NEWS_CREATION_DATE, DBHelper.COLUMN_NEWS_PUBLICATION_DATE,
				DBHelper.COLUMN_NEWS_CATEGORY, DBHelper.COLUMN_NEWS_SHORT_TEXT, DBHelper.COLUMN_NEWS_FULL_TEXT, DBHelper.COLUMN_NEWS_IMAGE,
				DBHelper.COLUMN_NEWS_METADESC, DBHelper.COLUMN_NEWS_METAKEY, DBHelper.COLUMN_NEWS_METADATA};
		Cursor cursor = context.getContentResolver().query(NewsProvider.CONTENT_URI, columns, null, null, null);
		
		while(cursor.moveToNext()) {
			News news = new News();
			news.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_TITLE)));
			news.setUrl(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_URL)));
			news.setCreationDate(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_CREATION_DATE)));
			news.setPublicationDate(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_PUBLICATION_DATE)));
			news.setCategory(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_CATEGORY)));
			news.setShortText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_SHORT_TEXT)));
			news.setFullText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_FULL_TEXT)));
			news.setImage(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_IMAGE)));
			news.setMetaDesc(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_METADESC)));
			news.setMetaKey(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_METAKEY)));
			news.setMetaData(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEWS_METADATA)));
			
			newsList.add(news);
		}
		cursor.close();
		
		return newsList;
	}
	
	public void deleteAll() {
		context.getContentResolver().delete(NewsProvider.CONTENT_URI, null, null);
	}
}
