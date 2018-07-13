package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Settings;

import java.util.List;

public interface SettingsService {
    List<Settings> getAll();
    Settings getByKey(String key);
    Settings update(Long id, Settings settings);
    void delete(Long id);
}
