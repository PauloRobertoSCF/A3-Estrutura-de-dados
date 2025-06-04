import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;


enum AccessResult {
    ENTER, HIT
}

// interface para políticas de substituição de cache
interface ReplacementPolicy {
    AccessResult accessPage(int page);
    void setCapacity(int capacity);
    List<Integer> getCacheContent();
}

// implementação da política FIFO (First In, First Out)
class FIFOPolicy implements ReplacementPolicy {
    private Queue<Integer> queue = new LinkedList<>();
    private int capacity;

    // define a capacidade do cache e limpa ele ao mudar de capacidade
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        queue.clear();  // limpa o cache ao mudar de capacidade
    }

    // acessa uma página, inserindo-a no cache
    public AccessResult accessPage(int page) {
        if (queue.size() == capacity) {
            queue.poll();
        }
        queue.offer(page);
        return AccessResult.ENTER; // FIFO sempre insere, não há HIT real
    }

    // retorna o conteúdo atual do cache como uma lista
    public List<Integer> getCacheContent() {
        return new ArrayList<>(queue);
    }
}

// implementação da política LRU (Least Recently Used)
class LRUPolicy implements ReplacementPolicy {
    private LinkedHashMap<Integer, Integer> cacheMap;
    private int capacity;

    // define a capacidade do cache e inicializa o mapa com a política LRU
public void setCapacity(int capacity) {
    this.capacity = capacity;
    cacheMap = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
            return size() > LRUPolicy.this.capacity;
        }
    };
}


    // acessa uma página, atualizando a ordem de uso ou inserindo-a no cache
    public AccessResult accessPage(int page) {
        if (cacheMap.containsKey(page)) {
            cacheMap.get(page); // só acessa para atualizar a ordem
            return AccessResult.HIT;
        } else {
            cacheMap.put(page, 1);
            return AccessResult.ENTER;
        }
    }

    // retorna o conteúdo atual do cache como uma lista
    public List<Integer> getCacheContent() {
        return new ArrayList<>(cacheMap.keySet());
    }
}

// classe principal da GUI do simulador de cache
public class CacheSimuladorGUI extends JFrame {
    private static final int CAPACIDADE = 4;

    private ReplacementPolicy policy;
    private String politicaAtual = "FIFO";

    private JTextField inputField;
    private JButton sendButton;
    private JTextArea outputArea;
    private JComboBox<String> policySelector;

    // construtor da GUI
    public CacheSimuladorGUI() {
        setTitle("Simulador de Cache");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        policy = criarPolitica(politicaAtual);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Política:"));
        policySelector = new JComboBox<>(new String[]{"FIFO", "LRU"});
        policySelector.setSelectedItem(politicaAtual);
        topPanel.add(policySelector);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Enviar");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        printInfoInicial();

        sendButton.addActionListener(e -> processInput());
        inputField.addActionListener(e -> processInput());
        policySelector.addActionListener(e -> mudarPolitica());
    }

    // método para imprimir informações iniciais no console
    private void printInfoInicial() {
        outputArea.append("Simulador de Cache (Capacidade: " + CAPACIDADE + ")\n");
        outputArea.append("Política inicial: " + politicaAtual + "\n");
        outputArea.append("Digite o número da página ou comando.\n");
        outputArea.append("Comandos: sair\n\n");
    }

    // cria uma nova política de substituição com base no tipo selecionado
    private ReplacementPolicy criarPolitica(String tipo) {
        ReplacementPolicy nova;
        if (tipo.equalsIgnoreCase("FIFO")) {
            nova = new FIFOPolicy();
        } else {
            nova = new LRUPolicy();
        }
        nova.setCapacity(CAPACIDADE);
        return nova;
    }

    // método para mudar a política de substituição
    private void mudarPolitica() {
        String novaPolitica = (String) policySelector.getSelectedItem();
        if (!novaPolitica.equalsIgnoreCase(politicaAtual)) {
            politicaAtual = novaPolitica;
            policy = criarPolitica(politicaAtual);
            outputArea.append("\nPolítica alterada para: " + politicaAtual + "\n");
            outputArea.append("Cache reiniciado.\n\n");
        }

        // reativar interface se estiver desabilitada
        if (!sendButton.isEnabled()) {
            sendButton.setEnabled(true);
            inputField.setEnabled(true);
            policySelector.setEnabled(true);
            outputArea.append("Interface reativada após troca de política.\n\n");
        }
    }

    // processa a entrada do usuário, acessando a página ou encerrando a simulação
    private void processInput() {
        String entrada = inputField.getText().trim();
        inputField.setText("");

        // verifica se o usuário quer sair da simulação
        if (entrada.equalsIgnoreCase("sair")) {
            outputArea.append("Simulação encerrada.\n");
            sendButton.setEnabled(false);
            inputField.setEnabled(false);
            policySelector.setEnabled(true); // ainda permite trocar política
            return;
        }

        // verifica se a entrada é um número válido
        try {
            int pagina = Integer.parseInt(entrada);
            AccessResult resultado = policy.accessPage(pagina);

            if (resultado == AccessResult.HIT) {
                outputArea.append("HIT!\n");
            } else {
                outputArea.append("ENTER!\n");
            }

            outputArea.append("Cache [" + politicaAtual + "]: " + policy.getCacheContent() + "\n\n");
        } catch (NumberFormatException e) {
            outputArea.append("Entrada inválida. Digite um número ou comando válido.\n\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CacheSimuladorGUI().setVisible(true));
    }
}
