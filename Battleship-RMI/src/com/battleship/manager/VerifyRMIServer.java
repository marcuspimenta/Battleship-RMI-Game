package com.battleship.manager;

import java.rmi.Remote;

import com.battleship.manager.IManager.OnManagerVerifyRMIServer;
import com.battleship.rmi.RMIClient;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 19:22:00 02/04/2013
 */
public class VerifyRMIServer implements Runnable{
	
	private String url;
	private RMIClient rmiClient;
	private OnManagerVerifyRMIServer verifyRMIServer;
	
	public VerifyRMIServer(String url, OnManagerVerifyRMIServer verifyRMIServer){
		this.url = url;
		this.verifyRMIServer = verifyRMIServer;
		
		rmiClient = new RMIClient();
	}

	@Override
	public void run() {
		Remote remote = null;
		remote = rmiClient.lookup(url);
		
		if(remote != null){
			verifyRMIServer.verifyRMIServer(remote);
		}
	}

}