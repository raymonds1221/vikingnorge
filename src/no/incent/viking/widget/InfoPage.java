package no.incent.viking.widget;

import no.incent.viking.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.Html;

public class InfoPage extends LinearLayout {
	private final String info = "Ved å trykke på plusstegnet får du ned en rullgardin der du kan du bestille veihjelp dersom uhellet er ute, fylle ut de viktigste punktene i skadeskjemaet eller få førstehjelpstips. Rullgardinen kan du dra ned uansett hvor i Appen du befinner deg.<br/><br/>" + 
								"Bestill veihjelp<br/>" +
								"Dersom uhellet er ute kan du med få taste-trykk bestille veihjelp via Vikings App. Viking vil motta din henvendelse direkte og formidle videre til nærmeste Viking bil. Viking vil ringe deg opp igjen umiddelbart for å bekrefte opplysningene.<br/><br/>" +
								"Skademeldingsskjema<br/>" +
								"Dersom du har vært ute for en alvorlig kollisjon eller en mindre bulking med en annen part er du nødt til å fylle ut et skadeskjema. Det er ikke alltid man har med seg skadeskjema i bilen, så nå har Viking gjort det enkelt for deg å fylle ut de viktigste punktene uten skjemaet mens du fortsatt er på skadestedet. Du kan dermed gå hjem og fylle ut skadeskjemaet i fred og ro, med alle de viktigste opplysningene og med dokumentasjon i lomma.<br/><br/>" +
								"Førstehjelp<br/>" +
								"Viking Redningstjeneste leverer i samarbeid med Røde Kors førstehjelpstips i tilfelle du skulle trenge veiledning ved et ulykkessted. Her lærer du hvordan du skal legge en person i stabilt sideleie, sikre frie luftveier, utføre hjerte/lunge redning og varsle dersom du trenger assistanse.<br/><br/>" +
								"Bilioteket<br/>" +
								"Har du vognkortet på et sted, førerkortet på et annet sted og serviceheftet på et tredje sted? Hos oss kan du lagre alt i en og samme mappe! Bilioteket gir deg muligheten til å scanne eller ta bilde av alle dokumenter som har med bilen og gjøre, registrere hvem som disponerer bilen, samt dele informasjon om bilen med de du måtte ønske. I tillegg kan Appen hjelpe deg å fylle ut all informasjon om bilen din gjennom oppslag i motorvognregisteret. I Bilioteket kan du også lagre telefonnummeret til ditt faste verksted, venner og familie eller andre disponenter av bilen.<br/><br/>" +
								"Venn av Viking<br/>" +
								"Trenger du en Venn i nøden? En som hjelper deg når uhellet er ute? Som Venn av Viking vil du få prioritet på telefonen, 10 % rabatt på veihjelp og en hel rekke fordeler du kan lese mer om her.<br/><br/>" +
								"Veimeldinger<br/>" +
								"Vil du slippe å stå i kø på vei hjem fra jobben eller vite om du må legge om ruten? Viking og trafikkflyt gir deg alt du trenger å vite om forholdene på veien nettopp der hvor du ferdes.<br/><br/>" +
								"EU kontroll<br/>" +
								"Det er ikke alltid like lett å holde oversikt over når du hadde bilen på EU kontroll sist eller når den skal på EU kontroll neste gang. Med Vikings EU kontroll, beregnes nøyaktig dato for siste frist du må få bilen EU godkjent. Ring verkstedet ditt og bestill time i samme slengen!<br/><br/>" +
								"Spill<br/>" +
								"Venter du på Viking, står i kø eller kjeder deg i baksetet? Prøv vår tradisjonelle bilbingo! ";

	public InfoPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.info_page, this);
		
		((TextView) findViewById(R.id.info_content)).setText(Html.fromHtml(info));
	}
}
