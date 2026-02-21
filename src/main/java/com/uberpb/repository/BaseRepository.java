package com.uberpb.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class BaseRepository<T> {

    protected static final String DATA_DIR = "database";
    protected final ObjectMapper objectMapper;
    protected final ReadWriteLock lock;
    protected final Path dataPath;
    protected final String entityName;

    public BaseRepository(String entityName) {
        this.entityName = entityName;
        this.dataPath = Paths.get(DATA_DIR, entityName, entityName + ".json");
        this.lock = new ReentrantReadWriteLock();

        // Inicializar ObjectMapper com suporte a LocalDateTime
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // suporte a LocalDateTime
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // JSON legível
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // evita timestamps

        // Criar diretório e arquivo se não existirem
        initializeDataFile();
    }

    private void initializeDataFile() {
        try {
            Files.createDirectories(dataPath.getParent());
            if (!Files.exists(dataPath)) {
                Files.write(dataPath, "[]".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao inicializar arquivo de dados para " + entityName, e);
        }
    }

    @SuppressWarnings("unchecked")
    protected List<T> loadAll() {
        lock.readLock().lock();
        try {
            if (!Files.exists(dataPath) || Files.size(dataPath) == 0) {
                return new ArrayList<>();
            }

            String content = Files.readString(dataPath);
            if (content.trim().isEmpty()) {
                return new ArrayList<>();
            }

            switch (entityName) {
                case "users":
                    return (List<T>) objectMapper.readValue(content, new TypeReference<List<com.uberpb.model.User>>() {
                    });
                case "passageiros":
                    return (List<T>) objectMapper.readValue(content,
                            new TypeReference<List<com.uberpb.model.Passageiro>>() {
                            });
                case "motoristas":
                    return (List<T>) objectMapper.readValue(content,
                            new TypeReference<List<com.uberpb.model.Motorista>>() {
                            });
                case "veiculos":
                    return (List<T>) objectMapper.readValue(content,
                            new TypeReference<List<com.uberpb.model.Veiculo>>() {
                            });
                case "corridas":
                    return (List<T>) objectMapper.readValue(content,
                            new TypeReference<List<com.uberpb.model.Corrida>>() {
                            });
                case "pagamentos":
                    return (List<T>) objectMapper.readValue(content,
                            new TypeReference<List<com.uberpb.model.pagamento.Pagamento>>() {
                            });
                case "avaliacoes":
                    return (List<T>) objectMapper.readValue(content,
                            new TypeReference<List<com.uberpb.model.Avaliacao>>() {
                            });
                case "entregadores":
                    return (List<T>) objectMapper.readValue(content,
                            new TypeReference<List<com.uberpb.model.Entregador>>() {
                            });
                case "restaurantes":
                    return (List<T>) objectMapper.readValue(content,
                            new TypeReference<List<com.uberpb.model.Restaurante>>() {
                            });
                default:
                    return objectMapper.readValue(content, new TypeReference<List<T>>() {
                    });
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar " + entityName + "do arquivo", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    protected void saveAll(List<T> entities) {
        lock.writeLock().lock();
        try {
            String jsonContent = objectMapper.writeValueAsString(entities);
            Files.write(dataPath, jsonContent.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar " + entityName + " no arquivo", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected int generateNextId() {
        lock.writeLock().lock();
        try {
            Path idCounterPath = Paths.get(DATA_DIR, "id_counter.json");
            Map<String, Integer> counters;

            if (Files.exists(idCounterPath)) {
                String content = Files.readString(idCounterPath);
                counters = objectMapper.readValue(content, new TypeReference<Map<String, Integer>>() {
                });
            } else {
                counters = new java.util.HashMap<>();
            }

            int currentId = counters.getOrDefault(entityName, 0);
            int nextId = currentId + 1;
            counters.put(entityName, nextId);

            String jsonContent = objectMapper.writeValueAsString(counters);
            Files.write(idCounterPath, jsonContent.getBytes());

            return nextId;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar ID único para " + entityName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected abstract int getId(T entity);

    protected abstract void setId(T entity, int id);
}
