package net.serveron.sato.pic2pdfv2.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import net.serveron.sato.pic2pdfv2.event.DropFileHandler;
import net.serveron.sato.pic2pdfv2.ui.ProgressRenderer;
import net.serveron.sato.pic2pdfv2.ui.TableModel;
import net.serveron.sato.pic2pdfv2.util.FileUtil;

public class MainFrame extends JFrame {

	private MainFrame _instance = null;

	private JButton btnRef = new JButton();
	private JLabel lblWorkDir = new JLabel();
	private JTextField txtWorkDir = new JTextField();
	private JButton btnExecute = new JButton();
	private JLabel lblDetailCount = new JLabel();

	private final TableModel model = new TableModel();
	private final JTable table = new JTable(model);

	public MainFrame() {

		_instance = this;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(800, 600);
		setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
		getContentPane().setLayout(null);
		initialize();
	}

	private void initialize() {

		btnRef.setText("参照");
		btnRef.setBounds(728, 7, 66, 22);
		btnRef.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int selected = jFileChooser.showOpenDialog(null);
				String filePath = "";
				if (selected == 0) {
					filePath = jFileChooser.getSelectedFile().getAbsolutePath();
				}
				txtWorkDir.setText(filePath);
			}
		});

		lblWorkDir.setText("作業フォルダー");
		lblWorkDir.setBounds(6, 6, 99, 22);
		
		txtWorkDir.setEditable(false);
		txtWorkDir.setBounds(117, 6, 599, 22);
		txtWorkDir.setDragEnabled(true);
		txtWorkDir.setTransferHandler(new DropFileHandler(txtWorkDir));
		
		btnExecute.setText("実行");
		btnExecute.setBounds(16, 40, 778, 47);
		btnExecute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				for (int i = 0; i < model.getRowCount(); i++) {
					model.removeRow(i);
				}
				_instance.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				btnExecute.setEnabled(false);
				btnRef.setEnabled(false);

				File[] target = FileUtil.getDirList(txtWorkDir.getText());

				if (target == null || target.length == 0){
					target = new File[1];
					target[0] = new File(txtWorkDir.getText());
				}
				
				File[] dirs = target;
				
				for (int i = 0; i < dirs.length; i++) {

					final int key = model.getRowCount();
					SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {

						@Override
						protected Integer doInBackground() {

							int fileCount = 0;

							try {
								File[] files = FileUtil.getFileList(dirs[key].getPath());

								fileCount = files.length;

								String pdfPath = dirs[key].getPath() + ".pdf";

								// PDF作成
								PDDocument doc = new PDDocument();

								for (int i = 0; i < files.length; i++) {

									// ページサイズの設定
									BufferedImage bufferedImage = ImageIO.read(files[i]);

									PDRectangle rec = new PDRectangle();
									rec.setUpperRightX(0);
									rec.setUpperRightY(0);
									rec.setLowerLeftX(bufferedImage.getWidth());
									rec.setLowerLeftY(bufferedImage.getHeight());

									bufferedImage.flush();

									// ページの追加
									PDPage page = new PDPage(rec);
									doc.addPage(page);

									PDPageContentStream contents = new PDPageContentStream(doc, page);

									//
									PDImageXObject pdImageXObject = PDImageXObject.createFromFileByContent(files[i],
											doc);

									contents.drawImage(pdImageXObject, 0, 0);

									// ストリームを閉じる
									contents.close();
									// 途中経過
									publish(100 * i / files.length);
								}
								// pdfファイルを出力
								doc.save(pdfPath);
								doc.close();

							} catch (Exception e) {
								e.printStackTrace();
							}
							return fileCount;
						}

						@Override
						protected void process(List<Integer> c) {
							if (isCancelled()) {
								return;
							}
							if (!isDisplayable()) {
								System.out.println("process: DISPOSE_ON_CLOSE");
								cancel(true);
								return;
							}
							model.setValueAt(c.get(c.size() - 1), key, 2);
						}

						@Override
						protected void done() {
							String text;
							int i = -1;
							if (isCancelled()) {
								text = "Cancelled";
							} else {
								try {
									i = get();
									text = i >= 0 ? "Done" : "Disposed";
								} catch (InterruptedException | ExecutionException ex) {
									ex.printStackTrace();
									text = ex.getMessage();
								}finally {
									isAllDone();
								}
							}

							System.out.format("%s:%s(%dms)%n", key, text, i);
						}

						private void isAllDone() {
							int rowCount = model.getRowCount();
							for (int i = 0; i < rowCount; i++) {
								SwingWorker worker = model.getSwingWorker(i);
								if (worker.isDone() == false) {
									return;
								}
							}
							_instance.setDefaultCloseOperation(EXIT_ON_CLOSE);
							btnExecute.setEnabled(true);
							btnRef.setEnabled(true);
						}
					};

					model.addProgressValue(dirs[i].getName(), 0, worker);
					worker.execute();
				}
			}
		});
		lblDetailCount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDetailCount.setBounds(490, 6, 92, 16);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(6, 99, 788, 473);
		scrollPane.setBackground(Color.WHITE);

		table.setFillsViewportHeight(true);
		table.setIntercellSpacing(new Dimension());
		table.setShowGrid(false);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		lblWorkDir.setText("作業フォルダー");

		TableColumn column = table.getColumnModel().getColumn(0);
		column.setMaxWidth(30);
		column.setMinWidth(30);
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(new ProgressRenderer());

		getContentPane().add(lblWorkDir);
		getContentPane().add(txtWorkDir);
		getContentPane().add(btnRef);
		getContentPane().add(btnExecute);
		getContentPane().add(scrollPane);
	}
}
