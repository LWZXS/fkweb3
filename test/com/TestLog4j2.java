package com;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

public class TestLog4j2 {
	static Logger logger = Logger.getLogger(TestLog4j2.class.getName());//（1）
	public TestLog4j2(){}
	public void testLog()
	{
	//同时输出到控制台和一个文件的实例
//	PropertyConfigurator.configure("E:\\log4j2.properties");
	PropertyConfigurator.configure("E:\\workspace2014\\fkweb\\src\\log4j.properties");
	logger.debug("2Start of the main()");
	logger.info("2Just testing a log message with priority set to INFO");
	logger.warn("2Just testing a log message with priority set to WARN");
	logger.error("2Just testing a log message with priority set to ERROR");
	logger.fatal("2Just testing a log message with priority set to FATAL");
	logger.log(Priority.DEBUG, "Testing a log message use a alternate form");
	logger.debug("2End of the main()");
	}
}
