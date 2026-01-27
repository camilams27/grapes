package com.grapes.config;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.grapes.domain.model.Player;
import com.grapes.domain.model.User;
import com.grapes.infrastructure.persistence.PlayerRepository;
import com.grapes.infrastructure.persistence.UserRepository;

@Configuration // Indica ao Spring que esta é uma classe de configuração/bean
public class DataInitializer implements CommandLineRunner {

    // Precisamos dos repositórios para salvar no banco
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;

    // Injeção de Dependência via Construtor (Boa prática!)
    public DataInitializer(UserRepository userRepository, PlayerRepository playerRepository) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🍇 GRAPES: Iniciando carga de dados de teste...");

        // 1. Verificar se já temos dados para não duplicar (caso reinicie o app sem
        // limpar o banco)
        if (userRepository.count() == 0) {
            criarDadosDeTeste();
        }

        System.out.println("🍇 GRAPES: Carga de dados finalizada!");
    }

    private void criarDadosDeTeste() {
        // --- PASSO A: CRIAR E SALVAR O USUÁRIO ---
        // Instanciamos o objeto User (Login)
        User novoUsuario = new User("admin@grapes.com", "senha123");

        // SALVAMOS PRIMEIRO O USER!
        // Por que? Porque o Player tem uma chave estrangeira (FK) apontando para o
        // User.
        // O User precisa existir no banco (ter um ID) antes do Player apontar para ele.
        novoUsuario = userRepository.save(novoUsuario);

        System.out.println("✅ Usuário criado com ID: " + novoUsuario.getId());

        // --- PASSO B: CRIAR O PLAYER E VINCULAR ---
        Player novoPlayer = new Player();
        novoPlayer.setNickname("AdminMaster");
        novoPlayer.setExperience(100);
        novoPlayer.setBalance(new BigDecimal("1000.00"));
        novoPlayer.setActiveSkin("default");
        // Aqui está a mágica: Vinculamos o objeto User inteiro ao Player
        novoPlayer.setUser(novoUsuario);

        // Salvamos o Player
        playerRepository.save(novoPlayer);

        System.out.println("✅ Player criado e vinculado ao usuário: " + novoPlayer.getUser().getEmail());

        // --- PASSO C: TESTAR O MÉTODO findByEmail ---
        System.out.println("🔍 Testando busca por email...");
        Optional<User> busca = userRepository.findByEmail("admin@grapes.com");

        if (busca.isPresent()) {
            System.out.println("🎯 Sucesso! Encontramos o usuário pelo email: " + busca.get().getEmail());
        } else {
            System.out.println("❌ Erro: Usuário não encontrado.");
        }
    }
}