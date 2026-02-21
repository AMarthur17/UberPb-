package com.uberpb.repository.json;

import com.uberpb.model.Restaurante;
import com.uberpb.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public class RestauranteRepositoryJSON extends BaseRepository<Restaurante> {

    public RestauranteRepositoryJSON() {
        super("restaurantes"); // Cria e usa database/restaurantes/restaurantes.json
    }

    public Restaurante save(Restaurante restaurante) {
        lock.writeLock().lock();
        try {
            if (restaurante.getId() == 0) restaurante.setId(generateNextId());
            List<Restaurante> restaurantes = loadAll();
            restaurantes.add(restaurante);
            saveAll(restaurantes);
            return restaurante;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Restaurante> findById(int id) {
        lock.readLock().lock();
        try {
            return loadAll().stream().filter(r -> r.getId() == id).findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void update(Restaurante restauranteAtualizado) {
        lock.writeLock().lock();
        try {
            List<Restaurante> restaurantes = loadAll();
            for (int i = 0; i < restaurantes.size(); i++) {
                if (restaurantes.get(i).getId() == restauranteAtualizado.getId()) {
                    restaurantes.set(i, restauranteAtualizado);
                    saveAll(restaurantes);
                    return;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    protected int getId(Restaurante entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Restaurante entity, int id) {
        entity.setId(id);
    }

    /**
     * Recupera restaurantes disponíveis (abertos) do sistema
     */
    public List<Restaurante> findDisponiveis() {
        lock.readLock().lock();
        try {
            return loadAll().stream()
                .filter(Restaurante::isAberto)
                .toList();
        } finally {
            lock.readLock().unlock();
        }
    }
}