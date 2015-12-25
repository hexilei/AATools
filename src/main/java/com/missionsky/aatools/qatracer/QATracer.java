package com.missionsky.aatools.qatracer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: Debuger.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2009
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall Debuger.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  May 26, 2009			john.huang				Initial.
 *  
 * </pre>
 */
public class QATracer
{
	public static StringBuffer OUTPUT = new StringBuffer();
	
	private static VirtualMachine vmBiz;
	private static VirtualMachine vmWeb;
	private static WebEventHandler handlerWeb;
	private static BizEventHandler handlerBiz;
	
	/** 6.7.0
	// monitor jdbc classes
	private final static String[] SQL_CLASSES = {
		"com.accela.aa.datautil.DBAccessor",
		"com.accela.db.DBAccessor", 
		"com.accela.queryengine.jdbc.JdbcUtil"};
	public static ArrayList<String> UNLOADED_CLASSES = new ArrayList<String>();
	private final static HashMap<String, int[]> SQL_POINTS = new HashMap<String, int[]>();
	
	// monitor BusinessHelper
	public final static String BIZ_HELPER = "com.accela.av360.framework.util.BusinessHelper$WrapperHandler";
	public final static int BIZ_HELPER_POINT = 140; 

	static {
		SQL_POINTS.put(SQL_CLASSES[0], new int[] {684, 751, 1030, 1060});
		SQL_POINTS.put(SQL_CLASSES[1], new int[] {216, 276, 395, 542, 632});
		SQL_POINTS.put(SQL_CLASSES[2], new int[] {433, 439});
	}
	
	// monitor request attributes
	public final static String ACCELA_REQUEST = "com.accela.security.filters.AccelaHttpServletRequest";
	public final static int ACCELA_REQUEST_POINT = 78;
	
	// monitor session attributes
	public final static String ACCELA_SESSION = "com.accela.security.filters.ModuleSession";
	public final static int ACCELA_SESSION_SETATTRIBUTE_POINT = 289;
	public final static int ACCELA_SESSION_GETATTRIBUTE_POINT = 174;
	
	// monitor MenuBarUtil FID
	public final static String MENUBARUTIL = "com.accela.av360.framework.web.MenuBarUtil";
	public final static int MENUBARUTIL_POINT = 872;
	
	// monitor TabBarUtil FID
	public final static String TABBARUTIL = "com.accela.av360.framework.web.TabBarUtil";
	public final static int TABBARUTIL_POINT = 475;
	
	// monitor HttpJSPBase
	public final static String HTTP_JSP_BASE = "org.apache.jasper.runtime.HttpJspBase";
	public final static int JSP_IN = 70;
	public final static int JSP_OUT = 71; 
	
	// monitor bizmodel
	public final static String BIZDOMINA_MODEL = "com.accela.aa.aamain.systemConfig.BizDomainModel";
	public final static int GET_BIZDOMAIN_VALUE = 177;
	
	//*/
	
	//* AA7.0.0
	// monitor jdbc classes
	private final static String[] SQL_CLASSES = {
		"com.accela.aa.datautil.DBAccessor",
		"com.accela.db.DBAccessor", 
		"com.accela.queryengine.jdbc.JdbcUtil"};
	public static ArrayList<String> UNLOADED_CLASSES = new ArrayList<String>();
	private final static HashMap<String, int[]> SQL_POINTS = new HashMap<String, int[]>();
	
	// monitor BusinessHelper
	public final static String BIZ_HELPER = "com.accela.aa.util.BusinessHelper$WrapperHandler";
	public final static int BIZ_HELPER_POINT = 133; 

	static {
		SQL_POINTS.put(SQL_CLASSES[0], new int[] {866, 938, 1220, 1252});
		SQL_POINTS.put(SQL_CLASSES[1], new int[] {216, 276, 395, 542});
		SQL_POINTS.put(SQL_CLASSES[2], new int[] {416, 446});
	}
	
