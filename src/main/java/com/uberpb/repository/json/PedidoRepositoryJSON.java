package com.uberpb.repository.json;

import com.uberpb.model.Pedido;
import com.uberpb.repository.BaseRepository;

import java.util.List;

public class PedidoRepositoryJSON extends BaseRepository<Pedido> {

    public PedidoRepositoryJSON() {
        super("pedidos");
    }

    public Pedido save(Pedido pedido) {
        lock.writeLock().lock();
        try {
            if (pedido.getId() == 0)
                pedido.setId(generateNextId());

            List<Pedido> pedidos = loadAll();
            pedidos.add(pedido);
            saveAll(pedidos);
            return pedido;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Pedido> findAll() {
        return loadAll();
    }
    @Override
    protected int getId(Pedido entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Pedido entity, int id) {
        entity.setId(id);
    }

    public Pedido update(Pedido pedidoAtualizado) {
        lock.writeLock().lock();
        try {
            List<Pedido> pedidos = loadAll();

            for (int i = 0; i < pedidos.size(); i++) {
                if (pedidos.get(i).getId() == pedidoAtualizado.getId()) {
                    pedidos.set(i, pedidoAtualizado);
                    saveAll(pedidos);
                    return pedidoAtualizado;
                }
            }

            throw new RuntimeException("Pedido não encontrado para atualização");

        } finally {
            lock.writeLock().unlock();
        }
    }
}