package br.senac.tads.pi3a.agenda;

import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Agenda {

    private Connection obterConexao() throws ClassNotFoundException, SQLException {
        //1A) Registrar drive JDBC
        Class.forName("com.mysql.jdbc.Driver");

        //1B) Abrir conexão com o BD
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/agendabd", "root", "");
    }

    public List<Pessoa> consultar() throws ClassNotFoundException, SQLException {
        //2) Escrever a lógica para executar algum comando no banco
        String query = "SELECT id, nome, dtnascimento FROM pessoa";

        List<Pessoa> lista = new ArrayList<Pessoa>();

        try (Connection conn = obterConexao();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet resultados = stmt.executeQuery()) {
                while (resultados.next()) {
                    Pessoa p = new Pessoa();
                    p.setId(resultados.getLong("id"));
                    p.setNome(resultados.getString("nome"));
                    p.setDtNascimento(resultados.getDate("dtnascimento"));
                    lista.add(p);
                }
            }
        } catch (Exception ex) {

        }

        return lista;
    }

    public void incluir(Pessoa p, Contato email, Contato telefone) throws ClassNotFoundException, SQLException {
        String query = "INSERT INTO pessoa (nome, dtnascimento) VALUES (?, ?)";
        String queryContato = "INSERT INTO contato (tipo, valor, idpessoa) VALUES (?, ?, ?)";

        try (Connection conn = obterConexao()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, p.getNome());
                stmt.setDate(2, new java.sql.Date(p.getDtNascimento().getTime()));
                stmt.executeUpdate();

                //tenta recuperar o ID gerado no banco de dados
                try (ResultSet chaves = stmt.getGeneratedKeys()) {
                    if (chaves.next()) {
                        long idPessoa = chaves.getLong(1);
                        
                        try (PreparedStatement stmt2 = conn.prepareStatement(queryContato)) {
                            stmt2.setInt(1, email.getTipo());
                            stmt2.setString(2, email.getValor());
                            stmt2.setLong(3, idPessoa);
                            stmt2.executeUpdate();
                        }
                        try (PreparedStatement stmt3 = conn.prepareStatement(queryContato)) {
                            stmt3.setInt(1, telefone.getTipo());
                            stmt3.setString(2, telefone.getValor());
                            stmt3.setLong(3, idPessoa);
                            stmt3.executeUpdate();
                        }
                        conn.commit();
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        }

    }

    public static void main(String[] args) {
        Agenda agenda = new Agenda();
        try {
            Pessoa p1 = new Pessoa();
            try (Scanner console = new Scanner(System.in)) {
                System.out.println("Digite o nome: ");
                String nome = console.nextLine();

                p1.setNome(nome);
                String strDtNascimento = "1970-05-15";
                DateFormat formatadorData = new SimpleDateFormat("yyyy-MM-dd");
                //Date dtNascimento = formatadorData.parse(strDtNascimento);
                p1.setDtNascimento(formatadorData.parse(strDtNascimento));
                Contato email = new Contato();
                email.setTipo(1);
                email.setValor("fulano@gmail.com");

                Contato telefone = new Contato();
                telefone.setTipo(2);
                telefone.setValor("(11) 9999-9999");
                agenda.incluir(p1, email, telefone);
            }
            List<Pessoa> lista = agenda.consultar();
            for (Pessoa p : lista) {
                System.out.println(p.getId() + ", " + p.getNome() + ", " + p.getDtNascimento());
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Agenda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Agenda.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
