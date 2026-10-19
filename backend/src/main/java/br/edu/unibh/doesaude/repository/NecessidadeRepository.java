package br.edu.unibh.doesaude.repository;

import br.edu.unibh.doesaude.domain.Necessidade;
import br.edu.unibh.doesaude.domain.PublicoAlvo;
import br.edu.unibh.doesaude.domain.StatusNecessidade;
import br.edu.unibh.doesaude.domain.TipoNecessidade;
import br.edu.unibh.doesaude.domain.TipoSanguineo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NecessidadeRepository extends JpaRepository<Necessidade, Long> {

    List<Necessidade> findByInstituicaoIdOrderByCriadoEmDesc(Long instituicaoId);

    @Query("""
            select n from Necessidade n
            where (:tipo is null or n.tipo = :tipo)
              and (:tipoSanguineo is null or n.tipoSanguineo = :tipoSanguineo)
              and (:publicoAlvo is null or n.publicoAlvo = :publicoAlvo)
              and (:status is null or n.status = :status)
            order by n.criadoEm desc
            """)
    List<Necessidade> buscarComFiltros(
            @Param("tipo") TipoNecessidade tipo,
            @Param("tipoSanguineo") TipoSanguineo tipoSanguineo,
            @Param("publicoAlvo") PublicoAlvo publicoAlvo,
            @Param("status") StatusNecessidade status
    );
}
