package com.battleship.manager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.battleship.manager.IManager.OnManagerPrintMsgConsole;
import com.battleship.manager.IManager.OnManagerVerifyRMIServer;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 19:22:21 02/04/2013
 */
public class ManagerRMIClient implements OnManagerVerifyRMIServer{
	
	private final int THREAD_POOL = 1;
	private final int TIMER_REPET = 1;

	private String url;
	private ScheduledExecutorService executor;
	
	private RMICommunication communication;
	
	private OnManagerPrintMsgConsole onManagerPrintMsgConsole;
	
	public ManagerRMIClient(String url){
		this.url = url;
		executor = Executors.newScheduledThreadPool(THREAD_POOL);
	}
	
	public void setPrintMsgConsole(OnManagerPrintMsgConsole onManagerPrintMsgConsole) {
		this.onManagerPrintMsgConsole = onManagerPrintMsgConsole;
	}	
	
	public void startVerify(){
		executor.scheduleWithFixedDelay((new VerifyRMIServer(url, this)), 0, TIMER_REPET, TimeUnit.SECONDS);
	}
	
	public void stopVerify(){
		executor.shutdownNow();
	}
	
	public RMICommunication getRMICommunication(){
		return communication;
	}
	
	public void sendCommand(byte[] command){
		if(communication != null){
			try {
				communication.onMessageListener(command);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}else{
			onManagerPrintMsgConsole.onPrintMsgConsole("No conection");
		}
	}

	@Override
	public void verifyRMIServer(Remote remote) {
		stopVerify();
		
		communication = (RMICommunication)remote;
		onManagerPrintMsgConsole.onPrintMsgConsole("Connection successful");
	}

}