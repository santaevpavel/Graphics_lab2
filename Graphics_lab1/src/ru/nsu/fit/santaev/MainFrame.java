package ru.nsu.fit.santaev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.jws.Oneway;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final int MIN_WIDTH = 800;
	public static final int MIN_HEIGHT = 600;

	private static String menuFileTitle = "File";
	private static String subMenuFileTitle = "New file";
	private static String subMenuEXitTitle = "Exit";
	private static String menuHelpTitle = "Help";
	private static String subMenuAbouTitle = "About";

	private JToolBar toolBar = null;
	private JPanel drawPanel = null;

	private ArrayList<MyPoint> figure = new ArrayList<MyPoint>();

	private int width = 0;
	private int height = 0;

	public double DEFAULT_MOVE_STEP = 0.5f;
	public double DEFAULT_ZOOM_STEP = 0.1f;
	
	public double MOVE_STEP = DEFAULT_MOVE_STEP;
	public double ZOOM_STEP = DEFAULT_ZOOM_STEP;

	private double leftBottomCoorX = 0;
	private double leftBottomCoorY = 0;
	private double rightTopCoorX = 0;
	private double rightTopCoorY = 0;

	private Point clickedPoint = new Point();
	private Point clickedPointPref = new Point();
	
	private BufferedImage img = null;
	
	private boolean isDrawColor = true;
	
	public MainFrame(String title) {
		super(title);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createGUI();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) dim.getWidth() / 2
				- (int) this.getSize().getWidth() / 2, (int) dim.getHeight()
				/ 2 - (int) this.getSize().getHeight() / 2);
		this.setVisible(true);
		height = getHeight();
		width = getWidth();

		setLeftBottomCoor(-5, -5);
		setRightTopCoor(10, 10);
		setDisplayRatio(true);

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				clickedPointPref = e.getPoint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				double dx = ((double) e.getX() - (double) clickedPointPref
						.getX()) / width * (rightTopCoorX - leftBottomCoorX);
				double dy = ((double) e.getY() - (double) clickedPointPref
						.getY()) / height * (rightTopCoorY - leftBottomCoorY);
				moveDisplay(-dx, dy);
				repaint();
				clickedPointPref = e.getPoint();
			}
		});
		addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int r = e.getWheelRotation();
				// System.out.println("Wheel " + r);
				if (r > 0) {
					zoom(1 - ZOOM_STEP);
				} else {
					zoom(1 + ZOOM_STEP);
				}
				repaint();
			}
		});
	}

	public void setDisplayRatio(boolean b) {
		double dx = rightTopCoorX - leftBottomCoorX;
		double dy = rightTopCoorY - leftBottomCoorY;
		double r = (double) height / width;
		if (b) {
			double d = dx * r;
			rightTopCoorY += (d - dy) / 2;
			leftBottomCoorY -= (d - dy) / 2;
		} else {
			double d = dy / r;
			rightTopCoorX += (d - dx) / 2;
			leftBottomCoorX -= (d - dx) / 2;
		}
	}

	protected void createGUI() {
		createMenu();
		createToolBar();
		drawPanel = new DrawJPanel();
		getContentPane().add(drawPanel);
	}

	protected void createMenu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu(menuFileTitle);
		menuBar.add(fileMenu);

		JMenuItem fileMenuSubItem1 = new JMenuItem(subMenuFileTitle);
		fileMenu.add(fileMenuSubItem1);
		JMenuItem fileMenuSubItem3 = new JMenuItem(subMenuEXitTitle);
		fileMenu.add(fileMenuSubItem3);
		fileMenuSubItem3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(NORMAL);
			}
		});
		JMenu helpMenu = new JMenu(menuHelpTitle);
		JMenuItem fileMenuSubItem2 = new JMenuItem(subMenuAbouTitle);
		helpMenu.add(fileMenuSubItem2);
		fileMenuSubItem2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JOptionPane.showMessageDialog(MainFrame.this, "About");
			}
		});
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	protected void createToolBar() {
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		ImageIcon iconNew = new ImageIcon("res/images/new_file.png");
		ImageIcon iconSave = new ImageIcon("res/images/save_file.png");
		ImageIcon iconAbout = new ImageIcon("res/images/about.png");
		ImageIcon iconExit = new ImageIcon("res/images/exit.png");

		Action actionNew = new AbstractAction("New", iconNew) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setTitle("Clicked " + "new file");
			}
		};
		Action actionSave = new AbstractAction("Save", iconSave) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setTitle("Clicked " + "save file");
			}
		};
		Action actionAbout = new AbstractAction("About", iconAbout) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(MainFrame.this, "About");
			}
		};
		Action actionExit = new AbstractAction("Exit", iconExit) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(NORMAL);
			}
		};
		// addButtonToToolbar(actionNew);
		// addButtonToToolbar(actionSave);
		addButtonToToolbar(actionAbout);
		addButtonToToolbar(actionExit, 1);
		getContentPane().add(toolBar, BorderLayout.NORTH);
	}

	public void addButtonToToolbar(Action action) {
		JButton button = new SmallButton(action);
		toolBar.add(button, 0);
		toolBar.repaint();
		revalidate();
	}
	public void addSeparatorToToolbar() {
		JSeparator sep = new JToolBar.Separator();
		sep.setSize(10, 30);
		toolBar.add(sep, 0);
		toolBar.repaint();
		revalidate();
	}
	public void addButtonToToolbar(Action action, int index) {
		JButton button = new SmallButton(action);
		toolBar.add(button, index);
	}

	public void moveDisplay(double dx, double dy) {
		leftBottomCoorX += dx;
		rightTopCoorX += dx;

		leftBottomCoorY += dy;
		rightTopCoorY += dy;
	}

	class SmallButton extends JButton {

		private static final long serialVersionUID = 1L;

		public SmallButton(Action act) {
			super((Icon) act.getValue(Action.SMALL_ICON));

			setMargin(new Insets(1, 1, 1, 1));
			addActionListener(act);
			//addMouseListener(act);
		}

		public float getAlignmentY() {
			return 0.5f;
		}

	}

	public JPanel getDrawPanel() {
		return drawPanel;
	}

	public void setDrawPanel(JPanel drawPanel) {
		this.drawPanel = drawPanel;
	}

	public ArrayList<MyPoint> getFigure() {
		return figure;
	}

	public void setFigure(ArrayList<MyPoint> figure) {
		this.figure = figure;
	}

	public void setLeftBottomCoor(double x, double y) {
		leftBottomCoorX = x;
		leftBottomCoorY = y;
	}

	public void setRightTopCoor(double x, double y) {
		rightTopCoorX = x;
		rightTopCoorY = y;
	}

	public void zoom(double zoomDelta) {
		double w = rightTopCoorX - leftBottomCoorX;
		double h = rightTopCoorY - leftBottomCoorY;

		double dw = w - w * zoomDelta;
		double dh = h - h * zoomDelta;

		if (rightTopCoorX - leftBottomCoorX > 4000 && zoomDelta < 1){
			return;
		}
		leftBottomCoorX -= dw / 2;
		leftBottomCoorY -= dh / 2;

		rightTopCoorX += dw / 2;
		rightTopCoorY += dh / 2;
	}
	public void setDefaultView(){
		setLeftBottomCoor(-5, -5);
		setRightTopCoor(10, 10);
		setDisplayRatio(true);
	}
	class DrawJPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public DrawJPanel() {
			setBackground(Color.WHITE);
			System.out.println("OnCreate");
		}

		protected void updateGraphics(Graphics graph) {
			// Graphics2D gr2D = (Graphics2D) graph;
			height = getHeight();
			width = getWidth();
			
			setDisplayRatio(true);
			
			//img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			graph.setColor(Color.RED);
			drawFirstPart(graph);
			graph.setColor(Color.BLUE);
			drawSecondPart(graph);
			graph.setColor(Color.CYAN);
			drawThirdPart(graph);
			graph.setColor(Color.magenta);
			drawFourthPart(graph);
			graph.setColor(Color.BLACK);
			drawFivethPart(graph);
			graph.setColor(Color.green);
			drawSixthPart(graph);
		
			//graph.drawImage(img, 100, 100, null);
			drawLines(graph);
		}

		public void drawDot(double x, double y, Graphics graph) {
			double xx = 0;
			double yy = 0;

			if (x > leftBottomCoorX && x <= rightTopCoorX) {
				xx = (int) ((x - (double) leftBottomCoorX)
						/ ((double) rightTopCoorX - (double) leftBottomCoorX) * width);
				if (y > leftBottomCoorY && y <= rightTopCoorY) {
					yy = (int) ((y - (double) leftBottomCoorY)
							/ ((double) rightTopCoorY - (double) leftBottomCoorY) * height);
					graph.drawLine((int) xx, height - (int) yy, (int) xx,
							height - (int) yy);
				}
			}
			/*if (x > leftBottomCoorX && x <= rightTopCoorX) {
				xx = (int) ((x - (double) leftBottomCoorX)
						/ ((double) rightTopCoorX - (double) leftBottomCoorX) * width);
				if (y > leftBottomCoorY && y <= rightTopCoorY) {
					yy = (int) ((y - (double) leftBottomCoorY)
							/ ((double) rightTopCoorY - (double) leftBottomCoorY) * height);
					img.getGraphics().drawLine((int) xx, height - (int) yy, (int) xx,
							height - (int) yy);
					
				}
			}*/
		}

		public void drawLines(Graphics graph) {
			graph.setColor(Color.GRAY);
			if (0 > leftBottomCoorX && 0 <= rightTopCoorX) {
				int x = (int) ((-1 * (double) leftBottomCoorX)
						/ ((double) rightTopCoorX - (double) leftBottomCoorX) * width);
				graph.drawLine(x, 0, x, height);
			}
			if (0 > leftBottomCoorY && 0 <= rightTopCoorY) {
				int y = (int) ((-1 * (double) leftBottomCoorY)
						/ ((double) rightTopCoorY - (double) leftBottomCoorY) * height);
				graph.drawLine(0, height - y, width, height - y);
			}
			if (1 >= leftBottomCoorX && 1 <= rightTopCoorX) {
				int x = (int) ((1 - (double) leftBottomCoorX)
						/ ((double) rightTopCoorX - (double) leftBottomCoorX) * width);
				int y = (int) ((-1 * (double) leftBottomCoorY)
						/ ((double) rightTopCoorY - (double) leftBottomCoorY) * height);
				graph.drawLine(x, height - y - 4, x, height - y + 4);
			}
			if (1 > leftBottomCoorY && 1 <= rightTopCoorY) {
				int x = (int) ((-1 * (double) leftBottomCoorX)
						/ ((double) rightTopCoorX - (double) leftBottomCoorX) * width);
				int y = (int) ((1 - (double) leftBottomCoorY)
						/ ((double) rightTopCoorY - (double) leftBottomCoorY) * height);
				graph.drawLine(x - 4, height - y, x + 4, height - y);
			}
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			updateGraphics(g);
		}
		public void drawFirstPart(Graphics graph) {
			double tEnd = 0;
			double t = 0;
			double Mx = 0;
			double My = 0;
			double dt = 0;
			double x = 0;
			double y = 0;
			if (isDrawColor){
				graph.setColor(Color.BLACK);
			}
			t = Math.sqrt(leftBottomCoorX / (leftBottomCoorX - 1));
			if (leftBottomCoorX > 0){
				return;
			}
			if (rightTopCoorX >= 0){
				tEnd = 0;
			}else{
				tEnd = Math.sqrt(rightTopCoorX / (rightTopCoorX - 1));
			}
			do {
				x = (t * t) / (t * t - 1);
				y = (t * t + 1) / (t + 2);
				drawDot(x, y, graph);
				t -= dt;
				My = Math.abs((t * t + 4 * t - 1)/((t + 2) * (t + 2)));
				Mx = Math.abs(-2 * t / ((t * t - 1) * (t * t - 1)));
				dt = Math.min((double) (rightTopCoorX - leftBottomCoorX)
						/ (Mx * width),
						(double) (rightTopCoorY - leftBottomCoorY)
								/ (My * height));
			} while (t > tEnd);
		}
		public void drawSecondPart(Graphics graph) {
			double tEnd = 0;
			double t = 0;
			double Mx = 0;
			double My = 0;
			double dt = 0;
			double x = 0;
			double y = 0;
			if (isDrawColor){
				graph.setColor(Color.BLACK);
			}
			t = -Math.sqrt(leftBottomCoorX / (leftBottomCoorX - 1));
			if (leftBottomCoorX > 0){
				return;
			}
			if (rightTopCoorX >= 0){
				tEnd = 0;
			}else{
				tEnd = -Math.sqrt(rightTopCoorX / (rightTopCoorX - 1));
			}
			do {
				x = (t * t) / (t * t - 1);
				y = (t * t + 1) / (t + 2);
				drawDot(x, y, graph);
				t += dt;
				My = Math.abs((t * t + 4 * t - 1)/((t + 2) * (t + 2)));
				Mx = Math.abs(-2 * t / ((t * t - 1) * (t * t - 1)));
				dt = Math.min((double) (rightTopCoorX - leftBottomCoorX)
						/ (Mx * width),
						(double) (rightTopCoorY - leftBottomCoorY)
								/ ( My * height));
			} while (t < tEnd);
		}
		public void drawThirdPart(Graphics graph) {
			double tEnd = 0;
			double t = 0;
			double Mx = 0;
			double My = 0;
			double dt = 0;
			double x = 0;
			double y = 0;
			if (isDrawColor){
				graph.setColor(Color.BLACK);
			}
			t = Math.sqrt(rightTopCoorX / (rightTopCoorX - 1));
			if (rightTopCoorX < 0){
				return;
			}
			if (rightTopCoorY < (double) 2 / 3){
				tEnd = 0;
				return;
			}else{
				tEnd = (rightTopCoorY + Math.sqrt(rightTopCoorY * rightTopCoorY - 4 * (1 - 2 * rightTopCoorY))) / 2;
			}
			if (leftBottomCoorY > 1){
				t = Math.max(t, (leftBottomCoorY + Math.sqrt(leftBottomCoorY * leftBottomCoorY - 4 * (1 - 2 * leftBottomCoorY))) / 2);
			}
			if (leftBottomCoorX > 1){
				tEnd = Math.min(tEnd, Math.sqrt(leftBottomCoorX / (leftBottomCoorX - 1)));
			}
			int i = 0;
			do {
				i++;
				x = (t * t) / (t * t - 1);
				y = (t * t + 1) / (t + 2);
				drawDot(x, y, graph);
				t += dt;
				My = Math.abs((t * t + 4 * t - 1)/((t + 2) * (t + 2)));
				Mx = Math.abs(-2 * t / ((t * t - 1) * (t * t - 1)));
				dt = Math.min((double) (rightTopCoorX - leftBottomCoorX)
						/ (Mx * width),
						(double) (rightTopCoorY - leftBottomCoorY)
								/ (My * height));
			} while (t < tEnd);
			//log("i = " + i);
		}
		public void drawFourthPart(Graphics graph) {
			double tEnd = 0;
			double t = 0;
			double Mx = 0;
			double My = 0;
			double dt = 0;
			double x = 0;
			double y = 0;
			if (isDrawColor){
				graph.setColor(Color.BLACK);
			}
			t = -Math.sqrt(rightTopCoorX / (rightTopCoorX - 1));
			if (rightTopCoorX < 0){
				return;
			}
			if (rightTopCoorY < 2){
				tEnd = 0;
			}else{
				tEnd = (rightTopCoorY - Math.sqrt(rightTopCoorY * rightTopCoorY - 4 * (1 - 2 * rightTopCoorY))) / 2;
			}
			if (leftBottomCoorY > 3){
				//log("t = " + t + " " + (leftBottomCoorY - Math.sqrt(leftBottomCoorY * leftBottomCoorY - 4 * (1 - 2 * leftBottomCoorY))) / 2);
				t = Math.min(t, (leftBottomCoorY - Math.sqrt(leftBottomCoorY * leftBottomCoorY - 4 * (1 - 2 * leftBottomCoorY))) / 2);
			}
			if (leftBottomCoorX > 2){
				tEnd = Math.max(tEnd, -Math.sqrt(leftBottomCoorX / (leftBottomCoorX - 1)));
			}
			int i = 0;
			do {
				i++;
				x = (t * t) / (t * t - 1);
				y = (t * t + 1) / (t + 2);
				drawDot(x, y, graph);
				t -= dt;
				My = Math.abs((t * t + 4 * t - 1)/((t + 2) * (t + 2)));
				Mx = Math.abs(-2 * t / ((t * t - 1) * (t * t - 1)));
				dt = Math.min((double) (rightTopCoorX - leftBottomCoorX)
						/ (Mx * width),
						(double) (rightTopCoorY - leftBottomCoorY)
								/ (My * height));
			} while (t > tEnd);
			log("i = " + i);
		}
		public void drawFivethPart(Graphics graph) {
			double tEnd = 0;
			double t = 0;
			double Mx = 0;
			double My = 0;
			double dt = 0;
			double x = 0;
			double y = 0;
			if (isDrawColor){
				graph.setColor(Color.BLACK);
			}
			t = (leftBottomCoorY + Math.sqrt(leftBottomCoorY * leftBottomCoorY - 4 * (1 - 2 * leftBottomCoorY))) / 2;
			if (leftBottomCoorY >= (-4 - Math.sqrt(20)) / 2){
				return;
			}
			if (rightTopCoorY >= (  ((-4 - Math.sqrt(20)) / 2) * (-4 - Math.sqrt(20)) / 2 + 1)/((-4 - Math.sqrt(20)) / 2 + 2) ){
				tEnd = (-4 - Math.sqrt(20)) / 2;
			}else{
				tEnd = (rightTopCoorY + Math.sqrt(rightTopCoorY * rightTopCoorY - 4 * (1 - 2 * rightTopCoorY))) / 2;
			}
			do {
				x = (t * t) / (t * t - 1);
				y = (t * t + 1) / (t + 2);
				drawDot(x, y, graph);
				t -= dt;
				My = Math.abs((t * t + 4 * t - 1)/((t + 2) * (t + 2)));
				Mx = Math.abs(-2 * t / ((t * t - 1) * (t * t - 1)));
				dt = Math.min((double) (rightTopCoorX - leftBottomCoorX)
						/ (2 * Mx * width),
						(double) (rightTopCoorY - leftBottomCoorY)
								/ ( 2 * My * height));
			} while (t > tEnd);
		}
		public void drawSixthPart(Graphics graph) {
			double tEnd = 0;
			double t = 0;
			double Mx = 0;
			double My = 0;
			double dt = 0;
			double x = 0;
			double y = 0;
			if (isDrawColor){
				graph.setColor(Color.BLACK);
			}
			t = (leftBottomCoorY - Math.sqrt(leftBottomCoorY * leftBottomCoorY - 4 * (1 - 2 * leftBottomCoorY))) / 2;
			if (leftBottomCoorY >= (-4 - Math.sqrt(20)) / 2){
				return;
			}
			if (rightTopCoorY >= (  ((-4 - Math.sqrt(20)) / 2) * (-4 - Math.sqrt(20)) / 2 + 1)/((-4 - Math.sqrt(20)) / 2 + 2) ){
				tEnd = (-4 - Math.sqrt(20)) / 2;
			}else{
				tEnd = (rightTopCoorY - Math.sqrt(rightTopCoorY * rightTopCoorY - 4 * (1 - 2 * rightTopCoorY))) / 2;
			}
			int i = 0;
			do {
				i++;
				x = (t * t) / (t * t - 1);
				y = (t * t + 1) / (t + 2);
				drawDot(x, y, graph);
				t += dt;
				My = Math.abs((t * t + 4 * t - 1)/((t + 2) * (t + 2)));
				Mx = Math.abs(-2 * t / ((t * t - 1) * (t * t - 1)));
				dt = Math.min((double) (rightTopCoorX - leftBottomCoorX)
						/ (2 * Mx * width),
						(double) (rightTopCoorY - leftBottomCoorY)
								/ (2 * My * height));
			} while (t < tEnd);
			//log("i = " + i);
		}
	}
	
	public static void log(String str){
		System.out.println(str);
	}

	public boolean isDrawColor() {
		return isDrawColor;
	}

	public void setDrawColor(boolean isDrawColor) {
		this.isDrawColor = isDrawColor;
	}
}
