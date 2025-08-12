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

import io.github.guisso.javasepersistencewithhibernateorm.beta.produto.Produto;
import io.github.guisso.javasepersistencewithhibernateorm.beta.produto.ProdutoRepository;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interface gráfica para gerenciar entidades Produto.
 */
public class TesteProduto extends TesteBase<Produto> {
    private JTextField nomeField, descricaoField, pesoField, precoField, qtdField;

    public TesteProduto() {
        super("Gerenciar Produtos", 700, 500, new ProdutoRepository());
    }

    @Override
    protected JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        nomeField = new JTextField();
        nomeField.setToolTipText("Digite o nome do produto (máximo 100 caracteres)");
        descricaoField = new JTextField();
        descricaoField.setToolTipText("Digite a descrição do produto (opcional)");
        pesoField = new JTextField();
        pesoField.setToolTipText("Digite o peso do produto (em kg)");
        precoField = new JTextField();
        precoField.setToolTipText("Digite o preço de venda (exemplo: 10.99)");
        qtdField = new JTextField();
        qtdField.setToolTipText("Digite a quantidade em estoque");

        inputPanel.add(new JLabel("Nome:"));
        inputPanel.add(nomeField);
        inputPanel.add(new JLabel("Descrição:"));
        inputPanel.add(descricaoField);
        inputPanel.add(new JLabel("Peso:"));
        inputPanel.add(pesoField);
        inputPanel.add(new JLabel("Preço Venda:"));
        inputPanel.add(precoField);
        inputPanel.add(new JLabel("Qtd Estoque:"));
        inputPanel.add(qtdField);
        inputPanel.add(new JLabel("ID (para ações):"));
        inputPanel.add(idField);

        return inputPanel;
    }

    @Override
    protected void addSpecificButtonActions(JPanel buttonPanel) {
        JButton addButton = new JButton("Adicionar/Atualizar");
        addButton.setToolTipText("Adiciona um novo produto ou atualiza um existente");
        JButton testarConexaoButton = new JButton("Testar Conexão");
        testarConexaoButton.setToolTipText("Testa a conexão com o banco criando um produto temporário");
        JButton listLixeiraButton = new JButton("Listar Lixeira");
        listLixeiraButton.setToolTipText("Lista todos os produtos na lixeira");
        JButton moverLixeiraButton = new JButton("Mover p/ Lixeira");
        moverLixeiraButton.setToolTipText("Move o produto para a lixeira (soft delete)");
        JButton restaurarButton = new JButton("Restaurar");
        restaurarButton.setToolTipText("Restaura um produto da lixeira");
        JButton excluirDefButton = new JButton("Excluir Definitivo");
        excluirDefButton.setToolTipText("Exclui permanentemente um produto");
        JButton esvaziarLixeiraButton = new JButton("Esvaziar Lixeira");
        esvaziarLixeiraButton.setToolTipText("Exclui permanentemente todos os produtos na lixeira");

        buttonPanel.add(addButton);
        buttonPanel.add(testarConexaoButton);
        buttonPanel.add(listLixeiraButton);
        buttonPanel.add(moverLixeiraButton);
        buttonPanel.add(restaurarButton);
        buttonPanel.add(excluirDefButton);
        buttonPanel.add(esvaziarLixeiraButton);

        addButton.addActionListener(e -> {
            try {
                String nome = nomeField.getText().trim();
                String descricao = descricaoField.getText().trim();
                String pesoStr = pesoField.getText().trim();
                String precoStr = precoField.getText().trim();
                String qtdStr = qtdField.getText().trim();
                String idStr = idField.getText().trim();

                if (nome.isEmpty()) {
                    outputArea.setText("Erro: Nome é obrigatório.");
                    return;
                }
                double peso = Double.parseDouble(pesoStr);
                if (peso <= 0) {
                    outputArea.setText("Erro: Peso deve ser maior que zero.");
                    return;
                }
                BigDecimal preco = new BigDecimal(precoStr);
                if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                    outputArea.setText("Erro: Preço deve ser maior que zero.");
                    return;
                }
                int qtd = Integer.parseInt(qtdStr);
                if (qtd < 0) {
                    outputArea.setText("Erro: Quantidade deve ser maior ou igual a zero.");
                    return;
                }

                Produto p = new Produto();
                p.setNome(nome);
                p.setDescricao(descricao);
                p.setPeso(peso);
                p.setPrecoVenda(preco);
                p.setQtdEstoque(qtd);

                if (!idStr.isEmpty()) {
                    Long id = Long.parseLong(idStr);
                    Produto existente = repository.findById(id);
                    if (existente == null) {
                        outputArea.setText("ID " + id + " não encontrado. Deixe em branco para criar novo produto.");
                        return;
                    }
                    p.setId(id);
                }

                Long id = repository.saveOrUpdate(p);
                List<Produto> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Produto salvo com ID: " + id + "\n\nProdutos Ativos:\n");
                for (Produto prod : ativos) {
                    sb.append(entityToString(prod)).append("\n");
                }
                outputArea.setText(sb.toString());

                clearInputFields();
            } catch (NumberFormatException ex) {
                outputArea.setText("Erro: Formato inválido para peso, preço ou quantidade.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        testarConexaoButton.addActionListener(e -> {
            try {
                Produto p = new Produto();
                p.setNome("TesteProduto");
                p.setDescricao("Teste");
                p.setPeso(1.0);
                p.setPrecoVenda(new BigDecimal("10.00"));
                p.setQtdEstoque(5);
                Long id = repository.saveOrUpdate(p);

                List<Produto> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Conexão OK! Produto teste salvo com ID: " + id + "\n\nProdutos Ativos:\n");
                for (Produto prod : ativos) {
                    sb.append(entityToString(prod)).append("\n");
                }
                outputArea.setText(sb.toString());

                // Limpar dados de teste
                repository.delete(id);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Falha na conexão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        listLixeiraButton.addActionListener(e -> {
            try {
                List<Produto> lixo = ((ProdutoRepository) repository).recuperarTodosDaLixeira();
                StringBuilder sb = new StringBuilder("Produtos na Lixeira:\n");
                for (Produto p : lixo) {
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
                Produto p = repository.findById(id);
                if (p == null) {
                    outputArea.setText("ID " + id + " não encontrado.");
                    return;
                }
                p.setNaLixeira(true);
                repository.saveOrUpdate(p);
                outputArea.setText("Produto movido para lixeira com ID: " + id);
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
                Produto p = repository.findById(id);
                if (p == null) {
                    outputArea.setText("ID " + id + " não encontrado.");
                    return;
                }
                p.setNaLixeira(false);
                repository.saveOrUpdate(p);
                outputArea.setText("Produto restaurado com ID: " + id);
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
                    "Tem certeza que deseja excluir o produto permanentemente?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long id = Long.parseLong(idStr);
                    Produto p = repository.findById(id);
                    if (p == null) {
                        outputArea.setText("ID " + id + " não encontrado.");
                        return;
                    }
                    repository.delete(id);
                    outputArea.setText("Produto excluído definitivamente com ID: " + id);
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
                    ((ProdutoRepository) repository).esvaziarLixeira();
                    outputArea.setText("Lixeira esvaziada com sucesso.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    protected String entityToString(Produto prod) {
        return prod.getId() + " - " + prod.getNome() + " / " + prod.getPrecoVenda();
    }

    @Override
    protected void clearInputFields() {
        nomeField.setText("");
        descricaoField.setText("");
        pesoField.setText("");
        precoField.setText("");
        qtdField.setText("");
        idField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TesteProduto().show());
    }
}