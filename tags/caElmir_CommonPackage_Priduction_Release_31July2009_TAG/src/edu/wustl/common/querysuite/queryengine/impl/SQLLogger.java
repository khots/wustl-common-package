package edu.wustl.common.querysuite.queryengine.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import edu.wustl.common.util.FileLogger;
import edu.wustl.common.util.ParseException;
import edu.wustl.common.util.SqlFormatter;

class SQLLogger extends FileLogger<String> {
    private static final Logger logger = Logger.getLogger(SQLLogger.class);
    
    SQLLogger() {
        super(false, false);
    }

    @Override
    protected String getLogFileExtension() {
        return "sql";
    }

    @Override
    protected String getBaseDir() {
        return System.getProperty("user.home") + "/sql_log";
    }

    @Override
    protected String getLogFileNamePrefix() {
        return "query";
    }
    
    @Override
    public boolean log(String t) throws IOException {
        try {
            t = new SqlFormatter(t).format();
        } catch (ParseException e) {
            logger.warn("error while formatting sql" + e.getStackTrace());
        }
        return super.log(t);
    }
}
