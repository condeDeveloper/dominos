package br.com.conde.dominos;

/**
 * A BUSCA POR COBERTURAS, e as duas respostas que ela pode dar.
 *
 * <p>O algoritmo é o mesmo de sempre: ache a primeira casa vazia, tente cobrir ela com um
 * dominó deitado e com um em pé, e continue. Como a primeira casa vazia tem de ser coberta de
 * algum jeito, não há escolha a perder, e a busca é completa.
 *
 * <p>Completa é a palavra importante. Quando ela diz "não existe cobertura", isso é um fato
 * demonstrado por exaustão, e não uma desistência. A terceira medida usa isso.
 */
public final class Cobertura {

    private Cobertura() {}

    /** Quantas vezes a busca pôs um dominó, que é o trabalho de verdade. */
    public static long tentativas;

    public static void zerar() {
        tentativas = 0;
    }

    /** Quantas coberturas diferentes existem. */
    public static long quantas(Tabuleiro tabuleiro) {
        return contar(tabuleiro.presentes(), tabuleiro);
    }

    private static long contar(long vazias, Tabuleiro tabuleiro) {
        if (vazias == 0) return 1;

        /* A primeira casa vazia TEM de ser coberta, entao nao ha escolha a perder em comecar
         * por ela. E isso que torna a busca completa sem precisar de inteligencia nenhuma. */
        int primeira = Long.numberOfTrailingZeros(vazias);

        int linha = primeira / tabuleiro.colunas();
        int coluna = primeira % tabuleiro.colunas();

        long total = 0;

        /* Deitado: esta casa e a da direita. */
        if (coluna + 1 < tabuleiro.colunas()) {
            int direita = tabuleiro.casa(linha, coluna + 1);

            if ((vazias & (1L << direita)) != 0) {
                tentativas++;

                total += contar(vazias & ~(1L << primeira) & ~(1L << direita), tabuleiro);
            }
        }

        /* Em pe: esta casa e a de baixo. */
        if (linha + 1 < tabuleiro.linhas()) {
            int baixo = tabuleiro.casa(linha + 1, coluna);

            if ((vazias & (1L << baixo)) != 0) {
                tentativas++;

                total += contar(vazias & ~(1L << primeira) & ~(1L << baixo), tabuleiro);
            }
        }

        return total;
    }

    /** Existe pelo menos uma cobertura. */
    public static boolean daParaCobrir(Tabuleiro tabuleiro) {
        return quantas(tabuleiro) > 0;
    }

    /**
     * A PROVA POR CONTAGEM DE CORES.
     *
     * <p>Todo dominó cobre uma casa clara e uma escura, porque casas vizinhas têm cores
     * diferentes. Então uma cobertura existe só se houver o mesmo número de claras e escuras.
     *
     * <p>Isso é uma condição NECESSÁRIA e não suficiente, e a quinta medida mede a diferença
     * entre as duas coisas: há tabuleiros com cores equilibradas que mesmo assim não se cobrem.
     */
    public static boolean coresPermitem(Tabuleiro tabuleiro) {
        int[] cores = tabuleiro.contarPorCor();

        return cores[0] == cores[1];
    }

    /**
     * A FÓRMULA PARA FAIXAS DE DUAS LINHAS, que é Fibonacci.
     *
     * <p>Numa faixa 2 por n, a primeira coluna é coberta por um dominó em pé, sobrando 2 por
     * n-1, ou por dois deitados, sobrando 2 por n-2. É a recorrência de Fibonacci, e ela
     * aparece aqui sem ninguém ter posto.
     */
    public static long fibonacci(int n) {
        long anterior = 1;
        long atual = 1;

        for (int passo = 2; passo <= n; passo++) {
            long proximo = atual + anterior;

            anterior = atual;
            atual = proximo;
        }

        return atual;
    }
}
