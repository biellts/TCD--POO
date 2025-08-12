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

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.IRepository;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Classe base abstrata para testes de entidades com interface gráfica Swing.
 * Fornece funcionalidades comuns para configuração de GUI e ações de botões.
 *
 * @param <T> Tipo da entidade gerenciada
 */
public abstract class TesteBase<T> {
    protected JFrame frame;
    protected JTextArea outputArea;
    protected JTextField idField;
    protected IRepository<T> repository;

    /**
     * Construtor que inicializa a janela principal e o repositório.
     *
     * @param title Título da janela
     * @param width Largura da janela
     * @param height Altura da janela
     * @param repository Repositório da entidade
     */
    public TesteBase(String title, int width, int height, IRepository<T> repository) {
        this.repository = repository;
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        idField = new JTextField();
        idField.setToolTipText("Digite o ID para ações como mover para lixeira ou excluir");
    }

    /**
     * Cria o painel de entrada específico para a entidade.
     * @return JPanel com os campos de entrada
     */
    protected abstract JPanel createInputPanel();

    /**
     * Adiciona ações específicas aos botões (ex.: Adicionar/Atualizar, Testar Conexão).
     * @param buttonPanel Painel onde os botões serão adicionados
     */
    protected abstract void addSpecificButtonActions(JPanel buttonPanel);

    /**
     * Converte uma entidade em uma representação de string para exibição.
     * @param entity Entidade a ser convertida
     * @return String representando a entidade
     */
    protected abstract String entityToString(T entity);

    /**
     * Limpa os campos de entrada específicos da entidade após uma ação.
     */
    protected abstract void clearInputFields();

    /**
     * Configura os botões comuns a todas as interfaces de teste (Listar Ativos, etc.).
     * @param buttonPanel Painel onde os botões serão adicionados
     */
    protected void setupCommonButtons(JPanel buttonPanel) {
        JButton listAtivosButton = new JButton("Listar Ativos");
        listAtivosButton.setToolTipText("Lista todos os itens ativos (não na lixeira)");
        buttonPanel.add(listAtivosButton);

        listAtivosButton.addActionListener(e -> {
            try {
                List<T> ativos = repository.findAll();
                StringBuilder sb = new StringBuilder("Itens Ativos:\n");
                for (T item : ativos) {
                    sb.append(entityToString(item)).append("\n");
                }
                outputArea.setText(sb.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao listar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Exibe a janela da interface gráfica.
     */
    public void show() {
        JPanel inputPanel = createInputPanel();
        frame.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        addSpecificButtonActions(buttonPanel);
        setupCommonButtons(buttonPanel);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}