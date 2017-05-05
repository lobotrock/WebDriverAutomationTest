package com.lobotrock.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

public class LoggingUtil {
	public static void writeErrorStack(Logger l, Exception e) {
		StringWriter stackWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stackWriter);
		e.printStackTrace(pw);
		l.error(stackWriter.toString());
		pw.close();
		stackWriter = null;
		pw = null;
	}
}
