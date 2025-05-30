package mspr.backend.etl.mapper;

import mspr.backend.etl.dto.FullGroupedDto;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import mspr.backend.entity.*;
import mspr.backend.etl.helpers.*;
import mspr.backend.etl.helpers.cache.CacheManager;

@Component
public class FullGroupedMapper {

    public static final String COVID_19_DISEASE_NAME = "COVID-19";

    private final CacheManager cacheManager;
    private final CleanerHelper cleanerHelper;
    private final Disease disease;

    @Autowired
    public FullGroupedMapper(CacheManager cacheManager, CleanerHelper cleanerHelper) {
        this.cacheManager = cacheManager;
        this.cleanerHelper = cleanerHelper;
        // Preload COVID-19 disease
        this.disease = cacheManager.getOrCreateDisease(COVID_19_DISEASE_NAME);
    }

    /**
     * Converts a FullGroupedDto to a DiseaseCase entity without direct persistence.
     * Builds the Country/Region/Location hierarchy via CacheManager.
     * 
     * @param dto The DTO to convert
     * @return The mapped DiseaseCase entity or null if the country is in the skip list
     */
    public DiseaseCase toEntity(FullGroupedDto dto) {
        // Clean country name
        String countryName = cleanerHelper.cleanCountryName(dto.getCountryRegion());
        
        // Vérifier si le pays est dans la liste à ignorer
        if (cleanerHelper.isInSkipList(countryName)) {
            return null; // Ignorer ce DTO
        }
        
        // Crée le pays (ou le récupère s'il existe déjà) avec la région WHO
        Country country = cacheManager.getOrCreateCountry(countryName, null, dto.getWhoRegion());
        
        // Création automatique de la région standard pour ce pays
        Region region = cacheManager.getOrCreateRegionWithEmptyHandling(country, null);
        
        // Création automatique de la location standard pour cette région
        Location location = cacheManager.getOrCreateLocationWithEmptyHandling(region, null);

        DiseaseCase diseaseCase = new DiseaseCase();
        diseaseCase.setDisease(this.disease);
        diseaseCase.setLocation(location);
        diseaseCase.setDate(dto.getDate());
        diseaseCase.setConfirmedCases(dto.getConfirmed());
        diseaseCase.setDeaths(dto.getDeaths());
        diseaseCase.setRecovered(dto.getRecovered());

        return diseaseCase;
    }
}
