const APP_STATE = {
    currentUser: null,
    currentViewId: 'dashboard',
    txFilter: 'ALL',
    alunos: [],
    empresas: [],
    transacoes: [],
    vantagens: [],
    instituicoes: []
};

/**
 * COMUNICAÇÃO HTTP COM A API JAVA
 */
async function fetchFromApi(endpoint, options = {}) {
    const url = `/api${endpoint}`;
    const config = {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        ...options
    };
    return await fetch(url, config);
}

/**
 * AUTENTICAÇÃO REAL CONTRA A API (POST /api/auth/login)
 */
async function handleLogin(e) {
    e.preventDefault();
    const loginInput = document.getElementById('login-username').value.trim();
    const senhaInput = document.getElementById('login-password').value.trim();
    const errorBox = document.getElementById('login-error-message');
    const submitBtn = document.getElementById('btn-login-submit');

    errorBox.classList.add('hidden');
    errorBox.innerText = '';
    submitBtn.disabled = true;
    submitBtn.innerText = 'Verificando credenciais...';

    try {
        let userData = null;

        const response = await fetchFromApi('/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                login: loginInput,
                senha: senhaInput
            })
        });

        if (response.ok) {
            userData = await response.json();
        } else {
            let errorMsg = 'Login ou senha incorretos.';
            try {
                const errData = await response.json();
                if (errData && errData.erro) errorMsg = errData.erro;
            } catch (_) {}
            throw new Error(errorMsg);
        }

        // Salvar usuário autenticado na sessão
        APP_STATE.currentUser = userData;

        showToast(`Bem-vindo, ${userData.nome}!`, 'success');
        showAppUI();
        await loadDataFromApi();
    } catch (err) {

        errorBox.innerText = err.message || 'Falha ao autenticar com o servidor.';
        errorBox.classList.remove('hidden');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerText = 'Entrar no Sistema';
    }
}

async function handleLogout() {
    try {
        const response = await fetchFromApi('/auth/logout', { method: 'POST' });
        if (!response.ok && response.status !== 401) throw new Error();
        showLoginUI();
    } catch (_) {
        showToast('Falha ao encerrar a sessão. Tente novamente.', 'error');
    }
}

function showLoginUI() {
    APP_STATE.currentUser = null;
    document.querySelectorAll('[data-admin-only]').forEach(element => {
        element.hidden = true;
    });
    for (const key of ['alunos', 'empresas', 'transacoes', 'vantagens', 'instituicoes']) {
        APP_STATE[key] = [];
    }
    ['modal-send-coins', 'modal-aluno', 'modal-aluno-detalhes', 'modal-empresa', 'modal-voucher'].forEach(closeModal);

    // Voltar para a tela de login
    document.getElementById('app-container').classList.add('hidden');
    document.getElementById('login-screen').classList.remove('hidden');
    document.getElementById('form-login').reset();
    document.getElementById('login-error-message').classList.add('hidden');
}

const VIEWS_BY_ROLE = {
    ADMIN: ['dashboard', 'alunos', 'empresas', 'transacoes', 'vantagens'],
    PROFESSOR: ['transacoes', 'alunos'],
    ALUNO: ['vantagens', 'transacoes'],
    EMPRESA: ['vantagens', 'transacoes']
};

function showAppUI() {
    document.getElementById('login-screen').classList.add('hidden');
    document.getElementById('app-container').classList.remove('hidden');

    const u = APP_STATE.currentUser;
    if (!u) return;

    document.getElementById('logged-user-name').innerText = u.nome || u.login;
    document.getElementById('mobile-user-name').innerText = u.nome || u.login;

    const roleLabels = {
        ALUNO: 'Aluno',
        PROFESSOR: 'Professor',
        EMPRESA: 'Empresa Parceira',
        ADMIN: 'Administrador'
    };
    document.getElementById('logged-user-role').innerText = roleLabels[u.tipo] || u.tipo;

    const views = VIEWS_BY_ROLE[u.tipo] || [];
    document.querySelectorAll('.nav-item').forEach(button => {
        button.classList.toggle('hidden', !views.includes(button.id.slice(4)));
    });
    for (const id of ['sidebar-action-transfer', 'btn-tx-nova-transferencia']) {
        document.getElementById(id).classList.toggle('hidden', u.tipo !== 'PROFESSOR');
    }
    document.getElementById('tx-filters').classList.toggle('hidden', u.tipo === 'PROFESSOR');
    document.querySelectorAll('[data-admin-only]').forEach(element => {
        element.hidden = u.tipo !== 'ADMIN';
    });
    for (const id of ['header-balance-card', 'vantagens-balance-banner']) {
        document.getElementById(id).classList.toggle('hidden', u.tipo !== 'ALUNO');
    }
    const vantagemForm = document.getElementById('form-vantagem');
    vantagemForm.reset();
    vantagemForm.classList.toggle('hidden', u.tipo !== 'EMPRESA');
    document.getElementById('vantagens-heading').textContent = u.tipo === 'EMPRESA' ? 'Minhas vantagens' : 'Catálogo de vantagens';
    document.getElementById('vantagens-instructions').classList.toggle('hidden', u.tipo !== 'ALUNO');
    setTxFilter('ALL');
    navigateTo(views[0]);
}

/**
 * CARREGAMENTO DE DADOS DA API
 */
async function loadDataFromApi(showFeedback = false) {
    try {
        const usuario = APP_STATE.currentUser;
        if (!usuario) return false;
        const endpoints = { transacoes: '/transacoes' };
        if (usuario.tipo !== 'PROFESSOR') endpoints.vantagens = '/vantagens';
        if (usuario.tipo === 'ADMIN') {
            Object.assign(endpoints, { instituicoes: '/instituicoes', alunos: '/alunos', empresas: '/empresas' });
        }
        if (usuario.tipo === 'PROFESSOR') {
            Object.assign(endpoints, { alunos: '/alunos', instituicoes: '/instituicoes' });
        }
        if (usuario.tipo === 'ALUNO') endpoints.usuario = '/auth/me';
        const entries = await Promise.all(Object.entries(endpoints).map(async ([key, url]) => {
            const response = await fetchFromApi(url);
            return { key, status: response.status, data: response.ok ? await response.json() : null };
        }));
        if (APP_STATE.currentUser !== usuario) return false;
        if (entries.some(entry => entry.status === 401)) {
            showLoginUI();
            return false;
        }
        if (entries.some(entry => entry.data === null)) throw new Error();
        for (const { key, data } of entries) {
            if (key === 'usuario') usuario.saldo = data.saldo;
            else APP_STATE[key] = data;
        }

        updateNavigationBadges();
        updateBalanceDisplay();
        populateInstituicoesSelects();
        refreshCurrentView(APP_STATE.currentViewId);

        if (showFeedback) {
            showToast('Dados sincronizados com o servidor!', 'success');
        }
        return true;
    } catch (err) {

        if (showFeedback) {
            showToast('Servidor Java não está acessível no momento.', 'error');
        }
        return false;
    }
}

function updateBalanceDisplay() {
    let saldo = 0;
    if (APP_STATE.currentUser && APP_STATE.currentUser.tipo === 'ALUNO') {
        saldo = APP_STATE.currentUser.saldo || 0;
    } else {
        saldo = APP_STATE.alunos.reduce((acc, curr) => acc + (curr.saldo || 0), 0);
    }

    const topBar = document.getElementById('top-bar-balance');
    if (topBar) topBar.innerText = `${saldo.toLocaleString()} $M`;

    const userBalance = document.getElementById('user-current-balance');
    if (userBalance) userBalance.innerText = `${saldo.toLocaleString()} $M`;
}
