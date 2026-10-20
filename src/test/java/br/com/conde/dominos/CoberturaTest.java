package br.com.conde.dominos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A busca e o argumento das cores.
 *
 * <p>Uma busca por cobertura que começasse numa casa qualquer, e não na primeira vazia,
 * contaria a mesma cobertura várias vezes e daria um número maior sem errar nenhum caso
 * individual. Por isso os valores conhecidos importam: eles pegam esse erro.
 */
class CoberturaTest {

    @Test
    @DisplayName("os casos que se faz de cabeca")
    void deCabeca() {
        assertEquals(1, Cobertura.quantas(Tabuleiro.cheio(1, 2)));
        assertEquals(0, Cobertura.quantas(Tabuleiro.cheio(1, 3)));
        assertEquals(2, Cobertura.quantas(Tabuleiro.cheio(2, 2)));
        assertEquals(3, Cobertura.quantas(Tabuleiro.cheio(2, 3)));
        assertEquals(36, Cobertura.quantas(Tabuleiro.cheio(4, 4)));
    }

    @Test
    @DisplayName("tabuleiro vazio tem exatamente uma cobertura: a vazia")
    void vazio() {
        assertEquals(1, Cobertura.quantas(new Tabuleiro(4, 4, 0L)));
    }

    @Test
    @DisplayName("numero impar de casas nunca se cobre")
    void imparNuncaCobre() {
        for (int mascara = 0; mascara < (1 << 12); mascara++) {
            if (Integer.bitCount(mascara) % 2 == 0) continue;

            assertEquals(0, Cobertura.quantas(new Tabuleiro(3, 4, mascara)),
                    "mascara " + mascara);
        }
    }

    @Test
    @DisplayName("tabuleiro grande demais nao existe")
    void grandeDemais() {
        assertThrows(IllegalArgumentException.class, () -> Tabuleiro.cheio(9, 8));
    }

    @Test
    @DisplayName("O ARGUMENTO DAS CORES NUNCA FALHA: cores desequilibradas, zero coberturas")
    void coresSaoNecessarias() {
        for (int mascara = 0; mascara < (1 << 16); mascara++) {
            Tabuleiro tabuleiro = new Tabuleiro(4, 4, mascara);

            if (Cobertura.coresPermitem(tabuleiro)) continue;

            assertEquals(0, Cobertura.quantas(tabuleiro),
                    "mascara " + mascara + " cobriu com cores desequilibradas");
        }
    }

    @Test
    @DisplayName("mas nao bastam: ha equilibrado que nao se cobre")
    void coresNaoBastam() {
        /* Quatro casas tiradas de um 4x4, duas de cada cor, e mesmo assim sem cobertura:
         * o canto de cima a esquerda fica isolado. */
        Tabuleiro ilhado = Tabuleiro.cheio(4, 4).sem(0, 1).sem(1, 0).sem(1, 1).sem(2, 2);

        assertTrue(Cobertura.coresPermitem(ilhado), "as cores deviam estar equilibradas");

        assertEquals(0, Cobertura.quantas(ilhado), "e mesmo assim nao devia cobrir");
    }

    @Test
    @DisplayName("casas vizinhas tem cores diferentes, sempre")
    void vizinhasTemCoresDiferentes() {
        for (int linha = 0; linha < 8; linha++)
            for (int coluna = 0; coluna < 8; coluna++) {
                boolean minha = Tabuleiro.clara(linha, coluna);

                if (coluna + 1 < 8)
                    assertFalse(minha == Tabuleiro.clara(linha, coluna + 1),
                            linha + "," + coluna);

                if (linha + 1 < 8)
                    assertFalse(minha == Tabuleiro.clara(linha + 1, coluna),
                            linha + "," + coluna);
            }
    }

    @Test
    @DisplayName("a faixa de duas linhas bate com fibonacci")
    void fibonacci() {
        for (int n = 1; n <= 16; n++)
            assertEquals(Cobertura.fibonacci(n), Cobertura.quantas(Tabuleiro.cheio(2, n)),
                    "n = " + n);

        assertEquals(1, Cobertura.fibonacci(1));
        assertEquals(2, Cobertura.fibonacci(2));
        /* A contagem comeca em 1 e 2, entao fibonacci(n) daqui e o F(n+1) da numeracao
         * de sempre: fibonacci(16) e 1597, e nao 987. */
        assertEquals(1597, Cobertura.fibonacci(16));
    }

    @Test
    @DisplayName("tirar uma casa de cada cor do 4x4 sempre deixa cobertura")
    void gomoryNoQuatro() {
        for (int primeira = 0; primeira < 16; primeira++)
            for (int segunda = primeira + 1; segunda < 16; segunda++) {
                int l1 = primeira / 4;
                int c1 = primeira % 4;
                int l2 = segunda / 4;
                int c2 = segunda % 4;

                if (Tabuleiro.clara(l1, c1) == Tabuleiro.clara(l2, c2)) continue;

                Tabuleiro tabuleiro = Tabuleiro.cheio(4, 4).sem(l1, c1).sem(l2, c2);

                assertTrue(Cobertura.quantas(tabuleiro) > 0,
                        "tirando " + primeira + " e " + segunda);
            }
    }
}
