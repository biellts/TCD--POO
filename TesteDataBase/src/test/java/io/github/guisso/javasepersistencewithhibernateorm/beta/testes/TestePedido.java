/*
 * Copyright (C) 2025 Luis Guisso <luis dot guisso at ifnmg dot edu dot br>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.guisso.javasepersistencewithhibernateorm.beta.testes;

import io.github.guisso.javasepersistencewithhibernateorm.beta.pedido.Pedido;
import io.github.guisso.javasepersistencewithhibernateorm.beta.pedido.PedidoRepository;
import io.github.guisso.javasepersistencewithhibernateorm.beta.pedido.Pedido.Status;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Interface gráfica para gerenciar entidades Pedido.
 */
public class TestePedido extends TesteBase<Pedido> {
    private JTextField clienteIdField, dataCriacaoField, valorTotalField;
    private JComboBox<Status> statusCombo;

    public TestePedido() {
        super("Gerenciar Pedidos", 700, 520, new PedidoRepository());
    }

    @Override
    protected JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        clienteIdField = new JTextField();
        clienteIdField.setToolTipText("Digite o ID do cliente");
        statusCombo = new JComboBox<>(Status.values());
        statusCombo.setToolTipText("Selecione o status do pedido");
        dataCriacaoField = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dataCriacaoField.setToolTipText("Digite a data de criação (formato: yyyy-MM-dd HH:mm:ss)");
        valorTotalField = new JTextField();
        valorTotalField.setToolTipText("Digite o valor total do pedido");

        inputPanel.add(new JLabel("Cliente ID:"));
        inputPanel.add(clienteIdField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusCombo);
        inputPanel.add(new JLabel("Data Criação (yyyy-MM-dd HH:mm:ss):"));
        inputPanel.add(dataCriacaoField);
        inputPanel.add(new JLabel("Valor Total:"));
        inputPanel.add(valorTotalField);
        inputPanel.add(new JLabel("ID (para ações):"));
        inputPanel.add(idField);

