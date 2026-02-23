package com.uberpb.test;

import com.uberpb.enums.Categoria;
import com.uberpb.model.Corrida;
import com.uberpb.enums.CorridaStatus;
import com.uberpb.repository.CorridaRepository;
import com.uberpb.repository.json.CorridaRepositoryJSON;

import java.util.List;

public class TestCorridaRepositoryCompleto {

    public static void main(String[] args) {
        CorridaRepository corridaRepo = new CorridaRepositoryJSON();

        // ===== Criar corridas de teste =====
        Corrida c1 = new Corrida(0, "Aeroporto", "Shopping", Categoria.UBER_X, 1, 10, 100, 12.5);
        Corrida c2 = new Corrida(0, "Centro", "Hospital", Categoria.COMFORT, 2, 11, 101, 5.0);
        Corrida c3 = new Corrida(0, "Aeroporto", "Praia", Categoria.BLACK, 1, 10, 100, 20.0);

        // ===== Salvar corridas =====
        corridaRepo.save(c1);
        corridaRepo.save(c2);
        corridaRepo.save(c3);

        // ===== Testar findAll =====
        System.out.println("=== Todas as corridas ===");
        corridaRepo.findAll()
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar findByMotoristaId =====
        System.out.println("\n=== Corridas do motorista 10 ===");
        corridaRepo.findByMotoristaId(10)
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar findByPassageiroId =====
        System.out.println("\n=== Corridas do passageiro 1 ===");
        corridaRepo.findByPassageiroId(1)
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar findByStatus =====
        System.out.println("\n=== Corridas pendentes ===");
        corridaRepo.findByStatus(CorridaStatus.PENDENTE)
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar countByStatus =====
        System.out.println("\nTotal de corridas pendentes: " + corridaRepo.countByStatus(CorridaStatus.PENDENTE));

        // ===== Testar getTotalReceitaByMotorista =====
        System.out.println("Receita total do motorista 10: " + corridaRepo.getTotalReceitaByMotorista(10));

        // ===== Testar findEmAndamento =====
        c1.iniciarCorrida();
        corridaRepo.update(c1);
        System.out.println("\n=== Corridas em andamento ===");
        corridaRepo.findByStatus(CorridaStatus.EM_ANDAMENTO)
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar findByOrigem =====
        System.out.println("\n=== Corridas com origem A ===");
        corridaRepo.findByOrigem("A")
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar findByDestino =====
        System.out.println("\n=== Corridas com destino C ===");
        corridaRepo.findByDestino("C")
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar findByOrigemAndDestino =====
        System.out.println("\n=== Corridas de A para C ===");
        corridaRepo.findByOrigemAndDestino("A", "C")
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar findByCategoria =====
        System.out.println("\n=== Corridas categoria BLACK ===");
        corridaRepo.findByCategoria(Categoria.BLACK)
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar update =====
        c2.setDestino("D");
        corridaRepo.update(c2);
        System.out.println("\n=== Corridas atualizadas (c2) ===");
        corridaRepo.findAll()
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));

        // ===== Testar delete =====
        corridaRepo.deleteById(c3.getId());
        System.out.println("\n=== Corridas após deletar c3 ===");
        corridaRepo.findAll()
                .forEach(c -> System.out.println(c.getId() + ": " + c.getOrigem() + " -> " + c.getDestino()));
    }
}
