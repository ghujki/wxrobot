package com.wsg.robot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
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

public class Application {
	private Display display;
    private Shell shell ;
    private Browser browser;
    private Table tb_rules;
    private Menu menu;
    
	public Application() {
		display = new Display ();
		shell = new Shell(display);
	}
	
	public void show() {
		
		GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        shell.setLayout(layout);
        shell.setSize(800,600);


        browser = new Browser(shell,SWT.BORDER);
        browser.setUrl("https://wx.qq.com");

        GridData gd3 = new GridData(GridData.FILL_BOTH);
        browser.setLayoutData(gd3);
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
        			browser.execute("var $scope1 = angular.element('.chat_item').scope();"
        					+ " var $scope2 = angular.element('[ng-controller=chatSenderController]').scope(); "
        					+ " var cur_userName = $scope1.account.UserName;"
        					+ " $scope1.$watch('chatList',function(v1,v2){"
        					+ "  v1.forEach(function(e){"
        					+ "  	var msg = e.MMDigest;"
        					+ " 	if (msg != null && msg != '' && e.UserName != cur_userName) {"
        					+ "  			var reply = getReply(msg,e.UserName,e.MMDigestTime,e.isInChatroom());"
        					+ "             if(reply != null) {     "
        					+ "  			$scope2.currentUserName = e.UserName;"
        					+ "  			$scope1.itemClick(e.UserName);"
        					+ "  			$scope2.editAreaCtn = reply;"
        					+ "			  	setTimeout(function(){  "
        					+ "			  		$scope2.$apply();"
        					+ "			  		$scope2.sendTextMessage();"
        					+ "			  		},0);}"
        					+ "  	}});},true);");
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
        		dialog.open();
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
        
        final String[][] cellValues = {{"投票","文本回复"},{"验证码","程序回复"},{"活动","文本回复"}};
        
        tb_rules.addListener(SWT.SetData, new Listener(){
            public void handleEvent(Event event) {
                TableItem item = (TableItem)event.item;
                int index = event.index;
                item.setText(cellValues [index]);
            }
        });
        tb_rules.setItemCount(3);//TODO:
        
        
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
                TableItem[] items = tb_rules.getSelection();  
                for (int i = 0; i < items.length; i++)  
                    System.out.print(items[i].getText());  
            }  
        });  
    }
	
	public static void main(String[] args) {
		Application app = new Application();
		app.show();
	}
}
