package br.com.conde.dominos;

import java.util.ArrayList;
import java.util.List;

/**
 * AS MEDIDAS: coberturas contadas por busca completa, e o argumento das cores medido no limite
 * em que ele deixa de bastar.
 */
public final class Medidas {

    private Medidas() {}

    /* ===================== 1. correcao ===================== */

    public record Correcao(int linhas, int colunas, int casas, long coberturas,
            boolean coresPermitem) {}

    private static List<Correcao> correcao;

    /**
     * AS COBERTURAS DOS TABULEIROS CHEIOS.
     *
     * <p>A busca é completa: ela pega a primeira casa vazia e tenta as duas únicas maneiras de
     * cobri-la. Como essa casa precisa ser coberta de algum jeito, não há escolha a perder, e
     * quando ela diz zero isso é exaustão e não desistência.
     *
     * <p>O tabuleiro de xadrez cheio tem 12.988.816 coberturas, que é um número conhecido
     * desde 1961 e serve de testemunha.
     */
    public static List<Correcao> correcao() {
        if (correcao != null) return correcao;

        List<Correcao> saida = new ArrayList<>();

        for (int[] grade : new int[][] {{1, 2}, {2, 2}, {2, 3}, {3, 4}, {4, 4}, {5, 6},
            {6, 6}, {8, 8}}) {
            Tabuleiro tabuleiro = Tabuleiro.cheio(grade[0], grade[1]);

            saida.add(new Correcao(grade[0], grade[1], tabuleiro.quantasCasas(),
                    Cobertura.quantas(tabuleiro), Cobertura.coresPermitem(tabuleiro)));
        }

        return correcao = List.copyOf(saida);
    }

    public static Correcao correcao(int linhas, int colunas) {
        return correcao().stream()
                .filter(x -> x.linhas() == linhas && x.colunas() == colunas)
                .findFirst().orElseThrow();
    }

    /* ===================== 2. fibonacci ===================== */

    public record Faixa(int n, long coberturas, long fibonacci) {

        public boolean bate() {
            return coberturas == fibonacci;
        }
    }

    private static List<Faixa> fibonacci;

    /**
     * A FAIXA DE DUAS LINHAS É FIBONACCI, e ninguém pôs Fibonacci ali.
     *
     * <p>A primeira coluna de uma faixa 2 por n é coberta por um dominó em pé, sobrando 2 por
     * n-1, ou por dois deitados, sobrando 2 por n-2. A recorrência sai da geometria.
     *
     * <p>A busca não sabe disso: ela enumera coberturas. Os números baterem é o fato medido.
     */
    public static List<Faixa> fibonacci() {
        if (fibonacci != null) return fibonacci;

        List<Faixa> saida = new ArrayList<>();

        for (int n = 1; n <= 20; n++)
            saida.add(new Faixa(n, Cobertura.quantas(Tabuleiro.cheio(2, n)),
                    Cobertura.fibonacci(n)));

        return fibonacci = List.copyOf(saida);
    }

    /* ===================== 3. mutilado ===================== */

    public record Mutilado(int lado, long cheio, long semDoisCantos, int claras, int escuras) {}

    private static List<Mutilado> mutilado;

    /**
     * O TABULEIRO MUTILADO, que é o problema clássico.
     *
     * <p>Tire dois cantos OPOSTOS do tabuleiro de xadrez. Sobram 62 casas, que dariam 31
     * dominós certinhos. A busca completa percorre tudo e acha zero.
     *
     * <p>E o motivo cabe numa linha: cantos opostos têm a MESMA cor. Tirando dois da mesma
     * cor, sobram 30 de uma e 32 de outra, e todo dominó cobre uma de cada. A busca gasta
     * milhões de passos para descobrir o que a contagem de cores diz de imediato.
     */
    public static List<Mutilado> mutilado() {
        if (mutilado != null) return mutilado;

        List<Mutilado> saida = new ArrayList<>();

        for (int lado : new int[] {4, 6, 8}) {
            Tabuleiro cheio = Tabuleiro.cheio(lado, lado);

            Tabuleiro semDois = cheio.sem(0, 0).sem(lado - 1, lado - 1);

            int[] cores = semDois.contarPorCor();

            saida.add(new Mutilado(lado, Cobertura.quantas(cheio),
                    Cobertura.quantas(semDois), cores[0], cores[1]));
        }

        return mutilado = List.copyOf(saida);
    }

