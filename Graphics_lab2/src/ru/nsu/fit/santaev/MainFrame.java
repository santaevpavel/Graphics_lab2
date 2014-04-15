package ru.nsu.fit.santaev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

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
	
	private static String aboutString = "Лемниска́та Берну́лли — плоская алгебраическая кривая. \nОпределяется как геометрическое место точек,\n произведение расстояний от которых до двух заданных точек (фокусов)\n постоянно и равно квадрату половины \nрасстояния между фокусами.";
	
	private JToolBar toolBar = null;
	private JPanel drawPanel = null;

	private ArrayList<MyPoint> figure = new ArrayList<MyPoint>();

	private int width = 0;
	private int height = 0;

	public double DEFAULT_MOVE_STEP = 0.5f;
	public double DEFAULT_ZOOM_STEP = 0.05f;

	public double MOVE_STEP = DEFAULT_MOVE_STEP;
	public double ZOOM_STEP = DEFAULT_ZOOM_STEP;

	private double leftBottomCoorX = 0;
	private double leftBottomCoorY = 0;
	private double rightTopCoorX = 0;
	private double rightTopCoorY = 0;

	private Point clickedPoint = new Point();
	private Point clickedPointPref = new Point();

	private double paramC = 1f;

	private BufferedImage img = null;

	private boolean isDrawColor = true;

	private Color graphColor = new Color(1, 0.3f, 0.3f);

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
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		setLeftBottomCoor(-2, -2);
		setRightTopCoor(2, 2);
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
				int x = MouseInfo.getPointerInfo().getLocation().x;
				int y = MouseInfo.getPointerInfo().getLocation().y;
				int panelX = drawPanel.getLocationOnScreen().x;
				int panelY = drawPanel.getLocationOnScreen().y;
				log("x y = " + (y - panelY));
				if (panelX < x && panelX + drawPanel.getWidth() > x
						&& panelY < y && panelY + drawPanel.getHeight() > y) {
					if (r > 0) {
						zoom(1 - ZOOM_STEP,
								new MyPoint(x - panelX, drawPanel.getHeight()
										- (y - panelY), Color.BLACK));
					} else {
						zoom(1 + ZOOM_STEP,
								new MyPoint(x - panelX, drawPanel.getHeight()
										- (y - panelY), Color.BLACK));
					}
				} else {
					if (r > 0) {
						zoom(1 - ZOOM_STEP);
					} else {
						zoom(1 + ZOOM_STEP);
					}
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

				JOptionPane
						.showMessageDialog(
								MainFrame.this,
								aboutString);
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
				JOptionPane.showMessageDialog(MainFrame.this, aboutString);
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
			// addMouseListener(act);
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

		if (rightTopCoorX - leftBottomCoorX > 100 && zoomDelta < 1) {
			return;
		}
		if (rightTopCoorX - leftBottomCoorX < 0.001 && zoomDelta > 1) {
			return;
		}
		leftBottomCoorX -= dw / 2;
		leftBottomCoorY -= dh / 2;

		rightTopCoorX += dw / 2;
		rightTopCoorY += dh / 2;
	}

	public void zoom(double zoomDelta, MyPoint xy) {
		double w = rightTopCoorX - leftBottomCoorX;
		double h = rightTopCoorY - leftBottomCoorY;
		double dx = (double) xy.x / drawPanel.getWidth();
		double dy = (double) xy.y / drawPanel.getHeight();
		double dw = w - w * zoomDelta;
		double dh = h - h * zoomDelta;
		log("------------ " + xy.x + " " + xy.y + " " + dx + " " + dy);
		if (rightTopCoorX - leftBottomCoorX > 100 && zoomDelta < 1) {
			return;
		}
		if (rightTopCoorX - leftBottomCoorX < 0.001 && zoomDelta > 1) {
			return;
		}
		leftBottomCoorX -= dw * dx;
		leftBottomCoorY -= dh * dy;

		rightTopCoorX += dw * (1 - dx);
		rightTopCoorY += dh * (1 - dy);
	}

	public void setDefaultView() {
		setLeftBottomCoor(-2, -2);
		setRightTopCoor(2, 2);
		setDisplayRatio(true);
	}

	class DrawJPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public DrawJPanel() {
			setBackground(Color.WHITE);
			System.out.println("OnCreate");
		}

		protected void updateGraphics(Graphics graph) {
			if (!(height == getHeight() && width == getWidth())) {
				height = getHeight();
				width = getWidth();
				img = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
			}
			height = getHeight();
			width = getWidth();

			setDisplayRatio(true);

			img.getGraphics().setColor(Color.white);
			img.getGraphics().fillRect(0, 0, img.getWidth(), img.getHeight());// drawRect(0,
																				// 0,
																				// 100,
																				// 100);
			graph.setColor(Color.RED);

			drawFirstPart(graph);
			drawSecondPart(graph);
			drawThirdPart(graph);
			drawFourthPart(graph);

			graph.drawImage(img, 0, 0, null);
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
					if (0 <= (int) xx && img.getWidth() > (int) xx) {
						if (0 <= height - (int) yy
								&& img.getHeight() > height - (int) yy) {
							img.setRGB((int) Math.round(xx),
									(int) Math.round(height - yy),
									graphColor.getRGB());
						}
					}
				}
			}

		}

		public void drawDot(int x, int y, Graphics graph) {
			int xx = 0;
			int yy = 0;
			xx = x;
			yy = y;
			if (0 <= (int) xx && img.getWidth() > (int) xx) {
				if (0 <= height - (int) yy
						&& img.getHeight() > height - (int) yy) {
					img.setRGB((int) xx, (int) height - yy, graphColor.getRGB());
				}
			}

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
			int x = 0;
			int y = 0;
			double xx = 0;
			double yy = 0;
			int startX = getXOnDislplay(0);
			int endX = getXOnDislplay(Math.sqrt(2));
			double k = Math.sin(Math.PI / 12);
			double endX2 = Math.sqrt(2 * (1 - k * k)
					/ ((k * k + 1) * (k * k + 1)));
			double endY2 = Math.sqrt(2 * (1 - k * k)
					/ ((k * k + 1) * (k * k + 1)))
					* k;
			int startY2 = getYOnDislplay(0);
			drawDot(100, 100, graph);

			if (leftBottomCoorX > Math.sqrt(2) || rightTopCoorX < 0) {
				return;
			}

			for (x = startX; x < getXOnDislplay(endX2) + 1
					&& x < getXOnDislplay(rightTopCoorX); x++) {
				xx = getXOnPlot(x);
				yy = Bernulli.getY(xx);
				y = getYOnDislplay(yy);
				drawDot(x, y, graph);
			}
			for (y = startY2; y <= getYOnDislplay(endY2) + 1
					&& y < getYOnDislplay(rightTopCoorY); y++) {
				yy = getYOnPlot(y);
				xx = Bernulli.getX(yy);
				x = getXOnDislplay(xx);
				drawDot(x, y, graph);

			}
		}

		public void drawSecondPart(Graphics graph) {
			int x = 0;
			int y = 0;
			double xx = 0;
			double yy = 0;
			int startX = getXOnDislplay(0);
			int endX = getXOnDislplay(Math.sqrt(2));
			double k = Math.sin(Math.PI / 12);
			double endX2 = Math.sqrt(2 * (1 - k * k)
					/ ((k * k + 1) * (k * k + 1)))
					* -1;
			double endY2 = Math.sqrt(2 * (1 - k * k)
					/ ((k * k + 1) * (k * k + 1)))
					* k;
			int startY2 = getYOnDislplay(0);
			drawDot(100, 100, graph);

			if (leftBottomCoorX > 0 || rightTopCoorX < -Math.sqrt(2)) {
				return;
			}

			for (x = startX; x > getXOnDislplay(endX2) - 1
					&& x > getXOnDislplay(leftBottomCoorX); x--) {
				xx = getXOnPlot(x);
				yy = Bernulli.getY(xx);
				y = getYOnDislplay(yy);
				drawDot(x, y, graph);
			}
			for (y = startY2; y <= getYOnDislplay(endY2)
					&& y < getYOnDislplay(rightTopCoorY); y++) {
				yy = getYOnPlot(y);
				xx = Bernulli.getX(yy);
				x = getXOnDislplay(-xx);
				drawDot(x, y, graph);
			}
		}

		public void drawThirdPart(Graphics graph) {
			int x = 0;
			int y = 0;
			double xx = 0;
			double yy = 0;
			int startX = getXOnDislplay(0);
			int endX = getXOnDislplay(Math.sqrt(2));
			double k = Math.sin(Math.PI / 12);
			double endX2 = Math.sqrt(2 * (1 - k * k)
					/ ((k * k + 1) * (k * k + 1)));
			double endY2 = -Math.sqrt(2 * (1 - k * k)
					/ ((k * k + 1) * (k * k + 1)))
					* k;
			int startY2 = getYOnDislplay(0);
			drawDot(100, 100, graph);

			if (leftBottomCoorX > Math.sqrt(2) || rightTopCoorX < 0) {
				// return;
			}

			for (x = startX; x < getXOnDislplay(endX2)
					&& x < getXOnDislplay(rightTopCoorX); x++) {
				xx = getXOnPlot(x);
				yy = Bernulli.getY(xx);
				y = getYOnDislplay(-yy);
				drawDot(x, y, graph);
			}
			for (y = startY2; y >= getYOnDislplay(endY2) - 1
					&& y > getYOnDislplay(leftBottomCoorY); y--) {
				yy = getYOnPlot(y);
				xx = Bernulli.getX(yy);
				x = getXOnDislplay(xx);
				drawDot(x, y, graph);

			}
		}

		public void drawFourthPart(Graphics graph) {
			int x = 0;
			int y = 0;
			double xx = 0;
			double yy = 0;
			int startX = getXOnDislplay(0);
			int endX = getXOnDislplay(Math.sqrt(2));
			double k = Math.sin(Math.PI / 12);
			double endX2 = Math.sqrt(2 * (1 - k * k)
					/ ((k * k + 1) * (k * k + 1)))
					* -1;
			double endY2 = -Math.sqrt(2 * (1 - k * k)
					/ ((k * k + 1) * (k * k + 1)))
					* k;
			int startY2 = getYOnDislplay(0);
			drawDot(100, 100, graph);

			if (leftBottomCoorX > Math.sqrt(2) || rightTopCoorX < 0) {
				// return;
			}

			for (x = startX; x > getXOnDislplay(endX2) - 1
					&& x > getXOnDislplay(leftBottomCoorX); x--) {
				xx = getXOnPlot(x);
				yy = -Bernulli.getY(xx);
				y = getYOnDislplay(yy);
				drawDot(x, y, graph);
			}
			for (y = startY2; y >= getYOnDislplay(endY2) - 1
					&& y > getYOnDislplay(leftBottomCoorY); y--) {
				yy = getYOnPlot(y);
				xx = -Bernulli.getX(yy);
				x = getXOnDislplay(xx);
				drawDot(x, y, graph);
			}
		}

		public double getXOnPlot(int x) {
			return ((leftBottomCoorX + (rightTopCoorX - leftBottomCoorX) * x
					/ width));
		}

		public double getYOnPlot(int y) {
			return ((leftBottomCoorY + (rightTopCoorY - leftBottomCoorY) * y
					/ height));
		}

		public int getXOnDislplay(double x, int ifNotOnDilspay) {
			if (leftBottomCoorX > x || rightTopCoorX < x) {
				return ifNotOnDilspay;
			}
			return (int) ((x - (double) leftBottomCoorX)
					/ ((double) rightTopCoorX - (double) leftBottomCoorX) * width);
		}

		public int getXOnDislplay(double x) {
			return (int) ((x - (double) leftBottomCoorX)
					/ ((double) rightTopCoorX - (double) leftBottomCoorX) * width);
		}

		public int getYOnDislplay(double y) {
			return (int) ((y - (double) leftBottomCoorY)
					/ ((double) rightTopCoorY - (double) leftBottomCoorY) * height);
		}

		public double getTFromX(double x) {
			double y = -Math.sqrt(Math.sqrt(Math.pow(paramC, 4) + 4 * x * x
					* paramC * paramC)
					- x * x - paramC * paramC);
			double t = Math.acos((x * x + y * y) / (2 * paramC * paramC)) / 2;
			return t;
		}
	}

	public static void log(String str) {
		System.out.println(str);
	}

	public boolean isDrawColor() {
		return isDrawColor;
	}

	public void setDrawColor(boolean isDrawColor) {
		this.isDrawColor = isDrawColor;
	}
}
