package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Funcionario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FuncionarioRepository {

    private final JdbcTemplate jdbc;

    public FuncionarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void cadastrar(Funcionario funcionario) {
        String sql = "INSERT INTO funcionario (nome, departamento, salario) VALUES (?, ?, ?)";
        jdbc.update(sql, funcionario.getNome(), funcionario.getDepartamento(), funcionario.getSalario());
    }

    public List<Funcionario> listar() {
        String sql = "SELECT * FROM funcionario";
        return jdbc.query(sql, new FuncionarioRowMapper());
    }

    public Funcionario buscarPorId(Long id) {
        String sql = "SELECT * FROM funcionario WHERE id = ?";
        return jdbc.queryForObject(sql, new FuncionarioRowMapper(), id);
    }

    public void editar(Funcionario funcionario) {
        String sql = "UPDATE funcionario SET nome = ?, departamento = ?, salario = ? WHERE id = ?";
        jdbc.update(sql, funcionario.getNome(), funcionario.getDepartamento(), funcionario.getSalario(), funcionario.getId());
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM funcionario WHERE id = ?";
        jdbc.update(sql, id);
    }

    private static class FuncionarioRowMapper implements RowMapper<Funcionario> {
        @Override
        public Funcionario mapRow(ResultSet rs, int rowNum) throws SQLException {
            Funcionario f = new Funcionario();
            f.setId(rs.getLong("id"));
            f.setNome(rs.getString("nome"));
            f.setDepartamento(rs.getString("departamento"));
            f.setSalario(rs.getDouble("salario"));
            return f;
        }
    }
}
