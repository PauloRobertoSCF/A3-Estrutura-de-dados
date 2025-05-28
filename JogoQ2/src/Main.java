import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Main {
    static final int NUM_PILHAS = 7;
    static final int TAMANHO_PILHA = 7;
    static List<Stack<String>> pilhas = new ArrayList<>();
    static String ultimaBolaMovida = null;
    static int ultimaPilhaDestino = -1;

    private JFrame frame;
    private JTextArea estadoTextArea;
    private JComboBox<Integer> origemCombo;
    private JComboBox<Integer> destinoCombo;
    private JButton moverButton;
    private JLabel tempoLabel;
    private long inicioTempo;

    public Main() {
        inicioTempo = System.currentTimeMillis();
        inicializarPilhas();
        embaralharEDistribuirBolas();
        criarInterface();
        atualizarEstado();
    }

    // método para criar as pilhas
    static void inicializarPilhas() {
        pilhas.clear();
        for (int i = 0; i < NUM_PILHAS; i++) {
            pilhas.add(new Stack<>());
        }
    }

    // método para embaralhar as bolas e distribuí-las nas pilhas
    static void embaralharEDistribuirBolas() {
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
                pilhas.get(i).clear();
            }

            for (int i = 0; i < NUM_PILHAS; i++) {
                if (i == NUM_PILHAS - 1) continue; // Deixa uma pilha vazia
                for (int j = 0; j < TAMANHO_PILHA; j++) {
                    pilhas.get(i).push(todasBolas.get(index++));
                }
            }

            // Verifica se os topos têm cores diferentes
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

    // método da mini interface gráfica
    private void criarInterface() {
        frame = new JFrame("Jogo das Pilhas (GUI)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        estadoTextArea = new JTextArea();
        estadoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        estadoTextArea.setEditable(false);
        frame.add(new JScrollPane(estadoTextArea), BorderLayout.CENTER);

        JPanel controlePanel = new JPanel();
        controlePanel.setLayout(new FlowLayout());

        origemCombo = new JComboBox<>();
        destinoCombo = new JComboBox<>();
        for (int i = 0; i < NUM_PILHAS; i++) {
            origemCombo.addItem(i);
            destinoCombo.addItem(i);
        }

        moverButton = new JButton("Mover");
        moverButton.addActionListener(e -> {
            int origem = (int) origemCombo.getSelectedItem();
            int destino = (int) destinoCombo.getSelectedItem();

            if (moverBola(origem, destino)) {
                atualizarEstado();
                if (verificarVitoria()) {
                    long tempoFinal = System.currentTimeMillis();
                    long total = tempoFinal - inicioTempo;
                    long segundos = total / 1000;
                    long minutos = segundos / 60;
                    segundos %= 60;
                    JOptionPane.showMessageDialog(frame,
                            "Parabéns! Você venceu o jogo!\nTempo total: " + minutos + " min e " + segundos + " seg.");
                    moverButton.setEnabled(false);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Movimento inválido. Tente novamente.");
            }
        });

        tempoLabel = new JLabel("Tempo: 0 min 0 seg");

        controlePanel.add(new JLabel("Origem:"));
        controlePanel.add(origemCombo);
        controlePanel.add(new JLabel("Destino:"));
        controlePanel.add(destinoCombo);
        controlePanel.add(moverButton);

        frame.add(controlePanel, BorderLayout.SOUTH);
        frame.add(tempoLabel, BorderLayout.NORTH);

        Timer timer = new Timer(1000, e -> atualizarTempo());
        timer.start();

        frame.setVisible(true);
    }

    // método para atualizar o tempo decorrido
    private void atualizarTempo() {
        long agora = System.currentTimeMillis();
        long tempoTotal = agora - inicioTempo;
        long segundos = tempoTotal / 1000;
        long minutos = segundos / 60;
        segundos %= 60;
        tempoLabel.setText("Tempo: " + minutos + " min " + segundos + " seg");
    }

    // método para atualizar o estado das pilhas na interface
    private void atualizarEstado() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pilhas.size(); i++) {
            sb.append("Pilha ").append(i).append(": ").append(pilhas.get(i)).append("\n");
        }
        estadoTextArea.setText(sb.toString());
    }

    // método para mover uma bola de uma pilha para outra
    static boolean moverBola(int origem, int destino) {
        if (origem < 0 || origem >= NUM_PILHAS || destino < 0 || destino >= NUM_PILHAS) return false;

        Stack<String> pilhaOrigem = pilhas.get(origem);
        Stack<String> pilhaDestino = pilhas.get(destino);

        if (pilhaOrigem.isEmpty()) return false;
        if (pilhaDestino.size() >= TAMANHO_PILHA) return false;

        String bola = pilhaOrigem.peek();

        if (origem == ultimaPilhaDestino && bola.equals(ultimaBolaMovida)) return false;

        pilhaOrigem.pop();
        pilhaDestino.push(bola);

        ultimaBolaMovida = bola;
        ultimaPilhaDestino = destino;

        return true;
    }

    // método para verificar se o jogador venceu
    static boolean verificarVitoria() {
        for (Stack<String> pilha : pilhas) {
            if (pilha.isEmpty()) continue;

            String cor = pilha.peek();
            for (String bola : pilha) {
                if (!bola.equals(cor)) return false;
            }

            if (pilha.size() != TAMANHO_PILHA) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}