package com.battleship.manager;

import java.rmi.RemoteException;

import com.battleship.manager.IManager.OnManagerOnMessageListener;
import com.battleship.rmi.RMIServer;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 20:05:30 02/04/2013
 */
public class ManagerRMIServer {

	private String url;
	
	private RMIServer rmiServer;
	private RMICommunication rmiCommunication;
	
	public ManagerRMIServer(String url, OnManagerOnMessageListener onManagerOnMessageListener){
		this.url = url;
		rmiServer = new RMIServer();

		try {
			rmiCommunication = new RMICommunicationImpl(onManagerOnMessageListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void createRegistry(){
		rmiServer.createRegistry(1090);
	}
	
	public void rebind(){
		rmiServer.rebind(url, rmiCommunication);
	}
	
	public void unbind(){
		rmiServer.unbind(url);
	}
	
}