package cz.trailsthroughshadows.api.table.effect;


import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/effects")
public class EffectController {

    @Autowired
    private EffectRepo repository;

    @GetMapping("/{id}")
    public EffectDTO findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid effect Id:" + id));
    }

    @GetMapping("")
    public Iterable<EffectDTO> findAll() {
        return repository.getAll();
    }

}
