package br.com.conde.dominos;

/**
 * UM TABULEIRO com casas que existem e casas que foram tiradas.
 *
 * <p>As casas presentes ficam num {@code long}, um bit por casa, o que comporta exatamente o
 * tabuleiro de xadrez: 64 casas, 64 bits. Guardar assim permite tirar casas de qualquer lugar,
 * que é o que a terceira medida precisa.
 *
 * <p>Um dominó cobre duas casas vizinhas, deitado ou em pé. Cobrir o tabuleiro é encher todas
 * as casas presentes sem sobrepor nada.
 */
public record Tabuleiro(int linhas, int colunas, long presentes) {

    public Tabuleiro {
        if (linhas < 1 || colunas < 1 || linhas * colunas > 64)
            throw new IllegalArgumentException("tabuleiro de " + linhas + " por " + colunas);
    }

    /** O tabuleiro cheio, com todas as casas. */
    public static Tabuleiro cheio(int linhas, int colunas) {
        return new Tabuleiro(linhas, colunas, linhas * colunas == 64 ? -1L
                : (1L << (linhas * colunas)) - 1);
    }

    public int casa(int linha, int coluna) {
        return linha * colunas + coluna;
    }

    public boolean tem(int linha, int coluna) {
        if (linha < 0 || linha >= linhas || coluna < 0 || coluna >= colunas) return false;

        return (presentes & (1L << casa(linha, coluna))) != 0;
    }

    public Tabuleiro sem(int linha, int coluna) {
        return new Tabuleiro(linhas, colunas, presentes & ~(1L << casa(linha, coluna)));
    }

    public int quantasCasas() {
        return Long.bitCount(presentes);
    }

    /**
     * A COR DA CASA, como num tabuleiro de xadrez.
     *
     * <p>Casas vizinhas têm cores diferentes, sempre. Essa é a única coisa que se precisa
     * saber para provar a quinta medida, e ela não aparece em lugar nenhum do código que
     * procura cobertura.
     */
    public static boolean clara(int linha, int coluna) {
        return (linha + coluna) % 2 == 0;
    }

    /** Quantas casas claras e quantas escuras o tabuleiro tem. */
    public int[] contarPorCor() {
        int claras = 0;
        int escuras = 0;

        for (int linha = 0; linha < linhas; linha++)
            for (int coluna = 0; coluna < colunas; coluna++) {
                if (!tem(linha, coluna)) continue;

                if (clara(linha, coluna)) claras++;
                else escuras++;
            }

        return new int[] {claras, escuras};
    }

    public String mostrar() {
        StringBuilder saida = new StringBuilder();

        for (int linha = 0; linha < linhas; linha++) {
            for (int coluna = 0; coluna < colunas; coluna++)
                saida.append(tem(linha, coluna) ? (clara(linha, coluna) ? '.' : '#') : ' ');

            saida.append('\n');
        }

        return saida.toString();
    }
}
