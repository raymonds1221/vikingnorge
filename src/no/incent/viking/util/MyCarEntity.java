package no.incent.viking.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class MyCarEntity extends LinearLayout {
	protected MyCarEntity next;
	
	public MyCarEntity(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setSuccessor(MyCarEntity next) {
		this.next = next;
	}
	
	public abstract void load();
	public abstract void setOwnerId(int ownerId);
}
