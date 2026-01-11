package br.com.sistema.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sistema.models.AvaliacaoFisica;

@Repository
public interface AvaliacaoFisicaRepository extends JpaRepository<AvaliacaoFisica, Long> {
    Optional<AvaliacaoFisica> findByConsultaId(Long consultaId);
    boolean existsByConsultaId(Long consultaId);
    void deleteByConsultaId(Long consultaId);
}