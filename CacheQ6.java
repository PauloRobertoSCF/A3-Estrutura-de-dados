import java.util.*;

// Enum indicando o tipo de acesso
enum AccessResult {
    ENTER, HIT
}

// Interface comum às políticas
interface ReplacementPolicy {
    AccessResult accessPage(int page);
    void setCapacity(int capacity);
    List<Integer> getCacheContent();
}

// FIFO: aceita duplicatas, não verifica presença
class FIFOPolicy implements ReplacementPolicy {
    private Queue<Integer> queue = new LinkedList<>();
    private int capacity;

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        queue.clear();
    }

    public AccessResult accessPage(int page) {
        if (queue.size() == capacity) {
            queue.poll(); // Remove o mais antigo
        }
        queue.offer(page);
        return AccessResult.ENTER;
    }

    public List<Integer> getCacheContent() {
        return new ArrayList<>(queue);
    }
}

// LRU: não aceita duplicatas, usa ordem de uso
class LRUPolicy implements ReplacementPolicy {
    private LinkedHashMap<Integer, Integer> cacheMap;
    private int capacity;

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        cacheMap = new LinkedHashMap<>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > LRUPolicy.this.capacity;
            }
        };
    }

    public AccessResult accessPage(int page) {
        if (cacheMap.containsKey(page)) {
            cacheMap.get(page); // Atualiza a ordem
            return AccessResult.HIT;
        } else {
            cacheMap.put(page, 1);
            return AccessResult.ENTER;
        }
    }

    public List<Integer> getCacheContent() {
        return new ArrayList<>(cacheMap.keySet());
    }
}

// Simulador
public class CacheSimulador {
    private static ReplacementPolicy policy;
    private static final int CAPACIDADE = 4;
    private static String politicaAtual = "FIFO";

    private static ReplacementPolicy criarPolitica(String tipo) {
        ReplacementPolicy nova;
        if (tipo.equalsIgnoreCase("FIFO")) {
            nova = new FIFOPolicy();
        } else {
            nova = new LRUPolicy();
        }
        nova.setCapacity(CAPACIDADE);
        return nova;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        policy = criarPolitica(politicaAtual);

        System.out.println("Simulador de Cache (Capacidade: " + CAPACIDADE + ")");
        System.out.println("Política inicial: " + politicaAtual);
        System.out.println("Comandos: política FIFO | política LRU | sair | [número da página]");

        while (true) {
            System.out.print("\n> ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("sair")) break;

            if (entrada.toLowerCase().startsWith("politica")) {
                String[] partes = entrada.split("\\s+");
                if (partes.length == 2 && (partes[1].equalsIgnoreCase("FIFO") || partes[1].equalsIgnoreCase("LRU"))) {
                    politicaAtual = partes[1].toUpperCase();
                    policy = criarPolitica(politicaAtual);
                    System.out.println("Política de substituição alterada para: " + politicaAtual);
                } else {
                    System.out.println("Essa política não existe. Políticas válidas: FIFO, LRU");
                }
                continue;
            }

            try {
                int pagina = Integer.parseInt(entrada);
                AccessResult resultado = policy.accessPage(pagina);

                if (resultado == AccessResult.HIT) {
                    System.out.println("HIT!");
                } else {
                    System.out.println("ENTER!");
                }

                System.out.println("Cache [" + politicaAtual + "]: " + policy.getCacheContent());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número ou um comando válido.");
            }
        }

        System.out.println("Simulação encerrada.");
    }
}

