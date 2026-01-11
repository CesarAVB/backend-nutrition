package br.com.sistema.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sistema.models.QuestionarioEstiloVida;

@Repository
public interface QuestionarioEstiloVidaRepository extends JpaRepository<QuestionarioEstiloVida, Long> {
    Optional<QuestionarioEstiloVida> findByConsultaId(Long consultaId);
    boolean existsByConsultaId(Long consultaId);
    void deleteByConsultaId(Long consultaId);
}