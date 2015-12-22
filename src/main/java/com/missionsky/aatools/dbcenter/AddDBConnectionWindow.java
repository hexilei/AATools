package com.missionsky.aatools.dbcenter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.missionsky.aatools.ui.AATools;
import com.missionsky.aatools.ui.Connection;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: AddDBConnectionWindow.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall AddDBConnectionWindow.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-8-3			john.huang				Initial.
 *  
 * </pre>
 */
public class AddDBConnectionWindow extends Shell
{
	private final static String[] items = new String[] {"7.3.0", "7.2.0", "7.1.0", "7.0.5", "7.0.0", "6.7.0", "6.6.0", "6.5.0"};
	
	private Text textName;
	private Text textHost;
	private Text textPort;
	private Text textService;
	private Text textUser;
	private Text textPassword;
	private Text textDate;
	private Text textComment;
	private Combo comboVersion;
	private Combo comboType;

	private AATools aaTools;
	private Connection conn;
	private boolean isNewCreated = false;
	private Text txtRawConnInfo;
	
	public void setAATools(AATools aaTools) {
		this.aaTools = aaTools;
	}
	
	public void setConnection(Connection connection) {
		this.conn = connection;
	}
	
	private void addDB() {
		if (conn == null) {
			conn = new Connection();
		}
		conn.setName(textName.getText());
		conn.setAAVersion(comboVersion.getText());
		conn.setType(comboType.getText());
		conn.setHost(textHost.getText());
		conn.setPort(textPort.getText());
		conn.setService(textService.getText());
		conn.setUser(textUser.getText());
		conn.setPass(textPassword.getText());
		conn.setDate(textDate.getText());
		conn.setComment(textComment.getText());
		
		if (isNewCreated && aaTools.connections.indexOf(conn) >= 0) {
			aaTools.showErrorMessage("The connection '" + conn.getName() + "' already exists");
		}
		else {
			if (isNewCreated) {
				aaTools.connections.add(conn);
				if (aaTools.saveConnections()) {
					aaTools.showMessage("Add New Connection", "Connection: " + conn.getName() + " added successfully!");;
					close();
				}
			}
			else {
				if (aaTools.saveConnections()) {
					aaTools.showMessage("Update Connection", "Connection: " + conn.getName() + " updated successfully!");
					close();
				}
			}
		}
	}
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public AddDBConnectionWindow(Display display, Connection connection)
	{
		super(display, SWT.SHELL_TRIM);
		conn = connection;
		
		if (conn == null) {
			conn = new Connection();
			isNewCreated = true;
		}
		
		setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		textName = new Text(this, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText(conn.getName());
		
		Label lblAaversion = new Label(this, SWT.NONE);
		lblAaversion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAaversion.setText("AAVersion:");
		
		comboVersion = new Combo(this, SWT.NONE);
		comboVersion.setItems(items);
		comboVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboVersion.select(1);
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(conn.getAAVersion())) {
				comboVersion.select(i);
			}
		}
		
		Label lblType = new Label(this, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type:");
		
		comboType = new Combo(this, SWT.NONE);
		comboType.setItems(new String[] {"ORACLE", "MSSQL"});
		comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if ("MSSQL".equalsIgnoreCase(conn.getType())) {
			comboType.select(1);
		}
		else {
			comboType.select(0);
		}
		
		Label lblHost = new Label(this, SWT.NONE);
		lblHost.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHost.setText("Host:");
		
		textHost = new Text(this, SWT.BORDER);
		textHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textHost.setText(conn.getHost());
		
		Label lblPort = new Label(this, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPort.setText("Port:");
		
		textPort = new Text(this, SWT.BORDER);
		textPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textPort.setText(conn.getPort());
		
		Label lblService = new Label(this, SWT.NONE);
		lblService.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblService.setText("Service:");
		
		textService = new Text(this, SWT.BORDER);
		textService.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textService.setText(conn.getService());
		
		Label lblUser = new Label(this, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser.setText("User:");
		
		textUser = new Text(this, SWT.BORDER);
		textUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textUser.setText(conn.getUser());
		
		Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Password:");
		
		textPassword = new Text(this, SWT.BORDER);
		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textPassword.setText(conn.getPass());
		
		Label lblDate = new Label(this, SWT.NONE);
		lblDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDate.setText("Date:");
		
		textDate = new Text(this, SWT.BORDER);
		textDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textDate.setText(conn.getDate());
		
		Label lblComment = new Label(this, SWT.NONE);
		lblComment.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblComment.setText("Comment:");
		
		textComment = new Text(this, SWT.BORDER);
		textComment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textComment.setText(conn.getComment());
		
		Button btnSubmit = new Button(this, SWT.NONE);
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addDB();
			}
		});
		btnSubmit.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		btnSubmit.setText("Submit");
		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		new Label(this, SWT.NONE);
		
		txtRawConnInfo = new Text(this, SWT.BORDER | SWT.MULTI);
		GridData gridData_1_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gridData_1_1.heightHint = 69;
		txtRawConnInfo.setLayoutData(gridData_1_1);
		
		Button btnIntelligentFill = new Button(this, SWT.NONE);
		btnIntelligentFill.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Connection conn = IntelligentFilUtil.getConnInfoFromText(txtRawConnInfo.getText());
				if (conn != null) {
					for (int i = 0; i < items.length; i++) {
						if (items[i].equals(conn.getAAVersion())) {
							comboVersion.select(i);
						}
					}
					
					if ("MSSQL".equalsIgnoreCase(conn.getType())) {
						comboType.select(1);
					}
					else {
						comboType.select(0);
					}
					textHost.setText(conn.getHost());
					textPort.setText(conn.getPort());
					textService.setText(conn.getService());
					textUser.setText(conn.getUser());
					textPassword.setText(conn.getPass());
					textDate.setText(conn.getDate());
					textComment.setText(conn.getComment());
				}
			}
		});
		btnIntelligentFill.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		btnIntelligentFill.setText("Intelligent Fill");
		createContents();
	}

	/**
	 * Creat1 contents of the shell.
	 */
	protected void createContents()
	{
		setText("Add New Connection");
		setSize(457, 437);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

}

/*
*$Log: av-env.bat,v $
*/