package com.battleship.rmi;

import java.rmi.Naming;
import java.rmi.Remote;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 19:18:55 02/04/2013
 */
public class RMIClient {

	public Remote lookup(String url){
		Remote remote = null;
		
		try {
			remote = Naming.lookup(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return remote;
	}
	
}