package cz.trailsthroughshadows.api.table.playerdata.adventure;

import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.IdResponse;
import cz.trailsthroughshadows.api.rest.model.ObjectResponse;
import cz.trailsthroughshadows.api.rest.model.MessageResponse;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import cz.trailsthroughshadows.api.table.campaign.Campaign;
import cz.trailsthroughshadows.api.table.campaign.CampaignRepo;
import cz.trailsthroughshadows.api.table.playerdata.license.License;
import cz.trailsthroughshadows.api.table.playerdata.license.LicenseRepo;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        AdventureDTO adventure = adventureRepo.findById(id).orElse(null);

        if (adventure == null) {
            RestError error = RestError.of(HttpStatus.NOT_FOUND, "Adventure not found!");
            log.warn(error.toString());
            throw new RestException(error);
        }
        return adventure;
    }

    public List<AdventureDTO> findAll() {
        return adventureRepo.findAll();
    }

    public RestResponse addAdventure(AdventureDTO adventure, Session session) {
        int limit = validationConfig.getLicense().getMaxAdventures();
        int current = adventureRepo.getCountByLicenseId(session.getLicenseId());

        if (current >= limit) {
            RestError error = RestError.of(HttpStatus.BAD_REQUEST, "License has reached the maximum number of adventures!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        if (!Objects.equals(adventure.getIdLicense(), session.getLicenseId())) {
            RestError error = RestError.of(HttpStatus.BAD_REQUEST, "License ID does not match session!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        validation.validate(adventure);

        session.getAdventures().add(adventure);
        log.debug("Saving adventure '{}' for license '{}'", adventure, session.getLicenseId());


        //TODO chce se mi grcat
        // custom insert or update
        Campaign mappedCampaign = campaignRepo.findById(adventure.getIdCampaign()).orElse(null);
        if (mappedCampaign == null) {
            RestError error = RestError.of(HttpStatus.NOT_FOUND, "Campaign not found!");
            log.warn(error.toString());
            throw new RestException(error);
        }
        adventure.setCampaign(mappedCampaign);

        License mappedLicense = licenseRepo.findById(adventure.getIdLicense()).orElse(null);
        if (mappedLicense == null) {
            RestError error = RestError.of(HttpStatus.NOT_FOUND, "License not found!");
            log.warn(error.toString());
            throw new RestException(error);
        }
        adventure.setLicense(mappedLicense);

        adventureRepo.save(adventure);

        return new IdResponse(HttpStatus.OK, adventure.getId());
    }
}
