package ro.pirvulescusurdeanu.septica.activities;

import ro.pirvulescusurdeanu.septica.controllers.MainController;
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
        
        // La creare este actualizata si activitatea curenta interna
        MainController.getInstance().setCurrentActivity(this);
        
        // Mai exista si alte lucruri care trebuie facute la crearea activitatii?
        afterCreate(savedInstanceState);
    }
	
	protected abstract void afterCreate(Bundle savedInstanceState);
}
