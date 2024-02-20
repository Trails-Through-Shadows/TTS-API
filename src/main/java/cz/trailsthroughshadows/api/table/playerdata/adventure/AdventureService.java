package cz.trailsthroughshadows.api.table.playerdata.adventure;

import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.response.IdResponse;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.table.campaign.Campaign;
import cz.trailsthroughshadows.api.table.campaign.CampaignRepo;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.playerdata.license.License;
import cz.trailsthroughshadows.api.table.playerdata.license.LicenseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AdventureService {

    @Autowired
    ValidationService validation;

    @Autowired
    ValidationConfig validationConfig;

    @Autowired
    AdventureRepo adventureRepo;

    @Autowired
    CampaignRepo campaignRepo;

    @Autowired
    LicenseRepo licenseRepo;

    public AdventureDTO findById(int id) {

        return adventureRepo.findById(id)
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Adventure not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });
    }

    public List<AdventureDTO> findAll() {
        return adventureRepo.findAll();
    }

    public RestResponse add(AdventureDTO adventure, Session session) {
        int limit = validationConfig.getLicense().getMaxAdventures();
        int current = adventureRepo.getCountByLicenseId(session.getLicenseId());

        if (adventure.getId() != null) {
            RestError error = RestError.of(HttpStatus.BAD_REQUEST, "ID must be null!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        if (current >= limit) {
            RestError error = RestError.of(HttpStatus.BAD_REQUEST, "License has reached the maximum number of adventures!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        if (!session.hasAccess(adventure.getIdLicense())) {
            RestError error = RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to add this resource!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        //TODO chce se mi grcat
        // custom insert or update
        Campaign mappedCampaign = campaignRepo.findById(adventure.getIdCampaign())
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Campaign not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });
        adventure.setCampaign(mappedCampaign);

        License mappedLicense = licenseRepo.findById(adventure.getIdLicense()).
                orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "License not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });
        adventure.setLicense(mappedLicense);

        validation.validate(adventure);

        log.debug("Saving adventure '{}' for license '{}'", adventure, session.getLicenseId());
        adventureRepo.save(adventure);

        return new IdResponse(HttpStatus.OK, adventure.getId());
    }

    public RestResponse delete(int id, Session session) {
        AdventureDTO adventure = adventureRepo.findById(id)
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Adventure not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });

        if (!session.hasAccess(adventure.getIdLicense())) {
            RestError error = RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to delete this resource!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        adventureRepo.delete(adventure);
        return new MessageResponse(HttpStatus.OK, "Adventure deleted!");
    }

    public RestResponse update(Integer id, AdventureDTO newAdventure, Session session) {
        AdventureDTO adventure = adventureRepo.findById(id)
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Adventure not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });

        if (!session.hasAccess(adventure.getIdLicense())) {
            RestError error = RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to update this resource!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        adventure.setTitle(newAdventure.getTitle());
        adventure.setDescription(newAdventure.getDescription());
        adventure.setReputation(newAdventure.getReputation());
        adventure.setExperience(newAdventure.getExperience());
        adventure.setGold(newAdventure.getGold());
        adventure.setLevel(newAdventure.getLevel());

        validation.validate(adventure);

        log.debug("Saving adventure '{}' for license '{}'", adventure, session.getLicenseId());
        adventureRepo.save(adventure);
        return new IdResponse(HttpStatus.OK, adventure.getId());
    }
}
