package br.com.sistema.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.sistema.models.Consulta;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByPacienteIdOrderByDataConsultaDesc(Long pacienteId);
    
    @Query("SELECT c FROM Consulta c WHERE c.paciente.id = :pacienteId ORDER BY c.dataConsulta DESC")
    List<Consulta> findUltimasConsultasPorPaciente(Long pacienteId);
    
    @Query("SELECT c FROM Consulta c " +
           "LEFT JOIN FETCH c.questionario " +
           "LEFT JOIN FETCH c.avaliacaoFisica " +
           "LEFT JOIN FETCH c.registroFotografico " +
           "WHERE c.id = :id")
    Optional<Consulta> findByIdCompleta(Long id);
    
    Optional<Consulta> findFirstByPacienteIdOrderByDataConsultaAsc(Long pacienteId);
    Optional<Consulta> findFirstByPacienteIdOrderByDataConsultaDesc(Long pacienteId);
}
