package net.serveron.sato.pic2pdfv2.ui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ProgressRenderer extends DefaultTableCellRenderer {
	private final JProgressBar bar = new JProgressBar(0, 100);

	public ProgressRenderer() {
		super();
		setOpaque(true);
		bar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Integer i = (Integer) value;
		String text = "Done";
		if (i < 0) {
			text = "Canceled";
		} else if (i < 100) {
			bar.setValue(i);
			return bar;
		}
		super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
		return this;
	}
}