        return inputPanel;
    }

    @Override
    protected void addSpecificButtonActions(JPanel buttonPanel) {
        JButton addButton = new JButton("Adicionar/Atualizar");
        addButton.setToolTipText("Adiciona um novo pedido ou atualiza um existente");
        JButton testarConexaoButton = new JButton("Testar Conexão");
        testarConexaoButton.setToolTipText("Testa a conexão com o banco criando um pedido temporário");
        JButton listLixeiraButton = new JButton("Listar Lixeira");
        listLixeiraButton.setToolTipText("Lista todos os pedidos na lixeira");
        JButton moverLixeiraButton = new JButton("Mover p/ Lixeira");
        moverLixeiraButton.setToolTipText("Move o pedido para a lixeira (soft delete)");
        JButton restaurarButton = new JButton("Restaurar");
        restaurarButton.setToolTipText("Restaura um pedido da lixeira");
        JButton excluirDefButton = new JButton("Excluir Definitivo");
        excluirDefButton.setToolTipText("Exclui permanentemente um pedido");
        JButton esvaziarLixeiraButton = new JButton("Esvaziar Lixeira");
        esvaziarLixeiraButton.setToolTipText("Exclui permanentemente todos os pedidos na lixeira");

        buttonPanel.add(addButton);
        buttonPanel.add(testarConexaoButton);
        buttonPanel.add(listLixeiraButton);
        buttonPanel.add(moverLixeiraButton);
        buttonPanel.add(restaurarButton);
        buttonPanel.add(excluirDefButton);
        buttonPanel.add(esvaziarLixeiraButton);

        addButton.addActionListener(e -> {
            try {
                String clienteIdStr = clienteIdField.getText().trim();
                Status status = (Status) statusCombo.getSelectedItem();
                String dataStr = dataCriacaoField.getText().trim();
                String valorStr = valorTotalField.getText().trim();
                String idStr = idField.getText().trim();

                if (clienteIdStr.isEmpty()) {
                    outputArea.setText("Erro: Cliente ID é obrigatório.");
                    return;
                }
                int clienteId = Integer.parseInt(clienteIdStr);
                if (clienteId <= 0) {
                    outputArea.setText("Erro: Cliente ID deve ser positivo.");
                    return;
                }
                LocalDateTime dataCriacao;
                try {
                    dataCriacao = LocalDateTime.parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (DateTimeParseException ex) {
                    outputArea.setText("Erro: Formato de data inválido (use yyyy-MM-dd HH:mm:ss).");
                    return;
                }
                BigDecimal valorTotal = new BigDecimal(valorStr);
                if (valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
                    outputArea.setText("Erro: Valor total deve ser maior que zero.");
                    return;
                }

                Pedido pedido = new Pedido();
                pedido.setClienteId(clienteId);
                pedido.setStatus(status);
                pedido.setDataCriacao(dataCriacao);
                pedido.setValorTotal(valorTotal);

                if (!idStr.isEmpty()) {
                    Long id = Long.parseLong(idStr);
                    Pedido existente = repository.findById(id);
                    if (existente == null) {
                        outputArea.setText("ID " + id + " não encontrado. Deixe em branco para criar novo pedido.");
                        return;
                    }
                    pedido.setId(id);
                }

                Long idSalvo = repository.saveOrUpdate(pedido);
                List<Pedido> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Pedido salvo com ID: " + idSalvo + "\n\nPedidos Ativos:\n");
                for (Pedido p : ativos) {
                    sb.append(entityToString(p)).append("\n");
                }
                outputArea.setText(sb.toString());

                clearInputFields();
            } catch (NumberFormatException ex) {
                outputArea.setText("Erro: Formato inválido para ID do cliente ou valor total.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        testarConexaoButton.addActionListener(e -> {
            try {
                Pedido pedidoTeste = new Pedido();
                pedidoTeste.setClienteId(1); // Ajuste conforme necessário
                pedidoTeste.setStatus(Status.CRIADO);
                pedidoTeste.setDataCriacao(LocalDateTime.now());
                pedidoTeste.setValorTotal(new BigDecimal("100.00"));

                Long idTeste = repository.saveOrUpdate(pedidoTeste);

                List<Pedido> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Conexão OK! Pedido teste salvo com ID: " + idTeste + "\n\nPedidos Ativos:\n");
                for (Pedido p : ativos) {
                    sb.append(entityToString(p)).append("\n");
                }
                outputArea.setText(sb.toString());

                // Limpar dados de teste
                repository.delete(idTeste);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Falha na conexão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        listLixeiraButton.addActionListener(e -> {
            try {
                List<Pedido> lixo = ((PedidoRepository) repository).recuperarTodosDaLixeira();
                StringBuilder sb = new StringBuilder("Pedidos na Lixeira:\n");
                for (Pedido p : lixo) {
                    sb.append(entityToString(p)).append("\n");
                }
                outputArea.setText(sb.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao listar lixeira: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        moverLixeiraButton.addActionListener(e -> {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty()) {
                outputArea.setText("Por favor, informe o ID.");
                return;
            }
            try {
                Long id = Long.parseLong(idStr);
                Pedido p = repository.findById(id);
                if (p == null) {
                    outputArea.setText("ID " + id + " não encontrado.");
                    return;
                }
                p.setNaLixeira(true);
                repository.saveOrUpdate(p);
                outputArea.setText("Pedido movido para lixeira com ID: " + id);
            } catch (NumberFormatException ex) {
                outputArea.setText("Erro: ID inválido.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        restaurarButton.addActionListener(e -> {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty()) {
                outputArea.setText("Por favor, informe o ID.");
                return;
            }
            try {
                Long id = Long.parseLong(idStr);
                Pedido p = repository.findById(id);
                if (p == null) {
                    outputArea.setText("ID " + id + " não encontrado.");
                    return;
                }
                p.setNaLixeira(false);
                repository.saveOrUpdate(p);
                outputArea.setText("Pedido restaurado com ID: " + id);
            } catch (NumberFormatException ex) {
                outputArea.setText("Erro: ID inválido.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        excluirDefButton.addActionListener(e -> {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty()) {
                outputArea.setText("Por favor, informe o ID.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Tem certeza que deseja excluir o pedido permanentemente?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long id = Long.parseLong(idStr);
                    Pedido p = repository.findById(id);
                    if (p == null) {
                        outputArea.setText("ID " + id + " não encontrado.");
                        return;
                    }
                    repository.delete(id);
                    outputArea.setText("Pedido excluído definitivamente com ID: " + id);
                } catch (NumberFormatException ex) {
                    outputArea.setText("Erro: ID inválido.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        esvaziarLixeiraButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Tem certeza que deseja esvaziar a lixeira?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ((PedidoRepository) repository).esvaziarLixeira();
                    outputArea.setText("Lixeira esvaziada com sucesso.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    protected String entityToString(Pedido p) {
        return p.getId() + " - Cliente ID: " + p.getClienteId() + ", Status: " + p.getStatus() +
                ", Data Criação: " + p.getDataCriacao().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                ", Valor Total: " + p.getValorTotal();
    }

    @Override
    protected void clearInputFields() {
        clienteIdField.setText("");
        statusCombo.setSelectedIndex(0);
        dataCriacaoField.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        valorTotalField.setText("");
        idField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TestePedido().show());
    }
}