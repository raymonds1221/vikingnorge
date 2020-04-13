package no.incent.viking.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.content.FileBody;

import android.util.Log;

public class HttpRequest {
	public static final String TAG = "VIKING";
	public static final int GET = 1;
	public static final int POST = 2;
	
	private HttpRequest() {};
	
	public static HttpRequest getInstance() {
		return new HttpRequest();
	}
	
	private InputStream get(String url) 
			throws URISyntaxException, ClientProtocolException, IOException {
		HttpParams params = new BasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, registry);
		HttpClient client = new DefaultHttpClient(cm, params);
		HttpGet request = new HttpGet();
		
		request.setURI(new URI(url));
		HttpResponse response = client.execute(request);
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		
		return null;
	}
	
	private InputStream post(String url, List<NameValuePair> queries)
		throws URISyntaxException, ClientProtocolException, IOException {
		HttpParams params = new BasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, registry);
		HttpClient client = new DefaultHttpClient(cm, params);
		HttpPost request = new HttpPost();
		
		request.setURI(new URI(url));
		request.setEntity(new UrlEncodedFormEntity(queries,"UTF-8"));
		
		HttpResponse response = client.execute(request);
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		
		return null;
	}
	
	public synchronized String send(String url, List<NameValuePair> queries, int method) {
		InputStream is = null;
		BufferedReader reader = null;
		
		try {
			switch(method) {
				case GET:
					is = get(url);
					break;
				case POST:
					is = post(url, queries);
					break;
			}
			
			if(is != null) {
				reader = new BufferedReader(
								new InputStreamReader(is));
				
				String s = "";
				StringBuffer sb = new StringBuffer();
				
				while((s = reader.readLine()) != null) {
					sb.append(s);
				}
				
				return sb.toString();
			}
		} catch(URISyntaxException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(ClientProtocolException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		} finally {
			try {
				if(is != null) {
					is.close();
				}
			} catch(IOException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return null;
	}
	
	public synchronized byte[] send(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		
		try {
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toByteArray(response.getEntity());
			}
		} catch(URISyntaxException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(ClientProtocolException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		return null;	
	}
	
	public synchronized String sendMultipart(String url, File file, Map<String, String> queries) {
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		
		try {
			MultipartEntity entity = new MultipartEntity();
			entity.addPart("postedfile", new FileBody(file));
			
			Set<String> keys = queries.keySet();
			Iterator<String> iter = keys.iterator();
			
			while(iter.hasNext()) {
				String key = iter.next();
				if(queries.get(key) != null) {
					StringBody sb = new StringBody(URLEncoder.encode(queries.get(key).toString()));
					entity.addPart(key, sb);
				}
			}
			request.setEntity(entity);
			
			HttpResponse response = client.execute(request);
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
											new InputStreamReader(response.getEntity().getContent()));
				String tempStr = "";
				StringBuffer sb = new StringBuffer();
				
				while((tempStr = reader.readLine()) != null) {
					sb.append(tempStr);
				}
				
				return sb.toString();
			}
		} catch(UnsupportedEncodingException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(ClientProtocolException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		return null;
	}
}
