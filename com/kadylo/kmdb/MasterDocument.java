/** @author Illya Piven
 * This class is used for orders. Contains scan + metadata
 */
package com.kadylo.kmdb;

import java.io.File;
import java.util.Date;

abstract class MasterDocument {
	int department; 					// YY
	int number; 					// XXXX
	Date date;					// When it was created IRL
	String title;
	Commander producer;				// director of department that 
							// created the document

	Soldier  star;					// executor of IRL document
	File scanned;
	
	/*constructors*/
	MasterDocument(){

	}

	MasterDocument(int dep, 
		int num, 
		Date date, 
		Commander comm, 
		Soldier sol, 
		String title){
		// here's nothing
	}
	
	MasterDocument(int dep, 
		int num, 
		Date date, 
		Commander comm, 
		Soldier sol, 
		String title, 
		File scanned){
		// here's nothing
	}
	
	/*getters*/
	abstract int getDepartment();
	abstract int getNumber();
	abstract String getTitle();
	abstract Commander getProducer();
	abstract Soldier getStar();
	abstract File getScan();
}