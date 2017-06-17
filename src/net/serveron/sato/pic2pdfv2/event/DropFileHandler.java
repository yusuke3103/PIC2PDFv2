package net.serveron.sato.pic2pdfv2.event;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 * ドロップ操作の処理を行うクラス
 */
public class DropFileHandler extends TransferHandler {

	private JTextComponent jTextComponent = null;
	
	public DropFileHandler(JTextComponent component) {
		super();
		this.jTextComponent = component;
	}
	
	/**
	 * ドロップされたものを受け取るか判断 (ファイルのときだけ受け取る)
	 */
	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDrop()) {
			// ドロップ操作でない場合は受け取らない
	        return false;
	    }

		if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			// ドロップされたのがファイルでない場合は受け取らない
	        return false;
	    }

		return true;
	}

	/**
	 * ドロップされたファイルを受け取る
	 */
	@Override
	public boolean importData(TransferSupport support) {
		// 受け取っていいものか確認する
		if (!canImport(support)) {
	        return false;
	    }

		// ドロップ処理
		Transferable t = support.getTransferable();
		try {
			// ファイルを受け取る
			List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

			if (files.size() != 1){
				return false;
			}
			
			// テキストエリアに表示するファイル名リストを作成する
			String fileList = files.get(0).toString();

			// テキストエリアにファイル名のリストを表示する
			jTextComponent.setText(fileList.toString());
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}