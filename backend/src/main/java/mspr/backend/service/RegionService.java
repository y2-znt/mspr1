package mspr.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import mspr.backend.entity.Region;
import mspr.backend.repository.RegionRepository;

@Service
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    public Page<Region> getAllRegions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return regionRepository.findAll(pageable);
    }

    public Optional<Region> getRegionById(Integer id) {
        return regionRepository.findById(id);
    }
    
    public Region getRegionByName(String name) {
        return regionRepository.findByName(name);
    }

    public Region createRegion(Region region) {
        return regionRepository.save(region);
    }

    public Region updateRegion(Integer id, Region region) {
        if (regionRepository.existsById(id)) {
            region.setId(id);
            return regionRepository.save(region);
        } else {
            return null;
        }
    }

    public void deleteRegion(Integer id) {
        regionRepository.deleteById(id);
    }


}
