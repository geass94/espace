package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Settings;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.SettingsRepository;
import ge.boxwood.espace.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {
    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public List<Settings> getAll() {
        return settingsRepository.findAllByStatus(Status.ACTIVE);
    }

    @Override
    public Settings getByKey(String key) {
        return settingsRepository.findByKeyAndStatus(key, Status.ACTIVE);
    }

    @Override
    public Settings update(Long id, Settings settings) {
        Settings original = settingsRepository.findOne(id);
        if (!settings.getValue().isEmpty() && settings.getValue() != null && original.getValue() != settings.getValue()){
            original.setValue(settings.getValue());
        }
        if (!settings.getName().isEmpty() && settings.getName() != null && original.getName() != settings.getName()){
            original.setName(settings.getName());
        }
        original = settingsRepository.save(original);
        return original;
    }

    @Override
    public void delete(Long id) {
        Settings settings = settingsRepository.findOne(id);
        settings.setStatus(Status.DELETED);
        settingsRepository.save(settings);
    }
}
