# Sistema Concessionária
Sistema de gerenciamento acadêmico para concessionária de veículos, desenvolvido em Java durante o curso de Sistemas de Informação.

---

## Sobre o projeto
Aplicação de terminal que permite gerenciar os principais processos de uma concessionária de veículos, desde o cadastro de produtos até o controle de vendas e geração de relatórios.

---

## Funcionalidades

**Produtos**
- Cadastrar, alterar e excluir veículos
- Controle de estoque
- Cálculo automático de preço de venda com base na margem de lucro

**Clientes**
- Cadastrar, alterar e excluir clientes
- Impressão de ficha do cliente

**Vendedores**
- Cadastrar, alterar e excluir vendedores
- Controle de salário base e percentual de comissão

**Vendas**
- Registrar e cancelar vendas
- Cálculo automático de comissão por venda
- Impressão e reimpressão de pedido de venda

**Relatórios**
- Relação de produtos em estoque
- Relação de clientes cadastrados
- Relação de vendedores cadastrados
- Salários líquidos por mês
- Vendas efetuadas por mês

---

## Tecnologias
- Java 8
- Serialização de objetos para persistência de dados

---

## Como rodar

### Pelo IntelliJ IDEA (recomendado)

**Pré-requisitos:** [IntelliJ IDEA](https://www.jetbrains.com/idea/download) e [Java 8+](https://adoptium.net) instalados na máquina.

1. Clone o repositório:
```bash
git clone https://github.com/Karemrebeca/Concessionaria.git
```
2. Abra o IntelliJ e vá em **File > Open**
3. Selecione a pasta `Concessionaria`
4. Aguarde o IntelliJ indexar o projeto
5. Abra o arquivo `Concessionaria.java` e clique no botão **▶ Run** (ou pressione `Shift + F10`)

### Pelo terminal (alternativo)

**Pré-requisitos:** [Java](https://adoptium.net) instalado na máquina.

```bash
git clone https://github.com/Karemrebeca/Concessionaria.git
cd Concessionaria
javac --release 8 Concessionaria.java
java Concessionaria
```

---

## Autora

**Karem Rebeca** — Estudante de Sistemas de Informação

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/karem-rebeca-9148b428b)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Karemrebeca)
