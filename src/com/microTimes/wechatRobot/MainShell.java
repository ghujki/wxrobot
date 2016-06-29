package com.microTimes.wechatRobot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class MainShell extends Shell {
	private Table table;
	private Shell shell = this;
	private Browser browser;
	private int initialized = 0;
	private String script = " function findLast(obj,userName) {" +
            "     obj.forEach(function(e) {" +
            "         if (obj.UserName == userName) {" +
            "             return e;" +
            "         }" +
            "     });" +
            "     return null;" +
            " }; var $scope1 = angular.element('.chat_item').scope(); var cur_userName=$scope1.account.UserName;"
			+" $scope1.$watch('chatList',function(v1,v2){ "
            +"     var  i = 0; "
            +"     var e = v1[0]; "
            +"     var last = findLast (v2,e.UserName); "
            +"     var msg = e.MMDigest; "
            +"     if (msg != null && msg != '' && (last == null || msg != last.MMDigest)  && !e.isBlackContact() && !e.isSpContact() && e.UserName != cur_userName) { " //&& !e.isRoomContact()
            +"         $scope1.currentUserName = e.UserName; "
            +"         $scope1.itemClick(e.UserName); "
            +"         setTimeout(function(){ "
            +"             $scope1.$apply(); "
            +"             var $scope3 = angular.element(\"[ng-repeat='message in chatContent']:last\").scope(); "
            +"             var message = $scope3.message.MMActualContent,msgId= $scope3.message.MsgId,actualSender = $scope3.message.MMActualSender; "
            +"             if (cur_userName == actualSender) {return;} "
            +"             noteMsg(message,msgId,cur_userName,actualSender,e.isRoomContact()); "
            +"         },500); "
            +"     } "
            +"},true);";
	private Text text;;
	/**
	 * Create the shell.
	 * @param display
	 */
	public MainShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		Composite composite_left = new Composite(sashForm, SWT.BORDER);
		composite_left.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		browser = new Browser(composite_left, SWT.NONE | SWT.MOZILLA);
		browser.setUrl("https://wx.qq.com");
		browser.setJavascriptEnabled(true);
		NotifyMessageFunction function = new NotifyMessageFunction(browser,"noteMsg");
		MessageAPI.setBrowser(browser);
		
		browser.addStatusTextListener(new StatusTextListener() {

			@Override
			public void changed(StatusTextEvent event) {
				//System.out.println(event.text);
				if (event.text.equals("")) {
					if (initialized == 0) {initialized ++;} else if (initialized == 1) {
						System.out.println("执行自动程序");
						browser.execute(script);
						initialized = 2;
					}
				} 
			}
        });
		
		Composite composite_right = new Composite(sashForm, SWT.BORDER);
		composite_right.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(composite_right, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		
		ToolItem toolItem_2 = new ToolItem(toolBar, SWT.NONE);
		toolItem_2.setText("全选");
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getItems();  
				for (TableItem item : items) {
					if (item.getChecked()) {
						ReplyConfig config = (ReplyConfig) item.getData();
						RobotConfig.removeConfig(config);
						item.dispose();
					}
				}
			}
		});
		tltmNewItem.setText("删除选中");
		
		ToolItem toolItem_4 = new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem toolItem_5 = new ToolItem(toolBar, SWT.NONE);
		toolItem_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigDialog dialog  = new ConfigDialog(shell,SWT.DIALOG_TRIM,null);
				dialog.setBrowser(browser);
				ReplyConfig config = (ReplyConfig) dialog.open();
				if (config != null) {
					TableItem item = new TableItem(table,SWT.NONE);
					item.setData(config);
					item.setText(new String[]{config.getConfigName(),config.getKeywords(),config.getReplyText(),config.getReplyPicture()});
					RobotConfig.addConfig(config);
					item.setChecked(true);
				}
			}
		});
		toolItem_5.setText("新建");
		
		ToolItem toolItem_6 = new ToolItem(toolBar, SWT.NONE);
		toolItem_6.setText("编辑");
		
		ToolItem toolItem_7 = new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem toolItem_8 = new ToolItem(toolBar, SWT.NONE);
		toolItem_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					RobotConfig.save();
				} catch (IOException e1) {
					showMessage("保存文件出错");
					e1.printStackTrace();
				}
			}
		});
		toolItem_8.setText("保存");
		
		ToolItem toolItem_9 = new ToolItem(toolBar, SWT.NONE);
		toolItem_9.setText("载入");
		
		Composite composite_2 = new Composite(composite_right, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION |SWT.CHECK);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem item = table.getItem(new Point(e.x,e.y));
				if (item != null && item.getData() != null) {
					ReplyConfig config  = (ReplyConfig)item.getData();
					ConfigDialog dialog  = new ConfigDialog(shell,SWT.DIALOG_TRIM,config);
					dialog.setBrowser(browser);
					dialog.open();
					item.setText(new String[]{config.getConfigName(),config.getKeywords(),config.getReplyText(),config.getReplyPicture()});
					item.setChecked(true);
				}
			}
		});
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(65);
		tableColumn.setText("规则名称");
		
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("关键字");
		
		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(100);
		tableColumn_3.setText("回复文本");
		
		Menu menu = new Menu(table);
		table.setMenu(menu);
		
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("复制");
		
		MenuItem menuItem_1 = new MenuItem(menu, SWT.NONE);
		menuItem_1.setText("编辑");
		
		MenuItem menuItem_2 = new MenuItem(menu, SWT.NONE);
		menuItem_2.setText("删除");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("回复图片");
		
		Composite composite_1 = new Composite(composite_right, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.SOUTH);
		composite_1.setLayout(new FormLayout());
		
		final Button button = new Button(composite_1, SWT.CHECK);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotConfig.setAutoReply(button.getSelection());
			}
		});
		FormData fd_button = new FormData();
		fd_button.bottom = new FormAttachment(0, 50);
		fd_button.top = new FormAttachment(0, 10);
		fd_button.left = new FormAttachment(0, 10);
		button.setLayoutData(fd_button);
		button.setText("启用自动回复");
		
		button.setSelection(RobotConfig.isAutoReply());
		
		final Spinner spinner = new Spinner(composite_1, SWT.BORDER);
		
		spinner.setSelection(RobotConfig.getDelay());
		spinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				RobotConfig.setDelay(spinner.getSelection());
			}
		});
		FormData fd_spinner = new FormData();
		fd_spinner.top = new FormAttachment(button, 9, SWT.TOP);
		fd_spinner.left = new FormAttachment(button, 34);
		spinner.setLayoutData(fd_spinner);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.bottom = new FormAttachment(spinner, 0, SWT.BOTTOM);
		fd_lblNewLabel.left = new FormAttachment(spinner, 6);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("秒后回复");
		
		text = new Text(composite_1, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		fd_text.top = new FormAttachment(button, 9, SWT.TOP);
		text.setLayoutData(fd_text);
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Object o = browser.evaluate(text.getText(), true);
					System.out.println(o);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		fd_text.right = new FormAttachment(button_1, -6);
		FormData fd_button_1 = new FormData();
		fd_button_1.top = new FormAttachment(button, 7, SWT.TOP);
		fd_button_1.left = new FormAttachment(0, 426);
		button_1.setLayoutData(fd_button_1);
		button_1.setText("执行");
		
		toolItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					RobotConfig.init();
					table.removeAll();
					button.setSelection(RobotConfig.isAutoReply());
					for (ReplyConfig config : RobotConfig.getConfigs()) {
						TableItem item = new TableItem(table, SWT.None);
						item.setText(new String[]{config.getConfigName(),config.getKeywords(),config.getReplyText(),config.getReplyPicture()});
						item.setData(config);
					}
				} catch (IOException e1) {
					showMessage("加载配置文件出错");
					e1.printStackTrace();
				}
			}
		});
		
		for (ReplyConfig config : RobotConfig.getConfigs()) {
			TableItem item  = new TableItem(table,SWT.None);
			item.setData(config);
			item.setText(new String[] {config.getConfigName(),config.getKeywords(),config.getReplyText(),config.getReplyPicture()});
		}
		sashForm.setWeights(new int[] {349, 458});
		createContents();
	}

	private void showMessage (String message) {
		MessageBox box = new MessageBox(this);
		box.setMessage(message);
		box.open();
	}
	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("微信个人号机器人");
		setSize(816, 484);
		this.setImage(SWTResourceManager.getImage("resources\\2.png"));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void dispose() {
		try {
			RobotConfig.save();
			ThreadPool.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.dispose();
	}
	
	
	
}
