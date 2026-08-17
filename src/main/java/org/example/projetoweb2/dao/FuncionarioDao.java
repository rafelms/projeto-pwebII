package org.example.projetoweb2.dao;

import org.example.projetoweb2.model.Funcionario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository // O Spring utiliza a anotação @Repository para indicar que esta classe é a camada de acesso a dados (DAO)
public class FuncionarioDao {

    private final JdbcTemplate jdbc;

    public FuncionarioDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void adicionarNovoFuncionario(Funcionario funcionario) {
        String comandoSql = "INSERT INTO funcionario (nome, departamento, salario) VALUES (?, ?, ?)";
        jdbc.update(comandoSql, funcionario.getNome(), funcionario.getDepartamento(), funcionario.getSalario());
    }

    public List<Funcionario> obterTodosOsFuncionarios() {
        String comandoSql = "SELECT * FROM funcionario";
        return jdbc.query(comandoSql, new MapeadorDeLinhaFuncionario());
    }

    public Funcionario buscarFuncionarioPeloId(Long id) {
        String comandoSql = "SELECT * FROM funcionario WHERE id = ?";
        return jdbc.queryForObject(comandoSql, new MapeadorDeLinhaFuncionario(), id);
    }

    public void atualizarDadosDoFuncionario(Funcionario funcionario) {
        String comandoSql = "UPDATE funcionario SET nome = ?, departamento = ?, salario = ? WHERE id = ?";
        jdbc.update(comandoSql, funcionario.getNome(), funcionario.getDepartamento(), funcionario.getSalario(), funcionario.getId());
    }

    public void deletarFuncionarioDoBanco(Long id) {
        String comandoSql = "DELETE FROM funcionario WHERE id = ?";
        jdbc.update(comandoSql, id);
    }

    //Conversor de dados para obj java
    private static class MapeadorDeLinhaFuncionario implements RowMapper<Funcionario> {
        @Override
        public Funcionario mapRow(ResultSet resultado, int numeroDaLinha) throws SQLException {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(resultado.getLong("id"));
            funcionario.setNome(resultado.getString("nome"));
            funcionario.setDepartamento(resultado.getString("departamento"));
            funcionario.setSalario(resultado.getDouble("salario"));
            return funcionario;
        }
    }
}
