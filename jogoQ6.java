
import java.util.*;

interface ReplacementPolicy {
    void accessPage(int page);
    boolean isInCache(int page);
    void setCapacity(int capacity);
    List<Integer> getCacheContent();
}

class FIFOPolicy implements ReplacementPolicy {
    private Queue<Integer> queue = new LinkedList<>();
    private Set<Integer> cache = new HashSet<>();
    private int capacity;

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        queue.clear();
        cache.clear();
    }

    public void accessPage(int page) {
        if (!cache.contains(page)) {
            if (cache.size() == capacity) {
                int removed = queue.poll();
                cache.remove(removed);
            }
            queue.offer(page);
            cache.add(page);
        }
    }

    public boolean isInCache(int page) {
        return cache.contains(page);
    }

    public List<Integer> getCacheContent() {
        return new ArrayList<>(queue);
    }
}

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

    public void accessPage(int page) {
        cacheMap.put(page, 1);
    }

    public boolean isInCache(int page) {
        return cacheMap.containsKey(page);
    }

    public List<Integer> getCacheContent() {
        return new ArrayList<>(cacheMap.keySet());
    }
}

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
                    System.out.println("Uso: politica FIFO ou politica LRU");
                }
                continue;
            }

            try {
                int pagina = Integer.parseInt(entrada);
                boolean hit = policy.isInCache(pagina);

                policy.accessPage(pagina);
                System.out.println(hit ? "MISS!" : "ENTER!");
                System.out.println("Cache [" + politicaAtual + "]: " + policy.getCacheContent());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número ou um comando válido.");
            }
        }

        System.out.println("Simulação encerrada.");
    }
}
