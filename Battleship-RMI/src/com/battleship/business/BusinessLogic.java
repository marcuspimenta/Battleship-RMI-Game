package com.battleship.business;

import java.awt.Color;

import com.battleship.components.Component;
import com.battleship.manager.IManager.OnManagerOnMessageListener;
import com.battleship.manager.IManager.OnManagerPrintMsgConsole;
import com.battleship.manager.ManagerRMIClient;
import com.battleship.manager.ManagerRMIServer;
import com.battleship.protocol.ActionCommand;
import com.battleship.protocol.Command;
import com.battleship.view.WindowBuilder;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 22:47:37 30/01/2013
 */
public class BusinessLogic implements OnManagerOnMessageListener, 
									  OnManagerPrintMsgConsole,
									  ActionsCallback{

	private int moves_sent = 0;
	private int responses_receiver = 0;

	private int NUMBER_MOVES = 3;
	private int NUMBER_RESPONSES = 3;
	
	private ManagerRMIServer managerRMIServer;
	private ManagerRMIClient managerRMIClient;
	
	private Command command;
	private WindowBuilder windowBuilder;
	
	public BusinessLogic(){
		command = new Command();
		windowBuilder = new WindowBuilder(this);
	}
	
	public void showWindow(){
		windowBuilder.printTabuleiro();
		
		managerRMIServer = new ManagerRMIServer(WindowBuilder.urlYourServer, this);
		managerRMIServer.createRegistry();
	}
	
	@Override
	public void onActionSelected(Action action) {
		startAction(action);
	}

	@Override
	public void onSendMessageChat(String msg) {
		sendCommand(command.formCommand(ActionCommand.MSG_CHAT.getActionCommand(), msg.getBytes()));
	}

	@Override
	public void onSendCoordinateSquare(int row, int column) {
		
		if(moves_sent < NUMBER_MOVES && responses_receiver == 0 && managerRMIClient != null && managerRMIClient.getRMICommunication() != null){
			if(!windowBuilder.getBoardSercondary()[row][column].isFill()){
				sendCommand(command.formCommand(ActionCommand.XY_SQUARE.getActionCommand(), new byte[]{(byte)row, (byte)column}));			
			}else{
				windowBuilder.printMsgDisplay("You wasted a move");
				sendCommand(command.formCommand(ActionCommand.LOST_PLAY.getActionCommand(), new byte[]{0}));	
			}
			
			moves_sent++;
		}
	}
	
	@Override
	public void onMessageListener(byte[] message) {
		manageCommand(message);
	}

	@Override
	public void onPrintMsgConsole(String message) {
		windowBuilder.printMsgDisplay(message);
	}
	
	public void manageCommand(byte[] msgReceiver){
		int actionCommand = command.getActionCommand(msgReceiver);
		
		if(actionCommand == ActionCommand.MSG_CHAT.getActionCommand()){
			byte[] content = command.getContentCommand(msgReceiver);
			
			if(content != null){
				windowBuilder.printMsgDisplay("Opponent: " + new String(content));
			}
			
		}else if(actionCommand == ActionCommand.XY_SQUARE.getActionCommand()){
			byte[] content = command.getContentCommand(msgReceiver);
			
			increaseResponsePlay();
			
			if(windowBuilder.getBoard().getValueSquare(content[0], content[1])){
				Component component = windowBuilder.getBoard().getComponent(content[0], content[1]);
				
				windowBuilder.getBoard().immersePiece(content[0], content[1], false);
				windowBuilder.getBoard().setColorSquare(content[0], content[1], Color.RED);
				
				if(windowBuilder.getBoard().verifyComponentsSubmerged()){
					if(windowBuilder.getBoard().verifyComponentKill(component)){
						sendCommand(command.formCommand(ActionCommand.RESPONSE_XY.getActionCommand(), new byte[]{1, content[0], content[1]}, ("You sank :" + component.getName()).getBytes()));
					}else{
						sendCommand(command.formCommand(ActionCommand.RESPONSE_XY.getActionCommand(), new byte[]{1, content[0], content[1]}, ("You hit :" + component.getName()).getBytes()));
					}
				}else{
					sendCommand(command.formCommand(ActionCommand.WIN_GAME.getActionCommand(), new byte[]{0}));
					windowBuilder.quitGame();
					closeCommunication();
					windowBuilder.printMsgDisplay("You lost the game\nSearch a new opponent");
				}
				
			}else{
				windowBuilder.getBoard().setColorSquare(content[0], content[1], Color.BLUE);
				sendCommand(command.formCommand(ActionCommand.RESPONSE_XY.getActionCommand(), new byte[]{0, content[0], content[1]}));
			}
			
		}else if(actionCommand == ActionCommand.RESPONSE_XY.getActionCommand()){
			byte[] content = command.getContentCommand(msgReceiver);
			
			if(content[0] == 1){
				windowBuilder.printMsgDisplay(new String(command.getContentAuxComand(msgReceiver)));
				windowBuilder.setColorSquare(content[1], content[2], Color.RED);
			}else{
				windowBuilder.setColorSquare(content[1], content[2], Color.BLUE);
			}
			
			if(moves_sent == NUMBER_MOVES){
				windowBuilder.printMsgDisplay("Now who is the opponent plays");
			}
			
		}else if(actionCommand == ActionCommand.WIN_GAME.getActionCommand()){
			windowBuilder.quitGame();
			closeCommunication();
			windowBuilder.printMsgDisplay("Congratulations! You won the game\nSearch a new opponent");
			
		}else if(actionCommand == ActionCommand.LOST_PLAY.getActionCommand()){
			increaseResponsePlay();
		}else if (actionCommand == ActionCommand.ABORT_GAME.getActionCommand()){
			closeCommunication();
			windowBuilder.printMsgDisplay("Your opponents abort the game");
		}else {
			windowBuilder.printMsgDisplay("Invalid message");
		}
	}
	
	public void increaseResponsePlay(){
		responses_receiver++;
		
		if(responses_receiver == NUMBER_RESPONSES){
			windowBuilder.printMsgDisplay("Your turn to play");
			moves_sent = 0;
			responses_receiver = 0;
		}
	}
	
	public void sendCommand(byte[] command){
		if(managerRMIClient != null){
			managerRMIClient.sendCommand(command);
		}else{
			windowBuilder.printMsgDisplay("No conection");
		}
	}
	
	private void startAction(Action action){
		switch (action) {
			case START_CLIENT:
				startClient();
				break;
			
			case QUIT_GAME:
				if(managerRMIClient != null){
					sendCommand(command.formCommand(ActionCommand.ABORT_GAME.getActionCommand(), new byte[]{0}));
				}
				
				closeCommunication();
				windowBuilder.printMsgDisplay("You abort the game");
				break;
		}
	}
	
	private void startClient(){
		managerRMIServer.rebind();
		
		managerRMIClient = new ManagerRMIClient(WindowBuilder.urlServer);
		managerRMIClient.setPrintMsgConsole(this);
		managerRMIClient.startVerify();
		
		windowBuilder.printMsgDisplay("Waiting opponent");
	}
	
	private void closeCommunication(){
		moves_sent = 0;
		responses_receiver = 0;
		
		managerRMIServer.unbind();
		managerRMIClient.stopVerify();
		managerRMIClient = null;
	}

}