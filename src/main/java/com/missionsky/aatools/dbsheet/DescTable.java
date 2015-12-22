package com.missionsky.aatools.dbsheet;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: DescTable.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2011
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall DescTable.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  Jan 18, 2011			john.huang				Initial.
 *  
 * </pre>
 */
public class DescTable
{

	protected Shell shell;
	private Table table;
	
	private String tableName;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			DescTable window = new DescTable();
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
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents()
	{
		shell = new Shell();
		shell.setSize(932, 368);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnColumnName = new TableColumn(table, SWT.NONE);
		tblclmnColumnName.setWidth(150);
		tblclmnColumnName.setText("Column Name");
		
		TableColumn tblclmnDatatype = new TableColumn(table, SWT.NONE);
		tblclmnDatatype.setWidth(72);
		tblclmnDatatype.setText("Data Type");
		
		TableColumn tblclmnDatalen = new TableColumn(table, SWT.NONE);
		tblclmnDatalen.setWidth(54);
		tblclmnDatalen.setText("Length");
		
		TableColumn tblclmnNullable = new TableColumn(table, SWT.NONE);
		tblclmnNullable.setWidth(34);
		tblclmnNullable.setText("Null");
		
		TableColumn tblclmnPkflag = new TableColumn(table, SWT.NONE);
		tblclmnPkflag.setWidth(113);
		tblclmnPkflag.setText("PK Flag");
		
		TableColumn tblclmnColumndesc = new TableColumn(table, SWT.NONE);
		tblclmnColumndesc.setWidth(481);
		tblclmnColumndesc.setText("Column Desc");

		fillItems();
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	private void fillItems() {
		shell.setText(tableName);
		ArrayList<TableObject> results = DBSheetUtil.desc(tableName);
		if (results == null) {
			return;
		}
		
		// fill results
		for (TableObject obj : results)
		{
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(new String[] {obj.getName(), obj.getDataType(), obj.getDataLen(), obj.getNullable(), obj.getPk_flag(), obj.getDescription()});
		}
	}

}

/*
*$Log: av-env.bat,v $
*/