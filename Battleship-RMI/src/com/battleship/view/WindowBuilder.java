package com.battleship.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import com.battleship.business.Action;
import com.battleship.business.ActionsCallback;
import com.battleship.components.Aircraftcarrier;
import com.battleship.components.Battleship;
import com.battleship.components.Component;
import com.battleship.components.Cruiser;
import com.battleship.components.Piece;
import com.battleship.components.Seaplane;
import com.battleship.components.Submarine;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 16:48:34 30/01/2013
 */
@SuppressWarnings("serial")
public class WindowBuilder extends JFrame implements ActionListener{

	public static String urlServer;
	public static String urlYourServer;
	public static String numberPort;
	
	private Square[][] squareSecondary;
	
	private Board board;
	private JButton send;
	private JTextArea message;
	private JPanel boardSecondary;
	private JTextArea displayAreaMsg;
	private JScrollPane scrollpane;
	private JMenuItem menuAbout, menuExit, menuStartGame, menuRepaint, menuQuitGame;
	
	private final ActionsCallback actionsCallback;

	public WindowBuilder(final ActionsCallback actionsCallback){
		this.actionsCallback = actionsCallback;
	}
	
	public void printTabuleiro(){
		
		board = new Board();
		
		boardSecondary = new JPanel();
		boardSecondary.setBorder(new TitledBorder("Opponent's game"));
		boardSecondary.setLayout(new GridLayout(15, 15, 0, 0));
		
		squareSecondary = new Square[15][15];
		createCleanBoard();

		displayAreaMsg = new JTextArea(10, 30);
		displayAreaMsg.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
		displayAreaMsg.setLineWrap(true);
		displayAreaMsg.setEditable(false);
		
		scrollpane = new JScrollPane(displayAreaMsg);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		message = new JTextArea(1, 25);
		message.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
		message.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		message.setEditable(true);
		
		send = new JButton("Send");
		send.addActionListener(this);
		
		JPanel jPanel = new JPanel();
		jPanel.setSize(1, 1);  
		
		final JPanel panelTextButton = new JPanel();
		panelTextButton.setLayout(new BorderLayout());
		panelTextButton.add(message, BorderLayout.WEST);
		panelTextButton.add(jPanel, BorderLayout.CENTER);
		panelTextButton.add(send, BorderLayout.EAST);
		
		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Arms"));
		panel.setLayout(new GridLayout(3, 2));
		panel.add(showPiecesGame(new Seaplane(1, 1)));
		panel.add(showPiecesGame(new Submarine(1, 2)));
		panel.add(showPiecesGame(new Cruiser(1, 1)));
		panel.add(showPiecesGame(new Battleship(1, 0)));
		panel.add(showPiecesGame(new Aircraftcarrier(1, 0)));
		
		final JPanel panelChat = new JPanel();
		panelChat.add(scrollpane, BorderLayout.CENTER);
		panelChat.add(panelTextButton, BorderLayout.SOUTH);
		panelChat.setBorder(new TitledBorder("Chat"));
		
		final JPanel mainPanel = new JPanel();
		mainPanel.add(board.paintBoard(), BorderLayout.CENTER);
		mainPanel.add(boardSecondary, BorderLayout.CENTER);
		mainPanel.setBorder(new TitledBorder("Players"));
		
		final JMenu optionMenu = new JMenu("Options");
		
		menuStartGame = new JMenuItem("Start game");
		menuStartGame.addActionListener(this);
		
		menuQuitGame = new JMenuItem("Quit game");
		menuQuitGame.addActionListener(this);
		
		menuRepaint = new JMenuItem("New board");
		menuRepaint.addActionListener(this);
		
		menuAbout = new JMenuItem("About");
		menuAbout.addActionListener(this);
		
		menuExit = new JMenuItem("Exit");
		menuExit.addActionListener(this);
		
		optionMenu.add(menuStartGame);
		optionMenu.add(menuQuitGame);
		optionMenu.add(menuRepaint);
		optionMenu.add(menuAbout);
		optionMenu.add(menuExit);

		final JMenuBar menu = new JMenuBar();
		menu.add(optionMenu);
		
		setJMenuBar(menu);
		add(mainPanel, BorderLayout.NORTH);
		add(panelChat, BorderLayout.CENTER);
		add(panel, BorderLayout.WEST);
		setSize(600, 630);
		setTitle("Battleship Game");
		
		setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		
		confEnableButtonsMenu(true);
		
		enterUrlServer();
		numberPort();
	}
	
	private void enterUrlServer() {
		urlYourServer = "";
		
		while(urlYourServer == null || (urlYourServer != null && urlYourServer.trim().equals(""))){
			urlYourServer = JOptionPane.showInputDialog(getContentPane().getParent(),
												   		"Enter url your server (//198.168.1.10/MarcusServer)", 
												   		"Settings url your server",
												   		JOptionPane.QUESTION_MESSAGE);
		}
	}
	
