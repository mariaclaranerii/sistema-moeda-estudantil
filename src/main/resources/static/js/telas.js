function navigateTo(viewId) {
    if (!VIEWS_BY_ROLE[APP_STATE.currentUser?.tipo]?.includes(viewId)) return;
    APP_STATE.currentViewId = viewId;

    document.querySelectorAll('.content-view').forEach(v => v.classList.add('hidden'));
    const target = document.getElementById(`view-${viewId}`);
    if (target) target.classList.remove('hidden');

    document.querySelectorAll('.nav-item').forEach(btn => {
        btn.classList.remove('bg-slate-100', 'text-blue-700', 'font-bold');
        btn.classList.add('text-slate-700');
    });
    const activeBtn = document.getElementById(`nav-${viewId}`);
    if (activeBtn) {
        activeBtn.classList.add('bg-slate-100', 'text-blue-700', 'font-bold');
    }

    const titles = {
        dashboard: { title: 'Visão Geral', subtitle: 'Resumo do sistema de incentivo e distribuição de moedas' },
        alunos: APP_STATE.currentUser.tipo === 'PROFESSOR'
            ? { title: 'Alunos', subtitle: 'Consulta à listagem de alunos' }
            : { title: 'Gestão de Alunos', subtitle: 'Cadastro de discentes, dados acadêmicos e consulta de saldos' },
        empresas: { title: 'Empresas Parceiras', subtitle: 'Rede conveniada e vantagens cadastradas para estudantes' },
        transacoes: APP_STATE.currentUser.tipo === 'PROFESSOR'
            ? { title: 'Meus envios', subtitle: 'Histórico de moedas enviadas por você' }
            : { title: 'Extrato de Transações', subtitle: 'Histórico de envios de professores e resgates de vantagens' },
        vantagens: { title: 'Catálogo de Vantagens', subtitle: 'Resgate de produtos, serviços e descontos' }
    };

    if (titles[viewId]) {
        document.getElementById('page-title').innerText = titles[viewId].title;
        document.getElementById('page-subtitle').innerText = titles[viewId].subtitle;
    }

    const sidebar = document.getElementById('sidebar');
    if (window.innerWidth < 768 && !sidebar.classList.contains('hidden')) {
        sidebar.classList.add('hidden');
    }

    refreshCurrentView(viewId);
}

function refreshCurrentView(viewId) {
    switch(viewId) {
        case 'dashboard': renderDashboard(); break;
        case 'alunos': renderAlunos(); break;
        case 'empresas': renderEmpresas(); break;
        case 'transacoes': renderTransacoes(); break;
        case 'vantagens': renderVantagens(); break;
    }
}

function toggleMobileSidebar() {
    document.getElementById('sidebar').classList.toggle('hidden');
}

function updateNavigationBadges() {
    document.getElementById('badge-count-alunos').innerText = APP_STATE.alunos.length;
    document.getElementById('badge-count-empresas').innerText = APP_STATE.empresas.length;
    document.getElementById('badge-count-transacoes').innerText = APP_STATE.transacoes.length;
    document.getElementById('badge-count-vantagens').innerText = APP_STATE.vantagens.length;
}

/**
 * RENDER: DASHBOARD
 */
