package ru.nsu.fit.santaev;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main {

	public static class MySettings extends JFrame {

		private static final long serialVersionUID = 1L;

		public static final int MIN_WIDTH = 400;
		public static final int MIN_HEIGHT = 300;

		private JSlider sliderZoom = null;
		private JSlider sliderMove = null;
		
		private JButton defaultButton  = null; 
		
		private JPanel panel = new JPanel(); 
		
		public MySettings(String title) {
			super(title);
			setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			/*this.setLocation((int) dim.getWidth() / 2
					- (int) this.getSize().getWidth() / 2,
					(int) dim.getHeight() / 2
							- (int) this.getSize().getHeight() / 2);
			*/
			this.setLocation(mainFrame.getLocation().x + mainFrame.getWidth(), mainFrame.getLocation().y);
			JTextField text1 = new JTextField("Zoom step");
			JTextField text2 = new JTextField("Move step");
			
			defaultButton = new JButton();
			defaultButton.setText("Set default params");
			
			text1.setEditable(false);
			text1.setPreferredSize(new Dimension(100, 30));
			text2.setEditable(false);
			text2.setPreferredSize(new Dimension(100, 30));
			sliderMove = new JSlider(10, 100, 50);
			sliderZoom = new JSlider(1, 50, 10);
			
			sliderMove.setToolTipText("sdf");
			sliderMove.setPaintLabels(true);
			sliderMove.setLabelTable(sliderMove.createStandardLabels(10, 10));
			sliderZoom.setPaintLabels(true);
			sliderZoom.setLabelTable(sliderZoom.createStandardLabels(10, 10));
			
			add(panel);
			
			panel.setLayout(new GridLayout(5, 1));
			panel.add(text2);
			panel.add(sliderMove);
			panel.add(text1);
			panel.add(sliderZoom);
			panel.add(defaultButton);
			
			defaultButton.setAction(new AbstractAction("Set default values") {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					mainFrame.MOVE_STEP = mainFrame.DEFAULT_MOVE_STEP;
					mainFrame.ZOOM_STEP = mainFrame.DEFAULT_ZOOM_STEP;
					sliderMove.setValue((int) (mainFrame.MOVE_STEP * 100));
					sliderZoom.setValue((int) (mainFrame.ZOOM_STEP * 100));
				}
			});
			
			sliderMove.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent arg0) {
					mainFrame.MOVE_STEP = ((double) sliderMove.getValue()) / 100;
				}
			});
			sliderZoom.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent arg0) {
					mainFrame.ZOOM_STEP = ((double) sliderZoom.getValue()) / 100;
				}
			});
		}

		@Override
		public void setVisible(boolean arg0) {
			sliderMove.setValue((int) (mainFrame.MOVE_STEP * 100));
			sliderZoom.setValue((int) (mainFrame.ZOOM_STEP * 100));
			this.setLocation(mainFrame.getLocation().x + mainFrame.getWidth(), mainFrame.getLocation().y);
			super.setVisible(arg0);
		}
	}

	public static final String MAIN_FRAME_TITLE = "Lab #1";
	private static final MainFrame mainFrame = new MainFrame(MAIN_FRAME_TITLE);
	private static Action settings;

	public static void main(String[] args) {
		final ArrayList<MyPoint> figure = new ArrayList<MyPoint>();

		final MySettings settingsFrame = new MySettings("Settings");

		ImageIcon iconNew = new ImageIcon("res/images/draw_square.png");

		Action actionDrawSquare = new AbstractAction("New", iconNew) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				figure.clear();
				Color color = Color.RED;
				double t = 0;
				double x = 0;
				double y = 0;
				for (t = 0; t < 100; t = t + 0.01f) {
					x = t * t / (t * t - 1);
					y = (t * t + 1) / (t + 2);
					x = x * 10;
					y = y * 10;
					figure.add(new MyPoint(200 + (int) x, 200 - (int) y, color));
				}

				mainFrame.setFigure(figure);
				mainFrame.repaint();
			}
		};
		ImageIcon iconUp = new ImageIcon("res/images/str_up.png");

		Action graphUp = new AbstractAction("New", iconUp) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				figure.clear();
				mainFrame.moveDisplay(0, mainFrame.MOVE_STEP);
				mainFrame.repaint();
			}
		};
		ImageIcon iconDown = new ImageIcon("res/images/str_down.png");

		Action graphDown = new AbstractAction("New", iconDown) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				figure.clear();
				mainFrame.moveDisplay(0, -mainFrame.MOVE_STEP);
				mainFrame.repaint();
			}
		};
		ImageIcon iconRight = new ImageIcon("res/images/str_right.png");

		Action graphRight = new AbstractAction("New", iconRight) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				figure.clear();
				mainFrame.moveDisplay(mainFrame.MOVE_STEP, 0);
				mainFrame.repaint();
			}
		};
		ImageIcon iconLeft = new ImageIcon("res/images/str_left.png");

		Action graphLeft = new AbstractAction("New", iconLeft) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				figure.clear();
				mainFrame.moveDisplay(-mainFrame.MOVE_STEP, 0);
				mainFrame.repaint();
			}
		};
		ImageIcon iconZoomIn = new ImageIcon("res/images/zoom_in.png");

		Action zoomIn = new AbstractAction("New", iconZoomIn) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				figure.clear();
				mainFrame.zoom(1 + mainFrame.ZOOM_STEP);
				mainFrame.repaint();
			}
		};
		ImageIcon iconZoom_out = new ImageIcon("res/images/zoom_out.png");

		Action zoomOut = new AbstractAction("New", iconZoom_out) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				figure.clear();
				mainFrame.zoom(1 - mainFrame.ZOOM_STEP);
				mainFrame.repaint();
			}
		};
		ImageIcon iconFindCenter = new ImageIcon("res/images/icon_find.png");

		Action findCenter = new AbstractAction("New", iconFindCenter) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainFrame.setDefaultView();
				mainFrame.repaint();
				mainFrame.MOVE_STEP = mainFrame.DEFAULT_MOVE_STEP;
				mainFrame.ZOOM_STEP = mainFrame.DEFAULT_ZOOM_STEP;
				settingsFrame.sliderMove.setValue((int) (mainFrame.MOVE_STEP * 100));
				settingsFrame.sliderZoom.setValue((int) (mainFrame.ZOOM_STEP * 100));
			}
		};
		ImageIcon iconSettings = new ImageIcon("res/images/settings.png");
		settings = new AbstractAction("New", iconSettings) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				settingsFrame.setVisible(true);
			}
		};
		ImageIcon iconDrawColorful = new ImageIcon("res/images/draw_color.png");
		Action drawColorful = new AbstractAction("New", iconDrawColorful) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainFrame.setDrawColor(!mainFrame.isDrawColor());
				mainFrame.repaint();
			}
		};
		mainFrame.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				settingsFrame.setLocation(mainFrame.getLocation().x + mainFrame.getWidth(), mainFrame.getLocation().y);
				settingsFrame.invalidate();
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		mainFrame.addButtonToToolbar(findCenter);
		mainFrame.addSeparatorToToolbar();
		mainFrame.addButtonToToolbar(graphRight);
		mainFrame.addButtonToToolbar(graphLeft);
		mainFrame.addButtonToToolbar(graphUp);
		mainFrame.addButtonToToolbar(graphDown);
		mainFrame.addSeparatorToToolbar();
		mainFrame.addButtonToToolbar(zoomIn);
		mainFrame.addButtonToToolbar(zoomOut);
		mainFrame.addSeparatorToToolbar();
		mainFrame.addButtonToToolbar(settings);
		mainFrame.addButtonToToolbar(drawColorful);
	}

}
