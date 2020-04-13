package no.incent.viking.widget;

import java.util.ArrayList;
import java.util.List;

import no.incent.viking.R;
import no.incent.viking.pojo.DamagePicture;
import no.incent.viking.adapter.DamagePictureAdapter;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class DamageReportPage5 extends Fragment {
	private GridView damage_pictures;
	public final List<DamagePicture> damagePictures = new ArrayList<DamagePicture>() {
		private static final long serialVersionUID = 1L;
		{
			add(new DamagePicture("Bilens front"));
			add(new DamagePicture("Bilens bak"));
			add(new DamagePicture("Skade pa din bil"));
			add(new DamagePicture("Skade pa motpart"));
			add(new DamagePicture("Skade bremselengde"));
			add(new DamagePicture("Gateskilt/Kryss"));
			add(new DamagePicture("Værforhold"));
			add(new DamagePicture("Veidekke"));
			add(new DamagePicture("Lysforhold"));
			add(new DamagePicture("Annet"));
			add(new DamagePicture("Annet2"));
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.damage_report_page5, container, false);
		damage_pictures = (GridView) view.findViewById(R.id.damage_pictures);
		DamagePictureAdapter adapter = new DamagePictureAdapter(getActivity(), R.layout.list_damage_picture, damagePictures);
		damage_pictures.setAdapter(adapter);
		
		damage_pictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				((DamageReportCallback) getActivity()).onDamagePictureClick(position);
			}
		});
		
		return view;
	}
	
	public void updateDamagePictures() {
		damage_pictures = (GridView) getView().findViewById(R.id.damage_pictures);
		DamagePictureAdapter adapter = new DamagePictureAdapter(getActivity(), R.layout.list_damage_picture, damagePictures);
		damage_pictures.setAdapter(adapter);
	}
	
	public static interface DamageReportCallback {
		public void onDamagePictureClick(int position);
		public void onDamageSuccess();
	}
}
