package com.wsg.robot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
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

import com.wsg.robot.tools.HttpUtil;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ImagePopUp extends Dialog {
	private Shell shell;
	private ReplyConfig config;
    private Browser browser;	

	public ImagePopUp(Shell parent) {
		super(parent);
	}

	public void setConfig (ReplyConfig conf) {
		this.config = conf;
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
	
	public void setBrowser(Browser browser) {
		this.browser = browser;
	};
	
	protected void createContents() {  
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);  
        shell.setSize(400, 500);  
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
        rd_text.setText("图片回复");
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
        txt_keywords.setToolTipText("可以设置多个关键字,不能重复设置.关键字可以是正则表达式.");
        if (this.config != null && this.config.getMatch() != null) {
        	txt_keywords.setText(this.config.getMatch());
        }
        
        Label l2 = new Label(shell,SWT.NONE);
        l2.setText("回复内容");
        l2.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false,1,1));
        
        
        Text txt_content = new Text(shell,SWT.BORDER);
        if (this.config == null) {
        	txt_content.setVisible(false);
        }
        txt_content.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,4,4));
        txt_content.setToolTipText("回复类型为文本回复的,直接回复此处的内容;自定义回复的,此处填写处理的类名(如:com.wsg.robot.reply.TextReply),处理类需继承com.wsg.robot.IReply接口。");
        
        if (this.config != null && this.config.getReplyContent() != null) {
        	txt_content.setText(this.config.getReplyContent());
        }
        
        if (this.config == null) {
        	Group group2  = new Group(shell,SWT.NONE);
            group2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,5,2));
            group2.setLayout(new GridLayout());
            browser.execute("var $scope3 = angular.element(\"[ng-repeat='message in chatContent']:last\").scope();var imageMessageThumbs = [];var mediaIds = [];");
            browser.execute("for (var i = 0 ;i < $scope3.imagesMessagesList.length; i ++) {"
            		+ " if($scope3.imagesMessagesList[i].msg.MediaId) { "
            		+ " imageMessageThumbs[i] = $scope3.imagesMessagesList[i].url;"
            		+ " mediaIds[i] = $scope3.imagesMessagesList[i].msg.MediaId;}}");
            Object[] o = (Object[]) browser.evaluate("return imageMessageThumbs;");
            Object[] medias = (Object[]) browser.evaluate("return mediaIds;");
            String cookiestr = (String)browser.evaluate("return document.cookie;");
            Map <String ,String> cookie = new HashMap<> ();
            cookie.put("Cookie", cookiestr);
            for(int i = 0; i < o.length ;i ++) {
            	String src = o[i].toString();
            	final int k = i;
            	Image image = null;
            	Button btn1 = new Button(group2,SWT.PUSH);
            	btn1.setSize(50, 50);
            	if (src.startsWith("data:image")) {
            		 image  = this.base64StringToImage(src.replace("data:image/jpeg;base64,", ""));
            		
            	}
            	else {
            		 image = this.loadRemoteImage("https://wx.qq.com" + src,cookie);
            	}
            	 btn1.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1));
                 //btn1.setBackgroundImage(image);
             	if (image != null) {
             		btn1.setImage(image);
             	}
                 //Image image = new Image(this.shell.getDisplay());
                 btn1.addSelectionListener(new SelectionAdapter() {

     				@Override
     				public void widgetSelected(SelectionEvent e) {
     					txt_content.setText(medias[k].toString());
     					txt_content.setVisible(true);
     					group2.setVisible(false);
     				}
                 	
                 });
            	
            }
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
	
	
	public Image loadRemoteImage (String uri,Map<String,String> params) {
		InputStream is = null;
		try {
			System.out.println(uri);
			is = HttpUtil.getHttpsSteam(uri, params);
			ImageLoader loader = new ImageLoader();
			ImageData[] datas = loader.load(is);
			Image image  = new Image(this.shell.getDisplay(),datas[0]);
			return image;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public Image base64StringToImage(String base64String){    
		BASE64Encoder encoder = new sun.misc.BASE64Encoder();  
		BASE64Decoder decoder = new sun.misc.BASE64Decoder();   
        try {    
            byte[] bytes1 = decoder.decodeBuffer(base64String);                  
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);    
            ImageLoader loader = new ImageLoader(); 
            ImageData[] data = loader.load(bais);
            Image image  = new Image(this.shell.getDisplay(),data[0]);
            return image;
        } catch (IOException e) {    
            e.printStackTrace();    
        }
        return null;
    }
}
