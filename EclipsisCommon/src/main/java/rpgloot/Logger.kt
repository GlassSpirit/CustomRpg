package rpgloot

import org.apache.logging.log4j.Level

class Logger(val logger: org.apache.logging.log4j.Logger) {

    fun log(logLevel: Level, msg: Any) {
        logger.log(logLevel, msg)
    }

    fun warn(msg: Any) {
        log(Level.WARN, msg)
    }

    fun info(msg: Any) {
        log(Level.INFO, msg)
    }

    fun error(msg: Any) {
        log(Level.ERROR, msg)
    }
}
