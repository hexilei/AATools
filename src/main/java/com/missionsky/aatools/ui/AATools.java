package com.missionsky.aatools.ui;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TrayItem;
import com.missionsky.aatools.dbcenter.AddDBConnectionWindow;
import com.missionsky.aatools.dbcenter.SearchResultWindow;
import com.missionsky.aatools.dbcenter.ServerConfigUtil;
import com.missionsky.aatools.dbproxy.AADbSwitcher;
import com.missionsky.aatools.dbproxy.ConnectionUtil;
import com.missionsky.aatools.dbproxy.DBProxy;
import com.missionsky.aatools.dbsheet.AAObject;
import com.missionsky.aatools.dbsheet.DBSheetUtil;
import com.missionsky.aatools.dbsheet.DescTable;
import com.missionsky.aatools.dbsheet.UsingDB;
import com.missionsky.aatools.fidmanage.FidManageWindow;
import com.missionsky.aatools.loganalyst.DBALogAnalyst;
import com.missionsky.aatools.loganalyst.LogUtils;
import com.missionsky.aatools.loganalyst.ServerLogAnalyst;
import com.missionsky.aatools.loganalyst.WebLogAnalyst;
import com.missionsky.aatools.logplus.LogsUtil;
import com.missionsky.aatools.qatracer.QATracer;
import com.missionsky.aatools.swtdesigner.SWTResourceManager;
import com.missionsky.aatools.util.MessageUtil;
import com.missionsky.aatools.v360text.V360TextGenerator;


/**
 * <pre>
 * 
 *  Accela Automation
 *  File: DBSwitcher.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2009-2014
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall DBSwitcher.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2009-12-28			john.huang				Initial.
 *  
 * </pre>
 */
public class AATools
{
	public final static boolean IS_QA_VERSION = false;
	public ArrayList<Connection> connections = new ArrayList<Connection>(); 
	public Connection currentConnection;
	
	protected Shell shlDbSwitcher;
	private Table tblConnections;
	
	private final static String FILE_CONNECTION = "Connections.properties";
	
	private final static String FILE_DBSHEET = "dbsheet.txt";
	
	private final static String FILE_CONNECTING = "connection.properties";
	
	/**
	 * @wbp.nonvisual location=346,1
	 */
	private final TrayItem trtmDbSwitcher = new TrayItem(Display.getDefault().getSystemTray(), SWT.NONE);
	private Text txtKeywords;
	private Table tblResults;
	private Combo ddType;
	
	private static boolean QATracerStarted = false;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			AATools window = new AATools();
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open()
	{
		String name = "Unknown";
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			name = addr.getHostName();
			name = name.replace('-', '.');
		}
		catch (UnknownHostException e)
		{
		}
		loadConnections();
		
		Display display = Display.getDefault();
		createContents(name);
		
		fillConnections();
		
		shlDbSwitcher.open();
		shlDbSwitcher.layout();
		
		// check ITS cases
//		Date d = new Date();
//		d.setHours(17);
//		d.setMinutes(35);
//		Timer timer = new Timer();
//		timer.schedule(new CheckTask(display, name), d, 24 * 3600L * 1000L);
		//--end
		
		if (currentConnection != null) {
			if (ConnectionUtil.testConnection(currentConnection)) {
				DBProxy.setCurrentConnection(currentConnection);
				if (DBProxy.start()) {
					showMessage("Connect to '" + currentConnection.getName() + "' successfully!");
				}
				else {
					showErrorMessage("Connect to '" + currentConnection.getName() + "' failed: " + DBProxy.getLastErrMSG());
				}
			}
			else {
				showErrorMessage("Connect to '" + currentConnection.getName() + "' failed: " + ConnectionUtil.getLastErrorMessage());
			}
		}
		else {
			showErrorMessage("Please set a active connection!");
		}
		
		while (!shlDbSwitcher.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		
		trtmDbSwitcher.dispose();
	}
	
	/**
	 * load connection.
	 * @return
	 */
	private File loadPropFile() {
		File file = FileUtil.loadConnectionFile(FILE_CONNECTION,false);
		return file;
	}
	
	private File loadLocalPropFile() {
		File file = FileUtil.loadConnectionFile(FILE_CONNECTING,true);
		return file;
	}
	
	/**
	 * load DB sheet.
	 * @return
	 */
	private File loadDBSheetFile(){
		File file = FileUtil.loadConnectionFile(FILE_DBSHEET, false);
		return file;
	}
	
	private String getSelectedLine() {
		File file = loadLocalPropFile();
		String selectedLine = ""; 
		try
		{
			BufferedReader reader =  new BufferedReader(new FileReader(file));
			selectedLine = reader.readLine();
			return selectedLine;
		}
		catch (Exception e)
		{
			showErrorMessage("Load connections.properties failed: " + e.getMessage());
			return null;
		}
	}
	
