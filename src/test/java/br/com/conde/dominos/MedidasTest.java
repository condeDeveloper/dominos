package br.com.conde.dominos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Os numeros que aparecem escritos no README, travados em teste. */
class MedidasTest {

    @Test
    @DisplayName("O TABULEIRO DE XADREZ tem 12.988.816 coberturas")
    void dozeMilhoes() {
        assertEquals(12_988_816L, Medidas.correcao(8, 8).coberturas());
    }

    @Test
    @DisplayName("e os outros tamanhos conhecidos: 36, 6.728")
    void outrosTamanhos() {
        assertEquals(36L, Medidas.correcao(4, 4).coberturas());
        assertEquals(6728L, Medidas.correcao(6, 6).coberturas());
        assertEquals(11L, Medidas.correcao(3, 4).coberturas());
    }

    @Test
    @DisplayName("a faixa de duas linhas bate com fibonacci em todo n")
    void fibonacci() {
        for (Medidas.Faixa f : Medidas.fibonacci())
            assertTrue(f.bate(), "n = " + f.n());

        /* O indice 19 da lista e n = 20. */
        assertEquals(20, Medidas.fibonacci().get(19).n());
        assertEquals(10946L, Medidas.fibonacci().get(19).coberturas());
    }

    @Test
    @DisplayName("O MUTILADO NAO SE COBRE, em nenhum tamanho")
    void mutiladoNaoCobre() {
        for (Medidas.Mutilado m : Medidas.mutilado()) {
            assertEquals(0L, m.semDoisCantos(), "lado " + m.lado());

            assertTrue(m.cheio() > 0, "lado " + m.lado());

            /* Duas casas da mesma cor a menos: a diferenca e sempre 2. */
            assertEquals(2, Math.abs(m.claras() - m.escuras()), "lado " + m.lado());
        }

        assertEquals(30, Medidas.mutilado(8).claras());
        assertEquals(32, Medidas.mutilado(8).escuras());
    }

    @Test
    @DisplayName("e a busca gasta milhares de passos para descobrir o que as cores dizem")
    void buscaPena() {
        assertTrue(Medidas.passosDaBuscaNoMutilado() > 1000,
                "foram " + Medidas.passosDaBuscaNoMutilado());
    }

    @Test
    @DisplayName("COM DUAS TIRADAS todos os 64 casos se cobrem, que e o teorema de Gomory")
    void gomory() {
        Medidas.Remocao duas = Medidas.contagem(2);

        assertEquals(64, duas.casos());
        assertEquals(64, duas.cobrem());
        assertEquals(0, duas.naoCobrem());
    }

    @Test
    @DisplayName("E COM QUATRO a cobertura some em 80 dos 784")
    void comQuatroJaFalha() {
        Medidas.Remocao quatro = Medidas.contagem(4);

        assertEquals(784, quatro.casos());
        assertEquals(704, quatro.cobrem());
        assertEquals(80, quatro.naoCobrem());

        assertEquals(0.102, quatro.fracaoQueNaoCobre(), 0.001);
    }

    @Test
    @DisplayName("o argumento das cores explica 73,5% dos que nao se cobrem")
    void oQueAsCoresExplicam() {
        Medidas.Cores c = Medidas.cores();

        assertEquals(32_768, c.casos());
        assertEquals(27_068, c.semCoberturaAoTodo());
        assertEquals(19_898, c.coresDesequilibradas());
        assertEquals(7_170, c.semCoberturaComCoresOk());

        assertEquals(0.735, Medidas.oQueAsCoresExplicam(), 0.001);
    }

    @Test
    @DisplayName("e as duas partes somam o total, sem sobra")
    void partesSomam() {
        Medidas.Cores c = Medidas.cores();

        assertEquals(c.semCoberturaAoTodo(),
                c.coresDesequilibradas() + c.semCoberturaComCoresOk());
    }
}
