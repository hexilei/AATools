package com.missionsky.aatools.qatracer;


import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;

public abstract class EventHandler extends Thread {
    private volatile boolean connected = true;
    private boolean completed = false;
    private VirtualMachine vm;
    
    // Lock for sql print
    protected static Object LOCK = new Object();
    protected static int PRINT_SWITCHER = 0;
    
    // bizdomain output
    protected static StringBuffer bizDomainOut = new StringBuffer();
    
    EventHandler(VirtualMachine vm) {
    	this.vm = vm;
    }

    synchronized void shutdown() {
        connected = false;  // force run() loop termination
        interrupt();
        while (!completed) {
            try {wait();} catch (InterruptedException exc) {}
        }
    }

    public final void run() { 
        EventQueue queue = vm.eventQueue();
        EventSet eventSet;
		System.out.println("Connected: " + connected);
		while (connected)
		{
			try
			{
				eventSet = queue.remove();
				EventIterator it = eventSet.eventIterator();
				while (it.hasNext())
				{
					try
					{
						Object obj = it.next();
						if (obj instanceof BreakpointEvent) {
							final BreakpointEvent event = (BreakpointEvent) obj;
							
							try
							{
								handleBreakPointEvent (event);
							}
							catch (Exception e)
							{
								e.printStackTrace();
								System.out.println("\r\n");
							}
						}
						else if (obj instanceof ClassPrepareEvent) {
							final ClassPrepareEvent event = (ClassPrepareEvent) obj;
							handleClassPrepareEvent(event);
						}
						else if (obj instanceof MethodEntryEvent) {
							final MethodEntryEvent event = (MethodEntryEvent) obj;
							handleMethodEntryEvent(event);
						}
						else if (obj instanceof MethodExitEvent) {
							final MethodExitEvent event = (MethodExitEvent) obj;
							handleMethodExitEvent(event);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				
				eventSet.resume();
			}
			catch (VMDisconnectedException discExc)
			{
				System.out.println("Disconnected.");
				break;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
        }
        synchronized (this) {
            completed = true;
            notifyAll();
        }
    }
    
	public synchronized void showBizDomain(String side, final BreakpointEvent event) throws Exception {
		StackFrame frame = event.thread().frame(0);
		ObjectReference obr = frame.thisObject();
		ReferenceType type = (ReferenceType) obr.type();
		Field bizdomain = type.fieldByName("bizdomain");
		Field value = type.fieldByName("default_value");
		Field desc = type.fieldByName("description");
		
		bizDomainOut.append(" -- Name: ").append(obr.getValue(bizdomain)).append("  Value: ").append(
			obr.getValue(value)).append("  Description: ").append(obr.getValue(desc)).append("\r\n");
	}

    public abstract void handleMethodEntryEvent(final MethodEntryEvent event);
    
    public abstract void handleMethodExitEvent(final MethodExitEvent event);
    
    public abstract void handleClassPrepareEvent(final ClassPrepareEvent event);
    
	public abstract void handleBreakPointEvent(final BreakpointEvent event) throws Exception;
	
	   public static String getFieldStrValue(ObjectReference obr, String fieldName) {
	    	ReferenceType type = (ReferenceType) obr.referenceType();
	    	Field f = type.fieldByName(fieldName);
	    	Value v = obr.getValue(f);
	    	if (v != null) {
	    		return v.toString();
	    	}
	    	else return null;
	    }
	    
	    public static Value getFieldValue(ObjectReference obr, String fieldName) {
	    	ReferenceType type = (ReferenceType) obr.referenceType();
	    	Field f = type.fieldByName(fieldName);
	    	Value v = obr.getValue(f);
	    	return v;
	    }
	    
	    public static String getFieldValueOfObjectVarialbe(StackFrame frame, String varName, String fieldName) {
	    	try
			{
				LocalVariable var = frame.visibleVariableByName(varName);
				ObjectReference v = (ObjectReference) frame.getValue(var);
				return getFieldStrValue(v, fieldName);
			}
			catch (AbsentInformationException e)
			{
				return null;
			}
	    }	
}
