package br.com.infnet.ricknmortyapi;

import br.com.infnet.ricknmortyapi.repository.PersonagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterStatsController {

    private final PersonagemRepository repository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Object[] row : repository.countByStatus()) {
            byStatus.put((String) row[0], (Long) row[1]);
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", repository.count());
        stats.put("byStatus", byStatus);
        return ResponseEntity.ok(stats);
    }
}
