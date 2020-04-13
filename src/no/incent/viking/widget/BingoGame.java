package no.incent.viking.widget;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import no.incent.viking.R;
import no.incent.viking.util.FlipAnimation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import android.widget.TextView;
import android.text.Html;

public class BingoGame extends LinearLayout {
	private Context context;
	private List<Integer> bingoRowList;
	private LinearLayout bingoContent;
	private ViewFlipper game_flipper;
	
	public BingoGame(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.bingogame, this);
		
		this.context = context;
		newBingoGame();
		
		game_flipper = (ViewFlipper) findViewById(R.id.game_flipper);
		
		findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newBingoGame();
			}
		});
		
		findViewById(R.id.game_info_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game_flipper.setInAnimation(context, R.anim.slide_left_in);
				game_flipper.setOutAnimation(context, R.anim.slide_left_out);
				game_flipper.showNext();
			}
		});
		
		findViewById(R.id.close_game_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game_flipper.setInAnimation(context, R.anim.slide_right_in);
				game_flipper.setOutAnimation(context, R.anim.slide_right_out);
				game_flipper.showPrevious();
			}
		});
		
		TextView game_info1 = (TextView) findViewById(R.id.game_info1);
		TextView game_info2 = (TextView) findViewById(R.id.game_info2);
		TextView game_info3 = (TextView) findViewById(R.id.game_info3);
		
		game_info1.setText(Html.fromHtml("Venter du på Viking, står i kø eller kjeder deg i baksetet? Prøv vår tradisjonelle bilbingo! "));
		game_info2.setText(Html.fromHtml("Ettersom du får øye på de ulike ikonene langs veien, trykker du på ikonet og det merkes i fargen grønt. Spillet går ut på å få flest grønne ikoner innen man har kommet fram til destinasjon eller innen en definert tidsfrist. "));
		game_info3.setText(Html.fromHtml("For eksempel trykker du bensinpumpe ikonet når du passerer eller ser en bensinpumpe, og du er et steg nærmere i kampen om å gå av med seieren i Vikings bilbingo! Spill med mor, far, bror, søster, venn, eller den som måtte sitte sammen med deg i baksetet. "));
	}
	
	private void newBingoGame(){
		bingoRowList = new ArrayList<Integer>();
		
		for(int i = 1; i <= 12; i++)
			bingoRowList.add(i);
		
		List<Integer> bingoBoxIds = new ArrayList<Integer>();
		
		for(int i = 1; i <= 50; i++)
		{	try {
			    Class res = R.drawable.class;
			    Field field = res.getField("bingo"+i);
			    bingoBoxIds.add(field.getInt(null));
			}
			catch (Exception e) {
			    Log.e("Bingo", "Failure to get drawable id.", e);
			}
		}
		
		Collections.shuffle(bingoBoxIds);
		
		bingoContent = (LinearLayout)findViewById(R.id.bingoContainer);
		bingoContent.removeAllViews();
		
		for(int i = 0; i < 5; i++) {
			LinearLayout row = new LinearLayout(context);
			row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			row.setOrientation(LinearLayout.HORIZONTAL);
			row.setGravity(Gravity.CENTER);
			
			for(int j = i*5; j < (5*(i+1)); j++)
			{	
				FrameLayout frameLayout = new FrameLayout(context);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				frameLayout.setLayoutParams(layoutParams);
				frameLayout.setId(j+1);
				
				ImageButton bingoBox = new ImageButton(context);
				bingoBox.setImageResource(bingoBoxIds.get(j));
				bingoBox.setPadding(2,2,2,2);
				//bingoBox.setId(j+1);
				bingoBox.setClickable(false);
				
				frameLayout.addView(bingoBox);
				try {
					ImageView bingoBoxOn = new ImageView(context);
					Class res = R.drawable.class;
					Field field = res.getField(getResourceNameFromClassByID(R.drawable.class, bingoBoxIds.get(j)) + "_on");
					bingoBoxOn.setImageResource(field.getInt(null));
					bingoBoxOn.setPadding(2, 2, 2, 2);
					bingoBoxOn.setVisibility(View.GONE);
					bingoBoxOn.setClickable(false);
					frameLayout.addView(bingoBoxOn);
				} catch(NoSuchFieldException ex) {
					Log.e("VIKING", ex.getMessage());
				} catch(IllegalAccessException ex) {
					Log.e("VIKING", ex.getMessage());
				}
				frameLayout.setClickable(true);
				
				frameLayout.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						FrameLayout f = (FrameLayout) v;
						final ImageView image1 = (ImageView) f.getChildAt(0);
						final ImageView image2 = (ImageView) f.getChildAt(1);
						
						float centerX = image1.getWidth() / 2.0f;
						float centerY = image1.getWidth() / 2.0f;
						
						FlipAnimation animation = new FlipAnimation(0, -90, centerX, centerY);
						animation.setDuration(400);
						animation.setFillAfter(true);
						animation.setInterpolator(new AccelerateInterpolator());
						animation.setAnimationListener(new Animation.AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								image1.post(new SwapViews(image1, image2));
								
								if (isBingo(bingoContent))
									showBingo();
							}
						});
						
						image1.startAnimation(animation);
						
						v.setEnabled(false);
					}
				}); 
			
				row.addView(frameLayout);
			}
			
			bingoContent.addView(row);
			
		}
	}
	
	private boolean isBingo(View bingoBoard){
		if (!bingoBoard.findViewById(1).isEnabled() && !bingoBoard.findViewById(2).isEnabled() &&
			!bingoBoard.findViewById(3).isEnabled() && !bingoBoard.findViewById(4).isEnabled() &&
			!bingoBoard.findViewById(5).isEnabled() && bingoRowList.contains(1)	)
		{	bingoRowList.remove(bingoRowList.indexOf(1));
			return true;
		}
		
		if (!bingoBoard.findViewById(6).isEnabled() && !bingoBoard.findViewById(7).isEnabled() &&
			!bingoBoard.findViewById(8).isEnabled() && !bingoBoard.findViewById(9).isEnabled() &&
			!bingoBoard.findViewById(10).isEnabled()&& bingoRowList.contains(2) )
		{	bingoRowList.remove(bingoRowList.indexOf(2));
			return true;
		}
		
		if (!bingoBoard.findViewById(11).isEnabled() && !bingoBoard.findViewById(12).isEnabled() &&
			!bingoBoard.findViewById(13).isEnabled() && !bingoBoard.findViewById(14).isEnabled() &&
			!bingoBoard.findViewById(15).isEnabled() && bingoRowList.contains(3))
		{	bingoRowList.remove(bingoRowList.indexOf(3));
			return true;
		}
		
		if (!bingoBoard.findViewById(16).isEnabled() && !bingoBoard.findViewById(17).isEnabled() &&
			!bingoBoard.findViewById(18).isEnabled() && !bingoBoard.findViewById(19).isEnabled() &&
			!bingoBoard.findViewById(20).isEnabled() && bingoRowList.contains(4))
		{	bingoRowList.remove(bingoRowList.indexOf(4));
			return true;
		}
		
		if (!bingoBoard.findViewById(21).isEnabled() && !bingoBoard.findViewById(22).isEnabled() &&
			!bingoBoard.findViewById(23).isEnabled() && !bingoBoard.findViewById(24).isEnabled() &&
			!bingoBoard.findViewById(25).isEnabled() && bingoRowList.contains(5))
		{	bingoRowList.remove(bingoRowList.indexOf(5));
			return true;
		}
		
		if (!bingoBoard.findViewById(1).isEnabled() && !bingoBoard.findViewById(6).isEnabled() &&
			!bingoBoard.findViewById(11).isEnabled() && !bingoBoard.findViewById(16).isEnabled() &&
			!bingoBoard.findViewById(21).isEnabled() && bingoRowList.contains(6))
		{	bingoRowList.remove(bingoRowList.indexOf(6));
			return true;
		}
		
		if (!bingoBoard.findViewById(2).isEnabled() && !bingoBoard.findViewById(7).isEnabled() &&
			!bingoBoard.findViewById(12).isEnabled() && !bingoBoard.findViewById(17).isEnabled() &&
			!bingoBoard.findViewById(22).isEnabled() && bingoRowList.contains(7))
		{	bingoRowList.remove(bingoRowList.indexOf(7));
			return true;
		}
		
		if (!bingoBoard.findViewById(3).isEnabled() && !bingoBoard.findViewById(8).isEnabled() &&
			!bingoBoard.findViewById(13).isEnabled() && !bingoBoard.findViewById(18).isEnabled() &&
			!bingoBoard.findViewById(23).isEnabled() && bingoRowList.contains(8))
		{	bingoRowList.remove(bingoRowList.indexOf(8));
			return true;
		}
		
		if (!bingoBoard.findViewById(4).isEnabled() && !bingoBoard.findViewById(9).isEnabled() &&
			!bingoBoard.findViewById(14).isEnabled() && !bingoBoard.findViewById(19).isEnabled() &&
			!bingoBoard.findViewById(24).isEnabled() && bingoRowList.contains(9))
		{	bingoRowList.remove(bingoRowList.indexOf(9));
			return true;
		}
		
		if (!bingoBoard.findViewById(5).isEnabled() && !bingoBoard.findViewById(10).isEnabled() &&
			!bingoBoard.findViewById(15).isEnabled() && !bingoBoard.findViewById(20).isEnabled() &&
			!bingoBoard.findViewById(25).isEnabled() && bingoRowList.contains(10))
		{	bingoRowList.remove(bingoRowList.indexOf(10));
			return true;
		}
		
		if (!bingoBoard.findViewById(1).isEnabled() && !bingoBoard.findViewById(7).isEnabled() &&
			!bingoBoard.findViewById(13).isEnabled() && !bingoBoard.findViewById(19).isEnabled() &&
			!bingoBoard.findViewById(25).isEnabled() && bingoRowList.contains(11))
		{	bingoRowList.remove(bingoRowList.indexOf(11));
			return true;
		}
		
		if (!bingoBoard.findViewById(5).isEnabled() && !bingoBoard.findViewById(9).isEnabled() &&
			!bingoBoard.findViewById(13).isEnabled() && !bingoBoard.findViewById(17).isEnabled() &&
			!bingoBoard.findViewById(21).isEnabled() && bingoRowList.contains(12))
		{	bingoRowList.remove(bingoRowList.indexOf(12));
			return true;
		}
		
		return false;
	}
	
	private void showBingo(){
		AlertDialog bingoDialog = new AlertDialog.Builder(context).create();  
		
		bingoDialog.setTitle("BINGO!");
		bingoDialog.setMessage("Vil du spille hele brettet?");
		bingoDialog.setButton("Spill", new DialogInterface.OnClickListener() {  
		    @Override  
		    public void onClick(DialogInterface dialog, int which) {  
		    	dialog.dismiss();
		    }  
		}); 
		
		bingoDialog.setButton2("Avbryt", new DialogInterface.OnClickListener() {  
		    @Override  
		    public void onClick(DialogInterface dialog, int which) {
		        newBingoGame();
		    }  
		});
		
		bingoDialog.show();
		
	}
	
	private String getResourceNameFromClassByID(Class<?> aClass, int resourceID) throws IllegalArgumentException{
		
		Field[] drawableFields = aClass.getFields();
		
		for(Field f : drawableFields){
			try {
				if (resourceID == f.getInt(null))
					return f.getName(); // Return the name.
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		throw new IllegalArgumentException();
	}
	
	private class SwapViews implements Runnable {
    	private ImageView image1;
    	private ImageView image2;
    	
    	public SwapViews(ImageView image1, ImageView image2) {
    		this.image1 = image1;
    		this.image2 = image2;
    	}
    	
    	public void run() {
    		float centerX = image1.getWidth() / 2.0f;
    		float centerY = image1.getHeight() / 2.0f;
    		
    		image1.setVisibility(View.GONE);
    		image2.setVisibility(View.VISIBLE);
    		//image2.requestFocus();
    		
    		FlipAnimation animation = new FlipAnimation(90, 0, centerX, centerY);
    		animation.setDuration(400);
    		animation.setFillAfter(true);
    		animation.setInterpolator(new DecelerateInterpolator());
    		
    		image2.startAnimation(animation);
    	}
    }
}
