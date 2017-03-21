package components;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.google.cloud.vision.GoogleCloudVision;

@SuppressWarnings("serial")
public class MainApp extends JFrame {

	private JPanel contentPane;
	File targetFile;
	BufferedImage targetImg;
	public JPanel panel, panel_1, panel_2;
	private JTextArea log;
	private static final int baseSize = 128;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp frame = new MainApp();
					frame.setVisible(true);
					frame.setResizable(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainApp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
		contentPane.add(panel, BorderLayout.WEST);

		log = new JTextArea(33, 48);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseButtonActionPerformed(e);
			}
		});

		JLabel lblSelectTargetPicture = new JLabel("Select target picture..");

		JButton btnAnalyse = new JButton("Analyse");
		btnAnalyse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				analyseButtonActionPerformed(e);
			}

		});

		panel_1 = new JPanel();
		panel_1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
				.createSequentialGroup().addGap(6)
				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup().addComponent(lblSelectTargetPicture).addGap(6)
								.addComponent(btnBrowse))
						.addGroup(gl_panel.createSequentialGroup().addGap(10).addComponent(btnAnalyse).addGap(18))))
				.addGroup(gl_panel.createSequentialGroup().addGap(50))
				.addGroup(gl_panel.createSequentialGroup().addContainerGap().addComponent(panel_1,
						GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
				.createSequentialGroup()
				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup().addGap(7).addComponent(lblSelectTargetPicture))
						.addGroup(gl_panel.createSequentialGroup().addGap(3).addComponent(btnBrowse)))
				.addGap(18).addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 199, GroupLayout.PREFERRED_SIZE)
				.addGap(22).addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(btnAnalyse))
				.addGap(18)));

		panel.setLayout(gl_panel);
		
		panel_2 = new JPanel();
		panel_2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
		
		panel_2.add(new JScrollPane(log));
		contentPane.add(panel_2, BorderLayout.EAST);
		
		
	}

	public BufferedImage rescale(BufferedImage originalImage) {
		BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
		g.dispose();
		return resizedImage;
	}

	public void setTarget(File reference) {
		try {
			targetFile = reference;
			targetImg = rescale(ImageIO.read(reference));
		} catch (IOException ex) {
			Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
		}

		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(new JLabel(new ImageIcon(targetImg)));
		setVisible(true);
	}

	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser fc = new JFileChooser();
		int res = fc.showOpenDialog(null);
		// We have an image!
		try {
			if (res == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				setTarget(file);
			} // Oops!
			else {
				JOptionPane.showMessageDialog(null, "You must select one image to be the reference.", "Aborting...",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception iOException) {
		}

	}

	private void analyseButtonActionPerformed(ActionEvent e) {
		try {
			GoogleCloudVision.processImage(targetFile, log);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
