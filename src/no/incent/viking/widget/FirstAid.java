package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.pojo.FirstAidItem;
import no.incent.viking.adapter.FirstAidAdapter;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.Gallery;

public class FirstAid extends LinearLayout {
	private Context context;
	private TabHost firstaid_tabhost;
	private Gallery firstaid_ulykke;
	private Gallery firstaid_hlr;
	private Gallery firstaid_redning;
	private Gallery firstaid_varsling;
	
	public FirstAid(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.first_aid, this);
		
		this.context = context;
		initViews();
		initContents();
		
		firstaid_tabhost.setup();
		setupTab(firstaid_tabhost, "Redning", R.id.firstaid_redning);
		setupTab(firstaid_tabhost, "Varsling", R.id.firstaid_varsling);
		setupTab(firstaid_tabhost, "HLR", R.id.firstaid_hlr);
		setupTab(firstaid_tabhost, "Ulykke", R.id.firstaid_ulykke);
	}
	
	private void initViews() {
		firstaid_tabhost = (TabHost) findViewById(R.id.firstaid_tabhost);
		firstaid_ulykke = (Gallery) findViewById(R.id.firstaid_ulykke);
		firstaid_hlr = (Gallery) findViewById(R.id.firstaid_hlr);
		firstaid_redning = (Gallery) findViewById(R.id.firstaid_redning);
		firstaid_varsling = (Gallery) findViewById(R.id.firstaid_varsling);
	}
	
	private void initContents() {
		List<FirstAidItem> ulykke = new ArrayList<FirstAidItem>() {
			private static final long serialVersionUID = 1L;
			{
				add(new FirstAidItem(R.drawable.ulykke_fig1, "1. Ta ansvar<br/>2. Sikre skadestedet<br/>3. Skaff oversikt, ring 1-1-3<br/>4. Gi livreddende førstehjelp"));
				add(new FirstAidItem(R.drawable.ulykke_fig2, "Undersøk om pasienten er bevisstløs. Det er ofte enklest å komme til pasienten via dørene i baksetet"));
				add(new FirstAidItem(R.drawable.ulykke_fig3, "Du får en god oversikt på skadene dersom du kommer inn døren der pasienten sitter"));
				add(new FirstAidItem(R.drawable.ulykke_fig4, "Dersom pasienten er bevisstløs må du sikre frie luftveier og sjekke om han puster normalt. Hvis mulig må du holde hode til pasienten helt til ambulansepersonellet seier at du kan slippe. Dersom pasienten ikke puster normalt må du ta han ut av bilen umiddelbart"));
			}
		};
		FirstAidAdapter ulykkeAdapter = new FirstAidAdapter(context, ulykke);
		firstaid_ulykke.setAdapter(ulykkeAdapter);
		
		List<FirstAidItem> hlr = new ArrayList<FirstAidItem>() {
			private static final long serialVersionUID = 4611161285464393549L;
			{
				add(new FirstAidItem(R.drawable.hlr_fig1, "Still deg tett inntil pasienten med rak rygg. Plasser hendene på brystet med strake armer"));
				add(new FirstAidItem(R.drawable.hlr_fig2, "Finn det riktige trykkpunktet. Midt på bryst beinet og mellom brystvortene."));
				add(new FirstAidItem(R.drawable.hlr_fig3, "Start med 30 hjertekompresjoner midt på brystbeinet mellom brystvortene."));
				add(new FirstAidItem(R.drawable.hlr_fig4, "Det korrekte trykkpunktet. Midt på bryst beinet og mellom brystvortene."));
				add(new FirstAidItem(R.drawable.hlr_fig5, "Husk strake armer. Trykk ned ca. 5-6 cm i en takt på ca. 100 kompresjoner pr min."));
				add(new FirstAidItem(R.drawable.hlr_fig6, "Gi frie luftveier og 2 innblåsinger. Innblåsingene skal være i ca. 1 sekund og til brystkassen begynner å heve seg"));
				add(new FirstAidItem(R.drawable.hlr_fig7, "Sjekk alltid at brystkassen senker seg etter innblåsing og at du kjenner det kommer luft ut igjen.. Forsett med 30:2"));
			}
		};
		FirstAidAdapter hlrAdapter = new FirstAidAdapter(context, hlr);
		firstaid_hlr.setAdapter(hlrAdapter);
		
		List<FirstAidItem> redning = new ArrayList<FirstAidItem>() {
			private static final long serialVersionUID = -8950529354788062563L;
			{
				add(new FirstAidItem(R.drawable.redning_fig1, "Sjekk bevisstheten, rist forsiktig i pasienten"));
				add(new FirstAidItem(R.drawable.redning_fig2, "Gi frie luftveier ved å bøye hode forsiktig bakover"));
				add(new FirstAidItem(R.drawable.redning_fig3, "Dersom du mistenker skade på nakke, skyves kjeven fremover for å sikre frie luftveier."));
				add(new FirstAidItem(R.drawable.redning_fig4, "Dersom du har problem med fire luftveier, kan du ta tak i kjeven til pasienten og løfte den frem"));
				add(new FirstAidItem(R.drawable.redning_fig5, "Sjekk om pasienten puster ved å legge kinn og øre mot pasientens munn og se om brystkassen hever og senker seg."));
				add(new FirstAidItem(R.drawable.redning_fig6, "Rop etter hjelp/ ring 1-1-3."));
				add(new FirstAidItem(R.drawable.redning_fig7, ""));
				add(new FirstAidItem(R.drawable.redning_fig8, ""));
				add(new FirstAidItem(R.drawable.redning_fig9, ""));
				add(new FirstAidItem(R.drawable.redning_fig10, ""));
				add(new FirstAidItem(R.drawable.redning_fig11, "Dersom pasienten puster normalt, legges han i sideleie på denne måten. Pass på at pasienten har frie luftveier i sideleie."));
				add(new FirstAidItem(R.drawable.redning_fig12, "Sjekk alltid at pasienten puster normalt."));
			}
		};
		FirstAidAdapter redningAdapter = new FirstAidAdapter(context, redning);
		firstaid_redning.setAdapter(redningAdapter);
		
		List<FirstAidItem> varsling = new ArrayList<FirstAidItem>() {
			private static final long serialVersionUID = 8149806611259299906L;
			{
				add(new FirstAidItem(R.drawable.varsling_fig1, "1.  Behold roen<br/>2.  Hvem ringer<br/>3.  Hvor ringer du fra<br/>4.  Hva har skjedd"));
			}
		};
		FirstAidAdapter varslingAdapter = new FirstAidAdapter(context, varsling);
		firstaid_varsling.setAdapter(varslingAdapter);
	}
		
	
	private void setupTab(TabHost mTabHost, final String tag, final int viewId) {
		View tabview = createTabView(mTabHost.getContext(), tag);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(viewId);
		mTabHost.addTab(setContent);
	}
	
	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}
