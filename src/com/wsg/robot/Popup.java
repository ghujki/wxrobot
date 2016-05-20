package com.wsg.robot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Popup extends Dialog{

	private Shell shell;
	private Object result;
	public Popup(Shell parent) {
		super(parent);
	}

	public Object open() {  
        createContents();  
        shell.open();  
        shell.layout();  
        Display display = getParent().getDisplay();  
        while (!shell.isDisposed()) {  
            if (!display.readAndDispatch())  
                display.sleep();  
        }  
        return result;  
    }  
  
    protected void createContents() {  
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);  
        shell.setSize(312, 212);  
        shell.setText("SWT Dialog");  
  
        Button button = new Button(shell, SWT.NONE);  
        button.setText("button");  
        button.setBounds(127, 74, 44, 23);  
    } 
}
