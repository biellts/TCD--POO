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

import io.github.guisso.javasepersistencewithhibernateorm.beta.item.Item;
import io.github.guisso.javasepersistencewithhibernateorm.beta.item.ItemRepository;
import io.github.guisso.javasepersistencewithhibernateorm.beta.testes.TesteBase;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interface gráfica para gerenciar entidades Item.
 */
public class TesteItem extends TesteBase<Item> {
    private JTextField pedidoIdField, produtoIdField, quantidadeField, precoUnitarioField;

    public TesteItem() {
        super("Gerenciar Itens", 700, 500, new ItemRepository());
    }

    @Override
    protected JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        pedidoIdField = new JTextField();
        pedidoIdField.setToolTipText("Digite o ID do pedido");
        produtoIdField = new JTextField();
        produtoIdField.setToolTipText("Digite o ID do produto");
        quantidadeField = new JTextField();
        quantidadeField.setToolTipText("Digite a quantidade do item");
        precoUnitarioField = new JTextField();
        precoUnitarioField.setToolTipText("Digite o preço unitário (exemplo: 10.99)");

        inputPanel.add(new JLabel("Pedido ID:"));
        inputPanel.add(pedidoIdField);
        inputPanel.add(new JLabel("Produto ID:"));
        inputPanel.add(produtoIdField);
        inputPanel.add(new JLabel("Quantidade:"));
        inputPanel.add(quantidadeField);
        inputPanel.add(new JLabel("Preço Unitário:"));
        inputPanel.add(precoUnitarioField);
        inputPanel.add(new JLabel("ID (para ações):"));
        inputPanel.add(idField);

