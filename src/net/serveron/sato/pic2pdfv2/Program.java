package net.serveron.sato.pic2pdfv2;

import java.awt.EventQueue;

import net.serveron.sato.pic2pdfv2.frame.MainFrame;

public class Program {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