	// monitor MenuBarUtil FID
	public final static String MENUBARUTIL = "com.accela.av360.framework.web.MenuBarUtil";
	public final static int MENUBARUTIL_POINT = 734;//956;
	
	// monitor TabBarUtil FID
	public final static String TABBARUTIL = "com.accela.av360.framework.web.TabBarUtil";
	public final static int TABBARUTIL_POINT = 694;
	
	// monitor HttpJSPBase
	public final static String HTTP_JSP_BASE = "org.apache.jasper.runtime.HttpJspBase";
	public final static int JSP_IN = 70;
	public final static int JSP_OUT = 71; 
	
	// monitor bizmodel
	public final static String BIZDOMINA_MODEL = "com.accela.aa.aamain.systemConfig.BizDomainModel";
	public final static int GET_BIZDOMAIN_VALUE = 177;
	
	// monitor FIDManager
	public final static String FID_MANAGER = "com.accela.av360.framework.security.FIDManager";
	public final static int FID_MANAGER_OUT1 = 72;
	public final static int FID_MANAGER_OUT2 = 89;
	//*/
	
	public final static String[] TABLE_FILTERS = {
		" FROM BATCH_JOB",
		" FROM GUI_TEXT",
		" FROM PUSER_PLAN",
		"UPDATE BATCH_JOB ",
	};
	
	public final static String[] SQL_FILTERS = {
//		"UPDATE",
//		"DELETE",
		//"SELECT"
	};
	
	public final static String[] BIZ_FILTERS = {
		"monitorServiceRequests",
		"getFeatureRecordsCount",
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		start();
	}
	
	public static boolean start() {
		VirtualMachineManager mgr = Bootstrap.virtualMachineManager();
        List connectors = mgr.attachingConnectors();
        AttachingConnector connector = (AttachingConnector)connectors.get(0);

        OUTPUT = OUTPUT.delete(0, OUTPUT.length());
        try
		{
			vmWeb = attachVM(connector, 8788); // web server
			System.out.println("Attached " + vmWeb.canBeModified());
			
			vmBiz = attachVM(connector, 8787); // biz server
			System.out.println("Attached " + vmBiz.canBeModified());
			
			// add breakpoint for web
			addBreakPointsForWeb(vmWeb);
			addBreakPointsForBizDomain(vmWeb);
			
			// add breakpoint for biz
			addBreakPointsForBizDomain(vmBiz);
			
			// add method entry event
			addMethodEntry(vmWeb);
			
			handlerWeb = new WebEventHandler(vmWeb);
			handlerBiz = new BizEventHandler(vmBiz);
			handlerWeb.start();
			handlerBiz.start();
			
			return true;
		}
        catch (IndexOutOfBoundsException e1) {
        	e1.printStackTrace();
        	OUTPUT.append(e1.getMessage()).append("\r\n");
        	OUTPUT.append("\r\n=====Please login V360 at first!=====\r\n");
        	return false;
        }
		catch (Exception e)
		{
			e.printStackTrace();
			OUTPUT.append(e.getMessage());
			return false;
		}
	}
	
	public static void stop() {
		try
		{
			if (handlerBiz != null) handlerBiz.shutdown();
			if (handlerWeb != null) handlerWeb.shutdown();
			
		}
		catch (RuntimeException e)
		{
		}
		if (vmBiz != null) vmBiz.dispose();
		if (vmWeb != null) vmWeb.dispose();
	}
	
	private static VirtualMachine attachVM(AttachingConnector connector, int port) throws Exception {
		Map<String, Connector.Argument> arguments = connector.defaultArguments();
        (arguments.get("port")).setValue(Integer.toString(port));
        (arguments.get("hostname")).setValue("192.168.0.148");
        VirtualMachine vm = connector.attach(arguments);
        return vm;
	}
	
