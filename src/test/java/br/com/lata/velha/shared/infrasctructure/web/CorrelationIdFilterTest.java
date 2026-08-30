package br.com.lata.velha.shared.infrasctructure.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    @DisplayName("deve gerar um novo correlation id quando o header nao for informado")
    void shouldGenerateCorrelationIdWhenHeaderAbsent() throws Exception {
        when(request.getHeader(CorrelationIdFilter.HEADER_NAME)).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader(eq(CorrelationIdFilter.HEADER_NAME), argThat(id -> id != null && !id.isBlank()));
    }

    @Test
    @DisplayName("deve reaproveitar o correlation id recebido no header")
    void shouldReuseIncomingCorrelationId() throws Exception {
        when(request.getHeader(CorrelationIdFilter.HEADER_NAME)).thenReturn("incoming-id-123");

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader(CorrelationIdFilter.HEADER_NAME, "incoming-id-123");
    }

    @Test
    @DisplayName("deve popular o MDC durante a cadeia de filtros")
    void shouldPopulateMdcDuringFilterChain() throws Exception {
        when(request.getHeader(CorrelationIdFilter.HEADER_NAME)).thenReturn("mdc-check-id");
        doAnswer(invocation -> {
            assertEquals("mdc-check-id", MDC.get(CorrelationIdFilter.MDC_KEY));
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("deve limpar o MDC apos a cadeia de filtros, mesmo em caso de excecao")
    void shouldClearMdcAfterFilterChainEvenOnException() throws Exception {
        when(request.getHeader(CorrelationIdFilter.HEADER_NAME)).thenReturn("cleanup-id");
        doThrow(new RuntimeException("boom")).when(filterChain).doFilter(request, response);

        assertThrows(RuntimeException.class, () -> filter.doFilter(request, response, filterChain));

        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
    }
}
