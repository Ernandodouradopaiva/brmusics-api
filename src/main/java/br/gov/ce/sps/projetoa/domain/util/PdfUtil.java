package br.gov.ce.sps.projetoa.domain.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class PdfUtil {

	public static int compressPDF(String inputFileName, String outputFileName) throws IOException {
		// Necessário instalar no servidor "sudo apt install ghostscript"
		final String ghostScript = "/usr/bin/gs";
		String[] commandArray = null;

		// Obter somente o diretório
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		String workingFolder = inputFile.getParent();

		commandArray = new String[] { ghostScript, "-sDEVICE=pdfwrite", "-dCompatibilityLevel=1.4",
				"-dPDFSETTINGS=/screen", "-dNOPAUSE", "-dQUIET", "-dBATCH",
				"-sOutputFile=".concat(outputFile.getAbsolutePath()), inputFile.getAbsolutePath() };

		Process process = Runtime.getRuntime().exec(commandArray, null, new File(workingFolder));

		BufferedReader buCommand = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		while (buCommand.readLine() != null) {
		}

		int exitcode = 0;
		try {
			exitcode = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			process.destroy();
		}

		return exitcode;
	}
}