	public static void addMethodEntry(VirtualMachine vm) throws Exception {
		EventRequestManager erm = vm.eventRequestManager();
		MethodEntryRequest mer = erm.createMethodEntryRequest();
		ReferenceType type = vm.classesByName("org.apache.jasper.runtime.HttpJspBase").get(0);
		mer.addClassFilter(type);
		mer.addClassExclusionFilter("org.apache.jasper.runtime.HttpJspBase");
//		mer.enable(); // will enable later
	}
	
	public static void addBreakPointsForWeb(VirtualMachine vm) throws Exception {
		EventRequestManager erm = vm.eventRequestManager();
		
		ReferenceType rt;
		Location location;
		BreakpointRequest bpr;
		
		// when check menu item FID
		List<ReferenceType> clses = vm.classesByName(MENUBARUTIL);
		if (clses.size() > 0) {
			rt = clses.get(0);
			location = rt.locationsOfLine(MENUBARUTIL_POINT).get(0);
			bpr = erm.createBreakpointRequest(location);
			bpr.enable();
			System.out.println(rt.name() + ".convertMenuItem() hooked.");
		}
		
		// when check tab item FID
		clses = vm.classesByName(TABBARUTIL);
		if (clses.size() > 0) {
			rt = clses.get(0);
			location = rt.locationsOfLine(TABBARUTIL_POINT).get(0);
			bpr = erm.createBreakpointRequest(location);
			bpr.enable();
			System.out.println(rt.name() + ".createTabBarItem() hooked.");
		}
		
		// Add in and out for HTTPBASE
		rt = vm.classesByName(HTTP_JSP_BASE).get(0);
		location = rt.locationsOfLine(JSP_IN).get(0);
		bpr = erm.createBreakpointRequest(location);
		bpr.enable();
		System.out.println(rt.name() + " service() in hooked.");
		
		// Add in and out for HTTPBASE
		rt = vm.classesByName(HTTP_JSP_BASE).get(0);
		location = rt.locationsOfLine(JSP_OUT).get(0);
		bpr = erm.createBreakpointRequest(location);
		bpr.enable();
		System.out.println(rt.name() + " service() out hooked.");
		
		// Add FIDManager out
		rt = vm.classesByName(FID_MANAGER).get(0);
		location = rt.locationsOfLine(FID_MANAGER_OUT1).get(0);
		bpr = erm.createBreakpointRequest(location);
		bpr.enable();
		System.out.println(rt.name() + ".getFIDFromSystem() out1 hooked.");
		
		rt = vm.classesByName(FID_MANAGER).get(0);
		location = rt.locationsOfLine(FID_MANAGER_OUT2).get(0);
		bpr = erm.createBreakpointRequest(location);
		bpr.enable();
		System.out.println(rt.name() + ".getFIDFromSystem() out2 hooked.");
	}
	
	public static void addBreakPointsForBizDomain(VirtualMachine vm) throws Exception {
		EventRequestManager erm = vm.eventRequestManager();
		
		// when get bizdomain value
		ReferenceType rt = vm.classesByName(BIZDOMINA_MODEL).get(0);
		Location location = rt.locationsOfLine(GET_BIZDOMAIN_VALUE).get(0);
		BreakpointRequest bpr = erm.createBreakpointRequest(location);
		bpr.enable();
		System.out.println(rt.name() + ".getBizDoaminValue() hooked.");
	}
	
	public static void addPointsForRefType(EventRequestManager erm, ReferenceType refType)
	{
		int points[] = SQL_POINTS.get(refType.name());
		Location location;
		BreakpointRequest bpr;
		try
		{
			for (int i = 0; i < points.length; i++) {
				location = refType.locationsOfLine(points[i]).get(0);
				bpr = erm.createBreakpointRequest(location);
				bpr.enable();
			}
			System.out.println(refType.name() + " hooked.");
		}
		catch (AbsentInformationException e)
		{
			e.printStackTrace();
		}
	}
}

/*
*$Log: av-env.bat,v $
*/