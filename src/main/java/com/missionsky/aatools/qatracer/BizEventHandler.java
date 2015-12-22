package com.missionsky.aatools.qatracer;


import com.sun.jdi.Method;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;

public class BizEventHandler extends EventHandler {
    BizEventHandler(VirtualMachine vm)
	{
		super(vm);
	}

    public void handleMethodEntryEvent(final MethodEntryEvent event) {

    }
    
    public void handleMethodExitEvent(final MethodExitEvent event) {

    }
    
    public void handleClassPrepareEvent(final ClassPrepareEvent event) {

    }
    
    
	public void handleBreakPointEvent(final BreakpointEvent event) throws Exception {
		Method m = event.location().method();
		String clsName = m.declaringType().name();
		
		if (clsName.equals(QATracer.BIZDOMINA_MODEL)) {
			showBizDomain("BIZ Side: ", event);
			return;
		}
	}
}
