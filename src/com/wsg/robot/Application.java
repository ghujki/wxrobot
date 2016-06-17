package com.wsg.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.json.JSONArray;
import org.json.JSONObject;

public class Application {
	private Display display;
    private Shell shell ;
    private Browser browser;
    private Table tb_rules;
    private Menu menu;
    private Table tb_rules2;
    private String script = " function findLast(obj,userName) {" +
            "     obj.forEach(function(e) {" +
            "         if (obj.UserName == userName) {" +
            "             return e;" +
            "         }" +
            "     });" +
            "     return null;" +
            " }" +
            " var $scope1 = angular.element('.chat_item').scope(); "
            +" var $scope2 = angular.element('[ng-controller=chatSenderController]').scope(); "
            +" var cur_userName = $scope1.account.UserName;"
            +" var enable_boot = true; "
            +" function getDeviceID() {return \"e\" + (\"\" + Math.random().toFixed(15)).substring(2, 17);}"
            +" function getCookie(e) {"
            +" for (var t = e + \"=\",o = document.cookie.split(\";\"), n = 0; n < o.length; n++) { "
            +"  for (var r = o[n];\" \" == r.charAt(0);) r = r.substring(1); "
            +"    if ( - 1 != r.indexOf(t)) return r.substring(t.length, r.length)} return \"\"}"

            + "function now() {return + new Date;} "
            + "function getMsgId () {return (now() + Math.random().toFixed(3)).replace(\".\", \"\");}"
            + "var local_id = getMsgId();"
  			
            + "function sendImageMessage(media_id,sendTo) {"
            + "var b = {\"BaseRequest\":{\"Uin\":$scope1.account.Uin,"
            + " \"Sid\":getCookie(\"wxsid\"),\"Skey\":$scope1.account.HeadImgUrl.substring($scope1.account.HeadImgUrl.lastIndexOf(\"=\") + 1),"
            + "\"DeviceID\":getDeviceID()},\"Msg\":{\"Type\":3,\"MediaId\":media_id,\"FromUserName\":$scope1.account.UserName,"
            + "\"ToUserName\":sendTo,\"LocalID\":local_id,\"ClientMsgId\":local_id},\"Scene\":0};"
            + " $.ajax({url:'/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json',type:'POST',contentType : 'application/json',data:JSON.stringify(b),"
            + " success:function(ex){var e = JSON.parse(ex);if (e.MsgID == null || e.MsgID == '') {alert('图片发送出错,可能资源已过期,需要重新设置图片');}},error:function(e){alert(e.responseText);}});"
            + "return JSON.stringify(b);} "
            + " $scope1.$watch('chatList',function(v1,v2){ "
            +"     var  i = 0; "
            +"     var e = v1[0]; "
            +"     var last = findLast (v2,e.UserName); "
            +"     var msg = e.MMDigest; "
            +"     if (enable_boot && msg != null && msg != '' && (last == null || msg != last.MMDigest)  && !e.isBlackContact() && !e.isSpContact() && e.UserName != cur_userName) { " //&& !e.isRoomContact()
            +"         $scope1.currentUserName = e.UserName; "
            +"         $scope1.itemClick(e.UserName); "
            +"         setTimeout(function(){ "
            +"             $scope1.$apply(); "
            +"             var $scope3 = angular.element(\"[ng-repeat='message in chatContent']:last\").scope(); "
            +"             var message = $scope3.message.MMActualContent,msgId= $scope3.message.MsgId,actualSender = $scope3.message.MMActualSender; "
            +"             if (cur_userName == actualSender) {return;} "
            +"             var reply = getReply(message,msgId,cur_userName,actualSender,e.isRoomContact()); "
            +"             if(reply != null) { "
            +"                $scope2.editAreaCtn = reply; "
            +"                $scope2.sendTextMessage(); "
            +"                setTimeout(function(){ "
            +"                         $scope2.$apply();$scope1.$apply() "
            +"                },500); "
            +"             }"
            +"             var sender = actualSender; "
            +"             var img = getImageReply(message,msgId,cur_userName,actualSender,e.isRoomContact());"
            +"             if (img != null) {try{sendImageMessage(img,sender);}catch(e){alert(e);}} "
            +"         },500); "
            +"     } "
            +"},true);";
    /**
     * 是否支持群聊回复
     * */
    public static boolean ROOMCHAT_ENABLED = false;
    private static List<ReplyConfig> configs = new ArrayList<> ();
    private static String filePath = "config/reply.json";
    private static String filePath2 = "config/imageReply.json";
    private Logger logger = LogManager.getLogger(Application.class);
    
