package net.serveron.sato.pic2pdfv2.ui;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel {
	private static final ColumnContext[] COLUMN_ARRAY = { new ColumnContext("No.", Integer.class, false),
			new ColumnContext("フォルダ名", String.class, false), new ColumnContext("進捗", Integer.class, false) };
	private final Map<Integer, SwingWorker> swmap = new ConcurrentHashMap<>();
	private int number;

	public void addProgressValue(String name, Integer iv, SwingWorker worker) {
		Object[] obj = { number, name, iv };
		super.addRow(obj);
		if (Objects.nonNull(worker)) {
			swmap.put(number, worker);
		}
		number++;
	}

	public synchronized SwingWorker getSwingWorker(int identifier) {
		Integer key = (Integer) getValueAt(identifier, 0);
		return swmap.get(key);
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return COLUMN_ARRAY[col].isEditable;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return COLUMN_ARRAY[column].columnClass;
	}

	@Override
	public int getColumnCount() {
		return COLUMN_ARRAY.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_ARRAY[column].columnName;
	}

	private static class ColumnContext {
		public final String columnName;
		public final Class columnClass;
		public final boolean isEditable;

		protected ColumnContext(String columnName, Class columnClass, boolean isEditable) {
			this.columnName = columnName;
			this.columnClass = columnClass;
			this.isEditable = isEditable;
		}
	}
}