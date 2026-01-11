package br.com.sistema.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sistema.models.RegistroFotografico;

@Repository
public interface RegistroFotograficoRepository extends JpaRepository<RegistroFotografico, Long> {
    Optional<RegistroFotografico> findByConsultaId(Long consultaId);
}