        return inputPanel;
    }

    @Override
    protected void addSpecificButtonActions(JPanel buttonPanel) {
        JButton addButton = new JButton("Adicionar/Atualizar");
        addButton.setToolTipText("Adiciona um novo item ou atualiza um existente");
        JButton testarConexaoButton = new JButton("Testar Conexão");
        testarConexaoButton.setToolTipText("Testa a conexão com o banco criando um item temporário");
        JButton listLixeiraButton = new JButton("Listar Lixeira");
        listLixeiraButton.setToolTipText("Lista todos os itens na lixeira");
        JButton moverLixeiraButton = new JButton("Mover p/ Lixeira");
        moverLixeiraButton.setToolTipText("Move o item para a lixeira (soft delete)");
        JButton restaurarButton = new JButton("Restaurar");
        restaurarButton.setToolTipText("Restaura um item da lixeira");
        JButton excluirDefButton = new JButton("Excluir Definitivo");
        excluirDefButton.setToolTipText("Exclui permanentemente um item");
        JButton esvaziarLixeiraButton = new JButton("Esvaziar Lixeira");
        esvaziarLixeiraButton.setToolTipText("Exclui permanentemente todos os itens na lixeira");

        buttonPanel.add(addButton);
        buttonPanel.add(testarConexaoButton);
        buttonPanel.add(listLixeiraButton);
        buttonPanel.add(moverLixeiraButton);
        buttonPanel.add(restaurarButton);
        buttonPanel.add(excluirDefButton);
        buttonPanel.add(esvaziarLixeiraButton);

        addButton.addActionListener(e -> {
            try {
                String pedidoIdStr = pedidoIdField.getText().trim();
                String produtoIdStr = produtoIdField.getText().trim();
                String quantidadeStr = quantidadeField.getText().trim();
                String precoUnitarioStr = precoUnitarioField.getText().trim();
                String idStr = idField.getText().trim();

                if (pedidoIdStr.isEmpty()) {
                    outputArea.setText("Erro: Pedido ID é obrigatório.");
                    return;
                }
                int pedidoId = Integer.parseInt(pedidoIdStr);
                if (pedidoId <= 0) {
                    outputArea.setText("Erro: Pedido ID deve ser positivo.");
                    return;
                }
                if (produtoIdStr.isEmpty()) {
                    outputArea.setText("Erro: Produto ID é obrigatório.");
                    return;
                }
                int produtoId = Integer.parseInt(produtoIdStr);
                if (produtoId <= 0) {
                    outputArea.setText("Erro: Produto ID deve ser positivo.");
                    return;
                }
                int quantidade = Integer.parseInt(quantidadeStr);
                if (quantidade <= 0) {
                    outputArea.setText("Erro: Quantidade deve ser maior que zero.");
                    return;
                }
                BigDecimal precoUnitario = new BigDecimal(precoUnitarioStr);
                if (precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
                    outputArea.setText("Erro: Preço unitário deve ser maior que zero.");
                    return;
                }

                Item item = new Item();
                item.setPedidoId(pedidoId);
                item.setProdutoId(produtoId);
                item.setQuantidade(quantidade);
                item.setPrecoUnitario(precoUnitario);

                if (!idStr.isEmpty()) {
                    Long id = Long.parseLong(idStr);
                    Item existente = repository.findById(id);
                    if (existente == null) {
                        outputArea.setText("ID " + id + " não encontrado. Deixe em branco para criar novo item.");
                        return;
                    }
                    item.setId(id);
                }

                Long idSalvo = repository.saveOrUpdate(item);
                List<Item> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Item salvo com ID: " + idSalvo + "\n\nItens Ativos:\n");
                for (Item i : ativos) {
                    sb.append(entityToString(i)).append("\n");
                }
                outputArea.setText(sb.toString());

                clearInputFields();
            } catch (NumberFormatException ex) {
                outputArea.setText("Erro: Formato inválido para IDs, quantidade ou preço.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        testarConexaoButton.addActionListener(e -> {
            try {
                Item itemTeste = new Item();
                itemTeste.setPedidoId(1); // Ajuste conforme necessário
                itemTeste.setProdutoId(1);
                itemTeste.setQuantidade(1);
                itemTeste.setPrecoUnitario(new BigDecimal("10.00"));

                Long idTeste = repository.saveOrUpdate(itemTeste);

                List<Item> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Conexão OK! Item teste salvo com ID: " + idTeste + "\n\nItens Ativos:\n");
                for (Item i : ativos) {
                    sb.append(entityToString(i)).append("\n");
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
                List<Item> lixo = ((ItemRepository) repository).recuperarTodosDaLixeira();
                StringBuilder sb = new StringBuilder("Itens na Lixeira:\n");
                for (Item i : lixo) {
                    sb.append(entityToString(i)).append("\n");
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
                Item i = repository.findById(id);
                if (i == null) {
                    outputArea.setText("ID " + id + " não encontrado.");
                    return;
                }
                i.setNaLixeira(true);
                repository.saveOrUpdate(i);
                outputArea.setText("Item movido para lixeira com ID: " + id);
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
                Item i = repository.findById(id);
                if (i == null) {
                    outputArea.setText("ID " + id + " não encontrado.");
                    return;
                }
                i.setNaLixeira(false);
                repository.saveOrUpdate(i);
                outputArea.setText("Item restaurado com ID: " + id);
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
                    "Tem certeza que deseja excluir o item permanentemente?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long id = Long.parseLong(idStr);
                    Item i = repository.findById(id);
                    if (i == null) {
                        outputArea.setText("ID " + id + " não encontrado.");
                        return;
                    }
                    repository.delete(id);
                    outputArea.setText("Item excluído definitivamente com ID: " + id);
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
                    ((ItemRepository) repository).esvaziarLixeira();
                    outputArea.setText("Lixeira esvaziada com sucesso.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    protected String entityToString(Item i) {
        return i.getId() + " - Pedido ID: " + i.getPedidoId() + ", Produto ID: " + i.getProdutoId() +
                ", Qtde: " + i.getQuantidade() + ", Preço Unitário: " + i.getPrecoUnitario();
    }

    @Override
    protected void clearInputFields() {
        pedidoIdField.setText("");
        produtoIdField.setText("");
        quantidadeField.setText("");
        precoUnitarioField.setText("");
        idField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TesteItem().show());
    }
}