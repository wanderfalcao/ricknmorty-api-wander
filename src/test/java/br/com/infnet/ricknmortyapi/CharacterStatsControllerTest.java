package br.com.infnet.ricknmortyapi;

import br.com.infnet.ricknmortyapi.repository.PersonagemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CharacterStatsController.class)
class CharacterStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonagemRepository repository;

    @Test
    void stats_retornaTotalEContadorPorStatus() throws Exception {
        when(repository.count()).thenReturn(3L);
        when(repository.countByStatus()).thenReturn(List.of(
                new Object[]{"Alive", 2L},
                new Object[]{"Dead", 1L}
        ));

        mockMvc.perform(get("/api/characters/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.byStatus.Alive").value(2))
                .andExpect(jsonPath("$.byStatus.Dead").value(1));
    }

    @Test
    void stats_semPersonagens_retornaZero() throws Exception {
        when(repository.count()).thenReturn(0L);
        when(repository.countByStatus()).thenReturn(List.of());

        mockMvc.perform(get("/api/characters/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.byStatus").isMap());
    }
}
