package net.serveron.sato.pic2pdfv2.business;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import net.serveron.sato.pic2pdfv2.frame.MainFrame;
import net.serveron.sato.pic2pdfv2.util.FileUtil;

public class PDFCreator {

//	public void Execute() {
//		File[] dirs = FileUtil.getDirList(dirPath);
//
//		for (int i = 0; i < dirs.length; i++) {
//			
//			try {
//				File[] files = FileUtil.getFileList(dirs[i].getPath());
//
//				String pdfPath = dirs[i].getPath() + ".pdf";
//
//				// PDF作成
//				createPDF(files, pdfPath);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * 
	 * @param files
	 */
	public void createPDF(File[] files, String pdfPath) throws Exception {

		// PDF作成
		PDDocument doc = new PDDocument();

		for(int i = 0; i < files.length; i++) {

			
			// ページサイズの設定
			PDRectangle rec = getPDRectangle(files[i]);

			// ページの追加
			PDPage page = new PDPage(rec);
			doc.addPage(page);

			PDPageContentStream contents = new PDPageContentStream(doc, page);

			//
			PDImageXObject pdImageXObject = PDImageXObject.createFromFileByContent(files[i], doc);

			contents.drawImage(pdImageXObject, 0, 0);

			// ストリームを閉じる
			contents.close();
		}
		// pdfファイルを出力
		doc.save(pdfPath);
		doc.close();
	}

	/***
	 * PDRectangle取得
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private PDRectangle getPDRectangle(File file) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(file);

		PDRectangle rec = new PDRectangle();
		rec.setUpperRightX(0);
		rec.setUpperRightY(0);
		rec.setLowerLeftX(bufferedImage.getWidth());
		rec.setLowerLeftY(bufferedImage.getHeight());

		bufferedImage.flush();

		return rec;
	}

}
