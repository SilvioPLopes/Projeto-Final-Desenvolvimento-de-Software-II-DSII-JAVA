const botao = document.querySelector('#buscar-cep');
const retorno = document.querySelector('#retorno-cep');

botao.addEventListener('click', async () => {
    const cep = document.querySelector('#cep').value.trim();

    if (!/^\d{5}-?\d{3}$/.test(cep)) {
        retorno.textContent = 'Informe um CEP com 8 dígitos.';
        return;
    }

    botao.disabled = true;
    retorno.textContent = 'Buscando endereço…';

    try {
        const resposta = await fetch(botao.dataset.url + encodeURIComponent(cep), {
            headers: {Accept: 'application/json'}
        });

        if (!resposta.ok || resposta.redirected) {
            retorno.textContent = resposta.status === 404
                ? 'CEP não encontrado. Preencha o endereço manualmente.'
                : 'Consulta indisponível. Preencha o endereço manualmente.';
            return;
        }

        const endereco = await resposta.json();
        for (const campo of ['logradouro', 'cidade', 'uf']) {
            document.getElementById(campo).value = endereco[campo] || '';
        }
        retorno.textContent = 'Confira o endereço e informe o número.';
    } catch {
        retorno.textContent = 'Consulta indisponível. Preencha o endereço manualmente.';
    } finally {
        botao.disabled = false;
    }
});
