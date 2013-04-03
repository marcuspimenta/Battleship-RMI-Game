package com.battleship.manager;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 19:21:12 02/04/2013
 */
public interface RMICommunication extends Remote {

	public abstract void onMessageListener(byte[] command) throws RemoteException;
	
}