    public static Mutilado mutilado(int lado) {
        return mutilado().stream().filter(x -> x.lado() == lado).findFirst().orElseThrow();
    }

    /** Quantos passos a busca gasta para descobrir que o 8x8 mutilado não se cobre. */
    public static long passosDaBuscaNoMutilado() {
        Tabuleiro semDois = Tabuleiro.cheio(8, 8).sem(0, 0).sem(7, 7);

        Cobertura.zerar();

        Cobertura.quantas(semDois);

        return Cobertura.tentativas;
    }

    /* ===================== 4. contagem ===================== */

    public record Remocao(int quantasTiradas, int casos, int cobrem, int naoCobrem) {

        public double fracaoQueNaoCobre() {
            return casos == 0 ? 0 : naoCobrem / (double) casos;
        }
    }

    private static List<Remocao> contagem;

    /**
     * TIRANDO CASAS DE CORES DIFERENTES, num tabuleiro 4 por 4.
     *
     * <p>Com DUAS casas tiradas, uma de cada cor, todos os 64 casos se cobrem. Isso não é
     * acaso: é um teorema de Gomory, de 1973, e a varredura o confirma caso a caso.
     *
     * <p>Com QUATRO casas, o equilíbrio de cores continua valendo e a cobertura some em 80 dos
     * 784 casos. É aí que a condição necessária deixa de ser suficiente.
     */
    public static List<Remocao> contagem() {
        if (contagem != null) return contagem;

        List<Remocao> saida = new ArrayList<>();

        for (int tiradas : new int[] {2, 4}) {
            int casos = 0;
            int cobrem = 0;

            for (int mascara = 0; mascara < (1 << 16); mascara++) {
                if (Integer.bitCount(mascara) != 16 - tiradas) continue;

                Tabuleiro tabuleiro = new Tabuleiro(4, 4, mascara);

                if (!Cobertura.coresPermitem(tabuleiro)) continue;

                casos++;

                if (Cobertura.quantas(tabuleiro) > 0) cobrem++;
            }

            saida.add(new Remocao(tiradas, casos, cobrem, casos - cobrem));
        }

        return contagem = List.copyOf(saida);
    }

    public static Remocao contagem(int tiradas) {
        return contagem().stream().filter(x -> x.quantasTiradas() == tiradas)
                .findFirst().orElseThrow();
    }

    /* ===================== 5. cores ===================== */

    public record Cores(int casos, int coresDesequilibradas, int semCoberturaComCoresOk,
            int semCoberturaAoTodo) {}

    private static Cores cores;

    /**
     * O ARGUMENTO DAS CORES: até onde ele vai.
     *
     * <p>Ele é uma condição NECESSÁRIA, e a varredura confere isso: nenhum tabuleiro com cores
     * desequilibradas se cobre, em nenhum caso.
     *
     * <p>E não é suficiente, o que a mesma varredura também mostra. A tabela separa os dois
     * motivos de um tabuleiro não se cobrir: cores desequilibradas, que o argumento explica, e
     * cores equilibradas sem cobertura, que ele não explica.
     */
    public static Cores cores() {
        if (cores != null) return cores;

        int casos = 0;
        int desequilibradas = 0;
        int equilibradasSemCobertura = 0;
        int semCobertura = 0;

        for (int mascara = 0; mascara < (1 << 16); mascara++) {
            if (Integer.bitCount(mascara) % 2 != 0) continue;

            casos++;

            Tabuleiro tabuleiro = new Tabuleiro(4, 4, mascara);

            boolean cobre = Cobertura.quantas(tabuleiro) > 0;

            if (!cobre) semCobertura++;

            if (!Cobertura.coresPermitem(tabuleiro)) {
                desequilibradas++;

                /* Se este cobrisse, o argumento das cores estaria errado. */
                if (cobre) throw new IllegalStateException("cobriu com cores desequilibradas");

                continue;
            }

            if (!cobre) equilibradasSemCobertura++;
        }

        return cores = new Cores(casos, desequilibradas, equilibradasSemCobertura,
                semCobertura);
    }

    /** Dos que não se cobrem, qual fração o argumento das cores explica. */
    public static double oQueAsCoresExplicam() {
        Cores c = cores();

        return c.semCoberturaAoTodo() == 0 ? 0
                : c.coresDesequilibradas() / (double) c.semCoberturaAoTodo();
    }
}
