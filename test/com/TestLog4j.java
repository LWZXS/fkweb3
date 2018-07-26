package com;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

public class TestLog4j {
	static Logger logger = Logger.getLogger(TestLog4j.class.getName());//（2）
	public TestLog4j(){}
	public static void main(String[] args)
	{
	//同时输出到控制台和一个文件的实例并实现了Logger的继承
		PropertyConfigurator.configure("E:\\workspace2014\\fkweb\\src\\log4j.properties");
//	PropertyConfigurator.configure("E:\\log4j2.properties");
	logger.debug("Start of the main() in TestLog4j");
	logger.info("Just testing a log message with priority set to INFO");
	logger.warn("Just testing a log message with priority set to WARN");
	logger.error("Just testing a log message with priority set to ERROR");
	logger.fatal("Just testing a log message with priority set to FATAL");
	logger.log(Priority.WARN, "Testing a log message use a alternate form");
	logger.debug(TestLog4j.class.getName());
	TestLog4j2 testLog4j2 = new TestLog4j2();//（1）
	testLog4j2.testLog();
	}
}
