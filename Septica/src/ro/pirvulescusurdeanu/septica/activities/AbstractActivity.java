package ro.pirvulescusurdeanu.septica.activities;

import android.app.Activity;
import android.os.Bundle;

public abstract class AbstractActivity extends Activity {
	private final int layoutResID;
	
	public AbstractActivity(int layoutResID) {
		this.layoutResID = layoutResID;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        
        afterCreate(savedInstanceState);
    }
	
	protected abstract void afterCreate(Bundle savedInstanceState);
}
