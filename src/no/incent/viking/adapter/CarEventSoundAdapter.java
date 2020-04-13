package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.CarEventSound;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.EditText;
import android.media.MediaPlayer;
import android.util.Log;

public class CarEventSoundAdapter extends ArrayAdapter<CarEventSound> {
	private final String TAG = "VIKING";
	private Context context;
	private int layoutResourceId;
	private List<CarEventSound> carEventSounds;
	
	public CarEventSoundAdapter(Context context, int layoutResourceId, List<CarEventSound> carEventSounds) {
		super(context, layoutResourceId, carEventSounds);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.carEventSounds = carEventSounds;
	}
	
	private class ViewHolder {
		public ImageButton play_recorded_sound;
		public ImageButton recorded_sound_delete;
		public EditText recorded_sound_name;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		final CarEventSound carEventSound = carEventSounds.get(position);
		
		if(v == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(layoutResourceId, null);
			holder.play_recorded_sound = (ImageButton) v.findViewById(R.id.play_recorded_sound);
			holder.recorded_sound_delete = (ImageButton) v.findViewById(R.id.recorded_sound_delete);
			holder.recorded_sound_name = (EditText) v.findViewById(R.id.recorded_sound_name);
			
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		holder.play_recorded_sound.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(carEventSound.getPath() != null) {
						MediaPlayer mediaPlayer = new MediaPlayer();
						mediaPlayer.setDataSource(carEventSound.getPath());
						mediaPlayer.prepare();
						mediaPlayer.start();
					}
				} catch(IOException ex) {
					Log.e(TAG, ex.getMessage());
				}
			}
		});
		holder.recorded_sound_name.setText(carEventSound.getName() + ".3gp");
		holder.recorded_sound_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				carEventSounds.remove(carEventSound);
				CarEventSoundAdapter.this.notifyDataSetChanged();
			}
		});
		return v;
	}
}
