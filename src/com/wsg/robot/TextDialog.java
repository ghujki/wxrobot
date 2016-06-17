package com.wsg.robot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextDialog extends Dialog {
	private Shell shell;
	private String obj;
	
	public TextDialog(Shell parent) {
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
        return obj;  
    }

	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);  
        shell.setSize(400, 150);  
        shell.setText("批量更新");
        
        GridLayout layout = new GridLayout(3,false);        
        shell.setLayout(layout);
        
        Group group = new Group(shell, SWT.NONE);
        group.setText("media id");
        group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,3,3));
        group.setLayout(new GridLayout());
        
        Text text = new Text(group, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,3,3));
        text.paste();
        text.forceFocus();
        text.selectAll();
        
        Button btn_ok = new Button(shell,SWT.PUSH);
        btn_ok.setText("确定");
        btn_ok.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,true,false,2,1));
        btn_ok.addMouseListener(new MouseAdapter () {
        	public void mouseUp(MouseEvent e){
        		obj = text.getText(); 
        		shell.dispose();
        	}
        });
        
        Rectangle parentBounds = this.getParent().getBounds();  
        Rectangle shellBounds = shell.getBounds();  
        shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width)/2, parentBounds.y + (parentBounds.height - shellBounds.height)/2);
	}  
}
