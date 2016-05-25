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
    
    /**
     * 是否支持群聊回复
     * */
    public static boolean ROOMCHAT_ENABLED = false;
    private static List<ReplyConfig> configs = new ArrayList<> ();
    private static String filePath = "config/reply.json";
    private Logger logger = LogManager.getLogger(Application.class);
    
    //
    private boolean firstClick = true;
    
	public Application() {
		display = new Display ();
		shell = new Shell(display);
		try {
			String configStr = getConfigString();
			JSONArray arr = new JSONArray(configStr);
			for (Object o : arr) {
				JSONObject obj = (JSONObject)o;
				ReplyConfig config = new ReplyConfig(obj.get("match").toString(),obj.get("replyType").toString(),obj.get("replyContent").toString());
				configs.add(config);
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
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}});
	}
	
	private String getConfigString() throws IOException {
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
	public void show() {
		
		GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        shell.setLayout(layout);
        shell.setSize(800,600);


        browser = new Browser(shell,SWT.BORDER);
        Browser.clearSessions();
        browser.setUrl("https://wx.qq.com");

        GridData gd3 = new GridData(GridData.FILL_BOTH);
        browser.setLayoutData(gd3);
        @SuppressWarnings("unused")
		BrowserFunction function = new GetReplyFunction(browser,"getReply");
        
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
        			browser.execute(" function findLast(obj,userName) {" +
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
                            + "var enable_boot = true; "
                            +" $scope1.$watch('chatList',function(v1,v2){ "
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
                            +"             } "
                            +"         },500); "
                            +"     } "
                            +"},true);");
        			firstClick = false;
        			} else {
        				browser.execute("enable_boot = true");
        			}
        		} else {
        			browser.execute("enable_boot = false");
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
        d4.height = 400;
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
        
        
        createMenu();
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
    }
	
    private void refreshConfigs() {
    	tb_rules.clearAll();
    	tb_rules.setItemCount(configs.size());
    }
	public static void main(String[] args) {
		Application app = new Application();
		app.show();
	}
}
