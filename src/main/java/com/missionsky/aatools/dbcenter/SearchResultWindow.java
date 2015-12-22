package com.missionsky.aatools.dbcenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.missionsky.aatools.swtdesigner.SWTResourceManager;

import org.eclipse.swt.layout.FillLayout;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: SearchResultWindow.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall SearchResultWindow.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-8-9			john.huang				Initial.
 *  
 * </pre>
 */
public class SearchResultWindow extends Shell
{
	private Text text;

	/**
	 * Create the shell.
	 * @param display
	 */
	public SearchResultWindow(Display display)
	{
		super(display, SWT.SHELL_TRIM);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		text = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents()
	{
		setText("DB Search Result");
		setSize(666, 361);

	}
	
	public void setResult(String rs) {
		text.setText(rs);
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