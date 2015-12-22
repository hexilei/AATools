package com.missionsky.aatools.qatracer;


import java.util.List;
import java.util.Stack;

import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.MethodEntryRequest;

public class WebEventHandler extends EventHandler {
	private static Stack<String> JSP_END_INFOs = new Stack<String>();
	
	// FID checked list output
	protected static StringBuilder FIDOutput = new StringBuilder();
	
    WebEventHandler(VirtualMachine vm)
	{
		super(vm);
	}

    public void handleMethodEntryEvent(final MethodEntryEvent event) {
    	Method m = event.method();
    	String clsName = m.declaringType().name();
    	String name = event.method().name();
    	
    	if (name.equals("_jspService")) {
    		// disable method entry
    		List<MethodEntryRequest> req2 = event.virtualMachine().eventRequestManager().methodEntryRequests();
    		for (MethodEntryRequest req : req2) {
    			req.disable();
    		}
    		
    		if (clsName.startsWith("org.apache.jsp.framework")) {
    			synchronized (LOCK)	{
    				if (PRINT_SWITCHER > 0) {
    					PRINT_SWITCHER++;
    				}
    			}
        		return;
        	}
    		
    		StringBuffer output = new StringBuffer();
	    	output.append("________________________").append(clsName).append(".").append(name).append("() BEGIN________________________\r\n");
	    	
	    	QATracer.OUTPUT.append(output.toString()).append("\r\n");
	    	
    		StringBuffer endInfo = new StringBuffer();
	    	endInfo.append("________________________").append(clsName).append(".").append(name).append("() END________________________\r\n");
	    	JSP_END_INFOs.push(endInfo.toString());
    		
			synchronized (LOCK) {
				PRINT_SWITCHER ++;
			}
    	}
    }
    
    public void handleMethodExitEvent(final MethodExitEvent event) {

    }
    