function renderDashboard() {
    document.getElementById('stat-total-alunos').innerText = APP_STATE.alunos.length;
    document.getElementById('stat-total-empresas').innerText = APP_STATE.empresas.length;

    const totalMoedas = APP_STATE.alunos.reduce((acc, curr) => acc + (curr.saldo || 0), 0);
    document.getElementById('stat-total-moedas').innerText = `${totalMoedas.toLocaleString()} $M`;
    document.getElementById('stat-total-transacoes').innerText = APP_STATE.transacoes.length;

    const recentTxTable = document.getElementById('dashboard-recent-tx-table');
    const recentTx = [...APP_STATE.transacoes].slice(0, 5);

    if (recentTx.length === 0) {
        recentTxTable.innerHTML = `<tr><td colspan="4" class="py-4 text-center text-slate-400">Nenhuma movimentação registrada.</td></tr>`;
    } else {
        recentTxTable.innerHTML = recentTx.map(tx => {
            const isEnvio = tx.tipo === 'ENVIO_PROFESSOR';
            return `
                <tr class="hover:bg-slate-50/60">
                    <td class="py-2.5 text-slate-500 font-mono whitespace-nowrap">${formatDate(tx.data)}</td>
                    <td class="py-2.5 font-medium text-slate-800">${escapeHtml(tx.origem)} &rarr; ${escapeHtml(tx.destino)}</td>
                    <td class="py-2.5 text-slate-600 truncate max-w-xs">${escapeHtml(tx.motivo || 'Reconhecimento')}</td>
                    <td class="py-2.5 text-right font-mono font-bold ${isEnvio ? 'text-emerald-700' : 'text-slate-800'}">
                        ${isEnvio ? '+' : '-'}${tx.valor} $M
                    </td>
                </tr>
            `;
        }).join('');
    }

    const popContainer = document.getElementById('dashboard-popular-vantagens');
    const popularVant = APP_STATE.vantagens.slice(0, 4);

    if (popularVant.length === 0) {
        popContainer.innerHTML = `<p class="text-xs text-slate-400 text-center py-4">Nenhuma vantagem cadastrada.</p>`;
    } else {
        popContainer.innerHTML = popularVant.map(v => `
            <div class="p-2.5 rounded border border-slate-200 bg-slate-50 flex items-center justify-between">
                <div>
                    <h4 class="font-semibold text-slate-800 text-xs">${escapeHtml(v.titulo)}</h4>
                    <span class="text-[11px] text-slate-500">${escapeHtml(v.empresaNome || 'Parceiro')}</span>
                </div>
                <span class="text-xs font-mono font-bold text-slate-800 bg-white px-2 py-0.5 rounded border border-slate-200">${v.custoMoedas} $M</span>
            </div>
        `).join('');
    }
}

/**
 * RENDER: ALUNOS
 */
