package br.com.sistema.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.sistema.models.QuestionarioEstiloVida;

@Repository
public interface QuestionarioEstiloVidaRepository extends JpaRepository<QuestionarioEstiloVida, Long> {
    
    // ✅ USE NATIVE QUERY COMPLETA
    @Query(value = "SELECT * FROM tbl_questionarios_estilo_vida WHERE consulta_id = ?1", nativeQuery = true)
    Optional<QuestionarioEstiloVida> findByConsultaId(Long consultaId);
    
    @Query(value = "SELECT COUNT(*) > 0 FROM tbl_questionarios_estilo_vida WHERE consulta_id = ?1", nativeQuery = true)
    boolean existsByConsultaId(Long consultaId);
    
}