import java.util.*;

public class JogoQ2 {
    static final int NUM_PILHAS = 7;
    static final int TAMANHO_PILHA = 7;
    static List<Stack<String>> pilhas = new ArrayList<>();
    static String ultimaBolaMovida = null;
    static int ultimaPilhaDestino = -1;

    public static void main(String[] args) {
        inicializarPilhas();
        embaralharEBotocarBolas();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            exibirPilhas();

            if (verificarVitoria()) {
                System.out.println("Parabéns! Você venceu o jogo!");
                break;
            }

            System.out.print("Mover bola de qual pilha (0-6)? ");
            int origem = scanner.nextInt();
            System.out.print("Para qual pilha (0-6)? ");
            int destino = scanner.nextInt();

            if (moverBola(origem, destino)) {
                System.out.println("Movimento realizado!");
            } else {
                System.out.println("Movimento inválido. Tente novamente.");
            }
        }

        scanner.close();
    }

    static void inicializarPilhas() {
        for (int i = 0; i < NUM_PILHAS; i++) {
            pilhas.add(new Stack<>());
        }
    }

    static void embaralharEBotocarBolas() {
        String[] cores = { "A", "B", "C", "D", "E", "F" };
        List<String> todasBolas = new ArrayList<>();

        for (String cor : cores) {
            for (int i = 0; i < TAMANHO_PILHA; i++) {
                todasBolas.add(cor);
            }
        }

        boolean valido = false;
        while (!valido) {
            Collections.shuffle(todasBolas);
            valido = true;

            int index = 0;
            for (int i = 0; i < NUM_PILHAS; i++) {
                pilhas.get(i).clear(); // Limpa caso esteja reembaralhando
            }

            for (int i = 0; i < NUM_PILHAS; i++) {
                if (i == NUM_PILHAS - 1)
                    continue; // Deixa uma pilha vazia
                for (int j = 0; j < TAMANHO_PILHA; j++) {
                    pilhas.get(i).push(todasBolas.get(index++));
                }
            }

            // Verifica se o topo das pilhas preenchidas tem cores diferentes
            Set<String> topos = new HashSet<>();
            for (int i = 0; i < NUM_PILHAS - 1; i++) {
                String topo = pilhas.get(i).peek();
                if (!topos.add(topo)) {
                    valido = false;
                    break;
                }
            }
        }
    }

    static void exibirPilhas() {
        System.out.println("\nEstado atual das pilhas:");
        for (int i = 0; i < pilhas.size(); i++) {
            System.out.println("Pilha " + i + ": " + pilhas.get(i));
        }
        System.out.println();
    }

    static boolean moverBola(int origem, int destino) {
        if (origem < 0 || origem >= NUM_PILHAS || destino < 0 || destino >= NUM_PILHAS)
            return false;

        Stack<String> pilhaOrigem = pilhas.get(origem);
        Stack<String> pilhaDestino = pilhas.get(destino);

        if (pilhaOrigem.isEmpty())
            return false;
        if (pilhaDestino.size() >= TAMANHO_PILHA)
            return false;

        String bola = pilhaOrigem.peek();

        // Não permitir mover a mesma bola que acabou de ser colocada
        if (origem == ultimaPilhaDestino && bola.equals(ultimaBolaMovida))
            return false;

        pilhaOrigem.pop();
        pilhaDestino.push(bola);

        ultimaBolaMovida = bola;
        ultimaPilhaDestino = destino;

        return true;
    }

    static boolean verificarVitoria() {
        for (Stack<String> pilha : pilhas) {
            if (pilha.isEmpty())
                continue;

            String cor = pilha.peek();

            for (String bola : pilha) {
                if (!bola.equals(cor)) {
                    return false;
                }
            }

            if (pilha.size() != TAMANHO_PILHA) {
                return false;
            }
        }
        return true;
    }
}
