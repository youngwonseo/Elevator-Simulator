import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class ElevatorUI extends JFrame {

	private ElevatorSystem sys;

	private JButton btnStart, btnStop;
	private JProgressBar progressBar;
	private JTextField txtWorkOut, txtWorkIn, txtElevatorStep, txtPersonStep,
			txtEleCapa, txtDoneTime;
	private int elvStep;
	private final int eleWidth = 30, eleHeight = 60;

	private boolean isDone = false;
	private RunSimulation process = new RunSimulation();
	private RunDraw proDraw = new RunDraw();

	private MainBoard mainBoard = new MainBoard();

	public ElevatorUI() {

		sys = new ElevatorSystem();

		setLayout(new BorderLayout());

		btnStart = new JButton("½ÃÀÛ");
		btnStop = new JButton("Á¤Áö");
		txtWorkOut = new JTextField();
		txtWorkIn = new JTextField();
		txtElevatorStep = new JTextField();
		txtPersonStep = new JTextField();
		txtEleCapa = new JTextField();
		txtDoneTime = new JTextField();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 7));

		mainPanel.add(btnStart);
		mainPanel.add(new JLabel("Åð±Ù½Â°´È®·ü : "));
		mainPanel.add(txtWorkOut);
		mainPanel.add(new JLabel("Ãâ±Ù½Â°´È®À² : "));
		mainPanel.add(txtWorkIn);
		mainPanel.add(new JLabel("¿¤¸®º£ÀÌÅÍ Á¤¿ø : "));
		mainPanel.add(txtEleCapa);
		mainPanel.add(btnStop);
		mainPanel.add(new JLabel("¿¤¸®º£ÀÌÅÍ ½Ã°£ : "));
		mainPanel.add(txtElevatorStep);
		mainPanel.add(new JLabel("½Â°´ ½Ã°£ : "));
		mainPanel.add(txtPersonStep);
		mainPanel.add(new JLabel("Á¾·á ½Ã°¢: "));
		mainPanel.add(txtDoneTime);

		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				if(checkBlank())
					return;
				
				sys.initialization(Integer.parseInt(txtWorkOut.getText()),
						Integer.parseInt(txtWorkIn.getText()),
						Integer.parseInt(txtEleCapa.getText()),
						Integer.parseInt(txtElevatorStep.getText()),
						Integer.parseInt(txtPersonStep.getText()),
						Integer.parseInt(txtDoneTime.getText()));
				progressBar.setMaximum(Integer.parseInt(txtDoneTime.getText()));
				
				
				elvStep = Integer.parseInt(txtElevatorStep.getText());

				proDraw.start();
				process.start();

			}
		});

		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// process.stop(process);

			}
		});

		add(mainPanel, "North");
		add(mainBoard, "Center");
		progressBar = new JProgressBar();

		add(progressBar, "South");

		setSize(800, 700);
		setVisible(true);

	}
	
	//ºóÄ­ÀÌ ÀÖÀ» °æ¿ì
	private boolean checkBlank() {
		if (txtWorkOut.getText().trim().equals("")
				|| txtWorkIn.getText().trim().equals("")
				|| txtEleCapa.getText().trim().equals("")
				|| txtElevatorStep.getText().trim().equals("")
				|| txtPersonStep.getText().trim().equals("")
				|| txtDoneTime.getText().trim().equals("")) {
			JDialog alert = new JDialog();
			alert.add(new JLabel("  ºóÄ­À» Ã¤¿öÁÖ¼¼¿ä"));
			alert.setSize(150,80);
			alert.setVisible(true);
			return true;
		}
		return false;
	}

	class RunSimulation extends Thread {
		public synchronized void run() {
			try {
				sys.run();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isDone = true;
		}
	}

	class RunDraw extends Thread {

		public synchronized void run() {

			while (!isDone) {
				
				progressBar.setValue(sys.getCurrentClock());
		
				for (int i = 0; i < 2; i++) {
					
					int state = sys.getElevator()[i].getCurrentStateForDraw();
					int di = sys.getElevator()[i].getDirection();

					int currentFloor = state / 100;
					int step = state % 100;

					int y = 540 - (currentFloor * 60);

					if (di == 1)
						y -= (60 / elvStep * step);
					else
						y += (60 / elvStep * step);

					mainBoard.setY(i, y);
					mainBoard.repaint();
					notify();
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	class MainBoard extends JPanel {
		private int x = 150;
		private int[] y = { 540, 540 };
		
		public void paint(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, 800, 700);
			
			g.setColor(Color.black);

			// Ãþ Ç¥ÇöÇÏ±â
			for (int i = 0; i < 10; i++) {
				g.drawLine(0, 600 - (i * 60), 800, 600 - (i * 60));
			}

			// 10°³ÀÇ ÃþÀÇ »ç¶÷µé ±×¸®±â
			for (int i = 0; i < 10; i++) {
				int p = sys.getSize(i);

				for (int j = 0; j < p; j++)
					g.drawRect(240 + j * (30 + 10), 570 - (i * 60), 30, 30);
			}

			// °¢°¢ÀÇ ¿¤¸®º£ÀÌÅÍ ±×¸®±â
			for (int i = 0; i < 2; i++) {
				g.setColor(Color.BLACK);

				char[] a = new char[3];
				a[0] = new Integer(sys.getElevator()[i].getSize()).toString()
						.charAt(0);

				g.drawChars(a, 0, 1, x + 10 + i * 50, y[i] + 20);
				g.setColor(Color.blue);
				g.drawRect(x + i * 50, y[i], eleWidth, eleHeight);
			}
		}

		public void setY(int i, int y) {
			this.y[i] = y;

		}

	}

}
