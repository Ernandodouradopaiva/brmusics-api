package br.gov.ce.sps.projetoa.infrastructure.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.jboss.logging.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import br.gov.ce.sps.projetoa.infrastructure.service.report.ReportExcepetion;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class ReportUtils {
	private static Logger LOGGER = Logger.getLogger(ReportUtils.class);

	public static <T extends Object> ResponseEntity<byte[]> printReport(Map<String, Object> params, List<T> data,
			String reportPath) {

		try {
			var absolutePath = (new File(reportPath)).getAbsolutePath();
			var fileName = (new File(reportPath)).getName();
			var fileNamePdf = fileName.substring(0, fileName.lastIndexOf('.')).concat(".pdf");

			byte[] bytesPdf = getByteArray(params, data, absolutePath);

			var headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(fileNamePdf));
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(bytesPdf);
		} catch (Exception e) {
			throw new ReportExcepetion("Não foi possível emitir o relatório", e);
		}
	}

	public static <T extends Object> ResponseEntity<byte[]> printReport(List<JasperPrint> prints, String pdfName) {
		var exp = new JRPdfExporter();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		exp.setExporterInput(SimpleExporterInput.getInstance(prints));
		exp.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

		try {
			exp.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}

		try {
			byte[] bytesPdf = outputStream.toByteArray();
			outputStream.close();

			var headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(pdfName));
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(bytesPdf);
		} catch (Exception e) {
			throw new ReportExcepetion("Não foi possível emitir o relatório", e);
		}
	}

	public static <T extends Object> byte[] getByteArray(Map<String, Object> params, List<T> data, String reportPath) {
		try {
			var jasperPrint = getJasperPrint(params, data, reportPath);

			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (Exception e) {
			throw new ReportExcepetion("Não foi possível emitir o relatório", e);
		}
	}

	public static <T extends Object> JasperPrint getJasperPrint(Map<String, Object> params, List<T> data,
			String reportPath) {
		try {
			var ret = new Throwable().getStackTrace();
			var ste = Arrays.asList(ret).stream().filter(r -> r.getClassName().contains("Controller")).findFirst()
					.orElse(null);

			var cl = Class.forName(ste.getClassName()); // Classe que chama o método estático
			var inpuntStream = cl.getResourceAsStream(reportPath);

			LOGGER.info("Caller class: ".concat(cl.getName()));

			BufferedImage header = ImageIO.read(cl.getResourceAsStream("/images/header.png"));
			BufferedImage footer = ImageIO.read(cl.getResourceAsStream("/images/ondas.png"));

			if (params == null)
				params = new HashMap<>();
			
			JRSwapFile arquivoSwap = new JRSwapFile("/tmp", 4096, 800);
			
			// Instancia o virtualizador
			JRAbstractLRUVirtualizer virtualizer = new JRSwapFileVirtualizer(100, arquivoSwap, true);
			 

			params.put("header", header);
			params.put("footer", footer);
			params.put("REPORT_LOCALE", new Locale("pt", "BR"));
			params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

			var dataSource = new JRBeanCollectionDataSource(data);
			var jasperPrint = JasperFillManager.fillReport(inpuntStream, params, dataSource);

			return jasperPrint;
		} catch (Exception e) {
			throw new ReportExcepetion("Não foi possível emitir o relatório", e);
		}
	}

	public static ResponseEntity<byte[]> download(byte[] bytes, String reportPath) {
		try {
			var fileName = (new File(reportPath)).getAbsolutePath();
			var headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(fileName));
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(bytes);
		} catch (Exception e) {
			throw new ReportExcepetion("Não foi possível emitir o relatório", e);
		}
	}
}