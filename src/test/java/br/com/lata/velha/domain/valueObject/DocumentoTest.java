package br.com.lata.velha.domain.valueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentoTest {

    // ==================== CPF ====================

    @Nested
    @DisplayName("CPF")
    class CpfTests {

        @Test
        @DisplayName("deve criar CPF válido")
        void deveCriarCpfValido() {
            Documento doc = Documento.of("52998224725");

            assertEquals("52998224725", doc.getValor());
            assertEquals(Documento.Tipo.CPF, doc.getTipo());
        }

        @Test
        @DisplayName("deve criar CPF válido com formatação")
        void deveCriarCpfComFormatacao() {
            Documento doc = Documento.of("529.982.247-25");

            assertEquals("52998224725", doc.getValor());
            assertEquals(Documento.Tipo.CPF, doc.getTipo());
        }

        @Test
        @DisplayName("deve formatar CPF corretamente")
        void deveFormatarCpf() {
            Documento doc = Documento.of("52998224725");

            assertEquals("529.982.247-25", doc.getFormatado());
        }

        @Test
        @DisplayName("deve rejeitar CPF com dígitos iguais")
        void deveRejeitarCpfDigitosIguais() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("11111111111"));
        }

        @Test
        @DisplayName("deve rejeitar CPF com dígito verificador inválido")
        void deveRejeitarCpfDigitoInvalido() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("52998224700"));
        }

        @Test
        @DisplayName("deve rejeitar CPF com menos de 11 dígitos")
        void deveRejeitarCpfCurto() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("1234567890"));
        }
    }

    // ==================== CNPJ ====================

    @Nested
    @DisplayName("CNPJ")
    class CnpjTests {

        @Test
        @DisplayName("deve criar CNPJ numérico válido (formato antigo)")
        void deveCriarCnpjNumericoValido() {
            Documento doc = Documento.of("11222333000181");

            assertEquals("11222333000181", doc.getValor());
            assertEquals(Documento.Tipo.CNPJ, doc.getTipo());
        }

        @Test
        @DisplayName("deve criar CNPJ numérico com formatação")
        void deveCriarCnpjComFormatacao() {
            Documento doc = Documento.of("11.222.333/0001-81");

            assertEquals("11222333000181", doc.getValor());
        }

        @Test
        @DisplayName("deve formatar CNPJ corretamente")
        void deveFormatarCnpj() {
            Documento doc = Documento.of("11222333000181");

            assertEquals("11.222.333/0001-81", doc.getFormatado());
        }

        @Test
        @DisplayName("deve criar CNPJ alfanumérico (formato novo)")
        void deveCriarCnpjAlfanumerico() {
            Documento doc = Documento.of("AB1CD234EF5G67");

            assertEquals("AB1CD234EF5G67", doc.getValor());
            assertEquals(Documento.Tipo.CNPJ, doc.getTipo());
        }

        @Test
        @DisplayName("deve formatar CNPJ alfanumérico corretamente")
        void deveFormatarCnpjAlfanumerico() {
            Documento doc = Documento.of("AB1CD234EF5G67");

            assertEquals("AB.1CD.234/EF5G-67", doc.getFormatado());
        }

        @Test
        @DisplayName("deve converter CNPJ alfanumérico para maiúsculas")
        void deveConverterParaMaiusculas() {
            Documento doc = Documento.of("ab1cd234ef5g67");

            assertEquals("AB1CD234EF5G67", doc.getValor());
        }

        @Test
        @DisplayName("deve rejeitar CNPJ numérico com dígitos iguais")
        void deveRejeitarCnpjDigitosIguais() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("11111111111111"));
        }

        @Test
        @DisplayName("deve rejeitar CNPJ numérico com dígito verificador inválido")
        void deveRejeitarCnpjDigitoInvalido() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("11222333000100"));
        }
    }

    // ==================== GERAL ====================

    @Nested
    @DisplayName("Validações gerais")
    class ValidacoesGerais {

        @Test
        @DisplayName("deve rejeitar documento nulo")
        void deveRejeitarNulo() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of(null));
        }

        @Test
        @DisplayName("deve rejeitar documento vazio")
        void deveRejeitarVazio() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of(""));
        }

        @Test
        @DisplayName("deve rejeitar documento com tamanho inválido")
        void deveRejeitarTamanhoInvalido() {
            assertThrows(IllegalArgumentException.class, () -> Documento.of("123456"));
        }

        @Test
        @DisplayName("documentos iguais devem ser equals")
        void documentosIguaisDevemSerEquals() {
            Documento doc1 = Documento.of("52998224725");
            Documento doc2 = Documento.of("529.982.247-25");

            assertEquals(doc1, doc2);
            assertEquals(doc1.hashCode(), doc2.hashCode());
        }

        @Test
        @DisplayName("documentos diferentes não devem ser equals")
        void documentosDiferentesNaoDevemSerEquals() {
            Documento cpf = Documento.of("52998224725");
            Documento cnpj = Documento.of("11222333000181");

            assertNotEquals(cpf, cnpj);
        }

        @Test
        @DisplayName("toString deve retornar formatado")
        void toStringDeveRetornarFormatado() {
            Documento doc = Documento.of("52998224725");

            assertEquals("529.982.247-25", doc.toString());
        }
    }
}