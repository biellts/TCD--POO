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

import io.github.guisso.javasepersistencewithhibernateorm.beta.cliente.Cliente;
import io.github.guisso.javasepersistencewithhibernateorm.beta.cliente.ClienteRepository;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Interface gráfica para gerenciar entidades Cliente.
 */
public class TesteCliente extends TesteBase<Cliente> {
    private JTextField nomeField, cpfField, emailField;

    public TesteCliente() {
        super("Gerenciar Clientes", 650, 450, new ClienteRepository());
    }

    @Override
    protected JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        nomeField = new JTextField();
        nomeField.setToolTipText("Digite o nome do cliente (máximo 100 caracteres)");
        cpfField = new JTextField();
        cpfField.setToolTipText("Digite o CPF (11 dígitos)");
        emailField = new JTextField();
        emailField.setToolTipText("Digite o email (exemplo: usuario@dominio.com)");

        inputPanel.add(new JLabel("Nome:"));
        inputPanel.add(nomeField);
        inputPanel.add(new JLabel("CPF:"));
        inputPanel.add(cpfField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("ID (para ações):"));
        inputPanel.add(idField);

        return inputPanel;
    }

    @Override
    protected void addSpecificButtonActions(JPanel buttonPanel) {
        JButton addButton = new JButton("Adicionar/Atualizar");
        addButton.setToolTipText("Adiciona um novo cliente ou atualiza um existente");
        JButton testarConexaoButton = new JButton("Testar Conexão");
        testarConexaoButton.setToolTipText("Testa a conexão com o banco criando um cliente temporário");
        JButton listLixeiraButton = new JButton("Listar Lixeira");
        listLixeiraButton.setToolTipText("Lista todos os clientes na lixeira");
        JButton moverLixeiraButton = new JButton("Mover p/ Lixeira");
        moverLixeiraButton.setToolTipText("Move o cliente para a lixeira (soft delete)");
        JButton restaurarButton = new JButton("Restaurar");
        restaurarButton.setToolTipText("Restaura um cliente da lixeira");
        JButton excluirDefButton = new JButton("Excluir Definitivo");
        excluirDefButton.setToolTipText("Exclui permanentemente um cliente");
        JButton esvaziarLixeiraButton = new JButton("Esvaziar Lixeira");
        esvaziarLixeiraButton.setToolTipText("Exclui permanentemente todos os clientes na lixeira");

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
                String cpf = cpfField.getText().trim();
                String email = emailField.getText().trim();
                String idStr = idField.getText().trim();

                if (nome.isEmpty()) {
                    outputArea.setText("Erro: Nome é obrigatório.");
                    return;
                }
                if (!isValidCPF(cpf)) {
                    outputArea.setText("Erro: CPF inválido (deve ter 11 dígitos).");
                    return;
                }
                if (email.isEmpty() || !email.contains("@")) {
                    outputArea.setText("Erro: Email inválido.");
                    return;
                }

                Cliente c = new Cliente();
                c.setNome(nome);
                c.setCpf(cpf);
                c.setEmail(email);

                if (!idStr.isEmpty()) {
                    Long id = Long.parseLong(idStr);
                    Cliente existente = repository.findById(id);
                    if (existente == null) {
                        outputArea.setText("ID " + id + " não encontrado. Deixe em branco para criar novo cliente.");
                        return;
                    }
                    c.setId(id);
                }

                Long id = repository.saveOrUpdate(c);
                List<Cliente> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Cliente salvo com ID: " + id + "\n\nClientes Ativos:\n");
                for (Cliente cl : ativos) {
                    sb.append(entityToString(cl)).append("\n");
                }
                outputArea.setText(sb.toString());

                clearInputFields();
            } catch (NumberFormatException ex) {
                outputArea.setText("Erro: Formato inválido para ID.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        testarConexaoButton.addActionListener(e -> {
            try {
                Cliente c = new Cliente();
                c.setNome("TesteConexao");
                c.setCpf(String.valueOf(System.currentTimeMillis()).substring(0, 11));
                c.setEmail("teste" + System.currentTimeMillis() + "@email.com");
                Long id = repository.saveOrUpdate(c);

                List<Cliente> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Conexão OK! Cliente teste salvo com ID: " + id + "\n\nClientes Ativos:\n");
                for (Cliente cl : ativos) {
                    sb.append(entityToString(cl)).append("\n");
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
                List<Cliente> ativos = ((ClienteRepository) repository).recuperarTodosDaLixeira();
                StringBuilder sb = new StringBuilder("Clientes na Lixeira:\n");
                for (Cliente cl : ativos) {
                    sb.append(entityToString(cl)).append("\n");
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
                Cliente c = repository.findById(id);
                if (c == null) {
                    outputArea.setText("ID " + id + " não encontrado.");
                    return;
                }
                c.setNaLixeira(true);
                repository.saveOrUpdate(c);
                outputArea.setText("Cliente movido para lixeira com ID: " + id);
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
                Cliente c = repository.findById(id);
                if (c == null) {
                    outputArea.setText("ID " + id + " não encontrado.");
                    return;
                }
                c.setNaLixeira(false);
                repository.saveOrUpdate(c);
                outputArea.setText("Cliente restaurado com ID: " + id);
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
                    "Tem certeza que deseja excluir o cliente permanentemente?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long id = Long.parseLong(idStr);
                    Cliente c = repository.findById(id);
                    if (c == null) {
                        outputArea.setText("ID " + id + " não encontrado.");
                        return;
                    }
                    repository.delete(id);
                    outputArea.setText("Cliente excluído definitivamente com ID: " + id);
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
                    ((ClienteRepository) repository).esvaziarLixeira();
                    outputArea.setText("Lixeira esvaziada com sucesso.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    protected String entityToString(Cliente c) {
        return c.getId() + " - " + c.getNome() + " / " + c.getCpf() + " / " + c.getEmail();
    }

    @Override
    protected void clearInputFields() {
        nomeField.setText("");
        cpfField.setText("");
        emailField.setText("");
        idField.setText("");
    }

    /**
     * Valida o formato do CPF (11 dígitos numéricos).
     * @param cpf CPF a ser validado
     * @return true se válido, false caso contrário
     */
    private static boolean isValidCPF(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");
        return cpf.length() == 11;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TesteCliente().show());
    }
}