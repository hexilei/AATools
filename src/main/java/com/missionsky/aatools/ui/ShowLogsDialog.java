package com.missionsky.aatools.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.missionsky.aatools.swtdesigner.SWTResourceManager;

/**
 * 
 * @author john.huang
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowLogsDialog extends Dialog
{

	protected Object result;

	protected Shell shlSwitchLogs;
	private Text txgLogs;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ShowLogsDialog(Shell parent, int style)
	{
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open()
	{
		createContents();
		shlSwitchLogs.open();
		shlSwitchLogs.layout();
		Display display = getParent().getDisplay();
		while (!shlSwitchLogs.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents()
	{
		shlSwitchLogs = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shlSwitchLogs.setSize(450, 300);
		shlSwitchLogs.setText("Switch Logs");
		shlSwitchLogs.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		txgLogs = new Text(shlSwitchLogs, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txgLogs.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));

		File file = FileUtil.loadFile("switchLogs.log");
		byte[] logs = FileUtil.loadBytesFromFile(file);
		if (logs != null) {
			txgLogs.setText(new String(logs));
		}
	}

}

/*
*$Log: av-env.bat,v $
*/