    public void handleClassPrepareEvent(final ClassPrepareEvent event) {
    	try
		{
			String className = event.referenceType().name();
			if (className.equals(QATracer.MENUBARUTIL)) {
				// when check menu item FID
				ReferenceType rt = event.virtualMachine().classesByName(QATracer.MENUBARUTIL).get(0);
				Location location = rt.locationsOfLine(QATracer.MENUBARUTIL_POINT).get(0);
				BreakpointRequest bpr = event.virtualMachine().eventRequestManager().createBreakpointRequest(location);
				bpr.enable();
				System.out.println(rt.name() + ".convertMenuItem() hooked.");
			}
			else if (className.equals(QATracer.TABBARUTIL)) {
				// when check tab item FID
				ReferenceType rt = event.virtualMachine().classesByName(QATracer.TABBARUTIL).get(0);
				Location location = rt.locationsOfLine(QATracer.TABBARUTIL_POINT).get(0);
				BreakpointRequest bpr = event.virtualMachine().eventRequestManager().createBreakpointRequest(location);
				bpr.enable();
				System.out.println(rt.name() + ".createTabBarItem() hooked.");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }
    
	public void handleBreakPointEvent(final BreakpointEvent event) throws Exception {
		Method m = event.location().method();
		String clsName = m.declaringType().name();
		
		if (clsName.equals(QATracer.HTTP_JSP_BASE)) {
			int lineNumber = event.location().lineNumber();
			if (lineNumber == QATracer.JSP_IN) {
				// enable method entry
				List<MethodEntryRequest> req2 = event.virtualMachine().eventRequestManager().methodEntryRequests();
				for (MethodEntryRequest req : req2) {
					req.enable();
				}
				return;
			}
			else if (lineNumber == QATracer.JSP_OUT) {
				synchronized (LOCK) {
					if (PRINT_SWITCHER > 0) {
						PRINT_SWITCHER --;
						// show all bizdomain values
						if (PRINT_SWITCHER <= 0) {
							if (bizDomainOut.length() > 0) {
								QATracer.OUTPUT.append("________________________________________________USED STANDARD CHOICES LIST________________________________________________\r\n").append("\r\n");
								
								QATracer.OUTPUT.append(bizDomainOut).append("\r\n");
								
								QATracer.OUTPUT.append("________________________________________________USED STANDARD CHOICES END________________________________________________\r\n").append("\r\n");
								bizDomainOut.delete(0, bizDomainOut.length());
							}
							
							if (FIDOutput.length() > 0) {
								QATracer.OUTPUT.append("________________________________________________CHECKED FID LIST________________________________________________\r\n").append("\r\n");
								
								QATracer.OUTPUT.append(FIDOutput).append("\r\n");
								
								QATracer.OUTPUT.append("________________________________________________CHECKED FID LIST END________________________________________________\r\n").append("\r\n");
								bizDomainOut.delete(0, FIDOutput.length());
							}
							
							QATracer.OUTPUT.append(JSP_END_INFOs.pop()).append("\r\n");
						}
					}
				}
				return;
			}
		}
		else if (clsName.equals(QATracer.BIZDOMINA_MODEL)) {
			showBizDomain("Web Side", event);
			return;
		}
		else if (clsName.equals(QATracer.FID_MANAGER)) {
			showFIDInfo(event);
		}

		synchronized(LOCK) {
			if (PRINT_SWITCHER > 0) {
				if (clsName.equals(QATracer.BIZ_HELPER)) {
					handleBPForInvokeEJB(event);
				}
			}
		}
		
		if (clsName.equals(QATracer.MENUBARUTIL)) {
			handleBPForMenuBarUtil(event);
		}
		else if (clsName.equals(QATracer.TABBARUTIL)) {
			handleBPForTabBarUtil(event);
		}
	}
	
	private void showFIDInfo(final BreakpointEvent event) throws Exception {
		List <StackFrame> frames = event.thread().frames();
		StackFrame frame = frames.get(0);
		String FIDCode = getFieldValueOfObjectVarialbe(frame, "fidModel", "FIDCode");
		if (FIDCode == null) return;
		
		String FIDName = getFieldValueOfObjectVarialbe(frame, "fidModel", "FIDName");
		String isFullAccess = getFieldValueOfObjectVarialbe(frame, "fidModel", "isFullAccess");
		if (isFullAccess.equals("true")) {
			isFullAccess = "Y";
		}
		else {
			isFullAccess = "N";
		}
		
		String isReadOnly = getFieldValueOfObjectVarialbe(frame, "fidModel", "isReadOnly");
		if (isReadOnly.equals("true")) {
			isReadOnly = "Y";
		}
		else {
			isReadOnly = "N";
		}
		
		FIDOutput.append("-- ").append("FID: ").append(FIDCode).append(" FULL?: ").append(isFullAccess).append(" ReadOnly?: ").append(isReadOnly).append("   Name: ").append(FIDName).append("\r\n");
	}
	
	private void handleBPForMenuBarUtil(final BreakpointEvent event) throws Exception {
		List <StackFrame> frames = event.thread().frames();
		StackFrame frame = frames.get(0);
		LocalVariable var = frame.visibleVariableByName("menuItem");
		ObjectReference item = (ObjectReference)frame.getValue(var);
		ReferenceType type = (ReferenceType) item.type();
		Field field = type.fieldByName("m_isDisplay");
		Value fidResult = item.getValue(field);
		field = type.fieldByName("m_itemPolicy");
		Value fid = item.getValue(field);
		field = type.fieldByName("m_itemName");
		Value name = item.getValue(field);
		field = type.fieldByName("m_itemLabel");
		Value label = item.getValue(field);
		
		StringBuffer output = new StringBuffer();
		output.append("+MenuBarItem > name: ").append(name).append(" FID: ").append(fid).append(" hasRight: ").append(fidResult).append(" label: ").append(label);
		
		QATracer.OUTPUT.append(output).append("\r\n");
	}
	
	private void handleBPForTabBarUtil(final BreakpointEvent event) throws Exception {
		List <StackFrame> frames = event.thread().frames();
		StackFrame frame = frames.get(0);
		LocalVariable var = frame.visibleVariableByName("tabBarItem");
		ObjectReference item = (ObjectReference)frame.getValue(var);
		ReferenceType type = (ReferenceType) item.type();
		Field field = type.fieldByName("m_isDisplay");
		Value fidResult = item.getValue(field);
		field = type.fieldByName("m_itemPolicy");
		Value fid = item.getValue(field);
		field = type.fieldByName("m_itemName");
		Value name = item.getValue(field);
		field = type.fieldByName("m_itemLabel");
		Value label = item.getValue(field);
		
		StringBuffer output = new StringBuffer();
		output.append("-TabBarItem > name: ").append(name).append(" FID: ").append(fid).append(" hasRight: ").append(fidResult).append(" label: ").append(label);
		
		QATracer.OUTPUT.append(output).append("\r\n");
	}
	
	private void handleBPForInvokeEJB(final BreakpointEvent event) throws Exception {
		StringBuffer output = new StringBuffer();
		
		// show web side stacks
		output.append("#JSP Stacks: \r\n");
		
		List <StackFrame> frames = event.thread().frames();
		StackFrame frame = frames.get(0);
		ObjectReference obj = frame.thisObject();
		String rs = obj.getValue(obj.referenceType().fieldByName("objectInterface")).toString();
		int start = rs.indexOf("class=");
		int end = rs.indexOf(",");
		rs = rs.substring(start + 6, end);
		
		int count = 0;
		String space = "";
		for (StackFrame frm : frames)
		{
			Location location = frm.location();
			String name = location.declaringType().name();
			count++;
			
			if (name.startsWith("org.apache.jsp.framework"))
			{ 
				// Ignore the call from tag and framework jsp
				return;
			}
			
			if (count == 2) {
				output.append(space + rs + "." + location.method().name() + "():" + location.lineNumber()).append("\r\n");
				count++;
				space += " ";
			}
			else if (count > 2) {
				output.append(space + name + "." + location.method().name() + "():" + location.lineNumber()).append("\r\n");
				if (name.indexOf("_jsp") > 0 || name.indexOf("RequestProcessor") > 0) {
					// TODO: get jsp source line number
					break;
				}
				count++;
				space += " ";
			}
		}
		// output
		output.append("-------------------------------------\r\n");
		QATracer.OUTPUT.append(output).append("\r\n");
	}
}
