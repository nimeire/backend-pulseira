package br.edu.fatecpg.backend_pulseira.service;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.dto.CadastroContaRequestDTO;
import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.repository.CuidadorRepository;

import lombok.RequiredArgsConstructor;

/** Cadastro público e configurações da própria conta (foto, telefone, e-mail, senha, tipo). */
@Service
@RequiredArgsConstructor
public class ContaService {

    // Mesmas regras do formulário do site; o servidor valida de novo porque o cliente não é confiável.
    private static final Pattern NOME = Pattern.compile("^\\p{L}[\\p{L}' -]*$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Pattern SENHA = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern FOTO = Pattern.compile("^data:image/(jpeg|png|webp);base64,[A-Za-z0-9+/=]+$");
    private static final int SENHA_MINIMO = 6;
    private static final int SENHA_MAXIMO = 32;
    private static final int ANO_MINIMO = 1900;
    // ~225 KB de imagem. O site reduz a foto para 256x256 antes de enviar (poucas dezenas de KB).
    private static final int FOTO_TAMANHO_MAXIMO = 300_000;

    private final CuidadorRepository cuidadorRepository;
    private final CuidadorService cuidadorService;
    private final PessoaMonitoradaService pessoaService;
    private final VinculoCuidadorService vinculoService;
    private final PulseiraService pulseiraService;
    private final PasswordEncoder passwordEncoder;

    public Cuidador cadastrar(CadastroContaRequestDTO requisicao) {
        PerfilConta perfil = requisicao.perfil() == null ? PerfilConta.RESPONSAVEL : requisicao.perfil();
        if (perfil == PerfilConta.ADMIN) {
            throw new IllegalArgumentException("Tipo de conta inválido.");
        }

        String nome = validarNome(requisicao.nome());
        String cpf = validarCpf(requisicao.cpf());
        validarDataNascimento(requisicao.dataNascimento());
        String email = validarEmail(requisicao.email());
        String telefone = validarTelefone(requisicao.telefone());
        validarSenha(requisicao.senha());

        if (cuidadorRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe uma conta com este e-mail.");
        }
        if (cuidadorRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Já existe uma conta com este CPF.");
        }

        Cuidador conta = new Cuidador();
        conta.setNome(nome);
        conta.setCpf(cpf);
        conta.setDataNascimento(requisicao.dataNascimento());
        conta.setEmail(email);
        conta.setTelefone(telefone);
        conta.setSenhaHash(passwordEncoder.encode(requisicao.senha()));
        conta.setPerfil(perfil);

        if (perfil == PerfilConta.PACIENTE) {
            conta.setPessoaMonitoradaId(criarPessoaDaConta(conta).getId());
        }

        return cuidadorService.cadastrar(conta);
    }

    /** Cria o administrador inicial (a partir da configuração do servidor), se ainda não existir. */
    public void garantirAdministrador(String email, String senha) {
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        if (cuidadorRepository.existsByEmail(emailNormalizado)) {
            return;
        }
        Cuidador admin = new Cuidador();
        admin.setNome("Administrador");
        admin.setEmail(emailNormalizado);
        admin.setSenhaHash(passwordEncoder.encode(senha));
        admin.setPerfil(PerfilConta.ADMIN);
        cuidadorService.cadastrar(admin);
    }

    /* ---------------------------------------------------------- configurações */

    /** {@code fotoUrl} nulo ou vazio remove a foto. */
    public Cuidador atualizarFoto(String contaId, String fotoUrl) {
        Cuidador conta = buscar(contaId);
        if (fotoUrl == null || fotoUrl.isBlank()) {
            conta.setFotoUrl(null);
        } else {
            if (fotoUrl.length() > FOTO_TAMANHO_MAXIMO || !FOTO.matcher(fotoUrl).matches()) {
                throw new IllegalArgumentException("Foto inválida. Use uma imagem JPEG, PNG ou WebP pequena.");
            }
            conta.setFotoUrl(fotoUrl);
        }
        return cuidadorRepository.save(conta);
    }

    public Cuidador atualizarTelefone(String contaId, String telefone) {
        Cuidador conta = buscar(contaId);
        String validado = validarTelefone(telefone);
        conta.setTelefone(validado);
        if (conta.getPerfil() == PerfilConta.PACIENTE && conta.getPessoaMonitoradaId() != null) {
            pessoaService.atualizarTelefone(conta.getPessoaMonitoradaId(), validado);
        }
        return cuidadorRepository.save(conta);
    }

    public Cuidador atualizarEmail(String contaId, String novoEmail, String senhaAtual) {
        Cuidador conta = buscar(contaId);
        exigirSenhaAtual(conta, senhaAtual);

        String email = validarEmail(novoEmail);
        if (email.equals(conta.getEmail())) {
            return conta;
        }
        if (cuidadorRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe uma conta com este e-mail.");
        }
        conta.setEmail(email);
        return cuidadorRepository.save(conta);
    }

    public Cuidador atualizarSenha(String contaId, String senhaAtual, String novaSenha) {
        Cuidador conta = buscar(contaId);
        exigirSenhaAtual(conta, senhaAtual);
        validarSenha(novaSenha);
        conta.setSenhaHash(passwordEncoder.encode(novaSenha));
        return cuidadorRepository.save(conta);
    }

    /**
     * Troca entre responsável e paciente. Só é permitido quando não deixa nada
     * para trás: antes de virar paciente a pessoa precisa ter desconectado os
     * pacientes que acompanha; antes de voltar a ser só responsável, precisa ter
     * desconectado a pulseira e os próprios responsáveis. O cadastro de paciente
     * fica guardado (inativo) e é reativado se ela voltar a ser paciente.
     */
    public Cuidador mudarPerfil(String contaId, PerfilConta novoPerfil) {
        Cuidador conta = buscar(contaId);
        PerfilConta atual = conta.getPerfil() == null ? PerfilConta.RESPONSAVEL : conta.getPerfil();

        if (atual == PerfilConta.ADMIN || novoPerfil == null || novoPerfil == PerfilConta.ADMIN) {
            throw new IllegalArgumentException("Tipo de conta inválido.");
        }
        if (atual == novoPerfil) {
            return conta;
        }

        if (novoPerfil == PerfilConta.PACIENTE) {
            if (!vinculoService.listarAtivosDoCuidador(contaId).isEmpty()) {
                throw new IllegalStateException(
                        "Desconecte os pacientes que você acompanha antes de mudar para paciente.");
            }
            if (conta.getPessoaMonitoradaId() == null) {
                conta.setPessoaMonitoradaId(criarPessoaDaConta(conta).getId());
            } else {
                pessoaService.definirAtiva(conta.getPessoaMonitoradaId(), true);
            }
        } else {
            String pessoaId = conta.getPessoaMonitoradaId();
            if (pessoaId != null) {
                if (pulseiraService.buscarPorPessoa(pessoaId).isPresent()) {
                    throw new IllegalStateException("Desconecte a sua pulseira antes de mudar para responsável.");
                }
                if (!vinculoService.listarAtivosDaPessoa(pessoaId).isEmpty()) {
                    throw new IllegalStateException("Desvincule os seus responsáveis antes de mudar para responsável.");
                }
                pessoaService.definirAtiva(pessoaId, false);
            }
        }

        conta.setPerfil(novoPerfil);
        return cuidadorRepository.save(conta);
    }

    /* ------------------------------------------------------------------ apoio */

    private Cuidador buscar(String contaId) {
        return cuidadorService.buscarAtivo(contaId);
    }

    private PessoaMonitorada criarPessoaDaConta(Cuidador conta) {
        PessoaMonitorada pessoa = new PessoaMonitorada();
        pessoa.setNome(conta.getNome());
        pessoa.setDataNascimento(conta.getDataNascimento());
        pessoa.setTelefone(conta.getTelefone());
        return pessoaService.cadastrar(pessoa);
    }

    private void exigirSenhaAtual(Cuidador conta, String senhaAtual) {
        if (senhaAtual == null || !passwordEncoder.matches(senhaAtual, conta.getSenhaHash())) {
            throw new IllegalArgumentException("A senha atual está incorreta.");
        }
    }

    private String validarNome(String valor) {
        String nome = valor == null ? "" : valor.trim();
        if (nome.isEmpty()) {
            throw new IllegalArgumentException("Informe seu nome completo.");
        }
        if (nome.length() > 100 || !NOME.matcher(nome).matches()) {
            throw new IllegalArgumentException("O nome deve ter apenas letras.");
        }
        if (!nome.contains(" ")) {
            throw new IllegalArgumentException("Informe nome e sobrenome.");
        }
        return nome;
    }

    private String validarCpf(String valor) {
        String digitos = valor == null ? "" : valor.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new IllegalArgumentException("Informe o CPF completo.");
        }
        return digitos;
    }

    private void validarDataNascimento(LocalDate data) {
        if (data == null || data.getYear() < ANO_MINIMO) {
            throw new IllegalArgumentException("Informe uma data de nascimento válida.");
        }
        if (data.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode estar no futuro.");
        }
    }

    private String validarEmail(String valor) {
        String email = valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
        if (email.length() > 254 || !EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        return email;
    }

    /** Opcional: vazio vira null; se informado, precisa ter DDD + 8 ou 9 dígitos. */
    private String validarTelefone(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String digitos = valor.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            throw new IllegalArgumentException("Informe o telefone com DDD.");
        }
        return valor.trim();
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < SENHA_MINIMO) {
            throw new IllegalArgumentException("A senha precisa ter no mínimo " + SENHA_MINIMO + " caracteres.");
        }
        if (senha.length() > SENHA_MAXIMO || !SENHA.matcher(senha).matches()) {
            throw new IllegalArgumentException("A senha deve ter apenas letras (sem acento) e números.");
        }
    }
}