	private void numberPort() {
		numberPort = "";
		
		while(numberPort == null || (numberPort != null && (!numberPort.matches("[0-9]+") || numberPort.trim().equals("")))){
			  numberPort = JOptionPane.showInputDialog(getContentPane().getParent(),
												   		"Enter number port:", 
												   		"Settings number port server",
												   		JOptionPane.QUESTION_MESSAGE);
		}
	}

	public JPanel showPiecesGame(final Component component){
		int rows = 3;
		int cols = 5;
		
		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(component.getName()));
		panel.setLayout(new GridLayout(rows, cols));
		
		final boolean area[][] = new boolean[rows][cols];
		
		for (final Piece piece : component.getPieces()) {
			area[piece.getPosition().getRow()][piece.getPosition().getColumn()] = true;
		}
		
		final Square[][] square = new Square[rows][cols];
		
		for (int row = 0; row < square.length; row++) {
			for (int column = 0; column < square[row].length; column++) {
				if(area[row][column]){
					square[row][column] = new Square(row, column, Color.BLACK, area[row][column], area[row][column]);
				}else{
					square[row][column] = new Square(row, column, Color.BLACK, false, false);
				}
				panel.add(square[row][column]);
			}
		}
		
		return panel;
	}
	
	@Override
	public void actionPerformed(final ActionEvent event) {
		if(event.getSource() == send){
			if(message.getText().length() > 0){
				if(event.getSource().equals(send)){
					printMsgDisplay("You: " + message.getText());
		   			actionsCallback.onSendMessageChat(message.getText());

		   			message.setText("");
				}
			}
			
		}else if(event.getSource() == menuAbout){
			JOptionPane.showMessageDialog(getContentPane(),
										  "BattleShip Game\n\n" +
										  "Autor:        Marcus Pimenta\n" +
										  "email:        mvinicius.pimenta@gmail.com\n" +
										  "Data: 		         april 02, 2013",
										  "Sobre BattleShip", JOptionPane.PLAIN_MESSAGE);
			
		}else if(event.getSource() == menuExit){
			System.exit(0);
			
		}else if(event.getSource() == menuRepaint){
			board.repaintBoard();
			
		}else if(event.getSource() == menuStartGame){
			displayAreaMsg.setText("");
			
			urlServer = JOptionPane.showInputDialog(getContentPane().getParent(),
											   		"Enter the opponent's url (//198.168.1.10/RaulServer)", 
											   		"Settings opponent's url",
											   		JOptionPane.QUESTION_MESSAGE);
			
			if(urlServer != null && !urlServer.equals("")){
				confEnableButtonsMenu(false);
				actionsCallback.onActionSelected(Action.START_CLIENT);
			}else if(urlServer.equals("")){
				displayAreaMsg.setText("No value entered for the host");
			}
		
		}else if(event.getSource() == menuQuitGame){
			quitGame();
			actionsCallback.onActionSelected(Action.QUIT_GAME);
		}
	}
	
	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		
		public void mouseReleased(final MouseEvent e){
			final Square square = (Square)(e.getSource());
			actionsCallback.onSendCoordinateSquare(square.getRow(), square.getColumn());
		}
		
	};
	
	public void quitGame(){
		confEnableButtonsMenu(true);
		displayAreaMsg.setText("");
		
		reseatBoard();
		board.repaintBoard();
	}
	
	public void printMsgDisplay(final String message){
		displayAreaMsg.append(message +"\n");
		displayAreaMsg.setCaretPosition(displayAreaMsg.getDocument().getLength());
	}
	
	public void confEnableButtonsMenu(final boolean value){
		menuStartGame.setEnabled(value);
		menuRepaint.setEnabled(value);
		menuQuitGame.setEnabled(!value);
	}
	
	public void setColorSquare(final int x, final int y, final Color color){
		squareSecondary[x][y].setFill(true);
		squareSecondary[x][y].setSquareColor(color);
		squareSecondary[x][y].repaint();
	}
	
	public void createCleanBoard(){
		for (int row = 0; row < squareSecondary.length; row++) {
			for (int column = 0; column < squareSecondary[row].length; column++) {
				squareSecondary[row][column] = new Square(row, column, Color.BLACK, false, true);
				squareSecondary[row][column].addMouseListener(mouseAdapter);
				boardSecondary.add(squareSecondary[row][column]);
			}
		}
	}
	
	public void reseatBoard(){
		for (int row = 0; row < squareSecondary.length; row++) {
			for (int column = 0; column < squareSecondary[row].length; column++) {
				squareSecondary[row][column].setSquareColor(Color.BLACK);
				squareSecondary[row][column].setFill(false);
				squareSecondary[row][column].repaint();
			}
		}
	}
	
	public Board getBoard(){
		return board;
	}
	
	public Square[][] getBoardSercondary(){
		return squareSecondary;
	}
	
}