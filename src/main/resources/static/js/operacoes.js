/**
 * CRUD: ALUNOS
 */
function openAlunoFormModal(alunoId = null) {
    if (APP_STATE.currentUser?.tipo !== 'ADMIN') return;
    populateInstituicoesSelects();
    const form = document.getElementById('form-aluno');
    form.reset();

    if (alunoId) {
        const aluno = APP_STATE.alunos.find(a => a.id === alunoId);
        if (aluno) {
            document.getElementById('modal-aluno-title').innerText = 'Editar Aluno';
            document.getElementById('aluno-id').value = aluno.id;
            document.getElementById('aluno-nome').value = aluno.nome || '';
            document.getElementById('aluno-cpf').value = aluno.cpf || '';
            document.getElementById('aluno-rg').value = aluno.rg || '';
            document.getElementById('aluno-email').value = aluno.email || '';
            document.getElementById('aluno-endereco').value = aluno.endereco || '';
            document.getElementById('aluno-instituicao').value = aluno.instituicaoId || '';
            document.getElementById('aluno-curso').value = aluno.curso || '';
        }
    } else {
        document.getElementById('modal-aluno-title').innerText = 'Cadastrar Novo Aluno';
        document.getElementById('aluno-id').value = '';
    }

    openModal('modal-aluno');
}

