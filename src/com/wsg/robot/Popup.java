package com.wsg.robot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Popup extends Dialog{

	private Shell shell;
	private ReplyConfig config;
	
	public Popup(Shell parent) {
		super(parent);
	}

	public void setConfig(ReplyConfig c) {
		this.config = c;
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
        return config;  
    }  
  
    protected void createContents() {  
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);  
        shell.setSize(400, 300);  
        shell.setText("编辑规则");  
        
        GridLayout layout = new GridLayout(5,false);        
        shell.setLayout(layout);
        
        Group group = new Group(shell, SWT.NONE);
        group.setText("回复类型");
        group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,5,1));
        
        FormLayout f = new FormLayout();
        f.marginLeft = 10;
        f.marginTop = 10;
        f.marginBottom = 10;
        group.setLayout(f);
        
        
        Button rd_text = new Button(group,SWT.RADIO);
        rd_text.setText("文本回复");
        rd_text.setData("Text");
        FormData d1 = new FormData();
        d1.left = new FormAttachment(0,10);
        rd_text.setLayoutData(d1);
        
        Button rd_cus = new Button(group,SWT.RADIO);
        rd_cus.setText("自定义");
        rd_cus.setData("Custom");
        FormData d2 = new FormData();
        d2.left = new FormAttachment(rd_text,10);
        rd_cus.setLayoutData(d2);
        
        if (this.config != null && this.config.getReplyType().equals("Custom")) {
        	rd_cus.setSelection(true);
        }
        
        Label l1 = new Label(shell,SWT.NONE);
        l1.setText("关键字");
        l1.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false,1,1));
        
        Text txt_keywords = new Text(shell,SWT.BORDER);
        txt_keywords.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,4,1));
        txt_keywords.setToolTipText("可以设置多个关键字,用逗号(半角)分开;关键字不能重复设置.关键字可以是正则表达式.");
        if (this.config != null && this.config.getMatch() != null) {
        	txt_keywords.setText(this.config.getMatch());
        }
        
        Label l2 = new Label(shell,SWT.NONE);
        l2.setText("回复内容");
        l2.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false,1,1));
        
        Text txt_content = new Text(shell,SWT.BORDER);
        txt_content.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,4,4));
        txt_content.setToolTipText("回复类型为文本回复的,直接回复此处的内容;自定义回复的,此处填写处理的类名(如:com.wsg.robot.reply.TextReply),处理类需继承com.wsg.robot.IReply接口。");
        
        if (this.config != null && this.config.getReplyContent() != null) {
        	txt_content.setText(this.config.getReplyContent());
        }
        
        Button btn_close = new Button(shell,SWT.PUSH );
        btn_close.setText("确定");
        btn_close.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,true,false,5,1));
        btn_close.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseUp(MouseEvent e) {
				super.mouseUp(e);
				String type = rd_text.getData().toString();
				if (rd_cus.getSelection()) {
					type = rd_cus.getData().toString();
				}
				if (config == null && txt_keywords.getText().length() > 0) {
					config = new ReplyConfig(txt_keywords.getText(), type, txt_content.getText());
				} else if (txt_keywords.getText().length() > 0) {
					config.setMatch(txt_keywords.getText());
					config.setReplyType(type);
					config.setReplyContent(txt_content.getText());
				}
				shell.dispose();
			}
        });
        
        Rectangle parentBounds = this.getParent().getBounds();  
        Rectangle shellBounds = shell.getBounds();  
        shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width)/2, parentBounds.y + (parentBounds.height - shellBounds.height)/2);
    } 
}
