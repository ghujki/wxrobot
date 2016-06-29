package com.microTimes.wechatRobot;

import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MainApplication {
	private static Shell shell ;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		System.setProperty("org.eclipse.swt.browser.XULRunnerPath", System.getProperty("user.dir") + "\\xulrunner");
		try {
			RobotConfig.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		shell = new MainShell(display);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public static void showMessage(String message) {
		MessageBox box = new MessageBox(shell);
		box.setMessage(message);
		box.open();
	}
}
