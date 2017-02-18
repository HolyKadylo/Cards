/** @author Illya Piven
 * This class is used for orders. Contains scan + metadata
 */
package com.kadylo.kmdb;

import java.io.File;
import java.util.Date;

public class Document extends MasterDocument{
	private int department; 					// YY
	private int number; 					// XXXX
	private Date date;						// When it was created IRL
	private String title;
	private Commander producer;				// director of department that 
								// created the document

	private Soldier star;					// executor of IRL document
	private File scanned;					// scan of the document created with
								// File (String) constructor
	
	/*constructor*/
	Document (int dep, int num, Date date, Commander comm, Soldier sol, String title, File scanned){
		super(dep,num,date,comm,sol,title,scanned);
		if (dep == 0 
			|| num == 0 
			|| date == null 
			|| comm == null 
			|| sol == null 
			|| title == null 
			|| scanned == null) 
				throw new NullPointerException(
					"Failed to construct document"
				);
		department = dep;
		number = num;
		this.date = date;
		producer = comm;
		star = sol;
		this.title = title;
		this.scanned = scanned;
	};

	/*equals & hashcode*/
	@Override
	public boolean equals(Object o){
		if (o == null)
			return false;
		if(o.hashCode() != this.hashCode())
			return false;
		if (!(o instanceof Document))
			return false;
		Document cand = (Document) o;
		if(cand.getDepartment() == this.department 
			&& cand.getNumber() == this.number)
			return true;
		else
			return false;
	}	
		
	@Override
	public int hashCode(){
		int hash = 43;
		return 8*hash + number + department;
	}
	
	/*getters*/
	int getDepartment(){
		return department;
	};

	int getNumber(){
		return number;
	};

	String getTitle(){
		return title;
	};

	Commander getProducer(){
		return producer;
	};

	Soldier getStar(){
		return star;
	};

	File getScan(){
		return scanned;
	};

	public static void main(String[] args){
		System.out.println("=========Testing Document.class=========\n");
		System.out.println("Creating two documents");
		Document doc1 = new Document (10,4556,new Date(),new Commander(), new Soldier(), "14hh88", new File("Compile.bat"));
		Document doc2 = new Document (11,4533,new Date(),new Commander(), new Soldier(), "14hh88", new File("Compile.bat"));
		System.out.println("Different documents are equal(): " + doc1.equals(doc2));
		Document doc3 = doc1;
		System.out.println("Same documents are equal(): " + doc1.equals(doc3));
		System.out.println("Hashcodes:");
		System.out.println(doc1.hashCode());
		System.out.println(doc2.hashCode());
		System.out.println(doc3.hashCode());
		System.out.println("=========Document.class tested=========\n");
	}
}