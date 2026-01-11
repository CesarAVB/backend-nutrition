package br.com.sistema.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.sistema.models.Consulta;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    
    List<Consulta> findByPacienteIdOrderByDataConsultaDesc(Long pacienteId);
    
    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.paciente.id = :pacienteId")
    Long countByPacienteId(Long pacienteId);
    
    @Query("SELECT c FROM Consulta c WHERE c.paciente.id = :pacienteId ORDER BY c.dataConsulta DESC")
    List<Consulta> findTopByPacienteIdOrderByDataConsultaDesc(Long pacienteId);
}