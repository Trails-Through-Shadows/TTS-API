package cz.trailsthroughshadows.api.table.campaign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/campaign")
public class CampaignController {
    @Autowired
    private CampaignRepo repo;

    @GetMapping("/campaigns")
    public Collection<Campaign> getAll() {
        return repo.findAll();
    }

    @GetMapping("/campaigns/{id}")
    public Campaign findById(@PathVariable int id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid campaign Id:" + id));
    }

}
