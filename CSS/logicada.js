document.addEventListener('DOMContentLoaded', function() {

    // --- SELEÇÃO DOS ELEMENTOS ---
    // Formulários
    const loginBox = document.getElementById('login-box');
    const cadastroBox = document.getElementById('cadastro-box');
    const contratanteBox = document.getElementById('form-contratante'); // Novo formulário

    // Botões
    const btnCadastrar = document.getElementById('btn-cadastrar'); // Vai para o cadastro de usuário
    const btnVoltar = document.getElementById('btn-voltar'); // Volta do cad. usuário para o login

    const forgotPasswordLink = document.querySelector('.forgot-password-link');
    if (forgotPasswordLink) {
    forgotPasswordLink.classList.add('hidden');
}
    
    // Botões do formulário de cadastro de usuário
     const btnContratante = document.querySelector('.btn-contratante'); // Vai para o cadastro de contratante
    // Botões do novo formulário de contratante
    const btnVoltarContratante = document.getElementById('btn-voltar-contratante'); // Volta do cad. contratante para o login


    // Botões da tela de login (novos e/ou com IDs específicos para o problema)
    const btnContratanteLogin = document.getElementById('btn-contratante-login'); // Esta linha foi ADICIONADA.

    
    const inputIdentificador = document.getElementById('login-identificador');///// troca///
    

    const btnLogin = document.getElementById('btn-login');
    const btnLoginContratante = document.getElementById('btn-login-contratante');

    let modoContratante = false;

    // --- FUNÇÕES DE CONTROLE ---

    // Esconde todos os formulários
    function hideAllForms() {
        loginBox.classList.add('hidden');
        cadastroBox.classList.add('hidden');
        contratanteBox.classList.add('hidden');
    }

    // --- LÓGICA DOS CLIQUES ---

    // 1. Do Login -> para Cadastro de Usuário
    btnCadastrar.addEventListener('click', function(event) {
        event.preventDefault();
        hideAllForms(); // Esconde todos
        cadastroBox.classList.remove('hidden'); // Mostra só o de cadastro
        forgotPasswordLink.classList.add('hidden'); //esconde "esqueci senha"
    });

    // 2. Do Cadastro de Usuário -> para Login
    btnVoltar.addEventListener('click', function(event) {
        event.preventDefault();
        hideAllForms(); // Esconde todos
        loginBox.classList.remove('hidden'); // Mostra só o de login
         forgotPasswordLink.classList.remove('hidden'); //esconde "esqueci senha"
    });

    // 3. Do Cadastro de Usuário -> para Cadastro de Contratante (NOVO)
    btnContratante.addEventListener('click', function(event) {
        event.preventDefault();
        hideAllForms(); // Esconde todos
        contratanteBox.classList.remove('hidden'); // Mostra só o de contratante
        forgotPasswordLink.classList.add('hidden'); //esconde "esqueci senha"
    });

    // 4. Do Cadastro de Contratante -> para Login (NOVO)
    btnVoltarContratante.addEventListener('click', function(event) {
        event.preventDefault();
        hideAllForms(); // Esconde todos
        loginBox.classList.remove('hidden'); // Mostra só o de login
        inputIdentificador.placeholder = "CPF/EMAIL";
        forgotPasswordLink.classList.remove('hidden'); //esconde "esqueci senha"
    });


    // Adicionado evento para o botão 'Contratante' da tela de login
// MUDANÇA AQUI: Corrigido o nome da variável de evento
btnContratanteLogin.addEventListener('click', function(e) {
    e.preventDefault();
    modoContratante = !modoContratante; // alterna estado

    if (modoContratante) {
        inputIdentificador.placeholder = "CNPJ/EMAIL";
        btnLogin.textContent = "Logar"; // só muda o texto do botão
    } else {
        inputIdentificador.placeholder = "CPF/EMAIL";
        btnLogin.textContent = "Logar";
    }
});

    // Seleciona o formulário de contratante
document.getElementById("btn-finalizar-contratante").addEventListener("click", function (e) {
    e.preventDefault(); // evita recarregar a página

    const data = {
        empresa: document.getElementById("contratante-empresa").value,
        name: document.getElementById("contratante-name").value,
        cnpj: document.getElementById("contratante-cnpj").value,
        telefone: document.getElementById("contratante-telefone").value,
        email: document.getElementById("contratante-email").value,
        passWord: document.getElementById("contratante-senha").value
    };

    fetch("http://localhost:8080/contratantes/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    .then(res => {
        if (res.ok) {
            alert("Cadastro realizado com sucesso!");
        } else {
            alert("Erro no cadastro!");
        }
    })
    .catch(err => console.error("Erro:", err));
});

document.getElementById("btn-finalizar-candidato").addEventListener("click", function (e) {
    e.preventDefault(); // não recarrega a página

    const data = {
        name: document.getElementById("candidato-nome").value,
        email: document.getElementById("candidato-email").value,
        cpf: document.getElementById("candidato-cpf").value,
        passWord: document.getElementById("candidato-senha").value,
        dataNascimento: document.getElementById("candidato-nascimento").value
    };

    fetch("http://localhost:8080/usuarios/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    .then(res => {
        if (res.ok) {
            alert("Candidato cadastrado com sucesso!");
            hideAllForms();
            loginBox.classList.remove('hidden');
        } else {
            return res.text().then(text => { throw new Error(text) });
        }
    })
    .catch(err => {
        console.error("Erro:", err);
        alert("Erro ao cadastrar candidato. Veja o console.");
    });
    });

    btnLogin.addEventListener('click', function(e) {
    e.preventDefault();
    const identificador = inputIdentificador.value;
    const senha = loginBox.querySelector('input[type="password"]').value;

    if (modoContratante) {
        // Login contratante
        fetch("http://localhost:8080/contratantes/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ identificador, senha })
        })
        .then(async res => {
            if (res.ok) {
                const token = await res.text();
                alert("Login contratante realizado com sucesso!");
                localStorage.setItem("token", token);
            } else {
                const msg = await res.text();
                alert("Erro no login contratante: " + msg);
            }
        })
        .catch(err => console.error("Erro:", err));
    } else {
        // Login usuário
        fetch("http://localhost:8080/usuarios/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ identificador, senha })
        })
        .then(async res => {
            if (res.ok) {
                const token = await res.text();
                alert("Login usuário realizado com sucesso!");
                console.log("Token:", token);
            } else {
                const msg = await res.text();
                alert("Erro no login usuário: " + msg);
            }
        })
        .catch(err => console.error("Erro:", err));
    }
});




});
