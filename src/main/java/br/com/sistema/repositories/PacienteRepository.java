package br.com.sistema.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.sistema.models.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    List<Paciente> findByNomeCompletoContainingIgnoreCase(String nome);
    
    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.consultas WHERE p.id = :id")
    Optional<Paciente> findByIdWithConsultas(Long id);
}
