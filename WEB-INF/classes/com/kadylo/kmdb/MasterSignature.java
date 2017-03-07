/** @author Illya Piven
 * This class is used for signatures and its histories
 */
package com.kadylo.kmdb;

import java.util.Date;

abstract class MasterSignature{
	String comment;
	Commander owner;
	Date applied;
	String password;
	
	MasterSignature(){
		//nothing
	};

	MasterSignature(Commander owner, String comment, String password){
		//nothing
	};
	MasterSignature(Commander owner, String password){
		//nothing
	};

	abstract String getComment();
	abstract boolean isOwner(Commander candidate);
	abstract Date getApplied();
	abstract String getPassword();
}