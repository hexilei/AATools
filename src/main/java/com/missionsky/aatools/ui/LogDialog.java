package com.missionsky.aatools.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Switch log dialog
 * 
 * @author john.huang
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LogDialog extends Dialog
{

	protected Object result = false;

	protected Shell shlPleaseFillThe;
	private Text txtUser;
	private Text txtReason;
	
	private String connName;
	
	private byte[] logs;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public LogDialog(Shell parent, int style)
	{
		super(parent, style);
	}
	
	public void setConnectionName(String name) {
		this.connName = name;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open()
	{
		loadLogs();
		createContents();
		shlPleaseFillThe.open();
		shlPleaseFillThe.layout();
		Display display = getParent().getDisplay();
		while (!shlPleaseFillThe.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		
		return result;
	}
	
	private void loadLogs() {
		File file = FileUtil.loadFile("switchLogs.log");
		logs = FileUtil.loadBytesFromFile(file);
		if (logs == null) {
			logs = new byte[0];
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents()
	{
		shlPleaseFillThe = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlPleaseFillThe.setSize(450, 111);
		shlPleaseFillThe.setText("Please fill the change log...");
		shlPleaseFillThe.setLayout(new GridLayout(2, false));
		
		Label lblUser = new Label(shlPleaseFillThe, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser.setText("User: ");
		
		txtUser = new Text(shlPleaseFillThe, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblReason = new Label(shlPleaseFillThe, SWT.NONE);
		lblReason.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReason.setText("Reason: ");
		
		txtReason = new Text(shlPleaseFillThe, SWT.BORDER);
		txtReason.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shlPleaseFillThe, SWT.NONE);
		
		Button btnContinue = new Button(shlPleaseFillThe, SWT.NONE);
		btnContinue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (txtUser.getText() == null || txtUser.getText().length() <= 1) {
					result = false;
				}
				else {
					result = true;
					// save logs
					byte[] newLog = (txtUser.getText() + " switch to " + connName + ": " + txtReason.getText() + " " + new Date().toLocaleString()+ "\r\n").getBytes();
					File file = FileUtil.loadFile("switchLogs.log");
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					try
					{
						bout.write(newLog);
						if (logs != null) {
							if (logs.length > 8192) {
								bout.write(logs, 0, 8192);
							}
							else {
								bout.write(logs);
							}
						}
						
						FileUtil.saveBytesToFile(file, bout.toByteArray());
					}
					catch (Exception e1)
					{
					}
				}
				LogDialog.this.shlPleaseFillThe.close();
			}
		});
		btnContinue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnContinue.setText("Continue...");

	}

}

/*
*$Log: av-env.bat,v $
*/