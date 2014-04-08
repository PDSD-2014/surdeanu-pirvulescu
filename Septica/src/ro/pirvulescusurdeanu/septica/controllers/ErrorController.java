package ro.pirvulescusurdeanu.septica.controllers;

import java.util.ArrayList;

public class ErrorController {
	private static ErrorController instance;
	private final ArrayList<Throwable> errors;
	
	private ErrorController() {
		errors = new ArrayList<Throwable>();
	}
	
	public static ErrorController getInstance() {
		if (instance == null) {
			instance = new ErrorController();
		}
		return instance;
	}
	
	/**
	 * Adauga o noua exceptie in cadrul vectorului curent de exceptii.
	 * 
	 * @param tr
	 * 		Exceptia dorita spre a fi inregistrata
	 */
	public synchronized void addError(Throwable tr) {
		errors.add(tr);
	}
	
	/**
	 * Intoarce o lista cu toate exceptiile gasite si le sterge din memoria 
	 * interna.
	 * 
	 * @return
	 */
	public synchronized Throwable[] getErrors() {
		Throwable[] exceptions = (Throwable[]) errors.toArray();
		errors.clear();
		return exceptions;
	}
}
