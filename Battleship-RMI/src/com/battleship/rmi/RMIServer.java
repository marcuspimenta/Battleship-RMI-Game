package com.battleship.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import com.battleship.manager.RMICommunication;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 19:19:08 02/04/2013
 */
public class RMIServer {
	
	public void createRegistry(int port){
		try {
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void rebind(String url, RMICommunication rmiCommunication){
		try {
			Naming.rebind(url, rmiCommunication);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unbind(String url){
		try {
			Naming.unbind(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}