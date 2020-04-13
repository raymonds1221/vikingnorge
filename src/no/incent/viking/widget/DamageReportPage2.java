package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.adapter.DamageCauseAdapter;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.HeaderViewListAdapter;

public class DamageReportPage2 extends Fragment {
	private ListView damage_cause_list;
	private final String[] causes = {"Pakert/stod stille i kollisonsøyeblikket",
			"Satte kjøretøyet i bevegelse/åpnet døra",
			"Var i ferd med å stoppe",
			"Kjørte ut fra p-plass/privat område",
			"Kjørte inn på p-plass/privat område",
			"Kjørte inn i en rundkjøring",
			"Kjørte i en rundkjøring",
			"Kjørte på bakfra i samme retning og kjørefelt",
			"Kjørte i samme retning i annet kjørefelt",
			"Skiftet kjørefelt",
			"Kjørte forbi",
			"Svingte til høyre",
			"Svingte til venstre",
			"Rygget",
			"Kom inn på del av veien bestemt for trafikk i motsatt retning",
			"Kom fra høyre i et kryss",
			"Fulgte ikke varsel om vikeplikt eller rødt lys"
	};
	private DamageCauseAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new DamageCauseAdapter(getActivity(), R.layout.list_damage_report, causes);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.damage_report_page2, container, false);
		damage_cause_list = (ListView) view.findViewById(R.id.damage_cause_list);
		
		ImageView imageView = new ImageView(getActivity());
		imageView.setImageResource(R.drawable.nav_icon);
		imageView.setPadding(0, 30, 0, 20);
		LinearLayout linearLayout = new LinearLayout(getActivity());
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		linearLayout.addView(imageView);
		damage_cause_list.addFooterView(linearLayout);
		damage_cause_list.setAdapter(adapter);
		
		return view;
	}
	
	public boolean[] getDamageCauses() {
		 return ((DamageCauseAdapter) ((HeaderViewListAdapter) damage_cause_list.getAdapter()).getWrappedAdapter()).getAllDamageCause();
	}
}
