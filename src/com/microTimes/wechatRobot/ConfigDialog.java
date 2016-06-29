package com.microTimes.wechatRobot;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public class ConfigDialog extends Dialog {

	protected Shell shell ;
	private Text txt_name;
	private Text text_keyword;
	private Text text_replytext;
	private Display display = getParent().getDisplay();
	private ReplyConfig config;
	private Browser browser;
	private Image image;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ConfigDialog(Shell parent, int style,ReplyConfig config) {
		super(parent, style);
		this.config = config;
	}

	public void setBrowser (Browser browser) {
		this.browser = browser;
	}
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return config;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(437, 408);
		shell.setText("规则设置");
		shell.setLayout(new BorderLayout(0, 0));
		Rectangle parentBounds = this.getParent().getBounds();  
        Rectangle shellBounds = shell.getBounds();  
        shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width)/2, parentBounds.y + (parentBounds.height - shellBounds.height)/2);
        
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.SOUTH);
		FormLayout f = new FormLayout();
		f.marginBottom = 10;
		f.marginHeight = 10;
		f.marginTop = 10;
		f.marginRight = 10;
		composite_1.setLayout(f);
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		FormData fd_button_1 = new FormData();
		fd_button_1.right = new FormAttachment(0, 150);
		fd_button_1.left = new FormAttachment(0, 85);
		button_1.setLayoutData(fd_button_1);
		button_1.setText("确定");
		
		Button btnNewButton = new Button(composite_1, SWT.NONE);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.right = new FormAttachment(0, 319);
		fd_btnNewButton.left = new FormAttachment(0, 239);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnNewButton.setText("取消");
		
		Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new FormLayout());
		
		Label label = new Label(composite_2, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		label.setText("规则名称");
		
		txt_name = new Text(composite_2, SWT.BORDER);
		fd_label.top = new FormAttachment(txt_name, 3, SWT.TOP);
		FormData fd_txt_name = new FormData();
		fd_txt_name.right = new FormAttachment(100, -179);
		fd_txt_name.left = new FormAttachment(label, 20);
		fd_txt_name.top = new FormAttachment(0, 10);
		txt_name.setLayoutData(fd_txt_name);
		
		Label label_1 = new Label(composite_2, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(label, 31);
		fd_label_1.left = new FormAttachment(0, 10);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("关键字");
		
		text_keyword = new Text(composite_2, SWT.BORDER);
		FormData fd_text_keyword = new FormData();
		fd_text_keyword.right = new FormAttachment(label_1, 278, SWT.RIGHT);
		fd_text_keyword.top = new FormAttachment(txt_name, 22);
		fd_text_keyword.left = new FormAttachment(label_1, 32);
		text_keyword.setLayoutData(fd_text_keyword);
		
		Label label_2 = new Label(composite_2, SWT.NONE);
		FormData fd_label_2 = new FormData();
		fd_label_2.top = new FormAttachment(label_1, 33);
		fd_label_2.left = new FormAttachment(label, 0, SWT.LEFT);
		label_2.setLayoutData(fd_label_2);
		label_2.setText("回复文字");
		
		text_replytext = new Text(composite_2, SWT.BORDER);
		FormData fd_text_replytext = new FormData();
		fd_text_replytext.bottom = new FormAttachment(text_keyword, 164, SWT.BOTTOM);
		fd_text_replytext.top = new FormAttachment(text_keyword, 27);
		fd_text_replytext.left = new FormAttachment(label_2, 20);
		fd_text_replytext.right = new FormAttachment(100, -55);
		text_replytext.setLayoutData(fd_text_replytext);
		
		Label label_3 = new Label(composite_2, SWT.NONE);
		FormData fd_label_3 = new FormData();
		fd_label_3.top = new FormAttachment(label_2, 141);
		fd_label_3.left = new FormAttachment(label, 0, SWT.LEFT);
		label_3.setLayoutData(fd_label_3);
		label_3.setText("回复图片");
		
		Button button = new Button(composite_2, SWT.NONE);
		
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(label_3, -5, SWT.TOP);
		fd_button.left = new FormAttachment(txt_name, 0, SWT.LEFT);
		button.setLayoutData(fd_button);
		button.setText("选择图片");
		
		final Label pictureHolder = new Label(composite_2, SWT.NONE);
		FormData fd_pictureHolder = new FormData();
		fd_pictureHolder.top = new FormAttachment(label_3, 0, SWT.TOP);
		fd_pictureHolder.left = new FormAttachment(button, 17);
		pictureHolder.setLayoutData(fd_pictureHolder);

		
		
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell,SWT.SINGLE);
				dialog.setFilterExtensions(new String[]{"*.jpg","*.jpeg","*.png","*.gif","*.bmp"});
				String filePath = dialog.open();
				if (filePath != null) {
					pictureHolder.setSize(50, 50);
					image = new Image(display,filePath);
					pictureHolder.redraw();
					pictureHolder.setData(filePath);
				}
			}
		});
		
		pictureHolder.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				final Rectangle bounds = image.getBounds();
				int picwidth = bounds.width; // 图片宽
				int picheight = bounds.height; // 图片高
				double H = 50; // label的高
				double W = 50; // label的宽
				double ratio = 1; // 缩放比率
				double r1 = H / picheight;
				double r2 = W / picwidth;
				ratio = Math.min(r1, r2);
				e.gc.drawImage(image, 0, 0, picwidth, picheight, 0, 0, (int) (picwidth * ratio),
						(int) (picheight * ratio));
			}
		});
		              
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (config == null) {
					config  = new ReplyConfig();
				}
				config.setConfigName(txt_name.getText());
				config.setKeywords(text_keyword.getText());
				config.setReplyText(text_replytext.getText());
				if (image != null) {
					config.setPicturePath(pictureHolder.getData().toString());
					MessageAPI.setBrowser(browser);
					String mediaId = MessageAPI.uploadFile(pictureHolder.getData().toString(), "filehelper");
					config.setReplyPicture(mediaId);
				}
				shell.dispose();
			}
		});
		
		if (config != null) {
			txt_name.setText(config.getConfigName());
			text_replytext.setText(config.getReplyText());
			text_keyword.setText(config.getKeywords());
			image = new Image(display,config.getPicturePath());
			pictureHolder.redraw();
		}
	}
}
