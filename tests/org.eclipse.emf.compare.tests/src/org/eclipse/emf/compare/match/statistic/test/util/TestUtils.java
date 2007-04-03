package org.eclipse.emf.compare.match.statistic.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.compare.match.statistic.test.EMFCompareTestPlugin;

public class TestUtils {
	private static final String PLUGIN_ID = ""; // TODO check that Id

	private static final String CLASS_FILE = "";

	public static String getPluginDirectory() {
		try {
			return new File(FileLocator
					.toFileURL(
							EMFCompareTestPlugin.getDefault().getBundle()
									.getEntry("/")).getFile()).toString();
		} catch (final Throwable t) {
		}

		final URL url = ClassLoader.getSystemResource(CLASS_FILE);
		if (url != null) {
			String path = url.getPath();
			path = path.substring(0, path.indexOf(PLUGIN_ID));
			if (path.startsWith("file:")) {
				path = path.substring("file:".length());
			}
			final File parentDir = new File(path);
			if (parentDir.isDirectory()) {
				final File[] files = parentDir.listFiles();
				for (int i = 0, maxi = files.length; i < maxi; i++) {
					if (files[i].isDirectory()
							&& files[i].getName().startsWith(PLUGIN_ID)) {
						return files[i].getAbsolutePath();
					}
				}
			}
		}

		return null;
	}

	public static String getPluginDirectory(final String pluginID) {
		try {
			if (Platform.isRunning()) {
				final File file = new File(FileLocator.toFileURL(
						Platform.getBundle(pluginID).getEntry("/")).getFile());
				if (file.isDirectory()) {
					return file.getAbsolutePath();
				}
			}
		} catch (final Throwable t) {
		}

		final File parentDirectory = new File(getPluginDirectory());
		final File[] plugins = parentDirectory.listFiles();
		for (int i = 0, maxi = plugins.length; i < maxi; i++) {
			if (plugins[i].isDirectory()) {
				final String name = plugins[i].getName();
				if (name.equals(pluginID) || name.startsWith(pluginID + "_")) {
					return plugins[i].getAbsolutePath();
				}
			}
		}

		return null;
	}

	public static String readFile(final File file, final boolean useSystemLineSeparator) {
		final StringBuffer stringBuffer = new StringBuffer();
		try {
			final BufferedReader in = new BufferedReader(new FileReader(file));
			try {
				int size = 0;
				final char[] buff = new char[512];
				while ((size = in.read(buff)) >= 0) {
					stringBuffer.append(buff, 0, size);
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}

		final int length = stringBuffer.length();
		if (length > 0) {
			final String nl = useSystemLineSeparator ? System.getProperties()
					.getProperty("line.separator") : "\n";
			return stringBuffer.toString().replaceAll("\\r\\n", "\n")
					.replaceAll("[\\n|\\r]", nl);
		}
		return stringBuffer.toString();
	}

	public static void delete(final File file) {
		if (file.isDirectory()) {
			final File[] children = file.listFiles();
			for (int i = 0, maxi = children.length; i < maxi; i++) {
				delete(children[i]);
			}
		}

		if (file.exists()) {
			file.delete();
		}
	}

}