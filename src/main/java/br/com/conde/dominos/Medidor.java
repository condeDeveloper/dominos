package br.com.conde.dominos;

import java.util.Locale;

/** O MEDIDOR: roda as buscas e imprime as tabelas do README. */
public final class Medidor {

    private Medidor() {}

    public static void main(String[] argumentos) {
        Locale.setDefault(Locale.US);

        String passo = argumentos.length > 0 ? argumentos[0] : "tudo";

        switch (passo) {
            case "correcao" -> correcao();
            case "fibonacci" -> fibonacci();
            case "mutilado" -> mutilado();
            case "contagem" -> contagem();
            case "cores" -> cores();
            default -> {
                correcao();
                fibonacci();
                mutilado();
                contagem();
                cores();
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("=".repeat(texto.length()));
        System.out.println();
    }

    static void correcao() {
        titulo("1. as coberturas dos tabuleiros cheios");

        System.out.println("tabuleiro  casas  coberturas   cores batem");

        for (Medidas.Correcao c : Medidas.correcao())
            System.out.printf("%5dx%-4d %6d  %10d  %12s%n", c.linhas(), c.colunas(), c.casas(),
                    c.coberturas(), c.coresPermitem() ? "sim" : "NAO");

        System.out.println();
        System.out.println("a busca e completa: ela pega a primeira casa vazia e tenta as duas");
        System.out.println("unicas maneiras de cobri-la. essa casa PRECISA ser coberta, entao");
        System.out.println("nao ha escolha a perder, e um zero aqui e exaustao, nao desistencia.");
        System.out.println();
        System.out.printf("o tabuleiro de xadrez cheio tem %d coberturas, numero conhecido%n",
                Medidas.correcao(8, 8).coberturas());
        System.out.println("desde 1961 e que serve de testemunha para a busca inteira.");
    }

    static void fibonacci() {
        titulo("2. a faixa de duas linhas e fibonacci");

        System.out.println("  n  coberturas  fibonacci");

        for (Medidas.Faixa f : Medidas.fibonacci())
            if (f.n() <= 10 || f.n() % 5 == 0)
                System.out.printf("%3d  %10d  %9d%s%n", f.n(), f.coberturas(), f.fibonacci(),
                        f.bate() ? "" : "   <<< DIFERE");

        System.out.println();
        System.out.println("a primeira coluna de uma faixa 2 por n e coberta por um domino em");
        System.out.println("pe, sobrando 2 por n-1, ou por dois deitados, sobrando 2 por n-2.");
        System.out.println("a recorrencia sai da geometria.");
        System.out.println();
        System.out.println("a busca nao sabe disso: ela enumera coberturas uma a uma. os numeros");
        System.out.println("baterem e o fato medido.");
    }

    static void mutilado() {
        titulo("3. o tabuleiro mutilado");

        System.out.println("lado  cheio        sem dois cantos  claras  escuras");

        for (Medidas.Mutilado m : Medidas.mutilado())
            System.out.printf("%4d  %-11d  %15d  %6d  %7d%n", m.lado(), m.cheio(),
                    m.semDoisCantos(), m.claras(), m.escuras());

        System.out.println();
        System.out.println("tire dois cantos OPOSTOS do tabuleiro de xadrez. sobram 62 casas,");
        System.out.println("que dariam 31 dominos certinhos. a busca completa acha ZERO.");
        System.out.println();
        System.out.printf("e ela gasta %d passos para descobrir isso.%n",
                Medidas.passosDaBuscaNoMutilado());
        System.out.println();
        System.out.println("o motivo cabe numa linha: cantos opostos tem a MESMA cor. tirando");
        System.out.println("dois da mesma cor sobram 30 de uma e 32 de outra, e todo domino");
        System.out.println("cobre uma de cada. a contagem diz de imediato o que a busca penou.");
    }

    static void contagem() {
        titulo("4. tirando casas de cores diferentes, num 4 por 4");

        System.out.println("tiradas  casos equilibrados  cobrem  NAO cobrem  fracao");

        for (Medidas.Remocao r : Medidas.contagem())
            System.out.printf("%7d  %17d  %6d  %10d  %5.1f%%%n", r.quantasTiradas(),
                    r.casos(), r.cobrem(), r.naoCobrem(), 100 * r.fracaoQueNaoCobre());

        System.out.println();
        System.out.println("com DUAS casas tiradas, uma de cada cor, TODOS os 64 casos se");
        System.out.println("cobrem. isso nao e acaso: e um teorema de gomory, de 1973, e a");
        System.out.println("varredura o confirma caso a caso.");
        System.out.println();
        System.out.println("com QUATRO, o equilibrio de cores continua valendo e a cobertura");
        System.out.println("some em 80 dos 784 casos. e ai que a condicao necessaria deixa de");
        System.out.println("ser suficiente, e o limite e exatamente entre duas e quatro.");
    }

    static void cores() {
        titulo("5. o argumento das cores: ate onde ele vai");

        Medidas.Cores c = Medidas.cores();

        System.out.printf("tabuleiros com numero par de casas   %d%n", c.casos());
        System.out.printf("sem cobertura, ao todo               %d%n", c.semCoberturaAoTodo());
        System.out.printf("  por cores desequilibradas          %d%n", c.coresDesequilibradas());
        System.out.printf("  com cores equilibradas             %d%n",
                c.semCoberturaComCoresOk());
        System.out.printf("o argumento explica                  %.1f%%%n",
                100 * Medidas.oQueAsCoresExplicam());

        System.out.println();
        System.out.println("o argumento e uma condicao NECESSARIA, e a varredura confere isso da");
        System.out.println("maneira mais forte possivel: se algum tabuleiro com cores");
        System.out.println("desequilibradas se cobrisse, o programa lancaria erro. nenhum lanca.");
        System.out.println();
        System.out.println("e nao e suficiente. a ultima linha e a fatia dos que nao se cobrem");
        System.out.println("por um motivo que a contagem de cores nao enxerga.");
    }
}
