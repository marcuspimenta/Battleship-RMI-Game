package com.battleship.manager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.battleship.manager.IManager.OnManagerOnMessageListener;

public class RMICommunicationImpl extends UnicastRemoteObject implements RMICommunication{

	private static final long serialVersionUID = 1179227415408023062L;
	
	private OnManagerOnMessageListener onManagerOnMessageListener;
	
	public RMICommunicationImpl(OnManagerOnMessageListener onManagerOnMessageListener) throws RemoteException {
		super();
		
		this.onManagerOnMessageListener = onManagerOnMessageListener;
	}

	@Override
	public void onMessageListener(byte[] command) {
		onManagerOnMessageListener.onMessageListener(command);
	}

}