    private static List <ReplyConfig> imageReplyConfigs = new ArrayList <> ();
    
    //
    private boolean firstClick = true;
    
	public Application() {
		display = new Display ();
		shell = new Shell(display);
		try {
			String configStr = getConfigString(filePath);
			JSONArray arr = new JSONArray(configStr);
			for (Object o : arr) {
				JSONObject obj = (JSONObject)o;
				ReplyConfig config = new ReplyConfig(obj.get("match").toString(),obj.get("replyType").toString(),obj.get("replyContent").toString());
				configs.add(config);
			}
			
			configStr = getConfigString(filePath2);
			arr = new JSONArray(configStr);
			for (Object o : arr) {
				JSONObject obj = (JSONObject)o;
				ReplyConfig config = new ReplyConfig(obj.get("match").toString(),obj.get("replyType").toString(),obj.get("replyContent").toString());
				imageReplyConfigs.add(config);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		shell.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent e) {
				//保存文件
				try {
					JSONArray arr = new JSONArray();
					for(ReplyConfig config : configs) {
						JSONObject obj = new JSONObject(config);
						arr = arr.put(obj);
					}
					OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(filePath),"UTF-8");
					BufferedWriter wr = new BufferedWriter(ow);
					wr.write(arr.toString());
					wr.flush();
					wr.close();
					ow.close();
					
					arr = new JSONArray();
					for(ReplyConfig config : imageReplyConfigs) {
						JSONObject obj = new JSONObject(config);
						arr = arr.put(obj);
					}
					ow = new OutputStreamWriter(new FileOutputStream(filePath2),"UTF-8");
					wr = new BufferedWriter(ow);
					wr.write(arr.toString());
					wr.flush();
					wr.close();
					ow.close();
					
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}});
	}
	
	private String getConfigString(String filePath) throws IOException {
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");  
		BufferedReader b = new BufferedReader(isr);  
		String line = b.readLine();
		StringBuffer buffer = new StringBuffer();
		while(line != null) {
			buffer.append(line);
			line = b.readLine();
		}
		b.close();
		isr.close();
		return buffer.toString();
	}
	public static String reply(String msg,String sender) throws Exception {
		for (ReplyConfig config : configs) {
			if (config.matches(msg)) {
				return config.getReply(msg,sender);
			}
		}
		return null;
	}
	
	public static String replyImage(String msg,String sender) throws Exception {
		for (ReplyConfig config : imageReplyConfigs) {
			if (config.matches(msg)) {
				return config.getReply(msg,sender);
			}
		}
		return null;
	}
	
	public void show() {
		
		GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        shell.setLayout(layout);
        shell.setSize(850,600);

        System.setProperty("org.eclipse.swt.browser.XULRunnerPath", System.getProperty("user.dir") + "\\xulrunner");

        browser = new Browser(shell,SWT.BORDER|SWT.MOZILLA);
        browser.setJavascriptEnabled(true);
        browser.setUrl("https://wx.qq.com");

        GridData gd3 = new GridData(GridData.FILL_BOTH);
        browser.setLayoutData(gd3);
        @SuppressWarnings("unused")
		BrowserFunction function = new GetReplyFunction(browser,"getReply");
        @SuppressWarnings("unused")
		BrowserFunction function2 = new GetImageReplyFunction(browser,"getImageReply");
        
        Group group = new Group(shell,SWT.PUSH);
        
        GridData gd4 = new GridData(SWT.FILL,SWT.FILL,false,false,1,2);
        gd4.widthHint = 200;
        group.setLayoutData(gd4);
        
        FormLayout f = new FormLayout();
        f.marginLeft = 10;
        f.marginTop = 10;
        
        group.setLayout(f);
        
        
        Button btn_autoResp = new Button(group,SWT.CHECK);
        btn_autoResp.setSelection(false);
        btn_autoResp.setText("自动回复");
        btn_autoResp.addSelectionListener(new SelectionAdapter(){
        	public void widgetSelected(SelectionEvent event) {
        		Button btn = (Button) event.widget;
        		if (btn.getSelection()) {
        			if (firstClick) {
        			browser.execute(script);
//            			browser.execute("var media_id = \"@crypt_6f9ab676_cacafee15247efbdc9c4742688b1f7bcb50eeb1bfa4c44007456ede0e5bbbca015f814c3b698490c87fe000a6eabbeb3712f8b99447d000cc2460ceea7c7460573a51c8647bf87d7a5b6103c8b667cad711a60975b54ff39a7b0de08a80dd955d6f6e0d9650ec276a72b4238149b384812fcf21d134f9bdaf316e51b6aed6fbf4a63bc91fc9c226a400a455d115cdb588fd7c70559e8cb3b425d468bdbcd4c32e7140f666c25aad6896a28d3488c380d5ac5c6e52dd31f1e21b94b67751c82e93a4567c600eda12bbbaf0e0d6d4d10c95eb8a53ce9888f89cba2c4743ae9cff6917edc1cab5c6d70f459298ad6f6b0de5de38e77fb16ece93e410f7a132371341ae21be5f9970d25f5bb33619f1cd154c30fffb7a3d83349a1440a821665c81e998df3d95161e65939c760d3567530204dcd01016455ea81fb7ced7b87785d7cc4745bf8821d5a448941616585abc720\";");
//            			browser.execute("var sender = \"filehelper\"");
//            			System.out.println(browser.evaluate("return sendImageMessage(media_id,sender)"));
        				firstClick = false;
        			} else {
        				browser.execute("enable_boot = true");
        			}
        		} else {
        			browser.execute("enable_boot = false");
        		}
        	}
        });
        
        browser.addStatusTextListener(new StatusTextListener() {

			@Override
			public void changed(StatusTextEvent event) {
				System.out.println(event.text);
				if (event.text.contains("Connected to webpush.wx.qq.com") && btn_autoResp.getSelection()) {
					System.out.println("重新执行回复程序");
					browser.execute(script);
				} 
			}
        });
        
        Button btn_roomChat = new Button(group,SWT.CHECK);
        btn_roomChat.setSelection(false);
        btn_roomChat.setText("开启群聊");
        FormData d1 = new FormData();
        d1.left = new FormAttachment(btn_autoResp,10);
        btn_roomChat.setLayoutData(d1);
        btn_roomChat.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.widget;
				if (btn.getSelection()) {
					ROOMCHAT_ENABLED = true;
				} else {
					ROOMCHAT_ENABLED = false;
				}
			}
        });
        
        Label l2 = new Label(group,SWT.NONE);
        l2.setText("规则列表");
        FormData data2 = new FormData();
        data2.top = new FormAttachment(btn_autoResp, 10);
        l2.setLayoutData(data2);
        
        Button btn_addRule = new Button(group,SWT.FLAT);
        btn_addRule.setText("增加规则");
        FormData d3 = new FormData();
        d3.left = new FormAttachment(l2,40);
        d3.top = new FormAttachment(btn_autoResp,5);
        btn_addRule.setLayoutData(d3);
        btn_addRule.addMouseListener(new MouseAdapter(){
        	public void mouseUp(MouseEvent e){
        		Popup dialog = new Popup(shell);
        		ReplyConfig config = (ReplyConfig) dialog.open();
        		if (config != null) {
        			if (!configs.contains(config)) {
        				configs.add(config);
        			}
        		}
        		refreshConfigs();
        	}
        });
		
        tb_rules = new Table(group,SWT.MULTI | SWT.FULL_SELECTION|SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
        FormData d4 = new FormData();
        d4.width = 160;
        d4.height = 150;
        d4.top = new FormAttachment(l2,20);
        tb_rules.setLayoutData(d4);
        tb_rules.setLinesVisible(true);
        tb_rules.setHeaderVisible(true);
        
        TableColumn tc1 = new TableColumn(tb_rules, SWT.NONE);
        tc1.setText("关键字");
        tc1.pack();
        
        TableColumn tc2 = new TableColumn(tb_rules, SWT.LEFT);
        tc2.setText(" 回复类型 ");
        tc2.pack();
        
        //final String[][] cellValues = {{"投票","文本回复"},{"验证码","程序回复"},{"活动","文本回复"}};
        
        tb_rules.addListener(SWT.SetData, new Listener(){
            public void handleEvent(Event event) {
                TableItem item = (TableItem)event.item;
                int index = event.index;
                item.setText(new String[]{configs.get(index).getMatch(),configs.get(index).getReplyType()});
            }
        });
        tb_rules.setItemCount(configs.size());//TODO:
        
        tb_rules2 = new Table(group,SWT.MULTI | SWT.FULL_SELECTION|SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
        FormData d5 = new FormData();
        d5.width = 160;
        d5.height = 150;
        d5.top = new FormAttachment(tb_rules,20);
        tb_rules2.setLayoutData(d5);
        tb_rules2.setLinesVisible(true);
        tb_rules2.setHeaderVisible(true);
        
        TableColumn tc4 = new TableColumn(tb_rules2, SWT.NONE);
        tc4.setText("关键字");
        tc4.pack();
        
        TableColumn tc5 = new TableColumn(tb_rules2, SWT.LEFT);
        tc5.setText(" 回复类型 ");
        tc5.pack();
        
        //final String[][] cellValues = {{"投票","文本回复"},{"验证码","程序回复"},{"活动","文本回复"}};
        
        tb_rules2.addListener(SWT.SetData, new Listener(){
            public void handleEvent(Event event) {
                TableItem item = (TableItem)event.item;
                int index = event.index;
                item.setText(new String[]{imageReplyConfigs.get(index).getMatch(),imageReplyConfigs.get(index).getReplyType()});
            }
        });
        tb_rules2.setItemCount(imageReplyConfigs.size());//TODO:
        
        Button btn_imagePop = new Button(group,SWT.PUSH);
        FormData d7 = new FormData();
        d7.top = new FormAttachment(tb_rules2,20);
        btn_imagePop.setLayoutData(d7);
        btn_imagePop.setText("添加图片回复");
        btn_imagePop.addMouseListener(new MouseAdapter(){
        	public void mouseUp(MouseEvent e){
        		ImagePopUp dialog = new ImagePopUp(shell);
        		dialog.setBrowser(browser);
        		ReplyConfig config = (ReplyConfig) dialog.open();
        		if (config != null) {
        			if (!imageReplyConfigs.contains(config)) {
        				imageReplyConfigs.add(config);
        			}
        		}
        		refreshImageConfigs();
        	}
        });
        
        Button btn_batchEdit = new Button(group,SWT.PUSH);
        FormData d8 = new FormData();
        d8.top = new FormAttachment(btn_imagePop,10);
        btn_batchEdit.setLayoutData(d8);
        btn_batchEdit.setText("更新全部");
        
        btn_batchEdit.addMouseListener(new MouseAdapter() {
        	public void mouseUp(MouseEvent e){
        		TextDialog dialog = new TextDialog(shell);
        		String obj = (String) dialog.open();
        		if (obj != null) {
        			for (ReplyConfig config : imageReplyConfigs) {
        				config.setReplyContent(obj);
        			}
        		}
        	}
        });
        
        createMenu();
        createMenu2();
		shell.open ();
		while (!shell.isDisposed ()) { 
		     if (!display.readAndDispatch ()) display.sleep (); 
		  } 
		display.dispose();
	}
	
	// 创建上下文菜单  
    private void createMenu()  
    {  
        // 创建弹出式菜单  
        menu = new Menu(shell, SWT.POP_UP);  
        // 设置该菜单为表格菜单  
        tb_rules.setMenu(menu);  
        // 创建删除菜单项  
        MenuItem del = new MenuItem(menu, SWT.PUSH);  
        del.setText("删除");   
        // 为删除菜单注册事件，当单击时，删除所选择的行  
        del.addListener(SWT.Selection, new Listener()  
        {  
            public void handleEvent(Event event)  
            {  
            	int index = tb_rules.getSelectionIndex();
            	configs.remove(index);
                // 此处需添加删除绑定Control的代码  
            	tb_rules.remove(tb_rules.getSelectionIndices());  
            	
            }  
        });  
        // 创建查看菜单项  
        MenuItem view = new MenuItem(menu, SWT.PUSH);  
        view.setText("查看");  
        // 为查看菜单项注册事件，当单击时打印出所选的姓名  
        view.addListener(SWT.Selection, new Listener()  
        {  
            public void handleEvent(Event event)  
            {  
            	int index = tb_rules.getSelectionIndex();
            	ReplyConfig config = configs.get(index);
            	Popup dialog = new Popup(shell);
            	dialog.setConfig(config);
        		dialog.open();
        		refreshConfigs();
            }  
        });
        
        MenuItem item = new MenuItem(menu,SWT.PUSH);
        item.setText("复制");
        item.addListener(SWT.Selection, new Listener()  
        {  
            public void handleEvent(Event event)  
            {  
            	int index = tb_rules.getSelectionIndex();
            	ReplyConfig config = configs.get(index);
            	ReplyConfig newConfig = new ReplyConfig(config.getMatch(),config.getReplyType(),config.getReplyContent());
            	configs.add(newConfig);
        		refreshConfigs();
            }  
        });
    }
	
 // 创建上下文菜单  
    private void createMenu2()  
    {  
        // 创建弹出式菜单  
        Menu menu2 = new Menu(shell, SWT.POP_UP);  
        // 设置该菜单为表格菜单  
        tb_rules2.setMenu(menu2);  
        // 创建删除菜单项  
        MenuItem del = new MenuItem(menu2, SWT.PUSH);  
        del.setText("删除");   
        // 为删除菜单注册事件，当单击时，删除所选择的行  
        del.addListener(SWT.Selection, new Listener()  
        {  
            public void handleEvent(Event event)  
            {  
            	int index = tb_rules2.getSelectionIndex();
            	imageReplyConfigs.remove(index);
                // 此处需添加删除绑定Control的代码  
            	tb_rules2.remove(tb_rules2.getSelectionIndices());  
            	
            }  
        });  
        // 创建查看菜单项  
        MenuItem view = new MenuItem(menu2, SWT.PUSH);  
        view.setText("查看");  
        // 为查看菜单项注册事件，当单击时打印出所选的姓名  
        view.addListener(SWT.Selection, new Listener()  
        {  
            public void handleEvent(Event event)  
            {  
            	int index = tb_rules2.getSelectionIndex();
            	ReplyConfig config = imageReplyConfigs.get(index);
            	ImagePopUp dialog = new ImagePopUp(shell);
            	dialog.setBrowser(browser);
            	dialog.setConfig(config);
        		dialog.open();
        		refreshImageConfigs();
            }  
        });
        MenuItem item = new MenuItem(menu2,SWT.PUSH);
        item.setText("复制");
        item.addListener(SWT.Selection, new Listener()  
        {  
            public void handleEvent(Event event)  
            {  
            	int index = tb_rules2.getSelectionIndex();
            	ReplyConfig config = imageReplyConfigs.get(index);
            	ReplyConfig newConfig = new ReplyConfig(config.getMatch(),config.getReplyType(),config.getReplyContent());
            	imageReplyConfigs.add(newConfig);
            	refreshImageConfigs();
            }  
        });
    }
    
    private void refreshConfigs() {
    	tb_rules.clearAll();
    	tb_rules.setItemCount(configs.size());
    }
    
    private void refreshImageConfigs() {
    	tb_rules2.clearAll();
    	tb_rules2.setItemCount(imageReplyConfigs.size());
    }
	public static void main(String[] args) {
		Application app = new Application();
		app.show();
	}
}