async function handleSaveAluno(e) {
    e.preventDefault();
    if (APP_STATE.currentUser?.tipo !== 'ADMIN') return;
    const id = document.getElementById('aluno-id').value;
    const instSelect = document.getElementById('aluno-instituicao');

    const payload = {
        nome: document.getElementById('aluno-nome').value.trim(),
        cpf: document.getElementById('aluno-cpf').value.trim(),
        rg: document.getElementById('aluno-rg').value.trim(),
        email: document.getElementById('aluno-email').value.trim(),
        endereco: document.getElementById('aluno-endereco').value.trim(),
        instituicaoId: parseInt(instSelect.value) || null,
        curso: document.getElementById('aluno-curso').value.trim()
    };

    try {
        const endpoint = id ? `/alunos/${id}` : '/alunos';
        const method = id ? 'PUT' : 'POST';

        const response = await fetchFromApi(endpoint, {
            method: method,
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Erro ao salvar aluno.');
        }

        closeModal('modal-aluno');
        showToast(id ? 'Aluno atualizado!' : 'Novo aluno cadastrado!', 'success');
        await loadDataFromApi();
    } catch (err) {

        showToast(err.message || 'Falha ao salvar aluno.', 'error');
    }
}

async function deleteAluno(id) {
    if (APP_STATE.currentUser?.tipo !== 'ADMIN') return;
    if (!await askConfirmation('Remover aluno?', 'Deseja realmente remover este discente do sistema?', 'Remover aluno')) return;

    try {
        const response = await fetchFromApi(`/alunos/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Falha ao remover aluno.');

        showToast('Aluno removido com sucesso.', 'info');
        await loadDataFromApi();
    } catch (err) {

        showToast('Não foi possível remover o aluno.', 'error');
    }
}

function viewAlunoDetails(id) {
    if (APP_STATE.currentUser?.tipo !== 'ADMIN') return;
    const aluno = APP_STATE.alunos.find(a => a.id === id);
    if (!aluno) return;

    const container = document.getElementById('aluno-detalhes-content');
    container.innerHTML = `
        <div class="p-3 bg-slate-50 border border-slate-200 rounded">
            <span class="text-[11px] text-slate-500 uppercase block font-semibold">Nome Completo</span>
            <strong class="text-sm text-slate-900 block">${escapeHtml(aluno.nome)}</strong>
            <span class="text-xs text-slate-600 block mt-0.5">${escapeHtml(aluno.curso || 'Graduação')} · ${escapeHtml(aluno.instituicaoNome || 'PUC Minas')}</span>
        </div>

        <div class="grid grid-cols-2 gap-2 text-xs">
            <div class="p-2.5 bg-white border border-slate-200 rounded">
                <span class="text-[10px] text-slate-400 uppercase font-semibold block">CPF</span>
                <span class="font-mono text-slate-800 font-medium">${escapeHtml(aluno.cpf)}</span>
            </div>
            <div class="p-2.5 bg-white border border-slate-200 rounded">
                <span class="text-[10px] text-slate-400 uppercase font-semibold block">RG</span>
                <span class="font-mono text-slate-800 font-medium">${escapeHtml(aluno.rg || 'Não informado')}</span>
            </div>
            <div class="p-2.5 bg-white border border-slate-200 rounded col-span-2">
                <span class="text-[10px] text-slate-400 uppercase font-semibold block">E-mail</span>
                <span class="text-slate-800">${escapeHtml(aluno.email)}</span>
            </div>
            <div class="p-2.5 bg-white border border-slate-200 rounded col-span-2">
                <span class="text-[10px] text-slate-400 uppercase font-semibold block">Endereço Residencial</span>
                <span class="text-slate-800">${escapeHtml(aluno.endereco || 'Não informado')}</span>
            </div>
        </div>

        <div class="p-3 bg-emerald-50 border border-emerald-200 rounded flex items-center justify-between">
            <span class="text-xs font-semibold text-emerald-800">Saldo Atual:</span>
            <strong class="text-lg font-mono font-bold text-emerald-800">${aluno.saldo || 0} $M</strong>
        </div>
    `;

    openModal('modal-aluno-detalhes');
}

/**
 * CRUD: EMPRESAS PARCEIRAS
 */
function openEmpresaFormModal(empresaId = null) {
    if (APP_STATE.currentUser?.tipo !== 'ADMIN') return;
    const form = document.getElementById('form-empresa');
    form.reset();
    document.getElementById('empresa-senha').required = !empresaId;
    document.getElementById('empresa-senha-help').textContent = empresaId
        ? 'Deixe em branco para manter a senha atual.'
        : 'Defina uma senha com pelo menos 8 caracteres.';

    if (empresaId) {
        const emp = APP_STATE.empresas.find(e => e.id === empresaId);
        if (emp) {
            document.getElementById('modal-empresa-title').innerText = 'Editar Empresa Parceira';
            document.getElementById('empresa-id').value = emp.id;
            document.getElementById('empresa-nome-fantasia').value = emp.nomeFantasia || '';
            document.getElementById('empresa-razao-social').value = emp.razaoSocial || '';
            document.getElementById('empresa-cnpj').value = emp.cnpj || '';
            document.getElementById('empresa-email').value = emp.email || '';
            document.getElementById('empresa-login').value = emp.login || '';
            document.getElementById('empresa-endereco').value = emp.endereco || '';
            document.getElementById('empresa-vantagens').value = emp.vantagens || '';
        }
    } else {
        document.getElementById('modal-empresa-title').innerText = 'Cadastrar Empresa Parceira';
        document.getElementById('empresa-id').value = '';
    }

    openModal('modal-empresa');
}

async function handleSaveEmpresa(e) {
    e.preventDefault();
    if (APP_STATE.currentUser?.tipo !== 'ADMIN') return;
    const id = document.getElementById('empresa-id').value;

    const payload = {
        nomeFantasia: document.getElementById('empresa-nome-fantasia').value.trim(),
        razaoSocial: document.getElementById('empresa-razao-social').value.trim(),
        cnpj: document.getElementById('empresa-cnpj').value.trim(),
        email: document.getElementById('empresa-email').value.trim(),
        login: document.getElementById('empresa-login').value.trim(),
        senha: document.getElementById('empresa-senha').value,
        endereco: document.getElementById('empresa-endereco').value.trim(),
        vantagens: document.getElementById('empresa-vantagens').value.trim()
    };

    try {
        const endpoint = id ? `/empresas/${id}` : '/empresas';
        const method = id ? 'PUT' : 'POST';

        const response = await fetchFromApi(endpoint, {
            method: method,
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.erro || 'Erro ao salvar empresa parceira.');
        }

        closeModal('modal-empresa');
        showToast(id ? 'Empresa atualizada!' : 'Empresa parceira cadastrada!', 'success');
        await loadDataFromApi();
    } catch (err) {

        showToast(err.message || 'Falha ao salvar empresa.', 'error');
    }
}

async function deleteEmpresa(id) {
    if (APP_STATE.currentUser?.tipo !== 'ADMIN') return;
    if (!await askConfirmation('Desativar empresa?', 'Deseja desativar o cadastro desta empresa parceira?', 'Desativar empresa')) return;

    try {
        const response = await fetchFromApi(`/empresas/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Falha ao remover empresa.');

        showToast('Empresa removida com sucesso.', 'info');
        await loadDataFromApi();
    } catch (err) {

        showToast('Não foi possível remover a empresa.', 'error');
    }
}

/**
 * TRANSAÇÕES: ENVIAR MOEDAS & RESGATES
 */
function openSendCoinsModal() {
    if (APP_STATE.currentUser?.tipo !== 'PROFESSOR') return;
    const select = document.getElementById('send-aluno-id');
    select.innerHTML = '<option value="">Selecione o Aluno...</option>';
    APP_STATE.alunos.filter(a => a.ativo).forEach(a => {
        select.innerHTML += `<option value="${a.id}">${escapeHtml(a.nome)} (${escapeHtml(a.curso)})</option>`;
    });

    document.getElementById('form-send-coins').reset();
    openModal('modal-send-coins');
}

function setSendAmount(amount) {
    document.getElementById('send-amount').value = amount;
}

async function handleSendCoins(e) {
    e.preventDefault();
    if (APP_STATE.currentUser?.tipo !== 'PROFESSOR') return;
    const alunoId = parseInt(document.getElementById('send-aluno-id').value);
    const amount = parseInt(document.getElementById('send-amount').value);
    const reason = document.getElementById('send-reason').value.trim();

    if (!alunoId || !amount || amount <= 0) {
        showToast('Informe um aluno e uma quantidade válida de moedas.', 'error');
        return;
    }

    const payload = { alunoId, valor: amount, motivo: reason };

    try {
        const response = await fetchFromApi('/transacoes/enviar', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error('Falha ao enviar moedas.');
        }

        closeModal('modal-send-coins');
        showToast(`${amount} Moedas enviadas com sucesso!`, 'success');
        await loadDataFromApi();
    } catch (err) {

        showToast('Erro ao processar transferência.', 'error');
    }
}

async function redeemVantagem(vantagemId, button) {
    if (APP_STATE.currentUser?.tipo !== 'ALUNO') return;
    const vantagem = APP_STATE.vantagens.find(v => v.id === vantagemId);
    const aluno = APP_STATE.currentUser;
    if (!vantagem) {
        showToast('Vantagem indisponível.', 'error');
        return;
    }

    if ((aluno.saldo || 0) < vantagem.custoMoedas) {
        showToast('Saldo insuficiente para este resgate.', 'error');
        return;
    }

    button.disabled = true;
    try {
        const confirmado = await askConfirmation('Resgatar vantagem?',
            `Você vai trocar ${vantagem.custoMoedas} moedas por "${vantagem.titulo}". O valor será descontado do seu saldo.`,
            'Confirmar resgate');
        if (!confirmado || APP_STATE.currentUser !== aluno) return;
        button.textContent = 'Resgatando…';
        const response = await fetchFromApi('/transacoes/resgatar', {
            method: 'POST',
            body: JSON.stringify({ vantagemId })
        });

        const dados = await response.json().catch(() => ({}));
        if (APP_STATE.currentUser !== aluno) return;
        if (response.status === 401) {
            showLoginUI();
            showToast('Sua sessão expirou. Entre novamente para resgatar.', 'error');
            return;
        }
        if (!response.ok) {
            throw new Error(dados.erro || 'Falha ao registrar resgate.');
        }

        if (Number.isInteger(dados.valor) && dados.valor > 0) {
            aluno.saldo -= dados.valor;
            updateBalanceDisplay();
        }

        let codigo = dados.codigoResgate;
        const codigoNaResposta = typeof codigo === 'string' && Boolean(codigo.trim());
        if (!codigoNaResposta) {
            await loadDataFromApi();
            if (APP_STATE.currentUser !== aluno) return;
            const resgate = APP_STATE.transacoes.find(tx => tx.id === dados.id && tx.tipo === 'RESGATE_VANTAGEM');
            codigo = resgate?.codigoResgate;
            if (typeof codigo !== 'string' || !codigo.trim()) {
                showToast('Resgate registrado, mas o código não foi retornado. Confira o extrato antes de tentar de novo.', 'error');
                return;
            }
        }

        document.getElementById('voucher-title').innerText = vantagem.titulo;
        document.getElementById('voucher-partner').innerText = `Parceiro: ${vantagem.empresaNome || 'Empresa Conveniada'}`;
        document.getElementById('voucher-code').innerText = codigo;
        openModal('modal-voucher');
        showToast('Vantagem resgatada.', 'success');

        if (codigoNaResposta) await loadDataFromApi();
    } catch (err) {
        showToast(err.message || 'Erro ao resgatar vantagem.', 'error');
    } finally {
        button.disabled = aluno.saldo < vantagem.custoMoedas;
        button.textContent = button.disabled ? 'Saldo insuficiente' : 'Resgatar vantagem';
    }
}

function copyVoucherCode() {
    const code = document.getElementById('voucher-code').innerText;
    navigator.clipboard.writeText(code).then(() => {
        showToast('Código copiado!', 'success');
    });
}
