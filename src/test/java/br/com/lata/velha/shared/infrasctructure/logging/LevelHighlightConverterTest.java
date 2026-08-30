package br.com.lata.velha.shared.infrasctructure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LevelHighlightConverterTest {

    private static final String ESC = "";
    private static final String RESET = ESC + "[0;39m";

    @Mock
    private ILoggingEvent event;

    private final LevelHighlightConverter converter = new LevelHighlightConverter();

    @Test
    @DisplayName("deve retornar INFO sem nenhuma cor")
    void shouldReturnInfoWithoutColor() {
        when(event.getLevel()).thenReturn(Level.INFO);

        assertEquals("INFO", converter.convert(event));
    }

    @Test
    @DisplayName("deve retornar ERROR em vermelho")
    void shouldReturnErrorInRed() {
        when(event.getLevel()).thenReturn(Level.ERROR);

        assertEquals(ESC + "[31mERROR" + RESET, converter.convert(event));
    }

    @Test
    @DisplayName("deve retornar WARN em amarelo")
    void shouldReturnWarnInYellow() {
        when(event.getLevel()).thenReturn(Level.WARN);

        assertEquals(ESC + "[33mWARN" + RESET, converter.convert(event));
    }

    @Test
    @DisplayName("deve retornar DEBUG em roxo (256-color)")
    void shouldReturnDebugInPurple() {
        when(event.getLevel()).thenReturn(Level.DEBUG);

        assertEquals(ESC + "[38;5;135mDEBUG" + RESET, converter.convert(event));
    }

    @Test
    @DisplayName("deve retornar TRACE em laranja (256-color)")
    void shouldReturnTraceInOrange() {
        when(event.getLevel()).thenReturn(Level.TRACE);

        assertEquals(ESC + "[38;5;208mTRACE" + RESET, converter.convert(event));
    }
}
