package ph.com.gs3.formalistics.service.synchronizers;

import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;

abstract class AbstractSynchronizer {

    protected final LoggingType loggingType;
    protected final String tag;

    AbstractSynchronizer(String tag, LoggingType loggingType) {
        this.tag = tag;
        this.loggingType = loggingType;
    }

    void log(String log) {
        if (loggingType == LoggingType.ENABLED) {
            FLLogger.d(tag, log);
        }
    }

}
