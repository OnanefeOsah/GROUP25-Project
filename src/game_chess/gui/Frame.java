package game_chess.gui;

import game_chess.engine.board.*;
import game_chess.engine.player.MoveTransition;
import game_chess.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.File;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Author: Ade Oyefeso
 * Author: Robert Odoh
 * Date Created:
 *
 * GUI class for the Board
 * This class contains subclasses for a board panel and a tile panel
 * The main GUI panel also contains a Game history panel and a taken pieces panel
 * **/

public class Frame {

	private JFrame gameFrame;
	private GameHistoryPanel gameHistoryPanel;
	private TakenPiecesPanel takenPiecesPanel;
	private BoardPanel boardPanel;
	private MoveLog moveLog;
	private Board chessBoard;
	private Tile sourceTile;
	private Tile destinationTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;

	//TODO - check the alliance of the turns
	private Move moveAlliance;

	private boolean highlightLegalMoves;

	private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
	private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
	private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

	//path to the folder containing the piece pictures
	private static String defaultPieceImagesPath = "chess graphics/GIF-github/05/";

	private final Color lightTileColor = Color.decode("#FFFACD");
	private final Color darkTileColor = Color.decode("#593E1A");

	public Frame(){
		this.gameFrame = new JFrame("CHESS 2D - Group 25");
		this.gameFrame.setLayout(new BorderLayout());
		final JMenuBar tableMenuBar = createTableMenuBar();
		this.gameFrame.setJMenuBar(tableMenuBar);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

		this.chessBoard = Board.createStandardBoard();
		this.gameHistoryPanel = new GameHistoryPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.boardPanel = new BoardPanel();
		this.moveLog = new MoveLog();
		this.boardDirection = BoardDirection.NORMAL;
		this.highlightLegalMoves = false;


		this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
		this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
		this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);