function renderAlunos() {
    const perfil = APP_STATE.currentUser?.tipo;
    if (!VIEWS_BY_ROLE[perfil]?.includes('alunos')) return;
    const canManage = perfil === 'ADMIN';
    const tbody = document.getElementById('alunos-table-body');
    const search = (document.getElementById('aluno-search-input')?.value || '').toLowerCase();
    const instFilter = document.getElementById('aluno-instituicao-filter')?.value || '';

    const filtered = APP_STATE.alunos.filter(a => {
        const matchSearch = (a.nome || '').toLowerCase().includes(search) ||
                            (a.cpf || '').includes(search) ||
                            (a.curso || '').toLowerCase().includes(search);
        const matchInst = !instFilter || String(a.instituicaoId) === instFilter;
        return matchSearch && matchInst;
    });

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="${canManage ? 5 : 4}" class="py-8 text-center text-slate-400">Nenhum aluno encontrado.</td></tr>`;
        return;
    }

    tbody.innerHTML = filtered.map(aluno => `
        <tr class="hover:bg-slate-50/60">
            <td class="py-3 px-4">
                <div class="font-semibold text-slate-900">${escapeHtml(aluno.nome)}</div>
                <div class="text-[11px] text-slate-500">${escapeHtml(aluno.email)}</div>
            </td>
            <td class="py-3 px-4 font-mono text-[11px] text-slate-600">
                <div>CPF: ${escapeHtml(aluno.cpf)}</div>
                ${aluno.rg ? `<div class="text-slate-400">RG: ${escapeHtml(aluno.rg)}</div>` : ''}
            </td>
            <td class="py-3 px-4">
                <div class="font-medium text-slate-800">${escapeHtml(aluno.curso || 'Graduação')}</div>
                <div class="text-[11px] text-slate-500">${escapeHtml(aluno.instituicaoNome || 'PUC Minas')}</div>
            </td>
            <td class="py-3 px-4 text-center">
                <span class="inline-block px-2.5 py-0.5 rounded text-xs font-mono font-bold bg-emerald-50 text-emerald-800 border border-emerald-200">
                    ${aluno.saldo || 0} $M
                </span>
            </td>
            ${canManage ? `<td class="py-3 px-4 text-right space-x-1 whitespace-nowrap">
                <button onclick="viewAlunoDetails(${aluno.id})" title="Detalhes" class="p-1.5 text-slate-500 hover:text-blue-600 rounded hover:bg-slate-100">
                    <i class="fa-solid fa-eye text-xs"></i>
                </button>
                    <button onclick="openAlunoFormModal(${aluno.id})" title="Editar" class="p-1.5 text-slate-500 hover:text-slate-900 rounded hover:bg-slate-100">
                        <i class="fa-solid fa-pen text-xs"></i>
                    </button>
                    <button onclick="deleteAluno(${aluno.id})" title="Remover" class="p-1.5 text-slate-500 hover:text-rose-600 rounded hover:bg-slate-100">
                        <i class="fa-solid fa-trash-can text-xs"></i>
                    </button>
            </td>` : ''}
        </tr>
    `).join('');
}

function filterAlunos() {
    renderAlunos();
}

/**
 * RENDER: EMPRESAS PARCEIRAS
 */
function renderEmpresas() {
    if (APP_STATE.currentUser?.tipo !== 'ADMIN') return;
    const grid = document.getElementById('empresas-cards-grid');
    const search = (document.getElementById('empresa-search-input')?.value || '').toLowerCase();

    const filtered = APP_STATE.empresas.filter(e =>
        (e.nomeFantasia || '').toLowerCase().includes(search) ||
        (e.cnpj || '').includes(search)
    );

    if (filtered.length === 0) {
        grid.innerHTML = `<div class="col-span-full py-8 text-center text-slate-400 bg-white rounded-lg border border-slate-200">Nenhuma empresa parceira encontrada.</div>`;
        return;
    }

    const canManage = APP_STATE.currentUser?.tipo === 'ADMIN';

    grid.innerHTML = filtered.map(emp => `
        <article class="empresa-item">
            <header class="empresa-heading">
                <div>
                    <h3>${escapeHtml(emp.nomeFantasia)}</h3>
                    <p>${escapeHtml(emp.razaoSocial || emp.nomeFantasia)}</p>
                </div>
                ${canManage ? `
                    <div class="empresa-actions">
                        <button onclick="openEmpresaFormModal(${emp.id})" class="px-3 py-2 rounded text-slate-700 hover:bg-slate-100"><i class="fa-solid fa-pen" aria-hidden="true"></i> Editar</button>
                        <button onclick="deleteEmpresa(${emp.id})" class="px-3 py-2 rounded text-rose-600 hover:bg-rose-50"><i class="fa-solid fa-trash-can" aria-hidden="true"></i> Excluir</button>
                    </div>
                ` : ''}
            </header>
            <div class="empresa-information">
                <dl class="empresa-contact">
                    <div><dt>CNPJ</dt><dd>${escapeHtml(emp.cnpj || 'Não informado')}</dd></div>
                    <div><dt>E-mail</dt><dd>${escapeHtml(emp.email || 'Não informado')}</dd></div>
                    <div><dt>Endereço</dt><dd>${escapeHtml(emp.endereco || 'Não informado')}</dd></div>
                </dl>
                <div class="empresa-benefits">
                    <h4>Benefícios oferecidos</h4>
                    <p>${escapeHtml(emp.vantagens || 'Nenhuma descrição cadastrada.')}</p>
                </div>
            </div>
        </article>
    `).join('');
}

function filterEmpresas() {
    renderEmpresas();
}

/**
 * RENDER: TRANSAÇÕES
 */
function setTxFilter(filter) {
    if (APP_STATE.currentUser?.tipo === 'PROFESSOR' && filter !== 'ALL') return;
    APP_STATE.txFilter = filter;
    document.querySelectorAll('.tx-filter-btn').forEach(btn => {
        btn.classList.remove('bg-slate-900', 'text-white');
        btn.classList.add('bg-white', 'border', 'border-slate-200', 'text-slate-700');
    });

    if (filter === 'ALL') {
        const b = document.getElementById('filter-tx-all');
        b.classList.add('bg-slate-900', 'text-white');
        b.classList.remove('bg-white', 'text-slate-700');
    } else if (filter === 'ENVIO_PROFESSOR') {
        const b = document.getElementById('filter-tx-envio');
        b.classList.add('bg-slate-900', 'text-white');
        b.classList.remove('bg-white', 'text-slate-700');
    } else if (filter === 'RESGATE_VANTAGEM') {
        const b = document.getElementById('filter-tx-resgate');
        b.classList.add('bg-slate-900', 'text-white');
        b.classList.remove('bg-white', 'text-slate-700');
    }
    renderTransacoes();
}

function renderTransacoes() {
    const tbody = document.getElementById('transacoes-table-body');
    let list = [...APP_STATE.transacoes];

    if (APP_STATE.txFilter !== 'ALL') {
        list = list.filter(t => t.tipo === APP_STATE.txFilter);
    }

    if (list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="py-8 text-center text-slate-400">Nenhuma transação encontrada.</td></tr>`;
        return;
    }

    tbody.innerHTML = list.map(tx => {
        const isEnvio = tx.tipo === 'ENVIO_PROFESSOR';
        const badge = isEnvio
            ? '<span class="inline-block px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200">BONIFICAÇÃO</span>'
            : '<span class="inline-block px-2 py-0.5 rounded text-[10px] font-semibold bg-slate-100 text-slate-800 border border-slate-200">RESGATE</span>';

        return `
            <tr class="hover:bg-slate-50/60">
                <td class="py-2.5 px-4 text-slate-500 font-mono text-[11px] whitespace-nowrap">${formatDate(tx.data)}</td>
                <td class="py-2.5 px-4">${badge}</td>
                <td class="py-2.5 px-4 font-medium text-slate-800">${escapeHtml(tx.origem)}</td>
                <td class="py-2.5 px-4 font-medium text-slate-800">${escapeHtml(tx.destino)}</td>
                <td class="py-2.5 px-4 text-slate-600">${escapeHtml(tx.motivo || '-')}
                    ${tx.codigoResgate ? `<div class="mt-1 font-mono text-[11px] break-all">${escapeHtml(tx.codigoResgate)}</div>` : ''}
                </td>
                <td class="py-2.5 px-4 text-right font-mono font-bold ${isEnvio ? 'text-emerald-700' : 'text-slate-900'}">
                    ${isEnvio ? '+' : '-'}${tx.valor} $M
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * RENDER: VANTAGENS
 */
async function handleSaveVantagem(event) {
    event.preventDefault();
    if (APP_STATE.currentUser?.tipo !== 'EMPRESA') return;
    const form = event.currentTarget;
    const button = form.querySelector('button');
    const dados = Object.fromEntries(new FormData(form));
    dados.custoMoedas = Number(dados.custoMoedas);
    button.disabled = true;
    try {
        const response = await fetchFromApi('/vantagens', {
            method: 'POST',
            body: JSON.stringify(dados)
        });
        if (!response.ok) {
            const { erro } = await response.json();
            throw new Error(erro);
        }
        form.reset();
        await loadDataFromApi();
        showToast('Vantagem cadastrada.', 'success');
    } catch (error) {
        showToast(error.message || 'Não foi possível cadastrar a vantagem.', 'error');
    } finally {
        button.disabled = false;
    }
}

function renderVantagens() {
    const grid = document.getElementById('vantagens-catalog-grid');
    const isAluno = APP_STATE.currentUser?.tipo === 'ALUNO';
    const currentBalance = APP_STATE.currentUser?.saldo || 0;

    updateBalanceDisplay();

    if (APP_STATE.vantagens.length === 0) {
        grid.innerHTML = `<div class="col-span-full py-8 text-center text-slate-400 bg-white rounded-lg border border-slate-200">Nenhuma vantagem cadastrada no momento.</div>`;
        return;
    }

    grid.innerHTML = APP_STATE.vantagens.map(v => {
        const canAfford = currentBalance >= v.custoMoedas;
        return `
            <article class="vantagem-item">
                <div class="vantagem-description">
                    <p class="vantagem-partner">${escapeHtml(v.empresaNome || 'Parceiro')}</p>
                    <h3>${escapeHtml(v.titulo)}</h3>
                    <p class="vantagem-summary">${escapeHtml(v.descricao || 'Benefício disponível para resgate via Moeda Estudantil.')}</p>
                </div>
                <div class="vantagem-redemption">
                    <div class="vantagem-price"><span>Valor do resgate</span><strong>${v.custoMoedas} <small>moedas</small></strong></div>
                    ${isAluno ? `<button onclick="redeemVantagem(${v.id}, this)" ${!canAfford ? 'disabled' : ''} class="px-3 py-1.5 rounded text-xs font-semibold transition-colors ${canAfford ? 'bg-blue-600 hover:bg-blue-700 text-white' : 'bg-slate-100 text-slate-400 cursor-not-allowed border border-slate-200'}">
                        ${canAfford ? 'Resgatar vantagem' : 'Saldo insuficiente'}
                    </button>` : ''}
                </div>
            </article>
        `;
    }).join('');
}
