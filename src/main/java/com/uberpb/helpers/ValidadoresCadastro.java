package com.uberpb.helpers;

import java.util.regex.Pattern;

public class ValidadoresCadastro {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static boolean validarCampoObrigatorio(String campo, String nomeCampo) {
        if (campo == null || campo.trim().isEmpty()) {
            System.out.println("ERRO: " + nomeCampo + " é obrigatório!");
            return false;
        }
        return true;
    }

    public static boolean validarEmail(String email) {
        if (!validarCampoObrigatorio(email, "Email"))
            return false;
        String emailLimpo = email.trim();
        if (!EMAIL_PATTERN.matcher(emailLimpo).matches()) {
            System.out.println("ERRO: Formato de e-mail inválido! Use formato: exemplo@dominio.com");
            return false;
        }
        return true;
    }

    public static boolean validarSenha(String senha) {
        if (!validarCampoObrigatorio(senha, "Senha"))
            return false;
        String senhaLimpa = senha.trim();
        if (senhaLimpa.length() < 5) {
            System.out.println("ERRO: Senha deve ter no mínimo 5 caracteres!");
            return false;
        }
        if (!senhaLimpa.chars().anyMatch(Character::isUpperCase)) {
            System.out.println("ERRO: Senha deve conter pelo menos 1 caractere maiúsculo!");
            return false;
        }
        if (!senhaLimpa.chars().anyMatch(Character::isDigit)) {
            System.out.println("ERRO: Senha deve conter pelo menos 1 número!");
            return false;
        }
        return true;
    }

    public static boolean validarNome(String nome) {
        if (!validarCampoObrigatorio(nome, "Nome"))
            return false;
        String nomeLimpo = nome.trim();
        if (nomeLimpo.matches("^[0-9]+$")) {
            System.out.println("ERRO: Nome não pode conter apenas números!");
            return false;
        }
        if (nomeLimpo.length() < 2) {
            System.out.println("ERRO: Nome deve ter pelo menos 2 caracteres!");
            return false;
        }
        return true;
    }

    public static boolean validarTelefone(String telefone) {
        if (!validarCampoObrigatorio(telefone, "Telefone"))
            return false;
        String telefoneLimpo = telefone.trim().replaceAll("[\\s()-]", "");
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            System.out.println("ERRO: Telefone deve ter 10 ou 11 dígitos!");
            return false;
        }
        if (!telefoneLimpo.matches("^[0-9]+$")) {
            System.out.println("ERRO: Telefone deve conter apenas números!");
            return false;
        }
        return true;
    }

    public static boolean validarIdade(int idade) {
        if (idade < 0) {
            System.out.println("ERRO: Idade não pode ser negativa!");
            return false;
        }
        if (idade > 120) {
            System.out.println("ERRO: Idade não pode ser maior que 120 anos!");
            return false;
        }
        return true;
    }

    public static boolean validarCNH(String cnh) {
        if (!validarCampoObrigatorio(cnh, "CNH"))
            return false;
        String cnhLimpa = cnh.trim().replaceAll("[\\s.-]", "");
        if (cnhLimpa.length() != 11) {
            System.out.println("ERRO: CNH deve ter exatamente 11 dígitos!");
            return false;
        }
        if (!cnhLimpa.matches("^[0-9]+$")) {
            System.out.println("ERRO: CNH deve conter apenas números!");
            return false;
        }
        if (cnhLimpa.matches("(.)\\1{10}")) {
            System.out.println("ERRO: CNH não pode ter todos os dígitos iguais!");
            return false;
        }
        return true;
    }

    public static boolean validarDataValidade(String data) {
        if (!validarCampoObrigatorio(data, "Data de validade"))
            return false;
        String dataLimpa = data.trim();
        if (!dataLimpa.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            System.out.println("ERRO: Data deve estar no formato dd/mm/aaaa (ex: 15/12/2025)!");
            return false;
        }
        String[] partes = dataLimpa.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        if (dia < 1 || dia > 31) {
            System.out.println("ERRO: Dia deve estar entre 01 e 31!");
            return false;
        }
        if (mes < 1 || mes > 12) {
            System.out.println("ERRO: Mês deve estar entre 01 e 12!");
            return false;
        }
        int anoAtual = java.time.LocalDate.now().getYear();
        if (ano < anoAtual) {
            System.out.println("ERRO: CNH não pode estar vencida!");
            return false;
        }
        if (ano > anoAtual + 10) {
            System.out.println("ERRO: Data de validade muito distante no futuro!");
            return false;
        }
        if (mes == 2) {
            boolean bissexto = (ano % 4 == 0 && ano % 100 != 0) || (ano % 400 == 0);
            if (dia > (bissexto ? 29 : 28)) {
                System.out.println("ERRO: Fevereiro não pode ter mais de " + (bissexto ? "29" : "28") + " dias!");
                return false;
            }
        } else if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
            if (dia > 30) {
                System.out.println("ERRO: Este mês só pode ter até 30 dias!");
                return false;
            }
        }
        return true;
    }

    public static boolean validarCNPJ(String cnpj) {
        if (!validarCampoObrigatorio(cnpj, "CNPJ")) return false;
        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
        if (cnpjLimpo.length() != 14) {
            System.out.println("ERRO: CNPJ deve ter 14 dígitos!");
            return false;
        }
        return true;
    }

    public static boolean validarTipoVeiculoEntregador(String tipo) {
        if (!validarCampoObrigatorio(tipo, "Tipo de Veículo")) return false;
        String t = tipo.trim().toUpperCase();
        if (!t.equals("MOTO") && !t.equals("BICICLETA")) {
            System.out.println("ERRO: O veículo do entregador deve ser MOTO ou BICICLETA!");
            return false;
        }
        return true;
    }
}
