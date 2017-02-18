/** @author Illya Piven
 * This class is used for signatures and its histories
 * Since there is no any transfer of documents,
 * there is no need to make it explicitly correct
 * to the common canvas of electronic signature.
 */
package com.kadylo.kmdb;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Signature extends MasterSignature{
	private static final DateFormat df = new SimpleDateFormat(
		"dd/MM/yyyy Â HH:mm"
	);	

	/* Assuming that NO_PASSWORD will never be repeated as actual password*/
	private static final String NO_PASSWORD = 
		"SOGHOIEG2039o283idhvop02kdkiiwwueue1818883HGFF985SKoqo2o283H1883947F85SKoqo2o283idhvop02kdkiiwwueue1818883HGFF985SKoqo2o283H1883947FCCNC";
	
	String comment;
	Card card;				// Here it is applied
						// After applying card and 
						// signature become a one 
						// single piece?????????????????????????
	Commander owner;
	Date applied;
	String password;
	boolean exists;
	
	/* Creating existing signature with comment*/
	Signature(Commander owner, String comment, String password){
		super(owner, comment, password);
		if (owner == null || comment == null || password == null) 
			throw new NullPointerException(
				"Failed to construct Signature with comment"
			);
		this.card = card;
		this.comment = comment;
		this.owner = owner;
		this.password = password;
		exists = true;
		applied = new Date();
	};
	
	/* Creating empty signature without comment*/
	Signature(Commander owner){
		if (owner == null)
			throw new NullPointerException(
				"Failed to construct Signature without comment"
			);
		comment = "";
		this.card = card;
		this.owner = owner;
		this.password = NO_PASSWORD;
		exists = false;
		applied = null;
	};

	/* This method returns comment to the signature if it has one. 
	 * Otherwise it returns empty String
	 */
	String getComment(){
		return comment;
	};

	boolean isOwner(Commander candidate){
		if (candidate == owner)		//TODO Verify with equals
			return true;
		else
			return false;
	};

	Date getApplied(){
		return applied;
	};

	/*equals & hashCode*/
	@Override
	public boolean equals(Object o){
		if (o == null)
			return false;
		if (!(o instanceof Signature))
			return false;
		Signature sig = (Signature) o;
		if (sig.hashCode() != this.hashCode())
			return false;
		try{
			// here it is safe to do this way because if first statement is false
			// second wouldn't be evaluated
			// so if second throws an exception, first part is true
			if (sig.doesExist() == this.doesExist()	&& sig.getPassword().equals(this.getPassword()))
				return true;
		} catch (NullPointerException npe){
			if (sig.getAppliedString().equals(this.getAppliedString()))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode(){
		int hash = 90;
		int i;
		if (exists == true)
			i = 1;
		else
			i = 0;
		return 7*hash + password.hashCode() + i;
	}


	/* Returns string date when signature has been applied if exists.
	 * Otherwise returns an empty string;
	 */
	String getAppliedString(){
		if (applied == null) 
			return ""; 
		else
			return df.format(applied);
	};

	String getPassword(){
		if (password.equals(NO_PASSWORD))
			throw new NullPointerException(
				"Trying to access non-existend password"
			);
		else 
			return password;
	};

	boolean doesExist(){
		return exists;
	};
	
	/* Version that does not add comment so that getComment would still return empty string*/	
	void apply(String password){
		this.password = password;
		exists = true;
		applied = new Date();
	};

	/* Version that adds comment*/
	void apply(String password, String comment){
		this.password = password;
		this.comment = comment;
		exists = true;
		applied = new Date();
	};

	// Test
	public static void main(String[] args){
		System.out.println("=========Testing Signature.class=========\n");
		Commander c1 = new Commander();
		System.out.println("Created commander 1");
		Commander c2 = new Commander();
		System.out.println("Created commander 2");
		Commander c0 = null;
		System.out.println("Created commander null");

		String comm1 = "hello comment";
		String comm2 = "hello againg comment";
		String comm0 = null;

		String password1 = "000123";
		String password2 = "321000";

		System.out.println("Creating signature with comment");
		Signature s1 = new Signature(c1, comm1, password1);
		System.out.println("Created signature with comment");
		System.out.println("Creating signature without comment");
		Signature s2 = new Signature(c2);
		System.out.println("Created signature without comment");
		System.out.println("=======Signature1.equals(Signature2): " + s1.equals(s2));
		System.out.println("S1.hashCode(): " + s1.hashCode());
		System.out.println("S2.hashCode(): " + s2.hashCode());
		Signature s222 = new Signature(c2);
		System.out.println("S222.hashCode(): " + s222.hashCode());
		System.out.println("=======Signature222.equals(Signature2): " + s222.equals(s2));
		s222.apply("hellopassword");
		System.out.println("now applied s222");
		System.out.println("=======Signature222.equals(Signature2): " + s222.equals(s2));
		System.out.println("S222.hashCode(): " + s222.hashCode());

		System.out.println("Creating signature with null comment, assuming exeption");
		try{
			Signature s3 = new Signature(c1, comm0, password2);
		} catch (NullPointerException e){
			System.out.println(e.toString());
		};
		System.out.println("Creating signature without comment with null commander, assuming exception");
		try{
			Signature s4 = new Signature(c0);
		} catch (NullPointerException e){
			System.out.println(e.toString());
		};
		System.out.println("Creating signature with no password and trying to get it, assuming exception");
		try{
			Signature s4 = new Signature(c1);
			System.out.println(s4.exists);
			String s = s4.getPassword();
		} catch (NullPointerException e){
			System.out.println(e.toString());
		};

		System.out.println("Applying signature S1");
		s1.apply("123");
		System.out.println("Applied signature S1");
		
		System.out.println("S1 applied: " + s1.getAppliedString());
		System.out.println("S1 password: " + s1.getPassword());
		System.out.println("S1 comment: " + s1.getComment());
		System.out.println("S1 is onwer c1? " + s1.isOwner(c1));
		System.out.println("S1 is owner c2? " + s1.isOwner(c2));

		System.out.println("S2 applied: " + s2.getAppliedString());
		try{
			System.out.println("S2 password: " + s2.getPassword());
		} catch (NullPointerException npe){
			System.out.println("No password");
		}
		System.out.println("S2 comment: " + s2.getComment());
		System.out.println("S2 is onwer c1? " + s2.isOwner(c1));
		System.out.println("S2 is owner c2? " + s2.isOwner(c2));
		System.out.println("=========Signature.class tested=========\n");
	};
}