	private void saveSelectedLine() {
		StringBuilder content = new StringBuilder();
		if (currentConnection != null) {
			content.append("SELECTED: " + currentConnection.getName()).append("\r\n");
		}
		else {
			content.append("SELECTED:  ").append("\r\n");
		}
		
		File file = loadLocalPropFile();
		try
		{
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.toString().getBytes());
			out.flush();
			out.close();
			fillConnections();
		}
		catch (Exception e)
		{
			showErrorMessage("Save connections failed: " + e.getMessage());
		}
	}
	
	private ArrayList<String> readLinesFromFile(File file) {
		ArrayList<String> lines = new ArrayList<String>();
		try
		{
			BufferedReader reader =  new BufferedReader(new FileReader(file));
			while (true) 
			{
				String s = reader.readLine();
				if (s == null) break;
				if (s.length() < 20) continue; // ignore the line content < 20
				lines.add(s);
			}
		}
		catch (Exception e)
		{
			showErrorMessage("Load " + file.getName() + " failed: " + e.getMessage());
		}
		
		return lines;
	}
	
	private void loadConnections() {
		connections.clear();
		File file = loadPropFile();
		String selectedLine = getSelectedLine(); 
		ArrayList<String> lines = readLinesFromFile(file);
		
		// parse connections
		for (String s : lines)
		{
			try
			{
				int pos = s.indexOf(":");
				String name = s.substring(0, pos);
				
				String str = s.substring(pos + 1);
				String strUpper = str.toUpperCase();
				
				pos = strUpper.indexOf("TYPE=");
				int end = strUpper.indexOf(";", pos);
				String type = str.substring(pos + "TYPE=".length(), end);
				
				pos = strUpper.indexOf("HOST=");
				end = strUpper.indexOf(";", pos);
				String host = str.substring(pos + "HOST=".length(), end);
				
				pos = strUpper.indexOf("PORT=");
				end = strUpper.indexOf(";", pos);
				String port = str.substring(pos + "PORT=".length(), end);
				
				pos = strUpper.indexOf("SERVICE=");
				end = strUpper.indexOf(";", pos);
				String service = str.substring(pos + "SERVICE=".length(), end);
				
				pos = strUpper.indexOf("USER=");
				end = strUpper.indexOf(";", pos);
				String user = str.substring(pos + "USER=".length(), end);
				
				pos = strUpper.indexOf("PASS=");
				end = strUpper.indexOf(";", pos);
				String pass = str.substring(pos + "PASS=".length(), end);
				
				pos = strUpper.indexOf("AAVERSION=");
				end = strUpper.indexOf(";", pos);
				String AAVersion = str.substring(pos + "AAVERSION=".length(), end);
				
				pos = strUpper.indexOf("DATE=");
				end = strUpper.indexOf(";", pos);
				String date = str.substring(pos + "DATE=".length(), end);
				
				pos = strUpper.indexOf("COMMENT=");
				end = strUpper.indexOf(";", pos);
				String comment = str.substring(pos + "COMMENT=".length(), end);
				
				Connection conn = new Connection();
				conn.setName(name.trim());
				conn.setType(type.trim());
				conn.setHost(host.trim());
				conn.setPort(port.trim());
				conn.setService(service.trim());
				conn.setUser(user.trim());
				conn.setPass(pass.trim());
				
				conn.setAAVersion(AAVersion);
				conn.setDate(date);
				conn.setComment(comment);
				
				connections.add(conn);
			}
			catch (Exception e)
			{
				showErrorMessage("Parse connections.properties error: " + s + " : " + e.getMessage());
			}
		}
		
		// check active connection
		if (selectedLine != null && selectedLine.length() > 0) {
			connections.get(0).setSelected(true);
			int pos = selectedLine.indexOf("SELECTED: ");
			String selectedName = selectedLine.substring(pos + "SELECTED: ".length()).trim(); 
			
			for (Connection conn : connections)
			{
				if (conn.getName().equals(selectedName)) {
					conn.setSelected(true);
					currentConnection = conn;
					return;
				}
			}
		}
		
		if(!connections.isEmpty())
		{
			connections.get(0).setSelected(true);
			currentConnection = connections.get(0);
		}
	}
	
	private void fillConnections() {
		Collections.sort(connections);
		tblConnections.removeAll();
		for (Connection conn : connections)
		{
			if (conn.getType().equals("ORACLE")) {
				TableItem tableItem = new TableItem(tblConnections, SWT.NONE);
				tableItem.setText(new String[] {"", conn.getName(), conn.getAAVersion(), conn.getType(), conn.getHost(), conn.getPort(), conn.getService(), conn.getUser(), conn.getPass(), conn.getDate(), conn.getComment()});
				tableItem.setData(conn);
				if (conn.isSelected()) {
					tableItem.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				}
			}
		}
		
		for (Connection conn : connections)
		{
			if (conn.getType().equals("MSSQL")) {
				TableItem tableItem = new TableItem(tblConnections, SWT.NONE);
				tableItem.setText(new String[] {"", conn.getName(), conn.getAAVersion(), conn.getType(), conn.getHost(), conn.getPort(), conn.getService(), conn.getUser(), conn.getPass(), conn.getDate(), conn.getComment()});
				tableItem.setData(conn);
				tableItem.setBackground(SWTResourceManager.getColor(224, 255, 255));
				if (conn.isSelected()) {
					tableItem.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				}
			}
		}
	}
	
	private void switchDB(boolean withAA) {
		int selectIndex = tblConnections.getSelectionIndex();
		if (selectIndex >= 0) {
			TableItem tableItem = tblConnections.getItem(selectIndex);
			Connection selectedConnection = (Connection) tableItem.getData();
			if (ConnectionUtil.testConnection(selectedConnection)) {
				if (IS_QA_VERSION) {
					LogDialog dlg = new LogDialog(shlDbSwitcher, SWT.DEFAULT);
					dlg.setConnectionName(selectedConnection.getName());
					Boolean rs = (Boolean) dlg.open();
					if (!rs) {
						showMessage("Switch cancled");
						return;
					}
				}
				if (currentConnection != null) {
					currentConnection.setSelected(false);
				}
				currentConnection = (Connection) tableItem.getData();
				currentConnection.setSelected(true);
				fillConnections();
				saveSelectedLine();
				
				DBProxy.stop();
				DBProxy.setCurrentConnection(currentConnection);
				
				if ("7.2.0".equals(currentConnection.getAAVersion()) || "7.3.0".equals(currentConnection.getAAVersion())) {
					if (ServerConfigUtil.replaceServerConfig(currentConnection)) {
						showMessage("Switch to '" + currentConnection.getName() + "' successfully!\r\nNotice: Please login V360 and clear the cache at first!");
						if (withAA) AADbSwitcher.runChangeToBat(currentConnection.getAAVersion(), currentConnection.getType());
					}
					else {
						showErrorMessage("Switch to '" + selectedConnection.getName() + "' failed: " + ConnectionUtil.getLastErrorMessage());
					}
				}
				else {
					if (DBProxy.start()) {
						showMessage("Switch to '" + currentConnection.getName() + "' successfully!\r\nNotice: Please login V360 and clear the cache at first!");
						if (withAA) AADbSwitcher.runChangeToBat(currentConnection.getAAVersion(), currentConnection.getType());
					}
					else {
						showErrorMessage("Switch to '" + selectedConnection.getName() + "' failed: " + ConnectionUtil.getLastErrorMessage());
					}
				}
			}
			else {
				showErrorMessage("Switch to '" + selectedConnection.getName() + "' failed: " + ConnectionUtil.getLastErrorMessage());
			}
		}
	}
	
	private void addDB() {
		AddDBConnectionWindow win = new AddDBConnectionWindow(shlDbSwitcher.getDisplay(), null);
		win.setAATools(this);
		win.open();
	}
	
	private void removeSelectedConnection() {
		TableItem[] items = tblConnections.getSelection();
		if (items == null || items.length == 0) {
			showErrorMessage("No connection selected!");
			return;
		}
		
		if (!MessageDialog.openConfirm(shlDbSwitcher, "Confirm", "Are you sure want to remove the selected connections?")) {
			return;
		}
		
		for (TableItem item : items)
		{
			Connection conn = (Connection) item.getData();
			connections.remove(conn);
		}
		
		saveConnections();
		fillConnections();
	}
	
	public boolean saveConnections() {
		StringBuilder content = new StringBuilder();
		
		for (Connection conn : connections)
		{
			String s = conn.getName() + ": " +
					"TYPE=" + conn.getType() +
					"; HOST=" + conn.getHost() +
					"; PORT=" +conn.getPort() +
					"; SERVICE=" + conn.getService() +
					"; USER=" + conn.getUser() +
					"; PASS=" + conn.getPass()  +
					"; AAVERSION=" + conn.getAAVersion()  +
					"; DATE=" + conn.getDate() +
					"; COMMENT=" + conn.getComment() + ";";
			content.append(s).append("\r\n");
		}
		
		File file = loadPropFile();
		try
		{
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.toString().getBytes());
			out.flush();
			out.close();
			fillConnections();
		}
		catch (Exception e)
		{
			showErrorMessage("Save connections failed: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private void searchDB() {
		String keyword = txtKeyword.getText().toLowerCase();
		if (keyword.length() == 0) {
			return;
		}
		int found1 = 0;
		String rs = "";
		
		// search in connections
		TableItem[] items = tblConnections.getItems();
		for (TableItem item : items) {
			if (item.getText(1).toLowerCase().indexOf(keyword) >= 0 || item.getText(10).toLowerCase().indexOf(keyword) >= 0) {
				if (item.getForeground() != SWTResourceManager.getColor(SWT.COLOR_BLUE)) {
					item.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
					String text = "";
					for (int i = 0; i < 11; i++)
					{
						text += item.getText(i) + " | ";
					}
					rs += text + "\r\n";
					found1++;
				}
			}
			else {
				if (item.getForeground() != SWTResourceManager.getColor(SWT.COLOR_BLUE)) {
					item.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
				}
			}
		}
		
		if (found1 > 0) {
			rs = found1 + " DB connection(s) found in current connections table (Marked as blue):\r\n---\r\n" + rs + "---\r\n\r\n";
		}
		
		// find in db sheet
		int found2 = 0;
		String rs2 = "";
		File file = loadDBSheetFile();
		ArrayList<String> lines = readLinesFromFile(file);
		for (String line : lines) {
			if (line.toLowerCase().indexOf(keyword) >= 0) {
				rs2 += line + "\r\n";
				found2++;
			}
		}
		
		if (found2 > 0) {
			rs2 = found2 + " DB connection(s) found in DB sheet: \r\n---\r\n" + rs2 + "---";
			rs += rs2;
		}
		
		//find in dbmread
		int found3 = 0;
		String rs3 = "";
		List<UsingDB> dbs = DBSheetUtil.getAvaliableDBs(keyword);
		for(UsingDB db : dbs)
		{
			rs3 += db.toString();
			found3++;
		}
		if(found3 > 0)
		{
			rs3 = found3 + "DB connection(S) found in DBMREAD: \r\n---\r\n" + rs3 + "---";
			rs += rs3;
		}
		
		SearchResultWindow win = new SearchResultWindow(shlDbSwitcher.getDisplay());
		win.setResult(rs);
		win.open();
	}
	
	private void refreshConnections() {
		loadConnections();
		fillConnections();
	}
	
	private void queryAgencies() {
		int selectIndex = tblConnections.getSelectionIndex();
		if (selectIndex >= 0) {
			TableItem tableItem = tblConnections.getItem(selectIndex);
			Connection conn = (Connection) tableItem.getData();
			String[] agencies = ConnectionUtil.queryAgencies(conn);
			if (agencies != null) {
				String output = "";
				for (int i = 0; i < agencies.length; i++)
				{
					output += agencies[i] + "\r\n";
				}
				showMessage("Agency List", output);
			}
			else {
				showErrorMessage("Query failed!" + ConnectionUtil.getLastErrorMessage());
			}
		}
		else {
			showErrorMessage("Please select one connection!");
		}
	}
	
	private void getProductVersion() {
		int selectIndex = tblConnections.getSelectionIndex();
		if (selectIndex >= 0) {
			TableItem tableItem = tblConnections.getItem(selectIndex);
			Connection conn = (Connection) tableItem.getData();
			String ver = ConnectionUtil.getProductVer(conn);
			showMessage("AA Product Version: " + ver);
		}
		else {
			showErrorMessage("Please select one connection!");
		}
	}
	
	private void resetPassword() {
		int selectIndex = tblConnections.getSelectionIndex();
		if (selectIndex >= 0) {
			TableItem tableItem = tblConnections.getItem(selectIndex);
			Connection conn = (Connection) tableItem.getData();
			if (!MessageDialog.openConfirm(shlDbSwitcher, "Reset password", "Are you sure want to reset all users password in DB '" + conn.getName() + "' to 'admin'?")) {
				return;
			}
			if (ConnectionUtil.resetPassword(conn)) {
				showMessage("All users' password reset to 'admin'.");
			}
			else {
				showErrorMessage("Reset password failed! " + ConnectionUtil.getLastErrorMessage());
			}
		}
		else {
			showErrorMessage("Please select one connection!");
		}
	}
	
	private void clearConsole() {
		int selectIndex = tblConnections.getSelectionIndex();
		if (selectIndex >= 0) {
			TableItem tableItem = tblConnections.getItem(selectIndex);
			Connection conn = (Connection) tableItem.getData();
			if (!MessageDialog.openConfirm(shlDbSwitcher, "Clear Console", "Are you sure want to clear the JCONSOLERECEIPT table in DB '" + conn.getName() + "'?")) {
				return;
			}
			if (ConnectionUtil.clearConsole(conn)) {
				showMessage("The JCONSOLERECEIPT table has been cleared.");
			}
			else {
				showErrorMessage("Clear JCONSOLERECEIPT table failed!");
			}
		}
		else {
			showErrorMessage("Please select one connection!");
		}
	}
	
	private void fidManage() {
		Connection conn = getCurrentConnection();
		if (conn != null) {
			FidManageWindow win = new FidManageWindow(shlDbSwitcher.getDisplay());
			win.setConnection(conn);
			win.open();
		}
		else {
			showErrorMessage("Please select one connection!");
		}
	}
	
	private Connection getCurrentConnection() {
		int selectIndex = tblConnections.getSelectionIndex();
		if (selectIndex >= 0) {
			TableItem tableItem = tblConnections.getItem(selectIndex);
			Connection conn = (Connection) tableItem.getData();
			return conn;
		}
		return null;
	}
	
	public void showErrorMessage(String message) 
	{
		MessageDialog.openInformation(
			shlDbSwitcher,
			"Error",
			message);
	}
	
	public void showMessage(String message) 
	{
		MessageDialog.openInformation(
			shlDbSwitcher,
			"Message",
			message);
	}
	
	public void showMessage(String title, String message) 
	{
		MessageDialog.openInformation(
			shlDbSwitcher,
			title,
			message);
	}
	
	private void showAboutMessage() 
	{
		MessageDialog.openInformation(
			shlDbSwitcher,
			"About AATools",
			"AA Tools V2.1\r\n\r\nJohn Huang 出品, 必属精品!");
	}
	
	private void search() {
		tblResults.removeAll();
		
		ArrayList<AAObject> results = DBSheetUtil.search(txtKeywords.getText(), ddType.getText());
		if (results == null) {
			showErrorMessage("Search failed: " + DBSheetUtil.getLastErr());
			return;
		}
		
		// fill results
		for (AAObject obj : results)
		{
			TableItem tableItem = new TableItem(tblResults, SWT.NONE);
			tableItem.setText(new String[] {obj.getName(), obj.getType(), obj.getDescription()});
			tableItem.setData(obj);
		}
	}

	Listener listenerSearch = new Listener()
	{
		public void handleEvent(Event e)
		{
			if (e.keyCode == 13)
			{
				search();
			}
		}
	};
	
	Listener listenerCopy = new Listener()
	{
		public void handleEvent(Event e)
		{
			if (e.stateMask == SWT.CTRL && e.keyCode == 'c')
			{
				if (((Table) e.widget).getSelection().length == 0) return;
				
				String text = ((Table) e.widget).getSelection()[0].getText();
				Clipboard clip = new Clipboard(Display.getDefault());
				clip.setContents(new Object[] {text}, new Transfer[] {TextTransfer.getInstance()});
			}
		}
	};
	
	Listener listenerConnCopy = new Listener()
	{
		public void handleEvent(Event e)
		{
			if (e.stateMask == SWT.CTRL && e.keyCode == 'c')
			{
				if (((Table) e.widget).getSelection().length == 0) return;
				
				TableItem item = ((Table) e.widget).getSelection()[0];
				String text = "";
				for (int i = 0; i < 11; i++)
				{
					text += item.getText(i) + " ";
				}
				Clipboard clip = new Clipboard(Display.getDefault());
				clip.setContents(new Object[] {text}, new Transfer[] {TextTransfer.getInstance()});
			}
		}
	};
	
	Listener listenerSelectAll = new Listener()
	{
		public void handleEvent(Event e)
		{
			if (e.stateMask == SWT.CTRL && e.keyCode == 'a')
			{
				((Text) e.widget).selectAll();
			}
		}
	};
	
	Listener searchListener = new Listener()
	{
		public void handleEvent(Event e)
		{
			if (e.keyCode == 13)
			{
				searchDB();
			}
		}
	};
	
	private Text txtTraceResults;
	private Text txtLogResults;
	private Text txtContent;
	private Text txtKeyword;
	private Text txtKey;
	private Text txtValue;
	private Text txtCategory;
	private Text txtDate;
	private Text txtRecName;
	private Text txtStatus;
	private Text txtJSP;
	private Text txtSelect;
	private Text txtIDU;
	private Text txtReq;
	private Text txtSession;
	private Text txtBizDomain;
	private Text txtFID;
	private Text txtWS;
	private Text txtReport;
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents(String name)
	{
		trtmDbSwitcher.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				shlDbSwitcher.setVisible(true);
				shlDbSwitcher.setActive();
				shlDbSwitcher.setMinimized(false);
			}
		});
		trtmDbSwitcher.setText("DB Switcher");
		trtmDbSwitcher.setToolTipText("DB Switcher");
		trtmDbSwitcher.setImage(SWTResourceManager.getImage(AATools.class, "/icons/logo.gif"));
		shlDbSwitcher = new Shell();
		shlDbSwitcher.setImage(SWTResourceManager.getImage(AATools.class, "/icons/logo.gif"));
		shlDbSwitcher.addShellListener(new ShellAdapter() {
			@Override
			public void shellIconified(ShellEvent e) {
				shlDbSwitcher.setVisible(false);
			}
			@Override
			public void shellClosed(ShellEvent e) {
				DBProxy.stop();
			}
		});
		shlDbSwitcher.setSize(991, 541);
		shlDbSwitcher.setText("AATools@" + name);
		shlDbSwitcher.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(shlDbSwitcher, SWT.NONE);
		
		TabItem tbtmDbswitcher = new TabItem(tabFolder, SWT.NONE);
		tbtmDbswitcher.setToolTipText("DBSwitcher");
		tbtmDbswitcher.setText("DBSwitcher");
		
		// -- DBSwitcher begin
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmDbswitcher.setControl(composite);
		composite.setEnabled(true);
		composite.setLayout(new GridLayout(1, false));
		
		Group grpExistConnections = new Group(composite, SWT.NONE);
		grpExistConnections.setToolTipText("Exist DB connections");
		grpExistConnections.setText("Exist DB connections");
		grpExistConnections.setLayout(new GridLayout(2, false));
		grpExistConnections.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite_3 = new Composite(grpExistConnections, SWT.NONE);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tblConnections = new Table(composite_3, SWT.BORDER | SWT.FULL_SELECTION);
		tblConnections.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Connection conn = (Connection) tblConnections.getSelection()[0].getData();
				AddDBConnectionWindow win = new AddDBConnectionWindow(shlDbSwitcher.getDisplay(), conn);
				win.setAATools(AATools.this);
				win.open();
			}
		});
		tblConnections.setToolTipText("Exist db connections");
		tblConnections.setHeaderVisible(true);
		tblConnections.setLinesVisible(true);
		tblConnections.addListener(SWT.KeyDown, listenerConnCopy);
		
		TableColumn tableColumn = new TableColumn(tblConnections, SWT.NONE);
		tableColumn.setWidth(22);
		tableColumn.setText("#");
		
		TableColumn tblclmnName = new TableColumn(tblConnections, SWT.NONE);
		tblclmnName.setWidth(133);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnAaVer = new TableColumn(tblConnections, SWT.NONE);
		tblclmnAaVer.setWidth(53);
		tblclmnAaVer.setText("AA Ver");
		
		TableColumn tblclmnType = new TableColumn(tblConnections, SWT.NONE);
		tblclmnType.setWidth(57);
		tblclmnType.setText("Type");
		tblConnections.setSortColumn(tblclmnType);
		
		TableColumn tblclmnHost = new TableColumn(tblConnections, SWT.NONE);
		tblclmnHost.setWidth(85);
		tblclmnHost.setText("Host");
		
		TableColumn tblclmnPort = new TableColumn(tblConnections, SWT.NONE);
		tblclmnPort.setToolTipText("");
		tblclmnPort.setWidth(43);
		tblclmnPort.setText("Port");
		
		TableColumn tblclmnService = new TableColumn(tblConnections, SWT.NONE);
		tblclmnService.setWidth(80);
		tblclmnService.setText("Service");
		
		TableColumn tblclmnUser = new TableColumn(tblConnections, SWT.NONE);
		tblclmnUser.setWidth(64);
		tblclmnUser.setText("User");
		
		TableColumn tblclmnPassword = new TableColumn(tblConnections, SWT.NONE);
		tblclmnPassword.setWidth(66);
		tblclmnPassword.setText("Password");
		
		TableColumn tblclmnDate = new TableColumn(tblConnections, SWT.NONE);
		tblclmnDate.setWidth(78);
		tblclmnDate.setText("Date");
		
		TableColumn tblclmnComment = new TableColumn(tblConnections, SWT.NONE);
		tblclmnComment.setWidth(200);
		tblclmnComment.setText("Comment");
		
		Composite composite_4 = new Composite(grpExistConnections, SWT.NONE);
		RowLayout rowLayout_1 = new RowLayout(SWT.VERTICAL);
		rowLayout_1.fill = true;
		composite_4.setLayout(rowLayout_1);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		Button btnSwitch = new Button(composite_4, SWT.NONE);
		btnSwitch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switchDB(false);
			}
		});
		btnSwitch.setText("Switch");
		
		Button btnSwitchWithAA = new Button(composite_4, SWT.NONE);
		btnSwitchWithAA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switchDB(true);
			}
		});
		btnSwitchWithAA.setText("Switch With AA");
		
		Button btnQueryAgency = new Button(composite_4, SWT.NONE);
		btnQueryAgency.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				queryAgencies();
			}
		});
		btnQueryAgency.setToolTipText("Get all agencies name");
		btnQueryAgency.setText("Query Agencies");
		
		Button btnGetDbVersion = new Button(composite_4, SWT.NONE);
		btnGetDbVersion.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getProductVersion();
			}
		});
		btnGetDbVersion.setToolTipText("Get the AA product version ");
		btnGetDbVersion.setText("Get Product Version");
		
		Button btnResetPassword = new Button(composite_4, SWT.NONE);
		btnResetPassword.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetPassword();
			}
		});
		btnResetPassword.setToolTipText("Reset all user's password to 'admin'");
		btnResetPassword.setText("Reset Password");
		
		Button btnClearConsole = new Button(composite_4, SWT.NONE);
		btnClearConsole.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearConsole();
			}
		});
		btnClearConsole.setText("Clear Console");
		
		Button btnFidManage = new Button(composite_4, SWT.NONE);
		btnFidManage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fidManage();
			}
		});
		btnFidManage.setText("FID Manage");
		
		// placeholder 
		new Label(composite_4, SWT.NONE);
		
		Button btnAdd = new Button(composite_4, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addDB();
			}
		});
		btnAdd.setText("Add Connection");
		
		Button btnRemoveConnection = new Button(composite_4, SWT.NONE);
		btnRemoveConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelectedConnection();
			}
		});
		btnRemoveConnection.setText("Remove Connection");
		
		Button refeshButton = new Button(composite_4, SWT.NONE);
		refeshButton.setText("Refresh");
		refeshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshConnections();
			}
		});
		
		// placeholder 
		new Label(composite_4, SWT.NONE);
		
		txtKeyword = new Text(composite_4, SWT.BORDER);
		txtKeyword.addListener(SWT.KeyDown, searchListener);
		
		Button btnSearch_1 = new Button(composite_4, SWT.NONE);
		btnSearch_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchDB();
			}
		});
		btnSearch_1.setText("Search");
		
		// placeholder 
		new Label(composite_4, SWT.NONE);
		
		// placeholder 
		new Label(composite_4, SWT.NONE);
		
		Button btnAbout = new Button(composite_4, SWT.NONE);
		btnAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showAboutMessage();
			}
		});
		btnAbout.setText("About");
		// --DBSwitcher end
		
		// --DB Sheet begin
		TabItem tbtmDbSheet = new TabItem(tabFolder, SWT.NONE);
		tbtmDbSheet.setToolTipText("DB Sheet");
		tbtmDbSheet.setText("DB Sheet");
		
		Composite composite_5 = new Composite(tabFolder, SWT.NONE);
		tbtmDbSheet.setControl(composite_5);
		composite_5.setLayout(new GridLayout(4, false));
		
		Label lblKeyword = new Label(composite_5, SWT.NONE);
		lblKeyword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblKeyword.setText("Keywords: ");
		
		txtKeywords = new Text(composite_5, SWT.BORDER);
		txtKeywords.addListener(SWT.KeyDown, listenerSearch);
		txtKeywords.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ddType = new Combo(composite_5, SWT.NONE);
		ddType.setItems(new String[] {"Table", "Column"});
		ddType.setToolTipText("DB Table or Column");
		ddType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		ddType.select(0);
		
		Button btnSearch = new Button(composite_5, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search();
			}
		});
		btnSearch.setText("Search");
		
		tblResults = new Table(composite_5, SWT.BORDER | SWT.FULL_SELECTION);
		tblResults.addListener(SWT.KeyDown, listenerCopy);
		tblResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		tblResults.setHeaderVisible(true);
		tblResults.setLinesVisible(true);
		
		tblResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				AAObject obj = (AAObject) tblResults.getSelection()[0].getData();
				String tableName = "";
				if (obj.getObjectType().equals("COLUMN")) {
					tableName = obj.getType();
				}
				else {
					tableName = obj.getName();
				}
				
				DescTable win = new DescTable();
				win.setTableName(tableName);
				win.open();
			}
		});
		
		TableColumn tblclmnName_1 = new TableColumn(tblResults, SWT.NONE);
		tblclmnName_1.setWidth(169);
		tblclmnName_1.setText("Object");
		
		TableColumn tblclmnType_1 = new TableColumn(tblResults, SWT.NONE);
		tblclmnType_1.setWidth(108);
		tblclmnType_1.setText("Type");
		
		TableColumn tblclmnDescription = new TableColumn(tblResults, SWT.NONE);
		tblclmnDescription.setWidth(666);
		tblclmnDescription.setText("Description");
		// --DB Sheet end
		
		// --QA Tracer begin
		TabItem tbtmQaTracer = new TabItem(tabFolder, SWT.NONE);
		tbtmQaTracer.setToolTipText("QA Tracer");
		tbtmQaTracer.setText("QA Tracer");
		
		Composite composite_6 = new Composite(tabFolder, SWT.NONE);
		tbtmQaTracer.setControl(composite_6);
		composite_6.setLayout(new GridLayout(1, false));
		
		final Button btnStartTracer = new Button(composite_6, SWT.NONE);
		btnStartTracer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!QATracerStarted) {
					if (QATracer.start()) {
						QATracerStarted = true;
						btnStartTracer.setText("Stop tracer and view the results...");
						txtTraceResults.setText("QA Tracer started successfully! \r\nPlease refresh target portlet, when the page load completed, click above button to get the trace results.\r\n");
					}
					else {
						QATracer.stop();
						txtTraceResults.setText("\u53EF\u80FD\u6709\u5176\u4ED6\u4E13\u5BB6\u5728\u8DDF\u8E2A, \u8BF7\u7A0D\u7B4910\u79D2\u518D\u8BD5: " + QATracer.OUTPUT.toString()); 
					}
				}
				else {
					QATracerStarted = false;
					QATracer.stop();
					btnStartTracer.setText("\u5F00\u59CB\u8DDF\u8E2A...");
					txtTraceResults.setText("Trace results: \r\n������������������\r\n" + QATracer.OUTPUT.toString());
				}
			}
		});
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 337;
		btnStartTracer.setLayoutData(gridData);
		btnStartTracer.setText("Start Tracer...");
		
		txtTraceResults = new Text(composite_6, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtTraceResults.setText("User Guide: \r\n\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\r\n1. \u767B\u5F55\uFF1A https://maintenance.achievo.com:5443/\r\n2. \u6D4F\u89C8\u5230\u8981\u8DDF\u8E2A\u7684Portlet\u9875\u9762\r\n3. \u70B9\u51FB\u201C\u5F00\u59CB\u8DDF\u8E2A\u201D\u6309\u94AE\r\n4. \u518D\u6B21\u5237\u65B0\u8981\u8DDF\u8E2A\u7684\u9875\u9762 \uFF08\u5728Portlet\u9875\u9762\u4E0A\u70B9\u53F3\u952E\uFF0C\u9009Refresh\uFF09\r\n5. \u5F85\u9875\u9762\u91CD\u65B0\u8F7D\u5165\u540E\uFF0C\u518D\u70B9\u51FB\u201C\u67E5\u770B\u7ED3\u679C\u6309\u94AE\u201D");
		txtTraceResults.setFont(SWTResourceManager.getFont("Tahoma", 9, SWT.NORMAL));
		txtTraceResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		// --QA Tracer end
		
		// --Log Analyst begin
		TabItem tbtmLogAnalyst = new TabItem(tabFolder, SWT.NONE);
		tbtmLogAnalyst.setText("Log Analyst");
		
		Composite composite_7 = new Composite(tabFolder, SWT.NONE);
		tbtmLogAnalyst.setControl(composite_7);
		composite_7.setLayout(new GridLayout(2, false));
		
		Button btnLoadLogFile = new Button(composite_7, SWT.NONE);
		btnLoadLogFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlDbSwitcher);
				String fn = dlg.open();
				String output = "";
				if (fn != null) {
					File f = new File(fn);
					if (fn.indexOf("dba") >= 0) {
						output = DBALogAnalyst.analyst(f);
					}
					else if (fn.indexOf("server") >= 0) {
						output = ServerLogAnalyst.analyst(f);
					}
					else if (fn.indexOf("access") >= 0) {
						output = WebLogAnalyst.analyst(f);
					}
					
					txtLogResults.setText(output);
				}
			}
		});
		btnLoadLogFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnLoadLogFile.setText("Load Log File...");
		
		Button btnFilterDiagnosticsContent = new Button(composite_7, SWT.NONE);
		btnFilterDiagnosticsContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnFilterDiagnosticsContent.setText("Filter Diagnostics Content");
		btnFilterDiagnosticsContent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlDbSwitcher);
				String fn = dlg.open();
				String output = "";
				if (fn != null) {
					File f = new File(fn);
					output = LogUtils.reduceDiagnostics(f);
					
					txtLogResults.setText(output);
				}
			}
		});
		
		txtLogResults = new Text(composite_7, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtLogResults.addListener(SWT.KeyDown, listenerSelectAll);
		txtLogResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		// --Log Analyst end		
		
		// --Decoder/Encoder begin
		TabItem tbtmDecoderencoder = new TabItem(tabFolder, SWT.NONE);
		tbtmDecoderencoder.setToolTipText("Decoder/Encoder");
		tbtmDecoderencoder.setText("Decoder/Encoder");
		
		Composite composite_8 = new Composite(tabFolder, SWT.NONE);
		tbtmDecoderencoder.setControl(composite_8);
		composite_8.setLayout(new GridLayout(2, false));
		
		Button btnDecode = new Button(composite_8, SWT.NONE);
		btnDecode.setText("Decode");
		btnDecode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String content = txtContent.getText();
				content = URLDecoder.decode(content);
				txtContent.setText(content);
			}
		});
		
		Button btnEncode = new Button(composite_8, SWT.NONE);
		btnEncode.setText("Encode");
		btnEncode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String content = txtContent.getText();
				content = URLEncoder.encode(content);
				txtContent.setText(content);
			}
		});
		
		txtContent = new Text(composite_8, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		txtContent.addListener(SWT.KeyDown, listenerSelectAll);
		// --Decoder/Encoder end
		
		// --V360 Text begin
		TabItem V360Text = new TabItem(tabFolder, SWT.NONE);
		V360Text.setToolTipText("Generate V360 Text");
		V360Text.setText("V360 Text");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		V360Text.setControl(composite_1);
		GridLayout gridLayout = new GridLayout(6, false);
		gridLayout.marginBottom = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		gridLayout.marginTop = 5;
		composite_1.setLayout(gridLayout);
		
		Label lblTargetVersions = new Label(composite_1, SWT.NONE);
		lblTargetVersions.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTargetVersions.setAlignment(SWT.RIGHT);
		lblTargetVersions.setText("Target Versions:");
		
		final Button chk670 = new Button(composite_1, SWT.CHECK);
		chk670.setText("6.7.0");
		
		final Button chk700 = new Button(composite_1, SWT.CHECK);
		chk700.setText("7.0.0");
		
		final Button chk705 = new Button(composite_1, SWT.CHECK);
		chk705.setText("7.0.5");
		
		final Button chk710 = new Button(composite_1, SWT.CHECK);
		chk710.setSelection(true);
		chk710.setText("7.1.0");
		
		final Button chk720 = new Button(composite_1, SWT.CHECK);
		chk720.setSelection(true);
		chk720.setText("7.2.0");
		
		Label lblStringkey = new Label(composite_1, SWT.NONE);
		lblStringkey.setAlignment(SWT.RIGHT);
		lblStringkey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStringkey.setText("STRING_KEY:");
		
		txtKey = new Text(composite_1, SWT.BORDER);
		txtKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Label lblStringvalue = new Label(composite_1, SWT.NONE);
		lblStringvalue.setAlignment(SWT.RIGHT);
		lblStringvalue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStringvalue.setText("STRING_VALUE: ");
		
		txtValue = new Text(composite_1, SWT.BORDER);
		txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Label lblCategoryname = new Label(composite_1, SWT.NONE);
		lblCategoryname.setAlignment(SWT.RIGHT);
		lblCategoryname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCategoryname.setText("CATEGORY_NAME: ");
		
		txtCategory = new Text(composite_1, SWT.BORDER);
		txtCategory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Label lblRecdate = new Label(composite_1, SWT.NONE);
		lblRecdate.setAlignment(SWT.RIGHT);
		lblRecdate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRecdate.setText("REC_DATE: ");
		
		txtDate = new Text(composite_1, SWT.BORDER);
		txtDate.setText(getCurDateStr());
		txtDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Label lblRecfulnam = new Label(composite_1, SWT.NONE);
		lblRecfulnam.setAlignment(SWT.RIGHT);
		lblRecfulnam.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRecfulnam.setText("REC_FUL_NAM: ");
		
		txtRecName = new Text(composite_1, SWT.BORDER);
		txtRecName.setText("ADMIN");
		txtRecName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Label lblRecstatus = new Label(composite_1, SWT.NONE);
		lblRecstatus.setAlignment(SWT.RIGHT);
		lblRecstatus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRecstatus.setText("REC_STATUS: ");
		
		txtStatus = new Text(composite_1, SWT.BORDER);
		txtStatus.setText("A");
		txtStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Label lblCommit = new Label(composite_1, SWT.NONE);
		lblCommit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCommit.setText("Pop Commit window: ");
		
		final Button btnPopCommitWindow = new Button(composite_1, SWT.CHECK);
		btnPopCommitWindow.setSelection(true);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		Button btnRtrText = new Button(composite_1, SWT.NONE);
		btnRtrText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean[] target = new boolean[] {chk670.getSelection(), chk700.getSelection(), chk705.getSelection(), chk710.getSelection(), chk720.getSelection() };
				try
				{
					List<String> infoList = new ArrayList<String>(); 
					if(txtKey.getText() == null || txtKey.getText()== "")
					{
						showMessage(null,MessageUtil.getText("error_empty_string_key"));
						return;
					}
					infoList = V360TextGenerator.retrieveInfo(txtKey.getText(), target);
					if(infoList.size() == 0)
					{
						showMessage(null,MessageUtil.getText("error_nofound_string_key"));
					}
					else
					{
						txtValue.setText(infoList.get(1));
						txtCategory.setText(infoList.get(2));
						txtDate.setText(infoList.get(3));
						txtRecName.setText(infoList.get(4));
						txtStatus.setText(infoList.get(5));
						showMessage("Success","Retrieve successful !");
					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					showErrorMessage(e1.getMessage());
				}
			}
		});
		
		btnRtrText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnRtrText.setText("Retrieve Info by String_Key");
		
		Label lblArrow = new Label(composite_1, SWT.NONE);
		lblArrow.setAlignment(SWT.RIGHT);
		lblArrow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblArrow.setText("==> ");
		
		Button btnUpdText = new Button(composite_1, SWT.NONE);
		btnUpdText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean[] target = new boolean[] {chk670.getSelection(), chk700.getSelection(), chk705.getSelection(), chk710.getSelection(), chk720.getSelection() };
				try
				{
					boolean flag = false ;
					if(txtKey.getText() == null || txtKey.getText()== "")
					{
						showMessage(null,MessageUtil.getText("error_empty_string_key"));
						return;
					}
					if(txtValue.getText() == null || txtValue.getText()== "")
					{
						showMessage(null,MessageUtil.getText("error_empty_string_value"));
						return;
					}
					flag = V360TextGenerator.updateText(txtKey.getText(), txtValue.getText(), txtCategory.getText(), txtDate.getText(), txtRecName.getText(), btnPopCommitWindow.getSelection(), target, flag);
					if(!flag)
					{
						showMessage(null,MessageUtil.getText("error_nofound_string_key"));
					}
					else
					{
						showMessage("Success","Update successfully, please commit it!");
					}
					
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					showErrorMessage(e1.getMessage());
				}
			}
		});
		
		btnUpdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnUpdText.setText("Update(The field which left empty means won't change original data)");
			
		new Label(composite_1, SWT.NONE);	
		
		Button btnAddText = new Button(composite_1, SWT.NONE);
		btnAddText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean[] target = new boolean[] {chk670.getSelection(), chk700.getSelection(), chk705.getSelection(), chk710.getSelection(), chk720.getSelection() };
				try
				{
					if(txtKey.getText() == null || txtKey.getText()== "")
					{
						showMessage(null,MessageUtil.getText("error_empty_string_key"));
						return;
					}
					if (!txtKey.getText().toLowerCase().equals(txtKey.getText())) {
						showMessage(null,MessageUtil.getText("error_lowercase_string_key")); 
						return;
					}
					V360TextGenerator.addText(txtKey.getText(), txtValue.getText(), txtCategory.getText(), txtDate.getText(), txtRecName.getText(), txtStatus.getText(), btnPopCommitWindow.getSelection(), target);
					showErrorMessage("Added successfully, please commit it!");
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					showErrorMessage(e1.getMessage());
				}
			}
		});
		
		btnAddText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));
		btnAddText.setText("Add");
		
		Label lblGoToText = new Label(composite_1, SWT.NONE);
		lblGoToText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGoToText.setText("Go to Text Folder: ");
		
		Button button_670 = new Button(composite_1, SWT.NONE);
		button_670.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openFolder(V360TextGenerator.paths[0]);
			}
		});
		button_670.setText("6.7.0");
		
		Button button_700 = new Button(composite_1, SWT.NONE);
		button_700.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openFolder(V360TextGenerator.paths[1]);
			}
		});
		button_700.setText("7.0.0");
		
		Button button_705 = new Button(composite_1, SWT.NONE);
		button_705.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openFolder(V360TextGenerator.paths[2]);
			}
		});
		button_705.setText("7.0.5");
		
		Button button_710 = new Button(composite_1, SWT.NONE);
		button_710.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openFolder(V360TextGenerator.paths[3]);
			}
		});
		button_710.setText("7.1.0");
		
		Button button_720 = new Button(composite_1, SWT.NONE);
		button_720.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openFolder(V360TextGenerator.paths[4]);
			}
		});
		button_720.setText("7.2.0");
		// V360 Text end
		
		
		// Log++ begin
		TabItem tbtmLog = new TabItem(tabFolder, SWT.NONE);
		tbtmLog.setText("Log++");
		
		Composite composite_logPlus = new Composite(tabFolder, SWT.NONE);
		tbtmLog.setControl(composite_logPlus);
		composite_logPlus.setLayout(new GridLayout(1, false));
		
		Composite composite_12 = new Composite(composite_logPlus, SWT.NONE);
		composite_12.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gridData_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gridData_1.heightHint = 31;
		composite_12.setLayoutData(gridData_1);
		
		Button btnReset = new Button(composite_12, SWT.NONE);
		btnReset.setText(MessageUtil.getText("msg_trace_start"));
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (LogsUtil.reset()) {
					txtJSP.setText("");
					txtSelect.setText("");
					txtIDU.setText("");
					txtReq.setText("");
					txtSession.setText("");
					txtBizDomain.setText("");
					txtFID.setText("");
					txtWS.setText("");
					txtReport.setText("");
				}
			}
		});
		
		
		Button btnView = new Button(composite_12, SWT.NONE);
		btnView.setText(MessageUtil.getText("msg_trace_end"));
		btnView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] rs = LogsUtil.populate();
				if (rs == null) {
					showErrorMessage("读取D:/AALogger/AALogger.class.btrace文件错误!");
					return;
				}
				
				txtJSP.setText(rs[0]);
				txtSelect.setText(rs[1]);
				txtIDU.setText(rs[2]);
				txtReq.setText(rs[3]);
				txtSession.setText(rs[4]);
				txtBizDomain.setText(rs[5]);
				txtFID.setText(rs[6]);
				txtWS.setText(rs[7]);
				txtReport.setText(rs[8]);
			}
		});
		
		
		TabFolder tabFolder_1 = new TabFolder(composite_logPlus, SWT.BOTTOM);
		tabFolder_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmJspAction = new TabItem(tabFolder_1, SWT.NONE);
		tbtmJspAction.setText("JSP + Action");
		
		Composite composite_2 = new Composite(tabFolder_1, SWT.NONE);
		tbtmJspAction.setControl(composite_2);
		FillLayout fillLayout_1 = new FillLayout(SWT.HORIZONTAL);
		composite_2.setLayout(fillLayout_1);
		
		txtJSP = new Text(composite_2, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		TabItem tbtmSelectSql = new TabItem(tabFolder_1, SWT.NONE);
		tbtmSelectSql.setText("Select SQL");
		
		Composite composite_9 = new Composite(tabFolder_1, SWT.NONE);
		tbtmSelectSql.setControl(composite_9);
		composite_9.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txtSelect = new Text(composite_9, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		TabItem tbtmIdlSql = new TabItem(tabFolder_1, SWT.NONE);
		tbtmIdlSql.setText("IDU SQL");
		
		Composite composite_10 = new Composite(tabFolder_1, SWT.NONE);
		tbtmIdlSql.setControl(composite_10);
		composite_10.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txtIDU = new Text(composite_10, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		TabItem tbtmRequestattribute = new TabItem(tabFolder_1, SWT.NONE);
		tbtmRequestattribute.setText("Request.Attribute");
		
		Composite composite_11 = new Composite(tabFolder_1, SWT.NONE);
		tbtmRequestattribute.setControl(composite_11);
		composite_11.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txtReq = new Text(composite_11, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		TabItem tbtmSessionattribute = new TabItem(tabFolder_1, SWT.NONE);
		tbtmSessionattribute.setText("Session.Attribute");
		
		Composite composite_13 = new Composite(tabFolder_1, SWT.NONE);
		tbtmSessionattribute.setControl(composite_13);
		composite_13.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txtSession = new Text(composite_13, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		TabItem tbtmBizdomain = new TabItem(tabFolder_1, SWT.NONE);
		tbtmBizdomain.setText("BizDomain");
		
		Composite composite_14 = new Composite(tabFolder_1, SWT.NONE);
		tbtmBizdomain.setControl(composite_14);
		composite_14.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txtBizDomain = new Text(composite_14, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		TabItem tbtmFid = new TabItem(tabFolder_1, SWT.NONE);
		tbtmFid.setText("FID");
		
		Composite composite_15 = new Composite(tabFolder_1, SWT.NONE);
		tbtmFid.setControl(composite_15);
		composite_15.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txtFID = new Text(composite_15, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		TabItem tbtmWebservice = new TabItem(tabFolder_1, SWT.NONE);
		tbtmWebservice.setText("GIS GovXML");
		
		Composite composite_16 = new Composite(tabFolder_1, SWT.NONE);
		tbtmWebservice.setControl(composite_16);
		composite_16.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txtWS = new Text(composite_16, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		TabItem tbtmReport = new TabItem(tabFolder_1, SWT.NONE);
		tbtmReport.setText("Report");
		
		Composite composite_17 = new Composite(tabFolder_1, SWT.NONE);
		tbtmReport.setControl(composite_17);
		composite_17.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txtReport = new Text(composite_17, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		
		// --Log++ end
	}
	
	private static void openFolder(String folder) {
		String program = "explorer " + folder;
		try
		{
			Process p = Runtime.getRuntime().exec(program);
			p.waitFor();
			p.destroy();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	private static String getCurDateStr() {
		String strFormat = "MM/dd/yyyy";
		SimpleDateFormat sdFormat = new SimpleDateFormat(strFormat);
		Date d = new Date();
		return sdFormat.format(d);
	}
}

/*
*$Log: av-env.bat,v $
*/
