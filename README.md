# Cobertura por dominós

Cobertura de tabuleiros por dominós em Java 21, com busca completa e o argumento das cores medido até o ponto em que ele deixa de bastar.

O tabuleiro de xadrez tem **12.988.816** coberturas. Sem dois cantos opostos, tem **zero**, e a busca gasta milhares de passos para descobrir o que uma contagem de cores diz numa linha.

```
mvn -q exec:java -Dexec.args=mutilado
```

## As medidas

### 1. As coberturas dos tabuleiros cheios

| tabuleiro | casas | coberturas |
|---|---:|---:|
| 2x2 | 4 | 2 |
| 2x3 | 6 | 3 |
| 3x4 | 12 | 11 |
| 4x4 | 16 | 36 |
| 6x6 | 36 | 6.728 |
| 8x8 | 64 | **12.988.816** |

A busca é completa, e isso tem uma razão simples: ela pega a **primeira casa vazia** e tenta as duas únicas maneiras de cobri-la. Essa casa precisa ser coberta de algum jeito, então não há escolha a perder, e um zero aqui é exaustão e não desistência.

O 12.988.816 é conhecido desde 1961 e serve de testemunha para a busca inteira.

### 2. A faixa de duas linhas é Fibonacci

| n | coberturas | Fibonacci |
|---:|---:|---:|
| 5 | 8 | 8 |
| 10 | 89 | 89 |
| 15 | 987 | 987 |
| 20 | 10.946 | 10.946 |

A primeira coluna de uma faixa 2 por n é coberta por um dominó em pé, sobrando 2 por n-1, ou por dois deitados, sobrando 2 por n-2. A recorrência sai da geometria.

A busca não sabe disso: ela enumera coberturas uma a uma. Os números baterem é o fato medido.

### 3. O tabuleiro mutilado

| lado | cheio | sem dois cantos | claras | escuras |
|---:|---:|---:|---:|---:|
| 4 | 36 | **0** | 6 | 8 |
| 6 | 6.728 | **0** | 16 | 18 |
| 8 | 12.988.816 | **0** | 30 | 32 |

Tire dois cantos **opostos** do tabuleiro de xadrez. Sobram 62 casas, que dariam 31 dominós certinhos. A busca completa acha zero.

E o motivo cabe numa linha: cantos opostos têm a **mesma cor**. Tirando dois da mesma cor sobram 30 de uma e 32 de outra, e todo dominó cobre uma de cada. A contagem diz de imediato o que a busca penou para descobrir.

### 4. Tirando casas de cores diferentes, num 4 por 4

| tiradas | casos equilibrados | cobrem | não cobrem | fração |
|---:|---:|---:|---:|---:|
| 2 | 64 | 64 | **0** | 0,0% |
| 4 | 784 | 704 | **80** | 10,2% |

Com **duas** casas tiradas, uma de cada cor, todos os 64 casos se cobrem. Isso não é acaso: é um teorema de Gomory, de 1973, e a varredura o confirma caso a caso.

Com **quatro**, o equilíbrio de cores continua valendo e a cobertura some em 80 dos 784. É aí que a condição necessária deixa de ser suficiente, e o limite é exatamente entre duas e quatro.

### 5. O argumento das cores: até onde ele vai

```
tabuleiros com número par de casas   32.768
sem cobertura, ao todo               27.068
  por cores desequilibradas          19.898
  com cores equilibradas              7.170
o argumento explica                   73,5%
```

Ele é uma condição **necessária**, e a varredura confere isso da maneira mais forte possível: se algum tabuleiro com cores desequilibradas se cobrisse, o programa lançaria erro. Nenhum lança.

E não é suficiente. Os 7.170 da última linha não se cobrem por um motivo que a contagem de cores não enxerga.

## O que as medidas me corrigiram

**O argumento das cores explica menos do que parece.** Ele é famoso por matar o tabuleiro mutilado numa linha, e é fácil sair daí achando que ele decide tudo. Ele explica 73,5% dos tabuleiros que não se cobrem. O resto precisa da busca.

**O limite entre "basta" e "não basta" é entre duas e quatro casas tiradas.** Com duas, o equilíbrio de cores garante a cobertura, sempre, e isso é teorema. Com quatro, já falha em 10% dos casos. É um limite estreito e específico, e eu não sabia onde ele estava antes de contar.

**Um zero de busca completa é uma afirmação forte.** A busca começa pela primeira casa vazia, que precisa ser coberta de algum jeito. Por isso ela não pode perder solução, e dizer "não existe" é exaustão e não cansaço.

## Rodar

```bash
mvn -q verify                              # 18 testes
mvn -q exec:java                           # todas as medidas, menos de um segundo
mvn -q exec:java -Dexec.args=correcao      # 1
mvn -q exec:java -Dexec.args=fibonacci     # 2
mvn -q exec:java -Dexec.args=mutilado      # 3
mvn -q exec:java -Dexec.args=contagem      # 4
mvn -q exec:java -Dexec.args=cores         # 5
```

Java 21, sem dependência fora do JUnit. Nenhum número deste README foi digitado à mão: todos saem de `Medidas`, e os testes conferem os que aparecem escritos aqui.
