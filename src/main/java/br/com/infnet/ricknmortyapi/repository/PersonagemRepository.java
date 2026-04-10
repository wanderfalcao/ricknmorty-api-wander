package br.com.infnet.ricknmortyapi.repository;

import br.com.infnet.ricknmortyapi.model.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonagemRepository extends JpaRepository<Personagem, Long> {

    @Query("SELECT p.status, COUNT(p) FROM Personagem p GROUP BY p.status ORDER BY p.status")
    List<Object[]> countByStatus();
}
