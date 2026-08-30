package br.com.lata.velha.shared.infrasctructure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LevelHighlightConverter extends ClassicConverter {

    private static final String ESC = "";
    private static final String RESET = ESC + "[0;39m";

    @Override
    public String convert(ILoggingEvent event) {
        String color = colorFor(event.getLevel());
        String levelStr = event.getLevel().toString();
        if (color == null) {
            return levelStr;
        }
        return color + levelStr + RESET;
    }

    private String colorFor(Level level) {
        switch (level.toInt()) {
            case Level.ERROR_INT:
                return ESC + "[31m";
            case Level.WARN_INT:
                return ESC + "[33m";
            case Level.DEBUG_INT:
                return ESC + "[38;5;135m";
            case Level.TRACE_INT:
                return ESC + "[38;5;208m";
            default:
                return null;
        }
    }
}
