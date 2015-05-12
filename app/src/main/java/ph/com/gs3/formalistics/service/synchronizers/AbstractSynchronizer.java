package ph.com.gs3.formalistics.service.synchronizers;

import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;

public abstract class AbstractSynchronizer {

    protected LoggingType loggingType;
    protected String tag;

    public AbstractSynchronizer(String tag, LoggingType loggingType) {
        this.tag = tag;
        this.loggingType = loggingType;
    }

    public void log(String log) {
        if (loggingType == LoggingType.ENABLED) {
            FLLogger.d(tag, log);
        }
    }

}
