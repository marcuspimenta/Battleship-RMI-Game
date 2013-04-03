package com.battleship.manager;

import java.rmi.Remote;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 19:20:46 02/04/2013
 */
public interface IManager {
	
	public interface OnManagerVerifyRMIServer {
		public abstract void verifyRMIServer(Remote remote);		
	}
	
	public interface OnManagerOnMessageListener{
		public abstract void onMessageListener(byte[] message);
	}
	
	public interface OnManagerPrintMsgConsole{
		public abstract void onPrintMsgConsole(String message);
	}
}