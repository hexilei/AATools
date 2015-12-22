package com.missionsky.aatools.fidmanage;


import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.missionsky.aatools.swtdesigner.SWTResourceManager;
import com.missionsky.aatools.ui.Connection;


/**
 * <pre>
 * 
 *  Accela Automation
 *  File: FidManageWindow.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall FidManageWindow.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-3-24			john.huang				Initial.
 *  
 * </pre>
 */
public class FidManageWindow extends Shell
{
//	private static class ContentProvider implements IStructuredContentProvider {
//		public Object[] getElements(Object inputElement) {
//			return new Object[0];
//		}
//		public void dispose() {
//		}
//		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		}
//	}
	private Connection connection;
	private Text userID;
	private Text agency;
	private Text keyword;
	private Label errorMsg;
	private Label resultMessage;
	
	private Combo comboGroups;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[])
	{
		try
		{
			Display display = Display.getDefault();
			FidManageWindow shell = new FidManageWindow(display);
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
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	Listener listener = new Listener()
	{
		public void handleEvent(Event e)
		{
			if (e.keyCode == 13)
			{
				loadGroups();
			}
		}
	};
	
	Listener searchListener = new Listener()
	{
		public void handleEvent(Event e)
		{
			if (e.keyCode == 13)
			{
				search();
			}
		}
	};
	private Table fidTable;
	
	private void search() {
		TableItem[] items = fidTable.getItems();
		for (TableItem item : items)
		{
			for (int i = 0; i < fidTable.getColumnCount() - 1; i++) {
				if (item.getText(i).toLowerCase().indexOf(keyword.getText().toLowerCase()) >= 0) {
					item.setBackground(i, SWTResourceManager.getColor(SWT.COLOR_RED));
				}
				else {
					item.setBackground(i, SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
			}
			
		}
	}
	
	private void loadGroups() {
		ArrayList<String[]> selectedGroups = FIDUtil.getSelectedGroups(connection, agency.getText(), userID.getText());
		
		if (selectedGroups.size() == 0) {
			errorMsg.setVisible(true);
			return;
		}
		
		errorMsg.setVisible(false);
		comboGroups.removeAll();
		int i = 0;
		int selected = 0;
		for (String[] group : selectedGroups)
		{
			comboGroups.add(group[2] + " -- " + group[1] + " Module (" + group[0] + ")");
			comboGroups.setData(Integer.toString(i), group);
			if ("BUILDING".equalsIgnoreCase(group[1])) {
				selected = i;
			}
			i++;
		}
		
		comboGroups.select(selected);
	}
	
	private void loadFIDs() {
		int selected = comboGroups.getSelectionIndex();
		if (selected < 0) {
			return;
		}
		
		fidTable.removeAll();
		
		resultMessage.setText("-> " + comboGroups.getText());
		
		String[] group = (String[]) comboGroups.getData(Integer.toString(selected));
		String module = group[1];
		String groupID = group[0];
		
		ArrayList<FIDModel> fids = FIDUtil.getFIDs(connection, agency.getText(), module, groupID);
		
		String lastFunGroup = "";
		String lastFunType = "";
		String lastSubType = "";
		String lastFunCategory = "";
		
		String funGroup;
		String funType;
		String subType;
		String funCategory;
		for (FIDModel model : fids)
		{
			TableItem tableItem = new TableItem(fidTable, SWT.NONE);
			funGroup = model.funGroup;
			funType = model.funType;
			subType = model.subType;
			funCategory = model.funCategory;
			
			if (funGroup.equals(lastFunGroup)) {
				funGroup = "";
			}
			else {
				lastFunGroup = funGroup;
			}
			
			if (funType.equals(lastFunType)) {
				funType = "";
			}
			else {
				lastFunType = funType;
			}
			
			if (subType.equals(lastSubType)) {
				subType = "";
			}
			else {
				lastSubType = subType;
			}
			
			if (funCategory.equals(lastFunCategory)) {
				funCategory = "";
			}
			else {
				lastFunCategory = funCategory;
			}
			
			tableItem.setText(new String[] {funGroup, funType, subType, funCategory, model.code + " - " + model.funName + " - " + model.funVer, model.access});
			tableItem.setData(model);
		}
	}
	
	private void updateFID(FIDModel model) {
		if (FIDUtil.updateFID(connection, model)) {
			resultMessage.setText("Update FID " + model.code + " to " + model.access + " successfully!");
		}
		else {
			resultMessage.setText("Update FID " + model.code + " to " + model.access + " failed!");
		}
	}

	public void setConnection(Connection conn) {
		this.connection = conn;
	}
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public FidManageWindow(Display display)
	{
		super(display, SWT.SHELL_TRIM);
		setLayout(new GridLayout(3, false));
		
		Label lblAgency = new Label(this, SWT.NONE);
		lblAgency.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAgency.setText("Agency: ");
		
		agency = new Text(this, SWT.BORDER);
		GridData gridData_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData_1.widthHint = 295;
		agency.setLayoutData(gridData_1);
		new Label(this, SWT.NONE);
		
		Label lblUserId = new Label(this, SWT.NONE);
		lblUserId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUserId.setText("User ID: ");
		
		userID = new Text(this, SWT.BORDER);
		userID.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				loadGroups();
			}
		});
		userID.addListener(SWT.KeyDown, listener);

		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 295;
		userID.setLayoutData(gridData);
		
		errorMsg = new Label(this, SWT.NONE);
		errorMsg.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
		errorMsg.setVisible(false);
		errorMsg.setText("The user id may be not available!");
		errorMsg.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		Label lblGroup = new Label(this, SWT.NONE);
		lblGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGroup.setText("Groups: ");
		
		comboGroups = new Combo(this, SWT.NONE);
		comboGroups.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				loadFIDs();
			}
		});
		GridData gridData_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData_2.widthHint = 279;
		comboGroups.setLayoutData(gridData_2);
		new Label(this, SWT.NONE);
		
		Label lblSearch = new Label(this, SWT.NONE);
		lblSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSearch.setText("Mark Red: ");
		
		keyword = new Text(this, SWT.BORDER);
		GridData gridData_4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData_4.widthHint = 296;
		keyword.setLayoutData(gridData_4);
		keyword.addListener(SWT.KeyDown, searchListener);
		
		resultMessage = new Label(this, SWT.NONE);
		resultMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		resultMessage.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		resultMessage.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
		
		TableViewer tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setColumnProperties(new String[] {"Function Group", "Function Type", "Function Sub Type", "Function Category", "Function Name", "Access"});
		CellEditor[] cellEditor = new CellEditor[6];
		cellEditor[5] = new ComboBoxCellEditor(tableViewer.getTable(), ACCESS, SWT.READ_ONLY);
		tableViewer.setCellEditors(cellEditor);
		
		 ICellModifier modifier = new MyCellModifier(tableViewer);   
	     tableViewer.setCellModifier(modifier);  
		
		fidTable = tableViewer.getTable();
		fidTable.setLinesVisible(true);
		fidTable.setHeaderVisible(true);
		fidTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		TableColumn tblclmnFunctionGroup = new TableColumn(fidTable, SWT.NONE);
		tblclmnFunctionGroup.setWidth(100);
		tblclmnFunctionGroup.setText("Function Group");
		
		TableColumn tblclmnFunctionType = new TableColumn(fidTable, SWT.NONE);
		tblclmnFunctionType.setWidth(100);
		tblclmnFunctionType.setText("Function Type");
		
		TableColumn tblclmnFunctionSubType = new TableColumn(fidTable, SWT.NONE);
		tblclmnFunctionSubType.setWidth(128);
		tblclmnFunctionSubType.setText("Function Sub Type");
		
		TableColumn tblclmnFunctionCategory = new TableColumn(fidTable, SWT.NONE);
		tblclmnFunctionCategory.setWidth(143);
		tblclmnFunctionCategory.setText("Function Category");
		
		TableColumn tblclmnFunctionName = new TableColumn(fidTable, SWT.NONE);
		tblclmnFunctionName.setWidth(267);
		tblclmnFunctionName.setText("Function Name");
		
		TableColumn tblclmnAccess = new TableColumn(fidTable, SWT.NONE);
		tblclmnAccess.setWidth(56);
		tblclmnAccess.setText("Access");
		
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents()
	{
		setText("FID Manage");
		setSize(858, 518);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	
	public String[] ACCESS ={"FULL", "READ", "NONE"};   
	class MyCellModifier implements ICellModifier{   
	    private TableViewer tv;   
	       
	    public MyCellModifier(TableViewer tv){   
	            this.tv = tv;   
	    }   
	    
	    public boolean canModify(Object element, String property) {
	    	if (property.equals("Access")) {
	    		return true;
	    	}
	        return false;   
	    }   

	    public Object getValue(Object element, String property) { 
	    	for (int i = 0; i < ACCESS.length; i++) {
	    		if (ACCESS[i].equals(((FIDModel)element).access)) {
	    			return i;
	    		}
	    	}
	    	
	    	return 2;
	    }

	    public void modify(Object element, String property, Object value) {
	    	FIDModel model = ((FIDModel)(((TableItem)element).getData()));
	    	String newValue = ACCESS[((Integer)value).intValue()];
	    	
	    	if (!newValue.equals(model.access)) {
	    		model.access = newValue; 
		    	((TableItem)element).setText(5, model.access);
		    	
		    	
		    	updateFID(model);
	    	}
	    }   
	       
	}
}




/*
*$Log: av-env.bat,v $
*/