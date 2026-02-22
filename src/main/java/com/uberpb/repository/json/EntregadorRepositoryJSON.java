package com.uberpb.repository.json;

import com.uberpb.model.Entregador;
import com.uberpb.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntregadorRepositoryJSON extends BaseRepository<Entregador> {

    public EntregadorRepositoryJSON() {
        super("entregadores"); // Cria e usa database/entregadores/entregadores.json
    }

    public Entregador save(Entregador entregador) {
        lock.writeLock().lock();
        try {
            if (entregador.getId() == 0) entregador.setId(generateNextId());
            List<Entregador> entregadores = loadAll();
            entregadores.add(entregador);
            saveAll(entregadores);
            return entregador;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Entregador> findById(int id) {
        lock.readLock().lock();
        try {
            return loadAll().stream().filter(e -> e.getId() == id).findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void update(Entregador entregadorAtualizado) {
        lock.writeLock().lock();
        try {
            List<Entregador> entregadores = loadAll();
            for (int i = 0; i < entregadores.size(); i++) {
                if (entregadores.get(i).getId() == entregadorAtualizado.getId()) {
                    entregadores.set(i, entregadorAtualizado);
                    saveAll(entregadores);
                    return;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Entregador> findAll() {
        lock.readLock().lock();
        try {
            return loadAll();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Entregador> findDisponiveis() {
        lock.readLock().lock();
        try {
            return loadAll().stream()
                    .filter(Entregador::isDisponivel)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }


    @Override
    protected int getId(Entregador entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Entregador entity, int id) {
        entity.setId(id);
    }
}