		this.gameFrame.setResizable(true);
		this.gameFrame.setVisible(true);
	}

	private JMenuBar createTableMenuBar(){
		final JMenuBar tableMenuBar = new JMenuBar();
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createPreferencesMenu());
		return tableMenuBar;
	}

	//"File" Menu for game
	private JMenu createFileMenu(){
		final JMenu fileMenu = new JMenu("File");

		//a PGN file is a "Portable Game Notation File" to store and load games
		final JMenuItem openPGN = new JMenuItem("Load PGN file: ");
		openPGN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				System.out.println("open that pgn file: ");
			}
		});
		fileMenu.add(openPGN);

		//Menu item to exit game
		final JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);

		return fileMenu;
	}

	private JMenu createPreferencesMenu(){
		final JMenu preferencesMenu = new JMenu("Preferences");

		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(new ActionListener(){
			@Override 
			public void actionPerformed(final ActionEvent e){
				boardDirection = boardDirection.opposite();
				boardPanel.drawBoard(chessBoard);
			}
		});
		//preferencesMenu.add(flipBoardMenuItem);

		preferencesMenu.addSeparator();
		final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight legal moves", false);
		legalMoveHighlighterCheckbox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
			}
		});
		preferencesMenu.add(legalMoveHighlighterCheckbox);
		return preferencesMenu;
	}

	public enum BoardDirection{

		NORMAL{
			@Override
			List<TilePanel> traverse(final List<TilePanel> boardTiles){
				return boardTiles;
			}
			@Override
			BoardDirection opposite(){
				return FLIPPED;
			}
		},
		FLIPPED{
			@Override
			List<TilePanel> traverse(final List<TilePanel> boardTiles){
				Collections.reverse(boardTiles);
				return boardTiles;
			}
			@Override
			BoardDirection opposite(){
				return NORMAL;
			}
		};


		abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

		abstract BoardDirection opposite();

	}

	private class BoardPanel extends JPanel{
		final List<TilePanel> boardTiles;

		BoardPanel(){
			super(new GridLayout(8,8)); //Chess boards are 8x8
			this.boardTiles = new ArrayList<>();

			//add each chess tile to board panel
			for(int i = 0; i < BoardUtils.NUM_TILES; i++){
				final TilePanel tilePanel = new TilePanel(this, i);
				this.boardTiles.add(tilePanel);
				add(tilePanel);
			}

			setPreferredSize(BOARD_PANEL_DIMENSION);
			validate();		
		}

		public void drawBoard(final Board board){
			removeAll();
			for(final TilePanel tilePanel : boardTiles){ //boardDirection.traverse(boardTiles)) {
				tilePanel.drawTile(board);
				add(tilePanel);
			}
			validate();
			repaint();
		}
	}

	//Stores game moves
	public static class MoveLog{
		private final List<Move> moves;

		MoveLog(){
			this.moves = new ArrayList<>();
		}

		public List<Move> getMoves(){
			return this.moves;
		}

		public void addMove(final Move move){
			this.moves.add(move);
		}

		public int size(){
			return this.moves.size();
		}

		public void clear(){
			this.moves.clear();
		}

		public Move removeMove(int index){
			return this.moves.remove(index);
		}

		public boolean removeMove(final Move move){
			return this.moves.remove(move);
		}

	}

	private class TilePanel extends JPanel{

		final private int tileId;

		TilePanel(final BoardPanel boardPanel, final int tileId){
			super(new GridBagLayout());
			this.tileId = tileId;
			setPreferredSize(TILE_PANEL_DIMENSION);
			assignTileColor();
			assignTilePieceIcon(chessBoard);

			addMouseListener(new MouseListener() {
				//listen for clicks on board
				@Override
				public void mouseClicked(final MouseEvent e){
					//invalid/reset move if user right clicks
					if(isRightMouseButton(e)){
						sourceTile = null;
						destinationTile = null;
						humanMovedPiece = null;

					//valid move for left click
					}else if (isLeftMouseButton(e)){
						if (sourceTile == null) {
							sourceTile = chessBoard.getTile(tileId);
							humanMovedPiece = sourceTile.getPiece();
							if (humanMovedPiece == null) {
								sourceTile = null;
							}
						}else{
							destinationTile = chessBoard.getTile(tileId);
							final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
							final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
							if (transition.getMoveStatus().isDone()) {
								chessBoard = transition.getTransitionBoard();
								moveLog.addMove(move);
							}
							sourceTile = null;
							destinationTile = null;
							humanMovedPiece = null;
						}
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run(){
								gameHistoryPanel.redo(chessBoard, moveLog);
								takenPiecesPanel.redo(moveLog);
								boardPanel.drawBoard(chessBoard);
							}
						});
					}
				}

				@Override
				public void mousePressed(final MouseEvent e){

				}
				@Override
				public void mouseReleased(final MouseEvent e){

				}
				@Override
				public void mouseEntered(final MouseEvent e){

				}
				@Override
				public void mouseExited(final MouseEvent e){

				}
			});
			validate();
		}

		public void drawTile(final Board board){
			assignTileColor();
			assignTilePieceIcon(board);
			highlightLegals(board);
			validate();
			repaint();
		}

		private void assignTilePieceIcon(final Board board) {
			this.removeAll();
			if (board.getTile(this.tileId).isTileOccupied()) {
				try {
					final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1) +
							board.getTile(this.tileId).getPiece().toString() + ".gif"));
					add(new JLabel(new ImageIcon(image)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		//highlight the moves the player can make
		private void highlightLegals(final Board board){
			String filePath = "chess graphics/GIF-github/misc/green_dot.png";
			if (highlightLegalMoves) {
				for (final Move move : pieceLegalMoves(board) ) {
					if (move.getDestinationCoordinate() == this.tileId) {
						try{
							add(new JLabel(new ImageIcon(ImageIO.read(new File(filePath)))));
						} catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		}

		private Collection<Move> pieceLegalMoves(final Board board){
			if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
				return humanMovedPiece.calculateLegalMoves(board);
			}
			return Collections.emptyList();
		}

		private void assignTileColor(){
			if(BoardUtils.EIGHTH_RANK[this.tileId] ||
				BoardUtils.SIXTH_RANK[this.tileId] ||
				BoardUtils.FOURTH_RANK[this.tileId] ||
				BoardUtils.SECOND_RANK[this.tileId]){
				setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
			} else if(BoardUtils.SEVENTH_RANK[this.tileId] ||
				BoardUtils.FIFTH_RANK[this.tileId] ||
				BoardUtils.THIRD_RANK[this.tileId] ||
				BoardUtils.FIRST_RANK[this.tileId]){
				setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
				}
			}
	}

}
